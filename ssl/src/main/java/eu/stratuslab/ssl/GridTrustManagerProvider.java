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

@SuppressWarnings("serial")
final public class GridTrustManagerProvider extends Provider {

    public static final String DESCRIPTION = "Provider for TrustManagerFactory that uses grid certificates";

    public static final double VERSION = 1.0;

    public static final String PROVIDER_NAME = "GridTrustManagerProvider";

    public static final String SERVICE_NAME = "TrustManagerFactory";

    public static final String ALGORITHM = "GridTM";

    public static final String TMF_SPI_CLASS = "eu.stratuslab.ssl.GridTrustManagerFactorySpiImpl";

    public GridTrustManagerProvider() {

        super(PROVIDER_NAME, VERSION, DESCRIPTION);

        Provider.Service service = new Provider.Service(this, SERVICE_NAME,
                ALGORITHM, TMF_SPI_CLASS, null, null);

        this.putService(service);
    }
}
