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

package com.tomoncle.config.springboot.ch;

import com.tomoncle.config.springboot.ch.query.StringClickHouseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tomoncle
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ClickHouseInitializer {
    private final static ClickHouseInitializer clickHouseInitializer = ClickHouseInitializer.getAnonymousInstance();
    private static Logger logger = LoggerFactory.getLogger(ClickHouseInitializer.class);
    private final StringClickHouseQuery stringClickHouseQuery;
    private List<String> sqlList = new ArrayList<>();

    public ClickHouseInitializer(StringClickHouseQuery stringClickHouseQuery) {
        this.stringClickHouseQuery = stringClickHouseQuery;
        this.initializer();
    }

    private ClickHouseInitializer() {
        this.stringClickHouseQuery = null;
    }

    private static ClickHouseInitializer getAnonymousInstance() {
        return new ClickHouseInitializer() {
            @Override
            public void loadContent() {
            }
        };
    }

    public abstract void loadContent();

    /**
     * @param content sql content
     */
    public void add(@NonNull String content) {
        if (StringUtils.isEmpty(content) || StringUtils.trimAllWhitespace(content).length() == 0) {
            throw new NullPointerException("SQL content can not be null.");
        }
        this.sqlList.add(content);
    }

    /**
     * @param paths file paths
     */
    public void addFilePaths(String... paths) {
        for (String path : paths) {
            this.add(read(path));
        }
    }

    /**
     * file reader
     * location: resources/sql/npm/db_npm.sql
     * input: /sql/npm/db_npm.sql
     *
     * @param path file path
     * @return file content
     */
    public String read(String path) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                clickHouseInitializer.getClass().getResourceAsStream(path)))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while (true) {
                line = bufferedReader.readLine();
                if (line == null || line.length() == 0) {
                    break;
                }
                stringBuilder.append(line).append(" ");
            }
            return stringBuilder.toString().replaceAll("\\s+", " ");
        } catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化
     */
    private void initializer() {
        this.loadContent();
        for (String sql : sqlList) {
            ResponseEntity<String> execute = this.stringClickHouseQuery.execute(sql, String.class);
            if (execute.getStatusCodeValue() != 200) {
                logger.error("执行SQL失败：" + sql + " ；返回值: " + execute.getBody());
            } else {
                logger.debug("执行SQL成功: " + sql);
            }
        }
    }
}
