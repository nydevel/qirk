package org.wrkr.clb.services.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.wrkr.clb.common.mail.DevOpsMailService;
import org.wrkr.clb.repo.MainDatabaseRepo;
import org.wrkr.clb.repo.auth.AuthDatabaseRepo;
import org.wrkr.clb.services.jms.JMSCheckService;

@Component("mainSelfCheckJobService")
@EnableScheduling
public class SelfCheckJobService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SelfCheckJobService.class);

    @Autowired
    private DevOpsMailService mailService;

    @Autowired
    private MainDatabaseRepo mainRepo;

    @Autowired
    private AuthDatabaseRepo authRepo;

    @Autowired
    private JMSCheckService jmsService;

    public void checkPostgresMain() {
        try {
            mainRepo.check();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("PostgreSQL clb database", e);
        }
    }

    public void checkPostgresAuth() {
        try {
            authRepo.check();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("PostgreSQL clb_auth database", e);
        }
    }

    public void checkActiveMQQueue() {
        try {
            jmsService.checkQueue();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("ActiveMQ queue", e);
        }
    }

    public void checkActiveMQTopic() {
        try {
            jmsService.checkTopic();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("ActiveMQ topic", e);
        }
    }
}
