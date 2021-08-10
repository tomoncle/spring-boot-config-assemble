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

package com.tomoncle.test.springboot.redis;

import com.tomoncle.config.springboot.redis.SpringbootConfigRedisConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = UserService.cacheNames)
@Service
public class UserService {
    static final String cacheNames = "UserService";
    private static final Logger logger = LoggerFactory.getLogger(SpringbootConfigRedisConfiguration.class);
    static String username = "tomoncle";

    @Cacheable
    public String getMyName() {
        logger.info("call getMyName method.{}", username);
        return username;
    }

    @CacheEvict(cacheNames = {cacheNames}, allEntries = true)
    public void setUsername(String value) {
        username = value;
    }

    @CacheEvict(cacheNames = {cacheNames}, allEntries = true)
    public void clear() {
    }
}
