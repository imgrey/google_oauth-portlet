package com.nxcgroup.liferay;

import java.util.Map;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.security.auth.Authenticator;


public class GoogleOAuthAuthenticator implements Authenticator {
	
    public int authenticateByEmailAddress(long companyId, String emailAddress, String password,
            Map<String, String[]> headerMap, Map<String, String[]> parameterMap)  throws AuthException {
    	_log.info("authenticateByEmailAddress called");
    	try {
            return authenticate(emailAddress);
    	} catch (Exception e) {
            _log.error(e, e);
            throw new AuthException(e);
    	}
    }

    public int authenticateByUserId(long companyId, long userId, String password,
    		Map<String, String[]> headerMap, Map<String, String[]> parameterMap) throws AuthException {
    	_log.info("authenticateByUserId called");
    	try {
            return authenticate(userId);
    	} catch ( AuthException e ) {
            _log.error(e, e);
            throw new AuthException(e);
    	} catch (Exception e) {
            _log.error(e, e);
            throw new AuthException(e);
    	}
    }

    public int authenticateByScreenName(long companyId, String screenName, String password,
    		Map<String, String[]> headerMap, Map<String, String[]> parameterMap) throws AuthException {
    	_log.info("authenticateByScreenName called");
    	try {
    		return authenticate(screenName);
    	} catch (Exception e) {
            _log.error(e, e);
            throw new AuthException(e);
    	}       
    }
    
    protected int authenticate(String emailAddress) throws Exception {
    	return SUCCESS;
    }
    protected int authenticate(Long userId) throws Exception {
    	return SUCCESS;
    }
    private static Log _log = LogFactoryUtil.getLog(GoogleOAuthAuthenticator.class);
}
