package com.xm.word.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.Charts;
import com.deepoove.poi.data.Includes;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.policy.RenderPolicy;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.LineSpacingRule;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.StructureDocumentTag;
import com.spire.license.LicenseProvider;
import com.xm.word.domain.CreateWordRequest;
import com.xm.word.domain.FileConvertRequest;
import com.xm.word.domain.wrap.*;
import com.xm.word.policy.LoopRowTableHyperlinkRenderPolicy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jodconverter.DocumentConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * @author yousj
 * @since 2022-05-17
 */
@Slf4j
@Component
public class WordUtil {

    @Value("${spire.doc.license.key}")
    private String spireDocLicenseKey;

    @PostConstruct
    public void loadSpireDocLicense() {
        // 付费版本加载证书
        // LicenseProvider.setLicenseKey(spireDocLicenseKey);
    }

    public static String createWord2Base64(CreateWordRequest request) {
        request.setTo(ObjectUtil.defaultIfNull(request.getTo(), FileTypeEnum.DOCX));
        return Base64.encode(createWord(request));
    }

    public static String saveFile2Base64(FileConvertRequest request) {
        File file = saveToFile(request);
        String base64Str = Base64.encode(file);
        //FileUtil.del(file);
        return base64Str;
    }

    public static File saveToFile(FileConvertRequest request) {
        String newPath = newPath(request.getFrom());
        FileUtil.writeBytes(request.getTargetFile(), newPath);
        return saveToFile(newPath, request.isUpdateTOC(), request.getTo(), request.getWatermark(), request.getWatermarkOpacity(), request.getWatermarkFontSize());
    }

    public static File createWord(CreateWordRequest request) {
        Map<String, RenderPolicy> policyBind = new HashMap<>(64);
        Map<String, Object> data = request.getData();
        analysis(data, policyBind);
        return createWord(data, policyBind, FileUtil.writeBytes(request.getTemplateFile(), newPath(FileTypeEnum.DOCX)),
                request.isUpdateTOC(), request.getTo(), request.getWatermark(), request.getWatermarkOpacity(), request.getWatermarkFontSize());
    }

    public static void analysis(Map<String, Object> data, Map<String, RenderPolicy> policyBind) {
        data.entrySet().forEach(e -> {
            try {
                String jsonStr = JSON.toJSONString(e.getValue());
                JSONObject j = JSON.parseObject(jsonStr);
                Integer type = j.getInteger("type");
                analysis(type, policyBind, data, e, jsonStr);
            } catch (Exception ex) {
                // 普通文本不用解析
            }
        });
    }

    /**
     * 解析数据
     */
    private static void analysis(Integer type, Map<String, RenderPolicy> policyBind, Map<String, Object> data, Map.Entry<String, Object> dataEntry, String jsonStr) {
        if (Objects.equals(DataTypeEnum.DEFAULT_TABLE.getType(), type)) {
            // 默认列表
            TableDataWrap tableDataWrap = JSON.parseObject(jsonStr, TableDataWrap.class);
            Optional.ofNullable(tableDataWrap.getData()).ifPresent(e -> data.put(dataEntry.getKey(), JSONArray.parseArray(tableDataWrap.getData())));
            policyBind.put(dataEntry.getKey(), new LoopRowTableRenderPolicy());
        } else if (Objects.equals(DataTypeEnum.TABLE_HYPERLINK.getType(), type)) {
            // 含超链接列表
            TableDataWrap tableHyperlinkDataWrap = JSON.parseObject(jsonStr, TableDataWrap.class);
            Optional.ofNullable(tableHyperlinkDataWrap.getData()).ifPresent(e -> data.put(dataEntry.getKey(), JSONArray.parseArray(tableHyperlinkDataWrap.getData())));
            LoopRowTableHyperlinkRenderPolicy loopRowTableHyperlinkRenderPolicy = BeanUtil.copyProperties(tableHyperlinkDataWrap, LoopRowTableHyperlinkRenderPolicy.class);
            policyBind.put(dataEntry.getKey(), loopRowTableHyperlinkRenderPolicy);
        } else if (Objects.equals(DataTypeEnum.PICTURE.getType(), type)) {
            // 图片
            PictureDataWrap pictureDataWrap = JSON.parseObject(jsonStr, PictureDataWrap.class);
            data.put(dataEntry.getKey(), Pictures.ofBytes(pictureDataWrap.getPictureData())
                    .size(pictureDataWrap.getWidth(), pictureDataWrap.getHeight()).create());
        } else if (Objects.equals(DataTypeEnum.BAR_CHART.getType(), type)) {
            // 柱状图
            ChartDataWrap barChartDataWrap = JSON.parseObject(jsonStr, ChartDataWrap.class);
            data.put(dataEntry.getKey(), Charts.ofMultiSeries(barChartDataWrap.getTitle(), barChartDataWrap.getChartX())
                    .addSeries("category", barChartDataWrap.getChartY()).create());
        } else if (Objects.equals(DataTypeEnum.PIE_CHART.getType(), type)) {
            // 饼图
            ChartDataWrap pieChartDataWrap = JSON.parseObject(jsonStr, ChartDataWrap.class);
            data.put(dataEntry.getKey(), Charts.ofSingleSeries(pieChartDataWrap.getTitle(), pieChartDataWrap.getChartX())
                    .series("category", pieChartDataWrap.getChartY()).create());
        } else if (Objects.equals(DataTypeEnum.NESTED_DOC.getType(), type)) {
            // 嵌套文档
            NestedWrap nestedWrap = JSON.parseObject(jsonStr, NestedWrap.class);
            analysisNestedData(nestedWrap, policyBind, data, dataEntry);
        } else if (Objects.equals(DataTypeEnum.SECTIONS.getType(), type)) {
            // 区块对
            SectionsWrap sectionsWrap = JSON.parseObject(jsonStr, SectionsWrap.class);
            // 内层Map也按同样规则解析
            sectionsWrap.getSections().forEach(s -> analysis(s, policyBind));
            data.put(dataEntry.getKey(), sectionsWrap.getSections());
        }
    }

