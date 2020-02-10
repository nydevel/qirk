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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.User_;
import org.wrkr.clb.repo.JPAIdEntityRepo;

@Repository
public class UserRepo extends JPAIdEntityRepo<User> {

    @Override
    public User get(Long id) {
        return get(User.class, id);
    }

    public List<User> listByIds(List<Long> ids) {
        return listByIds(User.class, ids);
    }

    public boolean existsByEmail(String email) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<User> root = query.from(User.class);

        query.where(cb.equal(root.get(User_.emailAddress), email));
        return exists(root, query);
    }

    public boolean existsByUsername(String username) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<User> root = query.from(User.class);

        query.where(cb.equal(root.get(User_.username), username));
        return exists(root, query);
    }

    public User getByEmail(String email) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> root = query.from(User.class);

        query.where(cb.equal(root.get(User_.emailAddress), email));
        return getSingleResultOrNull(query);
    }

    @Deprecated
    public User getEnabled(Long id) {
        return get(id);
    }

    @Deprecated
    public User getEnabledAndFetchTags(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> root = query.from(User.class);
        root.fetch(User_.tags, JoinType.LEFT);

        query.where(cb.equal(root.get(User_.id), id));
        query.distinct(true);
        return getSingleResultOrNull(query);
    }

    public User getByUsername(String username) {
        return getByUsername(username, false);
    }

    @Deprecated
    public User getEnabledByUsername(String username) {
        return getByUsername(username, true);
    }

    private User getByUsername(String username, @SuppressWarnings("unused") boolean excludeDisabled) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> userRoot = query.from(User.class);

        List<Predicate> predicates = new ArrayList<Predicate>(Arrays.asList(cb.equal(userRoot.get(User_.username), username)));
        query.where(predicates.toArray(new Predicate[0]));
        return getSingleResultOrNull(query);
    }

    public User getByUsernameOrEmail(String usernameOrEmail) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> root = query.from(User.class);

        query.where(
                cb.or(
                        cb.equal(root.get(User_.username), usernameOrEmail),
                        cb.equal(root.get(User_.emailAddress), usernameOrEmail.toLowerCase())));
        return getSingleResultOrNull(query);
    }
}
