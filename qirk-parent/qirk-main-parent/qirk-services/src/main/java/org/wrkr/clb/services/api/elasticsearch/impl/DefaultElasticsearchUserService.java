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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchNestedProjectDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchUserDTO;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class DefaultElasticsearchUserService extends DefaultElasticsearchService<User> implements ElasticsearchUserService {

    public static final String INDEX = "user";

    private static final String[] DEFAULT_SEARCH_EXCLUDE_FIELDS = {
            ElasticsearchUserDTO.NAME_SEARCH_FIELD,
            ElasticsearchUserDTO.TAGS, ElasticsearchUserDTO.ORGANIZATIONS + ".*",
            ElasticsearchUserDTO.PROJECTS, ElasticsearchUserDTO.INVITED_PROJECTS };

    private static final String[] SEARCH_EXCLUDE_FIELDS_FOR_TAGS = ArrayUtils.add(
            DEFAULT_SEARCH_EXCLUDE_FIELDS, ElasticsearchUserDTO.ORGANIZATIONS + ".*");

    @Override
    public String getIndex() {
        return INDEX;
    }

    @Override
    protected String convertEntityToJson(User user, RequestType request) throws JsonProcessingException {
        ElasticsearchUserDTO dto = ElasticsearchUserDTO.fromEntity(user);
        if (RequestType.INDEX.equals(request)) {
            dto.organizations = new ArrayList<Map<String, Object>>();
            dto.projects = new ArrayList<Long>();
            dto.invitedProjects = new ArrayList<Long>();
        }
        return dto.toJson();
    }

    @Override
    public void setProjects(User user, List<Long> projectIds) throws IOException {
        UpdateRequest request = new UpdateRequest(getIndex(), user.getId().toString());

        Map<String, Object> source = new HashMap<String, Object>(3);
        source.put(ElasticsearchUserDTO.PROJECTS, projectIds);
        request.doc(source, XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        logResponse(response);
    }

    private Map<String, Object> getProjectParams(Long projectId) {
        return getProjectParams(Arrays.asList(projectId));
    }

    private Map<String, Object> getProjectParams(List<Long> projectIds) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(ElasticsearchUserDTO.PROJECTS, projectIds);
        return map;
    }

    @Override
    public void addProject(Long userId, Long projectId) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.INLINE, "painless",
                        getAddElementsScript(ElasticsearchUserDTO.PROJECTS),
                        getProjectParams(projectId)));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    @Override
    public void removeProject(Long userId, Long projectId) throws IOException {
        removeProjects(userId, Arrays.asList(projectId));
    }

    @Override
    public void removeProjects(Long userId, List<Long> projectIds) throws IOException {
        if (projectIds.isEmpty()) {
            return;
        }

        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.INLINE, "painless",
                        getRemoveElementsScript(ElasticsearchUserDTO.PROJECTS),
                        getProjectParams(projectIds)));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    private Map<String, Object> getInvitedProjectParams(Long projectId) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(ElasticsearchUserDTO.INVITED_PROJECTS, Arrays.asList(projectId));
        return map;
    }

    @Override
    public void addInvitedProject(Long userId, Long projectId) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.INLINE, "painless",
                        getAddElementsScript(ElasticsearchUserDTO.INVITED_PROJECTS),
                        getInvitedProjectParams(projectId)));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    @Override
    public void removeInvitedProject(Long userId, Long projectId) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.INLINE, "painless",
                        getRemoveElementsScript(ElasticsearchUserDTO.INVITED_PROJECTS),
                        getInvitedProjectParams(projectId)));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    private QueryBuilder buildNameQuery(String prefix) {
        if (prefix.isEmpty()) {
            return new MatchAllQueryBuilder();
        }

        return new MatchPhrasePrefixQueryBuilder(
                ElasticsearchUserDTO.NAME_SEARCH_FIELD, prefix);
    }

    @Override
    public SearchHits searchByName(String prefix) throws IOException {
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);
        return search(identityQueryBuilder, Strings.EMPTY_ARRAY, SEARCH_EXCLUDE_FIELDS_FOR_TAGS);
    }

    private NestedQueryBuilder buildOrganizationIdQuery(Long organizationId) {
        TermQueryBuilder nestedQuery = new TermQueryBuilder(
                ElasticsearchUserDTO.ORGANIZATIONS + "." + ElasticsearchNestedProjectDTO.ORGANIZATION_ID, organizationId);
        return new NestedQueryBuilder(ElasticsearchUserDTO.ORGANIZATIONS, nestedQuery, ScoreMode.None);
    }

    private QueryBuilder buildOrganizationIdQueryWithInnerHits(Long organizationId) {
        NestedQueryBuilder query = buildOrganizationIdQuery(organizationId);

        InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
        innerHitBuilder.setSize(1);
        innerHitBuilder.setTrackScores(false);
        query.innerHit(innerHitBuilder);

        return query;
    }

    @Override
    public SearchHits searchByNameAndExcludeOrganization(String prefix, Long organizationId) throws IOException {
        QueryBuilder organizationIdQueryBuilder = buildOrganizationIdQuery(organizationId);
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.mustNot(organizationIdQueryBuilder);
        queryBuilder.must(identityQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }

    @Override
    public SearchHits searchByNameAndExcludeOrganizationAndProject(String prefix, Long organizationId, Long projectId)
            throws IOException {
        QueryBuilder projectIdQueryBuilder = buildProjectIdQuery(projectId);
        QueryBuilder organizationIdQueryBuilder = buildOrganizationIdQuery(organizationId);
        QueryBuilder invitedProjectIdQueryBuilder = buildInvitedProjectIdQuery(projectId);
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.mustNot(organizationIdQueryBuilder);
        queryBuilder.mustNot(projectIdQueryBuilder);
        queryBuilder.mustNot(invitedProjectIdQueryBuilder);
        queryBuilder.must(identityQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }

    private QueryBuilder buildProjectIdQuery(Long projectId) {
        return new TermQueryBuilder(ElasticsearchUserDTO.PROJECTS, projectId);
    }

    private QueryBuilder buildInvitedProjectIdQuery(Long projectId) {
        return new TermQueryBuilder(ElasticsearchUserDTO.INVITED_PROJECTS, projectId);
    }

    @Override
    public SearchHits searchByNameAndOrganization(String prefix, Long organizationId) throws IOException {
        QueryBuilder organizationIdQueryBuilder = buildOrganizationIdQueryWithInnerHits(organizationId);
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.filter(organizationIdQueryBuilder);
        queryBuilder.must(identityQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }

    @Override
    public SearchHits searchByNameAndOrganizationAndExcludeProject(String prefix, Long organizationId, Long projectId)
            throws IOException {
        QueryBuilder projectIdQueryBuilder = buildProjectIdQuery(projectId);
        QueryBuilder organizationIdQueryBuilder = buildOrganizationIdQueryWithInnerHits(organizationId);
        QueryBuilder invitedProjectIdQueryBuilder = buildInvitedProjectIdQuery(projectId);
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.mustNot(projectIdQueryBuilder);
        queryBuilder.mustNot(invitedProjectIdQueryBuilder);
        queryBuilder.filter(organizationIdQueryBuilder);
        queryBuilder.must(identityQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }

    @Override
    public SearchHits searchByNameAndOrganizationAndProject(String prefix, Long projectId)
            throws IOException {
        QueryBuilder organizationIdQueryBuilder = buildOrganizationIdQueryWithInnerHits(organizationId);
        QueryBuilder projectIdQueryBuilder = buildProjectIdQuery(projectId);
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.filter(organizationIdQueryBuilder);
        queryBuilder.filter(projectIdQueryBuilder);
        queryBuilder.must(identityQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }

    private QueryBuilder buildTagsQuery(String searchValue) {
        MatchQueryBuilder queryBuilder = new MatchQueryBuilder(ElasticsearchUserDTO.TAGS, searchValue);
        queryBuilder.zeroTermsQuery(ZeroTermsQuery.NONE);
        queryBuilder.operator(Operator.AND);
        return queryBuilder;
    }

    @Override
    public SearchHits searchByNameOrTags(String searchValue) throws IOException {
        QueryBuilder identityQueryBuilder = buildNameQuery(searchValue);
        QueryBuilder tagsQueryBuilder = buildTagsQuery(searchValue);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.should(identityQueryBuilder);
        queryBuilder.should(tagsQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }
}
