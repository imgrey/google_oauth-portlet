package com.nxcgroup.liferay;

import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Person;
import com.google.gdata.data.contacts.ContactFeed;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.nxcgroup.liferay.storage.GoogleKeyStorage;
import com.nxcgroup.liferay.storage.StorageItem;

public class GoogleOAuthAutoLogin implements AutoLogin {

	public String[] login(HttpServletRequest request, HttpServletResponse response) {
		_log.info("GoogleOAuthAutoLogin.login() called");

		String[] credentials = null;

		HttpSession session = request.getSession();
		String id = session.getId();

		StorageItem attrs = GoogleKeyStorage.getStorage().get(id);

		if (attrs == null) {
			return credentials;
		}

		String google_response_url = attrs.getUrl();
		String oauth_token_secret = attrs.getToken();

		if (google_response_url == null || oauth_token_secret == null) {
			return credentials;
		}

		GoogleKeyStorage.getStorage().remove(id);

		GoogleOAuthParameters oauthParameters = GoogleOAuthPortlet.setoAuthParameters();

		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(new OAuthHmacSha1Signer());
		/*
		 * try { oauthHelper.getUnauthorizedRequestToken(oauthParameters); // to
		 * remove } catch (Exception e1) {
		 * _log.info("getUnauthorizedRequestToken failed: " + e1.toString()); }
		 */
		oauthHelper.getOAuthParametersFromCallback(google_response_url, oauthParameters);

		oauthParameters.setOAuthTokenSecret(oauth_token_secret);
		if (_log.isDebugEnabled()) {
			_log.debug("OAuth Access Token's Secret: " + oauth_token_secret);
		}

		try {
			String accessToken = "";
			accessToken = oauthHelper.getAccessToken(oauthParameters);

			oauthParameters.setOAuthToken(accessToken);

			String accessTokenSecret = "";
			accessTokenSecret = oauthParameters.getOAuthTokenSecret();

			if (_log.isDebugEnabled()) {
				_log.debug("OAuth Access Token's Secret:" + accessTokenSecret);
			}

			String site_name = GoogleOAuthPortlet.getSiteName();

			if (site_name == null || site_name == "") {
				return credentials;
			}

			ContactsService client = new ContactsService(site_name);

			client.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

			URL feedUrl;
			feedUrl = new URL(GoogleOAuthPortlet.SCOPE);
			long companyId = PortalUtil.getDefaultCompanyId();
			List<Person> authors = null;
			ContactFeed resultFeed;
			resultFeed = client.getFeed(feedUrl, ContactFeed.class);

			authors = resultFeed.getAuthors();
			if (authors == null) {
				_log.info("No feed author found");
				return credentials;
			} else {
				if (authors.isEmpty()) {
					if (_log.isWarnEnabled()) {
						_log.info("No profile details were found.");
					}
					return credentials;
				} else {
					Person author = authors.get(0);
					String email = author.getEmail();
					String name = author.getName();
					_log.info("Name: " + name);
					_log.info("Email: " + email);

					User user = null;
					try {
						user = UserLocalServiceUtil.getUserByEmailAddress(companyId, email);
					} catch (NoSuchUserException e) {
						_log.info("No such user: " + e.toString());
						user = addUser(companyId, email, name);
					} catch (Exception e) {
						_log.info("Failed to create user: " + e.toString());
						return credentials;
					}
					credentials = new String[3];
					credentials[0] = String.valueOf(user.getUserId());
					credentials[1] = user.getPassword();
					credentials[2] = Boolean.TRUE.toString();
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			_log.info("GoogleOAuthAutoLogin.login: " + e1.toString());
			return credentials;
		}

		return credentials;
	}

	private User addUser(Long companyId, String email, String name) {

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		long facebookId = 0;
		Locale locale = LocaleUtil.getDefault();
		String middleName = StringPool.BLANK;
		int prefixId = 0;
		int suffixId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;
		String openId = StringPool.BLANK;

		String[] fullName = name.split(" ");
		String firstName = StringPool.BLANK;
		String lastName = StringPool.BLANK;
		if (fullName.length == 1) {
			firstName = fullName[0];
			lastName = "--//--";
		} else {
			if (fullName.length > 1) {
				firstName = fullName[0];
				lastName = fullName[0];
			}
		}

		ServiceContext serviceContext = new ServiceContext();
		User user = null;

		try {
			user = UserLocalServiceUtil.addUser(creatorUserId, companyId,
					autoPassword, password1, password2, autoScreenName, name,
					email, facebookId, openId, locale, firstName, middleName,
					lastName, prefixId, suffixId, male, birthdayMonth,
					birthdayDay, birthdayYear, jobTitle, groupIds,
					organizationIds, roleIds, userGroupIds, sendEmail,
					serviceContext);
		} catch (Exception e) {
			_log.info("Failed to create user: " + e.toString());
		}
		return user;
	};

	private static Log _log = LogFactoryUtil.getLog(GoogleOAuthPortlet.class);
}
