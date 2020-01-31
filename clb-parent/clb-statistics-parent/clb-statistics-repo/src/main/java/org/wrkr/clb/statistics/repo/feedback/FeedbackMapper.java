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
package org.wrkr.clb.statistics.repo.feedback;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.statistics.repo.model.feedback.Feedback_;
import org.wrkr.clb.statistics.repo.model.feedback.Feedback;

public class FeedbackMapper extends BaseMapper<Feedback> {

    public FeedbackMapper() {
        super();
    }

    public FeedbackMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(Feedback_.senderUserId) + ", " +
                generateSelectColumnStatement(Feedback_.senderUserEmail) + ", " +
                generateSelectColumnStatement(Feedback_.feedback) + ", " +
                generateSelectColumnStatement(Feedback_.createdAt);
    }

    @Override
    public Feedback mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Feedback feedback = new Feedback();

        feedback.senderId = rs.getLong(generateColumnAlias(Feedback_.senderUserId));
        feedback.senderEmail = rs.getString(generateColumnAlias(Feedback_.senderUserEmail));
        feedback.feedback = rs.getString(generateColumnAlias(Feedback_.feedback));
        feedback.createdAt = getOffsetDateTime(rs, generateColumnAlias(Feedback_.createdAt))
                .format(DateTimeUtils.WEB_DATETIME_FORMATTER);

        return feedback;
    }
}
