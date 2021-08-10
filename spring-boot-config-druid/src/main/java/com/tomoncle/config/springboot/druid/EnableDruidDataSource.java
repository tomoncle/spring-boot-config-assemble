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

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.tomoncle.config.springboot.constant.SpringBootConfig;
import com.tomoncle.config.springboot.constant.SpringBootDataSourceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * alibaba DruidDataSource
 * Created by tomoncle on 2017/12/2.
 */
@Configuration
@ConditionalOnProperty(
        prefix = SpringBootConfig.CONFIG_PREFIX,
        value = {"datasource.type"},
        havingValue = "druid",
        matchIfMissing = true
)
@EnableConfigurationProperties({DruidConfigurationProperties.class, SpringBootDataSourceProperties.class})
public class EnableDruidDataSource {

    private Logger logger = LoggerFactory.getLogger(EnableDruidDataSource.class);

    @Primary  // 使用该注解解决同一类型bean注入问题
    @Bean(name = "druidDataSource")
    public DruidDataSource druidDataSource(DruidConfigurationProperties ds, ApplicationContext context) throws SQLException {
        logger.info("init DruidDataSource");
        DruidDataSource druidDataSource = new DruidDataSource(false);
        if (StringUtils.hasText(ds.getId())) {
            druidDataSource.setName(ds.getId());
        }
        if (StringUtils.hasText(ds.getDriverClass())) {
            druidDataSource.setDriverClassName(ds.getDriverClass());
        }
        if (StringUtils.hasText(ds.getFilters())) {
            druidDataSource.setFilters(ds.getFilters());
        }
        if (StringUtils.hasText(ds.getProxyFilters())) {
            String[] result = ds.getProxyFilters().replaceAll(" ", "").split(",");
            List<Filter> filterList = new ArrayList<>();
            for (String str : result) {
                try {
                    Filter filter = (Filter) context.getBean(str);
                    filterList.add(filter);
                } catch (Exception e) {
                    this.logger.error("DruidDataSource连接池配置信息错误:" + e.getMessage());
                }
            }
            if (!CollectionUtils.isEmpty(filterList)) {
                druidDataSource.setProxyFilters(filterList);
            }
        }
        druidDataSource.setUsername(ds.getUsername());
        druidDataSource.setPassword(ds.getPassword());
        druidDataSource.setUrl(ds.getUrl());
        druidDataSource.setMaxActive(ds.getMaxActive());
        druidDataSource.setInitialSize(ds.getInitialSize());
        druidDataSource.setMaxWait((long) ds.getMaxWait());
        druidDataSource.setMinIdle(ds.getMinIdle());
        druidDataSource.setTimeBetweenEvictionRunsMillis((long) ds.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis((long) ds.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(ds.getValidationQuery());
        druidDataSource.setTestWhileIdle(ds.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(ds.isTestOnBorrow());
        druidDataSource.setTestOnReturn(ds.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(ds.isPoolPreparedStatements());
        druidDataSource.setMaxOpenPreparedStatements(ds.getMaxOpenPreparedStatements());
        return druidDataSource;
    }

    @Bean
    public StatFilter statFilter(DruidConfigurationProperties ds) {
        this.logger.info("init statFilter");
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(ds.getSlowSqlMillis());
        statFilter.setLogSlowSql(ds.isLogSlowSql());
        return statFilter;
    }

    @Bean
    public Slf4jLogFilter slf4jLogFilter(DruidConfigurationProperties ds) {
        this.logger.info("init slf4jLogFilter");
        Slf4jLogFilter logFilter = new Slf4jLogFilter();
        logFilter.setStatementExecutableSqlLogEnable(true);
        return logFilter;
    }

}
