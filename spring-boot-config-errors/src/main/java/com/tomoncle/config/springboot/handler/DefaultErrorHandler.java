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
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现ErrorController接口是重写BaseErrorController的error实现.
 * 实现HandlerExceptionResolver接口是重写异常解析处理函数
 * Created by tomoncle on 2017/7/16.
 */
@Controller
@RequestMapping("/error")
public class DefaultErrorHandler implements ErrorController, HandlerExceptionResolver {

    private static final Log logger = LogFactory.getLog(DefaultErrorHandler.class);


    @Override
    public String getErrorPath() {
        return "/error";
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        return null;
    }

    @RequestMapping(produces = "text/html")
    @ResponseBody
    public String errorHtml(HttpServletRequest httpServletRequest) {
        return HttpTools.errorHtml(httpServletRequest);
    }


    @RequestMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        Map<String, Object> body = new HashMap<>();
        HttpStatus status = HttpTools.getStatus(request);
        body.put("status", status.value());
        body.put("timestamp", new Date());
        body.put("data", null);
        body.put("path", HttpTools.getRequestPath(request));
        body.put("method", request.getMethod());
        body.put("message", status.getReasonPhrase());
        logger.error(String.format("\033[31;mDefaultErrorHandler error: path -> %s; message -> %s\033[0m", body.get("path"), body.get("message")));
        return new ResponseEntity<>(body, status);
    }

}
