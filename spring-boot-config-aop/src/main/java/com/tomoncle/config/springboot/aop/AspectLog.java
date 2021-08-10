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

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author tomoncle
 */
class AspectLog {
    private static Logger logger = LoggerFactory.getLogger(AspectLog.class);

    private String calledInfo(final JoinPoint joinPoint) {
        return Thread.currentThread().getName() + "：调用[[ " + joinPoint.getTarget().getClass().getSimpleName() + " ]]的 " + joinPoint.getSignature().getName() + " 方法";
    }

    private String findArgs(final JoinPoint joinPoint) {
        return "方法入参：" + Arrays.toString(joinPoint.getArgs());
    }

    void debugLog(final JoinPoint joinPoint) {
        logger.debug(calledInfo(joinPoint) + "，" + findArgs(joinPoint));
    }

    void debugLog(final JoinPoint joinPoint, final String message) {
        logger.debug(calledInfo(joinPoint) + ", " + message);
    }

    void errorLog(final JoinPoint joinPoint, final Throwable throwable) {
        logger.error(calledInfo(joinPoint) + "，" + findArgs(joinPoint) + "，触发异常：" + throwable);
    }

    void errorLog(final JoinPoint joinPoint, final String message) {
        logger.error(calledInfo(joinPoint) + ", " + message);
    }
}
