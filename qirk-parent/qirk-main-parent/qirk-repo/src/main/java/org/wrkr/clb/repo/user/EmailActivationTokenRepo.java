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
