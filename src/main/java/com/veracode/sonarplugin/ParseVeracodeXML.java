package com.veracode.sonarplugin;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

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





	
}
