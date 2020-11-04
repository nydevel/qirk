package org.wrkr.clb.services;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wrkr.clb.common.crypto.HashEncoder;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.ApplicationStatus;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.model.project.ProjectInvite;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.roadmap.Road;
import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.test.repo.JDBCTestRepo;
import org.wrkr.clb.test.repo.TestRepo;

@ContextConfiguration({ "classpath:qirk-services-test-root-ctx.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseServiceTest {

    protected static final String DEFAULT_USER_PASSWORD = "password";
    protected static final String DEFAULT_PROJECT_KEY = "key";

    @Autowired
    protected TestRepo testRepo;

    @Autowired
    protected JDBCTestRepo jdbcTestRepo;

    protected User saveUser(String email)
            throws Exception {
        return saveUser(email, DEFAULT_USER_PASSWORD);
    }

    protected User saveUser(String email, boolean manager)
            throws Exception {
        return saveUser(email.split("@")[0].toLowerCase(), email, DEFAULT_USER_PASSWORD, manager);
    }

    protected User saveUser(String email, String password)
            throws Exception {
        return saveUser(email.split("@")[0].toLowerCase(), email, password);
    }

    protected User saveUser(String username, String email, String password)
            throws Exception {
        return saveUser(username, email, password, false);
    }

    protected User saveUser(String username, String email, String password, boolean manager) throws Exception {
        User user = new User();

        user.setUsername(username);
        user.setEmailAddress(email);
        user.setPasswordHash(HashEncoder.encryptToHex(password));
        user.setCreatedAt(DateTimeUtils.now());
        user.setFullName(username);
        user.setManager(manager);

        testRepo.persistEntity(user);
        return user;
    }

    protected Project saveProject(User owner, String name, String uiId, boolean isPrivate) {
        ProjectTaskNumberSequence taskNumberSequence = new ProjectTaskNumberSequence();
        testRepo.persistEntity(taskNumberSequence);

        Project project = new Project();

        project.setTaskNumberSequence(taskNumberSequence);
        project.setOwner(owner);
        project.setName(name);
        project.setUiId(uiId);
        project.setKey(DEFAULT_PROJECT_KEY);
        project.setPrivate(isPrivate);
        project.setDescriptionMd("");
        project.setDescriptionHtml("");
        project.setDocumentationMd("");
        project.setDocumentationHtml("");
        testRepo.persistEntity(project);

        saveProjectMember(owner, project, true, true);

        return project;
    }

    protected ProjectInvite saveProjectInvite(User user, Project project, User sender, InviteStatus status) {
        ProjectInvite invite = new ProjectInvite();

        invite.setUser(user);
        invite.setProject(project);
        invite.setSender(sender);
        invite.setStatus(status);
        invite.setText("");

        OffsetDateTime now = DateTimeUtils.now();
        invite.setCreatedAt(now);
        invite.setUpdatedAt(now);

        testRepo.persistEntity(invite);
        return invite;
    }

    protected GrantedPermissionsProjectInvite saveGrantedPermissionsProjectInvite(
            User user, Project project, User sender, InviteStatus status) {
        GrantedPermissionsProjectInvite invite = new GrantedPermissionsProjectInvite();

        invite.setUser(user);
        invite.setProject(project);
        invite.setSender(sender);
        invite.setStatus(status);
        invite.setText("");

        OffsetDateTime now = DateTimeUtils.now();
        invite.setCreatedAt(now);
        invite.setUpdatedAt(now);

        testRepo.persistEntity(invite);
        return invite;
    }

    protected ProjectApplication saveProjectApplication(User user, Project project, ApplicationStatus status) {
        ProjectApplication application = new ProjectApplication();

        application.setUser(user);
        application.setProject(project);
        application.setStatus(status);
        application.setText("");

        OffsetDateTime now = DateTimeUtils.now();
        application.setCreatedAt(now);
        application.setUpdatedAt(now);

        testRepo.persistEntity(application);
        return application;
    }

    protected ProjectMember saveProjectMember(User user, Project project,
            boolean manager, boolean writeAllowed) {
        ProjectMember member = new ProjectMember();

        member.setUser(user);
        member.setProject(project);
        member.setManager(manager);
        member.setWriteAllowed(writeAllowed);
        member.setHiredAt(DateTimeUtils.now());

        testRepo.persistEntity(member);
        return member;
    }

    protected Road saveRoad(Project project, String name) {
        Road road = new Road();

        road.setProject(project);
        road.setName(name);

        testRepo.persistEntity(road);
        return road;
    }

    protected TaskCard saveTaskCard(Road road, String name) {
        TaskCard card = new TaskCard();

        card.setProject(road.getProject());
        card.setRoad(road);
        card.setName(name);
        card.setCreatedAt(DateTimeUtils.now());

        testRepo.persistEntity(card);
        return card;
    }

    protected Task createTask(ProjectMember reporter, ProjectMember assignee,
            TaskType type, TaskPriority priority, TaskStatus status, TaskCard card) {
        Task task = new Task();

        task.setReporter(reporter);
        task.setAssignee(assignee);
        task.setType(type);
        task.setPriority(priority);
        task.setStatus(status);
        task.setCard(card);
        task.setDescriptionMd("");
        task.setDescriptionHtml("");
        task.setSummary("");

        OffsetDateTime now = DateTimeUtils.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        return task;
    }

    protected void saveTasks(Project project, List<Task> tasks) {
        ProjectTaskNumberSequence taskNumberSequence = project.getTaskNumberSequence();
        long nextTaskNumber = taskNumberSequence.getNextTaskNumber();

        for (Task task : tasks) {
            task.setProject(project);
            task.setNumber(nextTaskNumber);
            nextTaskNumber++;
            testRepo.persistEntity(task);
        }

        taskNumberSequence.setNextTaskNumber(nextTaskNumber);
        taskNumberSequence = testRepo.mergeEntity(taskNumberSequence);
    }

    protected Issue saveIssue(Project project, User reporter) {
        Issue issue = new Issue();

        issue.setProject(project);
        issue.setReporter(reporter);
        issue.setSummary("");
        issue.setDescription("");
        issue.setCreatedAt(DateTimeUtils.now());

        testRepo.persistEntity(issue);
        return issue;
    }

    protected Memo saveMemo(Project project, ProjectMember author) {
        Memo memo = new Memo();

        memo.setProject(project);
        memo.setAuthor(author);
        memo.setBody("");
        memo.setCreatedAt(DateTimeUtils.now());

        testRepo.persistEntity(memo);
        return memo;
    }

    protected TaskHashtag saveTaskHashtag(Project project, String name) {
        TaskHashtag hashtag = new TaskHashtag();

        hashtag.setProject(project);
        hashtag.setName(name);

        testRepo.persistEntity(hashtag);
        return hashtag;
    }
}
