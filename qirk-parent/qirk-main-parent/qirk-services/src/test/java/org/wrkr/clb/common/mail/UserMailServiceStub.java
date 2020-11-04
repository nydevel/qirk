package org.wrkr.clb.common.mail;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.common.mail.UserMailService;

@SuppressWarnings("unused")
public class UserMailServiceStub extends UserMailService {

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public void sendServiceNewsEmail(List<String> recipients, String subject, String body) {
    }

    @Override
    public EmailSentDTO sendRegistrationEmail(String emailAddress, String password, String token) {
        return new EmailSentDTO(true);
    }

    @Override
    public EmailSentDTO sendPasswordResetEmail(String emailAddress, String token) {
        return new EmailSentDTO(true);
    }

    @Override
    public EmailSentDTO sendProjectInviteEmail(
            String emailAddress, String senderName, String projectName, String password, String token) {
        return new EmailSentDTO(true);
    }

    @Override
    public void sendTaskCreatedEmail(List<String> recipients, Map<String, Object> notificationBody) {
    }

    @Override
    public void sendTaskUpdatedEmail(List<String> recipients, Map<String, Object> notificationBody) {
    }

    @Override
    public void sendTaskCommentedEmail(Collection<String> recipients, Map<String, Object> notificationBody) {
    }
}
