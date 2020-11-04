package org.wrkr.clb.repo.project;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.Issue_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.repo.JPAIdEntityRepo;

@Repository
public class IssueRepo extends JPAIdEntityRepo<Issue> {

    @Override
    public Issue get(Long id) {
        return get(Issue.class, id);
    }

    public Issue getAndFetchUser(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Issue> query = cb.createQuery(Issue.class);

        Root<Issue> root = query.from(Issue.class);
        root.fetch(Issue_.reporter, JoinType.LEFT);

        query.where(cb.equal(root.get(Issue_.id), id));
        return getSingleResultOrNull(query);
    }

    public List<Issue> listByProjectIdAndFetchReporter(Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Issue> query = cb.createQuery(Issue.class);

        Root<Issue> issueRoot = query.from(Issue.class);
        issueRoot.fetch(Issue_.reporter, JoinType.LEFT);
        Join<Issue, Project> projectJoin = issueRoot.join(Issue_.project);

        query.where(cb.equal(projectJoin.get(Project_.id), projectId));
        return getResultList(query);
    }

    public List<Issue> listByProjectUiIdAndFetchReporter(String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Issue> query = cb.createQuery(Issue.class);

        Root<Issue> issueRoot = query.from(Issue.class);
        issueRoot.fetch(Issue_.reporter, JoinType.LEFT);
        Join<Issue, Project> projectJoin = issueRoot.join(Issue_.project);

        query.where(cb.equal(projectJoin.get(Project_.uiId), projectUiId));
        return getResultList(query);
    }
}
