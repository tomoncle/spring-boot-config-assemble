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

package com.tomoncle.config.springboot.utils.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义注解，用于实现对Excel 的导入导出做标识
 * "@Retention(RUNTIME)" //运行时通过反射被读取
 * "@Target({ FIELD })"  //修饰属性
 * <p>
 * Created by tomoncle on 2016/12/2.
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface ExcelConfiguration {

    /**
     * 导入标识
     */
    String importName() default "##default";

    /**
     * 导出标识
     */
    String exportName() default "##default";
}
