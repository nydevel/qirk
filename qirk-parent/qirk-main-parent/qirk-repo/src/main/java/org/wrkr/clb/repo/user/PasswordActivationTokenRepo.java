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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.PasswordActivationToken_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.User_;
import org.wrkr.clb.repo.JPABaseDeletingRepo;

@Repository
public class PasswordActivationTokenRepo extends JPABaseDeletingRepo<PasswordActivationToken> {

    @Override
    public PasswordActivationToken get(Long id) {
        return get(PasswordActivationToken.class, id);
    }

    public List<PasswordActivationToken> list() {
        return list(PasswordActivationToken.class);
    }

    public boolean existsByToken(String token) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<PasswordActivationToken> root = query.from(PasswordActivationToken.class);

        query.where(cb.equal(root.get(PasswordActivationToken_.token), token));
        return exists(root, query);
    }

    public PasswordActivationToken getByToken(String token) {
        return getByToken(token, false);
    }

    public PasswordActivationToken getByTokenAndFetchUser(String token) {
        return getByToken(token, true);
    }

    private PasswordActivationToken getByToken(String token, boolean fetchUser) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PasswordActivationToken> query = cb.createQuery(PasswordActivationToken.class);

        Root<PasswordActivationToken> root = query.from(PasswordActivationToken.class);
        if (fetchUser) {
            root.fetch(PasswordActivationToken_.user);
        }

        query.where(cb.equal(root.get(PasswordActivationToken_.token), token));
        return getSingleResultOrNull(query);
    }

    public PasswordActivationToken getByUser(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PasswordActivationToken> query = cb.createQuery(PasswordActivationToken.class);

        Root<PasswordActivationToken> root = query.from(PasswordActivationToken.class);

        query.where(cb.equal(root.get(PasswordActivationToken_.user), user));
        return getSingleResultOrNull(query);
    }

    public PasswordActivationToken getByEmailAndFetchUser(String email) {
        return getByEmailAndFetchUser(email, false);
    }

    @Deprecated
    public PasswordActivationToken getDisabledByEmailAndFetchUser(String email) {
        return getByEmailAndFetchUser(email, true);
    }

    private PasswordActivationToken getByEmailAndFetchUser(String email, @SuppressWarnings("unused") boolean excludeEnabled) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PasswordActivationToken> query = cb.createQuery(PasswordActivationToken.class);

        Root<PasswordActivationToken> activationTokenRoot = query.from(PasswordActivationToken.class);
        activationTokenRoot.fetch(PasswordActivationToken_.user);
        Join<PasswordActivationToken, User> userJoin = activationTokenRoot.join(PasswordActivationToken_.user);

        List<Predicate> predicates = new ArrayList<Predicate>(Arrays.asList(cb.equal(userJoin.get(User_.emailAddress), email)));
        query.where(predicates.toArray(new Predicate[0]));
        return getSingleResultOrNull(query);
    }
}