    /**
     * 解析嵌套文档
     * 主要用于复杂结构文档, 如多级列表, 循环嵌套等
     * NumberingRenderData可以创建多级列表
     * 区块对也可以很好的循环列表，并且保持有序列表编号有序
     * 但是实际使用中的结构可能更加复杂, 目前区块对中不支持嵌套语法
     * 推荐使用嵌套文档的方式来实现更复杂的结构以避免在区块对中循环嵌套
     */
    @SuppressWarnings("unchecked")
    private static void analysisNestedData(NestedWrap nestedWrap, Map<String, RenderPolicy> policyBind, Map<String, Object> data, Map.Entry<String, Object> dataEntry) {
        List nestedWrapDataList = nestedWrap.getData();
        if (CollUtil.isEmpty(nestedWrapDataList)) {
            return;
        }
        for (Object nestedWrapData : nestedWrapDataList) {
            try {
                // 内层Map也按同样规则解析
                analysis((Map<String, Object>) nestedWrapData, policyBind);
            } catch (Exception ignored) {
                // ignored
            }
        }
        data.put(dataEntry.getKey(), Includes.ofBytes(nestedWrap.getNestedTemplate()).setRenderModel(nestedWrapDataList).create());
    }

    // ----------------------------- [poi-tl(api简单易用, 暂不支持目录操作, 高级功能扩展较复杂)] -------------------------

