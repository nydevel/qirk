package org.wrkr.clb.common.mail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.crypto.token.notification.NotificationSettingsTokenData;
import org.wrkr.clb.common.jms.message.notification.TaskCommentMessage;
import org.wrkr.clb.common.jms.message.notification.TaskUpdateNotificationMessage;
import org.wrkr.clb.common.util.strings.CharSet;
import org.wrkr.clb.common.util.web.FrontURI;

public class UserMailService extends BaseMailService {

    private static final Logger LOG = LoggerFactory.getLogger(UserMailService.class);

    // base html templates
    private static final String DEFAULT_HTML_TEMPLATE = HTML_TEMPLATES_DIR + "default-html-template.vm";
    private static final String NON_REGISTERED_HTML_TEMPLATE = HTML_TEMPLATES_DIR + "non-registered-html-template.vm";
    private static final String SUBSCRIPTION_FOOTER_HTML_TEMPLATE = HTML_TEMPLATES_DIR + "subscription-footer-html-template.vm";

    // message templates
    private static class MessageTemplate {
        private static final String REGISTRATION = HTML_TEMPLATES_DIR + "registration.vm";
        private static final String PASSWORD_RESET = HTML_TEMPLATES_DIR + "password-reset.vm";
        private static final String PROJECT_INVITE = HTML_TEMPLATES_DIR + "project-invite.vm";
        private static final String TASK_CREATED = HTML_TEMPLATES_DIR + "task-created-en";
        private static final String TASK_UPDATED = HTML_TEMPLATES_DIR + "task-updated-en";
        private static final String TASK_COMMENTED = HTML_TEMPLATES_DIR + "task-commented-en";
    }

    // email subjects
    private static class Subject {
        private static final String REGISTRATION = "Registration instructions";
        private static final String PASSWORD_RESET = "Password reset instructions";
        private static final String PROJECT_INVITE = "[Qirk] %s has invited you to %s";
        private static final String TASK_NOTIFICATION = "Qirk Report: task #%d in %s";
    }

    // config value
    private String host;

    public void setHost(String host) {
        this.host = host;
    }

    @Autowired
    private TokenGenerator tokenGenerator;

    private TokenAndIvDTO generateNotificationSettingsToken(String userEmail, String notificationSettingsType) throws Exception {
        NotificationSettingsTokenData tokenData = new NotificationSettingsTokenData(userEmail, notificationSettingsType);
        return tokenGenerator.encrypt(tokenData.toJson());
    }

    private String renderSubscriptionFooterHTMLTemplate(String body, TokenAndIvDTO tokenDTO)
            throws UnsupportedEncodingException {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("body", body);
        velocityContext.put("url",
                host + FrontURI.MANAGE_SUBSCRIPTIONS +
                        "?token=" + URLEncoder.encode(tokenDTO.token, CharSet.UTF8_UPPER) +
                        "&iv=" + URLEncoder.encode(tokenDTO.IV, CharSet.UTF8_UPPER));
        return renderTemplate(SUBSCRIPTION_FOOTER_HTML_TEMPLATE, velocityContext);
    }

    @SuppressWarnings("unused")
    private void sendEmailsWithSubscriptionToken(
            Collection<String> recipients, String subject, String body, String notificationSettingsType)
            throws Exception {
        long startTime = System.currentTimeMillis();

        for (String to : recipients) {
            body = renderSubscriptionFooterHTMLTemplate(
                    body, generateNotificationSettingsToken(to, notificationSettingsType));
            _sendEmail(to, subject, body);
        }

        if (LOG.isInfoEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.info("processed sendEmailsWithSubscriptionToken for subject " + subject + " in " + resultTime + " ms");
        }
    }

    public void sendServiceNewsEmail(List<String> recipients, String subject, String body) throws Exception {
        sendEmail(recipients, subject, body);
    }

