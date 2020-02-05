/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
