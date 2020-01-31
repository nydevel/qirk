package org.wrkr.clb.testseeder.testrepo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Organization;
import org.wrkr.clb.model.OrganizationMemberGroup;
import org.wrkr.clb.model.Project;
import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.model.Version;
import org.wrkr.clb.model.task.TaskPriority;
import org.wrkr.clb.model.task.TaskStatus;
import org.wrkr.clb.model.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.testseeder.utils.HibernateUtil;

/**
 * @author Denis Bilenko
 */
@Repository
public class OldRepo {

    private EntityManager em = HibernateUtil.getSessionfactory().createEntityManager();
    private static OldRepo instance = new OldRepo();

    private OldRepo() {
    }

    public static OldRepo getInstance() {
        return instance;
    }
    
    public static OldRepo getNewInstance() {
        return new OldRepo();
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void persist(Object obj) {
        em.persist(obj);
    }

    public void merge(Object obj) {
        em.merge(obj);
    }

    public List<User> listUsers() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<TechnologyTag> listTags() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TechnologyTag> query = criteriaBuilder.createQuery(TechnologyTag.class);
        Root<TechnologyTag> root = query.from(TechnologyTag.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<Project> listProjects() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Project> query = criteriaBuilder.createQuery(Project.class);
        Root<Project> root = query.from(Project.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<Organization> listOrganizations() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organization> query = criteriaBuilder.createQuery(Organization.class);
        Root<Organization> root = query.from(Organization.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }
    
    public List<Organization> listOrganizations(long fromInclusive, long toInclusive) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Organization> query = criteriaBuilder.createQuery(Organization.class);
        Root<Organization> root = query.from(Organization.class);
        
        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteriaBuilder.greaterThanOrEqualTo(root.get("id"), fromInclusive);
        predicates[1] = criteriaBuilder.lessThanOrEqualTo(root.get("id"), toInclusive);
        
        query = query.select(root).where(predicates);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<Version> listVersions() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Version> query = criteriaBuilder.createQuery(Version.class);
        Root<Version> root = query.from(Version.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }
    
    public List<Version> listVersions(long fromInclusive, long toInclusive) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Version> query = criteriaBuilder.createQuery(Version.class);
        Root<Version> root = query.from(Version.class);
        
        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteriaBuilder.greaterThanOrEqualTo(root.get("id"), fromInclusive);
        predicates[1] = criteriaBuilder.lessThanOrEqualTo(root.get("id"), toInclusive);
        
        query = query.select(root).where(predicates);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<OrganizationMemberGroup> listGroups() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<OrganizationMemberGroup> query = criteriaBuilder.createQuery(OrganizationMemberGroup.class);
        Root<OrganizationMemberGroup> root = query.from(OrganizationMemberGroup.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<TaskPriority> listTaskPriorities() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TaskPriority> query = criteriaBuilder.createQuery(TaskPriority.class);
        Root<TaskPriority> root = query.from(TaskPriority.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<TaskStatus> listTaskStatuses() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TaskStatus> query = criteriaBuilder.createQuery(TaskStatus.class);
        Root<TaskStatus> root = query.from(TaskStatus.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<TaskType> listTaskTypes() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TaskType> query = criteriaBuilder.createQuery(TaskType.class);
        Root<TaskType> root = query.from(TaskType.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<Language> listLanguages() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Language> query = criteriaBuilder.createQuery(Language.class);
        Root<Language> root = query.from(Language.class);
        query = query.select(root);
        return getEntityManager().createQuery(query).getResultList();
    }

    public void commit() {
        em.getTransaction().commit();
    }
    
    public void begin() {
        em.getTransaction().begin();
    }
    
    public void close() {
        em.close();
    }
}