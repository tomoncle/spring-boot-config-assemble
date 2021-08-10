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

package com.tomoncle.config.springboot.tomcat;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.AprLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Collection;
import java.util.Collections;

/**
 * @author tomoncle
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ConfigProperties.class})
public class ConfigurableTomcat implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    private final Logger logger = LoggerFactory.getLogger(ConfigurableTomcat.class);

    private final ConfigProperties configProperties;

    public ConfigurableTomcat(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
        factory.setContextLifecycleListeners(getContextLifecycleListeners());
        logger.info("Tomcat container enable APR running mode.");
    }

    public Collection<? extends LifecycleListener> getContextLifecycleListeners() {
        final AprLifecycleListener aprLifecycleListener = new AprLifecycleListener();
        if (configProperties.isUseAprConnector()) {
            aprLifecycleListener.setUseAprConnector(true);
        }
        if (!configProperties.isSslEngine()) {
            aprLifecycleListener.setSSLEngine("off");
        }
        if (!configProperties.isUseOpenSSL()) {
            aprLifecycleListener.setUseOpenSSL(configProperties.isUseOpenSSL());
        }
        return Collections.singleton(aprLifecycleListener);
    }
}
