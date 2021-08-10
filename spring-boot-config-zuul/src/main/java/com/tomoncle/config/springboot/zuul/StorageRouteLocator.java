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

import com.tomoncle.config.springboot.zuul.mapper.StorageRouteMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从存储系统读取加载路由信息
 * Created by tomoncle on 18-5-28.
 */
public class StorageRouteLocator extends SimpleRouteLocator
        implements RefreshableRouteLocator {
    private static Logger logger = LogManager.getLogger(StorageRouteLocator.class);

    private final StorageRouteMapper routeMapper;

    private final ZuulProperties properties;

    /**
     * 初始化的构造方法
     *
     * @param servletPath        获取项目路径
     * @param zuulProperties     zuul配置文件信息
     * @param storageRouteMapper 持久化存储设备接口
     */
    public StorageRouteLocator(String servletPath, ZuulProperties zuulProperties, StorageRouteMapper storageRouteMapper) {
        super(servletPath, zuulProperties);
        this.properties = zuulProperties;
        this.routeMapper = storageRouteMapper;
        logger.info("initial StorageRouteLocator.class");
        if (null == this.routeMapper) {
            logger.warn("warning StorageRouteMapper Bean is null. \n" +
                    "You can set the following contents to the system configuration file " +
                    "to enable memory storage. 'spring.boot.config.zuul.storage.type=mem'");
        }
    }

    @Override
    public void refresh() {
        logger.info("StorageRouteLocator refresh.");
        doRefresh();
    }


    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        routesMap.putAll(super.locateRoutes());
        routesMap.putAll(this.getStorageLocateRoutes());
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            values.put(checkPath(entry.getKey()), entry.getValue());
        }
        return values;
    }


    private Map<String, ZuulProperties.ZuulRoute> getStorageLocateRoutes() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        if (this.routeMapper == null) {
            logger.warn("StorageRouteLocator load data fail, reason: StorageRouteMapper Bean is null.");
            return routes;
        }
        final List<ZuulProperties.ZuulRoute> routeList = this.routeMapper.routes();
        if (null == routeList) {
            logger.debug("StorageRouteLocator getStorageLocateRoutes size: 0");
            return routes;
        }
        for (ZuulProperties.ZuulRoute result : routeList) {
            if (StringUtils.isEmpty(result.getPath()) || StringUtils.isEmpty(result.getUrl())) {
                continue;
            }
            routes.put(result.getPath(), result);
        }
        logger.debug("StorageRouteLocator getStorageLocateRoutes size: " + routes.keySet().size());
        return routes;
    }

    private String checkPath(String path) {
        if (!path.startsWith("/")) {
            path = String.format("/%s", path);
        }
        if (StringUtils.hasText(this.properties.getPrefix())) {
            path = String.format("%s%s", this.properties.getPrefix(), path);
            if (!path.startsWith("/")) {
                path = String.format("/%s", path);
            }
        }
        return path;
    }
}
