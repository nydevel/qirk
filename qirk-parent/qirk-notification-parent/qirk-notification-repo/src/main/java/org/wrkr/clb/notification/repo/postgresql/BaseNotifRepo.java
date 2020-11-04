package org.wrkr.clb.notification.repo.postgresql;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wrkr.clb.common.jdbc.BaseRepo;

public abstract class BaseNotifRepo extends BaseRepo {

    @Override
    @Autowired
    @Qualifier("notifDataSource")
    public void setDataSource(DataSource dataSource) {
        setJdbcTemplate(new JdbcTemplate(dataSource));
    }
}
