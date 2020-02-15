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
package org.wrkr.clb.elasticsearch.datasync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.elasticsearch.datasync.services.DatabaseService;
import org.wrkr.clb.model.BaseEntity;
import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.InviteStatusRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchService;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchTaskService;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.api.elasticsearch.impl.DefaultElasticsearchTaskService;
import org.wrkr.clb.services.api.elasticsearch.impl.DefaultElasticsearchUserService;

public class DataSynchronizer {

    private static final Logger LOG = LoggerFactory.getLogger(DataSynchronizer.class);

    private static final String USER = DefaultElasticsearchUserService.INDEX;
    private static final String TASK = DefaultElasticsearchTaskService.INDEX;

    @Autowired
    private InviteStatusRepo inviteStatusRepo;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ElasticsearchUserService userService;

    @Autowired
    private ElasticsearchTaskService taskService;

    @Autowired
    protected RestHighLevelClient client;

    public void synchronize(String[] entityNames) {
        if (entityNames.length == 0) {
            synchronizeAll();
        }

        for (String entityName : entityNames) {
            switch (entityName.strip().toLowerCase()) {
                case USER:
                    synchronizeUsers();
                    break;
                case TASK:
                    synchronizeTasks();
                    break;
            }
        }

        try {
            client.close();
        } catch (Exception e) {
            LOG.error("Could not close elasticsearch client", e);
        }
    }

    private void logElasticsearchUpdateError(BaseEntity entity, Exception e) {
        if (e instanceof ResponseException) {
            ResponseException responseError = (ResponseException) e;
            LOG.error("Elasticsearch response: " + responseError.getResponse().toString());
        }
        LOG.error("Could not update entity " + entity + " in elasticsearch", e);
    }

    private void logElasticsearchDeleteError(Class<?> entityClass, String id, Exception e) {
        if (e instanceof ResponseException) {
            ResponseException responseError = (ResponseException) e;
            LOG.error("Elasticsearch response: " + responseError.getResponse().toString());
        }
        LOG.error("Could not delete entity " + entityClass.getSimpleName() + " with id " + id + " from elasticsearch", e);
    }

    private <E extends BaseIdEntity, S extends ElasticsearchService<E>> Set<String> listIds(S service) {
        try {
            return service.getIds();
        } catch (Exception e) {
            if (e instanceof ResponseException) {
                ResponseException responseError = (ResponseException) e;
                LOG.error("Elasticsearch response: " + responseError.getResponse().toString());
            }
            LOG.error("Could not execute listIds method in " + service.getClass().getSimpleName() + " service", e);
            return new HashSet<String>(0);
        }
    }

    private void synchronizeAll() {
        synchronizeUsers();
        synchronizeTasks();
    }

    public void synchronizeUsers() {
        LOG.info("Synchronizing users: start");

        InviteStatus pendingStatus = inviteStatusRepo.getByNameCode(InviteStatus.Status.PENDING);

        List<String> databaseIds = new ArrayList<String>();
        for (User user : databaseService.getAllUsers()) {
            databaseIds.add(user.getId().toString());
            try {
                userService.datasync(user);
                userService.setProjects(user);
                userService.setInvitedProjects(user.getId(), databaseService.getInvitedProjectIdsByUser(user, pendingStatus));
            } catch (Exception e) {
                logElasticsearchUpdateError(user, e);
            }
        }

        Set<String> elasticsearchIds = listIds(userService);
        elasticsearchIds.removeAll(databaseIds);
        for (String id : elasticsearchIds) {
            try {
                userService.delete(id);
            } catch (Exception e) {
                logElasticsearchDeleteError(User.class, id, e);
            }
        }

        LOG.info("Synchronizing users: finish");
    }

    public void synchronizeTasks() {
        LOG.info("Synchronizing tasks: start");

        List<String> databaseIds = new ArrayList<String>();
        for (Task task : databaseService.getAllTasks()) {
            databaseIds.add(task.getId().toString());
            try {
                taskService.datasync(task);
            } catch (Exception e) {
                logElasticsearchUpdateError(task, e);
            }
        }

        Set<String> elasticsearchIds = listIds(taskService);
        elasticsearchIds.removeAll(databaseIds);
        for (String id : elasticsearchIds) {
            try {
                taskService.delete(id);
            } catch (Exception e) {
                logElasticsearchDeleteError(Task.class, id, e);
            }
        }

        LOG.info("Synchronizing tasks: finish");
    }
}
