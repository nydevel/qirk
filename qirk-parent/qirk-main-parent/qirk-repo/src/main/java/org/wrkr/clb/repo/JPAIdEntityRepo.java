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
