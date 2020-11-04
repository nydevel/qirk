package org.wrkr.clb.common.jms.services;

import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;

public interface StatisticsSender {

    public void send(BaseStatisticsMessage message);
}
