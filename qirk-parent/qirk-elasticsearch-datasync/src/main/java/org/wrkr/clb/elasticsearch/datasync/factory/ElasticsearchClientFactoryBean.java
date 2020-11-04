package org.wrkr.clb.elasticsearch.datasync.factory;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;
import org.wrkr.clb.common.elasticsearch.ElasticsearchClientFactory;

public class ElasticsearchClientFactoryBean extends ElasticsearchClientFactory implements FactoryBean<RestHighLevelClient> {

    private String joinedNodes;
    private String scheme;
    private String username;
    private String password;

    public String getJoinedNodes() {
        return joinedNodes;
    }

    public void setJoinedNodes(String joinedNodes) {
        this.joinedNodes = joinedNodes;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public RestHighLevelClient getObject() throws Exception {
        return (RestHighLevelClient) createClient(joinedNodes, scheme, username, password);
    }

    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
