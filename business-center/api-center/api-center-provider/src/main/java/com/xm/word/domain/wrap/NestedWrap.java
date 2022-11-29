package com.xm.word.domain.wrap;

import lombok.Data;

import java.util.List;

/**
 * 嵌套文档包装
 *
 * @author yousj
 * @since 2022-06-20
 */
@Data
public class NestedWrap extends DataType {

    /**
     * 嵌套文档模板
     */
    private byte[] nestedTemplate;

    /**
     * 数据, 多个会依次往下排列展示
     */
    private List data;

}
