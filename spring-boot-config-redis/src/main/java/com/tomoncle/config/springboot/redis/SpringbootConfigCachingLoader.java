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

package com.tomoncle.config.springboot.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@EnableCaching
@ConditionalOnBean(SpringbootConfigRedisConfiguration.class)
public class SpringbootConfigCachingLoader extends CachingConfigurerSupport {
    private static final Logger logger = LoggerFactory.getLogger(SpringbootConfigCachingLoader.class);

    public SpringbootConfigCachingLoader() {
        logger.info("Initializing CachingConfigurerSupport and opened EnableCaching. ");
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName()).append("_").append(method.getName());
            for (Object obj : objects) {
                sb.append("_").append(obj.toString());
            }
            return sb.toString();
        };
    }
}
