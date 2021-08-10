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

import java.net.InetAddress;

/**
 * 参考自：https://blog.csdn.net/neolance/article/details/6253750 , https://www.cnblogs.com/victorbu/p/11098647.html
 *
 * @author tomoncle
 */
public final class GenUUID {
    private static final int IP;
    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
    private final static String sep = "";
    private static short counter = (short) 0;

    static {
        int ipadd;
        try {
            ipadd = ipToInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }

    private GenUUID() {
    }

    private static int ipToInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }

    public static GenUUID build() {
        return SingletonHolder.instance;
    }

    /**
     * Unique across JVMs on this machine (unless they load this class
     * in the same quater second - very unlikely)
     */
    private int getJVM() {
        return JVM;
    }

    /**
     * Unique in a millisecond for this JVM instance (unless there
     * are > Short.MAX_VALUE instances created in a millisecond)
     */
    private short getCount() {
        synchronized (GenUUID.class) {
            if (counter < 0) counter = 0;
            return counter++;
        }
    }

    /**
     * Unique in a local network
     */
    private int getIP() {
        return IP;
    }

    /**
     * Unique down to millisecond
     */
    private short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }

    private int getLoTime() {
        return (int) System.currentTimeMillis();
    }

    private String format(int value) {
        String formatted = Integer.toHexString(value);
        StringBuilder sb = new StringBuilder("00000000");
        sb.replace(8 - formatted.length(), 8, formatted);
        return sb.toString();
    }

    private String format(short value) {
        String formatted = Integer.toHexString(value);
        StringBuilder sb = new StringBuilder("0000");
        sb.replace(4 - formatted.length(), 4, formatted);
        return sb.toString();
    }

    public String generate() {
        return format(getIP()) + format(getJVM()) + format(getHiTime()) + format(getLoTime()) + format(getCount());
    }

    private static class SingletonHolder {
        private final static GenUUID instance = new GenUUID();
    }

}