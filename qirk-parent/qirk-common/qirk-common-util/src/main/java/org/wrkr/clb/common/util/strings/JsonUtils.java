/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.common.util.strings;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class JsonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectWriter DEFAULT_WRITER = new ObjectMapper().writer();
    private static final ObjectWriter MAP_WRITER = new ObjectMapper().writerFor(Map.class);

    private static final ObjectReader MAP_READER = new ObjectMapper().readerFor(Map.class);
    private static final ObjectReader LONG_FOR_INTS_MAP_READER = new ObjectMapper().configure(
            DeserializationFeature.USE_LONG_FOR_INTS, true).readerFor(Map.class);
    private static final ObjectReader LONG_FOR_INTS_AND_BIGDECIMAL_FOR_FLOATS_MAP_READER = new ObjectMapper()
            .configure(DeserializationFeature.USE_LONG_FOR_INTS, true)
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true).readerFor(Map.class);

    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        return DEFAULT_WRITER.writeValueAsString(object);
    }

    public static <T extends Object> String convertMapToJson(Map<String, T> map) throws JsonProcessingException {
        long startTime = System.currentTimeMillis();
        String json = MAP_WRITER.writeValueAsString(map);
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isInfoEnabled()) {
            LOG.info("processed convert to json for type Map in " + resultTime + " ms");
        }
        return json;
    }

    public static <T extends Object> Map<String, T> convertJsonToMap(String json) throws IOException {
        return MAP_READER.<Map<String, T>>readValue(json);
    }

    public static <T extends Object> Map<String, T> convertJsonToMapUsingLongForInts(String json) throws IOException {
        return LONG_FOR_INTS_MAP_READER.<Map<String, T>>readValue(json);
    }

    public static <T extends Object> Map<String, T> convertJsonToMapUsingLongForIntsAndBigDecimalForFloats(String json)
            throws IOException {
        return LONG_FOR_INTS_AND_BIGDECIMAL_FOR_FLOATS_MAP_READER.<Map<String, T>>readValue(json);
    }

    public static <T extends Object> Map<String, T> convertJsonToMap(byte[] json) throws IOException {
        return MAP_READER.<Map<String, T>>readValue(json);
    }

    public static <T extends Object> Map<String, T> convertJsonToMapUsingLongForInts(byte[] json) throws IOException {
        return LONG_FOR_INTS_MAP_READER.<Map<String, T>>readValue(json);
    }

    public static <T extends Object> Map<String, T> convertJsonToMap(JsonNode json) throws IOException {
        return MAP_READER.<Map<String, T>>readValue(json);
    }

    public static <T extends Object> Map<String, T> convertJsonToMapUsingLongForInts(JsonNode json) throws IOException {
        return LONG_FOR_INTS_MAP_READER.<Map<String, T>>readValue(json);
    }
}
