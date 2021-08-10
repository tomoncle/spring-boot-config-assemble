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

package com.tomoncle.config.springboot.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;


@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class SpringbootConfigRedisConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SpringbootConfigRedisConfiguration.class);

    private final RedisProperties redisProperties;

    public SpringbootConfigRedisConfiguration(RedisProperties rp1) {
        logger.info("Initializing RedisConfiguration, load configuration standalone mode.");
        this.redisProperties = rp1;
    }

    /**
     * 单机模式
     *
     * @return RedisStandaloneConfiguration
     */
    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        logger.debug("Initializing RedisStandaloneConfiguration.");
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setDatabase(redisProperties.getDatabase());
        standaloneConfiguration.setHostName(redisProperties.getHost());
        if (null != redisProperties.getPassword() && redisProperties.getPassword().length() > 0) {
            standaloneConfiguration.setPassword(redisProperties.getPassword());
        }
        standaloneConfiguration.setPort(redisProperties.getPort());
        return standaloneConfiguration;
    }


    @Bean
    @ConditionalOnMissingBean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        if (null == pool) {
            pool = new RedisProperties.Pool();
        }
        jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
        jedisPoolConfig.setMinIdle(pool.getMinIdle());
        jedisPoolConfig.setMaxTotal(pool.getMaxActive());
        jedisPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(false);
        return jedisPoolConfig;
    }

    @Bean
    @ConditionalOnMissingBean(name = "jedisClientConfiguration")
    public JedisClientConfiguration jedisClientConfiguration(JedisPoolConfig jedisPoolConfig) {
        final Duration timeout = null == redisProperties.getTimeout() ? Duration.ofMinutes(10) : redisProperties.getTimeout();
        return JedisClientConfiguration.builder()
                .usePooling().poolConfig(jedisPoolConfig)
                .and()
                .readTimeout(timeout)
                .connectTimeout(timeout)
                .build();
    }

    /**
     * @param redisStandaloneConfiguration 单机配置
     * @return RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
                                                         JedisClientConfiguration jedisClientConfiguration) {
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }


}
