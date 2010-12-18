package eu.stratuslab.authn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.http.security.Credential;
import org.eclipse.jetty.plus.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.plus.jaas.spi.UserInfo;

public class CertLoginModule extends AbstractLoginModule {

    @Override
    public UserInfo getUserInfo(String username) throws Exception {

        Credential credential = new AlwaysValidCredential();

        List<String> roles = new ArrayList<String>(1);
        roles.add("cloud-access");

        return new UserInfo(username, credential, roles);
    }

    /**
     * With a certificate, the validity checking has always been done at the
     * transport layer. Return a credential that always evaluates to being
     * valid.
     * 
     * @author loomis
     * 
     */
    @SuppressWarnings("serial")
    private static class AlwaysValidCredential extends Credential {

        @Override
        public boolean check(Object credentials) {
            return true;
        }
    }

}
