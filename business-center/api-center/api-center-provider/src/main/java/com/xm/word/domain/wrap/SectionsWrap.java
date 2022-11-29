package com.xm.word.domain.wrap;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author yousj
 * @since 2022-11-28
 */
@Data
public class SectionsWrap extends DataType {

    List<Map<String, Object>> sections;

}
