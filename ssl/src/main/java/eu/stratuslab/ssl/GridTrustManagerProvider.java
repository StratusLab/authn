package eu.stratuslab.ssl;

import java.security.Provider;

@SuppressWarnings("serial")
final public class GridTrustManagerProvider extends Provider {

	public static final String DESCRIPTION = "Provider for TrustManagerFactory that uses grid certificates";

	public static final double VERSION = 1.0;

	public static final String PROVIDER_NAME = "GridTrustManagerProvider";

	public static final String SERVICE_NAME = "TrustManagerFactory";

	public static final String ALGORITHM = "GridTM";

	public static final String TMF_SPI_CLASS = "eu.stratuslab.ssl.GridTrustManagerFactorySpiImpl";

	public GridTrustManagerProvider() {

		super(PROVIDER_NAME, VERSION, DESCRIPTION);

		Provider.Service service = new Provider.Service(this, SERVICE_NAME,
				ALGORITHM, TMF_SPI_CLASS, null, null);

		this.putService(service);
	}
}