    public EmailSentDTO sendRegistrationEmail(String emailAddress, String password, String token) {
        try {
            String subject = Subject.REGISTRATION;

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("url", host + FrontURI.ACTIVATE_EMAIL_TOKEN + token);
            velocityContext.put("password", password);

            String body = renderHTMLTemplate(DEFAULT_HTML_TEMPLATE,
                    renderTemplate(MessageTemplate.REGISTRATION, velocityContext));

            sendEmail(emailAddress, subject, body);
            return new EmailSentDTO(true);
        } catch (Exception e) {
            LOG.error("Could not send registration email", e);
            return new EmailSentDTO(false);
        }
    }

    public EmailSentDTO sendPasswordResetEmail(String emailAddress, String token) {
        try {
            String subject = Subject.PASSWORD_RESET;

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("url", host + FrontURI.RESET_PASSWORD + token);

            String body = renderHTMLTemplate(DEFAULT_HTML_TEMPLATE,
                    renderTemplate(MessageTemplate.PASSWORD_RESET, velocityContext));

            sendEmail(emailAddress, subject, body);
            return new EmailSentDTO(true);
        } catch (Exception e) {
            LOG.error("Could not send password reset email", e);
            return new EmailSentDTO(false);
        }
    }

    public EmailSentDTO sendProjectInviteEmail(
            String emailAddress, String senderName, String projectName, String password, String token) {
        try {
            String subject = String.format(Subject.PROJECT_INVITE, senderName, projectName);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("host", host);
            velocityContext.put("acceptUrl", host + FrontURI.ACCEPT_INVITE + token);
            velocityContext.put("senderName", senderName);
            velocityContext.put("projectName", projectName);
            velocityContext.put("password", password);

            String body = renderHTMLTemplate(NON_REGISTERED_HTML_TEMPLATE,
                    renderTemplate(MessageTemplate.PROJECT_INVITE, velocityContext));

            sendEmail(emailAddress, subject, body);
            return new EmailSentDTO(true);
        } catch (Exception e) {
            LOG.error("Could not send password reset email", e);
            return new EmailSentDTO(false);
        }
    }

    public void sendTaskCreatedEmail(List<String> recipients, Map<String, Object> notificationBody) {
        if (recipients.isEmpty()) {
            return;
        }

        try {
            Long taskNumber = (Long) notificationBody.get(TaskUpdateNotificationMessage.TASK_NUMBER);
            String projectName = (String) notificationBody.get(TaskUpdateNotificationMessage.PROJECT_NAME);
            String projectUiId = (String) notificationBody.get(TaskUpdateNotificationMessage.PROJECT_UI_ID);
            String subject = String.format(Subject.TASK_NOTIFICATION, taskNumber, projectName);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("createdByName",
                    ((String) notificationBody.get(TaskUpdateNotificationMessage.UPDATED_BY_FULL_NAME)) + " " +
                            "@" + ((String) notificationBody.get(TaskUpdateNotificationMessage.UPDATED_BY_USERNAME)));
            velocityContext.put("taskNumber", taskNumber);
            velocityContext.put("projectName", projectName);
            velocityContext.put("taskUrl", FrontURI.generateGetTaskURI(host, projectUiId, taskNumber));

            String body = renderTemplate(MessageTemplate.TASK_CREATED, velocityContext);

            sendEmail(recipients, subject, body);
        } catch (Exception e) {
            LOG.error("Could not send task created email", e);
        }
    }

