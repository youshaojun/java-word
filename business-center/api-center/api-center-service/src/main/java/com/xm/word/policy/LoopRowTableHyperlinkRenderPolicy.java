package com.xm.word.policy;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.xm.word.utils.XssUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.util.List;

/**
 * 扩展LoopRowTableRenderPolicy插件
 * 支持表格内设置超链接
 *
 * @author yousj
 * @since 2022-05-17
 */
@Slf4j
@Data
@NoArgsConstructor
public class LoopRowTableHyperlinkRenderPolicy extends LoopRowTableRenderPolicy {

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
     * 超链接字段对齐方式, 默认左对齐, 1 左对齐, 2 居中, 3 右对齐
     */
    private Integer columnAlignment = 1;

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

    public LoopRowTableHyperlinkRenderPolicy(int columnIndex, String columnName, String urlColumnName){
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.urlColumnName = urlColumnName;
    }

    @Override
    protected void afterloop(XWPFTable table, Object data) {
        try {
            List<JSONObject> jsonObjectList = JSONArray.parseArray(JSON.toJSONString(data), JSONObject.class);
            List<XWPFTableRow> rows = table.getRows();
            // index从1开始, 跳过表头
            for (int i = 1; i < rows.size(); i++) {
                XWPFTableCell cell = rows.get(i).getCell(columnIndex);
                List<XWPFParagraph> paragraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    JSONObject js = jsonObjectList.get(i - 1);
                    String url = String.valueOf(js.get(urlColumnName));
                    String value = String.valueOf(js.get(columnName));
                    if (StrUtil.isBlank(url) || StrUtil.equalsAny(url, "null", "--", "-")) {
                        // 不满足条件, 正常填充值
                        cell.setText(value);
                    } else {
                        // 设置超链接
                        setLink(url, value, paragraph);
                    }
                    // 垂直居中
                    paragraph.setVerticalAlignment(TextAlignment.CENTER);
                    // 水平对齐方式
                    paragraph.setAlignment(ParagraphAlignment.valueOf(columnAlignment));
                    paragraph.getRuns().forEach(e -> {
                        setFont(e.getCTR().addNewRPr().addNewRFonts());
                        e.setFontSize(fontSize);
                    });
                }
            }
        } catch (Exception ex) {
            log.error("超链接设置失败, data: {}, msg: {}", data, ex.getMessage(), ex);
        }
    }

    private void setLink(String url, String text, XWPFParagraph e) {
        String id = e.getDocument().getPackagePart().addExternalRelationship(XssUtil.parserUrl(url),
                XWPFRelation.HYPERLINK.getRelation()).getId();
        CTHyperlink ctHyperlink = e.getCTP().addNewHyperlink();
        ctHyperlink.setId(id);

        CTText ctText = CTText.Factory.newInstance();
        // 设置值
        ctText.setStringValue(text);
        CTR ctr = CTR.Factory.newInstance();
        CTRPr rpr = ctr.addNewRPr();
        setFont(rpr.addNewRFonts());
        rpr.addNewSz().setVal(fontSize << 1);
        CTColor color = CTColor.Factory.newInstance();
        // 设置颜色
        color.setVal(urlColumnColor);
        rpr.setColorArray(new CTColor[]{color});
        // 是否设置下划线
        rpr.addNewU().setVal(underline ? STUnderline.SINGLE : STUnderline.NONE);
        ctr.setTArray(new CTText[]{ctText});
        ctHyperlink.setRArray(new CTR[]{ctr});
    }

    private void setFont(CTFonts font) {
        font.setEastAsia(fontFamily);
        font.setAscii(fontFamily);
        font.setHAnsi(fontFamily);
    }

}
