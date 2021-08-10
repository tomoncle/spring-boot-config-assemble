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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

/**
 * @author tomoncle
 */
public class StringClickHouseQuery extends ClickHouseQuery<String> {
    public StringClickHouseQuery(ClickHouseProperties clickHouseProperties) {
        super(clickHouseProperties);
    }

    @Override
    public ResponseEntity<String> execute(String sql, Class<String> stringClass) {
        try {
            return super.execute(sql, stringClass);
        } catch (RestClientResponseException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.valueOf(e.getRawStatusCode()));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> execute(String sql) {
        return this.execute(sql, String.class);
    }
}
