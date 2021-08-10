/*
 * Copyright 2018 tomoncle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomoncle.config.springboot.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用@ControllerAdvice注解来捕获系统抛出异常，触发该异常处理类
 * Created by tomoncle on 17-7-20.
 */
@ControllerAdvice
public class DefaultExceptionHandler {
    private static final Log logger = LogFactory.getLog(DefaultExceptionHandler.class);


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handlerMethodNotSupportedException(
            HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return responseMap(request, response, exception);
    }

    @ExceptionHandler(UnavailableException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handlerUnauthorizedException(
            HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return responseMap(request, response, exception);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handlerBadRequestException(
            HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return responseMap(request, response, exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handlerServerErrorException(
            HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return responseMap(request, response, exception);
    }


    private ResponseEntity<Map<String, Object>> responseMap(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        Map<String, Object> body = new HashMap<>();
        HttpStatus status;
        if (exception instanceof HttpRequestMethodNotSupportedException) {
            status = HttpStatus.METHOD_NOT_ALLOWED;
        } else if (exception instanceof NoHandlerFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof UnavailableException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof MissingPathVariableException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        body.put("status", status.value());
        body.put("timestamp", new Date());
        body.put("data", null);
        body.put("path", request.getRequestURI());
        body.put("method", request.getMethod());
        body.put("message", exception.getMessage());
        logger.error(String.format("\033[31;mDefaultExceptionHandler error: path -> %s; message -> %s\033[0m", body.get("path"), body.get("message")));
        return new ResponseEntity<>(body, status);
    }

}
