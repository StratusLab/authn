package eu.stratuslab.authn;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;

public class OneProxyClient {

	public static void main(String[] args) throws NoSuchAlgorithmException {

		// System.setProperty("javax.net.ssl.keyStore", "jetty.jks");
		// System.setProperty("javax.net.ssl.keyStoreType", "jks");
		// System.setProperty("javax.net.ssl.keyStorePassword", "jettycred");

		System.setProperty("javax.net.ssl.keyStore", "grid.p12");
		System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		System.setProperty("javax.net.ssl.keyStorePassword", "jettycred");

		System.setProperty("javax.net.ssl.trustStore", "jetty.jks");
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "jettycred");

		// System.setProperty("javax.net.debug", "ssl");

		Provider provider = new eu.stratuslab.ssl.GridTrustManagerProvider();

		Security.addProvider(provider);

		Provider p = Security.getProvider("StratusLabTrustProvider");

		System.out.println("My provider name is " + p.getName());
		System.out.println("My provider version # is " + p.getVersion());
		System.out.println("My provider info is " + p.getInfo());

		TrustManagerFactorySpi tmspi = new eu.stratuslab.ssl.GridTrustManagerFactorySpiImpl();
		TrustManagerFactory factory = TrustManagerFactory.getInstance("GridTM");

		// XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		// try {
		// config.setServerURL(new URL("https://localhost:8443/xmlrpc"));
		// } catch (MalformedURLException e) {
		// throw new RuntimeException(e.getMessage());
		// }

		// XmlRpcClient client = new XmlRpcClient();
		// client.setConfig(config);

		// Vector<Object> params = new Vector<Object>();

		// params.addElement("tutorial:djdjdjdjd");
		// params.addElement(Integer.valueOf(-1));

		// try {
		// Object result = client.execute("one.vmpool.info", params);
		// System.err.println(result.toString());
		// } catch (XmlRpcException e) {
		// throw new RuntimeException(e.getMessage());
		// }
	}

}
