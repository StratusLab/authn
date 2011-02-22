package eu.stratuslab.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * This TrustManager rejects all requests to trust a client or server
 * certificate. This is intended to be used only when the configuration of a
 * real, functional TrustManager fails.
 * 
 * @author loomis
 * 
 */
public class EmptyTrustManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        throw new CertificateException("no trusted root certificates");
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        throw new CertificateException("no trusted root certificates");
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] {};
    }
}
