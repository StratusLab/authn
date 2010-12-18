package eu.stratuslab.authn;

import java.security.Principal;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.spi.LoginModule;

import org.eclipse.jetty.plus.jaas.JAASRole;

public class CertLoginModule implements LoginModule {

    public boolean abort() {
        return true;
    }

    public boolean commit() {
        return true;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {

        System.err.println("Adding 'cloud-access' role...");
        JAASRole role = new JAASRole("cloud-access");
        subject.getPrincipals().add(role);

        System.err.println("Principals:");
        for (Principal p : subject.getPrincipals()) {
            System.err.println(p.getName());
        }

        System.err.println("Shared State:");
        for (Entry<String, ?> entry : sharedState.entrySet()) {
            System.err.println(entry.getKey() + " => " + entry.getValue());
        }

        System.err.println("\nOptions:");
        for (Entry<String, ?> entry : options.entrySet()) {
            System.err.println(entry.getKey() + " => " + entry.getValue());
        }
        System.err.println("\n");

    }

    public boolean login() {
        return true;
    }

    public boolean logout() {
        return true;
    }

}
