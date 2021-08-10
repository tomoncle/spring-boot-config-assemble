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

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * xml解析
 * Created by tomoncle on 2016/12/3.
 */
public class ExcelXmlParse<E> {
    public static final String SUFFIX_2003 = ".xls";
    public static final String SUFFIX_2007 = ".xlsx";
    private int rowNum = 0;

    @SuppressWarnings("unchecked")
    public List<E> excelImport(Class clazz, HSSFWorkbook workbook, String xmlPath) {
        List<E> list = new ArrayList<>();
        try {
            Element root = getRootElement(xmlPath);
            assert root != null;
            //获取指针
            int headIndex = root.getChild("title") == null
                    ? 0 : Integer.valueOf(root.getChild("title").getAttributeValue("rowSpan"));
            //读取默认第一个工作表sheet
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow headRow = sheet.getRow(headIndex);
            //获取sheet中最后一行行号
            int lastRowNum = sheet.getLastRowNum();
            for (int i = headIndex + 1; i <= lastRowNum; i++) {
                Object object = clazz.newInstance();
                HSSFRow row = sheet.getRow(i);
                //获取当前行最后单元格列号
                int lastCellNum = headRow.getLastCellNum();
                for (int j = 0; j < lastCellNum; j++) {
                    HSSFCell cell = row.getCell(j);
                    HSSFCell headCell = headRow.getCell(j);
                    String fieldName = getXmlFieldName(root, headCell.getStringCellValue());
                    if (StringUtils.isEmpty(fieldName)) {
                        continue;
                    }
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    setValue(object, field, cell);
                }
                list.add((E) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void excelExport(List lists, OutputStream outputStream, String xmlPath) {
        HSSFWorkbook workbook = null;
        try {
            Element root = getRootElement(xmlPath);
            assert root != null;
            //创建workbook
            workbook = new HSSFWorkbook();
            String sheetName = root.getAttributeValue("sheet");
            HSSFSheet sheet = workbook.createSheet(sheetName);
            writeHSSFWorkbook(workbook, root, sheet, lists);
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建03Excel模板
     *
     * @param xmlPath   model.xml
     * @param buildPath created path
     */
    public void createTemplate03(String xmlPath, String buildPath) {
        try {
            Element root = getRootElement(xmlPath);
            assert root != null;
            String templateName = root.getAttributeValue("name");
            FileOutputStream fileOutputStream = new FileOutputStream(buildPath + templateName + SUFFIX_2003);
            createTemplate03(xmlPath, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建03Excel模板
     *
     * @param xmlPath      model.xml
     * @param outPutStream 文件输出流
     */
    public void createTemplate03(String xmlPath, OutputStream outPutStream) {
        HSSFWorkbook workbook = null;
        try {
            Element root = getRootElement(xmlPath);
            assert root != null;
            //创建workbook
            workbook = new HSSFWorkbook();
            String sheetName = root.getAttributeValue("sheet");
            HSSFSheet sheet = workbook.createSheet(sheetName);
            writeHSSFWorkbook(workbook, root, sheet);
            setContentStyle(workbook, root, sheet);
            workbook.write(outPutStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取xml根节点
     *
     * @return root Element
     */
    private Element getRootElement(String xmlPath) {
        try {
            Document document = new SAXBuilder().build(new File(xmlPath));
            if (null != document) {
                return document.getRootElement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置单元格宽度
     *
     * @param root  ElementRoot
     * @param sheet HSSFSheet
     */
    private void setWidth(Element root, HSSFSheet sheet) {
        //设置列宽
        Element columnGroup = root.getChild("columnGroup");
        List columns = columnGroup.getChildren("column");
        for (int i = 0; i < columns.size(); i++) {
            final Element column = (Element) columns.get(i);
            String unit = column.getAttributeValue("width").replaceAll("[0-9\\.]", "");
            String value = column.getAttributeValue("width").replaceAll(unit, "");
            int width = 0;
            if (StringUtils.isEmpty(unit) || "px".endsWith(unit)) {
                width = Math.round(Float.parseFloat(value) * 37F);
            } else if ("em".endsWith(unit)) {
                width = Math.round(Float.parseFloat(value) * 267.5F);
            }
            sheet.setColumnWidth(i, width);
        }
    }

    /**
     * 设置标题
     *
     * @param workbook HSSFWorkbook
     * @param root     ElementRoot
     * @param sheet    HSSFSheet
     */
    private void setTitle(HSSFWorkbook workbook, Element root, HSSFSheet sheet) {
        Element title = root.getChild("title");
        HSSFRow titleRow = sheet.createRow(rowNum);
        HSSFCell titleCell = titleRow.createCell(0);
        int rowSpan = Integer.valueOf(title.getAttributeValue("rowSpan"));
        int colSpan = Integer.valueOf(title.getAttributeValue("colSpan"));
        String titleName = title.getAttributeValue("name");
        int size = Integer.valueOf(title.getAttributeValue("size"));
        //设置单元格字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) size);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        this.writeCell(workbook, titleCell, titleName, font);
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, rowSpan - 1, 0, colSpan - 1));
        rowNum += rowSpan; //下标移动+1
    }

    /**
     * 获取单元格样式
     *
     * @param workbook HSSFWorkbook
     * @return HSSFCellStyle
     */
    private HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
        //设置单元格样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居中
        return cellStyle;
    }


    /**
     * 设置表头
     *
     * @param root  Element root
     * @param sheet HSSFSheet
     */
    private void setHead(HSSFWorkbook workbook, Element root, HSSFSheet sheet) {
        Element head = root.getChild("head");
        Element tr = head.getChild("tr");
        int fontSize = Integer.valueOf(tr.getAttributeValue("fontSize"));
        HSSFRow row = sheet.createRow(rowNum);
        List list = tr.getChildren("th");
        for (int j = 0; j < list.size(); j++) {
            Element th = (Element) list.get(j);
            String name = th.getAttributeValue("name");
            HSSFCell cell = row.createCell(j);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) fontSize);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            font.setColor(HSSFFont.COLOR_NORMAL);
            this.writeCell(workbook, cell, name, font);
        }
        rowNum++;
    }

    /**
     * 设置数据区域样式
     *
     * @param workbook HSSFWorkbook
     * @param root     Element root
     * @param sheet    HSSFSheet
     */
    private void setContentStyle(HSSFWorkbook workbook, Element root, HSSFSheet sheet) {
        Element content = root.getChild("content");
        Element tr = content.getChild("tr");
        int repeat = Integer.valueOf(tr.getAttributeValue("repeat"));
        List list = tr.getChildren("td");
        for (int i = 0; i < repeat; i++) {
            HSSFRow row = sheet.createRow(rowNum);
            for (int j = 0; j < list.size(); j++) {
                Element td = (Element) list.get(j);
                HSSFCell cell = row.createCell(j);
                setContentTypeOrDate(workbook, cell, td, null);
            }
            rowNum++;
        }
    }

    /**
     * 加载数据到Excel
     *
     * @param workbook HSSFWorkbook
     * @param root     ElementRoot
     * @param sheet    HSSFSheet
     * @param lists    data
     * @throws NoSuchFieldException   getDeclaredField not found
     * @throws IllegalAccessException filed.get(obj) err
     */
    private void setContentDate(
            HSSFWorkbook workbook, Element root, HSSFSheet sheet, List lists)
            throws NoSuchFieldException, IllegalAccessException {
        if (lists == null || lists.size() <= 0) {
            return;
        }
        Element content = root.getChild("content");
        Element tr = content.getChild("tr");
        List list = tr.getChildren("td");
        for (Object object : lists) {
            Class clazz = object.getClass();
            HSSFRow row = sheet.createRow(rowNum);
            for (int j = 0; j < list.size(); j++) {
                Element td = (Element) list.get(j);
                HSSFCell cell = row.createCell(j);
                String fieldName = getXmlFieldName(root, td.getAttributeValue("alias"));
                if (StringUtils.isEmpty(fieldName)) continue;
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                setContentTypeOrDate(workbook, cell, td, field.get(object));
            }
            rowNum++;
        }
    }

    /**
     * 测试单元格样式
     *
     * @param wb     HSSFWorkbook
     * @param cell   HSSFCell
     * @param column Element content column
     */
    private void setContentTypeOrDate(HSSFWorkbook wb, HSSFCell cell, Element column, Object data) {
        String type = column.getAttributeValue("type");
        String formatValue = column.getAttributeValue("format");
        HSSFDataFormat format = wb.createDataFormat();
        HSSFCellStyle cellStyle = wb.createCellStyle();
        if ("NUMERIC".equalsIgnoreCase(type)) {
            if (data != null) {
                String temp = data.toString();
                if (temp.contains(".")) {
                    cell.setCellValue(Double.valueOf(temp));
                }
                cell.setCellValue(Integer.valueOf(temp));
            }
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            formatValue = !StringUtils.isEmpty(formatValue) ? formatValue : "#,##0.00";
            cellStyle.setDataFormat(format.getFormat(formatValue));
        } else if ("STRING".equalsIgnoreCase(type)) {
            if (data != null) {
                cell.setCellValue(String.valueOf(data));
            }
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cellStyle.setDataFormat(format.getFormat("@"));
        } else if ("DATE".equalsIgnoreCase(type)) {
            if (data != null) {
                cell.setCellValue((Date) data);
            }
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            cellStyle.setDataFormat(format.getFormat("yyyy-mm-dd HH:mm:ss"));
        } else if ("ENUM".equalsIgnoreCase(type)) {
            if (data != null) {
                cell.setCellValue(String.valueOf(data));
            }
            CellRangeAddressList regions =
                    new CellRangeAddressList(cell.getRowIndex(), cell.getRowIndex(),
                            cell.getColumnIndex(), cell.getColumnIndex());
            //加载下拉列表内容
            DVConstraint constraint =
                    DVConstraint.createExplicitListConstraint(formatValue.split(","));
            //数据有效性对象
            HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
            wb.getSheetAt(0).addValidationData(dataValidation);
        }
        cell.setCellStyle(cellStyle);
    }


    private void setValue(Object object, Field field, HSSFCell cell) throws IllegalAccessException {
        int type = cell.getCellType();
        switch (type) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    field.set(object, cell.getDateCellValue());
                } else {
                    field.set(object, (int) cell.getNumericCellValue());
                }
                break;
//            case HSSFCell.CELL_TYPE_STRING:
//                field.set(object, cell.getStringCellValue());
//                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                field.set(object, cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
            case HSSFCell.CELL_TYPE_FORMULA:
            case HSSFCell.CELL_TYPE_ERROR:
            default:
                field.set(object, cell.getStringCellValue());
                break;
        }
    }


    private String getXmlFieldName(Element root, String headName) {
        List list = root.getChild("head").getChild("tr").getChildren("th");
        for (Object object : list) {
            final Element element = (Element) object;
            if (element.getAttributeValue("name").equals(headName)) {
                return element.getAttributeValue("field");
            }
        }
        return null;
    }

    private void writeCell(HSSFWorkbook workbook, HSSFCell cell, String value, HSSFFont font) {
        //设置单元格值
        cell.setCellValue(value);
        //设置单元格样式
        HSSFCellStyle cellStyle = getCellStyle(workbook);
        cellStyle.setFont(font);//加入到样式
        cell.setCellStyle(cellStyle);//加入样式
    }

    private void writeHSSFWorkbook(HSSFWorkbook workbook, Element root, HSSFSheet sheet) {
        setWidth(root, sheet);
        setTitle(workbook, root, sheet);
        setHead(workbook, root, sheet);
    }

    private void writeHSSFWorkbook(HSSFWorkbook workbook, Element root, HSSFSheet sheet, List list)
            throws NoSuchFieldException, IllegalAccessException {
        this.writeHSSFWorkbook(workbook, root, sheet);
        setContentDate(workbook, root, sheet, list);
    }


}
