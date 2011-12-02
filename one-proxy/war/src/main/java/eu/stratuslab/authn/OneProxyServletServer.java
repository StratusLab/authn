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

import javax.servlet.http.HttpServletRequest;

import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;

public class OneProxyServletServer extends XmlRpcServletServer {

    private static final String SHIBBOLETH_IDP_HEADER = "Shib-Identity-Provider";

    private static final String SHIBBOLETH_USER_HEADER = "REMOTE_USER";

    @Override
    protected XmlRpcHttpRequestConfigImpl newConfig(HttpServletRequest pRequest) {
        return new OneProxyRequestConfigImpl(extractUserDn(pRequest));
    }

    private String extractUserDn(HttpServletRequest request) {

        String idp = request.getHeader(SHIBBOLETH_IDP_HEADER);
        String user = request.getHeader(SHIBBOLETH_USER_HEADER);

        if (idp != null && user != null) {
            return String.format("%s/%s", idp, user);
        }

        return "";
    }

}
