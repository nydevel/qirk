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
package org.wrkr.clb.services.dto.project;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IssueReadDTO extends IdDTO {

    public String summary;

    public String description;

    @JsonProperty(value = "user") // legacy
    public PublicUserDTO reporter;

    @JsonProperty(value = "created_at")
    public String createdAt;

    public static IssueReadDTO fromEntity(Issue issue) {
        IssueReadDTO dto = new IssueReadDTO();

        dto.id = issue.getId();
        dto.summary = issue.getSummary();
        dto.description = issue.getDescription();
        dto.reporter = PublicUserDTO.fromEntity(issue.getReporter());
        dto.createdAt = issue.getCreatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);

        return dto;
    }

    public static List<IssueReadDTO> fromEntities(List<Issue> issueList) {
        List<IssueReadDTO> dtoList = new ArrayList<IssueReadDTO>(issueList.size());
        for (Issue issue : issueList) {
            dtoList.add(fromEntity(issue));
        }
        return dtoList;
    }
}
