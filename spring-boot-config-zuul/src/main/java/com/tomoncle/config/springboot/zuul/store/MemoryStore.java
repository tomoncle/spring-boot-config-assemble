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

package com.tomoncle.config.springboot.zuul.store;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 单例内存存储
 *
 * @author : tomoncle
 * @since : 2019/5/16 21:02
 */
public final class MemoryStore {

    private static final List<ZuulProperties.ZuulRoute> routes = new ArrayList<>();

    private MemoryStore() {
    }

    public static MemoryStore getInstance() {
        return SingletonHolder.instance;
    }

    public List<ZuulProperties.ZuulRoute> routes() {
        return routes;
    }

    private static class SingletonHolder {
        private static MemoryStore instance = new MemoryStore();
    }

}
