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

import org.wrkr.clb.model.project.Road;
import org.wrkr.clb.model.project.RoadMeta;
import org.wrkr.clb.repo.mapper.RecordVersionMapper;

public class RoadMapper extends RecordVersionMapper<Road> {

    public RoadMapper() {
        super();
    }

    public RoadMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(RoadMeta.name) + ", " +
                generateSelectColumnStatement(RoadMeta.previousId) + ", " +
                generateSelectColumnStatement(RoadMeta.deleted);
    }

    @Override
    public Road mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Road road = new Road();

        road.setId(rs.getLong(generateColumnAlias(RoadMeta.id)));
        road.setRecordVersion(rs.getLong(generateColumnAlias(RoadMeta.recordVersion)));
        road.setName(rs.getString(generateColumnAlias(RoadMeta.name)));
        road.setPreviousId((Long) rs.getObject(generateColumnAlias(RoadMeta.previousId))); // nullable
        road.setDeleted(rs.getBoolean(generateColumnAlias(RoadMeta.deleted)));

        return road;
    }
}
