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

package com.tomoncle.test.springboot.handler;

import com.tomoncle.config.springboot.constant.SpringBootConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * http tools
 * Created by tomoncle on 18-5-17.
 */
@RestController
@SpringBootApplication(scanBasePackages = {SpringBootConfig.SCAN_BASE_PACKAGE})
public class TestErrorApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestErrorApplication.class, args);
    }

    @RequestMapping("/index")
    String index() {
        return "welcome index.";
    }

    @RequestMapping("/num")
    Integer num() {
        return 1 / 0;
    }
}
