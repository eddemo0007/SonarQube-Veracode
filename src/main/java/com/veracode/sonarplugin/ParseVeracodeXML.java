package com.veracode.sonarplugin;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
//import java.util.Map;
//import java.util.HashMap;

// StAX XML parser
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
//import javax.xml.stream.events.Characters;
//import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.text.ParseException;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
import org.sonar.api.rule.RuleKey;

public class ParseVeracodeXML {
	
	// class variables
	XMLInputFactory m_factory;
	XMLEventReader m_eventReader;
	ByteArrayInputStream m_inputStream;
	
	private final Logger log = Loggers.get(getClass());
	
	public ParseVeracodeXML(final String xmldata)
	{
		// StAX is designed to handle reading from a stream, so a little fudging here
		byte[] byteArray = xmldata.getBytes(StandardCharsets.UTF_8);
		m_inputStream = new ByteArrayInputStream(byteArray);
		
		m_factory = XMLInputFactory.newInstance();	
	}
	
	// get the app ID from applist.xsd doc (from getAppList() )
	public String getAppIDFromList(final String appName)
	throws ParseException, XMLStreamException
	{
		log.info("getting AppID for " + appName);
		
		try
		{
			m_eventReader = m_factory.createXMLEventReader(m_inputStream);
						
			while(m_eventReader.hasNext())
			{
				XMLEvent event = m_eventReader.nextEvent();
				
				switch(event.getEventType())
				{
				case XMLStreamConstants.START_ELEMENT:
					
					StartElement startElem = event.asStartElement();
					String eName = startElem.getName().getLocalPart();
					
					// find an 'app' element with the matching name
					if(eName.equalsIgnoreCase("app"))
					{
						Attribute attribName = startElem.getAttributeByName(new QName("app_name"));
						String name = attribName.getValue();
						
						Attribute attribID = startElem.getAttributeByName(new QName("app_id"));
						String id = attribID.getValue();
						
						log.debug("attribName = " + name + ",  attribID = " + id);
						
						if(name.equals(appName) )
							return id;
					}
					
					break;
					
				case XMLStreamConstants.CHARACTERS:
					break;
					
					
				case XMLStreamConstants.END_ELEMENT:
					break;
				}
			}
			
			log.error("No app found matching name " + appName);
			throw new ParseException("No app found matching name " + appName, 0);			
		}
		catch(XMLStreamException e)
		{
			log.error("Error reading from xml string " + e.toString());
			throw new XMLStreamException("Error reading from xml string " + e.toString());
		}
	}

	/** 
	 * helper class for returning build info
	 */
	public class BuildInformation {
		public String m_buildID;
		public String m_buildName;

		public BuildInformation(String id, String name) {
			m_buildID = id;
			m_buildName = name;
		}
	}

	// get the build ID from buildinfo.xsd doc (e.g., from getBuildInfo() )
	public BuildInformation getBuildIDFromInfo(final String appID)
	throws ParseException, XMLStreamException
	{
		log.info("getting latest build info for appID = " + appID);
	
		try
		{
			m_eventReader = m_factory.createXMLEventReader(m_inputStream);
						
			while(m_eventReader.hasNext())
			{
				XMLEvent event = m_eventReader.nextEvent();
				
				switch(event.getEventType())
				{
				case XMLStreamConstants.START_ELEMENT:
					
					StartElement startElem = event.asStartElement();
					String eName = startElem.getName().getLocalPart();
					
					// find the 'build' element
					if(eName.equalsIgnoreCase("build"))
					{
						Attribute attribID = startElem.getAttributeByName(new QName("build_id"));
						String id = attribID.getValue();
						log.debug("attribID = " + id);

						Attribute attribReady = startElem.getAttributeByName(new QName("results_ready"));
						String ready = attribReady.getValue();
						log.debug("attribReady = " + ready);

						Attribute attribVersion = startElem.getAttributeByName(new QName("version"));
						String version = attribVersion.getValue();
						log.debug("attribVersion = " + version);

						// are the reaults ready?
						if(ready.equalsIgnoreCase("true"))
						{
							BuildInformation retVal = new BuildInformation(id, version);
							return retVal;
						}
					}

					// if I got this far, the build is not ready yet.  Get the current status.
					if(eName.equalsIgnoreCase("analysis_unit"))
					{
						Attribute attribStatus = startElem.getAttributeByName(new QName("status"));
						String status = attribStatus.getValue();
						log.debug("attribStatus = " + status);	
		
						log.info("Latest build not ready to analyze, status = " + status);
						return null;
					}
					
					break;
					
				case XMLStreamConstants.CHARACTERS:
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					break;
				}
			}
			
			//log.error("No build element in build with name " + buildName);
			throw new ParseException("No build element in build with name " /*+ buildName*/, 0);
		}
		catch(XMLStreamException e)
		{
			//log.error("Error reading from xml string " + e.toString());
			throw new XMLStreamException("Error reading from xml string " + e.toString());
		}
	}

	// parse the detailed report from detailedreport.xsd doc (from detailedReport() )
	public String addFlawsFromReport(SensorContext context)
	throws /*ParseException, */XMLStreamException
	{
		//log.info("getting AppID for " + appName);
		
		try
		{
			m_eventReader = m_factory.createXMLEventReader(m_inputStream);
						
			while(m_eventReader.hasNext())
			{
				XMLEvent event = m_eventReader.nextEvent();
				
				switch(event.getEventType())
				{
				case XMLStreamConstants.START_ELEMENT:
					
					StartElement startElem = event.asStartElement();
					String eName = startElem.getName().getLocalPart();
					
					// find a 'flaw' element
					if(eName.equalsIgnoreCase("flaw"))
					{
						Attribute attribCWE = startElem.getAttributeByName(new QName("cweid"));
						String cweID = attribCWE.getValue();
					
						// add the issue to SonarQube
						context.newIssue()
									.forRule(RuleKey.of(VeracodeRules.REPO_KEY, cweID))
									.at(new DefaultIssueLocation().on(context.module()) /* .at?? .message?? */  )
									.save();
						log.debug("flaw: CWE ID = " + cweID);
						
						// do I care about the 'mitigtion' field in the flaw data??
					}
					
					break;
					
				case XMLStreamConstants.CHARACTERS:
					break;
					
				
				case XMLStreamConstants.END_ELEMENT:
					break;
				}
			}
			
			//log.error("No app found matching name " + appName);
			//throw new ParseException("No flaws found", 0);	
			
			return "x";
		}
		catch(XMLStreamException e)
		{
			log.error("Error reading from xml string " + e.toString());
			throw new XMLStreamException("Error reading from xml string " + e.toString());
		}
	}
}
