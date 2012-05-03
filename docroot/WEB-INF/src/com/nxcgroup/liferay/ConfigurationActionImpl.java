package com.nxcgroup.liferay;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;


public class ConfigurationActionImpl implements com.liferay.portal.kernel.portlet.ConfigurationAction {
	protected String portletResource = "2legged-google-oauth";
	protected String settingsJSP = "/settings.jsp";

	public void processAction(PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse) {

		_log.info("processAction called");

		String consumerKey = ParamUtil.getString(actionRequest, "consumerKey");
		String consumerSecret = ParamUtil.getString(actionRequest, "consumerSecret");
		String siteName = ParamUtil.getString(actionRequest, "siteName");

		_log.info("consumerKey: " + consumerKey);
		_log.info("consumerSecret: " + consumerSecret);
		_log.info("siteName: " + siteName);

		//PortletPreferences prefs = actionRequest.getPreferences();

		try {
			PortletPreferences prefs = PortletPreferencesFactoryUtil.getPortletSetup(actionRequest, portletResource);
			prefs.setValue("consumerKey", "blah");
			//prefs.setValue("consumerSecret", consumerSecret);
			//prefs.setValue("siteName", siteName);
			prefs.store();// fail
			//_log.info("################# just stored consumerKey: " + prefs.getValue("consumerKey", ""));
		} catch (Exception e) {
			e.printStackTrace();
			_log.info("processAction: " + e.toString());
			SessionErrors.add(actionRequest, e.toString());
		}
	}

	public String render(PortletConfig portletConfig, RenderRequest renderRequest,
            RenderResponse renderResponse) {
		_log.info("render called");
		try {
			PortletPreferences prefs = PortletPreferencesFactoryUtil.getPortletSetup(renderRequest, portletResource);
			String consumerKey = prefs.getValue("consumerKey", "");
			//String consumerSecret = prefs.getValue("consumerSecret", "");
			//String siteName = prefs.getValue("siteName", "");
			_log.info("stored consumerKey" + consumerKey);

			renderRequest.setAttribute("consumerKey", consumerKey);
			/*renderRequest.setAttribute("consumerSecret", consumerSecret);
			renderRequest.setAttribute("siteName", siteName);*/
		} catch (Exception e) {
			_log.info("render: " + e.toString());
			SessionErrors.add(renderRequest, e.toString());
		}

		return settingsJSP;
	}

    private static Log _log = LogFactoryUtil.getLog(GoogleOAuthPortlet.class);
}
