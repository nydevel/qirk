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
package org.wrkr.clb.services.project.task.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.crypto.token.chat.TaskChatTokenData;
import org.wrkr.clb.common.jdbc.transaction.RetryOnDuplicateKey;
import org.wrkr.clb.common.jms.message.notification.TaskMessage;
import org.wrkr.clb.common.jms.message.notification.TaskUpdateNotificationMessage;
import org.wrkr.clb.common.jms.message.statistics.NewTaskMessage;
import org.wrkr.clb.common.jms.message.statistics.TaskUpdateStatisticsMessage;
import org.wrkr.clb.common.jms.services.NotificationSourceSender;
import org.wrkr.clb.common.jms.services.StatisticsSender;
import org.wrkr.clb.common.util.chat.ChatType;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.common.util.strings.MarkdownUtils;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskCard;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.context.TaskSearchContext;
import org.wrkr.clb.repo.organization.JDBCOrganizationMemberRepo;
import org.wrkr.clb.repo.organization.OrganizationMemberRepo;
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.repo.project.task.ProjectTaskNumberSequenceRepo;
import org.wrkr.clb.repo.project.task.TaskCardRepo;
import org.wrkr.clb.repo.project.task.TaskHashtagRepo;
import org.wrkr.clb.repo.project.task.TaskLinkRepo;
import org.wrkr.clb.repo.project.task.TaskPriorityRepo;
import org.wrkr.clb.repo.project.task.TaskStatusRepo;
import org.wrkr.clb.repo.project.task.TaskSubscriberRepo;
import org.wrkr.clb.repo.project.task.TaskTypeRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchTaskService;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.PaginatedListDTO;
import org.wrkr.clb.services.dto.project.task.LinkedTaskDTO;
import org.wrkr.clb.services.dto.project.task.SearchedTaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskCardIdDTO;
import org.wrkr.clb.services.dto.project.task.TaskDTO;
import org.wrkr.clb.services.dto.project.task.TaskLinkDTO;
import org.wrkr.clb.services.dto.project.task.TaskReadDTO;
import org.wrkr.clb.services.impl.VersionedEntityService;
import org.wrkr.clb.services.project.task.TaskService;
import org.wrkr.clb.services.project.task.TaskSubscriberService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;


