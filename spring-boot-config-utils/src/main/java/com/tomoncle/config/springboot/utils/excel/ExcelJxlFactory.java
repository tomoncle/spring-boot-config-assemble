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

import com.google.common.collect.Lists;
import com.tomoncle.config.springboot.utils.DateUtils;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;

import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Excel导入工具类
 *
 * @author tomoncle
 */
public class ExcelJxlFactory<E> implements ExcelService<Workbook, E> {

    /**
     * 注此类现在只能对单表作用，以后会完善此功能
     * 导入方法
     * 通过反射自动注入
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<E> excelImport(Class clazz, Workbook excel) {
        if (excel == null) {
            return null;
        }
        List<E> lists = new ArrayList<>();
        try {
            Sheet sheet = excel.getSheet(0);
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 1; i < sheet.getRows(); i++) {
                Object object = clazz.newInstance();
                for (int j = 0; j < sheet.getColumns(); j++) {
                    if (sheet.getCell(j, 0).getContents().replaceAll(" ", "").length() == 0) {
                        break;
                    }
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String data = sheet.getCell(j, i).getContents();
                        ExcelConfiguration annotation = field.getAnnotation(ExcelConfiguration.class);
                        if (annotation != null) {
                            if (!annotation.importName().equals("##default") && annotation.importName().equals(
                                    sheet.getCell(j, 0).getContents().replaceAll(" ", ""))) {
                                setValue(object, field, data);
                                break;
                            }
                        }
                    }
                }
                lists.add((E) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        excel.close();
        return lists;


    }


    /**
     * excel 导出工具类
     *
     * @param lists        　数据集合
     * @param clazz        　操作类
     * @param outputStream 　输出流
     */
    @Override
    public void excelExport(List<E> lists, Class clazz, OutputStream outputStream) {
        WritableWorkbook excel = null;
        final int maxRows = 65535;
        try {
            excel = Workbook.createWorkbook(outputStream);
            List partition = Lists.partition(lists, maxRows);
            for (int i = 0; i < partition.size(); i++) {
                WritableSheet sheet = excel.createSheet("sheet" + (i + 1), i);
                sheet.getSettings().setDefaultColumnWidth(20);
                writeTitle(clazz, sheet);
                if (partition.get(i) != null && ((List) partition.get(i)).size() > 0) {
                    writeContents(((List) partition.get(i)), sheet);
                }
            }
            if (partition.size() > 0) {
                excel.write(); // 文件写入
            } else {
                excel = Workbook.createWorkbook(outputStream);
                WritableSheet sheet = excel.createSheet("sheet", 0);
                sheet.getSettings().setDefaultColumnWidth(20);
                writeTitle(clazz, sheet);
                if (lists.size() > 0) {
                    writeContents(lists, sheet);
                }
                excel.write();// 文件写入
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (excel != null)
                    excel.close();// 对象关闭
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createTemplate(Class clazz, OutputStream outputStream) {
        WritableWorkbook excel = null;
        try {
            excel = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = excel.createSheet("sheet0", 0);
            sheet.getSettings().setDefaultColumnWidth(20);
            writeTitle(clazz, sheet);
            excel.write();// 文件写入
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (excel != null)
                    excel.close();// 对象关闭
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载值给对应属性
     */
    private void setValue(Object object, Field field, String value) throws Exception {
        switch (field.getType().toString()) {
            case "class java.lang.String":
                field.set(object, value);
                break;
            case "int":
                field.setInt(object, Integer.valueOf(value));
                break;
            case "class java.lang.Integer":
                field.set(object, Integer.valueOf(value));
                break;
            case "double":
                field.set(object, Double.valueOf(value));
                break;
            case "float":
                field.set(object, Float.valueOf(value));
                break;
            case "class java.util.Date":
                field.set(object, DateUtils.parseDate(value));
                break;
        }
    }

    /**
     * 校验是否为空
     * 以后会改善使用注解自动检测
     *
     * @param list  数据列表
     * @param array 属性名称 数组必须和类一致
     * @return boolean
     */
    public boolean validate(List<E> list, String[] array) {
        try {
            for (Object t : list) {
                for (String string : array) {
                    PropertyDescriptor pd = new PropertyDescriptor(string,
                            t.getClass());
                    Method method = pd.getReadMethod();
                    Object o = method.invoke(t);
                    if (o == null || o.equals("")) {
                        System.err.println(string + "不能为空");
                        return false;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 写入头部
     *
     * @param T     类对象
     * @param sheet 　WritableSheet
     */
    private void writeTitle(Class T, WritableSheet sheet) throws WriteException {
        Field[] fields = T.getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            ExcelConfiguration annotation = field.getAnnotation(ExcelConfiguration.class);
            if (annotation != null) {
                if (!annotation.exportName().equals("##default")
                        && !annotation.exportName().equals("")) {
                    WritableFont font = new WritableFont(WritableFont.ARIAL, 14,
                            WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLUE);
                    WritableCellFormat cellFormat = new WritableCellFormat(font);
                    cellFormat.setWrap(true);
                    cellFormat.setAlignment(Alignment.CENTRE);
                    cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
                    Label label = new Label(index, 0, annotation.exportName(), cellFormat);
                    sheet.addCell(label);
                    index++;
                }
            }

        }
    }

    /**
     * 写入内容
     *
     * @param lists 　数据
     * @param sheet 　　WritableSheet
     */
    private void writeContents(List lists, WritableSheet sheet) throws Exception {
        for (int i = 0; i < lists.size(); i++) {
            Object object = lists.get(i);
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int index = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                ExcelConfiguration annotation = field.getAnnotation(ExcelConfiguration.class);
                if (annotation != null) {
                    if (!annotation.exportName().equals("##default")
                            && !annotation.exportName().equals("")) {
                        WritableCellFormat cellFormat = new WritableCellFormat();
                        cellFormat.setWrap(true);
                        Label label = new Label(index, i + 1, String.valueOf(field.get(object)), cellFormat);
                        sheet.addCell(label);
                        index++;
                    }
                }
            }

        }
    }


}



