package eu.stratuslab.authn;

import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;

import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;

public class OneProxyServletServer extends XmlRpcServletServer {

	final private static String X509_ATTR_NAME = "javax.servlet.request.X509Certificate";

	@Override
	protected XmlRpcHttpRequestConfigImpl newConfig(HttpServletRequest pRequest) {
		return new OneProxyRequestConfigImpl(extractUserDn(pRequest));
	}

	private String extractUserDn(HttpServletRequest request) {

		Object c = request.getAttribute(X509_ATTR_NAME);

		if (c != null && c instanceof X509Certificate[]) {
			X509Certificate[] certs = (X509Certificate[]) c;
			X500Principal principal = certs[0].getSubjectX500Principal();
			return principal.getName();
		}

		return "";

	}

}
