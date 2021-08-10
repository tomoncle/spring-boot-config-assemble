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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * http://localhost:8088/swagger-ui.html
 *
 * @Time : 2017/12/3 17:01
 * @Author : tomoncle
 * @File : SwaggerConfiguration.java
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SwaggerProperties.class)
public class EnableSwaggerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(EnableSwaggerConfiguration.class);

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Bean
    public Docket createAllRestApi() {
        logger.info("EnableSwaggerConfiguration createAllRestApi");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName(swaggerProperties.getGroupName())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getScanBasePackage()))
//                .paths(Predicates.or(PathSelectors.regex("/.*")))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getApiTitle())
                .version(swaggerProperties.getVersion())
                .build();
    }
}
