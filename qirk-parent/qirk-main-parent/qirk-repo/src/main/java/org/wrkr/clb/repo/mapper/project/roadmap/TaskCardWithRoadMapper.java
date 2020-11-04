package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.roadmap.TaskCard;

public class TaskCardWithRoadMapper extends TaskCardMapper {

    private RoadMapper roadMapper;

    public TaskCardWithRoadMapper(String cardTableName, String roadTableName) {
        super(cardTableName);
        roadMapper = new RoadMapper(roadTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                roadMapper.generateSelectColumnsStatement();
    }

    @Override
    public TaskCard mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskCard card = super.mapRow(rs, rowNum);
        card.setRoad(roadMapper.mapRow(rs, rowNum));
        return card;
    }
}
