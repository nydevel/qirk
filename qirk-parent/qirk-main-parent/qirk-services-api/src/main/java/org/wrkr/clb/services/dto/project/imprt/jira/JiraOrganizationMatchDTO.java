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
package org.wrkr.clb.services.dto.project.imprt.jira;

import java.util.List;

import org.wrkr.clb.services.dto.project.imprt.QirkOrganizationDTO;

public class JiraOrganizationMatchDTO {

    public List<JiraProjectDTO> projects;
    public List<JiraUserDTO> users;
    public List<JiraIdAndNameDTO> types;
    public List<JiraIdAndNameDTO> priorities;
    public List<JiraIdAndNameDTO> statuses;

    public QirkOrganizationDTO qirk;

    public JiraOrganizationMatchDTO(List<JiraProjectDTO> projects, List<JiraUserDTO> users,
            List<JiraIdAndNameDTO> types, List<JiraIdAndNameDTO> priorities, List<JiraIdAndNameDTO> statuses,
            QirkOrganizationDTO qirk) {
        this.projects = projects;
        this.users = users;
        this.types = types;
        this.priorities = priorities;
        this.statuses = statuses;
        this.qirk = qirk;
    }
}
