package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.MemoMeta;
import org.wrkr.clb.model.project.ProjectMeta;

public class SecurityMemoWithProjectMapper extends BaseMapper<Memo> {

    protected SecurityProjectMapper projectMapper;

    protected SecurityMemoWithProjectMapper(String memoTableName) {
        super(memoTableName);
    }

    public SecurityMemoWithProjectMapper(String memoTableName, ProjectMeta projectMeta) {
        super(memoTableName);
        this.projectMapper = new SecurityProjectMapper(projectMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(MemoMeta.id) + ", " +
                generateSelectColumnStatement(MemoMeta.projectId) + ", " +
                generateSelectColumnStatement(MemoMeta.authorId) + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public Memo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Memo memo = new Memo();

        memo.setId(rs.getLong(generateColumnAlias(MemoMeta.id)));
        memo.setProjectId(rs.getLong(generateColumnAlias(MemoMeta.projectId)));
        memo.setAuthorId(rs.getLong(generateColumnAlias(MemoMeta.authorId)));

        memo.setProject(projectMapper.mapRow(rs, rowNum));

        return memo;
    }
}
