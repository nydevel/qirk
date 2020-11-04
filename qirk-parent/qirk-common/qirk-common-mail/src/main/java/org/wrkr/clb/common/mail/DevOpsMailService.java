package org.wrkr.clb.common.mail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;

public class DevOpsMailService extends BaseMailService {

    private static final Logger LOG = LoggerFactory.getLogger(DevOpsMailService.class);

    private static final String DEVOPS_MESSAGE_TEMPLATES_DIR = HTML_TEMPLATES_DIR + "to-devops/";

    // message templates
    private static class MessageTemplate {
        private static final String RESOURCE_FAILED = DEVOPS_MESSAGE_TEMPLATES_DIR + "resource-failed.vm";
    }

    // email subjects
    private static class Subject {
        private static final String RESOURCE_FAILED = "[QIRK WARNING] Resource failed: %s in %s environment";
    }

    // config values
    private List<String> devOpsEmails;
    private String environment;
    private String nodeId;

    public void setDevOpsEmails(String devOpsEmails) {
        this.devOpsEmails = Arrays.asList(devOpsEmails.split(","));
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    private void sendEmailToDevOps(String subject, String body) throws Exception {
        sendEmail(devOpsEmails, subject, body);
    }

    void _sendResourceFailedEmail(String resourceName, Exception resourceException) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("environment", environment);
        velocityContext.put("nodeId", nodeId);
        velocityContext.put("resourceName", resourceName);
        velocityContext.put("exceptionMessage", ExceptionUtils.getFullStackTrace(resourceException));
        velocityContext.put("timestamp", DateTimeUtils.now().format(DateTimeUtils.WEB_DATETIME_FORMATTER));

        String body = renderTemplate(MessageTemplate.RESOURCE_FAILED, velocityContext);

        sendEmailToDevOps(String.format(Subject.RESOURCE_FAILED, resourceName, environment), body);
    }

    public void sendResourceFailedEmail(String resourceName, Exception resourceException) {
        try {
            _sendResourceFailedEmail(resourceName, resourceException);
        } catch (Exception e) {
            LOG.error("Could not send resource failed email", e);
        }
    }
}
