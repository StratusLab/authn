package eu.stratuslab.authn;

import static org.junit.Assert.fail;

import org.junit.Test;

public class CertLoginModuleTest {

    private static final String DN = "CN=Charles Loomis,OU=LAL,O=CNRS,C=FR,O=GRID-FR";
    private static final String OLD_PROXY_DN = "CN=proxy,CN=Charles Loomis,OU=LAL,O=CNRS,C=FR,O=GRID-FR";
    private static final String RFC_PROXY_DN = "CN=1391896933,CN=Charles Loomis,OU=LAL,O=CNRS,C=FR,O=GRID-FR";

    @Test
    public void proxyStrippingOK() {

        String stripped = CertLoginModule.stripCNProxy(OLD_PROXY_DN);
        if (!DN.equals(stripped)) {
            fail("proxy was not stripped from DN: proxydn=" + OLD_PROXY_DN
                    + "; stripped=" + stripped);
        }

        stripped = CertLoginModule.stripCNProxy(RFC_PROXY_DN);
        if (!DN.equals(stripped)) {
            fail("proxy was not stripped from DN: proxydn=" + RFC_PROXY_DN
                    + "; stripped=" + stripped);
        }
    }

    public void proxyStrippingUnneededOK() {

        String stripped = CertLoginModule.stripCNProxy(DN);
        if (!DN.equals(stripped)) {
            fail("proxy changed DN incorrectly: dn=" + DN + "; stripped="
                    + stripped);
        }
    }
}
