package org.wrkr.clb.common.jms.services;

import org.wrkr.clb.common.jms.message.statistics.BaseStatisticsMessage;

@SuppressWarnings("unused")
public class StatisticsSenderStub implements StatisticsSender {

    @Override
    public void send(BaseStatisticsMessage message) {
    }
}
