package com.xm.word.domain;

import com.xm.word.domain.wrap.FileTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * @author yousj
 * @since 2022-05-16
 */
@Data
public class CreateWordRequest {

    /**
     * 模板文件
     */
    private byte[] templateFile;

    /**
     * 是否更新目录
     */
    private boolean updateTOC;

    /**
     * 文件类型转换(默认docx)
     */
    private FileTypeEnum to;

    /**
     * 数据
     */
    private Map<String, Object> data;

    /**
     * pdf水印
     */
    private String watermark;

    /**
     * pdf水印透明度
     */
    private Float watermarkOpacity;

    /**
     * pdf水印字体大小
     */
    private Float watermarkFontSize;

}
