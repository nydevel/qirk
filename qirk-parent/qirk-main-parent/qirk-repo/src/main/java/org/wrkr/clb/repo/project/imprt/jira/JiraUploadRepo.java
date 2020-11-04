package org.wrkr.clb.repo.project.imprt.jira;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.project.imprt.jira.JiraUpload;
import org.wrkr.clb.model.project.imprt.jira.JiraUploadMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class JiraUploadRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + JiraUploadMeta.TABLE_NAME + " " +
            JiraUploadMeta.uploadTimestamp + ", " + // 1
            JiraUploadMeta.archiveFilename + ") " + // 2
            "VALUES (?, ?);";

    private static final String SELECT_ALL = "SELECT " +
            JiraUploadMeta.uploadTimestamp + ", " +
            JiraUploadMeta.archiveFilename + " " +
            "FROM " + JiraUploadMeta.TABLE_NAME + ";";

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void save(JiraUpload upload) {
        getJdbcTemplate().update(INSERT,
                upload.getUploadTimestamp(), upload.getArchiveFilename());
    }

    public Map<Long, String> mapTimestampToArchiveFilename() {
        List<Map<String, Object>> results = getJdbcTemplate().queryForList(SELECT_ALL);

        Map<Long, String> map = new HashMap<Long, String>();
        for (Map<String, Object> result : results) {
            map.put((Long) result.get(JiraUploadMeta.uploadTimestamp), (String) result.get(JiraUploadMeta.archiveFilename));
        }

        return map;
    }
}
