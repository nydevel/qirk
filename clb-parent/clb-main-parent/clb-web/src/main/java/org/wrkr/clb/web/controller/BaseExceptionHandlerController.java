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
package org.wrkr.clb.web.controller;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.PaymentRequiredException;
import org.wrkr.clb.services.util.http.JsonStatusCode;
import org.wrkr.clb.web.json.JsonContainer;

public abstract class BaseExceptionHandlerController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(BaseExceptionHandlerController.class);

    @ExceptionHandler(ApplicationException.class)
    private JsonContainer<Void, Void> handleApplicationException(HttpServletResponse response, ApplicationException e) {
        if (e.getHttpStatus() != HttpServletResponse.SC_CONFLICT) {
            LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        }
        response.setStatus(e.getHttpStatus());
        return JsonContainer.fromApplicationException(e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private JsonContainer<Void, Void> handleValidationConstraintViolationException(ConstraintViolationException e) {
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            LOG.error("Constraint violation: " + violation.getMessage());
            if (violation.getConstraintDescriptor().getConstraintValidatorClasses().contains(EmailValidator.class)) {
                return JsonContainer.fromCodeAndReason(JsonStatusCode.INVALID_EMAIL, "Invalid email.");
            }
        }

        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.CONSTRAINT_VIOLATION, "Invalid input.");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private JsonContainer<Void, Void> handleDuplicateKeyException(Exception e) {
        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.CONSTRAINT_VIOLATION, "Invalid input.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private JsonContainer<Void, Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.BAD_REQUEST, "HTTP message is not readable.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private JsonContainer<Void, Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private JsonContainer<Void, Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.BAD_REQUEST,
                "Parameter '" + e.getName() + "' must be of type " + e.getRequiredType().getSimpleName() + ".");
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private JsonContainer<Void, Void> handleUnauthorized(
            @SuppressWarnings("unused") AuthenticationCredentialsNotFoundException e) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.UNAUTHORIZED,
                "Authentication credentials were not provided.");
    }

    @ExceptionHandler(PaymentRequiredException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    private JsonContainer<Void, Void> handlePaymentRequired(PaymentRequiredException e) {
        LOG.info(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.PAYMENT_REQUIRED, "Payment required.");
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private JsonContainer<Void, Void> handleSecurityException(SecurityException e) {
        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.FORBIDDEN,
                "You do not have permission to perform this action.");
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    private JsonContainer<Void, Void> handleNotAcceptable(@SuppressWarnings("unused") HttpMediaTypeNotAcceptableException e) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.NOT_ACCEPTABLE, "Not acceptable.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    private JsonContainer<Void, Void> handleUnsupportedMediaType(
            @SuppressWarnings("unused") HttpMediaTypeNotSupportedException e) {
        return JsonContainer.fromCodeAndReason(JsonStatusCode.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type.");
    }

    @ExceptionHandler(PersistenceException.class)
    private JsonContainer<Void, Void> handlePersistenceException(HttpServletResponse response, PersistenceException e) {
        Throwable cause = e.getCause();
        if (cause != null && cause instanceof org.hibernate.exception.ConstraintViolationException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return handleDuplicateKeyException(e);
        }

        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.INTERNAL_SERVER_ERROR,
                "A server error occurred.");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private JsonContainer<Void, Void> handleHttpClientErrorException(HttpClientErrorException e) {
        LOG.error("HTTP client error response: " + e.getResponseBodyAsString());
        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.INTERNAL_SERVER_ERROR,
                "A server error occurred.");
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    private JsonContainer<Void, Void> handleUnsupportedOperationException(UnsupportedOperationException e) {
        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.NOT_IMPLEMENTED, "Not implemented.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private JsonContainer<Void, Void> handleException(Exception e) {
        LOG.error(EXCEPTION_HANDLER_LOG_MESSAGE, e);
        return JsonContainer.fromCodeAndReason(JsonStatusCode.INTERNAL_SERVER_ERROR,
                "A server error occurred.");
    }
}