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

package com.tomoncle.config.springboot.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * @author tomoncle
 */
public final class JsonConverter {

    private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonConverter() {
    }


    /**
     * Java对象 转 json
     */
    public static <T> String toJson(@NotNull final T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Parse Java object error: " + e.getMessage());
        }
        return null;
    }

    /**
     * json 字符串转 Java对象
     */
    public static <T> T toObject(@NotNull final String json, @NotNull final Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("Parse json data error: " + e.getMessage());
        }
        return null;
    }


    /**
     * json 字符串数组 转 Java集合
     */
    public static <T> List<T> toObjects(@NotNull final String json, @NotNull final Class<T> clazz) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructParametricType(List.class, clazz));
        } catch (IOException e) {
            logger.error("Parse json data error: " + e.getMessage());
        }
        return null;
    }


}
