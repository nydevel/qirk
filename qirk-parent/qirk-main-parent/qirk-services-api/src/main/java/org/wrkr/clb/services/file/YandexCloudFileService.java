package org.wrkr.clb.services.file;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.task.Task;


@Validated
public interface YandexCloudFileService extends FileService {

    public String generatePathPrefix(Task task);
}
