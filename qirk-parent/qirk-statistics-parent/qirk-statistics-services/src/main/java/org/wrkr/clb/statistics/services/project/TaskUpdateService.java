package org.wrkr.clb.statistics.services.project;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.message.statistics.NewTaskMessage;
import org.wrkr.clb.common.jms.message.statistics.TaskUpdateStatisticsMessage;
import org.wrkr.clb.statistics.repo.project.TaskPriorityUpdateRepo;
import org.wrkr.clb.statistics.repo.project.TaskStatusUpdateRepo;
import org.wrkr.clb.statistics.repo.project.TaskTypeUpdateRepo;
import org.wrkr.clb.statistics.repo.project.TaskUpdateRepo;
import org.wrkr.clb.statistics.services.BaseEventService;


@Service
public class TaskUpdateService extends BaseEventService {

    @Autowired
    private TaskUpdateRepo taskUpdateRepo;

    @Autowired
    private TaskTypeUpdateRepo typeUpdateRepo;

    @Autowired
    private TaskPriorityUpdateRepo priorityUpdateRepo;

    @Autowired
    private TaskStatusUpdateRepo statusUpdateRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.TASK_UPDATE;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        Long projectId = (Long) requestBody.get(NewTaskMessage.PROJECT_ID);
        String projectName = (String) requestBody.get(NewTaskMessage.PROJECT_NAME);
        Long taskId = (Long) requestBody.get(TaskUpdateStatisticsMessage.TASK_ID);
        Long updatedAt = (Long) requestBody.get(TaskUpdateStatisticsMessage.UPDATED_AT);
        String type = (String) requestBody.get(TaskUpdateStatisticsMessage.TYPE);
        String priority = (String) requestBody.get(TaskUpdateStatisticsMessage.PRIORITY);
        String status = (String) requestBody.get(TaskUpdateStatisticsMessage.STATUS);

        taskUpdateRepo.save(projectId, projectName, taskId, updatedAt);
        if (type != null) {
            typeUpdateRepo.save(projectId, projectName, taskId, updatedAt, type);
        }
        if (priority != null) {
            priorityUpdateRepo.save(projectId, projectName, taskId, updatedAt, priority);
        }
        if (status != null) {
            statusUpdateRepo.save(projectId, projectName, taskId, updatedAt, status);
        }
    }
}
