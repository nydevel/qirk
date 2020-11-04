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
