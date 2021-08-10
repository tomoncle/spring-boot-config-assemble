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

import com.tomoncle.config.springboot.model.AutoDebug;
import com.tomoncle.config.springboot.model.SilentError;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 定义切面类
 * enabled config: logging.level.com.tomoncle.config.springboot=debug
 *
 * @author tomoncle
 */

@Component
@Aspect
@EnableAspectJAutoProxy
public class DefaultConfigAutoProxy {

    private AspectLog logger = new AspectLog();


    @Pointcut("@annotation(com.tomoncle.config.springboot.model.AutoDebug)" +
            "||@annotation(com.tomoncle.config.springboot.model.SilentError)")
    void aspectPoint() {
    }

    @Around(value = "aspectPoint()")
    public Object aroundCheck(final ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debugLog(joinPoint);
        try {
            Object result = joinPoint.proceed();
            logger.debugLog(joinPoint, "返回值[" + result + "]");
            return result;
        } catch (Throwable throwable) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            AutoDebug autoDebug = AnnotationUtils.findAnnotation(methodSignature.getMethod(), AutoDebug.class);
            // 判断开启了AutoDebug, 这个异常输出信息，包含参数，所以更敏感
            if (null != autoDebug) {
                logger.errorLog(joinPoint, throwable);
            }
            // 假如包含使用了静默异常处理，则返回空
            SilentError silentError = AnnotationUtils.findAnnotation(methodSignature.getMethod(), SilentError.class);
            if (Objects.isNull(silentError)) {
                throw throwable;
            }
            if (!Objects.equals("", silentError.value())) { // 优先使用自定义错误信息
                logger.errorLog(joinPoint, "异常提示：" + silentError.value());
            } else if (null == autoDebug) { // 如果没用开启AutoDebug，并且没用默认错误提示，则输出
                logger.errorLog(joinPoint, "异常提示：" + throwable);
            }
            return null;
        }
    }


}
