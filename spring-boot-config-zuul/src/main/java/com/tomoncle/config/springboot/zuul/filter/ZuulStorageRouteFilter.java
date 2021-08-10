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

package com.tomoncle.config.springboot.zuul.filter;


import com.tomoncle.config.springboot.zuul.mapper.IRouteMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Objects;

/**
 * 使用过滤器自动切换代理地址，拦截所有请求
 * 并执行 RefreshRouteMapper 接口的实现来处理
 * Created by tomoncle on 18-5-30.
 */
@Configuration
@WebFilter(urlPatterns = "*", filterName = "zuulStorageRouteFilter")
public class ZuulStorageRouteFilter implements Filter {

    private static Logger logger = LogManager.getLogger(ZuulStorageRouteFilter.class);

    @Autowired(required = false)
    private IRouteMapper iRouteMapper;

    public ZuulStorageRouteFilter() {
        logger.info("Initial ZuulStorageRouteFilter.");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("ZuulStorageRouteFilter execute init. IRouteMapper instance :" + iRouteMapper);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        if (!Objects.equals(null, iRouteMapper)) {
            logger.debug("ZuulStorageRouteFilter execute doFilter method.");
            // 拦截请求的具体实现
            iRouteMapper.refresh(servletRequest, servletResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        logger.debug("ZuulStorageRouteFilter execute destroy method.");

    }
}
