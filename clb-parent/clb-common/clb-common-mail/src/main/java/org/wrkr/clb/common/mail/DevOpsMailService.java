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

    private static final String MESSAGE_TEMPLATES_DIR = "message-templates/to-devops/";

    // email subjects
    private static class Subject {
        private static final String SERVER_OK = "[QIRK INFO] Server is up in %s environment";
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

    void _sendServerOKEmail() throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("environment", environment);
        velocityContext.put("nodeId", nodeId);

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "server-ok-en.vm", velocityContext);

        sendEmailToDevOps(String.format(Subject.SERVER_OK, environment), body);
    }

    public void sendServerOKEmail() {
        try {
            _sendServerOKEmail();
        } catch (Exception e) {
            LOG.error("Could not send server OK email", e);
        }
    }

    void _sendResourceFailedEmail(String resourceName, Exception resourceException) throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("environment", environment);
        velocityContext.put("nodeId", nodeId);
        velocityContext.put("resourceName", resourceName);
        velocityContext.put("exceptionMessage", ExceptionUtils.getFullStackTrace(resourceException));
        velocityContext.put("timestamp", DateTimeUtils.now().format(DateTimeUtils.WEB_DATETIME_FORMATTER));

        String body = renderTemplate(MESSAGE_TEMPLATES_DIR + "resource-failed-en.vm", velocityContext);

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
