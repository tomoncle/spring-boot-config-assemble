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

package com.tomoncle.test.springboot.zuul;

import com.tomoncle.config.springboot.utils.http.OkHttpRequest;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by tomoncle on 18-5-28.
 */
public class ZuulTest {

    @Test
    public void getTomoncle() throws IOException {
        final String url = "http://localhost:8601/tomoncle/tags/index.html";
        final String request = OkHttpRequest.GET.request(url);
        System.out.println(request);
    }

    @Test
    public void getBaidu() throws IOException {
        final String url = "http://localhost:8601/baidu/s?wd=ssss";
        final String request = OkHttpRequest.GET.request(url);
        System.out.println(request);
    }

//    ZuulController

    @Test
    public void testString() {
        System.out.println(String.format("/%s", "qq"));
        System.out.println(String.format("%s%s", "pp", "qq"));
    }


}
