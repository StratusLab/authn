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

import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public final class GridTrustManagerProvider extends Provider {

	public static final String DESCRIPTION = "Provider for TrustManagerFactory that uses grid certificates";

	public static final double VERSION = 1.0;

	public static final String PROVIDER_NAME = "GridTrustManagerProvider";

	public static final String SERVICE_NAME = "TrustManagerFactory";

	public static final String ALGORITHM = "GridTM";

	public static final String TMF_SPI_CLASS = "eu.stratuslab.ssl.GridTrustManagerFactorySpiImpl";

	public GridTrustManagerProvider(String caDirectory, String nsCheckMode,
			String updateInterval) {

		super(PROVIDER_NAME, VERSION, DESCRIPTION);

		Map<String, String> attrs = createAttributesMap(caDirectory,
				nsCheckMode, updateInterval);

		Provider.Service service = new Provider.Service(this, SERVICE_NAME,
				ALGORITHM, TMF_SPI_CLASS, null, attrs);

		this.putService(service);
	}

	private Map<String, String> createAttributesMap(String caDirectory,
			String nsCheckMode, String updateInterval) {

		Map<String, String> attrs = new HashMap<String, String>();

		attrs.put(GridTrustManagerFactorySpiImpl.CA_DIR_ATTR, caDirectory);
		attrs.put(GridTrustManagerFactorySpiImpl.NS_CHECK_MODE_ATTR,
				nsCheckMode);
		attrs.put(GridTrustManagerFactorySpiImpl.UPDATE_INTERVAL_ATTR,
				updateInterval);

		return attrs;

	}
}
