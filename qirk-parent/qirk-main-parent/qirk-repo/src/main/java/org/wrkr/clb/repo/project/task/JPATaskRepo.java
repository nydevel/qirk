/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.repo.project.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.organization.OrganizationMember_;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtag_;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskPriority_;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.project.task.TaskType_;
import org.wrkr.clb.model.project.task.Task_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.User_;
import org.wrkr.clb.repo.JPABaseIdRepo;
import org.wrkr.clb.repo.context.TaskSearchContext;
import org.wrkr.clb.repo.sort.SortingOption;

@Deprecated
@Repository
public class JPATaskRepo extends JPABaseIdRepo<Task> {

    @Override
    public Task get(Long id) {
        return get(Task.class, id);
    }

    public List<Task> listByProjectIdOrUiIdAndFetchTypeAndPriorityAndStatus(Long projectId, String projectUiId) {
        if (projectId == null && projectUiId == null) {
            return new ArrayList<Task>();
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);

        Root<Task> taskRoot = query.from(Task.class);
        taskRoot.fetch(Task_.type);
        taskRoot.fetch(Task_.priority);
        taskRoot.fetch(Task_.status);
        Join<Task, Project> projectJoin = taskRoot.join(Task_.project);

        List<Predicate> predicates = new ArrayList<Predicate>(Arrays.asList(
                cb.equal(taskRoot.get(Task_.hidden), false)));
        if (projectId != null) {
            predicates.add(cb.equal(projectJoin.get(Project_.id), projectId));
        }
        if (projectUiId != null) {
            predicates.add(cb.equal(projectJoin.get(Project_.uiId), projectUiId));
        }
        query.where(predicates.toArray(new Predicate[0]));
        query.distinct(true);
        query.orderBy(cb.desc(taskRoot.get(Task_.updatedAt)));
        return getResultList(query);
    }

