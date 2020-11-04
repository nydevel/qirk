package org.wrkr.clb.common.jdbc.transaction;

public interface Executor {

    public <T> T exec(int retryNumber) throws Exception;
}
