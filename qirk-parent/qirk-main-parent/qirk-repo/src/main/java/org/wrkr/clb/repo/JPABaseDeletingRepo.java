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
package org.wrkr.clb.repo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.BaseIdEntity_;

public abstract class JPABaseDeletingRepo<E extends BaseIdEntity> extends JPABaseIdRepo<E> {

    private static final Logger LOG = LoggerFactory.getLogger(JPABaseDeletingRepo.class);

    public void delete(E entityToDelete) {
        long startTime = System.currentTimeMillis();
        getEntityManager().remove(entityToDelete);
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed remove query for type " + entityToDelete.getClass().getName() + " in " +
                resultTime + " ms");
    }

    protected void deleteById(Class<E> entityClass, Long id) throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<E> criteria = cb.createCriteriaDelete(entityClass);

        Root<E> root = criteria.from(entityClass);
        criteria.where(cb.equal(root.get(BaseIdEntity_.id), id));

        long startTime = System.currentTimeMillis();
        int affectedRows = getEntityManager().createQuery(criteria).executeUpdate();
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed delete by id query for type " + entityClass.getName() + " in " +
                resultTime + " ms");

        if (affectedRows > 1) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(criteria.toString(), 1, affectedRows);
        }
    }
}