    public List<Task> listTopByProjectIdOrUiIdAndAssigneeUserAndFetchEverythingForRead(
            Long projectId, String projectUiId, User assignee, int maxResults) {
        if (projectId == null && projectUiId == null) {
            return new ArrayList<Task>();
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);

        Root<Task> taskRoot = query.from(Task.class);
        taskRoot.fetch(Task_.project);
        taskRoot.fetch(Task_.type);
        taskRoot.fetch(Task_.priority);
        taskRoot.fetch(Task_.status);
        Join<Task, OrganizationMember> assigneeJoin = taskRoot.join(Task_.assignee);
        Join<Task, Project> projectJoin = taskRoot.join(Task_.project);

        List<Predicate> predicates = new ArrayList<Predicate>(Arrays.asList(
                cb.equal(assigneeJoin.get(OrganizationMember_.user), assignee),
                cb.equal(taskRoot.get(Task_.hidden), false)));
        if (projectId != null) {
            predicates.add(cb.equal(projectJoin.get(Project_.id), projectId));
        }
        if (projectUiId != null) {
            predicates.add(cb.equal(projectJoin.get(Project_.uiId), projectUiId));
        }
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(taskRoot.get(Task_.updatedAt)));
        return getEntityManager().createQuery(query).setMaxResults(maxResults).getResultList();
    }

    private Path<?> getOrderPath(Root<Task> taskRoot, SortingOption.ForTask sortBy) {
        switch (sortBy) {
            case ASSIGNEE:
                Join<Task, OrganizationMember> assigneeJoin = taskRoot.join(Task_.assignee, JoinType.LEFT);
                Join<OrganizationMember, User> assigneeUserJoin = assigneeJoin.join(OrganizationMember_.user, JoinType.LEFT);
                return assigneeUserJoin.get(User_.username);
            case CREATED_AT:
                return taskRoot.get(Task_.createdAt);
            case NUMBER:
                return taskRoot.get(Task_.number);
            case PRIORITY:
                Join<Task, TaskPriority> taskPriorityJoin = taskRoot.join(Task_.priority);
                return taskPriorityJoin.get(TaskPriority_.importance);
            case REPORTER:
                Join<Task, OrganizationMember> reporterJoin = taskRoot.join(Task_.reporter);
                Join<OrganizationMember, User> reporterJoinUserJoin = reporterJoin.join(OrganizationMember_.user);
                return reporterJoinUserJoin.get(User_.username);
            case SUMMARY:
                return taskRoot.get(Task_.summary);
            case UPDATED_AT:
                return taskRoot.get(Task_.updatedAt);
        }
        return taskRoot.get(Task_.updatedAt);
    }

    public List<Task> search(TaskSearchContext searchContext) {
        if (searchContext.projectDTO.getId() == null && searchContext.projectDTO.getUiId() == null) {
            return new ArrayList<Task>();
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);

        Root<Task> taskRoot = query.from(Task.class);
        Join<Task, Project> projectJoin = taskRoot.join(Task_.project);
        taskRoot.fetch(Task_.reporter).fetch(OrganizationMember_.user);
        taskRoot.fetch(Task_.assignee, JoinType.LEFT).fetch(OrganizationMember_.user, JoinType.LEFT);
        taskRoot.fetch(Task_.type);
        taskRoot.fetch(Task_.priority);
        taskRoot.fetch(Task_.status);

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (searchContext.projectDTO.getId() != null) {
            predicates.add(cb.equal(projectJoin.get(Project_.id), searchContext.projectDTO.getId()));
        }
        if (searchContext.projectDTO.getUiId() != null) {
            predicates.add(cb.equal(projectJoin.get(Project_.uiId), searchContext.projectDTO.getUiId()));
        }

        if (searchContext.reporterId != null) {
            Join<Task, OrganizationMember> organizationMemberJoin = taskRoot.join(Task_.reporter);
            predicates.add(cb.equal(organizationMemberJoin.get(OrganizationMember_.id), searchContext.reporterId));
        }
        if (searchContext.assigneeId != null) {
            Join<Task, OrganizationMember> organizationMemberJoin = taskRoot.join(Task_.assignee);
            predicates.add(cb.equal(organizationMemberJoin.get(OrganizationMember_.id), searchContext.assigneeId));
        }

        if (searchContext.types.size() > 0) {
            Join<Task, TaskType> taskTypeJoin = taskRoot.join(Task_.type);
            predicates.add(taskTypeJoin.get(TaskType_.nameCode).in(searchContext.types));
        }
        if (searchContext.priorities.size() > 0) {
            Join<Task, TaskPriority> taskPriorityJoin = taskRoot.join(Task_.priority);
            predicates.add(taskPriorityJoin.get(TaskPriority_.nameCode).in(searchContext.priorities));
        }
        if (!searchContext.hashtag.isBlank()) {
            Join<Task, TaskHashtag> taskHashtagJoin = taskRoot.join(Task_.hashtags);
            predicates.add(cb.equal(taskHashtagJoin.get(TaskHashtag_.name), searchContext.hashtag));
        }

        query.where(predicates.toArray(new Predicate[0]));

        Path<?> orderPath = getOrderPath(taskRoot, searchContext.sortBy);
        List<Order> orders = new ArrayList<Order>();
        switch (searchContext.sortOrder) {
            case ASC:
                orders.add(cb.asc(orderPath));
                break;
            case DESC:
                orders.add(cb.desc(orderPath));
                break;
        }
        if (!searchContext.sortBy.equals(SortingOption.ForTask.CREATED_AT)
                && !searchContext.sortBy.equals(SortingOption.ForTask.UPDATED_AT)
                && !searchContext.sortBy.equals(SortingOption.ForTask.NUMBER)) {
            orders.add(cb.desc(taskRoot.get(Task_.id)));
        }
        query.orderBy(orders);

        return getResultList(query);
    }
}
