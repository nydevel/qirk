package org.wrkr.clb.web.json;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.services.dto.PaginatedListDTO;
import org.wrkr.clb.services.dto.meta.PaginationMetaDTO;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.http.JsonStatusCode;

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

    @JsonProperty(value = "status_reason")
    private String statusReason;

    private List<D> data = new ArrayList<D>();
    private M meta;

    public JsonContainer() {
    }

    public String toJson() throws JsonProcessingException {
        return JSON_CONTAINER_WRITER.writeValueAsString(this);
    }

    public JsonContainer(D object) {
        this.data.add(object);
    }

    public JsonContainer(D object, M meta) {
        this.data.add(object);
        this.meta = meta;
    }

    public JsonContainer(List<D> data) {
        this.data = data;
    }

    public JsonContainer(List<D> data, M meta) {
        this.data = data;
        this.meta = meta;
    }

    public static <M extends Object> JsonContainer<Void, M> fromMeta(M meta) {
        JsonContainer<Void, M> result = new JsonContainer<Void, M>();
        result.meta = meta;
        return result;
    }

    public static <D extends Object> JsonContainer<D, PaginationMetaDTO> fromPaginatedList(PaginatedListDTO<D> list) {
        return new JsonContainer<D, PaginationMetaDTO>(list.data, list.meta);
    }

    public static <M extends Object> JsonContainer<Void, M> fromCodeAndReason(String code, String reason, M meta) {
        JsonContainer<Void, M> result = new JsonContainer<Void, M>();

        result.statusOK = false;
        result.jsonStatusCode = code;
        result.statusReason = reason;
        if (meta != null) {
            result.meta = meta;
        }

        return result;
    }

    public static JsonContainer<Void, Void> fromCodeAndReason(String code, String reason) {
        return JsonContainer.<Void>fromCodeAndReason(code, reason, null);
    }

    public static <M extends Object> JsonContainer<Void, M> fromApplicationException(ApplicationException e, M meta) {
        return JsonContainer.<M>fromCodeAndReason(e.getJsonStatusCode(), e.getMessage(), meta);
    }

    public static JsonContainer<Void, Void> fromApplicationException(ApplicationException e) {
        return JsonContainer.<Void>fromApplicationException(e, null);
    }

    public Boolean isStatusOK() {
        return statusOK;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public List<D> getData() {
        return data;
    }

    public M getMeta() {
        return meta;
    }
}
