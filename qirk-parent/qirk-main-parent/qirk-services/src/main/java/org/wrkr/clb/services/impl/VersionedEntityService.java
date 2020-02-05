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
package org.wrkr.clb.services.impl;

import org.wrkr.clb.model.VersionedIdEntity;
import org.wrkr.clb.services.util.exception.ConflictException;

public abstract class VersionedEntityService {

    protected <E extends VersionedIdEntity> E checkRecordVersion(E entity, Long recordVersion) throws ConflictException {
        if (!entity.getRecordVersion().equals(recordVersion)) {
            throw new ConflictException("Concurrent modification occured.");
        }
        entity.setRecordVersion(entity.getRecordVersion() + 1);
        return entity;
    }
}
