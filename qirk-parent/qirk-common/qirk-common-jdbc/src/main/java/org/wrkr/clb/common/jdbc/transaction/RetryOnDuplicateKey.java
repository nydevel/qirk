package org.wrkr.clb.common.jdbc.transaction;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DuplicateKeyException;

public class RetryOnDuplicateKey {

    public static final int DEFAULT_RETRIES_COUNT = 3;

    public static <T> T exec(Executor executor, int retriesCount) throws Exception {
        int retryNumber = 0;
        Exception caughtException;
        do {
            try {
                return executor.exec(retryNumber);
            } catch (DuplicateKeyException | CannotAcquireLockException e) {
                retryNumber++;
                caughtException = e;
            }
        } while (retryNumber < retriesCount);
        throw caughtException;
    }

    public static <T> T exec(Executor executor) throws Exception {
        return exec(executor, DEFAULT_RETRIES_COUNT);
    }
}
