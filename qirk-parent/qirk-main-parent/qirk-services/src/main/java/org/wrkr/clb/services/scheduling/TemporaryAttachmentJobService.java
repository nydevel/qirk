package org.wrkr.clb.services.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.wrkr.clb.services.project.task.attachment.TemporaryAttachmentService;


@Component("temporaryAttachmentJobService")
@EnableScheduling
public class TemporaryAttachmentJobService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TemporaryAttachmentJobService.class);

    @Autowired
    private TemporaryAttachmentService attachmentService;

    public void clearTemporaryAttachments() {
        attachmentService.clearTemporaryAttachments();
    }
}
