package org.wrkr.clb.statistics.services.project;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.message.statistics.NewMemoMessage;
import org.wrkr.clb.statistics.repo.project.NewMemoRepo;
import org.wrkr.clb.statistics.services.BaseEventService;

@Service
public class NewMemoService extends BaseEventService {

    @Autowired
    private NewMemoRepo newMemoRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.NEW_MEMO;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        newMemoRepo.save((Long) requestBody.get(NewMemoMessage.AUTHOR_USER_ID),
                (Long) requestBody.get(NewMemoMessage.CREATED_AT));
    }
}