//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultTaskService extends VersionedEntityService implements TaskService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskService.class);

    private static final Set<String> RESERVED_HASHTAG_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            TaskType.Type.BUG.toString().toLowerCase(),
            TaskType.Type.IMPROVEMENT.toString().toLowerCase(),
            "feature", // instead of new_feature
            TaskType.Type.TASK.toString().toLowerCase(),
            TaskPriority.Priority.CRITICAL.toString().toLowerCase(),
            TaskPriority.Priority.MAJOR.toString().toLowerCase(),
            TaskPriority.Priority.MINOR.toString().toLowerCase())));

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String RECURSIVE_LINK = "RECURSIVE_LINK";
        public static final String TASKS_FROM_SEPARATE_PROJECTS = "TASKS_FROM_SEPARATE_PROJECTS";
    }

    // token config values
    private Integer chatTokenNotBeforeToleranceSeconds;
    private Integer chatTokenLifetimeSeconds;
    // search config value
    private Integer searchSize;

    public void setChatTokenNotBeforeToleranceSeconds(Integer chatTokenNotBeforeToleranceSeconds) {
        this.chatTokenNotBeforeToleranceSeconds = chatTokenNotBeforeToleranceSeconds;
    }

    public void setChatTokenLifetimeSeconds(Integer chatTokenLifetimeSeconds) {
        this.chatTokenLifetimeSeconds = chatTokenLifetimeSeconds;
    }

    public void setSearchSize(Integer searchSize) {
        this.searchSize = searchSize;
    }

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private JDBCProjectRepo jdbcProjectRepo;

    @Autowired
    private ProjectTaskNumberSequenceRepo taskNumberSequenceRepo;

    @Autowired
    private OrganizationMemberRepo organizationMemberRepo;

    @Autowired
    private JDBCOrganizationMemberRepo jdbcOrganizationMemberRepo;

    @Autowired
    private TaskTypeRepo taskTypeRepo;

    @Autowired
    private TaskPriorityRepo taskPriorityRepo;

    @Autowired
    private TaskStatusRepo taskStatusRepo;

    @Autowired
    private TaskHashtagRepo hashtagRepo;

    @Autowired
    private TaskSubscriberRepo subscriberRepo;

    @Autowired
    private TaskCardRepo cardRepo;

    @Autowired
    private TaskLinkRepo linkRepo;

    @Autowired
    private TaskSubscriberService subscriberService;

    @Autowired
    private ElasticsearchTaskService elasticsearchService;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private StatisticsSender statisticsSender;

    @Autowired
    private NotificationSourceSender notificationSender;

    @Autowired
    private TokenGenerator tokenGenerator;

    private ProjectTaskNumberSequence setNextTaskNumber(ProjectTaskNumberSequence sequence, long number) {
        sequence.setNextTaskNumber(number);
        taskNumberSequenceRepo.update(sequence);
        return sequence;
    }

    private <T extends Enum<T>> T getEnum(Class<T> enumClass, String nameCode) throws ApplicationException {
        try {
            return Enum.valueOf(enumClass, nameCode);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(JsonStatusCode.CONSTRAINT_VIOLATION, "Invalid input.", e);
        }
    }

    private Task setAttributes(Task task, TaskDTO taskDTO) throws Exception {
        TaskType.Type type = getEnum(TaskType.Type.class, taskDTO.typeNameCode);
        TaskPriority.Priority priority = getEnum(TaskPriority.Priority.class, taskDTO.priorityNameCode);
        TaskStatus.Status status = getEnum(TaskStatus.Status.class, taskDTO.statusNameCode);

        if ((task.getType() == null || !type.equals(task.getType().getNameCode())) && type.isDeprecated()) {
            throw new BadRequestException(JsonStatusCode.CONSTRAINT_VIOLATION, "Invalid input.");
        }
        if ((task.getPriority() == null || !priority.equals(task.getPriority().getNameCode())) && priority.isDeprecated()) {
            throw new BadRequestException(JsonStatusCode.CONSTRAINT_VIOLATION, "Invalid input.");
        }

        String descriptionMd = taskDTO.description.strip();
        String descriptionHtml = MarkdownUtils.markdownToHtml(descriptionMd);
        task.setDescriptionMd(descriptionMd);
        task.setDescriptionHtml(descriptionHtml);
        task.setSummary(MarkdownUtils.descriptionToSummary(descriptionMd, descriptionHtml, Task.SUMMARY_LENGTH).strip());

        if (task.getId() == null && taskDTO.assignToMe) {
            task.setAssignee(task.getReporter());
        } else {
            if (taskDTO.assigneeId != null) {
                // don't set assignee when it's the same
                if (task.getAssignee() == null || !taskDTO.assigneeId.equals(task.getAssignee().getId())) {
                    OrganizationMember assignee = organizationMemberRepo.getNotFiredByIdAndOrganizationAndFetchUser(
                            taskDTO.assigneeId, task.getProject().getOrganization());
                    if (assignee == null) {
                        throw new NotFoundException("Assignee organization member");
                    }
                    task.setAssignee(assignee);
                }
            } else {
                task.setAssignee(null);
            }
        }

        task.setType(taskTypeRepo.getByNameCode(type));
        task.setPriority(taskPriorityRepo.getByNameCode(priority));
        task.setStatus(taskStatusRepo.getByNameCode(status));

        return task;
    }

    private List<TaskHashtag> setHashtags(Task task, Set<Long> hashtagIds, Set<String> hashtagNames) {
        long startTime = System.currentTimeMillis();

        Long projectId = task.getProject().getId();
        Long taskId = task.getId();

        List<TaskHashtag> hashtagsToSet = new ArrayList<TaskHashtag>();
        if (!hashtagIds.isEmpty()) {
            hashtagsToSet = hashtagRepo.listByIdsAndProjectId(hashtagIds, projectId);
        }

        List<String> hashtagNamesToInsert = new ArrayList<String>(hashtagNames.size());
        for (String hashtagName : hashtagNames) {
            if (RESERVED_HASHTAG_NAMES.contains(hashtagName)) {
                continue;
            }

            TaskHashtag hashtag = hashtagRepo.getByProjectIdAndName(projectId, hashtagName);
            if (hashtag != null) {
                hashtagsToSet.add(hashtag);
            } else {
                hashtagNamesToInsert.add(hashtagName);
            }
        }

        if (!hashtagNamesToInsert.isEmpty()) {
            hashtagsToSet.addAll(hashtagRepo.saveBatchByProjectIdAndNames(projectId, hashtagNamesToInsert));
        }
        hashtagRepo.setHashtagsToTask(taskId, hashtagsToSet);

        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed setHashtags in " + resultTime + " ms");

        return hashtagsToSet;
    }

    private List<Task> setLinkedTasks(Task task, Set<Long> linkedTaskIds) {
        List<Task> linkedTasks = taskRepo.listByProjectIdAndIds(task.getProject().getId(), linkedTaskIds);
        linkRepo.saveBatch(task, linkedTasks);
        return linkedTasks;
    }

    private <M extends TaskMessage> M setAttributesToNotificationMessage(M notificationMessage, User currentUser, Task task) {
        Project project = task.getProject();
        notificationMessage.projectId = project.getId();
        notificationMessage.projectUiId = project.getUiId();
        notificationMessage.projectName = project.getName();

        Organization organizaiton = project.getOrganization();
        notificationMessage.organizationId = organizaiton.getId();
        notificationMessage.organizationUiId = organizaiton.getUiId();

        notificationMessage.taskId = task.getId();
        notificationMessage.taskNumber = task.getNumber();
        notificationMessage.taskSummary = task.getSummary();

        notificationMessage.updatedByUserId = currentUser.getId();
        notificationMessage.updatedByUsername = currentUser.getUsername();
        notificationMessage.updatedByFullName = currentUser.getFullName();

        return notificationMessage;
    }

    private TaskUpdateNotificationMessage setOldAttributesToNotificationMessage(
            TaskUpdateNotificationMessage notificationMessage, Task task) {
        if (task.getAssignee() != null) {
            notificationMessage.oldAssignee = task.getAssignee().getUser().getId();
        }
        notificationMessage.oldType = task.getType().getNameCode().toString();
        notificationMessage.oldPriority = task.getPriority().getNameCode().toString();
        notificationMessage.oldStatus = task.getStatus().getNameCode().toString();
        return notificationMessage;
    }

    private <M extends TaskMessage> M setNewAttributesToNotificationMessage(M notificationMessage, Task task) {
        notificationMessage.updatedAt = task.getUpdatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        if (task.getAssignee() != null) {
            User assignee = task.getAssignee().getUser();
            notificationMessage.newAssignee = assignee.getId();
            notificationMessage.newAssigneeUsername = assignee.getUsername();
            notificationMessage.newAssigneeFullName = assignee.getFullName();
        }
        notificationMessage.newType = task.getType().getNameCode().toString();
        notificationMessage.newPriority = task.getPriority().getNameCode().toString();
        notificationMessage.newStatus = task.getStatus().getNameCode().toString();
        notificationMessage.newTypeHumanReadable = task.getType().getNameCode().toHumanReadable();
        notificationMessage.newPriorityHumanReadable = task.getPriority().getNameCode().toHumanReadable();
        notificationMessage.newStatusHumanReadable = task.getStatus().getNameCode().toHumanReadable();
        return notificationMessage;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public TaskReadDTO create(User currentUser, TaskDTO taskDTO, int retryNumber) throws Exception {
        // security
        securityService.authzCanCreateTask(currentUser, taskDTO.project);
        // security

        taskDTO.normalize();

        Task task = new Task();

        Project project = null;
        if (taskDTO.project.id != null) {
            project = projectRepo.getAndFetchTaskNumberSequence(taskDTO.project.id);
        } else if (taskDTO.project.uiId != null) {
            project = projectRepo.getByUiIdAndFetchTaskNumberSequence(taskDTO.project.uiId);
        }
        if (project == null) {
            throw new NotFoundException("Project");
        }
        task.setProject(project);

        ProjectTaskNumberSequence taskNumberSequence = project.getTaskNumberSequence();
        long taskNumber = taskNumberSequence.getNextTaskNumber();
        if (retryNumber >= RetryOnDuplicateKey.DEFAULT_RETRIES_COUNT - 1) {
            // safety measure against corrupted data
            taskNumber = taskRepo.getMaxNumberByProjectIdOrZero(project.getId()) + 1L;
        }
        task.setNumber(taskNumber);
        setNextTaskNumber(taskNumberSequence, taskNumber + 1L);

        OrganizationMember reporter = organizationMemberRepo.getNotFiredByUserAndOrganization(currentUser,
                project.getOrganization());
        if (reporter == null) {
            throw new NotFoundException("Reporter organization member");
        }
        task.setReporter(reporter);

        task = setAttributes(task, taskDTO);

        OffsetDateTime now = DateTimeUtils.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        task = taskRepo.save(task);
        task.setHashtags(setHashtags(task, taskDTO.hashtagIds, taskDTO.hashtagNames));
        task.setLinkedTasks(setLinkedTasks(task, taskDTO.linkedTaskIds));

        TaskReadDTO dto = TaskReadDTO.fromEntityWithEverythingForReadAndLinkedTasks(task, task.getLinkedTasks());

        // notification
        Long assigneeUserId = (task.getAssignee() == null ? null : task.getAssignee().getUser().getId());
        Long reporterUserId = task.getReporter().getUser().getId();
        if (assigneeUserId != null && !assigneeUserId.equals(reporterUserId)) {
            subscriberService.create(assigneeUserId, task.getId());
        }

        List<User> subscribers = subscriberService.listWithEmail(task.getId(), NotificationSettings.Setting.TASK_CREATED);
        TaskMessage notificationMessage = new TaskMessage(
                User.toIds(subscribers), User.toEmailsIfSendEmailNotifications(subscribers));
        notificationMessage = setAttributesToNotificationMessage(notificationMessage, currentUser, task);
        notificationMessage = setNewAttributesToNotificationMessage(notificationMessage, task);
        notificationSender.send(notificationMessage);

        // reporter is subscribed after TASK_CREATED notification
        subscriberService.create(reporterUserId, task.getId());
        // notification

        // statistics
        statisticsSender.send(new NewTaskMessage(task.getProject().getId(),
                task.getProject().getName(),
                task.getId(),
                task.getCreatedAt(),
                task.getType().getNameCode().toString(),
                task.getPriority().getNameCode().toString(),
                task.getStatus().getNameCode().toString()));
        // statistics

        // elasticsearch
        try {
            elasticsearchService.index(task);
        } catch (Exception e) {
            LOG.error("Could not save task " + task.getId() + " to elasticsearch", e);
        }
        // elasticsearch

        return dto;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public TaskReadDTO update(User currentUser, TaskDTO taskDTO) throws Exception {
        // security
        securityService.authzCanUpdateTask(currentUser, taskDTO.id);
        // security

        taskDTO.normalize();

        Task task = taskRepo.getByIdAndFetchEverythingForUpdate(taskDTO.id);
        if (task == null) {
            throw new NotFoundException("Task");
        }
        task = checkRecordVersion(task, taskDTO.recordVersion);

        // notification
        TaskUpdateNotificationMessage notificationMessage = new TaskUpdateNotificationMessage();
        notificationMessage = setAttributesToNotificationMessage(notificationMessage, currentUser, task);
        notificationMessage = setOldAttributesToNotificationMessage(notificationMessage, task);
        // notification

        // statistics
        TaskUpdateStatisticsMessage statisticsMessage = new TaskUpdateStatisticsMessage(
                task.getProject().getId(), task.getProject().getName(),
                task.getId(), task.getUpdatedAt());
        if (!task.getType().getNameCode().getValue().equals(taskDTO.typeNameCode)) {
            statisticsMessage.type = taskDTO.typeNameCode;
        }
        if (!task.getPriority().getNameCode().getValue().equals(taskDTO.priorityNameCode)) {
            statisticsMessage.priority = taskDTO.priorityNameCode;
        }
        if (!task.getStatus().getNameCode().getValue().equals(taskDTO.statusNameCode)) {
            statisticsMessage.status = taskDTO.statusNameCode;
        }
        // statistics

        Long oldAssigneeUserId = (task.getAssignee() == null ? null : task.getAssignee().getUser().getId());
        TaskStatus.Status oldStatus = task.getStatus().getNameCode();

        task = setAttributes(task, taskDTO);
        task.setUpdatedAt(DateTimeUtils.now());

        taskRepo.update(task);
        task.setHashtags(setHashtags(task, taskDTO.hashtagIds, taskDTO.hashtagNames));

        TaskStatus.Status newStatus = task.getStatus().getNameCode();
        if (task.getCardId() != null && !oldStatus.equals(newStatus)) {
            TaskCard.Status cardStatus = task.getCard().getStatus();

            if (!TaskCard.Status.COMPLETED.equals(cardStatus) && oldStatus.isAlive() && newStatus.isNotAlive()) {
                if (!taskRepo.existsAliveByCardId(task.getCardId())) {
                    cardRepo.updateStatusAndIncRecordVersion(TaskCard.Status.COMPLETED, task.getCardId());
                }
            }

            if (TaskCard.Status.COMPLETED.equals(cardStatus) && oldStatus.isNotAlive() && newStatus.isAlive()) {
                cardRepo.updateStatusAndIncRecordVersion(TaskCard.Status.ACTIVE, task.getCardId());
            }
        }

        TaskReadDTO dto = TaskReadDTO.fromEntityWithEverythingForRead(task);

        // notification
        Long newAssigneeUserId = (task.getAssignee() == null ? null : task.getAssignee().getUser().getId());
        if (newAssigneeUserId != null && !newAssigneeUserId.equals(oldAssigneeUserId)) {
            subscriberService.create(newAssigneeUserId, task.getId());
        }
        List<User> subscribers = subscriberService.listWithEmail(task.getId(), NotificationSettings.Setting.TASK_UPDATED);

        notificationMessage.subscriberIds = User.toIds(subscribers);
        notificationMessage.subscriberEmails = User.toEmailsIfSendEmailNotifications(subscribers);
        notificationMessage = setNewAttributesToNotificationMessage(notificationMessage, task);
        notificationSender.send(notificationMessage);
        // notification

        // statistics
        if (statisticsMessage.type != null || statisticsMessage.priority != null || statisticsMessage.status != null) {
            statisticsSender.send(statisticsMessage);
        }
        // statistics

        // elasticsearch
        try {
            elasticsearchService.updateOrIndex(task);
        } catch (Exception e) {
            LOG.error("Could not save task " + task.getId() + " to elasticsearch", e);
        }
        // elasticsearch

        return dto;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public TaskCardIdDTO updateCard(User currentUser, TaskCardIdDTO taskDTO) throws ApplicationException {
        // security
        securityService.authzCanModifyTaskCardByTaskId(currentUser, taskDTO.id);
        // security

        Task task = taskRepo.getForUpdateCardIdById(taskDTO.id);
        if (task == null) {
            throw new NotFoundException("Task");
        }
        task = checkRecordVersion(task, taskDTO.recordVersion);

        TaskCard card = null;
        if (taskDTO.cardId != null) {
            card = cardRepo.getActiveByIdAndProjectId(taskDTO.cardId, task.getProjectId());
            if (card == null) {
                throw new NotFoundException("Task card");
            }
        }

        task.setCard(card);
        task.setHidden(card != null && TaskCard.Status.STOPPED.equals(card.getStatus()));
        taskRepo.updateRecordVersionAndCardIdAndHiddenById(task);

        // elasticsearch
        try {
            elasticsearchService.updateCardAndHidden(task);
        } catch (Exception e) {
            LOG.error("Could not save task " + task.getId() + " to elasticsearch", e);
        }
        // elasticsearch

        return TaskCardIdDTO.fromEntity(task);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void addLink(User currentUser, TaskLinkDTO taskLinkDTO) throws ApplicationException {
        if (taskLinkDTO.task1Id.equals(taskLinkDTO.task2Id)) {
            throw new BadRequestException(JsonStatusCode.RECURSIVE_LINK, "Task can't be linked to itself.");
        }

        // security
        securityService.authzCanUpdateTask(currentUser, taskLinkDTO.task1Id);
        // security

        List<Long> projectIds = taskRepo.listProjectIdsByIds(taskLinkDTO.task1Id, taskLinkDTO.task2Id);
        if (projectIds.size() != 2) {
            throw new NotFoundException("Task");
        }
        if (!projectIds.get(0).equals(projectIds.get(1))) {
            throw new BadRequestException(JsonStatusCode.TASKS_FROM_SEPARATE_PROJECTS,
                    "Can't link tasks from separate projects.");
        }

        if (linkRepo.exists(taskLinkDTO.task1Id, taskLinkDTO.task2Id)) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS,
                    "Task link already exists.");
        }
        linkRepo.save(taskLinkDTO.task1Id, taskLinkDTO.task2Id);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void removeLink(User currentUser, TaskLinkDTO taskLinkDTO) throws ApplicationException {
        if (taskLinkDTO.task1Id.equals(taskLinkDTO.task2Id)) {
            throw new BadRequestException(JsonStatusCode.RECURSIVE_LINK, "Task can't be linked to itself.");
        }

        // security
        securityService.authzCanUpdateTask(currentUser, taskLinkDTO.task1Id);
        // security

        linkRepo.delete(taskLinkDTO.task1Id, taskLinkDTO.task2Id);
    }

    private TaskReadDTO getDTO(User currentUser, Long taskId) throws ApplicationException {
        Task task = taskRepo.getByIdAndFetchEverythingForRead(taskId);
        if (task == null) {
            throw new NotFoundException("Task");
        }

        task.setLinkedTasks(taskRepo.listByLinkedTaskId(taskId));
        task.setHashtags(hashtagRepo.listByTaskId(task.getId()));

        TaskReadDTO dto = TaskReadDTO.fromEntityWithEverythingForReadAndOrganizationAndSubscription(task);
        if (currentUser != null) {
            dto.subscribed = subscriberRepo.exists(currentUser.getId(), taskId);
        }
        dto.subscribersCount = subscriberRepo.countByTaskId(taskId);
        return dto;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public TaskReadDTO get(User currentUser, Long taskId) throws ApplicationException {
        // security
        securityService.authzCanReadTask(currentUser, taskId);
        // security

        return getDTO(currentUser, taskId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public TaskReadDTO getByProjectAndNumber(User currentUser, IdOrUiIdDTO projectDTO, Long number)
            throws ApplicationException {
        // security
        Long taskId = securityService.authzCanReadTask(currentUser, projectDTO, number);
        // security

        if (taskId == null) {
            throw new NotFoundException("Task");
        }
        return getDTO(currentUser, taskId);
    }

    /*@formatter:off
    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskReadDTO> listByProject(User currentUser, IdOrUiIdDTO projectDTO) {
        // security
        securityService.authzCanReadTasks(currentUser, projectDTO);
        // security

        List<Task> taskList = jpaTaskRepo.listByProjectIdOrUiIdAndFetchTypeAndPriorityAndStatus(projectDTO.id, projectDTO.uiId);
        return TaskReadDTO.fromEntities(taskList);
    }

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskReadDTO> listAssignedToCurrentUserByProject(User currentUser, IdOrUiIdDTO projectDTO) {
        // security
        securityService.authzCanReadTasks(currentUser, projectDTO);
        // security

        List<Task> taskList = jpaTaskRepo.listTopByProjectIdOrUiIdAndAssigneeUserAndFetchEverythingForRead(
                projectDTO.id, projectDTO.uiId,
                currentUser, LIST_BY_PROJECT_MAX_RESULTS);

        return TaskReadDTO.fromEntitiesWithProject(taskList);
    }
    @formatter:on*/

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskReadDTO> listParentOptions(User currentUser, IdOrUiIdDTO projectDTO) {
        // security
        securityService.authzCanReadTasks(currentUser, projectDTO);
        // security

        List<Task> taskList = new ArrayList<Task>();
        if (projectDTO.id != null) {
            taskList = taskRepo.listAliveNonHiddenNonChildByProjectId(projectDTO.id);
        } else if (projectDTO.uiId != null) {
            taskList = taskRepo.listAliveNonHiddenNonChildByProjectUiId(projectDTO.uiId);
        }
        return TaskReadDTO.fromEntitiesWithoutOtherFields(taskList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<LinkedTaskDTO> listLinkOptionsByTaskId(User currentUser, Long taskId) {
        // security
        securityService.authzCanReadTask(currentUser, taskId);
        // security

        Long projectId = taskRepo.getProjectIdById(taskId);
        if (projectId == null) {
            return new ArrayList<LinkedTaskDTO>();
        }
        List<Task> taskList = taskRepo.listAliveNonHiddenByProjectIdAndNotLinkedTaskId(projectId, taskId);
        return LinkedTaskDTO.fromEntities(taskList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<LinkedTaskDTO> listLinkOptionsByProjectId(User currentUser, Long projectId) {
        // security
        securityService.authzCanReadTasks(currentUser, projectId);
        // security

        List<Task> taskList = taskRepo.listAliveNonHiddenByProjectId(projectId);
        return LinkedTaskDTO.fromEntities(taskList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskReadDTO> listCardless(User currentUser, IdOrUiIdDTO projectDTO) {
        // security
        securityService.authzCanReadTasks(currentUser, projectDTO);
        // security

        List<Task> taskList = new ArrayList<Task>();
        if (projectDTO.id != null) {
            taskList = taskRepo.listAliveCardlessByProjectId(projectDTO.id);
        } else if (projectDTO.uiId != null) {
            taskList = taskRepo.listAliveCardlessByProjectUiId(projectDTO.uiId);
        }
        return TaskReadDTO.fromEntitiesWithoutTimestamps(taskList);
    }

    private Long getOrganizationMemberIdByUsernameAndProject(User currentUser, IdOrUiIdDTO projectDTO) {
        if (projectDTO.id != null) {
            return jdbcOrganizationMemberRepo.getIdByUserIdAndProjectId(currentUser.getId(), projectDTO.id);
        }
        if (projectDTO.uiId != null) {
            return jdbcOrganizationMemberRepo.getIdByUserIdAndProjectUiId(currentUser.getId(), projectDTO.uiId);
        }
        return null;
    }

    @Deprecated
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskReadDTO> search(User currentUser, TaskSearchContext searchContext) {
        IdOrUiIdDTO projectDTO = (IdOrUiIdDTO) searchContext.projectDTO;
        // security
        securityService.authzCanReadTasks(currentUser, projectDTO);
        // security

        if (TaskSearchContext.REPORTED_BY_ME_ID.equals(searchContext.reporterId) ||
                TaskSearchContext.ASSIGNED_TO_ME_ID.equals(searchContext.assigneeId)) {
            Long currentUserId = getOrganizationMemberIdByUsernameAndProject(currentUser, projectDTO);
            if (TaskSearchContext.REPORTED_BY_ME_ID.equals(searchContext.reporterId)) {
                searchContext.reporterId = currentUserId;
            }
            if (TaskSearchContext.ASSIGNED_TO_ME_ID.equals(searchContext.assigneeId)) {
                searchContext.assigneeId = currentUserId;
            }
        }

        List<Task> taskList = taskRepo.search(searchContext);
        return TaskReadDTO.fromEntitiesWithEverythingForRead(taskList);
    }

    @Override
    public PaginatedListDTO<SearchedTaskDTO> textSearch(User currentUser, TaskSearchContext searchContext) throws Exception {
        IdOrUiIdDTO projectDTO = (IdOrUiIdDTO) searchContext.projectDTO;
        // security
        securityService.authzCanReadTasks(currentUser, projectDTO);
        // security

        if (TaskSearchContext.REPORTED_BY_ME_ID.equals(searchContext.reporterId) ||
                TaskSearchContext.ASSIGNED_TO_ME_ID.equals(searchContext.assigneeId)) {
            Long currentUserId = getOrganizationMemberIdByUsernameAndProject(currentUser, projectDTO);
            if (TaskSearchContext.REPORTED_BY_ME_ID.equals(searchContext.reporterId)) {
                searchContext.reporterId = currentUserId;
            }
            if (TaskSearchContext.ASSIGNED_TO_ME_ID.equals(searchContext.assigneeId)) {
                searchContext.assigneeId = currentUserId;
            }
        }

        Long projectId = searchContext.projectDTO.getId();
        if (projectId == null) {
            projectId = jdbcProjectRepo.getProjectIdByUiId(searchContext.projectDTO.getUiId());
        }
        if (projectId == null) {
            return new PaginatedListDTO<SearchedTaskDTO>(new ArrayList<SearchedTaskDTO>(), 0L);
        }

        searchContext.excludeCards = cardRepo.listIdsByProjectIdAndStatus(projectId, TaskCard.Status.STOPPED);

        SearchHits hits = elasticsearchService.search(projectId, searchContext, searchSize);
        return new PaginatedListDTO<SearchedTaskDTO>(SearchedTaskDTO.fromHits(hits), hits.getTotalHits().value);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskType> listTaskTypes() {
        return taskTypeRepo.list();
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskPriority> listTaskPriorities() {
        return taskPriorityRepo.listAndOrderDescByImportanceDesc();
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskStatus> listTaskStatuses() {
        return taskStatusRepo.list();
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ChatPermissionsDTO getChatToken(User currentUser, long taskId) throws Exception {
        // security
        securityService.authzCanReadTask(currentUser, taskId);
        // security

        Task task = taskRepo.getByIdForChatAndFetchProjectAndOrganization(taskId);
        if (task == null) {
            throw new NotFoundException("Task");
        }

        TaskChatTokenData tokenData = new TaskChatTokenData();
        tokenData.chatType = ChatType.TASK;
        tokenData.chatId = task.getId();

        if (currentUser == null) {
            tokenData.write = false;
        } else {
            tokenData.senderId = currentUser.getId();
            try {
                securityService.authzCanUpdateTask(currentUser, taskId);
                tokenData.write = true;
            } catch (SecurityException e) {
                tokenData.write = false;
            }
        }

        if (tokenData.write) {
            tokenData.senderUsername = currentUser.getUsername();
            tokenData.senderFullName = currentUser.getFullName();

            Project project = task.getProject();
            tokenData.projectId = project.getId();
            tokenData.projectUiId = project.getUiId();
            tokenData.projectName = project.getName();

            Organization organization = project.getOrganization();
            tokenData.organizationId = organization.getId();
            tokenData.organizationUiId = organization.getUiId();

            tokenData.taskNumber = task.getNumber();
        }

        long now = System.currentTimeMillis();
        tokenData.notBefore = now - chatTokenNotBeforeToleranceSeconds * 1000;
        tokenData.notOnOrAfter = now + chatTokenLifetimeSeconds * 1000;

        TokenAndIvDTO dto = tokenGenerator.encrypt(tokenData.toJson());
        return new ChatPermissionsDTO(dto, true, tokenData.write);
    }
}
