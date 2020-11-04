package org.wrkr.clb.elasticsearch.datasync.config;

public class ElasticsearchClientConfig {

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
}
