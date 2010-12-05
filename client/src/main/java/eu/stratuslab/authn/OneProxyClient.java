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

		System.setProperty("javax.net.ssl.trustStore", "src/main/certs/jetty.jks");
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "jettycred");

		System.setProperty("javax.net.debug", "ssl");

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL("https://onehost-172:8444/xmlrpc"));
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
