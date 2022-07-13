package com.veracode.sonarplugin;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

// StAX XML parser
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.text.ParseException;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
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
		return getElementIDFromList("app", appName);
	}

	// get the sandbox ID from sandbolist.xsd doc (from getSendboxList(appID) )
	public String getSandboxIDFromList(final String sandboxName)
	throws ParseException, XMLStreamException
	{
		return getElementIDFromList("sandbox", sandboxName);
	}

	private String getElementIDFromList(final String elementType, final String elementName)
	throws ParseException, XMLStreamException
	{
		log.info("Getting " + elementType + " ID for " + elementName);
		
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
					
					// find an element with the matching name
					if(eName.equalsIgnoreCase(elementType))
					{
						Attribute attribName = startElem.getAttributeByName(new QName(elementType + "_name"));
						String name = attribName.getValue();
						
						Attribute attribID = startElem.getAttributeByName(new QName(elementType + "_id"));
						String id = attribID.getValue();
						
						log.debug("attribName = " + name + ",  attribID = " + id);
						
						if(name.equals(elementName) )
							return id;
					}
					
					break;
					
				case XMLStreamConstants.CHARACTERS:
					break;
					
					
				case XMLStreamConstants.END_ELEMENT:
					break;
				}
			}
			
			throw new ParseException("No " + elementType + " found matching name " + elementName, 0);			
		}
		catch(XMLStreamException e)
		{
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
		log.info("Getting latest build info for appID = " + appID);
	
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
			
			throw new ParseException("No build element found in XML", 0);
		}
		catch(XMLStreamException e)
		{
			throw new XMLStreamException("Error reading from xml string " + e.toString());
		}
	}

	// parse the detailed report from detailedreport.xsd doc (from detailedReport() )
	public void addFlawsFromReport(SensorContext context)
	throws /*ParseException, */XMLStreamException
	{
		int loopCounter = 0;

		log.info("Reading flaws from Veracode detailed report");
		
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

						Attribute attribFile = startElem.getAttributeByName(new QName("sourcefile"));
						if(attribFile == null)
						{
							// no source file, so either a dynamic or MPT finding
							continue;
						}
						String sourceFile = attribFile.getValue();

						Attribute attribPath = startElem.getAttributeByName(new QName("sourcefilepath"));
						String sourcePath = attribPath.getValue();

						//Attribute attribDescription = startElem.getAttributeByName(new QName("description"));
						//String flawDescription = attribDescription.getValue();

						Attribute attribModule = startElem.getAttributeByName(new QName("module"));
						String moduleName = attribModule.getValue();

						Attribute attribLine = startElem.getAttributeByName(new QName("line"));
						String sourceLine = attribLine.getValue();

						Attribute attribFlawID = startElem.getAttributeByName(new QName("issueid"));
						String flawID = attribFlawID.getValue();

						Attribute attribMitigationStatus = startElem.getAttributeByName(new QName("mitigation_status"));
						String mitigationStatus = attribMitigationStatus.getValue();

						/** testing code
						if(flawID.equalsIgnoreCase("17"))
						{
							cweID = "foo";
							//log.debug("skipping flaw " + flawID);
							//continue;
						}
						*/

						// skip mitigated flaws
						if(mitigationStatus.equalsIgnoreCase("accepted")) {
							log.info("Flaw ID = " + flawID + " is mitigated, skipping");
							continue;
						}

						log.debug("adding flaw: [" + moduleName + "]" 
										+ sourcePath + sourceFile + ":" 
										+ sourceLine + ", cweID=" + cweID);

						/** testing code 
						// setup
						Path flawPath = Paths.get("verademo-test/src/main/java/" + sourcePath +sourceFile);		// fudge, we don't know the absolute path
						SensorStrategy strat = new SensorStrategy();

						DefaultIndexedFile iFile = new DefaultIndexedFile(flawPath, 					// absolute path
													"VERADEMO2",											// module key 
													sourcePath, 										// proj relative path
													sourcePath,  										// module relative path
													Type.MAIN, 
													"veracode", 
													((DefaultInputModule)context.module()).batchId(),	//batchId
													strat);							


						DefaultTextPointer p1 = new DefaultTextPointer(1, 1);
						DefaultTextPointer p2 = new DefaultTextPointer(2,2);
						DefaultTextRange r1 = new DefaultTextRange(p1, p2);

						DefaultInputModule im = new DefaultInputModule(((DefaultInputModule)context.module()).definition(),
						((DefaultInputModule)context.module()).batchId() );
						*/

						// progress counter
						if(++loopCounter % 100 == 0)
							log.info("Processed " + Integer.toString(loopCounter) + " flaws");

						// handle a CWE not in the Rules list
						ActiveRule rule = context.activeRules()									// ActiveRules							
											.find(RuleKey.of(VeracodeRules.REPO_KEY, cweID));	// ActiveRule
						if(rule == null) {
							log.warn("Unknown CWE found, skipping.  Flaw ID = " + flawID + ", CWE ID = " + cweID);
							continue;
						}

						String msg = rule.param("ruletext");
						
						// add the issue to SonarQube
						NewIssue newIssue = context.newIssue();
						newIssue
							.forRule(RuleKey.of(VeracodeRules.REPO_KEY, cweID))
							.at(newIssue.newLocation()
								.on( /*iFile*/ /*im*/ /*context.module()*/ context.project() )
								//.at(r1)	// only valid for a file, and requires valid file Metadata
								.message(msg + " - Veracode (flawID = " + flawID + ")" +
										" [" + moduleName + "]" + sourcePath + sourceFile + ":" + sourceLine)
									)
							.save();
					}
					
					break;
					
				case XMLStreamConstants.CHARACTERS:
					break;
					
				
				case XMLStreamConstants.END_ELEMENT:
					break;
				}
			}
		}
		catch(XMLStreamException e)
		{
			throw new XMLStreamException("Error reading from xml string " + e.toString());
		}
	}
}
