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

package com.tomoncle.config.springboot.ch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tomoncle
 */
@Data
@ConfigurationProperties("spring.boot.config.clickhouse")
public class ClickHouseProperties {
    private String cipher;
    private String scheme = "http";
    private String hostname = "127.0.0.1";
    private Integer port = 8123;
    private String path = "/";
    private String username = "default";
    private String password = "";
    private String database = "default";
    private Integer maxResultRows = 1000000;
    private Integer maxExecutionTimes = 60;
    private boolean validator = false;
    private boolean showSql = false;

}
