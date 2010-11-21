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

public class GridTrustManagerFactorySpiImpl extends TrustManagerFactorySpi {

	private static AtomicReference<TrustManager> ref = new AtomicReference<TrustManager>();

	@Override
	protected TrustManager[] engineGetTrustManagers() {
		return new TrustManager[] { ref.get() };
	}

	@Override
	protected void engineInit(KeyStore arg0) throws KeyStoreException {

		try {
			initializeTrustManager();
		} catch (InitializationException e) {
			throw new KeyStoreException(e);
		}
	}

	@Override
	protected void engineInit(ManagerFactoryParameters arg0)
			throws InvalidAlgorithmParameterException {

		try {
			initializeTrustManager();
		} catch (InitializationException e) {
			throw new InvalidAlgorithmParameterException(e);
		}
	}

	private void initializeTrustManager() throws InitializationException {
		if (ref.get() == null) {
			ref.compareAndSet(null, createTrustManager());
		}
	}

	private TrustManager createTrustManager() throws InitializationException {

		try {
			return new OpensslTrustmanager("/etc/grid-security/certificates",
					true);
		} catch (CertificateException e) {
			throw new InitializationException(e.getMessage());
		} catch (NoSuchProviderException e) {
			throw new InitializationException(e.getMessage());
		} catch (IOException e) {
			throw new InitializationException(e.getMessage());
		} catch (ParseException e) {
			throw new InitializationException(e.getMessage());
		}
	}

	@SuppressWarnings("serial")
	private class InitializationException extends Exception {

		public InitializationException(String message) {
			super(message);
		}
	}

}
