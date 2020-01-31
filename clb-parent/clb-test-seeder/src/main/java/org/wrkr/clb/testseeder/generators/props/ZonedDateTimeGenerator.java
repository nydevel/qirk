package org.wrkr.clb.testseeder.generators.props;

import java.time.Instant;
import java.time.ZoneId;
import java.time.OffsetDateTime;

import org.wrkr.clb.testseeder.generators.BaseGenerator;

/**
 * @author Denis Bilenko
 */
public class OffsetDateTimeGenerator implements BaseGenerator<OffsetDateTime> {
    
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    private LongGenerator longGenerator = LongGenerator.getInstance();
    private long min = 969645600000L;
    private long max = System.currentTimeMillis();

    static OffsetDateTimeGenerator instance = null;
    
    private OffsetDateTimeGenerator() { }

    public OffsetDateTime generate() {
        Long randTimeInMillis = longGenerator.generateWithin(min, max);
        return Instant
               .ofEpochMilli(randTimeInMillis)
               .atZone(UTC_ZONE_ID);
    }
    

    public static OffsetDateTimeGenerator getInstance() {
        if (instance == null) {
             instance = new OffsetDateTimeGenerator();
        }
        return instance;
    }

}
