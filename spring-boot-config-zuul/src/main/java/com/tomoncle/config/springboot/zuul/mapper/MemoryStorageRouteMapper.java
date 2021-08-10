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

package com.tomoncle.config.springboot.zuul.mapper;

import com.tomoncle.config.springboot.constant.SpringBootConfig;
import com.tomoncle.config.springboot.zuul.store.MemoryStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 内置内存存储
 * Created by tomoncle on 18-5-29.
 */
@Primary
@Component
@ConditionalOnProperty(
        prefix = SpringBootConfig.CONFIG_PREFIX,
        value = {"zuul.storage.type"},
        havingValue = "mem"
)
public class MemoryStorageRouteMapper implements StorageRouteMapper {
    private static Logger logger = LogManager.getLogger(MemoryStorageRouteMapper.class);

    private List<ZuulProperties.ZuulRoute> routes = MemoryStore.getInstance().routes();

    public MemoryStorageRouteMapper() {
        logger.info("initial MemoryStorageRouteMapper.class");
    }

    private String extractId(final String path) {
        return (path.startsWith("/") ? path.substring(1) : path)
                .replace("/*", "")
                .replace("*", "");

    }

    @Override
    public void add(String path, String url) {
        routes.add(new ZuulProperties.ZuulRoute(
                extractId(path),
                path,
                null,
                url,
                true,
                null,
                null));
    }

    @Override
    public void addOrReplace(String path, String url) {
        for (ZuulProperties.ZuulRoute zz : routes) {
            if (extractId(path).equals(zz.getId())) {
                zz.setPath(path);
                zz.setUrl(url);
                return;
            }
        }
        add(path, url);
    }

    @Override
    public List<ZuulProperties.ZuulRoute> routes() {
        return routes;
    }
}