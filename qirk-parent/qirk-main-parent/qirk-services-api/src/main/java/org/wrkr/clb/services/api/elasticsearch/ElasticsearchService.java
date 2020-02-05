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
package org.wrkr.clb.services.api.elasticsearch;

import java.util.List;
import java.util.Set;

import org.elasticsearch.action.get.MultiGetItemResponse;
import org.wrkr.clb.model.BaseIdEntity;

public interface ElasticsearchService<E extends BaseIdEntity> {

    public static enum RequestType {
        INDEX("INDEX"),
        UPDATE("UPDATE"),
        DATASYNC_UPDATE("DATASYNC_UPDATE"),
        JIRA_UPDATE("JIRA_UPDATE");

        @SuppressWarnings("unused")
        private final String type;

        RequestType(final String type) {
            this.type = type;
        }
    }

    public void index(E entity);

    public boolean exists(E entity) throws Exception;

    public void updateOrIndex(E entity) throws Exception;

    public void datasync(E entity) throws Exception;

    public MultiGetItemResponse[] multiGet(List<Long> ids) throws Exception;

    public MultiGetItemResponse[] multiGet(List<Long> ids, String[] includeFields) throws Exception;

    public Set<String> getIds() throws Exception;

    public void delete(String id) throws Exception;
}
