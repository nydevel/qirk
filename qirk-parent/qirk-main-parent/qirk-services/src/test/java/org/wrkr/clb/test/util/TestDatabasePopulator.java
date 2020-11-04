package org.wrkr.clb.test.util;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class TestDatabasePopulator {

    public TestDatabasePopulator(ResourceDatabasePopulator populator, DataSource dataSource) {
        populator.execute(dataSource);
    }
}
