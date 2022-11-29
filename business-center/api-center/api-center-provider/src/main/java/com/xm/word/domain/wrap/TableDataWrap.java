package com.xm.word.domain.wrap;

import lombok.Data;

/**
 * 默认列表包装
 *
 * @author yousj
 * @since 2022-05-16
 */
@Data
public class TableDataWrap extends DataType {

    /**
     * 第几列
     */
    private int columnIndex = 1;

    /**
     * 列字段名
     */
    private String columnName = "title";

    /**
     * 超链接字段名
     */
    private String urlColumnName = "url";

    /**
     * 超链接字段对齐方式, 默认左对齐
     * @see org.apache.poi.xwpf.usermodel.ParagraphAlignment
     */
    private Integer columnAlignment = 11;

    /**
     * 超链接字段颜色, 默认0000FF
     */
    private String urlColumnColor = "0000FF";

    /**
     * 超链接字段是否带下划线, 默认否
     */
    private boolean underline;

    /**
     * 超链接字段字体, 默认宋体
     */
    private String fontFamily = "宋体";

    /**
     * 超链接字段字体大小, 默认10号
     */
    private Integer fontSize = 10;
    /**
     * 表格数据(json字符串)
     */
    private String data;

}
