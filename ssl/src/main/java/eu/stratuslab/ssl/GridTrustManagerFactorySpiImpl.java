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
import java.util.concurrent.atomic.AtomicReference;

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

	private static final AtomicReference<TrustManager> ref = new AtomicReference<TrustManager>();

	private static final String DEFAULT_CA_DIR = "/etc/grid-security/certificates";
	private static final NamespaceCheckingMode DEFAULT_NS_CHECK_MODE = NamespaceCheckingMode.EUGRIDPMA_AND_GLOBUS;
	private static final Long DEFAULT_UPDATE_INTERVAL = 15L * 60L * 1000L; // 15min

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GridTrustManagerFactorySpiImpl.class.getCanonicalName());

	@Override
	protected TrustManager[] engineGetTrustManagers() {
		return new TrustManager[] { ref.get() };
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
		if (ref.get() == null) {
			ref.compareAndSet(null, createTrustManager());
		}
	}

	private TrustManager createTrustManager() {

		String dir = getCADirectory();
		NamespaceCheckingMode mode = getNSCheckingMode();
		Long interval = getUpdateInterval();

		X509CertChainValidator validator = new OpensslCertChainValidator(dir,
				mode, interval);
		return new CommonX509TrustManager(validator);
	}

	private static String getCADirectory() {

		String dir = System.getProperty("STRATUSLAB_CA_DIRECTORY",
				DEFAULT_CA_DIR);

		LOGGER.info("using {} for trusted certificates", dir);
		if (!(new File(dir)).isDirectory()) {
			LOGGER.error("trusted certificate directory {} does not exist", dir);
		}
		return dir;
	}

	private static NamespaceCheckingMode getNSCheckingMode() {

		String name = System.getProperty("STRATUSLAB_NS_CHECK_MODE",
				DEFAULT_NS_CHECK_MODE.name());

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

	private static Long getUpdateInterval() {

		String name = System.getProperty("STRATUSLAB_UPDATE_INTERVAL",
				DEFAULT_UPDATE_INTERVAL.toString());

		Long interval = DEFAULT_UPDATE_INTERVAL;
		try {
			interval = Long.valueOf(name);
		} catch (NumberFormatException e) {
			LOGGER.warn("invalid update interval: {}", name);
		}

		LOGGER.info("using {} for update interval", interval.toString());

		return interval;
	}
}
