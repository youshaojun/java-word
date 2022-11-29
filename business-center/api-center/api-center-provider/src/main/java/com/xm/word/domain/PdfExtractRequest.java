package com.xm.word.domain;

import lombok.Data;

/**
 * @author yousj
 * @since 2022-06-21
 */
@Data
public class PdfExtractRequest {

    /**
     * 目标文件
     */
    private byte[] targetFile;

    private boolean center;

}
