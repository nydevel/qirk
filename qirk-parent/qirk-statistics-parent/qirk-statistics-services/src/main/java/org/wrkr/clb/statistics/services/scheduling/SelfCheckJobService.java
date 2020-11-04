package org.wrkr.clb.statistics.services.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.wrkr.clb.common.mail.DevOpsMailService;
import org.wrkr.clb.statistics.repo.StatDatabaseRepo;


@Component("statSelfCheckJobService")
@EnableScheduling
public class SelfCheckJobService {

    @Autowired
    private DevOpsMailService mailService;

    @Autowired
    private StatDatabaseRepo statRepo;

    public void checkStatDatabase() {
        try {
            statRepo.check();
        } catch (Exception e) {
            mailService.sendResourceFailedEmail("clb_stat database", e);
        }
    }
}