    public void sendTaskUpdatedEmail(List<String> recipients, Map<String, Object> notificationBody) {
        if (recipients.isEmpty()) {
            return;
        }

        try {
            Long taskNumber = (Long) notificationBody.get(TaskUpdateNotificationMessage.TASK_NUMBER);
            String projectName = (String) notificationBody.get(TaskUpdateNotificationMessage.PROJECT_NAME);
            String projectUiId = (String) notificationBody.get(TaskUpdateNotificationMessage.PROJECT_UI_ID);
            String subject = String.format(Subject.TASK_NOTIFICATION, taskNumber, projectName);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("updatedByName",
                    ((String) notificationBody.get(TaskUpdateNotificationMessage.UPDATED_BY_FULL_NAME)) + " " +
                            "@" + ((String) notificationBody.get(TaskUpdateNotificationMessage.UPDATED_BY_USERNAME)));
            velocityContext.put("taskNumber", taskNumber);
            velocityContext.put("projectName", projectName);
            velocityContext.put("taskUrl", FrontURI.generateGetTaskURI(host, projectUiId, taskNumber));

            Long oldAssignee = (Long) notificationBody.get(TaskUpdateNotificationMessage.OLD_ASSIGNEE);
            Long newAssignee = (Long) notificationBody.get(TaskUpdateNotificationMessage.NEW_ASSIGNEE);
            velocityContext.put("assigneeChanged", !Objects.equals(oldAssignee, newAssignee));
            velocityContext.put("newAssignee",
                    ((String) notificationBody.get(TaskUpdateNotificationMessage.NEW_ASSIGNEE_FULL_NAME)) + " " +
                            "@" + ((String) notificationBody.get(TaskUpdateNotificationMessage.NEW_ASSIGNEE_USERNAME)));

            String oldType = (String) notificationBody.get(TaskUpdateNotificationMessage.OLD_TYPE);
            String newType = (String) notificationBody.get(TaskUpdateNotificationMessage.NEW_TYPE);
            velocityContext.put("typeChanged", !newType.equals(oldType));
            velocityContext.put("newType",
                    notificationBody.getOrDefault(TaskUpdateNotificationMessage.NEW_TYPE_HUMAN_READABLE, newType));

            String oldPriority = (String) notificationBody.get(TaskUpdateNotificationMessage.OLD_PRIORITY);
            String newPriority = (String) notificationBody.get(TaskUpdateNotificationMessage.NEW_PRIORITY);
            velocityContext.put("priorityChanged", !newPriority.equals(oldPriority));
            velocityContext.put("newPriority",
                    notificationBody.getOrDefault(TaskUpdateNotificationMessage.NEW_PRIORITY_HUMAN_READABLE, newPriority));

            String oldStatus = (String) notificationBody.get(TaskUpdateNotificationMessage.OLD_STATUS);
            String newStatus = (String) notificationBody.get(TaskUpdateNotificationMessage.NEW_STATUS);
            velocityContext.put("statusChanged", !newStatus.equals(oldStatus));
            velocityContext.put("newStatus",
                    notificationBody.getOrDefault(TaskUpdateNotificationMessage.NEW_STATUS_HUMAN_READABLE, newStatus));

            String body = renderTemplate(MessageTemplate.TASK_UPDATED, velocityContext);

            sendEmail(recipients, subject, body);
        } catch (Exception e) {
            LOG.error("Could not send task updated email", e);
        }
    }

    public void sendTaskCommentedEmail(Collection<String> recipients, Map<String, Object> notificationBody) {
        if (recipients.isEmpty()) {
            return;
        }

        try {
            Long taskNumber = (Long) notificationBody.get(TaskCommentMessage.TASK_NUMBER);
            String projectName = (String) notificationBody.get(TaskCommentMessage.PROJECT_NAME);
            String projectUiId = (String) notificationBody.get(TaskUpdateNotificationMessage.PROJECT_UI_ID);
            String subject = String.format(Subject.TASK_NOTIFICATION, taskNumber, projectName);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("commentedByName",
                    (String) notificationBody.get(TaskCommentMessage.SENDER_FULL_NAME) + "@ " +
                            (String) notificationBody.get(TaskCommentMessage.SENDER_USERNAME));
            velocityContext.put("taskNumber", taskNumber);
            velocityContext.put("projectName", projectName);
            velocityContext.put("taskUrl", FrontURI.generateGetTaskURI(host, projectUiId, taskNumber));
            velocityContext.put("message", (String) notificationBody.get(TaskCommentMessage.MESSAGE));

            String body = renderTemplate(MessageTemplate.TASK_COMMENTED, velocityContext);

            sendEmail(recipients, subject, body);
        } catch (Exception e) {
            LOG.error("Could not send task commented email", e);
        }
    }
}
