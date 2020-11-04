package org.wrkr.clb.services.project.task.attachment.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.web.FrontURI;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.project.task.attachment.AttachmentRepo;
import org.wrkr.clb.services.file.FileService;
import org.wrkr.clb.services.file.YandexCloudFileService;
import org.wrkr.clb.services.project.task.attachment.AttachmentFileService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.http.JsonStatusCode;

//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultAttachmentFileService implements AttachmentFileService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAttachmentFileService.class);

    protected static final String GET_FILE_ERROR_CODE_PARAMETER = "get_file_error_code";

    @Autowired
    private AttachmentRepo attachmentRepo;

    @Autowired
    protected JDBCProjectRepo projectRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private YandexCloudFileService yandexCloudFileService;

    // front url config values
    protected String host;

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public String getTemporaryLink(User currentUser, Long id) {
        Attachment attachment = attachmentRepo.getNotDeletedByIdAndFetchDropboxSettingsAndTask(id);
        if (attachment == null) {
            return null;
        }

        try {
            // security
            securityService.authzCanReadTask(currentUser, attachment.getTask().getId());
            // security
        } catch (SecurityException e) {
            return null;
        }

        FileService fileService = yandexCloudFileService;
        String errorCode = JsonStatusCode.INTERNAL_SERVER_ERROR;
        try {
            return fileService.getTemporaryLink(attachment);
        } catch (Exception e) {
            try {
                errorCode = fileService.getErrorCodeFromException(e);
            } catch (IOException e2) {
                LOG.error("Exception caught at file service", e2);
            }
        }

        Project project = projectRepo.getNameAndUiIdByTaskId(attachment.getTask().getId());
        return FrontURI.generateGetTaskURI(host, project.getUiId(), attachment.getTask().getNumber()) +
                "?" + GET_FILE_ERROR_CODE_PARAMETER + "=" + errorCode;
    }
}
