package com.xm.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.plugin.table.MultipleRowTableRenderPolicy;
import com.xm.word.domain.CreateWordRequest;
import com.xm.word.domain.wrap.*;
import com.xm.word.service.CreateWordService;
import com.xm.word.utils.WordUtil;
import com.xm.word.utils.WrapUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
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

	@Test
	public void testMultiRow() throws IOException {

		Date date = new Date();
		Map<String, Object> params = new HashMap<>();
		params.put("title", "某某某会议");
		params.put("date", date);
		params.put("address", "某某会议室");

		List<Report> reports = new ArrayList<>();


		reports.add(new Report("王五", date, "汇报内容1"));
		reports.add(new Report("张三", date, "汇报内容2"));
		reports.add(new Report("李四", date, "汇报内容3"));
		params.put("reports", reports);

		ConfigureBuilder builder = Configure.builder();
		builder.bind("reports", new MultipleRowTableRenderPolicy());
		XWPFTemplate xt = XWPFTemplate.compile("src/test/resources/templates/renderMultipleRow.docx", builder.build())
			.render(params);
		xt.writeToFile(String.format("multiple-row-%d.docx", System.currentTimeMillis()));
	}


	@After
    public void after() {
        System.out.println(result);
    }

    private Map<String, Object> assembleBasicFunctionsData() {
        Map<String, Object> data = new HashMap<>();
        // 图片
        data.put("bankruptcyStatus",WrapUtil.pictureDataWrap(80,23,wordUtil.getBytes("static/images/bankruptcy.png")));
        // 柱状图
        data.put("chart", WrapUtil.barChartDataWrap("统计图", new String[]{"2020", "2021", "2022"}, new Long[]{100L, 200L, 300L}));
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
        data.put("sections", WrapUtil.sectionsWrap(sections));
        return data;
    }

    private Map<String, Object> assembleNestedData() {
        Map<String, Object> data = new HashMap<>();
        // 嵌套一个包含基础功能的文档
        data.put("nested01", WrapUtil.nestedWrap(Collections.singletonList(assembleBasicFunctionsData()),
                wordUtil.getBytes("templates/basicFunctionsDemo.docx")));

        // 嵌套包含多个区块对的文档
        data.put("nested02", WrapUtil.nestedWrap(Arrays.asList(assembleSectionsData(), assembleSectionsData()),
                wordUtil.getBytes("templates/sectionsDemo.docx")));
        return data;
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

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Report {
		private String author;

		private Date time;

		private String content;

	}

}
