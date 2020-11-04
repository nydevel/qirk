package org.wrkr.clb.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wrkr.clb.services.util.http.JsonStatusCode;
import org.wrkr.clb.web.json.JsonContainer;


@RestControllerAdvice
public class GlobalExceptionHandlerController extends BaseController {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public JsonContainer<Void, Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.METHOD_NOT_ALLOWED,
                "Method " + e.getMethod() + " not allowed.");
    }
}
