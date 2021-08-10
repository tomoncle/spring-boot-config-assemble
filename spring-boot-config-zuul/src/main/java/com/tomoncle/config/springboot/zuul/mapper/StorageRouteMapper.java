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

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.List;

/**
 * 抽象获取route接口
 * 实现该接口即可
 * Created by tomoncle on 18-5-28.
 */
public interface StorageRouteMapper {
    /**
     * 获取route信息
     *
     * @return ZuulRoute data
     */
    List<ZuulProperties.ZuulRoute> routes();

    /**
     * 添加 route 信息
     *
     * @param path route path
     * @param url  route url
     */
    void add(String path, String url);

    /**
     * 添加或替换 route 信息
     *
     * @param path route path
     * @param url  route url
     */
    void addOrReplace(String path, String url);
}
