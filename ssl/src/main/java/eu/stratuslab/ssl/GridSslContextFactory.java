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

import java.io.File;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CRL;
import java.util.Collection;

import javax.net.ssl.TrustManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.CommonX509TrustManager;
import eu.emi.security.authn.x509.CrlCheckingMode;
import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.RevocationParameters;
import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;

public class GridSslContextFactory extends SslContextFactory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GridSslContextFactory.class.getCanonicalName());

	public static final String ALGORITHM = "CommonX509TrustManager";

	public static final String DEFAULT_CA_DIRECTORY = //
	"/etc/grid-security/certificates";

	public static final NamespaceCheckingMode DEFAULT_NS_CHECKING_MODE = //
	NamespaceCheckingMode.EUGRIDPMA_AND_GLOBUS;

	public static final CrlCheckingMode DEFAULT_CRL_CHECKING_MODE = //
	CrlCheckingMode.REQUIRE;

	public static final Long DEFAULT_UPDATE_INTERVAL = //
	10L * 60L * 1000L; // 10min

	private final String caDirectory;
	private final NamespaceCheckingMode namespaceCheckingMode;
	private final Long updateInterval;
	private final CrlCheckingMode crlCheckingMode;

	public GridSslContextFactory(String caDirectory,
			String namespaceCheckingMode, String updateInterval,
			String crlCheckingMode) {

		super();

		registerCryptoServiceProvider();

		this.caDirectory = getCADirectory(caDirectory);
		this.namespaceCheckingMode = getNSCheckingMode(namespaceCheckingMode);
		this.updateInterval = getUpdateInterval(updateInterval);
		this.crlCheckingMode = getCrlCheckingMode(crlCheckingMode);

		// Algorithm name is never really used.
		setTrustManagerFactoryAlgorithm(ALGORITHM);

		// Allow, but don't require, client certificates to be compatible with
		// other authentication methods.
		setWantClientAuth(true);
	}

	@Override
	protected TrustManager[] getTrustManagers(KeyStore trustStore,
			Collection<? extends CRL> crls) throws Exception {

		return new TrustManager[] { createTrustManager() };
	}

	private void registerCryptoServiceProvider() {

		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			LOGGER.info("registering {} provider",
					BouncyCastleProvider.PROVIDER_NAME);
			Security.addProvider(new BouncyCastleProvider());
		}

	}

	private TrustManager createTrustManager() {

		OpensslCertChainValidator validator = new OpensslCertChainValidator(
				caDirectory, namespaceCheckingMode, updateInterval);

		RevocationParameters params = validator.getRevocationCheckingMode();
		params.setCrlCheckingMode(crlCheckingMode);

		TrustManager trustManager = new CommonX509TrustManager(validator);

		LOGGER.info("created new CommonX509TrustManager");

		return trustManager;
	}

	public static String getCADirectory(String name) {

		String dir = DEFAULT_CA_DIRECTORY;
		if (name != null && !"".equals(name.trim())) {
			dir = name;
		}

		LOGGER.info("using {} for trusted certificates", dir);
		if (!(new File(dir)).isDirectory()) {
			LOGGER.error("trusted certificate directory {} does not exist",
					name);
		}
		return dir;
	}

	public static NamespaceCheckingMode getNSCheckingMode(String name) {

		NamespaceCheckingMode mode = DEFAULT_NS_CHECKING_MODE;
		try {
			mode = NamespaceCheckingMode.valueOf(name);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("invalid namespace checking mode: {}", name);
		} catch (NullPointerException e) {
			LOGGER.warn("null namespace checking mode given");
		}

		LOGGER.info("using {} for namespace checking mode", mode.toString());
		return mode;
	}

	public static Long getUpdateInterval(String name) {

		Long interval = DEFAULT_UPDATE_INTERVAL;
		try {
			interval = Long.valueOf(name);
			if (interval < 60000) {
				LOGGER.warn("interval cannot be less than 60000 ms");
				interval = DEFAULT_UPDATE_INTERVAL;
			}
		} catch (NumberFormatException e) {
			LOGGER.warn("invalid update interval: {}", name);
		}

		LOGGER.info("using update interval of {} ms", interval.toString());

		return interval;
	}

	public static CrlCheckingMode getCrlCheckingMode(String name) {

		CrlCheckingMode mode = DEFAULT_CRL_CHECKING_MODE;
		try {
			mode = CrlCheckingMode.valueOf(name);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("invalid CRL checking mode: {}", name);
		} catch (NullPointerException e) {
			LOGGER.warn("null CRL checking mode given");
		}

		LOGGER.info("using {} for CRL checking mode", mode.toString());
		return mode;
	}

}
