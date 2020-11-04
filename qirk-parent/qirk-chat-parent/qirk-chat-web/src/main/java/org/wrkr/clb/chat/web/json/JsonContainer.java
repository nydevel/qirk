package org.wrkr.clb.chat.web.json;

import java.util.Arrays;
import java.util.List;

import org.wrkr.clb.chat.services.util.json.JsonContainer_;
import org.wrkr.clb.chat.services.util.json.JsonStatusCode;
import org.wrkr.clb.chat.web.controller.ChatWebSocketController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonContainer<D extends Object, M extends Object> {

    private static final ObjectWriter JSON_CONTAINER_WRITER = new ObjectMapper().writerFor(JsonContainer.class);

    @JsonProperty(value = JsonContainer_.statusOk)
    private Boolean statusOK = true;
    @JsonProperty(value = JsonContainer_.statusCode)
    private String jsonStatusCode = JsonStatusCode.OK;

    @JsonProperty(value = JsonContainer_.responseType)
    private String responseType;

    @JsonProperty(value = JsonContainer_.data)
    private List<D> data;
    @JsonProperty(value = JsonContainer_.meta)
    @JsonInclude(Include.NON_NULL)
    private M meta;

    private JsonContainer() {
    }

    private String toJson() throws JsonProcessingException {
        return JSON_CONTAINER_WRITER.writeValueAsString(this);
    }

    public static String fromResponseType(ChatWebSocketController.ResponseType responseType) throws JsonProcessingException {
        JsonContainer<Void, Void> result = new JsonContainer<Void, Void>();
        result.responseType = responseType.toString();
        return result.toJson();
    }

    public static <D extends Object> String fromObject(D object, ChatWebSocketController.ResponseType responseType)
            throws JsonProcessingException {
        JsonContainer<D, Void> result = new JsonContainer<D, Void>();

        result.data = Arrays.asList(object);
        result.responseType = responseType.toString();

        return result.toJson();
    }

    public static <D extends Object> String fromObjects(List<D> objectList, ChatWebSocketController.ResponseType responseType)
            throws JsonProcessingException {
        JsonContainer<D, Void> result = new JsonContainer<D, Void>();

        result.data = objectList;
        result.responseType = responseType.toString();

        return result.toJson();
    }

    public static <D extends Object, M extends Object> String fromObjectsAndMeta(List<D> list, M meta,
            ChatWebSocketController.ResponseType responseType)
            throws JsonProcessingException {
        JsonContainer<D, M> result = new JsonContainer<D, M>();

        result.data = list;
        result.meta = meta;
        result.responseType = responseType.toString();

        return result.toJson();
    }

    public static String fromErrorCode(String code) throws JsonProcessingException {
        return fromErrorCode(code, null);
    }

    public static String fromErrorCode(String code, ChatWebSocketController.RequestType responseType)
            throws JsonProcessingException {
        JsonContainer<Void, Void> result = new JsonContainer<Void, Void>();

        result.statusOK = false;
        result.jsonStatusCode = code;
        if (responseType != null) {
            result.responseType = responseType.toString();
        }

        return result.toJson();
    }
}
