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
package org.wrkr.clb.repo;

import java.sql.Array;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.common.util.collections.ExtendedArrayUtils;
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.TagMeta;
import org.wrkr.clb.model.project.ProjectToTagMeta;
import org.wrkr.clb.model.user.UserToTagMeta;
import org.wrkr.clb.repo.mapper.TagMapper;

@Repository
public class TagRepo extends JDBCBaseIdRepo {

    private static final String INSERT = "INSERT INTO " + TagMeta.TABLE_NAME + " " +
            "(" + TagMeta.name + ") " + // 1
            "VALUES (?);";

    private static final String INSERT_BATCH = "INSERT INTO " +
            TagMeta.TABLE_NAME + " " +
            "(" + TagMeta.name + ") " + // 2
            "VALUES (unnest(?)) " +
            "RETURNING " + TagMeta.id + ", " +
            TagMeta.name + ";";

    private static final String INSERT_BATCH_INTO_USER_TAG = "INSERT INTO " +
            UserToTagMeta.TABLE_NAME + " " +
            "(" + UserToTagMeta.userId + ", " + // 1
            UserToTagMeta.tagId + ") " + // 2
            "VALUES (unnest(?), unnest(?));";

    private static final TagMapper TAG_MAPPER = new TagMapper();

    private static final String SELECT_BY_NAME = "SELECT " +
            TAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TagMeta.TABLE_NAME + " " +
            "WHERE " + TagMeta.name + " = ?;"; // 1

    private static final String SELECT_IDS_BY_USER_ID = "SELECT " +
            UserToTagMeta.tagId + " " +
            "FROM " + UserToTagMeta.TABLE_NAME + " " +
            "WHERE " + UserToTagMeta.userId + " = ?;"; // 1

    private static final String SELECT_BY_USER_ID = "SELECT " +
            TAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TagMeta.TABLE_NAME + " " +
            "INNER JOIN " + UserToTagMeta.TABLE_NAME + " " +
            "ON " + TagMeta.id + " = " + UserToTagMeta.tagId + " " +
            "WHERE " + UserToTagMeta.userId + " = ?;"; // 1

    private static final String SELECT_BY_PROJECT_ID = "SELECT " +
            TAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TagMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectToTagMeta.TABLE_NAME + " " +
            "ON " + TagMeta.id + " = " + ProjectToTagMeta.tagId + " " +
            "WHERE " + ProjectToTagMeta.projectId + " = ?;"; // 1

    private static final String DELETE_FROM_USER_TAG_BY_USER_ID_PREFIX = "DELETE FROM " +
            UserToTagMeta.TABLE_NAME + " " +
            "WHERE " + UserToTagMeta.userId + " = ?"; // 1

    private static final String DELETE_FROM_USER_TAG_BY_USER_ID = DELETE_FROM_USER_TAG_BY_USER_ID_PREFIX + ";";

    private static final String DELETE_FROM_USER_TAG_BY_USER_ID_AND_IDS_TO_RETAIN = "" +
            DELETE_FROM_USER_TAG_BY_USER_ID_PREFIX + " " +
            "AND NOT " + UserToTagMeta.tagId + " = ANY(?);"; // 2

    public Tag save(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, new String[] { TagMeta.id });
            ps.setString(1, tag.getName());
            return ps;
        }, keyHolder);

        return setIdAfterSave(tag, keyHolder);
    }

    public List<Tag> saveBatchByNames(List<String> tagNames) {
        Array tagNamesArray = createArrayOf(JDBCType.VARCHAR.getName(), tagNames.toArray(new String[] {}));

        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_BATCH,
                    new String[] { TagMeta.id, TagMeta.name });
            ps.setArray(1, tagNamesArray);
            return ps;
        }, keyHolder);

        List<Tag> tags = new ArrayList<Tag>();
        for (Map<String, Object> map : keyHolder.getKeyList()) {
            tags.add(Tag.fromMap(map));
        }
        return tags;
    }

    public Tag getByName(String name) {
        return queryForObjectOrNull(SELECT_BY_NAME, TAG_MAPPER, name);
    }

    public List<Long> listIdsByUserId(Long userId) {
        return queryForList(SELECT_IDS_BY_USER_ID, Long.class, userId);
    }

    public List<Tag> listByUserId(Long userId) {
        return queryForList(SELECT_BY_USER_ID, TAG_MAPPER, userId);
    }

    public List<Tag> listByProjectId(Long projectId) {
        return queryForList(SELECT_BY_PROJECT_ID, TAG_MAPPER, projectId);
    }

    public void setTagsToUser(Long userId, List<Tag> tags) {
        if (!tags.isEmpty()) {
            Long[] tagIds = buildIdsArray(tags);
            Array tagIdArray = createArrayOf(JDBCType.BIGINT.getName(), tagIds);
            getJdbcTemplate().update(DELETE_FROM_USER_TAG_BY_USER_ID_AND_IDS_TO_RETAIN,
                    userId, tagIdArray);

            Set<Long> tagIdsToInsert = new HashSet<Long>(tagIds.length);
            Collections.addAll(tagIdsToInsert, tagIds);
            tagIdsToInsert.removeAll(listIdsByUserId(userId));

            Array userIdArray = createArrayOf(JDBCType.BIGINT.getName(),
                    ExtendedArrayUtils.buildArrayOfElement(userId, tagIdsToInsert.size()));
            Array tagIdArrayToInsert = createArrayOf(JDBCType.BIGINT.getName(), tagIdsToInsert.toArray(new Long[0]));

            getJdbcTemplate().update(INSERT_BATCH_INTO_USER_TAG,
                    userIdArray, tagIdArrayToInsert);
        } else {
            getJdbcTemplate().update(DELETE_FROM_USER_TAG_BY_USER_ID, userId);
        }
    }
}
