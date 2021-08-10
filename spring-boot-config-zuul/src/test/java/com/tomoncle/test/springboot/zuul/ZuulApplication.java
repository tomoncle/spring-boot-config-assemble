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

package com.tomoncle.test.springboot.zuul;

import com.tomoncle.config.springboot.constant.SpringBootConfig;
import com.tomoncle.config.springboot.zuul.config.RouteUpdater;
import com.tomoncle.config.springboot.zuul.mapper.IRouteMapper;
import com.tomoncle.config.springboot.zuul.mapper.MemoryStorageRouteMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomoncle on 18-5-28.
 */
@RestController
@SpringBootApplication(scanBasePackages = SpringBootConfig.SCAN_BASE_PACKAGE)
public class ZuulApplication {

    private static final Map<String, Map<String, String>> websiteMap;

//    // 模拟数据
//    private static final List<ZuulProperties.ZuulRoute> routes = new ArrayList<>();
//
//    static {
//        ZuulProperties.ZuulRoute zz = new ZuulProperties.ZuulRoute(
//                "qq",
//                "/qq/**",
//                null,
//                "http://www.qq.com/",
//                true,
//                null,
//                null
//        );
//        routes.add(zz);
//    }
//
//    /**
//     * 使用自定义的StorageRouteMapper
//     * 注意：要在配置文件中关闭　spring.boot.config.zuul.storage.type=mem
//     * @return
//     */
//    @Bean
//    public StorageRouteMapper storageRouteMapper() {
//        LOG.debug("StorageRouteMapper bean load.");
//        return new StorageRouteMapper() {
//            @Override
//            public List<ZuulProperties.ZuulRoute> routes() {
//                // 模拟从数据库拿取数据
//                return routes;
//            }
//        };
//    }

    // initial data
    static {
        websiteMap = new HashMap<>();
        websiteMap.put("qq", new HashMap<String, String>() {{
            put("path", "/qq/**");
            put("url", "http://www.qq.com/");
        }});
        websiteMap.put("sina", new HashMap<String, String>() {{
            put("path", "/sina/**");
            put("url", "http://www.sina.com.cn/");
        }});
        websiteMap.put("http", new HashMap<String, String>() {{
            put("path", "/http/**");
            put("url", "https://httpbin.org/");
        }});
    }

    @Autowired(required = false)
    RouteUpdater routeUpdater;
    @Autowired(required = false)
    MemoryStorageRouteMapper memoryRoute;

    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);
    }

    /**
     * 模拟修改存储
     */
    @RequestMapping("/zuulRoute/update2")
    public void update2() {
        memoryRoute.addOrReplace("/qq/**", "http://www.qq.com/");
        memoryRoute.addOrReplace("/sina/**", "http://www.sina.com.cn/");
    }

    /**
     * 模拟修改存储
     */
    @RequestMapping("/zuulRoute/update1")
    public void update1() {
        memoryRoute.addOrReplace("/qq/**", "http://www.sina.com.cn/");
        memoryRoute.addOrReplace("/sina/**", "http://www.qq.com/");
    }

    /**
     * 刷新route
     *
     * @return
     */
    @RequestMapping("/zuulRoute/refresh")
    public boolean refresh() {
        return routeUpdater.refresh();
    }

    /**
     * 使用过滤器自动切换代理地址
     * <p>
     * 测试：
     * POST:
     * curl -X POST -F "website=sina" http://localhost:8601
     * 浏览器访问：http://localhost:8601/zuulProxy
     * <p>
     * GET:
     * curl http://localhost:8601?website=qq
     * 浏览器访问：　http://localhost:8601/zuulProxy
     */
    @Bean
    public IRouteMapper iRouteMapper() {
        return new IRouteMapper() {
            private final Log logger = LogFactory.getLog(IRouteMapper.class);

            @Override
            public void refresh(ServletRequest servletRequest, ServletResponse servletResponse) {
                if (null == memoryRoute) {
                    logger.warn("MemoryTemporaryRoute Bean is null, zuul proxy route refresh fail.");
                } else {
                    // 使用过滤器来自动更新"/zuulProxy/**"这个请求路径，转发的url链接
                    final String website = servletRequest.getParameter("website");
                    if (StringUtils.isNotEmpty(website) && websiteMap.containsKey(website)) {
                        final Map<String, String> map = websiteMap.get(website);
                        if (null != map && map.containsKey("url")) {
                            memoryRoute.addOrReplace("/zuulProxy/**", map.get("url"));
                            routeUpdater.refresh();
                        }
                    }
                }
            }
        };
    }


}
