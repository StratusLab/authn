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

package eu.stratuslab.ssl;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridSslSelectChannelConnector extends SslSelectChannelConnector {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GridSslSelectChannelConnector.class.getCanonicalName());

	public GridSslSelectChannelConnector(SslContextFactory sslContextFactory) {
		super(sslContextFactory);
	}

	public GridSslSelectChannelConnector() {
		super();
	}

	@Override
	public void doStart() throws Exception {

		if (Security.getProvider(GridTrustManagerProvider.PROVIDER_NAME) == null) {
			LOGGER.info("registering {} provider",
					GridTrustManagerProvider.PROVIDER_NAME);
			Security.addProvider(new GridTrustManagerProvider());
		}
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			LOGGER.info("registering {} provider",
					BouncyCastleProvider.PROVIDER_NAME);
			Security.addProvider(new BouncyCastleProvider());
		}
		
		super.doStart();

	}

}
