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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

import org.glite.security.trustmanager.OpensslTrustmanager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridTrustManagerFactorySpiImpl extends TrustManagerFactorySpi {

	private static AtomicReference<TrustManager> ref = new AtomicReference<TrustManager>();

	private static final String CA_DIRECTORY = "/etc/grid-security/certificates";

	private static final String TM_DEACTIVATED = "certificate authentication deactivated: {}";

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

		try {

			// TODO: This needs to be updated to actually pass the expected
			// properties in the last argument.
			return new OpensslTrustmanager(CA_DIRECTORY, true, null);

		} catch (CertificateException e) {
			return logErrorAndGetEmptyTrustManager(e.getMessage());
		} catch (NoSuchProviderException e) {
			return logErrorAndGetEmptyTrustManager(e.getMessage());
		} catch (IOException e) {
			return logErrorAndGetEmptyTrustManager(e.getMessage());
		} catch (ParseException e) {
			return logErrorAndGetEmptyTrustManager(e.getMessage());
		}
	}

	private TrustManager logErrorAndGetEmptyTrustManager(String msg) {
		LOGGER.error(TM_DEACTIVATED, msg);
		return new EmptyTrustManager();
	}

}
