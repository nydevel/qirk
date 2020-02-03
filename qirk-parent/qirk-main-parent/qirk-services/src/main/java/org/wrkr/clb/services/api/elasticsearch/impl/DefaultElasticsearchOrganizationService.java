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

import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchOrganizationService;
import org.wrkr.clb.services.dto.elasticsearch.ElasticsearchOrganizationDTO;

import com.fasterxml.jackson.core.JsonProcessingException;


// @Service
@Deprecated
public class DefaultElasticsearchOrganizationService extends DefaultElasticsearchService<Organization>
        implements ElasticsearchOrganizationService {

    public static final String INDEX = "organization";

    private static final String[] DEFAULT_SEARCH_EXCLUDE_FIELDS = { ElasticsearchOrganizationDTO.NAME_SEARCH_FIELD };

    @Override
    public String getIndex() {
        return INDEX;
    }

    @Override
    protected String convertEntityToJson(Organization organization, @SuppressWarnings("unused") RequestType request)
            throws JsonProcessingException {
        return ElasticsearchOrganizationDTO.fromEntity(organization).toJson();
    }

    private QueryBuilder buildPublicQuery() {
        return new TermQueryBuilder(ElasticsearchOrganizationDTO.PRIVATE, false);
    }

    @Deprecated
    @Override
    public SearchHits searchByName(String prefix) throws Exception {
        MatchPhrasePrefixQueryBuilder searchQueryBuilder = new MatchPhrasePrefixQueryBuilder(
                ElasticsearchOrganizationDTO.NAME_SEARCH_FIELD, prefix);
        QueryBuilder publicFilterBuilder = buildPublicQuery();

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(searchQueryBuilder);
        queryBuilder.filter(publicFilterBuilder);

        return search(queryBuilder, Strings.EMPTY_ARRAY, DEFAULT_SEARCH_EXCLUDE_FIELDS);
    }
}
