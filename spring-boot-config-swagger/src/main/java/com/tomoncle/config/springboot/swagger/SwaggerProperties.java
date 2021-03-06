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

package com.tomoncle.config.springboot.swagger;

import com.tomoncle.config.springboot.constant.SpringBootConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 2018/5/20 9:38
 * tomoncle
 * SwaggerProperties.java
 */

@ConfigurationProperties(prefix = SpringBootConfig.CONFIG_PREFIX + ".swagger")
public class SwaggerProperties {
    private String apiTitle = "swagger UI";
    private String version = "v1.0";
    private String scanBasePackage = "com.**.controller";
    private String groupName = "all";

    public String getApiTitle() {
        return apiTitle;
    }

    public void setApiTitle(String apiTitle) {
        this.apiTitle = apiTitle;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScanBasePackage() {
        return scanBasePackage;
    }

    public void setScanBasePackage(String scanBasePackage) {
        this.scanBasePackage = scanBasePackage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
