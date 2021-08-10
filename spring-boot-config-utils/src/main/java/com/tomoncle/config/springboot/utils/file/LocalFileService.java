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

package com.tomoncle.config.springboot.utils.file;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface LocalFileService extends FileSystemService {

    /**
     * @param file
     */
    void save(MultipartFile file);

    /**
     * @param file
     */
    void saveOrReplace(MultipartFile file);

    /**
     * 获取目录文件列表
     *
     * @return
     */
    Stream<Path> loadAll();


    /**
     * 获取文件
     *
     * @param filename file full path
     * @return
     */
    Path load(String filename);


    /**
     * 获取文件为 Resource
     *
     * @param filename
     * @return
     */
    Resource loadAsResource(String filename);


}
