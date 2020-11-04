package org.wrkr.clb.statistics.services.project;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.message.statistics.NewTaskMessage;
import org.wrkr.clb.statistics.repo.project.NewTaskRepo;
import org.wrkr.clb.statistics.services.BaseEventService;


@Service
public class NewTaskService extends BaseEventService {

    @Autowired
    private NewTaskRepo newTaskRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.NEW_TASK;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        newTaskRepo.save((Long) requestBody.get(NewTaskMessage.PROJECT_ID),
                (String) requestBody.get(NewTaskMessage.PROJECT_NAME),
                (Long) requestBody.get(NewTaskMessage.TASK_ID),
                (Long) requestBody.get(NewTaskMessage.CREATED_AT),
                (String) requestBody.get(NewTaskMessage.TYPE),
                (String) requestBody.get(NewTaskMessage.PRIORITY),
                (String) requestBody.get(NewTaskMessage.STATUS));
    }
}
