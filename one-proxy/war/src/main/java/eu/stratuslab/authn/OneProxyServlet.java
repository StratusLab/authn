/*
 Created as part of the StratusLab project (http://stratuslab.eu),
 co-funded by the European Commission under the Grant Agreement
 INSFO-RI-261552.

 Copyright (c) 2010, Centre Nationale de la Recherche Scientifique

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package eu.stratuslab.authn;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcRequestConfig;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.common.XmlRpcNotAuthorizedException;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class OneProxyServlet extends XmlRpcServlet {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OneProxyServlet.class.getCanonicalName());

	private static final String PROXY_URL_PARAM_NAME = "oneProxyUrl";
	private static final String DEFAULT_PROXY_URL = "http://localhost:2633/RPC2";

	private static final Charset UTF8 = Charset.forName("UTF-8");

	private URL proxyUrl = null;

	@Override
	public void init(ServletConfig pConfig) throws ServletException {
		super.init(pConfig);

		proxyUrl = extractProxyUrl(pConfig);
	}

	@Override
	public XmlRpcHandlerMapping newXmlRpcHandlerMapping()
			throws XmlRpcException {

		return new ProxyHandlerMapping();
	}

	@Override
	public XmlRpcServletServer newXmlRpcServer(ServletConfig pConfig) {

		return new OneProxyServletServer();
	}

	private class ProxyHandlerMapping implements XmlRpcHandlerMapping {

		public XmlRpcHandler getHandler(String handlerName) {
			return new ProxyHandler(proxyUrl);
		}
	}

	private URL extractProxyUrl(ServletConfig pConfig) throws ServletException {

		String url = pConfig.getInitParameter(PROXY_URL_PARAM_NAME);
		if (url == null) {
			url = DEFAULT_PROXY_URL;
		}

		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new ServletException("Proxy URL is malformed: " + url);
		}
	}

	private static class ProxyHandler implements XmlRpcHandler {

		private final URL proxyUrl;

		public ProxyHandler(URL proxyUrl) {
			this.proxyUrl = proxyUrl;
		}

		public Object execute(XmlRpcRequest request) throws XmlRpcException {

			List<Object> params = prepareRequestParameters(request);

			return executeProxyRequest(request.getMethodName(), params);
		}

		private Object executeProxyRequest(String methodName,
				List<Object> params) throws XmlRpcException {

			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(proxyUrl);

			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);

			return client.execute(methodName, params);
		}

		private List<Object> prepareRequestParameters(XmlRpcRequest request)
				throws XmlRpcException {

			List<Object> params = new ArrayList<Object>();

			// Replace the authentication information.
			String authInfo = extractAuthnInfo(request);
			params.add(authInfo);

			// Log this request.
			LOGGER.info("forwarding request from {}", authInfo);

			// Copy all remaining parameters.
			for (int i = 1; i < request.getParameterCount(); i++) {
				params.add(request.getParameter(i));
			}

			return params;
		}

		private static String stripCNProxy(String username) {
			return username.replaceFirst("^CN\\s*=\\s*proxy\\s*,\\s*", "");
		}

		private String extractAuthnInfo(XmlRpcRequest request)
				throws XmlRpcException {

			XmlRpcRequestConfig config = request.getConfig();

			String user = "";
			String basicPswdHash = "";
			String defaultPswdHash = "dummy:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

			if (config instanceof OneProxyRequestConfigImpl) {
				OneProxyRequestConfigImpl opconfig = (OneProxyRequestConfigImpl) config;
				user = opconfig.getUserDn();
			}

			if ("".equals(user)
					&& config instanceof XmlRpcHttpRequestConfigImpl) {
				XmlRpcHttpRequestConfigImpl hconfig = (XmlRpcHttpRequestConfigImpl) config;
				user = hconfig.getBasicUserName();
				basicPswdHash = hashPassword(hconfig.getBasicPassword());
			}

			if (!"".equals(user)) {

				// Get rid of the proxy part of the DN.
				user = stripCNProxy(user);

				try {

					// Pass the hash of the password through if the username is
					// 'oneadmin'.
					String credentials = ("oneadmin".equals(user)) ? basicPswdHash
							: defaultPswdHash;

					// All of the usernames must be URL encoded to remove spaces
					// and other special characters.
					return URLEncoder.encode(user, "UTF-8") + ":" + credentials;

				} catch (UnsupportedEncodingException e) {
					LOGGER.error("can't create UTF-8 encoding for URL encoding");
					throw new XmlRpcException("internal server error");
				}
			} else {
				throw new XmlRpcNotAuthorizedException(
						"certificate DN or username not provided");
			}
		}
	}

	private static String hashPassword(String password) throws XmlRpcException {

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update((password != null) ? password.getBytes(UTF8)
					: new byte[] {});
			BigInteger digest = new BigInteger(1, md.digest());
			return String.format("%040x", digest);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("can't create UTF-8 encoding for URL encoding");
			throw new XmlRpcException("internal server error");
		}

	}

}
