package org.wrkr.clb.statistics.services.project;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.message.statistics.ProjectDocUpdateMessage;
import org.wrkr.clb.statistics.repo.project.ProjectDocUpdateRepo;
import org.wrkr.clb.statistics.services.BaseEventService;

@Service
public class ProjectDocUpdateService extends BaseEventService {

    @Autowired
    private ProjectDocUpdateRepo projectDocUpdateRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.PROJECT_DOC_UPDATE;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        projectDocUpdateRepo.save((Long) requestBody.get(ProjectDocUpdateMessage.UPDATED_BY_USER_ID));
    }
}
