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

import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * file exec utils
 * <p>
 * Reference Of https://github.com/spring-guides/gs-uploading-files
 */


public class LocalFileServiceImpl implements LocalFileService {

    private final Path uploadDir;

    public LocalFileServiceImpl(String rootPath) {
        this.uploadDir = Paths.get(rootPath);
        this.createDir(rootPath);
    }

    @Override
    public void save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new LocalFileServiceException("Failed to store empty file " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), this.uploadDir.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new LocalFileServiceException("Failed to save file " + file.getOriginalFilename() + e.getMessage());
        }
    }

    @Override
    public void saveOrReplace(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new LocalFileServiceException("Failed to store empty file " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(),
                    this.uploadDir.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new LocalFileServiceException("Failed to save file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.uploadDir, 1)
                    .filter(path -> !path.equals(this.uploadDir))
                    .map(this.uploadDir::relativize);
        } catch (IOException e) {
            throw new LocalFileServiceException("Failed to read system files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return this.uploadDir.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new LocalFileServiceException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new LocalFileServiceException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void createDir(String dirName) {
        if (exists(dirName)) {
            return;
        }
        try {
            Files.createDirectories(Paths.get(dirName));
        } catch (IOException e) {
            throw new LocalFileServiceException("Could not create dir.", e);
        }
    }

    @Override
    public void deleteDir(String dirName) {
        if (exists(dirName)) {
            FileSystemUtils.deleteRecursively(Paths.get(dirName).toFile());
        }

    }

    @Override
    public boolean exists(String path) {
        if (Strings.isBlank(path)) {
            return false;
        }
        return Files.exists(Paths.get(path));
    }
}
