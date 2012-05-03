package com.nxcgroup.liferay;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthParameters.OAuthType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.nxcgroup.liferay.storage.GoogleKeyStorage;
import com.nxcgroup.liferay.storage.StorageItem;

public class GoogleOAuthPortlet extends MVCPortlet {
	/*
	 * Allows to authenticate users using google 2-legged oAuth
	 */

	static String SCOPE = "https://www.google.com/m8/feeds/contacts/default/full";

	public static String getSiteName() {
		String site_name = "";
		try {
			site_name = PrefsPropsUtil.getString(PropsUtil.SITE_NAME);
		} catch (Exception e) {
			_log.info("Error during obtaining portlet properties: " + e.toString());
		}
		return site_name;
	}

	static GoogleOAuthParameters setoAuthParameters() {
		/*
		 * Set google oAuth call parameters
		 */
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();

		try {
			String consumer_key = PrefsPropsUtil.getString(PropsUtil.GOOGLE_OAUTH_CONSUMER_KEY);
			String consumer_secret = PrefsPropsUtil.getString(PropsUtil.GOOGLE_OAUTH_CONSUMER_SECRET);
			oauthParameters.setOAuthConsumerKey(consumer_key);
			oauthParameters.setOAuthConsumerSecret(consumer_secret);
		} catch (Exception e) {
			_log.info(e.toString());
		}

		oauthParameters.setScope(GoogleOAuthPortlet.SCOPE);
		oauthParameters.setOAuthType(OAuthType.TWO_LEGGED_OAUTH);
		return oauthParameters;
	}

	public void getAccessToken(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
		/*
		 * Redirects user to google authentication page
		 */
		
		GoogleOAuthParameters oauthParameters = GoogleOAuthPortlet.setoAuthParameters();

		HttpServletRequest httpreq = PortalUtil.getHttpServletRequest(request);

		String oauthLoginUrl = ParamUtil.getString(httpreq, "oauthLoginUrl");
		oauthLoginUrl = oauthLoginUrl.replace("|", "&"); // hack
		if (_log.isDebugEnabled()) {
			_log.debug("Login URL: " + oauthLoginUrl);
		}
		oauthParameters.setOAuthCallback(oauthLoginUrl);
		
		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(new OAuthHmacSha1Signer());

		try {
			oauthHelper.getUnauthorizedRequestToken(oauthParameters);
			String token_secret = oauthParameters.getOAuthTokenSecret();
			if (_log.isDebugEnabled()) {
				_log.debug("Secret Request Token: " + token_secret);
			}

			String id = httpreq.getSession().getId();
			Map<String, StorageItem> storage = GoogleKeyStorage.getStorage();
			StorageItem attrs = new StorageItem(token_secret);
			storage.put(id, attrs);
		} catch (OAuthException e) {
			SessionErrors.add(request, e.getClass().getName());
			_log.info(e.toString());
		}

		String approvalPageUrl = oauthHelper.createUserAuthorizationUrl(oauthParameters);
		if (_log.isDebugEnabled()) {
			_log.debug("Approval Page Url: " + approvalPageUrl);
		}
		response.sendRedirect(approvalPageUrl);
	}

	public void oauthLogin(ActionRequest request, ActionResponse response)
			throws OAuthException, IOException {
		/*
		 * Callback for google
		 */
		if (_log.isDebugEnabled()) {
			_log.info("oauthLogin called");
		}

		String site_name = getSiteName();
		if ( site_name == null || site_name == "" ) {
			SessionMessages.add(request, "not-configured");
		}

		HttpServletRequest httpreq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));

		URL queryString = new URL(httpreq.getAttribute("CURRENT_COMPLETE_URL").toString());

		String id = httpreq.getSession().getId();
		Map<String, StorageItem> storage = GoogleKeyStorage.getStorage();
		StorageItem attrs = storage.get(id);
		if(attrs == null) {
			response.sendRedirect("/");
			return;
		}
		attrs.setUrl(queryString.toString());
		storage.put(id, attrs);

		response.sendRedirect("/");
	}

	private static Log _log = LogFactoryUtil.getLog(GoogleOAuthPortlet.class);
}
