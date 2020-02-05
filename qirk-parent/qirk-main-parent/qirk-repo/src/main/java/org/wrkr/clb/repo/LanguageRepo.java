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
package org.wrkr.clb.repo;

import java.sql.Array;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.common.util.collections.ExtendedArrayUtils;
import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.LanguageMeta;
import org.wrkr.clb.model.project.ProjectToLanguageMeta;
import org.wrkr.clb.model.user.UserToLanguageMeta;
import org.wrkr.clb.repo.mapper.LanguageMapper;

@Repository
public class LanguageRepo extends JDBCBaseIdRepo {

    private static final String INSERT_BATCH_INTO_USER_LANGUAGE = "INSERT INTO " +
            UserToLanguageMeta.TABLE_NAME + " " +
            "(" + UserToLanguageMeta.userId + ", " + // 1
            UserToLanguageMeta.languageId + ") " + // 2
            "VALUES (unnest(?), unnest(?));";

    private static final LanguageMapper LANGUAGE_MAPPER = new LanguageMapper();

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            LANGUAGE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + LanguageMeta.TABLE_NAME + " " +
            "WHERE " + LanguageMeta.nameCode + " = ?;"; // 1

    private static final String SELECT_AND_ORDER_BY_NAME_CODE_ASC = "SELECT " +
            LANGUAGE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + LanguageMeta.TABLE_NAME + " " +
            "ORDER BY " + LanguageMeta.nameCode + " ASC;";

    private static final String SELECT_BY_IDS_PREFIX = "SELECT " +
            LANGUAGE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + LanguageMeta.TABLE_NAME + " " +
            "WHERE " + LanguageMeta.id + " IN ("; // 1

    private static final String SELECT_IDS_BY_USER_ID = "SELECT " +
            UserToLanguageMeta.languageId + " " +
            "FROM " + UserToLanguageMeta.TABLE_NAME + " " +
            "WHERE " + UserToLanguageMeta.userId + " = ?;"; // 1

    private static final String SELECT_BY_USER_ID = "SELECT " +
            LANGUAGE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + LanguageMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserToLanguageMeta.TABLE_NAME + " " +
            "ON " + LanguageMeta.id + " = " + UserToLanguageMeta.languageId + " " +
            "WHERE " + UserToLanguageMeta.userId + " = ?;"; // 1

    private static final String SELECT_BY_PROJECT_ID = "SELECT " +
            LANGUAGE_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + LanguageMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectToLanguageMeta.TABLE_NAME + " " +
            "ON " + LanguageMeta.id + " = " + ProjectToLanguageMeta.languageId + " " +
            "WHERE " + ProjectToLanguageMeta.projectId + " = ?;"; // 1

    private static final String DELETE_FROM_USER_LANGUAGE_BY_USER_ID_PREFIX = "DELETE FROM " +
            UserToLanguageMeta.TABLE_NAME + " " +
            "WHERE " + UserToLanguageMeta.userId + " = ?"; // 1

    private static final String DELETE_FROM_USER_LANGUAGE_BY_USER_ID = DELETE_FROM_USER_LANGUAGE_BY_USER_ID_PREFIX + ";";

    private static final String DELETE_FROM_USER_LANGUAGE_BY_USER_ID_AND_IDS_TO_RETAIN = "" +
            DELETE_FROM_USER_LANGUAGE_BY_USER_ID_PREFIX + " " +
            "AND NOT " + UserToLanguageMeta.languageId + " = ANY(?);"; // 2

    public Language getByNameCode(String nameCode) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, LANGUAGE_MAPPER, nameCode);
    }

    public List<Language> listAndOrderByNameCodeAsc() {
        return queryForList(SELECT_AND_ORDER_BY_NAME_CODE_ASC, LANGUAGE_MAPPER);
    }

    public List<Language> listByIds(Collection<Long> languageIds) {
        if (languageIds.isEmpty()) {
            return new ArrayList<Language>();
        }

        return queryForList(insertNBindValues(SELECT_BY_IDS_PREFIX, languageIds.size(), ");"),
                languageIds.toArray(),
                LANGUAGE_MAPPER);
    }

    public List<Long> listIdsByUserId(Long userId) {
        return queryForList(SELECT_IDS_BY_USER_ID, Long.class, userId);
    }

    public List<Language> listByUserId(Long userId) {
        return queryForList(SELECT_BY_USER_ID, LANGUAGE_MAPPER, userId);
    }

    public List<Language> listByProjectId(Long projectId) {
        return queryForList(SELECT_BY_PROJECT_ID, LANGUAGE_MAPPER, projectId);
    }

    public void setLanguagesToUser(Long userId, List<Language> languages) {
        if (!languages.isEmpty()) {
            Long[] languageIds = buildIdsArray(languages);
            Array languageIdArray = createArrayOf(JDBCType.BIGINT.getName(), languageIds);
            getJdbcTemplate().update(DELETE_FROM_USER_LANGUAGE_BY_USER_ID_AND_IDS_TO_RETAIN,
                    userId, languageIdArray);

            Set<Long> languageIdsToInsert = new HashSet<Long>(languageIds.length);
            Collections.addAll(languageIdsToInsert, languageIds);
            languageIdsToInsert.removeAll(listIdsByUserId(userId));

            Array userIdArray = createArrayOf(JDBCType.BIGINT.getName(),
                    ExtendedArrayUtils.buildArrayOfElement(userId, languageIdsToInsert.size()));
            Array languageIdArrayToInsert = createArrayOf(JDBCType.BIGINT.getName(), languageIdsToInsert.toArray(new Long[] {}));

            getJdbcTemplate().update(INSERT_BATCH_INTO_USER_LANGUAGE,
                    userIdArray, languageIdArrayToInsert);
        } else {
            getJdbcTemplate().update(DELETE_FROM_USER_LANGUAGE_BY_USER_ID, userId);
        }
    }
}
