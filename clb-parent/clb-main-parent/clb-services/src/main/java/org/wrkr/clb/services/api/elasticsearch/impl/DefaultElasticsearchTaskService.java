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
package org.wrkr.clb.services.api.elasticsearch.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.util.collections.MapBuilder;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.repo.context.TaskSearchContext;
import org.wrkr.clb.repo.sort.SortingOption;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchTaskService;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchTaskDTO;

import com.fasterxml.jackson.core.JsonProcessingException;


@Service
public class DefaultElasticsearchTaskService extends DefaultElasticsearchService<Task> implements ElasticsearchTaskService {

    public static final String INDEX = "task";

    private static final String[] DEFAULT_SEARCH_EXCLUDE_FIELDS = {
            ElasticsearchTaskDTO.DESCRIPTION, ElasticsearchTaskDTO.HIDDEN, ElasticsearchTaskDTO.HASHTAGS };

    @Override
    public String getIndex() {
        return INDEX;
    }

    @Override
    protected String convertEntityToJson(Task task, RequestType request)
            throws JsonProcessingException {
        return ElasticsearchTaskDTO.fromEntity(task, request).toJson();
    }

    @Override
    public void updateForJira(Task task) throws IOException {
        if (exists(task)) {
            update(task, RequestType.JIRA_UPDATE);
        } else {
            index(task);
        }
    }

    @Override
    public void updateCardAndHidden(Task task) throws IOException {
        UpdateRequest request = new UpdateRequest(getIndex(), task.getId().toString());

        Map<String, Object> source = new MapBuilder<String, Object>()
                .put(ElasticsearchTaskDTO.CARD, ElasticsearchTaskDTO.getTaskCardId(task))
                .put(ElasticsearchTaskDTO.HIDDEN, task.isHidden())
                .build();
        request.doc(source, XContentType.JSON);

        client.update(request, RequestOptions.DEFAULT);
    }

    private QueryBuilder buildProjectQuery(Long projectId) {
        return new TermQueryBuilder(ElasticsearchTaskDTO.PROJECT_ID, projectId);
    }

    private QueryBuilder buildCardsQuery(List<Long> cards) {
        return new TermsQueryBuilder(ElasticsearchTaskDTO.CARD, cards);
    }

    @SuppressWarnings("unused")
    private QueryBuilder buildNonHiddenQuery() {
        return new TermQueryBuilder(ElasticsearchTaskDTO.HIDDEN, false);
    }

    private QueryBuilder buildReporterQuery(Long reporterId) {
        return new TermQueryBuilder(ElasticsearchTaskDTO.REPORTER, reporterId);
    }

    private QueryBuilder buildExistsAssigneeQuery() {
        return new ExistsQueryBuilder(ElasticsearchTaskDTO.ASSIGNEE);
    }

    private QueryBuilder buildAssigneeQuery(Long assigneeId) {
        return new TermQueryBuilder(ElasticsearchTaskDTO.ASSIGNEE, assigneeId);
    }

    private QueryBuilder buildTypeQuery(List<String> types) {
        return new TermsQueryBuilder(ElasticsearchTaskDTO.TASK_TYPE, types.toArray(new String[0]));
    }

    private QueryBuilder buildPriorityQuery(List<String> priorities) {
        return new TermsQueryBuilder(ElasticsearchTaskDTO.TASK_PRIORITY, priorities.toArray(new String[0]));
    }

    private QueryBuilder buildStatusQuery(List<String> statuses) {
        return new TermsQueryBuilder(ElasticsearchTaskDTO.TASK_STATUS, statuses.toArray(new String[0]));
    }

    private QueryBuilder buildNumberQuery(String numberPrefix) {
        return new PrefixQueryBuilder(ElasticsearchTaskDTO.NUMBER_STRING, numberPrefix);
    }

    private QueryBuilder buildTextQuery(String text) {
        return new MatchPhraseQueryBuilder(ElasticsearchTaskDTO.DESCRIPTION, text);
    }

    private QueryBuilder buildHashtagQuery(String hashtag) {
        return new TermQueryBuilder(ElasticsearchTaskDTO.HASHTAGS, hashtag);
    }

    private FieldSortBuilder buildSearchSort(SortingOption.ForTask sortBy) {
        switch (sortBy) {
            case CREATED_AT:
                return new FieldSortBuilder(ElasticsearchTaskDTO.CREATED_AT);
            case UPDATED_AT:
            default:
                return new FieldSortBuilder(ElasticsearchTaskDTO.UPDATED_AT);

        }
    }

    @Override
    public SearchHits search(Long projectId, TaskSearchContext searchContext, int size) throws IOException {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        queryBuilder.filter(buildProjectQuery(projectId));
        queryBuilder.mustNot(buildCardsQuery(searchContext.excludeCards));

        if (searchContext.reporterId != null) {
            queryBuilder.filter(buildReporterQuery(searchContext.reporterId));
        }
        if (searchContext.assigneeId != null) {
            if (TaskSearchContext.UNASSIGNED_ID.equals(searchContext.assigneeId)) {
                queryBuilder.mustNot(buildExistsAssigneeQuery());
            } else {
                queryBuilder.filter(buildAssigneeQuery(searchContext.assigneeId));
            }
        }
        if (!searchContext.types.isEmpty()) {
            queryBuilder.filter(buildTypeQuery(searchContext.types));
        }
        if (!searchContext.priorities.isEmpty()) {
            queryBuilder.filter(buildPriorityQuery(searchContext.priorities));
        }
        if (!searchContext.statuses.isEmpty()) {
            queryBuilder.filter(buildStatusQuery(searchContext.statuses));
        }

        if (!searchContext.text.isBlank()) {
            if (StringUtils.isNumeric(searchContext.text)) {
                queryBuilder.must(buildNumberQuery(searchContext.text));
            } else {
                queryBuilder.must(buildTextQuery(searchContext.text));
            }
        }
        if (!searchContext.hashtag.isBlank()) {
            queryBuilder.must(buildHashtagQuery(searchContext.hashtag));
        }

        FieldSortBuilder sortBuilder = buildSearchSort(searchContext.sortBy);
        switch (searchContext.sortOrder) {
            case ASC:
                sortBuilder.order(SortOrder.ASC);
                break;
            case DESC:
                sortBuilder.order(SortOrder.DESC);
                break;
        }

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS,
                size, (searchContext.searchAfter == null ? null : new Object[] { searchContext.searchAfter }), sortBuilder);
    }
}
