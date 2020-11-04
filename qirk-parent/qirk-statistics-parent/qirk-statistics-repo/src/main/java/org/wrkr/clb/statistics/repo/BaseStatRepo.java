package org.wrkr.clb.statistics.repo;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wrkr.clb.common.jdbc.BaseRepo;

public class BaseStatRepo extends BaseRepo {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    @Autowired
    @Qualifier("statDataSource")
    public void setDataSource(DataSource dataSource) {
        setJdbcTemplate(new JdbcTemplate(dataSource));
    }
}
