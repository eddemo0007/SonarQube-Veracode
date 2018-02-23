package com.veracode.sonarplugin;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.veracode.apiwrapper.AbstractAPIWrapper;


public class CredentialsHelper {	
    
    private static VeracodeSensorConfiguration m_config;
	private final Logger log = Loggers.get(getClass());
	
	public CredentialsHelper(VeracodeSensorConfiguration config)
	{
        m_config = config;
	}
    
    /**
     * only supporting API ID/Key
     */
	public boolean setUpCredentials(final AbstractAPIWrapper wrapper)
	{
        log.debug("Setting up API creds");
        
        try
        {
            wrapper.setUpApiCredentials(m_config.getApiId(), m_config.getApiKey());
        }
        catch(IllegalArgumentException e)
        {
            // this can get thrown if the API Key is bogus
            log.error("Problem setting up API credentials.  Exception: " + e.toString());
            
            return false;				
        }
		
		return true;
	}
}
