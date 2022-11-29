package com.xm.word.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.xm.word.domain.wrap.FileTypeEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * @author yousj
 * @since 2022-06-06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PdfUtil {

    public static final String SPIRE_WARNING1 = "Evaluation Warning : The document was created with Spire.PDF for java.";
    public static final String SPIRE_WARNING2 = "Evaluation Warning : The document was created with Spire.PDF for Java.";

    /**
     * 添加水印
     */
    @SneakyThrows
    public static File addWatermark(File source, String target, String watermark, Float watermarkOpacity, Float watermarkFontSize) {
        FileOutputStream out = null;
        PdfReader reader = null;
        PdfStamper stamper = null;
        File file = FileUtil.file(target);
        try {
            out = new FileOutputStream(file);
            // 原PDF文件
            reader = new PdfReader(FileUtil.readBytes(source));
            // 输出的PDF文件内容
            stamper = new PdfStamper(reader, out);
            // 字体 来源于 itext-asian JAR包
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", true);
            PdfGState gs = new PdfGState();
            // 设置透明度
            gs.setFillOpacity(ObjectUtil.defaultIfNull(watermarkOpacity, 0.055f));
            gs.setStrokeOpacity(ObjectUtil.defaultIfNull(watermarkOpacity, 0.055f));
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                // 内容上层
                PdfContentByte content = stamper.getOverContent(i);
                content.beginText();
                // 字体添加透明度
                content.setGState(gs);
                // 添加字体大小等
                content.setFontAndSize(baseFont, ObjectUtil.defaultIfNull(watermarkFontSize, 65f));
                // 添加范围
                content.setTextMatrix(50, 150);
                // 具体位置 内容 旋转多少度 共360度
                content.showTextAligned(Element.ALIGN_CENTER, watermark, 300, 485, 45);
                content.endText();
            }
        } finally {
            if (stamper != null) {
                stamper.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (out != null) {
                out.close();
            }
        }
        FileUtil.del(source);
        return file;
    }

    public static String extract(byte[] target) {
        try {
            PdfDocument pdfDocument = new PdfDocument();
            pdfDocument.loadFromBytes(target);
            StringBuilder sb = new StringBuilder();
            PdfPageBase page;
            for (int i = 0; i < pdfDocument.getPages().getCount(); i++) {
                page = pdfDocument.getPages().get(i);
                sb.append(page.extractText(true));
            }
            return StrUtil.trimEnd(sb.toString().replace(SPIRE_WARNING1, "").replace(SPIRE_WARNING2, ""));
        } catch (Exception ex) {
            return null;
        }
    }

    public static String pdf2Html(byte[] target, boolean center) {
        PdfDocument pdf = new PdfDocument();
        try {
            pdf.loadFromBytes(target);
            String newPath = WordUtil.newPath(FileTypeEnum.HTML);
            pdf.saveToFile(newPath, FileFormat.HTML);
            String htmlStr = FileUtil.readString(newPath, Charset.defaultCharset());
            FileUtil.del(newPath);
            htmlStr = htmlStr.replace(SPIRE_WARNING1, "").replace(SPIRE_WARNING2, "");
            return center ? htmlStr.replace("<body style='margin:0'>", "<body style='text-align: center'>") : htmlStr;
        } catch (Exception ignore) {
            // ignore
        } finally {
            pdf.close();
        }
        return null;
    }

}
