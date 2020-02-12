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
package org.wrkr.clb.common.mail;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.wrkr.clb.common.util.strings.CharSet;

public abstract class BaseMailService implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(BaseMailService.class);

    private static final String CONTENT_TYPE_TEXT_HTML = MediaType.TEXT_HTML_VALUE + ";charset=" + CharSet.UTF8;

    protected static final String HTML_TEMPLATES_DIR = "message-templates/";

    // email config values
    private String emailHost;
    private Integer emailPort;
    private String emailHostUser;
    private String emailHostPassword;
    private String emailFrom;

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public void setEmailPort(Integer emailPort) {
        this.emailPort = emailPort;
    }

    public void setEmailHostUser(String emailHostUser) {
        this.emailHostUser = emailHostUser;
    }

    public void setEmailHostPassword(String emailHostPassword) {
        this.emailHostPassword = emailHostPassword;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    protected JavaMailSenderImpl mailSender;
    protected VelocityEngine velocityEngine;

    @Override
    public void afterPropertiesSet() {
        // instantiate mail sender
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);
        mailSender.setUsername(emailHostUser);
        mailSender.setPassword(emailHostPassword);

        Properties maiLSenderProps = mailSender.getJavaMailProperties();
        maiLSenderProps.put("mail.transport.protocol", "smtp");
        maiLSenderProps.put("mail.smtp.auth", "true");
        maiLSenderProps.put("mail.smtp.starttls.enable", "true");
        maiLSenderProps.put("mail.debug", "true");

        // instantiate velocity engine
        Properties velocityEngineProps = new Properties();
        velocityEngineProps.setProperty("resource.loader", "class");
        velocityEngineProps.setProperty("contentType", CONTENT_TYPE_TEXT_HTML);
        velocityEngineProps.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngineProps.setProperty("input.encoding", CharSet.UTF8_UPPER);
        velocityEngineProps.setProperty("output.encoding", CharSet.UTF8_UPPER);

        velocityEngine = new VelocityEngine(velocityEngineProps);
    }

    private VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    protected String renderTemplate(String templatePath, VelocityContext velocityContext) {
        long startTime = System.currentTimeMillis();

        StringWriter stringWriter = new StringWriter();
        getVelocityEngine().mergeTemplate(templatePath, CharSet.UTF8_UPPER, velocityContext, stringWriter);
        String result = stringWriter.toString();

        if (LOG.isInfoEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.info("processed renderTemplate for template " + templatePath + " in " +
                    resultTime + " ms");
        }

        return result;
    }

    protected String renderHTMLTemplate(String templatePath, String body) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("body", body);
        return renderTemplate(templatePath, velocityContext);
    }

    private void _sendEmail(String from, String to, String subject, String body) throws AddressException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        message.setSubject(subject);
        message.setContent(body, CONTENT_TYPE_TEXT_HTML);

        mailSender.send(message);
    }

    protected void _sendEmail(String to, String subject, String body) throws AddressException, MessagingException {
        _sendEmail(emailFrom, to, subject, body);
    }

    protected void sendEmail(String from, String to, String subject, String body) throws AddressException, MessagingException {
        long startTime = System.currentTimeMillis();

        _sendEmail(from, to, subject, body);

        if (LOG.isInfoEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.info("processed sendEmail for subject " + subject + " in " + resultTime + " ms");
        }
    }

    protected void sendEmail(String to, String subject, String body) throws Exception {
        sendEmail(emailFrom, to, subject, body);
    }

    protected void sendEmail(Collection<String> recipients, String subject, String body)
            throws AddressException, MessagingException {
        long startTime = System.currentTimeMillis();

        for (String to : recipients) {
            _sendEmail(to, subject, body);
        }

        if (LOG.isInfoEnabled()) {
            long resultTime = System.currentTimeMillis() - startTime;
            LOG.info("processed sendEmail for subject " + subject + " in " + resultTime + " ms");
        }
    }
}
