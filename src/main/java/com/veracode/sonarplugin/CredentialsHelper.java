package com.veracode.sonarplugin;

import org.apache.commons.lang3.StringUtils;
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
        
        try {
            wrapper.setUpApiCredentials(m_config.getApiId(), m_config.getApiKey());
        }
        catch(IllegalArgumentException e) {
            // this can get thrown if the API Key is bogus
            log.error("Problem setting up API credentials.  Exception: " + e.toString());
            
            return false;				
        }
        
        log.debug("Proxy Host = [" + m_config.getProxyHost() + "]");
        log.debug("Proxy Port = [" + m_config.getProxyPort() + "]");
        log.debug("Proxy Username = [" + m_config.getProxyUsername() + "]");
        log.debug("Proxy Password = [" + m_config.getProxyPassword() + "]");

        // setup proxy, if required
        if(StringUtils.isNotBlank(m_config.getProxyHost()) ) {

            log.debug("Setting up proxy");

            // validate params
            if(StringUtils.isBlank(m_config.getProxyPort()) ) {
                log.error("Proxy Host is set, but not proxy port.");
                return false;
            }

            if(StringUtils.isNotBlank(m_config.getProxyUsername()) && StringUtils.isBlank(m_config.getProxyPassword()) ) {
                log.error("Proxy username is set, but not proxy password.");
                return false;
            }

            // config the proxy
            if(StringUtils.isNotBlank(m_config.getProxyUsername()) ) {
                log.debug("4-way proxy");
                wrapper.setUpProxy(m_config.getProxyHost(),
                                    m_config.getProxyPort(), 
                                    m_config.getProxyUsername(),
                                    m_config.getProxyPassword());
            }
            else {
                log.debug("2-way proxy");
                wrapper.setUpProxy(m_config.getProxyHost(), m_config.getProxyPort());
            }
        }

		return true;
	}
}
