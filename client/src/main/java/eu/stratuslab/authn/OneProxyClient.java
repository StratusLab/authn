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

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class OneProxyClient {

    public static void main(String[] args) throws NoSuchAlgorithmException {

        // System.setProperty("javax.net.ssl.keyStore", "jetty.jks");
        // System.setProperty("javax.net.ssl.keyStoreType", "jks");
        // System.setProperty("javax.net.ssl.keyStorePassword", "jettycred");

        System.setProperty("javax.net.ssl.keyStore", "grid.p12");
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
        System.setProperty("javax.net.ssl.keyStorePassword", "jettycred");

        System.setProperty("javax.net.ssl.trustStore",
                "src/main/certs/jetty.jks");
        System.setProperty("javax.net.ssl.trustStoreType", "jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "jettycred");

        System.setProperty("javax.net.debug", "ssl");

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            // config.setServerURL(new URL("https://onehost-172:8443/xmlrpc"));
            config.setServerURL(new URL(
                    "https://localhost:8443/authn_proxy/xmlrpc"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }

        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        Vector<Object> params = new Vector<Object>();

        params.addElement("tutorial:djdjdjdjd");
        params.addElement(Integer.valueOf(-1));

        try {
            Object result = client.execute("one.vmpool.info", params);
            Object[] values = (Object[]) result;
            int i = 0;
            for (Object o : values) {
                System.err.println(i++);
                System.err.println(o.toString());
            }
        } catch (XmlRpcException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
