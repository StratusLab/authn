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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;

@SuppressWarnings("serial")
public class ClaudiaProxyServlet extends HttpServlet {

    protected static final Logger LOGGER = Logger
            .getLogger("eu.stratuslab.claudia.proxy");

    static {

        // Remove all existing handlers.
        Handler[] handlers = LOGGER.getHandlers();
        for (Handler handler : handlers) {
            LOGGER.removeHandler(handler);
        }

        // Basic handler.
        LOGGER.addHandler(new ConsoleHandler());
        LOGGER.setUseParentHandlers(false);

    }

    // This should NOT have a trailing slash!
    private static final String DEFAULT_URL = "http://localhost:8182";

    private static final int BUFFER_SIZE = 2048;

    private static final String X509_ATTR_NAME = "javax.servlet.request.X509Certificate";

    private static final String STRATUSLAB_USER_HEADER = "X-StratusLab-User";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) {

        String proxyUri = getProxyUri(request);
        LOGGER.info("GET: " + proxyUri);
        System.err.println("GET: " + proxyUri);
        HttpGet httpget = new HttpGet(proxyUri);

        copyAndModifyHeaders(httpget, request);

        HttpEntity entity = sendMsgAndSetStatus(httpget, response);
        if (entity != null) {
            try {
                InputStream is = entity.getContent();
                OutputStream os = response.getOutputStream();
                copyAndClose(is, os);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                System.err.println(e.getMessage());
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) {

        String proxyUri = getProxyUri(request);
        LOGGER.info("POST: " + proxyUri);
        System.err.println("POST: " + proxyUri);
        HttpPost httppost = new HttpPost(proxyUri);

        copyAndModifyHeaders(httppost, request);

        httppost.setEntity(createEntityFromRequest(request));

        HttpEntity entity = sendMsgAndSetStatus(httppost, response);

        if (entity != null) {
            try {
                InputStream is = entity.getContent();
                OutputStream os = response.getOutputStream();
                copyAndClose(is, os);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                System.err.println(e.getMessage());
            }
        }

    }

    @Override
    protected void doPut(HttpServletRequest request,
            HttpServletResponse response) {

        String proxyUri = getProxyUri(request);
        LOGGER.info("PUT: " + proxyUri);
        System.err.println("PUT: " + proxyUri);
        HttpPut httpput = new HttpPut(proxyUri);

        copyAndModifyHeaders(httpput, request);

        httpput.setEntity(createEntityFromRequest(request));

        HttpEntity entity = sendMsgAndSetStatus(httpput, response);

        if (entity != null) {
            try {
                InputStream is = entity.getContent();
                OutputStream os = response.getOutputStream();
                copyAndClose(is, os);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                System.err.println(e.getMessage());
            }
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response) {

        String proxyUri = getProxyUri(request);
        LOGGER.info("DELETE: " + proxyUri);
        System.err.println("DELETE: " + proxyUri);
        HttpDelete httpdelete = new HttpDelete(proxyUri);

        copyAndModifyHeaders(httpdelete, request);

        sendMsgAndSetStatus(httpdelete, response);

    }

    public static String getProxyUri(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder(DEFAULT_URL);

        String path = request.getPathInfo();
        if (path != null) {
            sb.append(path);
        } else {
            sb.append("/");
        }

        String query = request.getQueryString();
        if (query != null) {
            sb.append("?");
            sb.append(query);
        }

        return sb.toString();

    }

    public static HttpEntity createEntityFromRequest(HttpServletRequest request) {
        try {

            InputStream is = request.getInputStream();
            long length = request.getContentLength();
            return new InputStreamEntity(is, length);

        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return null;
        }
    }

    public static HttpEntity sendMsgAndSetStatus(HttpUriRequest msg,
            HttpServletResponse response) {

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse clientResponse = httpclient.execute(msg);
            StatusLine statusline = clientResponse.getStatusLine();
            response.setStatus(statusline.getStatusCode());

            LOGGER.info("POST status: " + statusline.getStatusCode());
            System.err.println("POST status: " + statusline.getStatusCode());

            return clientResponse.getEntity();

        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    public static Header[] getHeaders(HttpServletRequest request) {
        ArrayList<Header> headers = new ArrayList<Header>();

        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getHeader(name);
            Header header = new BasicHeader(name, value);
            headers.add(header);
        }

        return headers.toArray(new Header[headers.size()]);
    }

    private void copyAndModifyHeaders(AbstractHttpMessage msg,
            HttpServletRequest request) {

        Header[] headers = getHeaders(request);
        msg.setHeaders(headers);

        String username = getUsername(request);
        String loginfo = "ADDING " + STRATUSLAB_USER_HEADER + " HEADER: "
                + username;
        LOGGER.info(loginfo);
        System.err.println(loginfo);
        msg.addHeader(STRATUSLAB_USER_HEADER, username);

    }

    private void copyAndClose(InputStream is, OutputStream os)
            throws IOException {
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            for (int n = is.read(buf); n != -1; n = is.read(buf)) {
                os.write(buf, 0, n);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            closeReliably(is);
            closeReliably(os);
        }

    }

    private String getUsername(HttpServletRequest request) {
        String username = extractUserDn(request);
        if (username == null) {
            username = extractBasicUsername(request);
        }
        return username;
    }

    private String extractBasicUsername(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal != null) {
            return userPrincipal.getName();
        } else {
            return "";
        }
    }

    private String extractUserDn(HttpServletRequest request) {

        Object c = request.getAttribute(X509_ATTR_NAME);

        if (c instanceof X509Certificate[]) {
            X509Certificate[] certs = (X509Certificate[]) c;
            X500Principal principal = certs[0].getSubjectX500Principal();
            String dn = principal.getName();
            return stripCNProxy(dn);
        }

        return "";

    }

    private void closeReliably(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            System.err.println(e.getMessage());
        }
    }

    private static String stripCNProxy(String username) {
        return username.replaceFirst("^CN\\s*=\\s*proxy\\s*,\\s*", "");
    }

}
