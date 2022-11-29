package com.xm.word.service.impl;

import com.xm.word.service.CreateWordService;
import com.xm.word.utils.WordUtil;
import com.xm.word.domain.CreateWordRequest;
import org.springframework.stereotype.Service;

/**
 * @author yousj
 * @since 2022-05-17
 */
@Service
public class CreateWordServiceImpl implements CreateWordService {

    @Override
    public String create(CreateWordRequest request) {
        return WordUtil.createWord2Base64(request);
    }
}
