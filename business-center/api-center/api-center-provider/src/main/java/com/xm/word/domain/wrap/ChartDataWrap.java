package com.xm.word.domain.wrap;

import lombok.Data;

/**
 * 图表包装
 *
 * @author yousj
 * @since 2022-05-16
 */
@Data
public class ChartDataWrap extends DataType {

    /**
     * 标题
     */
    private String title;

    /**
     * 横坐标
     */
    private String[] chartX;

    /**
     * 纵坐标
     */
    private Long[] chartY;
}
