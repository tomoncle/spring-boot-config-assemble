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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 *
 * @author tomoncle
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class DateUtils {
    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("Europe/London");
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
    // 线程不安全，应该使用 ThreadLocal
    // private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static LocalDate parseLocalDate(String text) {
        return LocalDate.parse(text, DATE_TIME_FORMATTER);
    }


    public static LocalDate parseLocalDate(String text, String formatter) {
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(formatter));
    }


    public static ZonedDateTime parseZonedDateTime(String text) {
        return ZonedDateTime.parse(text);
    }


    public static ZonedDateTime shanghaiZonedNowDateTime() {
        return ZonedDateTime.now(SHANGHAI_ZONE_ID);
    }


    public static Date now() {
        return Date.from(shanghaiZonedNowDateTime().toInstant());
    }

    public static LocalDateTime localDateTime(Long timestamp) {
        return localDateTime(timestamp, SHANGHAI_ZONE_ID);
    }

    public static LocalDateTime localDateTime(Long timestamp, ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
    }

    public static String timestampFormat(Long timestamp) {
        return DATE_TIME_FORMATTER.format(localDateTime(timestamp));
    }

    public static String timestampFormat(Long timestamp, String format) {
        return timestampFormat(timestamp, format, SHANGHAI_ZONE_ID);
    }

    public static String timestampFormat(Long timestamp, String format, ZoneId zoneId) {
        return DateTimeFormatter.ofPattern(format).format(localDateTime(timestamp, zoneId));
    }


    public static String currentTimeString() {
        return currentTimeString(DEFAULT_PATTERN);
    }


    public static String currentTimeString(String format) {
        return shanghaiZonedNowDateTime().format(DateTimeFormatter.ofPattern(format));
    }


    public static Date nowMinusSeconds(int seconds) {
        return Date.from(shanghaiZonedNowDateTimeMinusSeconds(seconds).toInstant());
    }


    public static Date nowMinusMinutes(int seconds) {
        return Date.from(shanghaiZonedNowDateTimeMinusSeconds(seconds).toInstant());
    }


    public static Date nowMinusHours(int hours) {
        return Date.from(shanghaiZonedNowDateTime().minusHours(hours).toInstant());
    }


    public static Date nowMinusDays(int days) {
        return Date.from(shanghaiZonedNowDateTime().minusDays(days).toInstant());
    }


    public static ZonedDateTime shanghaiZonedNowDateTimeMinusSeconds(int seconds) {
        return ZonedDateTime.now(SHANGHAI_ZONE_ID).minusSeconds(seconds);
    }


    public static ZonedDateTime utcZonedNowDateTime() {
        return ZonedDateTime.now(UTC_ZONE_ID);
    }


    public static String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DATE_TIME_FORMATTER);
    }


    public static String formatT(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime).replace(" ", "T");
    }


    public static Date parseDate(String text) {
        try {
            return Date.from(LocalDateTime.parse(text, DATE_TIME_FORMATTER).atZone(SHANGHAI_ZONE_ID).toInstant());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date parseDate(String text, String formatter) {
        try {
            return new SimpleDateFormat(formatter).parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date parseDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }


    public static Long timestamp(TimeUnit timeUnit) {
        switch (timeUnit) {
            case DAYS:
                return parse(currentTimeString("yyyy-MM-dd").concat(" 00:00:00"));
            case HOURS:
                return parse(currentTimeString("yyyy-MM-dd HH").concat(":00:00"));
            case MINUTES:
                return parse(currentTimeString("yyyy-MM-dd HH:mm").concat(":00"));
            case SECONDS:
            default:
                return parse(currentTimeString());
        }
    }


    public static Long timestampOfMinus(TimeUnit timeUnit, int minus) {
        switch (timeUnit) {
            case DAYS:
                return timestamp(timeUnit) - 86400 * minus;
            case HOURS:
                return timestamp(timeUnit) - 3600 * minus;
            case MINUTES:
                return timestamp(timeUnit) - 60 * minus;
            case SECONDS:
            default:
                return timestamp(timeUnit) - minus;
        }
    }


    public static Long timestampOfPlus(TimeUnit timeUnit, int plus) {
        switch (timeUnit) {
            case DAYS:
                return timestamp(timeUnit) + 86400 * plus;
            case HOURS:
                return timestamp(timeUnit) + 3600 * plus;
            case MINUTES:
                return timestamp(timeUnit) + 60 * plus;
            case SECONDS:
            default:
                return timestamp(timeUnit) + plus;
        }
    }

    private static Long parse(String dateString) {
        Date date = parseDate(dateString);
        if (null == date) {
            return -1L;
        }
        return date.getTime() / 1000;
    }

}
