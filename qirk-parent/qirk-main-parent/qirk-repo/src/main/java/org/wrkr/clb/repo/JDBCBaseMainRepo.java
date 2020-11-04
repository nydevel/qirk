package org.wrkr.clb.repo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.common.jdbc.BaseRepo;


@Repository
public abstract class JDBCBaseMainRepo extends BaseRepo {

    @Override
    @Autowired
    @Qualifier("dataSource")
    public void setDataSource(DataSource dataSource) {
        setJdbcTemplate(new JdbcTemplate(dataSource));
    }
}
