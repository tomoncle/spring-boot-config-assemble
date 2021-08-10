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

package com.tomoncle.config.springboot.druid;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.tomoncle.config.springboot.constant.SpringBootConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by tomoncle on 2017/12/2.
 */
@Configuration
@ConditionalOnProperty(
        prefix = SpringBootConfig.CONFIG_PREFIX,
        value = {"druid.datasource.monitorEnabled"},
        havingValue = "true"
)
@EnableConfigurationProperties({DruidConfigurationProperties.class})
public class EnableDruidServletConfiguration {

    private Logger logger = LoggerFactory.getLogger(EnableDruidServletConfiguration.class);

    /**
     * 注册WebStatFilter到系统过滤器链
     *
     * @param ds DruidConfigurationProperties
     */
    @Bean
    public FilterRegistrationBean druidWebStatFilter(DruidConfigurationProperties ds) {
        this.logger.debug("init druidWebStatFilter");
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        Map<String, String> initParameters = new HashMap<>(1);
        initParameters.put("exclusions", ds.getExclusions());
        initParameters.put("sessionStatMaxCount", ds.getSessionStatMaxCount().toString());
        initParameters.put("sessionStatEnable", String.valueOf(ds.isSessionStatEnable()));
        if (null != ds.getPrincipalSessionName()) {
            initParameters.put("principalSessionName", ds.getPrincipalSessionName());
        }
        initParameters.put("profileEnable", String.valueOf(ds.isProfileEnable()));
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }

    /**
     * 注册编码过滤器
     */
    @Bean
    public FilterRegistrationBean charsetEncodingFilter() {
        this.logger.debug("init charsetEncodingFilter");
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        filterRegistrationBean.setOrder(-2147483648);
        filterRegistrationBean.setFilter(characterEncodingFilter);
        return filterRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean druidStatViewServlet(DruidConfigurationProperties ds) {
        this.logger.debug("init druidStatViewServlet");
        String[] urlMappings = new String[]{"/druid/*"};
        ServletRegistrationBean<Servlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(), urlMappings);
        Map<String, String> map = new HashMap<>();
        map.put("resetEnable", String.valueOf(ds.isResetEnable()));
        if (ds.isAuthentication()) {
            if (null != ds.getLoginUsername()) {
                map.put("loginUsername", ds.getLoginUsername());
            }
            if (null != ds.getLoginPassword()) {
                map.put("loginPassword", ds.getLoginPassword());
            }
        }
        if (null != ds.getJmxUrl()) {
            map.put("jmxUrl", ds.getJmxUrl());
        }
        if (null != ds.getJmxUsername()) {
            map.put("jmxUsername", ds.getJmxUsername());
        }
        if (null != ds.getJmxPassword()) {
            map.put("jmxPassword", ds.getJmxPassword());
        }
        registrationBean.setInitParameters(map);
        return registrationBean;
    }


    @Bean
    public DruidStatInterceptor druidStatInterceptor() {
        logger.debug("init DruidStatInterceptor");
        return new DruidStatInterceptor();
    }


    @Bean
    @Scope("prototype")
    public JdkRegexpMethodPointcut jdkRegexpMethodPointcut(DruidConfigurationProperties ds) {
        logger.debug("init JdkRegexpMethodPointcut");
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        if (ds.getSpringPatterns() != null && ds.getSpringPatterns().length() > 1) {
            druidStatPointcut.setPatterns(ds.getSpringPatterns().replaceAll(" ", "").split(","));
        }
        return druidStatPointcut;
    }


    @Bean
    public DefaultPointcutAdvisor druidStatAdvisor(DruidStatInterceptor druidStatInterceptor, JdkRegexpMethodPointcut druidStatPointcut) {
        logger.debug("init DefaultPointcutAdvisor");
        DefaultPointcutAdvisor defaultPointAdvisor = new DefaultPointcutAdvisor();
        defaultPointAdvisor.setPointcut(druidStatPointcut);
        defaultPointAdvisor.setAdvice(druidStatInterceptor);
        return defaultPointAdvisor;
    }


}
