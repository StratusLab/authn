// ========================================================================
// Copyright (c) 2007-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at 
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses. 
// ========================================================================
/*
 * This is a modified version of the standard Jetty LDAP login module
 * intended to be used with certificates.  This uses the standard user
 * and group lookups from the standard module, but DOES NO PASSWORD CHECK!
 *
 * This should also be used in a context in which a certificate has
 * been checked and validated. 
 */
package eu.stratuslab.authn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.eclipse.jetty.http.security.Credential;
import org.eclipse.jetty.plus.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.plus.jaas.spi.UserInfo;
import org.eclipse.jetty.util.log.Log;

/**
 * A LdapLoginModule for use with JAAS setups
 * <p/>
 * The jvm should be started with the following parameter: <br>
 * <br>
 * <code>
 * -Djava.security.auth.login.config=etc/ldap-loginModule.conf
 * </code> <br>
 * <br>
 * and an example of the ldap-loginModule.conf would be: <br>
 * <br>
 * 
 * <pre>
 * ldaploginmodule {
 *    org.eclipse.jetty.server.server.plus.jaas.spi.LdapLoginModule required
 *    useLdaps="false"
 *    contextFactory="com.sun.jndi.ldap.LdapCtxFactory"
 *    hostname="ldap.example.com"
 *    port="389"
 *    bindDn="cn=Directory Manager"
 *    bindPassword="directory"
 *    authenticationMethod="simple"
 *    userBaseDn="ou=people,dc=alcatel"
 *    userRdnAttribute="uid"
 *    userIdAttribute="uid"
 *    userPasswordAttribute="userPassword"
 *    userObjectClass="inetOrgPerson"
 *    roleBaseDn="ou=groups,dc=example,dc=com"
 *    roleNameAttribute="cn"
 *    roleMemberAttribute="uniqueMember"
 *    roleObjectClass="groupOfUniqueNames";
 *    };
 * </pre>
 * 
 * 
 * 
 * 
 */
public class LdapCertLoginModule extends AbstractLoginModule {

    /**
     * hostname of the ldap server
     */
    private String _hostname;

    /**
     * port of the ldap server
     */
    private int _port;

    /**
     * Context.SECURITY_AUTHENTICATION
     */
    private String _authenticationMethod;

    /**
     * Context.INITIAL_CONTEXT_FACTORY
     */
    private String _contextFactory;

    /**
     * root DN used to connect to
     */
    private String _bindDn;

    /**
     * password used to connect to the root ldap context
     */
    private String _bindPassword;

    /**
     * object class of a user
     */
    private String _userObjectClass = "inetOrgPerson";

    /**
     * attribute that the principal is located
     */
    private String _userRdnAttribute = "uid";

    /**
     * attribute that the principal is located
     */
    private String _userIdAttribute = "cn";

    /**
     * name of the attribute that a users password is stored under
     * <p/>
     * NOTE: not always accessible, see force binding login
     */
    private String _userPasswordAttribute = "userPassword";

    /**
     * base DN where users are to be searched from
     */
    private String _userBaseDn;

    /**
     * base DN where role membership is to be searched from
     */
    private String _roleBaseDn;

    /**
     * object class of roles
     */
    private String _roleObjectClass = "groupOfUniqueNames";

    /**
     * name of the attribute that a username would be under a role class
     */
    private String _roleMemberAttribute = "uniqueMember";

    /**
     * the name of the attribute that a role would be stored under
     */
    private String _roleNameAttribute = "roleName";

    /**
     * When true changes the protocol to ldaps
     */
    private boolean _useLdaps = false;

    private DirContext _rootContext;

    /**
     * get the available information about the user
     * <p/>
     * for this LoginModule, the credential can be null which will result in a
     * binding ldap authentication scenario
     * <p/>
     * roles are also an optional concept if required
     * 
     * @param username
     * @return the userinfo for the username
     * @throws Exception
     */
    public UserInfo getUserInfo(String username) throws Exception {
        Credential credential = Credential.getCredential("");
        List<String> roles = getUserRoles(_rootContext, username);

        return new UserInfo(username, credential, roles);
    }

    /**
     * attempts to get the users roles from the root context
     * <p/>
     * NOTE: this is not an user authenticated operation
     * 
     * @param dirContext
     * @param username
     * @return
     * @throws LoginException
     */
    private List<String> getUserRoles(DirContext dirContext, String username)
            throws LoginException, NamingException {

        String userDn = _userRdnAttribute + "=" + username + "," + _userBaseDn;

        return getUserRolesByDn(dirContext, userDn);
    }

    private List<String> getUserRolesByDn(DirContext dirContext, String userDn)
            throws LoginException, NamingException {

        List<String> roleList = new ArrayList<String>();

        if (dirContext == null || _roleBaseDn == null
                || _roleMemberAttribute == null || _roleObjectClass == null) {
            return roleList;
        }

        SearchControls ctls = new SearchControls();
        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String filter = "(&(objectClass={0})({1}={2}))";
        Object[] filterArguments = { _roleObjectClass, _roleMemberAttribute,
                userDn };
        NamingEnumeration<SearchResult> results = dirContext.search(
                _roleBaseDn, filter, filterArguments, ctls);

        Log.info("Found user roles?: " + results.hasMoreElements());

        while (results.hasMoreElements()) {
            SearchResult result = results.nextElement();

            Attributes attributes = result.getAttributes();

            if (attributes == null) {
                continue;
            }

            Attribute roleAttribute = attributes.get(_roleNameAttribute);

            if (roleAttribute == null) {
                continue;
            }

            NamingEnumeration<?> roles = roleAttribute.getAll();
            while (roles.hasMore()) {
                roleList.add(roles.next().toString());
            }
        }

        return roleList;
    }

