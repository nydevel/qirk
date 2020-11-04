package org.wrkr.clb.chat.services.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.repo.sql.SQLChatDatabaseRepo;
import org.wrkr.clb.common.mail.DevOpsMailService;

@Component("chatSelfCheckJobService")
@EnableScheduling
public class SelfCheckJobService {

    @Autowired
    private DevOpsMailService mailService;

    @Autowired
    private SQLChatDatabaseRepo chatRepo;

    public void checkChat() {
        try {
            chatRepo.check();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("clb_chat database", e);
        }
    }
}
