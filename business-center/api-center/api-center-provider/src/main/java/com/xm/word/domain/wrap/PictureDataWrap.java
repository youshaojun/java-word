package com.xm.word.domain.wrap;

import lombok.Data;

/**
 * 图片包装
 *
 * @author yousj
 * @since 2022-05-16
 */
@Data
public class PictureDataWrap extends DataType {

    /**
     * 宽
     */
    private int width;

    /**
     * 高
     */
    private int height;

    /**
     * 图片
     */
    private byte[] pictureData;

}
