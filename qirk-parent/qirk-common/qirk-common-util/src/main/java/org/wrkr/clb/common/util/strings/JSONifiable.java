package org.wrkr.clb.common.util.strings;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JSONifiable {

    public String toJson() throws JsonProcessingException;
}
