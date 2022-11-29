package com.xm.word.domain;

import com.xm.word.domain.wrap.FileTypeEnum;

import lombok.Data;

/**
 * @author yousj
 * @since 2022-06-01
 */
@Data
public class FileConvertRequest {

    /**
     * 目标文件
     */
    private byte[] targetFile;

    /**
     * 是否更新目录
     */
    private boolean updateTOC;

    /**
     * 原文件类型
     */
    private FileTypeEnum from;

    /**
     * 转换文件类型
     */
    private FileTypeEnum to;

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
