package eu.stratuslab.authn;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

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

@SuppressWarnings("serial")
public class OneProxyServlet extends XmlRpcServlet {

	final private static String PROXY_URL_PARAM_NAME = "oneProxyUrl";
	final private static String DEFAULT_PROXY_URL = "http://localhost:2633/RPC2";

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

	private class ProxyHandler implements XmlRpcHandler {

		final private URL proxyUrl;

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
				throws XmlRpcNotAuthorizedException {

			Vector<Object> params = new Vector<Object>();

			// Replace the authentication information.
			String authInfo = extractAuthnInfo(request);
			System.err.println("AUTH INFO: " + authInfo);
			params.addElement(authInfo);

			// Copy all remaining parameters.
			for (int i = 1; i < request.getParameterCount(); i++) {
				params.addElement(request.getParameter(i));
			}

			return params;
		}

		private String extractAuthnInfo(XmlRpcRequest request)
				throws XmlRpcNotAuthorizedException {

		    String passwd = "c5c9b9371be52dbb3d838aa5d687057c71966dc8";

			XmlRpcRequestConfig config = request.getConfig();

			if (config instanceof OneProxyRequestConfigImpl) {
				OneProxyRequestConfigImpl opconfig = (OneProxyRequestConfigImpl) config;
				return opconfig.getUserDn() + ":" + passwd;
			}

			if (config instanceof XmlRpcHttpRequestConfigImpl) {
				XmlRpcHttpRequestConfigImpl hconfig = (XmlRpcHttpRequestConfigImpl) config;
				return hconfig.getBasicUserName() + ":" + passwd;
			}

			throw new XmlRpcNotAuthorizedException(
					"certificate DN or username not provided");
		}

	}

}
