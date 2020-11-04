package org.wrkr.clb.repo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.BaseIdEntity_;

public abstract class JPADeletingRepo<E extends BaseIdEntity> extends JPAIdEntityRepo<E> {

    private static final Logger LOG = LoggerFactory.getLogger(JPADeletingRepo.class);

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
