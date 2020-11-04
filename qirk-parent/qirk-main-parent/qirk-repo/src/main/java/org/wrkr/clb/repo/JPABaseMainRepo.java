package org.wrkr.clb.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.BaseEntity;

@Repository
public abstract class JPABaseMainRepo<E extends BaseEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(JPABaseMainRepo.class);

    @PersistenceContext
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public void persist(E entityToPersist) {
        long startTime = System.currentTimeMillis();
        getEntityManager().persist(entityToPersist);
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed persist query for type " + entityToPersist.getClass().getName() + " in " +
                resultTime + " ms");
    }

    public E merge(E entityToMerge) {
        long startTime = System.currentTimeMillis();
        entityToMerge = getEntityManager().merge(entityToMerge);
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed merge query for type " + entityToMerge.getClass().getName() + " in " +
                resultTime + " ms");

        return entityToMerge;
    }

    protected <T extends Object> T getSingleResultOrNull(CriteriaQuery<T> query) throws NonUniqueResultException {
        long startTime = System.currentTimeMillis();
        List<T> results = getEntityManager().createQuery(query).getResultList();
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed single result query for type " + query.getResultType().getName() + " in " +
                resultTime + " ms");

        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new NonUniqueResultException("Too many results found: " + results.size() + ", " + results.toString());
        }
        return results.get(0);
    }

    protected <T extends Object> T getFirstResultOrNull(CriteriaQuery<T> query) throws NonUniqueResultException {
        long startTime = System.currentTimeMillis();
        List<T> results = getEntityManager().createQuery(query).setMaxResults(1).getResultList();
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed first result query for type " + query.getResultType().getName() + " in " +
                resultTime + " ms");

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    protected <T extends Object> List<T> getResultList(CriteriaQuery<T> query) {
        long startTime = System.currentTimeMillis();
        List<T> results = getEntityManager().createQuery(query).getResultList();
        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed result list query for type " + query.getResultType().getName() +
                " with " + results.size() + " results in " +
                resultTime + " ms");

        return results;
    }

    protected List<E> list(Class<E> entityClass) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(entityClass);

        Root<E> root = query.from(entityClass);

        query = query.select(root);
        return getResultList(query);
    }
}
