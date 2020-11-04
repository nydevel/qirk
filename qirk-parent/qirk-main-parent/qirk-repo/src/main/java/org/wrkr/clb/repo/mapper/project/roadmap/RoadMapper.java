package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.roadmap.Road;
import org.wrkr.clb.model.project.roadmap.RoadMeta;
import org.wrkr.clb.repo.mapper.VersionedEntityMapper;

public class RoadMapper extends VersionedEntityMapper<Road> {

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
