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
