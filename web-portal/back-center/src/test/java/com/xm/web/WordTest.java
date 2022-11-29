package com.xm.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import com.xm.word.domain.CreateWordRequest;
import com.xm.word.domain.wrap.*;
import com.xm.word.service.CreateWordService;
import com.xm.word.utils.WordUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author yousj
 * @since 2022-11-24
 */
@SpringBootTest(classes = BackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(value = SpringRunner.class)
@ActiveProfiles(value = "test")
public class WordTest {

    @Autowired
    CreateWordService createWordService;
    @Autowired
    WordUtil wordUtil;

    private String result = null;

    /**
     * 基础功能测试
     */
    @Test
    public void basicFunctionsTest() {
        CreateWordRequest request = new CreateWordRequest();
        request.setTo(FileTypeEnum.PDF);
        request.setUpdateTOC(true);
        request.setTemplateFile(wordUtil.getBytes("templates/basicFunctionsDemo.docx"));
        request.setData(assembleBasicFunctionsData());
        request.setWatermark("测试一下");
        result = createWordService.create(request);
    }

    /**
     * 区块对(推荐使用嵌套实现循环功能)
     */
    @Test
    public void sectionsTest() {
        CreateWordRequest request = new CreateWordRequest();
        request.setTo(FileTypeEnum.DOCX);
        request.setUpdateTOC(false);
        request.setTemplateFile(wordUtil.getBytes("templates/sectionsDemo.docx"));
        request.setData(assembleSectionsData());
        result = createWordService.create(request);
    }

    /**
     * 嵌套
     */
    @Test
    public void nestedTest() {
        CreateWordRequest request = new CreateWordRequest();
        request.setTo(FileTypeEnum.DOCX);
        request.setUpdateTOC(false);
        request.setTemplateFile(wordUtil.getBytes("templates/nestedDemo.docx"));
        request.setData(assembleNestedData());
        result = createWordService.create(request);
    }

    @After
    public void after() {
        System.out.println(result);
    }

    private Map<String, Object> assembleBasicFunctionsData() {
        Map<String, Object> data = new HashMap<>();
        // 图片
        data.put("bankruptcyStatus", assemblePictureDataWrap());
        // 柱状图
        data.put("chart", chartDateWrap());
        // 列表
        // 默认使用LoopRowTableRenderPolicy插件
        setList(data, DataTypeEnum.DEFAULT_TABLE, list(), "01");
        // 包含超链接使用LoopRowTableHyperlinkRenderPolicy插件
        setList(data, DataTypeEnum.TABLE_HYPERLINK, list(), "02");
        // el表达式, 参考 https://docs.spring.io/spring-framework/docs/5.3.18/reference/html/core.html#expressions
        data.put("conditionIfNull", null);
        data.put("conditionIfNotNull", "");
        return data;
    }

    private Map<String, Object> assembleSectionsData() {
        Map<String, Object> data = new HashMap<>();
        // 普通数据
        setList(data, DataTypeEnum.DEFAULT_TABLE, list(), "01");
        // 区块对数据
        List<Map<String, Object>> sections = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> sectionData = new HashMap<>();
            setList(sectionData, DataTypeEnum.DEFAULT_TABLE, list(), "01");
            setList(sectionData, DataTypeEnum.TABLE_HYPERLINK, list(), "02");
            sections.add(sectionData);
        }
        SectionsWrap sectionsWrap = new SectionsWrap();
        sectionsWrap.setSections(sections);
        sectionsWrap.setType(DataTypeEnum.SECTIONS.getType());
        data.put("sections", sectionsWrap);
        return data;
    }

    private Map<String, Object> assembleNestedData() {
        Map<String, Object> data = new HashMap<>();
        // 嵌套一个包含基础功能的文档
        NestedWrap nestedWrap01 = new NestedWrap();
        nestedWrap01.setData(Collections.singletonList(assembleBasicFunctionsData()));
        nestedWrap01.setNestedTemplate(wordUtil.getBytes("templates/basicFunctionsDemo.docx"));
        nestedWrap01.setType(DataTypeEnum.NESTED_DOC.getType());
        data.put("nested01", nestedWrap01);

        // 嵌套包含多个区块对的文档
        NestedWrap nestedWrap02 = new NestedWrap();
        nestedWrap02.setData(Arrays.asList(assembleSectionsData(), assembleSectionsData()));
        nestedWrap02.setNestedTemplate(wordUtil.getBytes("templates/sectionsDemo.docx"));
        nestedWrap02.setType(DataTypeEnum.NESTED_DOC.getType());
        data.put("nested02", nestedWrap02);
        return data;
    }

    private PictureDataWrap assemblePictureDataWrap() {
        PictureDataWrap pictureDataWrap = new PictureDataWrap();
        pictureDataWrap.setWidth(80);
        pictureDataWrap.setHeight(23);
        pictureDataWrap.setPictureData(wordUtil.getBytes("static/images/bankruptcy.png"));
        pictureDataWrap.setType(DataTypeEnum.PICTURE.getType());
        return pictureDataWrap;
    }

    private ChartDataWrap chartDateWrap() {
        ChartDataWrap chartDataWrap = new ChartDataWrap();
        chartDataWrap.setTitle("统计图");
        chartDataWrap.setChartX(new String[]{"2020", "2021", "2022"});
        chartDataWrap.setChartY(new Long[]{100L, 200L, 300L});
        chartDataWrap.setType(DataTypeEnum.BAR_CHART.getType());
        return chartDataWrap;
    }

    private void setList(Map<String, Object> data, DataTypeEnum dataTypeEnum, List list, String suffix) {
        TableDataWrap tableDataWrap = tableDataWrap(dataTypeEnum, list);
        String key = "list" + suffix;
        data.put(key, tableDataWrap);
        data.put(key + "Size", list.size());
    }

    private TableDataWrap tableDataWrap(DataTypeEnum dataTypeEnum, List list) {
        TableDataWrap tableDataWrap = new TableDataWrap();
        Optional.ofNullable(list).ifPresent(e -> tableDataWrap.setData(JSON.toJSONString(e)));
        if (dataTypeEnum == DataTypeEnum.TABLE_HYPERLINK) {
            tableDataWrap.setColumnIndex(1);
            tableDataWrap.setUrlColumnName("url");
            tableDataWrap.setColumnName("name");
        }
        tableDataWrap.setType(dataTypeEnum.getType());
        return tableDataWrap;
    }

    private List<ModelData> list() {
        return Arrays.asList(
                new ModelData("张三", "https://www.baidu.com", "浙江杭州", new Date()),
                new ModelData("李四", "https://www.baidu.com", "浙江温州", new Date()),
                new ModelData("王五", "https://www.baidu.com", "浙江台州", new Date())
        );
    }

    @Data
    @AllArgsConstructor
    private static class ModelData {

        private String name;
        private String url;
        private String address;
        @JSONField(format = "yyyy-MM-dd")
        private Date date;

    }

}
