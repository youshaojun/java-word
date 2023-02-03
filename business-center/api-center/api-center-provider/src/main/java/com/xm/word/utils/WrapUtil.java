package com.xm.word.utils;

import com.xm.word.domain.wrap.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author yousj
 * @since 2023-02-03
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WrapUtil {

    /**
     * 设置默认空数据列表
     */
    public static void tableDataWrap(Map<String, Object> data, String... keys) {
        TableDataWrap tableDataWrap = new TableDataWrap();
        tableDataWrap.setType(DataTypeEnum.DEFAULT_TABLE.getType());
        for (String key : keys) {
            data.put(key, tableDataWrap);
        }
    }

    /**
     * 嵌套类型
     *
     * @param data           List<<Map<String, Object>> 多个会在文档从上往下依次展开
     * @param nestedTemplate 嵌套文档模板
     * @return NestedWrap
     */
    public static NestedWrap nestedWrap(List data, byte[] nestedTemplate) {
        NestedWrap nestedWrap = new NestedWrap();
        nestedWrap.setType(DataTypeEnum.NESTED_DOC.getType());
        nestedWrap.setNestedTemplate(nestedTemplate);
        nestedWrap.setData(data);
        return nestedWrap;
    }

    /**
     * 区块对
     */
    public static SectionsWrap sectionsWrap(List<Map<String, Object>> data) {
        SectionsWrap sectionsWrap = new SectionsWrap();
        sectionsWrap.setType(DataTypeEnum.SECTIONS.getType());
        sectionsWrap.setSections(data);
        return sectionsWrap;
    }

    /**
     * 柱状图
     */
    public static ChartDataWrap barChartDataWrap(String title, String[] chartX, Long[] chartY) {
        ChartDataWrap chartDataWrap = new ChartDataWrap();
        chartDataWrap.setType(DataTypeEnum.BAR_CHART.getType());
        chartDataWrap.setTitle(title);
        chartDataWrap.setChartX(chartX);
        chartDataWrap.setChartY(chartY);
        return chartDataWrap;
    }

    /**
     * 图片
     */
    public static PictureDataWrap pictureDataWrap(int width, int height, byte[] pictureData) {
        PictureDataWrap pictureDataWrap = new PictureDataWrap();
        pictureDataWrap.setType(DataTypeEnum.PICTURE.getType());
        pictureDataWrap.setWidth(width);
        pictureDataWrap.setHeight(height);
        pictureDataWrap.setPictureData(pictureData);
        return pictureDataWrap;
    }

}
