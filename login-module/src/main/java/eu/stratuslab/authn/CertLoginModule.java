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

package eu.stratuslab.authn;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import org.eclipse.jetty.http.security.Credential;
import org.eclipse.jetty.plus.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.plus.jaas.spi.UserInfo;

public class CertLoginModule extends AbstractLoginModule {

    final private static AtomicReference<AuthnData> authnUsersRef = new AtomicReference<AuthnData>();

    static {
        authnUsersRef.set(new AuthnData(null));
    }

    final private static Logger logger;
    static {
        logger = Logger.getLogger("eu.stratuslab.authn");
        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        Handler handler = new ConsoleHandler();
        logger.addHandler(handler);
    }

    @Override
    public UserInfo getUserInfo(String username) throws Exception {

        Credential credential = createUserCredential(username);
        List<String> roles = getUserRoles(username);

        return new UserInfo(username, credential, roles);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map sharedState, Map options) {

        super.initialize(subject, callbackHandler, sharedState, options);

        authnUsersRef.set(new AuthnData(options.get("file")));
    }

    private Credential createUserCredential(String username) {
        AuthnData data = authnUsersRef.get();
        logger.warning("TRYING TO CREATE USER: " + username);
        return new BooleanCredential(data.isValidUser(username));
    }

    private List<String> getUserRoles(String username) {
        AuthnData data = authnUsersRef.get();
        return data.groups(username);
    }

    @SuppressWarnings("serial")
    private static class BooleanCredential extends Credential {

        final private boolean valid;

        public BooleanCredential(boolean valid) {
            this.valid = valid;
        }

        @Override
        public boolean check(Object credentials) {
            return valid;
        }
    }

}
