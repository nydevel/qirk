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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchService;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchUserDTO;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class DefaultElasticsearchService<E extends BaseIdEntity> implements ElasticsearchService<E> {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final int MAX_SEARCH_REQUEST_SIZE = 10000;

    @Autowired
    protected RestHighLevelClient client;

    protected static final String getAddElementsScript(String fieldName) {
        return "ctx._source." + fieldName + ".addAll(" +
                "params." + fieldName +
                ");";
    }

    protected static final String getRemoveElementsScript(String fieldName) {
        return "ctx._source." + fieldName + ".removeAll(" +
                "params." + fieldName +
                ");";
    }

    public abstract String getIndex();

    protected abstract String convertEntityToJson(E entity, RequestType request) throws JsonProcessingException;

    protected void logRequest(SearchRequest request) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Elasticsearch request: " + request.toString());
        }
    }

    protected void logResponse(ActionResponse response) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Elasticsearch response: " + response.toString());
        }
    }

    @Override
    public void index(E entity) {
        try {
            IndexRequest request = new IndexRequest(getIndex());
            request.id(entity.getId().toString());
            request.source(convertEntityToJson(entity, RequestType.INDEX), XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            logResponse(response);
        } catch (Exception e) {
            LOG.error("Could not save entity " + entity + " to elasticsearch", e);
        }
    }

    @Override
    public boolean exists(E entity) throws IOException {
        GetRequest request = new GetRequest(getIndex(), entity.getId().toString());
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");

        return client.exists(request, RequestOptions.DEFAULT);
    }

    protected void update(E entity, RequestType requestType) throws IOException {
        UpdateRequest request = new UpdateRequest(getIndex(), entity.getId().toString());
        request.doc(convertEntityToJson(entity, requestType), XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        logResponse(response);
    }

    @Override
    public void updateOrIndex(E entity) throws IOException {
        if (exists(entity)) {
            update(entity, RequestType.UPDATE);
            LOG.debug("Updated entity " + entity + " in elasticsearch");
        } else {
            index(entity);
            LOG.debug("Saved entity " + entity + " in elasticsearch");
        }
    }

    @Override
    public void datasync(E entity) throws IOException {
        if (exists(entity)) {
            update(entity, RequestType.DATASYNC_UPDATE);
            LOG.debug("Updated entity " + entity + " in elasticsearch");
        } else {
            index(entity);
            LOG.debug("Saved entity " + entity + " in elasticsearch");
        }
    }

    @Override
    public MultiGetItemResponse[] multiGet(List<Long> ids) throws Exception {
        return multiGet(ids, Strings.EMPTY_ARRAY);
    }

    @Override
    public MultiGetItemResponse[] multiGet(List<Long> ids, String[] includeFields) throws IOException {
        if (ids.isEmpty()) {
            return new MultiGetItemResponse[0];
        }
        if (includeFields == null) {
            includeFields = Strings.EMPTY_ARRAY;
        }

        MultiGetRequest request = new MultiGetRequest();

        for (Long id : ids) {
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includeFields, Strings.EMPTY_ARRAY);
            request.add(new MultiGetRequest.Item(getIndex(), id.toString()).fetchSourceContext(fetchSourceContext));
        }

        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
        return response.getResponses();
    }

    @Override
    public Set<String> getIds() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(new MatchAllQueryBuilder());
        sourceBuilder.fetchSource(false);

        SearchRequest request = new SearchRequest(getIndex());
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();

        Set<String> ids = new HashSet<String>(hits.getHits().length);
        for (SearchHit hit : hits) {
            ids.add(hit.getId());
        }
        return ids;
    }

    @Override
    public void delete(String id) throws IOException {
        DeleteRequest request = new DeleteRequest(getIndex(), id);

        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        logResponse(response);
    }

    protected QueryBuilder buildPrefixOrMatchQuery(String field, String searchValue) {
        // only one word - search for prefix
        if (!searchValue.isBlank() && !searchValue.contains(" ")) {
            return new PrefixQueryBuilder(ElasticsearchUserDTO.TAGS, searchValue);
        }

        MatchQueryBuilder queryBuilder = new MatchQueryBuilder(field, searchValue);
        queryBuilder.zeroTermsQuery(ZeroTermsQuery.NONE);
        queryBuilder.operator(Operator.AND);
        return queryBuilder;
    }

    private SearchHits executeSearchRequest(SearchSourceBuilder sourceBuilder) throws IOException {
        long startTime = System.currentTimeMillis();

        SearchRequest request = new SearchRequest(getIndex());
        request.source(sourceBuilder);
        logRequest(request);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isInfoEnabled()) {
            LOG.info("processed search for index " + getIndex() + " in elasticsearch in " + resultTime + " ms");
        }
        logResponse(response);

        return response.getHits();
    }

    protected SearchHits search(QueryBuilder queryBuilder, String[] includeFields, String[] excludeFields) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(queryBuilder);
        sourceBuilder.fetchSource(includeFields, excludeFields);
        sourceBuilder.size(MAX_SEARCH_REQUEST_SIZE);
        sourceBuilder.trackTotalHits(false);

        return executeSearchRequest(sourceBuilder);
    }

    protected SearchHits search(QueryBuilder queryBuilder) throws IOException {
        return search(queryBuilder, Strings.EMPTY_ARRAY, Strings.EMPTY_ARRAY);
    }

    protected SearchHits search(QueryBuilder queryBuilder,
            String[] includeFields, String[] excludeFields,
            int size, Object[] searchAfter, SortBuilder<?> sortBuilder)
            throws IOException {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(queryBuilder);
        sourceBuilder.fetchSource(includeFields, excludeFields);
        sourceBuilder.size(size);
        if (searchAfter != null) {
            sourceBuilder.searchAfter(searchAfter);
        }
        if (sortBuilder != null) {
            sourceBuilder.trackScores(false);
            sourceBuilder.sort(sortBuilder);
        }

        return executeSearchRequest(sourceBuilder);
    }
}
