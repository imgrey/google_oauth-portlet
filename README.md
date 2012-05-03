google OAuthportlet for Liferay
================================

2-legged google OAuth portlet for Liferay

Allows you to authenticate your users against google.

You need to go to Google Apps For Business website 
( http://www.google.com/enterprise/apps/business/index.html  ),
get account ( currently its free ). Then you go to Domain Management Interface > 
Advanced Tools > Manage OAuth domain key.

You generate OAuth consumer secret and put it to portal.properties. Along with OAuth consumer key.


Dependencies:
* jstl
* gdata library (http://code.google.com/p/gdata-java-client/  )


Configuration

In portal.properties you need to change the following:
* google_oauth_consumer_key -- usualy your domain name
* google_oauth_consumer_secret -- your google apps secret key
* google_oauth_site_name -- sitename that will be shown when user gets redirected to google for authentication

