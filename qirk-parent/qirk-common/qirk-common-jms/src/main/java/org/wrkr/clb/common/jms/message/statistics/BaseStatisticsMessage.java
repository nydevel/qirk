package org.wrkr.clb.common.jms.message.statistics;

import org.wrkr.clb.common.util.strings.JSONifiable;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseStatisticsMessage implements JSONifiable {

    public static class Code {
        public static final String PROJECT_DOC_UPDATE = "STAT_PROJECT_DOC_UPDATE";
        public static final String NEW_TASK = "STAT_NEW_TASK";
        public static final String TASK_UPDATE = "STAT_TASK_UPDATE";
        public static final String NEW_MEMO = "STAT_NEW_MEMO";
        public static final String NEW_COMMENT = "STAT_NEW_COMMENT";
    }

    public static final String CODE = "code";

    @JsonProperty(value = CODE)
    public String code;

    public BaseStatisticsMessage(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return getClass() + " [code=" + code + "]";
    }
}
