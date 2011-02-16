package eu.stratuslab.authn;

import static org.junit.Assert.fail;

import org.junit.Test;

public class CertLoginModuleTest {

    private static final String DN = "CN=Charles_Loomis,OU=LAL,O=CNRS,C=FR,O=GRID-FR";
    private static final String PROXY_DN = "CN=proxy,CN=Charles_Loomis,OU=LAL,O=CNRS,C=FR,O=GRID-FR";

    @Test
    public void proxyStrippingOK() {

        String stripped = CertLoginModule.stripCNProxy(PROXY_DN);
        if (!DN.equals(stripped)) {
            fail("proxy was not stripped from DN: proxydn=" + PROXY_DN
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
