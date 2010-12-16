
/*
 Created as part of the StratusLab project (http://stratuslab.eu),
 co-funded by the European Commission under the Grant Agreement
 INSFO-RI-261552."

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

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

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

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {

	super.execute(request, response);

	printRequest(request);
    }

    public static void printRequest(HttpServletRequest request) {
	
	StringBuilder sb = new StringBuilder();
	
        sb.append("session=" + request.getSession(true).getId() + "\n");
        sb.append("authtype=" + request.getAuthType() + "\n");
        sb.append("contextpath=" + request.getContextPath() + "\n");
        sb.append("principal=" + request.getUserPrincipal() + "\n");
	
        sb.append("cookies\n");
        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
            for (Cookie cookie : cookies) {
                sb.append(" -- " + formatCookie(cookie));
            }
        }
        sb.append("\n");
        sb.append(formatHeaders(request));
	
	System.err.println(sb.toString());
    }
    
    public static String formatCookie(Cookie cookie) {
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName() + " : ");
        sb.append(cookie.getDomain() + " : ");
        sb.append(cookie.getMaxAge() + " : ");
        sb.append(cookie.getSecure() + " : ");
        sb.append(cookie.getValue() + " : ");
        return sb.toString();
    }
    
    public static String formatHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("headers\n");
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getHeader(name);
            sb.append(" -- " + name + " = " + value + "\n");
        }
        sb.append("\n");
        return sb.toString();
    }

}
