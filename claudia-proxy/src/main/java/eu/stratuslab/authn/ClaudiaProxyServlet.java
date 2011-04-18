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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

@SuppressWarnings("serial")
public class ClaudiaProxyServlet extends HttpServlet {

    // This should NOT have a trailing slash!
    private static final String DEFAULT_URL = "http://www.lal.in2p3.fr";

    private static final int BUFFER_SIZE = 2048;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) {

        HttpClient httpclient = new DefaultHttpClient();

        String proxyUri = getProxyUri(request);
        HttpGet httpget = new HttpGet(proxyUri);

        System.err.println("DEBUG: " + proxyUri);

        Header[] headers = getHeaders(request);
        httpget.setHeaders(headers);

        try {
            HttpResponse clientResponse = httpclient.execute(httpget);

            HttpEntity entity = clientResponse.getEntity();
            if (entity != null) {
                try {
                    InputStream is = entity.getContent();
                    OutputStream os = response.getOutputStream();
                    copyAndClose(is, os);
                } catch (IOException e) {
                    // TODO: Log this.
                }
            }
        } catch (IOException e) {
            // TODO: Log this.
        }

    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) {

    }

    @Override
    protected void doPut(HttpServletRequest request,
            HttpServletResponse response) {

    }

    @Override
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response) {

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
            try {
                is.close();
            } catch (IOException e) {
                // TODO: Log this.
            }
            try {
                os.close();
            } catch (IOException e) {
                // TODO: Log this.
            }
        }

    }

}
