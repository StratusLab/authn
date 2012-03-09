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
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridSslContextFactory extends SslContextFactory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GridSslContextFactory.class.getCanonicalName());

	private String caDirectory = null;
	private String namespaceCheckingMode = null;
	private String updateInterval = null;

	public GridSslContextFactory() {

		super();

		initializeDefaultValues();
	}

	public GridSslContextFactory(String caDirectory, String nsCheckMode,
			String updateInterval) {

		super();

		this.caDirectory = caDirectory;
		this.namespaceCheckingMode = nsCheckMode;
		this.updateInterval = updateInterval;

		initializeDefaultValues();
	}

	@Override
	public void doStart() throws Exception {

		registerTrustManagerServiceProvider();
		registerCryptoServiceProvider();

		super.doStart();

	}

	private void initializeDefaultValues() {
		setTrustManagerFactoryAlgorithm(GridTrustManagerProvider.ALGORITHM);
		setWantClientAuth(true);
	}

	public String getCaDirectory() {
		return caDirectory;
	}

	public void setCaDirectory(String caDirectory) {
		this.caDirectory = caDirectory;
	}

	public String getNamespaceCheckingMode() {
		return namespaceCheckingMode;
	}

	public void setNamespaceCheckingMode(String namespaceCheckingMode) {
		this.namespaceCheckingMode = namespaceCheckingMode;
	}

	public String getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(String updateInterval) {
		this.updateInterval = updateInterval;
	}

	private void registerTrustManagerServiceProvider() {

		if (Security.getProvider(GridTrustManagerProvider.PROVIDER_NAME) == null) {
			LOGGER.info("registering {} provider",
					GridTrustManagerProvider.PROVIDER_NAME);
			Security.addProvider(new GridTrustManagerProvider(caDirectory,
					namespaceCheckingMode, updateInterval));
		}

	}

	private void registerCryptoServiceProvider() {

		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			LOGGER.info("registering {} provider",
					BouncyCastleProvider.PROVIDER_NAME);
			Security.addProvider(new BouncyCastleProvider());
		}

	}

}
