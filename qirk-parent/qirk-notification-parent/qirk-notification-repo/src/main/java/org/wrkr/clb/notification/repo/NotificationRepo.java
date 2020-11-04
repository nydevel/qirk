package org.wrkr.clb.notification.repo;

import java.util.List;

import org.wrkr.clb.notification.repo.dto.NotificationDTO;

public interface NotificationRepo {

    public boolean save(long subscriberId, long timestamp, String notificationType, String json);

    public List<NotificationDTO> listTopSinceTimestampBySubscriberId(long subscriberId, long timestamp, int limit);
}
