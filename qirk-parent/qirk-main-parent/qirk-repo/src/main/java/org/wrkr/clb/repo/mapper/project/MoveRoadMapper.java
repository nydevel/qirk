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
package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.Road;
import org.wrkr.clb.model.project.RoadMeta;

public class MoveRoadMapper extends BaseMapper<Road> {

    protected String nextTableName = "";

    public MoveRoadMapper(String tableName, String nextTableName) {
        super(tableName);
        this.nextTableName = nextTableName;
    }

    public String generateNextColumnAlias(String columnLabel) {
        return nextTableName + "__" + columnLabel;
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(RoadMeta.id) + ", " +
                generateSelectColumnStatement(RoadMeta.projectId) + ", " +
                generateSelectColumnStatement(RoadMeta.previousId) + ", " +
                nextTableName + "." + RoadMeta.id + " AS " + generateNextColumnAlias(RoadMeta.id);
    }

    @Override
    public Road mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Road road = new Road();

        road.setId(rs.getLong(generateColumnAlias(RoadMeta.id)));
        road.setProjectId(rs.getLong(generateColumnAlias(RoadMeta.projectId)));
        road.setPreviousId((Long) rs.getObject(generateColumnAlias(RoadMeta.previousId))); // nullable
        road.setNextId((Long) rs.getObject(generateNextColumnAlias(RoadMeta.id))); // nullable

        return road;
    }
}
