package eu.stratuslab.ssl;

import java.security.Provider;

@SuppressWarnings("serial")
final public class GridTrustManagerProvider extends Provider {

	public GridTrustManagerProvider() {
		super("StratusLabTrustProvider", 1.0,
				"Provider for TrustManagerFactory that uses grid certificates");

		Provider.Service service = new Provider.Service(this,
				"TrustManagerFactory", "GridTM",
				"eu.stratuslab.ssl.GridTrustManagerFactorySpiImpl", null,
				null);

		this.putService(service);

		this.put("TrustManagerFactory.GridTM",
				"eu.stratuslab.authn.GridTrustManagerFactorySpiImpl");

		System.err.println("GOT TO PROVIDER!!");
		System.err.println(this.getService("TrustManagerFactory", "GridTM"));
	}

}
