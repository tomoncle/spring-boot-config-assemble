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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.util.List;


/**
 * poi excel utils
 * Created by tomoncle on 2016/12/2.
 */
public class ExcelPoiFactory<E> implements ExcelService<HSSFWorkbook, E> {

    private ExcelXmlParse<E> excelXmlParse;
    private String xmlPath;

    public ExcelPoiFactory(String xmlPath) {
        this.xmlPath = xmlPath;
        this.excelXmlParse = new ExcelXmlParse<>();
    }

    @Override
    public List<E> excelImport(Class clazz, HSSFWorkbook excel) {
        return this.excelXmlParse.excelImport(clazz, excel, xmlPath);
    }

    @Override
    public void excelExport(List lists, Class clazz, OutputStream outputStream) {
        this.excelXmlParse.excelExport(lists, outputStream, xmlPath);
    }

    @Override
    public void createTemplate(Class clazz, OutputStream outputStream) {
        this.excelXmlParse.createTemplate03(xmlPath, outputStream);
    }
}
