package org.wrkr.clb.common.elasticsearch;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchClientFactory implements ObjectFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchClientFactory.class);

    private static final String NODES_KEY = "nodes";
    private static final String SCHEME_KEY = "scheme";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    @Override
    public Object getObjectInstance(Object obj, @SuppressWarnings("unused") Name name,
            @SuppressWarnings("unused") Context nameCtx, @SuppressWarnings("unused") Hashtable<?, ?> environment)
            throws Exception {
        if (obj instanceof Reference) {
            Reference reference = (Reference) obj;
            Class<?> theClass = loadClass(this, reference.getClassName());
            if (RestHighLevelClient.class.isAssignableFrom(theClass)) {
                Map<String, String> keys = new HashMap<String, String>(4);
                for (Enumeration<?> iter = reference.getAll(); iter.hasMoreElements();) {
                    StringRefAddr addr = (StringRefAddr) iter.nextElement();
                    switch (addr.getType()) {
                        case NODES_KEY:
                        case SCHEME_KEY:
                        case USERNAME_KEY:
                        case PASSWORD_KEY:
                            keys.put(addr.getType(), (addr.getContent() == null ? null : addr.getContent().toString().strip()));
                            break;
                    }
                }
                return createClient(keys.get(NODES_KEY), keys.get(SCHEME_KEY), keys.get(USERNAME_KEY), keys.get(PASSWORD_KEY));
            }
        } else {
            LOG.error("Object " + obj + " is not an instance of javax.naming.Reference");
        }
        return null;
    }

    protected Object createClient(String joinedNodes, String scheme, String username, String password) {
        if (joinedNodes == null || joinedNodes.isBlank()) {
            throw new IllegalArgumentException(NODES_KEY + " is not set in server.xml");
        }
        String[] nodes = joinedNodes.strip().split(";");

        if (scheme == null || scheme.isBlank()) {
            throw new IllegalArgumentException(SCHEME_KEY + " is not set in server.xml");
        }
        scheme = scheme.strip();

        List<HttpHost> hosts = new ArrayList<HttpHost>();
        for (String node : nodes) {
            node = node.strip();
            if (node.isEmpty()) {
                continue;
            }

            int lastColonIndex = node.lastIndexOf(':');
            if (lastColonIndex < 0) {
                throw new IllegalArgumentException("port is missing in node " + node + " in server.xml");
            }
            Integer port = null;
            try {
                port = Integer.parseInt(node.substring(lastColonIndex + 1).strip());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("port is not an integer in node " + node + " in server.xml", e);
            }
            hosts.add(new HttpHost(node.substring(0, lastColonIndex).strip(), port, scheme));
        }

        RestClientBuilder builder = RestClient.builder(hosts.toArray(new HttpHost[hosts.size()]));
        boolean useCredentials = ((username != null && !username.isBlank()) || (password != null && !password.isBlank()));
        if (useCredentials) {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException(PASSWORD_KEY + " is set in server.xml, but " + USERNAME_KEY + "is not");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException(USERNAME_KEY + " is set in server.xml, but " + PASSWORD_KEY + "is not");
            }

            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username.strip(), password.strip()));
            builder = builder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }

        RestHighLevelClient restClient = new RestHighLevelClient(builder);
        return restClient;
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
