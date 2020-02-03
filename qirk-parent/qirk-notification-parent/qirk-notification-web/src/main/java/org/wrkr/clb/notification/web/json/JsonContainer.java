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
package org.wrkr.clb.notification.web.json;

import java.util.Arrays;
import java.util.List;

import org.wrkr.clb.notification.services.json.JsonStatusCode;
import org.wrkr.clb.notification.web.controller.NotificationWebSocketController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonContainer<D extends Object, M extends Object> {

    private static final ObjectWriter JSON_CONTAINER_WRITER = new ObjectMapper().writerFor(JsonContainer.class);

    @JsonProperty(value = "status_ok")
    private Boolean statusOK = true;
    @JsonProperty(value = "status_code")
    private String jsonStatusCode = JsonStatusCode.OK;

    @JsonProperty(value = "response_type")
    private String responseType;

    @JsonProperty(value = "data")
    private List<D> data;

    @JsonProperty(value = "meta")
    private M meta;

    private JsonContainer() {
    }

    public String toJson() throws JsonProcessingException {
        return JSON_CONTAINER_WRITER.writeValueAsString(this);
    }

    public static <D extends Object> JsonContainer<D, Void> fromObject(
            D object, NotificationWebSocketController.ResponseType responseType) {
        JsonContainer<D, Void> result = new JsonContainer<D, Void>();

        result.data = Arrays.asList(object);
        result.responseType = responseType.toString();

        return result;
    }

    public static <D extends Object> JsonContainer<D, Void> fromObjects(
            List<D> objectList, NotificationWebSocketController.ResponseType responseType) {
        JsonContainer<D, Void> result = new JsonContainer<D, Void>();

        result.data = objectList;
        result.responseType = responseType.toString();

        return result;
    }

    public static <D extends Object, M extends Object> JsonContainer<D, M> fromObjectsAndMeta(
            List<D> list, M meta, NotificationWebSocketController.ResponseType responseType) {
        JsonContainer<D, M> result = new JsonContainer<D, M>();

        result.data = list;
        result.meta = meta;
        result.responseType = responseType.toString();

        return result;
    }

    public static <M extends Object> JsonContainer<Void, M> fromMeta(
            M meta, NotificationWebSocketController.ResponseType responseType) {
        JsonContainer<Void, M> result = new JsonContainer<Void, M>();

        result.meta = meta;
        result.responseType = responseType.toString();

        return result;
    }

    public static JsonContainer<Void, Void> fromResponseType(NotificationWebSocketController.ResponseType responseType) {
        JsonContainer<Void, Void> result = new JsonContainer<Void, Void>();
        result.responseType = responseType.toString();
        return result;
    }

    public static JsonContainer<Void, Void> fromErrorCode(String code) {
        JsonContainer<Void, Void> result = new JsonContainer<Void, Void>();

        result.statusOK = false;
        result.jsonStatusCode = code;

        return result;
    }

    public static JsonContainer<Void, Void> fromErrorCode(String code, NotificationWebSocketController.RequestType responseType) {
        JsonContainer<Void, Void> result = fromErrorCode(code);
        result.responseType = responseType.toString();
        return result;
    }
}
