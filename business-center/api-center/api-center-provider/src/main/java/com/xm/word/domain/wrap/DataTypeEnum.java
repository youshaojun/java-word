package com.xm.word.domain.wrap;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据类型枚举
 *
 * @author yousj
 * @since 2022-05-17
 */
@Getter
@AllArgsConstructor
public enum DataTypeEnum {

    DEFAULT_TABLE(1, "默认列表"),
    TABLE_HYPERLINK(2, "含超链接列表"),
    PICTURE(3, "图片"),
    BAR_CHART(4, "柱状图"),
    PIE_CHART(5, "饼图"),
    NESTED_DOC(6, "嵌套文档"),
    SECTIONS(7, "区块对"),
    ;

    private int type;
    private String describe;

}
