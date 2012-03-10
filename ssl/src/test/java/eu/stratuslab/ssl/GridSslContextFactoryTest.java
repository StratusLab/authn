package eu.stratuslab.ssl;

import static eu.stratuslab.ssl.GridSslContextFactory.DEFAULT_CA_DIRECTORY;
import static eu.stratuslab.ssl.GridSslContextFactory.DEFAULT_CRL_CHECKING_MODE;
import static eu.stratuslab.ssl.GridSslContextFactory.DEFAULT_NS_CHECKING_MODE;
import static eu.stratuslab.ssl.GridSslContextFactory.DEFAULT_UPDATE_INTERVAL;
import static eu.stratuslab.ssl.GridSslContextFactory.getCADirectory;
import static eu.stratuslab.ssl.GridSslContextFactory.getCrlCheckingMode;
import static eu.stratuslab.ssl.GridSslContextFactory.getNSCheckingMode;
import static eu.stratuslab.ssl.GridSslContextFactory.getUpdateInterval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.emi.security.authn.x509.CrlCheckingMode;
import eu.emi.security.authn.x509.NamespaceCheckingMode;

public class GridSslContextFactoryTest {

	@Test
	public void defaultUpdateInterval() {
		assertEquals(DEFAULT_UPDATE_INTERVAL, getUpdateInterval(null));
		assertEquals(DEFAULT_UPDATE_INTERVAL, getUpdateInterval("1"));
		assertEquals(DEFAULT_UPDATE_INTERVAL, getUpdateInterval("-1"));
		assertEquals(DEFAULT_UPDATE_INTERVAL, getUpdateInterval("59999"));
		assertEquals(DEFAULT_UPDATE_INTERVAL, getUpdateInterval("099"));
		assertEquals(DEFAULT_UPDATE_INTERVAL, getUpdateInterval("0xGG"));
	}

	@Test
	public void realUpdateInterval() {
		assertEquals(Long.valueOf(60000), getUpdateInterval("60000"));
		assertEquals(Long.valueOf(120000), getUpdateInterval("120000"));
	}

	@Test
	public void defaultNSMode() {
		assertEquals(DEFAULT_NS_CHECKING_MODE, getNSCheckingMode(null));
		assertEquals(DEFAULT_NS_CHECKING_MODE, getNSCheckingMode(""));
		assertEquals(DEFAULT_NS_CHECKING_MODE, getNSCheckingMode("DUMMY"));
	}

	@Test
	public void roundTripNSModes() {
		for (NamespaceCheckingMode mode : NamespaceCheckingMode.values()) {
			assertEquals(mode, getNSCheckingMode(mode.name()));
		}
	}

	@Test
	public void defaultCrlMode() {
		assertEquals(DEFAULT_CRL_CHECKING_MODE, getCrlCheckingMode(null));
		assertEquals(DEFAULT_CRL_CHECKING_MODE, getCrlCheckingMode(""));
		assertEquals(DEFAULT_CRL_CHECKING_MODE, getCrlCheckingMode("DUMMY"));
	}

	@Test
	public void roundTripCrlModes() {
		for (CrlCheckingMode mode : CrlCheckingMode.values()) {
			assertEquals(mode, getCrlCheckingMode(mode.name()));
		}
	}

	@Test
	public void defaultDirectory() {
		assertEquals(DEFAULT_CA_DIRECTORY, getCADirectory(null));
		assertEquals(DEFAULT_CA_DIRECTORY, getCADirectory(""));
		assertEquals(DEFAULT_CA_DIRECTORY, getCADirectory("   "));
	}

	@Test
	public void setDirectoryCorrectly() {
		assertEquals("/alpha/beta", getCADirectory("/alpha/beta"));
	}

}
