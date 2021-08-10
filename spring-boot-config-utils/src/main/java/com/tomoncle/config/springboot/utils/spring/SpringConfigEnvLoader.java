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

package com.tomoncle.config.springboot.utils.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Map;

/**
 * @author tomoncle
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableConfigurationProperties(SpringConfigEnvProperties.class)
class SpringConfigEnvLoader {
    private static Logger logger = LoggerFactory.getLogger(SpringConfigEnvLoader.class);

    private final SpringConfigEnvProperties springConfigEnvProperties;

    public SpringConfigEnvLoader(SpringConfigEnvProperties springConfigEnvProperties) {
        logger.debug("Initializing custom configuration to environment variables.");
        this.springConfigEnvProperties = springConfigEnvProperties;
        this.initializeProperties();
    }

    private void initializeProperties() {
        Map<String, String> properties = springConfigEnvProperties.getProperties();
        for (String key : properties.keySet()) {
            if (null == properties.get(key)) {
                continue;
            }
            if (null != System.getProperty(key)) {
                logger.warn("Custom environment variable {} overwrite the default environment variable value.", key);
            }
            System.setProperty(key, properties.get(key));
        }

    }
}
