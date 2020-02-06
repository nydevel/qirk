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
package org.wrkr.clb.repo.project.imprt.jira;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.project.imprt.jira.JiraUpload;
import org.wrkr.clb.model.project.imprt.jira.JiraUploadMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class JiraUploadRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + JiraUploadMeta.TABLE_NAME + " " +
            JiraUploadMeta.uploadTimestamp + ", " + // 1
            JiraUploadMeta.archiveFilename + ") " + // 2
            "VALUES (?, ?);";

    private static final String SELECT_ALL = "SELECT " +
            JiraUploadMeta.uploadTimestamp + ", " +
            JiraUploadMeta.archiveFilename + " " +
            "FROM " + JiraUploadMeta.TABLE_NAME + ";";

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void save(JiraUpload upload) {
        getJdbcTemplate().update(INSERT,
                upload.getUploadTimestamp(), upload.getArchiveFilename());
    }

    public Map<Long, String> mapTimestampToArchiveFilenameByOrganizationId() {
        List<Map<String, Object>> results = getJdbcTemplate().queryForList(SELECT_ALL);

        Map<Long, String> map = new HashMap<Long, String>();
        for (Map<String, Object> result : results) {
            map.put((Long) result.get(JiraUploadMeta.uploadTimestamp), (String) result.get(JiraUploadMeta.archiveFilename));
        }

        return map;
    }
}
