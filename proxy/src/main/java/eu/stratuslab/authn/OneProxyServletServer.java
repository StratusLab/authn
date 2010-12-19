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
