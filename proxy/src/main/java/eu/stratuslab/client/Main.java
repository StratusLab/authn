package eu.stratuslab.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Main {

    public static void main(String[] args) throws MalformedURLException {

        trustAllCertificates();

        String endpoint = "https://cloud-grnet.stratuslab.eu:2634/pswd/xmlrpc";

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(endpoint));
        config.setBasicUserName("harald");
        config.setBasicPassword("g00ny6ForDs");

        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        System.out.println(client.getMaxThreads());
        client.setMaxThreads(100);

        List<Object> params = new ArrayList<Object>();
        params.add("ignored_user:ignored_password");
        params.add(Integer.valueOf(-1));

        try {
            Object[] results = (Object[]) client.execute("one.vmpool.info",
                    params);

            System.err.println(results[0]);
            System.err.println(results[1]);

        } catch (XmlRpcException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void trustAllCertificates() {

        // Create empty HostnameVerifier
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
