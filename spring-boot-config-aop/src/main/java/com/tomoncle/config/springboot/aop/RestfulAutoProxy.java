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

package com.tomoncle.config.springboot.aop;

import com.tomoncle.config.springboot.model.Disregard;
import com.tomoncle.config.springboot.model.HttpRestObject;
import com.tomoncle.config.springboot.model.NullValue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 定义切面类
 *
 * @author tomoncle
 */
@ConditionalOnWebApplication
@Component
@Aspect
@EnableAspectJAutoProxy
public class RestfulAutoProxy {
    private AspectLog logger = new AspectLog();

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)")
    void aspectPointForHttp() {
    }


    @Around(value = "aspectPointForHttp() && @within(com.tomoncle.config.springboot.model.ApiFormat)")
    public Object httpResponse(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 使用 @Disregard 该注解忽略aop, 异常正常抛出
        if (Objects.nonNull(AnnotationUtils.findAnnotation(methodSignature.getMethod(), Disregard.class))) {
            return joinPoint.proceed();
        }
        try {
            return formatMethodResponse(joinPoint.proceed(), methodSignature.getMethod());
        } catch (Throwable throwable) {
            logger.errorLog(joinPoint, throwable);
            return new HttpRestObject(500, null, throwable.getMessage());
        }
    }

    /**
     * @param result 响应
     * @param method 执行的方法
     * @return HttpRestObject
     */
    private HttpRestObject formatMethodResponse(final Object result, final Method method) {
        // 如果方法使用 @NullValue 注解，返回空值时显示的指定响应信息
        NullValue nullValue = AnnotationUtils.findAnnotation(method, NullValue.class);
        if (Objects.nonNull(result) || Objects.isNull(nullValue)) {
            return new HttpRestObject(200, result, "ok");
        }
        HttpStatus status = HttpStatus.resolve(nullValue.code());
        if (Objects.nonNull(status) && Objects.toString(nullValue.message(), "").length() == 0) {
            return new HttpRestObject(nullValue.code(), null, status.getReasonPhrase());
        }
        return new HttpRestObject(nullValue.code(), null, nullValue.message());
    }


}
