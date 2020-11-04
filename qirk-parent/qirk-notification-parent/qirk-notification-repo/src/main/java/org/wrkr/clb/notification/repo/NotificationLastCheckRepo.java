package org.wrkr.clb.notification.repo;

import org.wrkr.clb.notification.model.NotificationLastCheck;

public interface NotificationLastCheckRepo {

    public boolean save(NotificationLastCheck lastCheck);

    public boolean exists(long subscriberId);

    public Long getLastCheckTimestampBySubscriberId(Long subscriberId);

    public boolean update(NotificationLastCheck lastCheck);
}
