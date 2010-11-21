package eu.stratuslab.authn;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

@SuppressWarnings("serial")
public class OneProxyRequestConfigImpl extends XmlRpcClientConfigImpl {

	final private String userDn;

	public OneProxyRequestConfigImpl(String userDn) {
		super();

		this.userDn = (userDn != null) ? userDn : "";
	}

	public String getUserDn() {
		return userDn;
	}

}
