package org.wrkr.clb.statistics.repo.project;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.project.ProjectDocUpdate_;

@Repository
public class ProjectDocUpdateRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + ProjectDocUpdate_.TABLE_NAME + " " +
            "(" + ProjectDocUpdate_.updatedByUserId + ", " + // 1
            ProjectDocUpdate_.updatedAt + ") " +
            "VALUES (?, NOW());";

    public void save(@NotNull(message = "updatedByUserId in ProjectDocUpdateRepo must not be null") Long updatedByUserId) {
        getJdbcTemplate().update(INSERT, updatedByUserId);
    }
}
