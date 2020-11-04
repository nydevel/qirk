package org.wrkr.clb.services.project.task.attachment.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.strings.ExtStringUtils;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.attachment.TemporaryAttachment;
import org.wrkr.clb.repo.project.task.attachment.TemporaryAttachmentRepo;
import org.wrkr.clb.services.api.yandexcloud.YandexCloudApiService;
import org.wrkr.clb.services.project.task.attachment.TemporaryAttachmentService;

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
    private YandexCloudApiService yandexCloudService;

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public String create(String externalPath, Project project) {
        String uuid = generateUuid();
        while (attachmentRepo.existsByUuid(uuid)) {
            uuid = generateUuid();
        }

        TemporaryAttachment attachment = new TemporaryAttachment();

        attachment.setUuid(uuid);
        attachment.setFilename(ExtStringUtils.substringFromLastSymbol(externalPath, '/'));
        attachment.setPath(externalPath);
        attachment.setProject(project);
        attachment.setCreatedAt(System.currentTimeMillis());

        attachmentRepo.save(attachment);
        return uuid;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void clearTemporaryAttachments() {
        long clearUntil = System.currentTimeMillis() - (1000 * temporaryAttachmentLifetimeSeconds);
        List<TemporaryAttachment> attachmentsToDelete = attachmentRepo.listByCreatedAtUntil(clearUntil);

        List<String> yandexCloudPaths = new ArrayList<String>(attachmentsToDelete.size());
        for (TemporaryAttachment attachment : attachmentsToDelete) {
            yandexCloudPaths.add(attachment.getPath());
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
