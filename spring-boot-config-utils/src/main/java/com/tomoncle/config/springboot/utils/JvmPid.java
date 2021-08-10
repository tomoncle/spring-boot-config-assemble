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

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Created by tomoncle on 18-5-23.
 */
public final class JvmPid {
    /**
     * 获取当前运行程序的pid
     *
     * @return 获取当前运行程序的pid
     */
    public static Integer getPid() {
        final String name = ManagementFactory.getRuntimeMXBean().getName();
        final String pid = name.split("@")[0];
        return Integer.valueOf(pid);
    }

    /**
     * 杀死指定的pid进程
     *
     * @param pid 要杀死的进程pid
     */
    public static void killPid(final int pid) {
        final String[] cmdArray = {"kill", "-9", String.valueOf(pid)};
        try {
            Runtime.getRuntime().exec(cmdArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
