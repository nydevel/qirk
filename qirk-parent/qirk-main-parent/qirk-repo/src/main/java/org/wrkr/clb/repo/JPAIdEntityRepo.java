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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.BaseIdEntity_;


@Repository
public abstract class JPAIdEntityRepo<E extends BaseIdEntity> extends JPABaseMainRepo<E> {

    private static final Logger LOG = LoggerFactory.getLogger(JPAIdEntityRepo.class);

    protected boolean exists(Root<E> root, CriteriaQuery<Long> query) {
        query.select(root.get(BaseIdEntity_.ID));

        long startTime = System.currentTimeMillis();
        List<Long> results = getEntityManager().createQuery(query).setMaxResults(1).getResultList();
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed exists query for type " + root.getJavaType().getName() + " in " +
                resultTime + " ms");

        return !results.isEmpty();
    }

    protected E get(Class<E> entityClass, Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityClass);

        Root<E> root = query.from(entityClass);

        query.where(cb.equal(root.get(BaseIdEntity_.id), id));
        return getSingleResultOrNull(query);
    }

    public abstract E get(Long id);

    protected List<E> listAndOrderById(Class<E> entityClass) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityClass);

        Root<E> root = query.from(entityClass);

        query = query.select(root);
        query.orderBy(cb.asc(root.get(BaseIdEntity_.id)));
        return getResultList(query);
    }

    protected List<E> listByIds(Class<E> entityClass, List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<E>();
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityClass);

        Root<E> root = query.from(entityClass);

        query.where(root.get(BaseIdEntity_.id).in(ids));
        return getResultList(query);
    }
}
