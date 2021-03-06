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

package com.tomoncle.config.springboot.utils;

import com.tomoncle.config.springboot.constant.SpringBootConfig;

/**
 * Created by tomoncle on 18-5-23.
 */
public final class SpringBootConfigTools {

    public static String buildConfigPropertyName(String model, String name) {
        return String.format("%s.%s.%s", SpringBootConfig.CONFIG_PREFIX, model, name);
    }

    public static String buildConfigPropertyModelPrefix(String model) {
        return String.format("%s.%s", SpringBootConfig.CONFIG_PREFIX, model);
    }
}
