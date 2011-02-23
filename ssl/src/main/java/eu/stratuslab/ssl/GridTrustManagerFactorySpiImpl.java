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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

import org.glite.security.trustmanager.OpensslTrustmanager;

public class GridTrustManagerFactorySpiImpl extends TrustManagerFactorySpi {

    private static AtomicReference<TrustManager> ref = new AtomicReference<TrustManager>();

    private static final String CA_DIRECTORY = "/etc/grid-security/certificates";

    private static final String TM_DEACTIVATED = "certificate authentication deactivated: ";

    final private static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(GridTrustManagerFactorySpiImpl.class
                .getCanonicalName());
        for (Handler h : LOGGER.getHandlers()) {
            LOGGER.removeHandler(h);
        }

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new ShortMsgFormatter());
        LOGGER.addHandler(handler);

        LOGGER.setUseParentHandlers(false);
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[] { ref.get() };
    }

    @Override
    protected void engineInit(KeyStore arg0) throws KeyStoreException {
        initializeTrustManager();
    }

    @Override
    protected void engineInit(ManagerFactoryParameters arg0)
            throws InvalidAlgorithmParameterException {

        initializeTrustManager();
    }

    private void initializeTrustManager() {
        if (ref.get() == null) {
            ref.compareAndSet(null, createTrustManager());
        }
    }

    private TrustManager createTrustManager() {

        try {

            return new OpensslTrustmanager(CA_DIRECTORY, true);

        } catch (CertificateException e) {
            return logErrorAndGetEmptyTrustManager(e.getMessage());
        } catch (NoSuchProviderException e) {
            return logErrorAndGetEmptyTrustManager(e.getMessage());
        } catch (IOException e) {
            return logErrorAndGetEmptyTrustManager(e.getMessage());
        } catch (ParseException e) {
            return logErrorAndGetEmptyTrustManager(e.getMessage());
        }
    }

    private TrustManager logErrorAndGetEmptyTrustManager(String msg) {
        LOGGER.severe(TM_DEACTIVATED + msg);
        return new EmptyTrustManager();
    }

    // TODO: Pull into separate class.
    private static class ShortMsgFormatter extends Formatter {

        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            Date date = new Date(record.getMillis());
            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");
            sb.append(dateFormat.format(date));
            sb.append(":");

            sb.append(record.getLevel().getName());
            sb.append("::");

            sb.append(record.getMessage());
            sb.append("\n");

            return sb.toString();
        }
    }

}
