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
package org.wrkr.clb.services.api.elasticsearch.impl;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchClusterService;


@Service
public class DefaultElasticsearchClusterService implements ElasticsearchClusterService {

    @Autowired
    protected RestHighLevelClient client;

    @Override
    public void checkClusterHealth() throws IOException {
        ClusterHealthRequest request = new ClusterHealthRequest();
        client.cluster().health(request, RequestOptions.DEFAULT);
    }
}
