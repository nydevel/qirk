package org.wrkr.clb.repo.user;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.EmailActivationToken;
import org.wrkr.clb.model.user.EmailActivationToken_;
import org.wrkr.clb.repo.JPADeletingRepo;

@Repository
public class EmailActivationTokenRepo extends JPADeletingRepo<EmailActivationToken> {

    @Override
    public EmailActivationToken get(Long id) {
        return get(EmailActivationToken.class, id);
    }

    public void deleteById(Long id) {
        deleteById(EmailActivationToken.class, id);
    }

    public boolean existsByToken(String token) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<EmailActivationToken> root = query.from(EmailActivationToken.class);

        query.where(cb.equal(root.get(EmailActivationToken_.token), token));
        return exists(root, query);
    }

    public EmailActivationToken getByEmail(String email) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EmailActivationToken> query = cb.createQuery(EmailActivationToken.class);

        Root<EmailActivationToken> activationTokenRoot = query.from(EmailActivationToken.class);

        query.where(cb.equal(activationTokenRoot.get(EmailActivationToken_.emailAddress), email));
        return getSingleResultOrNull(query);
    }

    public EmailActivationToken getNotExpiredByToken(String token) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EmailActivationToken> query = cb.createQuery(EmailActivationToken.class);

        Root<EmailActivationToken> activationTokenRoot = query.from(EmailActivationToken.class);

        query.where(cb.equal(activationTokenRoot.get(EmailActivationToken_.token), token),
                cb.greaterThanOrEqualTo(
                        activationTokenRoot.get(EmailActivationToken_.expiresAt),
                        System.currentTimeMillis()));
        return getSingleResultOrNull(query);
    }
}
