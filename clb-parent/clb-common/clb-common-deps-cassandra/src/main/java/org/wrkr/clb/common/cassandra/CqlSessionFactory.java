/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.common.cassandra;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.metadata.EndPoint;
import com.datastax.oss.driver.internal.core.metadata.DefaultEndPoint;
import com.datastax.oss.driver.internal.core.type.codec.BigIntCodec;

public class CqlSessionFactory implements ObjectFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CqlSessionFactory.class);

    private static final String NODES_KEY = "nodes";
    private static final String KEYSPACE_KEY = "keyspace";
    private static final String LOCALDC_KEY = "localDataCenter";
    private static final String PROP_FILE_KEY = "applicationConfFile";

    private static final String CATALINA_HOME_PLACEHOLDER = "${sys:catalina.home}";

    @Override
    public Object getObjectInstance(Object obj, @SuppressWarnings("unused") Name name,
            @SuppressWarnings("unused") Context nameCtx, @SuppressWarnings("unused") Hashtable<?, ?> environment)
            throws Exception {
        if (obj instanceof Reference) {
            Reference reference = (Reference) obj;
            Class<?> theClass = loadClass(this, reference.getClassName());
            if (CqlSession.class.isAssignableFrom(theClass)) {
                String nodes = "";
                String keyspace = "";
                String localDataCenter = "";
                String propertiesFilePath = "";
                for (Enumeration<?> iter = reference.getAll(); iter.hasMoreElements();) {
                    StringRefAddr addr = (StringRefAddr) iter.nextElement();
                    switch (addr.getType()) {
                        case NODES_KEY:
                            nodes = addr.getContent() == null ? "" : addr.getContent().toString();
                            break;
                        case KEYSPACE_KEY:
                            keyspace = addr.getContent() == null ? "" : addr.getContent().toString();
                            break;
                        case LOCALDC_KEY:
                            localDataCenter = addr.getContent() == null ? "" : addr.getContent().toString();
                            break;
                        case PROP_FILE_KEY:
                            propertiesFilePath = addr.getContent() == null ? "" : addr.getContent().toString();
                            break;
                    }
                }
                try {
                    return createSession(nodes, keyspace, localDataCenter, propertiesFilePath);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    throw e;
                }
            }
        } else {
            String errStr = "Object" + obj + "is not an instance of javax.naming.Reference";
            LOG.error(errStr);
        }
        return null;
    }

    protected CqlSession createSession(String nodes, String keyspace, String localDataCenter, String propertiesFilePath)
            throws UnknownHostException {
        LOG.info("creating CqlSession with nodes: " + nodes + "; keyspace: " + keyspace + "; localDataCenter: " + localDataCenter
                + "; propertiesFilePath: " + propertiesFilePath);

        nodes = nodes.strip();
        if (nodes.isEmpty()) {
            throw new IllegalArgumentException(NODES_KEY + " is not set in server.xml");
        }

        keyspace = keyspace.strip();
        if (keyspace.isEmpty()) {
            throw new IllegalArgumentException(KEYSPACE_KEY + " is not set in server.xml");
        }

        localDataCenter = localDataCenter.strip();
        if (localDataCenter.isEmpty()) {
            throw new IllegalArgumentException(
                    "If 2 or more nodes are configured you must set" + LOCALDC_KEY + " attribute in server.xml");
        }

        CqlSessionBuilder sessionBuilder = CqlSession.builder();
        List<EndPoint> endpoints = parseNodes(nodes);
        sessionBuilder.addContactEndPoints(endpoints);
        sessionBuilder.withKeyspace(keyspace);
        sessionBuilder.withLocalDatacenter(localDataCenter);

        propertiesFilePath = propertiesFilePath.strip();
        if (!propertiesFilePath.isEmpty()) {
            propertiesFilePath = replaceHomePlaceholder(propertiesFilePath);
            File applicationConf = new File(propertiesFilePath);
            if (!applicationConf.isFile()) {
                String errStr = "Wrong value of " + PROP_FILE_KEY + " attribute: " +
                        "file " + applicationConf.getAbsolutePath() + " does not exist";
                throw new IllegalArgumentException(errStr);
            }
            sessionBuilder.withConfigLoader(DriverConfigLoader.fromFile(applicationConf));
        }

        sessionBuilder.addTypeCodecs(new BigIntCodec());

        CqlSession session = sessionBuilder.build();
        LOG.info("created CqlSession with nodes: " + nodes + "; keyspace: " + keyspace + "; localDataCenter: " + localDataCenter
                + "; propertiesFilePath: " + propertiesFilePath);
        return session;
    }

    private String replaceHomePlaceholder(String path) {
        if (path.contains(CATALINA_HOME_PLACEHOLDER)) {
            return path.replace(CATALINA_HOME_PLACEHOLDER, System.getProperty("catalina.home"));
        }
        return path;
    }

    private List<EndPoint> parseNodes(String nodesString) throws UnknownHostException {
        List<EndPoint> result = new ArrayList<EndPoint>();
        String[] nodes = nodesString.split(";");
        for (String nodeString : nodes) {
            StringTokenizer st = new StringTokenizer(nodeString, ":");
            if (st.countTokens() != 2) {
                String errStr = "Wrong " + NODES_KEY + " attribute in server.xml; wrong node address: " + nodeString
                        + "; host:port expected";
                throw new IllegalArgumentException(errStr);
            }
            String host = st.nextToken().strip();
            int port = -1;
            String portStr = st.nextToken().strip();
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                String errStr = "Wrong port format of " + NODES_KEY + " attribute in server.xml: " + portStr;
                throw new IllegalArgumentException(errStr, e);
            }
            result.add(new DefaultEndPoint(new InetSocketAddress(InetAddress.getByName(host), port)));
        }
        if (result.isEmpty()) {
            String errStr = "No nodes to connect to, check " + NODES_KEY + " attribute in server.xml";
            throw new IllegalArgumentException(errStr);
        }
        return result;
    }

    private static Class<?> loadClass(Object thisObj, String className) throws ClassNotFoundException {
        ClassLoader loader = thisObj.getClass().getClassLoader();
        Class<?> theClass;
        if (loader != null) {
            theClass = loader.loadClass(className);
        } else {
            theClass = Class.forName(className);
        }
        return theClass;
    }
}