    /**
     * since ldap uses a context bind for valid authentication checking, we
     * override login()
     * <p/>
     * if credentials are not available from the users context or if we are
     * forcing the binding check then we try a binding authentication check,
     * otherwise if we have the users encoded password then we can try
     * authentication via that mechanic
     * 
     * @return true if authenticated, false otherwise
     * @throws LoginException
     */
    @Override
    public boolean login() throws LoginException {
        try {
            if (getCallbackHandler() == null) {
                throw new LoginException("No callback handler");
            }

            Callback[] callbacks = configureCallbacks();
            getCallbackHandler().handle(callbacks);

            String webUserName = ((NameCallback) callbacks[0]).getName();

            if (webUserName == null) {
                setAuthenticated(false);
                return isAuthenticated();
            }

            return bindingLogin(webUserName);

        } catch (UnsupportedCallbackException e) {
            throw new LoginException("Error obtaining callback information.");
        } catch (IOException e) {
            throw new LoginException("IO Error performing login.");
        } catch (Exception e) {
            throw new LoginException("Error obtaining user info.");
        }
    }

    /**
     * binding authentication check This method of authentication works only if
     * the user branch of the DIT (ldap tree) has an ACI (access control
     * instruction) that allow the access to any user or at least for the user
     * that logs in.
     * 
     * @param username
     * @return true always
     * @throws LoginException
     */
    public boolean bindingLogin(String username) throws LoginException,
            NamingException {

        Log.info("Searching for user identifier: " + username);

        SearchResult searchResult = findUser(username);

        String userDn = searchResult.getNameInNamespace();

        Log.info("Finding group information for: " + userDn);

        List<String> roles = getUserRolesByDn(_rootContext, userDn);

        UserInfo userInfo = new UserInfo(username, null, roles);
        setCurrentUser(new JAASUserInfo(userInfo));
        setAuthenticated(true);

        return true;
    }

    private SearchResult findUser(String username) throws NamingException,
            LoginException {

        SearchControls ctls = new SearchControls();
        ctls.setCountLimit(1);
        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String filter = "(&(objectClass={0})({1}={2}))";

        Object[] filterArguments = new Object[] { _userObjectClass,
                _userIdAttribute, username };

        Log.info("Searching for users with filter: \'"
                + String.format(filter, filterArguments) + "\'"
                + " from base dn: " + _userBaseDn);

        NamingEnumeration<SearchResult> results = _rootContext.search(
                _userBaseDn, filter, filterArguments, ctls);

        Log.info("Found user?: " + results.hasMoreElements());

        if (!results.hasMoreElements()) {
            throw new LoginException("User not found.");
        }

        return (SearchResult) results.nextElement();
    }

    /**
     * Init LoginModule. Called once by JAAS after new instance is created.
     * 
     * @param subject
     * @param callbackHandler
     * @param sharedState
     * @param options
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);

        _hostname = (String) options.get("hostname");
        _port = Integer.parseInt((String) options.get("port"));
        _contextFactory = (String) options.get("contextFactory");
        _bindDn = (String) options.get("bindDn");
        _bindPassword = (String) options.get("bindPassword");
        _authenticationMethod = (String) options.get("authenticationMethod");

        _userBaseDn = (String) options.get("userBaseDn");

        _roleBaseDn = (String) options.get("roleBaseDn");

        if (options.containsKey("useLdaps")) {
            _useLdaps = Boolean.parseBoolean((String) options.get("useLdaps"));
        }

        _userObjectClass = getOption(options, "userObjectClass",
                _userObjectClass);
        _userRdnAttribute = getOption(options, "userRdnAttribute",
                _userRdnAttribute);
        _userIdAttribute = getOption(options, "userIdAttribute",
                _userIdAttribute);
        _userPasswordAttribute = getOption(options, "userPasswordAttribute",
                _userPasswordAttribute);
        _roleObjectClass = getOption(options, "roleObjectClass",
                _roleObjectClass);
        _roleMemberAttribute = getOption(options, "roleMemberAttribute",
                _roleMemberAttribute);
        _roleNameAttribute = getOption(options, "roleNameAttribute",
                _roleNameAttribute);

        try {
            _rootContext = new InitialDirContext(getEnvironment());
        } catch (NamingException ex) {
            throw new IllegalStateException("Unable to establish root context",
                    ex);
        }
    }

    @Override
    public boolean commit() throws LoginException {
        closeRootContext();
        return super.commit();
    }

    @Override
    public boolean abort() throws LoginException {
        closeRootContext();
        return super.abort();
    }

    private void closeRootContext() throws LoginException {
        try {
            _rootContext.close();
        } catch (NamingException e) {
            throw new LoginException("error closing root context: "
                    + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private String getOption(Map options, String key, String defaultValue) {
        Object value = options.get(key);

        if (value == null) {
            return defaultValue;
        }

        return (String) value;
    }

    /**
     * get the context for connection
     * 
     * @return the environment details for the context
     */
    public Hashtable<Object, Object> getEnvironment() {
        Properties env = new Properties();

        env.put(Context.INITIAL_CONTEXT_FACTORY, _contextFactory);

        if (_hostname != null) {
            env.put(Context.PROVIDER_URL, (_useLdaps ? "ldaps://" : "ldap://")
                    + _hostname + (_port == 0 ? "" : ":" + _port) + "/");
        }

        if (_authenticationMethod != null) {
            env.put(Context.SECURITY_AUTHENTICATION, _authenticationMethod);
        }

        if (_bindDn != null) {
            env.put(Context.SECURITY_PRINCIPAL, _bindDn);
        }

        if (_bindPassword != null) {
            env.put(Context.SECURITY_CREDENTIALS, _bindPassword);
        }

        return env;
    }

}
