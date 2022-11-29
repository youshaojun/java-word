package com.xm.web.controller;

import com.xm.word.IAssetWordServiceClient;
import com.xm.word.domain.CreateWordRequest;
import com.xm.word.entity.Res;
import com.xm.word.service.CreateWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yousj
 * @since 2022-05-17
 */
@RestController
@RequiredArgsConstructor
public class WordController implements IAssetWordServiceClient {

    private final CreateWordService createWordService;

    @Override
    public Res<String> create(@RequestBody CreateWordRequest request) {
        return Res.success(createWordService.create(request));
    }

}