    /**
     * 生成word
     *
     * @param data         填充的数据
     * @param policyBind   绑定插件
     * @param templateFile 模板文件
     * @param updateTOC    是否更新目录
     * @param to           保存的文件类型
     * @param watermark    水印
     * @return 生成的word文档
     * @see <a href="http://deepoove.com/poi-tl/">官方文档</a>
     */
    @SneakyThrows
    public static File createWord(Map<String, Object> data, Map<String, RenderPolicy> policyBind, File templateFile,
                                  boolean updateTOC, FileTypeEnum to, String watermark, Float watermarkOpacity, Float watermarkFontSize) {
        ConfigureBuilder configureBuilder = Configure.builder()
                // isStrict = false , 关闭严格模式, 严格模式下key不存在会报错
                // 关闭后key不存在会被忽略
                // 主要用于支持一些条件判断, 如 {{?produces == null}}无{{/}}
                .useSpringEL(false);
        // 绑定policy
        policyBind.forEach(configureBuilder::bind);
        // TOCRenderPolicy Beta实验功能：目录，打开文档时会弹出窗口提示更新域(wps不好使)
        // https://github.com/Sayi/poi-tl/issues/131
        // data.put("TOC", "TOC");
        // configureBuilder.bind("TOC", new TOCRenderPolicy());
        XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile, configureBuilder.build()).render(data);
        String path = newPath(FileTypeEnum.DOCX);
        File file = new File(path);
        xwpfTemplate.writeToFile(path);
        FileUtil.del(templateFile);
        return (!updateTOC && Objects.isNull(to)) ? file : saveToFile(path, updateTOC, to, watermark, watermarkOpacity, watermarkFontSize);
    }

    // ---------------------- [spire.doc, spire.doc.free]免费版限制内容不超过500个段落, 25个表格, 超过部分会被截断, 免费版的可能会出现水印 ----------------

    /**
     * 另存为
     *
     * @param source    原文件
     * @param updateTOC 是否更新目录
     * @param fileType  新文件类型
     * @return 新文件
     * @see <a href="https://www.e-iceblue.cn/Introduce/Spire-Doc-JAVA.html">官方文档</a>
     */
    public static File saveToFile(String source, boolean updateTOC, FileTypeEnum fileType, String watermark, Float watermarkOpacity, Float watermarkFontSize) {
        String newPath = newPath(fileType);
        File newFile = new File(newPath);
        try {
            converterFile(source, updateTOC, newPath, newFile);
            if (StrUtil.isNotBlank(watermark) && fileType == FileTypeEnum.PDF) {
                return PdfUtil.addWatermark(newFile, newPath(fileType), watermark, watermarkOpacity, watermarkFontSize);
            }
        } finally {
            FileUtil.del(source);
        }
        return newFile;
    }

    @SneakyThrows
    private static void converterFile(String source, boolean updateTOC, String newPath, File newFile) {
        Environment environment = SpringUtil.getBean(Environment.class);
        Boolean useSpire = environment.getProperty("spire.converter.file", Boolean.class, Boolean.FALSE);
        if (useSpire) {
            Document document = new Document(source);
            try {
                document.saveToFile(newPath, FileFormat.Auto);
            } finally {
                document.close();
            }
        } else {
            DocumentConverter documentConverter = SpringUtil.getBean(DocumentConverter.class);
            documentConverter.convert(new File(source)).to(newFile).execute();
        }
        if (updateTOC) {
            updateTOC(newPath);
        }
    }

    /**
     * 使用spire.doc更新目录, 免费版更新存在一些限制并且更新的页码不准确
     */
    private static void updateTOC(String source) {
        try {
            Document document = new Document(source);
            try {
                document.updateTableOfContents();
                setTOCParagraphLineSpacing(document, 20f);
                document.saveToFile(source, FileFormat.Auto);
            } finally {
                document.close();
            }
        } catch (Exception ex) {
            log.error("update toc is failed, msg [{}]", ex.getMessage());
        }
    }

    private static void setTOCParagraphLineSpacing(Document document, float size) {
        for (Object obj1 : document.getSections()) {
            for (Object obj2 : ((Section) obj1).getBody().getChildObjects()) {
                if (!(obj2 instanceof StructureDocumentTag)) {
                    continue;
                }
                for (Object obj3 : ((StructureDocumentTag) obj2).getChildObjects()) {
                    if (!(obj3 instanceof Paragraph)) {
                        continue;
                    }
                    Paragraph paragraph = (Paragraph) obj3;
                    if (StrUtil.contains(paragraph.getStyle().getName(), "TOC")) {
                        // 找到目录项里的所有段落 设置他们的间距为同一个值
                        paragraph.getFormat().setLineSpacingRule(LineSpacingRule.Exactly);
                        paragraph.getFormat().setLineSpacing(size);
                    }
                }
            }
        }
    }

    public static String newPath(FileTypeEnum fileType) {
        return newPath(fileType.getSuffix());
    }

    public static String newPath(String suffix) {
        return "/tmp/" + IdUtil.fastSimpleUUID() + suffix;
    }

    public byte[] getBytes(String path) {
        File file = getFile(path);
        byte[] bytes = FileUtil.readBytes(file);
        FileUtil.del(file);
        return bytes;
    }

    /**
     * 获取resources/templates文件夹下资源文件
     */
    @SneakyThrows
    public File getFile(String path) {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(path);
        if (Objects.isNull(input)) {
            return null;
        }
        File file = new File(newPath("." + FileUtil.getSuffix(path)));
        FileUtils.copyInputStreamToFile(input, file);
        return file;
    }

}
