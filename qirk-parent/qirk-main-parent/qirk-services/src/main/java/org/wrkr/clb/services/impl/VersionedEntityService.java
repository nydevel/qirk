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
