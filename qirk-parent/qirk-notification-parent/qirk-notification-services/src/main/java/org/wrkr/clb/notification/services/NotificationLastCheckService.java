package org.wrkr.clb.notification.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.notification.model.NotificationLastCheck;
import org.wrkr.clb.notification.repo.NotificationLastCheckRepo;
import org.wrkr.clb.notification.services.dto.LastCheckDTO;


@Service
public class NotificationLastCheckService {

    @Autowired
    private NotificationLastCheckRepo notificationLastCheckRepo;

    public LastCheckDTO updateLastCheckTimestamp(Long subscriberId) {
        if (subscriberId == null) {
            return null;
        }

        long lastCheckTimestamp = System.currentTimeMillis();
        NotificationLastCheck lastCheck = new NotificationLastCheck();
        lastCheck.setSubscriberId(subscriberId);
        lastCheck.setLastCheckTimestamp(lastCheckTimestamp);

        boolean updated = false;
        if (notificationLastCheckRepo.exists(subscriberId)) {
            updated = notificationLastCheckRepo.update(lastCheck);
        } else {
            updated = notificationLastCheckRepo.save(lastCheck);
        }

        if (updated) {
            return new LastCheckDTO(lastCheckTimestamp);
        }
        return getLastCheckTimestamp(subscriberId);
    }

    public LastCheckDTO getLastCheckTimestamp(Long subscriberId) {
        if (subscriberId == null) {
            return null;
        }

        Long lastCheck = notificationLastCheckRepo.getLastCheckTimestampBySubscriberId(subscriberId);
        return new LastCheckDTO(lastCheck);
    }
}
