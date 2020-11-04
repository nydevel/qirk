package org.wrkr.clb.notification.services.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.wrkr.clb.common.mail.DevOpsMailService;
import org.wrkr.clb.notification.repo.NotifDatabaseRepo;


@Component("notifSelfCheckJobService")
@EnableScheduling
public class SelfCheckJobService {

    @Autowired
    private DevOpsMailService mailService;

    @Autowired
    private NotifDatabaseRepo notifRepo;

    public void checkNotifDatabase() {
        try {
            notifRepo.check();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("clb_notif database", e);
        }
    }
}
