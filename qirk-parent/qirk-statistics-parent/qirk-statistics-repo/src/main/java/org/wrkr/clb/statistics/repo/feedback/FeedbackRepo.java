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
package org.wrkr.clb.statistics.repo.feedback;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.feedback.Feedback;
import org.wrkr.clb.statistics.repo.model.feedback.Feedback_;


@Repository
public class FeedbackRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + Feedback_.TABLE_NAME + " " +
            "(" + Feedback_.senderUserId + ", " + // 1
            Feedback_.senderUserEmail + ", " + // 2
            Feedback_.feedback + ", " + // 3
            Feedback_.createdAt + ") " +
            "VALUES (?, ?, ?, NOW());";

    private static final FeedbackMapper FEEDBACK_MAPPER = new FeedbackMapper();

    private static final String SELECT_AND_ORDER_DESC_BY_CREATED_AT = "SELECT " +
            FEEDBACK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + Feedback_.TABLE_NAME + " " +
            "ORDER BY " + Feedback_.createdAt + " DESC;";

    public void save(Long senderUserId, String senderUserEmail,
            @NotNull(message = "feedback in FeedbackRepo must not be null") String feedback) {
        getJdbcTemplate().update(INSERT, senderUserId, senderUserEmail, feedback);
    }

    public List<Feedback> listAndOrderDescByCreatedAt() {
        return queryForList(SELECT_AND_ORDER_DESC_BY_CREATED_AT, FEEDBACK_MAPPER);
    }
}
