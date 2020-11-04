package org.wrkr.clb.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

import org.wrkr.clb.web.json.JsonContainer;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class ResponseBodyFilter implements Filter {

    protected void writeJsonToResponse(HttpServletResponse response, int status, String code, String reason)
            throws JsonProcessingException, IOException {
        String jsonBody = JsonContainer.fromCodeAndReason(code, reason).toJson();

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(status);
        response.getWriter().write(jsonBody);
    }
}
