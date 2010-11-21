package eu.stratuslab.ssl;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;

public class GridSslSelectChannelConnector extends SslSelectChannelConnector {

	@Override
	public void doStart() throws Exception {

		Security.addProvider(new GridTrustManagerProvider());
		Security.addProvider(new BouncyCastleProvider());

		super.doStart();

	}

}
