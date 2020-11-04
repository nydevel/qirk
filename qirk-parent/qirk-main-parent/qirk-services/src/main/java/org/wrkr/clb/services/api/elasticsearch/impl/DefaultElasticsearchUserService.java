package org.wrkr.clb.services.api.elasticsearch.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchNestedProjectDTO;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchUserDTO;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class DefaultElasticsearchUserService extends DefaultElasticsearchService<User> implements ElasticsearchUserService {

    public static final String INDEX = "user";

    private static class Scripts {
        private static final String ADD_PROJECT = "user_add_project";
        private static final String REMOVE_PROJECT = "user_remove_project";
    }

    private static final String[] DEFAULT_SEARCH_EXCLUDE_FIELDS = {
            ElasticsearchUserDTO.NAME_SEARCH_FIELD,
            ElasticsearchUserDTO.PROJECTS + ".*", ElasticsearchUserDTO.INVITED_PROJECTS };

    @Override
    public String getIndex() {
        return INDEX;
    }

    @Override
    protected String convertEntityToJson(User user, RequestType request) throws JsonProcessingException {
        ElasticsearchUserDTO dto = ElasticsearchUserDTO.fromEntity(user);
        if (RequestType.INDEX.equals(request)) {
            dto.projects = new ArrayList<Map<String, Object>>();
            dto.invitedProjects = new ArrayList<Long>();
        }
        return dto.toJson();
    }

    @Override
    public void setProjects(User user) throws IOException {
        UpdateRequest request = new UpdateRequest(getIndex(), user.getId().toString());

        Map<String, Object> source = new HashMap<String, Object>(1);
        source.put(ElasticsearchUserDTO.PROJECTS, ElasticsearchNestedProjectDTO.fromEntities(user.getProjectMembership()));
        request.doc(source, XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        logResponse(response);
    }

    @Override
    public void addProject(Long userId, ProjectMember member) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.STORED, null, Scripts.ADD_PROJECT,
                        ElasticsearchNestedProjectDTO.fromEntity(member)));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    private Map<String, Object> getRemoveProjectParams(ProjectMember member) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(ElasticsearchNestedProjectDTO.MEMBER_ID, member.getId());
        return map;
    }

    @Override
    public void removeProject(Long userId, ProjectMember member) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.STORED, null, Scripts.REMOVE_PROJECT,
                        getRemoveProjectParams(member)));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    private Map<String, Object> getInvitedProjectParams(List<Long> projectIds) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(ElasticsearchUserDTO.INVITED_PROJECTS, projectIds);
        return map;
    }

    @Override
    public void setInvitedProjects(Long userId, List<Long> projectIds) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.INLINE, "painless",
                        getAddElementsScript(ElasticsearchUserDTO.INVITED_PROJECTS),
                        getInvitedProjectParams(projectIds)));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    @Override
    public void addInvitedProject(Long userId, Long projectId) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.INLINE, "painless",
                        getAddElementsScript(ElasticsearchUserDTO.INVITED_PROJECTS),
                        getInvitedProjectParams(Arrays.asList(projectId))));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    @Override
    public void removeInvitedProject(Long userId, Long projectId) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), userId.toString());
        updateRequest.script(
                new Script(ScriptType.INLINE, "painless",
                        getRemoveElementsScript(ElasticsearchUserDTO.INVITED_PROJECTS),
                        getInvitedProjectParams(Arrays.asList(projectId))));

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        logResponse(updateResponse);
    }

    private QueryBuilder buildNameQuery(String prefix) {
        if (prefix.isEmpty()) {
            return new MatchAllQueryBuilder();
        }

        return new MatchPhrasePrefixQueryBuilder(ElasticsearchUserDTO.NAME_SEARCH_FIELD, prefix);
    }

    private NestedQueryBuilder buildProjectIdQuery(Long projectId) {
        TermQueryBuilder nestedQuery = new TermQueryBuilder(
                ElasticsearchUserDTO.PROJECTS + "." + ElasticsearchNestedProjectDTO.PROJECT_ID, projectId);
        return new NestedQueryBuilder(ElasticsearchUserDTO.PROJECTS, nestedQuery, ScoreMode.None);
    }

    private QueryBuilder buildProjectIdQueryWithInnerHits(Long projectId) {
        NestedQueryBuilder query = buildProjectIdQuery(projectId);

        InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
        innerHitBuilder.setSize(1);
        innerHitBuilder.setTrackScores(false);
        query.innerHit(innerHitBuilder);

        return query;
    }

    private QueryBuilder buildInvitedProjectIdQuery(Long projectId) {
        return new TermQueryBuilder(ElasticsearchUserDTO.INVITED_PROJECTS, projectId);
    }

    @Override
    public SearchHits searchByName(String prefix) throws IOException {
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);
        return search(identityQueryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }

    @Override
    public SearchHits searchByNameAndExcludeProject(String prefix, Long projectId) throws IOException {
        QueryBuilder projectIdQueryBuilder = buildProjectIdQuery(projectId);
        QueryBuilder invitedProjectIdQueryBuilder = buildInvitedProjectIdQuery(projectId);
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.mustNot(projectIdQueryBuilder);
        queryBuilder.mustNot(invitedProjectIdQueryBuilder);
        queryBuilder.must(identityQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }

    @Override
    public SearchHits searchByNameAndProject(String prefix, Long projectId) throws IOException {
        QueryBuilder projectIdQueryBuilder = buildProjectIdQueryWithInnerHits(projectId);
        QueryBuilder identityQueryBuilder = buildNameQuery(prefix);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.filter(projectIdQueryBuilder);
        queryBuilder.must(identityQueryBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }
}
