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

package com.tomoncle.config.springboot.zuul;

import com.tomoncle.config.springboot.constant.SpringBootConfig;
import com.tomoncle.config.springboot.zuul.mapper.MemoryStorageRouteMapper;
import com.tomoncle.config.springboot.zuul.mapper.StorageRouteMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 启动zuul 组件
 * Created by tomoncle on 18-5-28.
 */
@Configuration
@EnableZuulProxy
@EnableConfigurationProperties({ServerProperties.class, ZuulProperties.class})
public class EnableZuulConfiguration {
    private static Logger logger = LogManager.getLogger(EnableZuulConfiguration.class);


    public EnableZuulConfiguration() {
        logger.info("Initial EnableZuulConfiguration.");
    }

    /**
     * @param serverProperties springboot config
     * @param zuulProperties   zuul config
     * @param routeMapper      route Impl, 允许为空
     * @return RouteLocator
     */
    @Bean
    @ConditionalOnProperty(
            prefix = SpringBootConfig.CONFIG_PREFIX,
            value = {"zuul.storage.enabled"},
            havingValue = "true"
    )
    public RouteLocator routeLocator(
            ServerProperties serverProperties,
            ZuulProperties zuulProperties,
            @Autowired(required = false) StorageRouteMapper routeMapper) {
        return new StorageRouteLocator(serverProperties.getServlet().getContextPath(), zuulProperties, routeMapper);
    }

    @Primary
    @Bean
    @ConditionalOnProperty(
            prefix = SpringBootConfig.CONFIG_PREFIX,
            value = {"zuul.storage.type"},
            havingValue = "mem"
    )
    public MemoryStorageRouteMapper memoryTemporaryRoute() {
        return new MemoryStorageRouteMapper();
    }

}
