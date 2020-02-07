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
package org.wrkr.clb.repo.project;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Memo_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMember_;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.repo.JPADeletingRepo;

@Repository
public class MemoRepo extends JPADeletingRepo<Memo> {

    @Override
    public Memo get(Long id) {
        return get(Memo.class, id);
    }

    public void deleteById(Long id) {
        deleteById(Memo.class, id);
    }

    public Memo getAndFetchAuthorUser(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Memo> query = cb.createQuery(Memo.class);

        Root<Memo> memoRoot = query.from(Memo.class);
        Fetch<Memo, ProjectMember> projectMemberFetch = memoRoot.fetch(Memo_.author);
        projectMemberFetch.fetch(ProjectMember_.user);

        query.where(cb.equal(memoRoot.get(Memo_.id), id));
        return getSingleResultOrNull(query);
    }

    public List<Memo> listByProjectIdAndFetchAuthor(Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Memo> query = cb.createQuery(Memo.class);

        Root<Memo> memoRoot = query.from(Memo.class);
        memoRoot.fetch(Memo_.author);
        Join<Memo, Project> projectJoin = memoRoot.join(Memo_.project);

        query.where(cb.equal(projectJoin.get(Project_.id), projectId));
        query.orderBy(cb.desc(memoRoot.get(Memo_.createdAt)));
        return getResultList(query);
    }

    public List<Memo> listByProjectUiIdAndFetchAuthor(String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Memo> query = cb.createQuery(Memo.class);

        Root<Memo> memoRoot = query.from(Memo.class);
        memoRoot.fetch(Memo_.author);
        Join<Memo, Project> projectJoin = memoRoot.join(Memo_.project);

        query.where(cb.equal(projectJoin.get(Project_.uiId), projectUiId));
        query.orderBy(cb.desc(memoRoot.get(Memo_.createdAt)));
        return getResultList(query);
    }
}
