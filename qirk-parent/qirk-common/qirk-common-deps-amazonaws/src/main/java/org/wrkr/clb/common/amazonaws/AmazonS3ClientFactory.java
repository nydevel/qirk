/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.common.amazonaws;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AmazonS3ClientFactory implements ObjectFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ClientFactory.class);

    private static final String ACCESS_KEY_KEY = "accessKey";
    private static final String SECRET_KEY_KEY = "secretKey";
    private static final String STORAGE_URL_KEY = "storageUrl";
    private static final String REGION_KEY = "region";
    private static final String CONNECTION_TIMEOUT_KEY = "connectionTimeout";
    private static final String MAX_CONNECTIONS_KEY = "maxConnections";
    private static final String SOCKET_TIMEOUT_KEY = "socketTimeout";
    private static final String MAX_ERROR_RETRY_KEY = "maxErrorRetry";
    private static final String PAYLOAD_SIGNING_ENABLED_KEY = "payloadSigningEnabled";

    @Override
    public Object getObjectInstance(Object obj, @SuppressWarnings("unused") Name name,
            @SuppressWarnings("unused") Context nameCtx, @SuppressWarnings("unused") Hashtable<?, ?> environment)
            throws Exception {
        if (obj instanceof Reference) {
            Reference reference = (Reference) obj;
            Class<?> theClass = loadClass(this, reference.getClassName());
            if (AmazonS3.class.isAssignableFrom(theClass)) {
                Map<String, String> keys = new HashMap<String, String>(4);
                for (Enumeration<?> iter = reference.getAll(); iter.hasMoreElements();) {
                    StringRefAddr addr = (StringRefAddr) iter.nextElement();
                    switch (addr.getType()) {
                        case ACCESS_KEY_KEY:
                        case SECRET_KEY_KEY:
                        case STORAGE_URL_KEY:
                        case REGION_KEY:
                        case CONNECTION_TIMEOUT_KEY:
                        case MAX_CONNECTIONS_KEY:
                        case SOCKET_TIMEOUT_KEY:
                        case MAX_ERROR_RETRY_KEY:
                        case PAYLOAD_SIGNING_ENABLED_KEY:
                            keys.put(addr.getType(), (addr.getContent() == null ? null : addr.getContent().toString().strip()));
                            break;
                    }
                }
                try {
                    return createClient(
                            keys.get(ACCESS_KEY_KEY), keys.get(SECRET_KEY_KEY), keys.get(STORAGE_URL_KEY), keys.get(REGION_KEY),
                            keys.get(CONNECTION_TIMEOUT_KEY), keys.get(MAX_CONNECTIONS_KEY),
                            keys.get(SOCKET_TIMEOUT_KEY), keys.get(MAX_ERROR_RETRY_KEY), keys.get(PAYLOAD_SIGNING_ENABLED_KEY));
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    throw e;
                }
            }
        } else {
            LOG.error("Object " + obj + " is not an instance of javax.naming.Reference");
        }
        return null;
    }

    private String validateStringKey(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is not set in server.xml");
        }
        return value.strip();
    }

    private Integer validateIntegerKey(String name, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " is not an integer in server.xml", e);
        }
    }

    private int validateIntegerKey(String name, String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " is not an integer in server.xml", e);
        }
    }

    private boolean validateBooleanKey(String name, String value, boolean defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Boolean.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " is not a boolean in server.xml", e);
        }
    }

    protected Object createClient(String accessKey, String secretKey, String storageUrl, String region,
            String connectionTimeoutString, String maxConnectionsString, String socketTimeoutString, String maxErrorRetryString,
            String payloadSigningEnabledString) {
        accessKey = validateStringKey(ACCESS_KEY_KEY, accessKey);
        secretKey = validateStringKey(SECRET_KEY_KEY, secretKey);
        storageUrl = validateStringKey(STORAGE_URL_KEY, storageUrl);
        region = validateStringKey(REGION_KEY, region);

        int connectionTimeout = validateIntegerKey(CONNECTION_TIMEOUT_KEY, connectionTimeoutString,
                ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
        int maxConnections = validateIntegerKey(MAX_CONNECTIONS_KEY, maxConnectionsString,
                ClientConfiguration.DEFAULT_MAX_CONNECTIONS);
        int socketTimeout = validateIntegerKey(SOCKET_TIMEOUT_KEY, socketTimeoutString,
                ClientConfiguration.DEFAULT_SOCKET_TIMEOUT);
        Integer maxErrorRetry = validateIntegerKey(MAX_ERROR_RETRY_KEY, maxErrorRetryString);

        boolean payloadSigningEnabled = validateBooleanKey(PAYLOAD_SIGNING_ENABLED_KEY, payloadSigningEnabledString, false);

        ClientConfiguration config = new ClientConfiguration();
        config.setConnectionTimeout(connectionTimeout);
        config.setMaxConnections(maxConnections);
        config.setSocketTimeout(socketTimeout);
        if (maxErrorRetry != null) {
            config.setMaxErrorRetry(maxErrorRetry);
        }

        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration(storageUrl, region))
                .withClientConfiguration(config)
                .withPayloadSigningEnabled(payloadSigningEnabled)
                .build();
        return client;
    }

    private static Class<?> loadClass(Object obj, String className) throws ClassNotFoundException {
        ClassLoader loader = obj.getClass().getClassLoader();
        Class<?> theClass;
        if (loader != null) {
            theClass = loader.loadClass(className);
        } else {
            theClass = Class.forName(className);
        }
        return theClass;
    }
}
