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
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.CommonX509TrustManager;
import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.X509CertChainValidator;
import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;

public class GridTrustManagerFactorySpiImpl extends TrustManagerFactorySpi {

	public static final String DEFAULT_CA_DIR = "/etc/grid-security/certificates";
	public static final NamespaceCheckingMode DEFAULT_NS_CHECK_MODE = NamespaceCheckingMode.EUGRIDPMA_AND_GLOBUS;
	public static final Long DEFAULT_UPDATE_INTERVAL = 15L * 60L * 1000L; // 15min

	public static final String CA_DIR_ATTR = "CA_DIR_ATTR";
	public static final String NS_CHECK_MODE_ATTR = "NS_CHECK_MODE_ATTR";
	public static final String UPDATE_INTERVAL_ATTR = "UPDATE_INTERVAL_ATTR";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GridTrustManagerFactorySpiImpl.class.getCanonicalName());

	@Override
	protected TrustManager[] engineGetTrustManagers() {
		return new TrustManager[] { createTrustManager() };
	}

	@Override
	protected void engineInit(KeyStore arg0) throws KeyStoreException {
		initializeTrustManager();
	}

	@Override
	protected void engineInit(ManagerFactoryParameters arg0)
			throws InvalidAlgorithmParameterException {

		initializeTrustManager();
	}

	private void initializeTrustManager() {
	}

	private TrustManager createTrustManager() {

		Provider provider = Security
				.getProvider(GridTrustManagerProvider.PROVIDER_NAME);

		String dir = getCADirectory(provider.getProperty(CA_DIR_ATTR));
		NamespaceCheckingMode mode = getNSCheckingMode(provider
				.getProperty(NS_CHECK_MODE_ATTR));
		Long interval = getUpdateInterval(provider
				.getProperty(UPDATE_INTERVAL_ATTR));

		X509CertChainValidator validator = new OpensslCertChainValidator(dir,
				mode, interval);
		return new CommonX509TrustManager(validator);
	}

	private static String getCADirectory(String name) {

		String dir = (name != null) ? name : DEFAULT_CA_DIR;

		LOGGER.info("using {} for trusted certificates", dir);
		if (!(new File(dir)).isDirectory()) {
			LOGGER.error("trusted certificate directory {} does not exist",
					name);
		}
		return dir;
	}

	private static NamespaceCheckingMode getNSCheckingMode(String name) {

		NamespaceCheckingMode mode = DEFAULT_NS_CHECK_MODE;
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

	private static Long getUpdateInterval(String name) {

		Long interval = DEFAULT_UPDATE_INTERVAL;
		try {
			interval = Long.valueOf(name);
			if (interval <= 60000) {
				LOGGER.warn("interval cannot be less than 60000 ms");
				interval = DEFAULT_UPDATE_INTERVAL;
			}
		} catch (NumberFormatException e) {
			LOGGER.warn("invalid update interval: {}", name);
		}

		LOGGER.info("using update interval of {} ms", interval.toString());

		return interval;
	}

}
