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
package org.wrkr.clb.test.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.BaseEntity;
import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.BaseIdEntityMeta;

public class TestRepo {

    private static final Logger LOG = LoggerFactory.getLogger(TestRepo.class);

    @PersistenceContext
    private EntityManager entityManager;

    private EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public <E extends BaseEntity> void persistEntity(E entityToPersist) {
        getEntityManager().persist(entityToPersist);
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public <E extends BaseEntity> E mergeEntity(E entityToMerge) {
        return getEntityManager().merge(entityToMerge);
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public <E extends BaseIdEntity> E getEntityOrNull(Class<E> entityClass, Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityClass);

        Root<E> root = query.from(entityClass);

        query.where(cb.equal(root.get(BaseIdEntityMeta.id), id));

        List<E> results = getEntityManager().createQuery(query).getResultList();
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new NonUniqueResultException("Too many results found: " + results.size() + ", " + results.toString());
        }
        return results.get(0);
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public <E extends BaseIdEntity> E getEntity(Class<E> entityClass, Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityClass);

        Root<E> root = query.from(entityClass);

        query.where(cb.equal(root.get(BaseIdEntityMeta.id), id));
        return getEntityManager().createQuery(query).getSingleResult();
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public <E extends BaseEntity> long countEntities(Class<E> entityClass) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        query = query.select(cb.count(query.from(entityClass)));
        return getEntityManager().createQuery(query).getSingleResult();
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public <E extends BaseEntity> List<E> listEntities(Class<E> entityClass) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityClass);

        Root<E> root = query.from(entityClass);

        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void deleteEntities(BaseEntity... entities) {
        for (BaseEntity entity : entities) {
            getEntityManager().remove(entity);
        }
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public <E extends BaseEntity> void clearTable(Class<E> entityClass) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaDelete<E> criteria = cb.createCriteriaDelete(entityClass);
        criteria.from(entityClass);
        try {
            getEntityManager().createQuery(criteria).executeUpdate();
        } catch (ConstraintViolationException e) {
            LOG.error(e.getSQLException().getMessage());
            throw e;
        }
    }
}
