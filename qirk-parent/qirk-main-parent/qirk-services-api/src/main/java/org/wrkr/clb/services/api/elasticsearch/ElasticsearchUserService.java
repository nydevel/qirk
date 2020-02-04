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
package org.wrkr.clb.services.api.elasticsearch;

import java.util.List;

import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.model.user.User;

public interface ElasticsearchUserService extends ElasticsearchService<User> {

    public void setProjects(User user, List<Long> projectIds) throws Exception;

    public void addProject(Long userId, Long projectId) throws Exception;

    public void removeProject(Long userId, Long projectId) throws Exception;

    public void removeProjects(Long userId, List<Long> projectIds) throws Exception;

    public void addInvitedProject(Long userId, Long projectId) throws Exception;

    public void removeInvitedProject(Long userId, Long projectId) throws Exception;

    public SearchHits searchByName(String prefix) throws Exception;

    public SearchHits searchByNameAndExcludeOrganization(String prefix, Long organizationId) throws Exception;

    public SearchHits searchByNameAndExcludeOrganizationAndProject(String prefix, Long organizationId, Long projectId)
            throws Exception;

    public SearchHits searchByNameAndOrganization(String prefix, Long organizationId) throws Exception;

    public SearchHits searchByNameAndOrganizationAndExcludeProject(String prefix, Long organizationId, Long projectId)
            throws Exception;

    public SearchHits searchByNameAndOrganizationAndProject(String prefix, Long organizationId, Long projectId) throws Exception;

    public SearchHits searchByNameOrTags(String searchValue) throws Exception;
}
