package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.roadmap.Road;
import org.wrkr.clb.model.project.roadmap.RoadMeta;

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
