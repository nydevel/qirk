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
package org.wrkr.clb.services.file.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.strings.ExtStringUtils;
import org.wrkr.clb.model.project.DropboxSettings;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.TemporaryAttachment;
import org.wrkr.clb.repo.project.task.TemporaryAttachmentRepo;
import org.wrkr.clb.services.api.dropbox.DropboxApiService;
import org.wrkr.clb.services.api.yandexcloud.YandexCloudApiService;
import org.wrkr.clb.services.file.TemporaryAttachmentService;

//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultTemporaryAttachmentService implements TemporaryAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTemporaryAttachmentService.class);

    // config value
    private Long temporaryAttachmentLifetimeSeconds;

    public Long getTemporaryAttachmentLifetimeSeconds() {
        return temporaryAttachmentLifetimeSeconds;
    }

    public void setTemporaryAttachmentLifetimeSeconds(Long temporaryAttachmentLifetimeSeconds) {
        this.temporaryAttachmentLifetimeSeconds = temporaryAttachmentLifetimeSeconds;
    }

    @Autowired
    private TemporaryAttachmentRepo attachmentRepo;

    @Autowired
    private DropboxApiService dropboxService;

    @Autowired
    private YandexCloudApiService yandexCloudService;

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public String create(String externalPath, Project project, DropboxSettings dropboxSettings) {
        String uuid = generateUuid();
        while (attachmentRepo.existsByUuid(uuid)) {
            uuid = generateUuid();
        }

        TemporaryAttachment attachment = new TemporaryAttachment();

        attachment.setUuid(uuid);
        attachment.setFilename(ExtStringUtils.substringFromLastSymbol(externalPath, '/'));
        attachment.setPath(externalPath);
        attachment.setProject(project);
        attachment.setDropboxSettings(dropboxSettings);
        attachment.setCreatedAt(System.currentTimeMillis());

        attachmentRepo.save(attachment);
        return uuid;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public String create(String externalPath, Project project) {
        return create(externalPath, project, null);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void clearTemporaryAttachments() {
        long clearUntil = System.currentTimeMillis() - (1000 * temporaryAttachmentLifetimeSeconds);
        List<TemporaryAttachment> attachmentsToDelete = attachmentRepo.listByCreatedAtUntil(clearUntil);

        List<String> yandexCloudPaths = new ArrayList<String>(attachmentsToDelete.size());
        for (TemporaryAttachment attachment : attachmentsToDelete) {
            if (attachment.getDropboxSettings() != null) {
                try {
                    dropboxService.delete(attachment.getDropboxSettings().getToken(), attachment.getPath());
                } catch (Exception e) {
                    LOG.error("Could not delete temporary attachment " + attachment.getPath() +
                            " with dropbox settings " + attachment.getDropboxSettings().getId() + " from Dropbox", e);
                }
            } else {
                yandexCloudPaths.add(attachment.getPath());
            }
        }

        try {
            if (!yandexCloudPaths.isEmpty()) {
                yandexCloudService.deleteFiles(yandexCloudPaths.toArray(new String[0]));
            }
        } catch (Exception e) {
            LOG.error("Could not delete temporary attachments " + yandexCloudPaths + " from YandexCloud", e);
        }

        attachmentRepo.deleteByCreatedAtUntil(clearUntil);
    }
}
