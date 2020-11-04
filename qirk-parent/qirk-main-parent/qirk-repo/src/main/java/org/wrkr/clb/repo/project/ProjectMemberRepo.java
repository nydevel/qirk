package org.wrkr.clb.repo.project;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMember_;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.User_;
import org.wrkr.clb.repo.JPADeletingRepo;

@Repository
public class ProjectMemberRepo extends JPADeletingRepo<ProjectMember> {

    /**
     * @deprecated use {@link #getNotFired()} instead
     */
    @Deprecated
    @Override
    public ProjectMember get(Long id) {
        return get(ProjectMember.class, id);
    }

    public ProjectMember getNotFired(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);

        query.where(cb.equal(memberRoot.get(ProjectMember_.id), id),
                cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public ProjectMember getNotFiredAndFetchUserAndProject(Long id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);
        memberRoot.fetch(ProjectMember_.user);
        memberRoot.fetch(ProjectMember_.project);

        query.where(cb.equal(memberRoot.get(ProjectMember_.id), id),
                cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public ProjectMember getNotFiredByUserAndProject(User user, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);

        query.where(cb.equal(memberRoot.get(ProjectMember_.user), user),
                cb.equal(memberRoot.get(ProjectMember_.project), project),
                cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public ProjectMember getNotFiredByUserAndProjectId(User user, Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);
        Join<ProjectMember, Project> projectJoin = memberRoot.join(ProjectMember_.project);

        query.where(cb.equal(memberRoot.get(ProjectMember_.user), user),
                cb.equal(projectJoin.get(Project_.id), projectId),
                cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public ProjectMember getNotFiredByUserAndProjectUiId(User user, String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);
        Join<ProjectMember, Project> projectJoin = memberRoot.join(ProjectMember_.project);

        query.where(cb.equal(memberRoot.get(ProjectMember_.user), user),
                cb.equal(projectJoin.get(Project_.uiId), projectUiId),
                cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public ProjectMember getNotFiredByUsernameAndProjectId(String username, Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);
        Join<ProjectMember, User> userJoin = memberRoot.join(ProjectMember_.user);
        Join<ProjectMember, Project> projectJoin = memberRoot.join(ProjectMember_.project);

        query.where(cb.equal(userJoin.get(User_.username), username),
                cb.equal(projectJoin.get(Project_.id), projectId),
                cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getSingleResultOrNull(query);
    }

    public List<ProjectMember> listNotFiredByProjectIdAndFetchUser(Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);
        memberRoot.fetch(ProjectMember_.user);
        Join<ProjectMember, Project> projectJoin = memberRoot.join(ProjectMember_.project);

        query.where(cb.equal(projectJoin.get(Project_.id), projectId), cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getResultList(query);
    }

    public List<ProjectMember> listNotFiredByProjectUiIdAndFetchUser(String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProjectMember> query = cb.createQuery(ProjectMember.class);

        Root<ProjectMember> memberRoot = query.from(ProjectMember.class);
        memberRoot.fetch(ProjectMember_.user);
        Join<ProjectMember, Project> projectJoin = memberRoot.join(ProjectMember_.project);

        query.where(cb.equal(projectJoin.get(Project_.uiId), projectUiId), cb.equal(memberRoot.get(ProjectMember_.fired), false));
        return getResultList(query);
    }
}
