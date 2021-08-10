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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * IRouteMapper 抽象刷新功能，供过滤器使用
 * Created by tomoncle on 18-5-30.
 */
public interface IRouteMapper {
    /**
     * 自定义实现刷新方法
     * <p/>
     *
     * @param servletRequest  　 ServletRequest
     * @param servletResponse 　ServletResponse
     * @see com.tomoncle.test.springboot.zuul
     */
    void refresh(ServletRequest servletRequest, ServletResponse servletResponse);
}
