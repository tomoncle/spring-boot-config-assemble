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

package com.tomoncle.config.springboot.ch.query;

import com.tomoncle.config.springboot.ch.ClickHouseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author tomoncle
 */
public class ClickHouseQuery<T> {
    private static Logger logger = LoggerFactory.getLogger(ClickHouseQuery.class);
    private final ClickHouseProperties clickHouseProperties;
    private final RestTemplate restTemplate;
    private final String url;

    public ClickHouseQuery(ClickHouseProperties clickHouseProperties) {
        this.clickHouseProperties = clickHouseProperties;
        this.restTemplate = new RestTemplate();
        this.url = this.buildUrl();
    }


    private String buildUrl() {
        String cipher = clickHouseProperties.getCipher();
        Map<String, String> parameters = new HashMap<>();
        String scheme = clickHouseProperties.getScheme();
        String hostname = clickHouseProperties.getHostname();
        Integer port = clickHouseProperties.getPort();
        String path = clickHouseProperties.getPath();
        if (!StringUtils.isEmpty(cipher)) {
            String decodeUrl = new String(Base64.getDecoder().decode(cipher.getBytes()));
            UriComponents build = UriComponentsBuilder.fromHttpUrl(decodeUrl).build();
            MultiValueMap<String, String> queryParams = build.getQueryParams();
            for (String key : queryParams.keySet()) {
                parameters.put(key, queryParams.getFirst(key));
            }
            if (null != build.getScheme()) {
                scheme = build.getScheme();
            }
            if (null != build.getHost()) {
                hostname = build.getHost();
            }
            if (null != build.getPath()) {
                path = build.getPath();
            }
            port = build.getPort();
        }
        parameters.putIfAbsent("add_http_cors_header", "1");
        parameters.putIfAbsent("log_queries", "1");
        parameters.putIfAbsent("output_format_json_quote_64bit_integers", "1");
        parameters.putIfAbsent("output_format_json_quote_denormals", "1");
        parameters.putIfAbsent("user", clickHouseProperties.getUsername());
        parameters.putIfAbsent("password", clickHouseProperties.getPassword());
        parameters.putIfAbsent("database", clickHouseProperties.getDatabase());
        parameters.putIfAbsent("result_overflow_mode", "throw");
        parameters.putIfAbsent("max_result_rows", String.valueOf(clickHouseProperties.getMaxResultRows()));
        parameters.putIfAbsent("timeout_overflow_mode", "throw");
        parameters.putIfAbsent("max_execution_time", String.valueOf(clickHouseProperties.getMaxExecutionTimes()));
        StringBuilder stringBuilder = new StringBuilder(scheme).append("://").append(hostname).append(":").append(port)
                .append(path).append("?");
        for (String key : parameters.keySet()) {
            if (!stringBuilder.toString().endsWith("?")) {
                stringBuilder.append("&");
            }
            stringBuilder.append(key).append("=").append(parameters.get(key));
        }
        return stringBuilder.toString();
    }

    public ResponseEntity<T> execute(String sql, Class<T> tClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("query", sql);
        if (clickHouseProperties.isShowSql()) {
            logger.info("SHOW SQL: " + sql.replaceAll("\\s+", " "));
            //  logger.info("SHOW SQL: " + sql.replaceAll(" +", " "));
        }
        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map, headers), tClass);
    }

    public boolean authentication() {
        try {
            return Objects.equals(restTemplate.getForObject(url + "&query=SELECT+1", String.class), "1\n");
        } catch (RuntimeException e) {
            return false;
        }
    }

}
