package eu.stratuslab.ssl;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;

public class GridSslSelectChannelConnector extends SslSelectChannelConnector {

	@Override
	public void doStart() throws Exception {

		// Add the BouncyCastle (crypto algorithms) and TrustManager providers.
		if (Security.getProvider(GridTrustManagerProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new GridTrustManagerProvider());
		}
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		super.doStart();

	}
}
