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

package com.tomoncle.config.springboot.ch;

import com.tomoncle.config.springboot.ch.query.JsonClickHouseQuery;
import com.tomoncle.config.springboot.ch.query.StringClickHouseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author tomoncle
 */
@Configuration
@EnableConfigurationProperties(ClickHouseProperties.class)
public class ClickHouseConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "jsonClickHouseQuery")
    public JsonClickHouseQuery jsonClickHouseQuery(ClickHouseProperties clickHouseProperties) {
        return new JsonClickHouseQuery(clickHouseProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stringClickHouseQuery")
    public StringClickHouseQuery stringClickHouseQuery(ClickHouseProperties clickHouseProperties) {
        return new StringClickHouseQuery(clickHouseProperties);
    }

    @Component
    @ConditionalOnProperty(prefix = "spring.boot.config", value = {"clickhouse.validator"}, havingValue = "true")
    private class Validator {
        private Validator(StringClickHouseQuery stringClickHouseQuery) {
            Logger logger = LoggerFactory.getLogger(Validator.class);
            if (stringClickHouseQuery.authentication()) {
                logger.debug("Authentication successful, connected to clickHouse.");
            } else {
                logger.error("Authentication failed, cannot connect to clickHouse.");
            }
        }

    }


}
