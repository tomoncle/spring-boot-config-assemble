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

package com.tomoncle.test.springboot.utils;

import com.tomoncle.config.springboot.utils.http.OkHttpRequest;
import lombok.SneakyThrows;
import okhttp3.Headers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tomoncle
 */
public class OkHttpRequestTest {

    @SneakyThrows
    @Test
    public void testReq() {
        System.out.println(OkHttpRequest.X_509_TRUST_MANAGER);

    }

    @SneakyThrows
    @Test
    public void post() {
        String url = "https://api.tomoncle.com/?user=tom&age=21";
        String response = OkHttpRequest.POST.request(url);
        System.out.println(response);
    }

    @SneakyThrows
    @Test
    public void postBody() {
        String url = "https://api.tomoncle.com/?user=tom&age=21";
        OkHttpRequest.FormMap map = new OkHttpRequest.FormMap();
        map.put("id", 12);
        map.put("names", new ArrayList<String>() {{
            add("Jackson");
            add("Michael");
        }});
        String response = OkHttpRequest.POST.request(url, map);

        System.out.println(response);
    }

    @SneakyThrows
    @Test
    public void get() {
        String url = "https://api.tomoncle.com/?user=tom&age=21";
        Map<String, String> map = new HashMap<>();
        map.put("token", "123456");
        String response = OkHttpRequest.GET.request(url, Headers.of(map));
        System.out.println(response);
    }

    @Test
    public void test2() {
        A a = new A();
        a.p();

        A a1 = new B();
        a1.p();

        B a2 = new B();
        a2.p();
    }

    class A {
        public void p() {
            System.out.println("A.p");
        }
    }

    class B extends A {
        @Override
        public void p() {
            System.out.println("B.p");
        }
    }

}
