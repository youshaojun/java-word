package com.xm.word.domain.wrap;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型转换
 *
 * @author yousj
 * @since 2022-06-01
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {

    DOC(".doc"),
    DOCX(".docx"),
    PDF(".pdf"),
    HTML(".html"),
    ;
    private String suffix;

}
