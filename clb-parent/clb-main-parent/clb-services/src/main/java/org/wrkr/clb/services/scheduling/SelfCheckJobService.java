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
package org.wrkr.clb.services.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wrkr.clb.common.mail.DevOpsMailService;
import org.wrkr.clb.repo.MainDatabaseRepo;
import org.wrkr.clb.repo.auth.AuthDatabaseRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchClusterService;
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
    private ElasticsearchClusterService elasticsearchService;

    @Autowired
    private JMSCheckService jmsService;

    @Scheduled(cron = "1 0 12 * * *")
    public void checkServer() {
        mailService.sendServerOKEmail();
    }

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

    public void checkElasticsearch() {
        try {
            elasticsearchService.checkClusterHealth();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("Elasticsearch", e);
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
