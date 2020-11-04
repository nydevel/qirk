package org.wrkr.clb.common.util.datetime;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DateTimeWithEpochDTO {

    public String iso8601;

    @JsonProperty(value = "epoch_milli")
    public long epochMilli;

    public DateTimeWithEpochDTO(OffsetDateTime datetime) {
        iso8601 = datetime.format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        epochMilli = datetime.toInstant().toEpochMilli();
    }

    public DateTimeWithEpochDTO(long epochMilli) {
        this.epochMilli = epochMilli;
        this.iso8601 = DateTimeUtils.convertEpochMilliToOffsetDateTime(epochMilli).format(DateTimeUtils.WEB_DATETIME_FORMATTER);
    }
}
