package org.wrkr.clb.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.util.http.JsonStatusCode;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
public class RootController {

    @RequestMapping(value = "/*", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public JsonContainer<Void, Void> handleNotFound(@SuppressWarnings("unused") HttpSession session) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.HANDLER_NOT_FOUND, "Handler not found.");
    }
}
