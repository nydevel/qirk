package org.wrkr.clb.repo;

import java.util.List;

import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.BaseIdEntityMeta;


@Repository
public abstract class JDBCIdEntityRepo extends JDBCBaseMainRepo {

    protected <E extends BaseIdEntity> Long[] buildIdsArray(List<E> entities) {
        Long[] result = new Long[entities.size()];
        for (int i = 0; i < entities.size(); i++) {
            result[i] = entities.get(i).getId();
        }
        return result;
    }

    protected <E extends BaseIdEntity> E setIdAfterSave(E entity, KeyHolder keyHolder) {
        entity.setId((Long) keyHolder.getKeys().get(BaseIdEntityMeta.id));
        return entity;
    }
}
