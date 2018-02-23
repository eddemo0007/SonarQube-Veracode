package com.veracode.sonarplugin;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
import java.util.Map;

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
//import org.sonar.api.batch.sensor.issue.NewIssueLocation;
//import org.sonar.api.batch.sensor.issue.internal.DefaultIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
//import org.sonar.api.batch.fs.internal.DefaultInputModule;
//import org.sonar.api.batch.fs.internal.DefaultTextPointer;
//import org.sonar.api.batch.fs.internal.DefaultTextRange;
//import org.sonar.api.batch.fs.internal.SensorStrategy;
//import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
//import org.sonar.api.batch.fs.internal.DefaultIndexedFile;
//import org.sonar.api.batch.fs.internal.DefaultInputComponent;
//import org.sonar.api.batch.fs.internal.DefaultInputFile;
//import org.sonar.api.batch.fs.InputComponent;
//import org.sonar.api.batch.fs.*;
//import org.sonar.api.batch.fs.InputFile;
//import org.sonar.api.batch.fs.TextPointer;
//import org.sonar.api.batch.fs.TextRange;
//import org.sonar.api.batch.fs.InputFile.Type;
//import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.internal.*;
//import org.sonar.api.scan.filesystem.PathResolver.RelativePath;
//import org.sonar.api.batch.bootstrap.ProjectDefinition;

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
		log.info("Getting AppID for " + appName);
		
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
			
			throw new ParseException("No app found matching name " + appName, 0);			
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
						String sourceFile = attribFile.getValue();

						Attribute attribPath = startElem.getAttributeByName(new QName("sourcefilepath"));
						String sourcePath = attribPath.getValue();

						Attribute attribDescription = startElem.getAttributeByName(new QName("description"));
						String flawDescription = attribDescription.getValue();

						Attribute attribModule = startElem.getAttributeByName(new QName("module"));
						String moduleName = attribModule.getValue();

						Attribute attribLine = startElem.getAttributeByName(new QName("line"));
						String sourceLine = attribLine.getValue();

						Attribute attribFlawID = startElem.getAttributeByName(new QName("issueid"));
						String flawID = attribFlawID.getValue();

						log.debug("adding flaw: [" + moduleName + "]" 
										+ sourcePath + sourceFile + ":" 
										+ sourceLine + ", cewID=" + cweID);

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
						
						//add paths to the ProjectDefinition
						ProjectDefinition pd = ((DefaultInputModule)context.module()).definition();
						log.debug("Project working dir: " + pd.getWorkDir().getAbsolutePath());
						log.debug("Project base dir: " + pd.getBaseDir().getAbsolutePath());
						List<String> projSources = pd.sources();
						for(String s : projSources)
							log.debug("Project source: " + s); 
						*/

						String msg = context.activeRules()								// ActiveRules
									.find(RuleKey.of(VeracodeRules.REPO_KEY, cweID))	// ActiveRule
									.param("ruletext");									// rule text
						
						// add the issue to SonarQube
						context.newIssue()
									.forRule(RuleKey.of(VeracodeRules.REPO_KEY, cweID))
									.at(new DefaultIssueLocation().on( /*iFile*/ /*im*/ context.module() )
										//.at(r1)
										.message(msg + " - Veracode (flawID = " + flawID + ")" +
											" [" + moduleName + "]" + sourcePath + sourceFile + ":" + sourceLine)
										)
									.save();
						
						
						// TODO: do I care about the 'mitigtion' field in the flaw data??

						// TODO: progress counter for long reports??
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
