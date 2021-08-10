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

package com.tomoncle.config.springboot;

import com.tomoncle.config.springboot.constant.SpringBootConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;

/**
 * 配置包扫描路径
 * Created by tomoncle on 18-5-22.
 */
@Lazy(value = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ComponentScan({SpringBootConfig.SCAN_BASE_PACKAGE})
public class EnableSpringBootConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnableSpringBootConfig.class);

    public EnableSpringBootConfig() {
        logger.info("load Configuration :" + SpringBootConfig.SCAN_BASE_PACKAGE);
    }
}
