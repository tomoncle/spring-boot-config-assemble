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

import com.tomoncle.config.springboot.constant.SpringBootConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DruidDataSource config
 * Created by tomoncle on 2017/12/2.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = SpringBootConfig.CONFIG_PREFIX + ".druid.datasource")
public class DruidConfigurationProperties {
    private String id;
    private String username;
    private String url;
    private String password;
    private String driverClass;
    private String filters;
    private String proxyFilters;
    private Integer maxActive = 20;
    private Integer initialSize = 1;
    private Integer maxWait = 60000; //Integer.valueOf('\uea60')
    private Integer minIdle = 1;
    private Integer timeBetweenEvictionRunsMillis = 60000;
    private Integer minEvictableIdleTimeMillis = 300000;
    private String validationQuery = "SELECT \'x\'";
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean poolPreparedStatements = false;
    private Integer maxOpenPreparedStatements = 20;
    private Long slowSqlMillis = 5000L;
    private boolean isLogSlowSql = true;
    private String loginUsername = "admin";
    private String loginPassword = "admin1234";
    private boolean resetEnable;
    private boolean sessionStatEnable = true;
    private Integer sessionStatMaxCount = 2000;
    private String exclusions = "*.js,*.gif,*.jpg,*.png,*.css,*.ico";
    private String principalSessionName;
    private boolean profileEnable;
    private String jmxUrl;
    private String jmxUsername;
    private String jmxPassword;
    private boolean authentication = true;
    private String springPatterns;  // com.aric.*,com.tomoncle.*
    private boolean monitorEnabled = false; // 当该属性值为 true 时启动web监控
}
