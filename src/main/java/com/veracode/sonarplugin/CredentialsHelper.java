package com.veracode.sonarplugin;

import com.veracode.apiwrapper.AbstractAPIWrapper;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;


public class CredentialsHelper {	
	//private SecurityUtils m_securityUtils;
    
    private static VeracodeSensorConfiguration m_config;
	private final Logger log = Loggers.get(getClass());
	
	public CredentialsHelper(VeracodeSensorConfiguration config)
	{
        m_config = config;
		//m_securityUtils = new SecurityUtils();
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
