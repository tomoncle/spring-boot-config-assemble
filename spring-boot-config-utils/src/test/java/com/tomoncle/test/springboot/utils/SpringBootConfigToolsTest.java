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

/**
 * Created by tomoncle on 18-5-23.
 */
public class SpringBootConfigToolsTest {

    public static void main(String[] args) {
        {
            byte[] placeholder = new byte[64 * 1024 * 1024];
        }
        //[GC (System.gc())  73400K->66752K(375296K), 0.0304336 secs]
        //[Full GC (System.gc())  66752K->66560K(375296K), 0.0054942 secs]
        // placeholder=null;
        //[GC (System.gc())  73400K->1216K(375296K), 0.0011874 secs]
        //[Full GC (System.gc())  1216K->1024K(375296K), 0.0046524 secs]
        int i = 0;
        System.gc();
        System.out.println("\n\n");
    }

}
