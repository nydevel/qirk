package org.wrkr.clb.statistics.services.chat;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;
import org.wrkr.clb.common.jms.message.statistics.NewCommentMessage;
import org.wrkr.clb.statistics.repo.chat.NewMessageRepo;
import org.wrkr.clb.statistics.services.BaseEventService;

@Service
public class NewMessageService extends BaseEventService {

    @Autowired
    private NewMessageRepo newMessageRepo;

    @Override
    public String getCode() {
        return BaseStatisticsMessage.Code.NEW_COMMENT;
    }

    @Override
    @Transactional(value = "statTransactionManager", rollbackFor = Throwable.class)
    public void onMessage(Map<String, Object> requestBody) {
        newMessageRepo.save((String) requestBody.get(NewCommentMessage.OWNER_TYPE),
                (Long) requestBody.get(NewCommentMessage.OWNER_ID),
                (Long) requestBody.get(NewCommentMessage.CREATED_AT));
    }
}
