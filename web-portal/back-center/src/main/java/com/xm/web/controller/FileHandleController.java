package com.xm.web.controller;

import com.xm.word.entity.Res;
import com.xm.word.IAssetFileHandleServiceClient;
import com.xm.word.domain.FileConvertRequest;
import com.xm.word.domain.PdfExtractRequest;
import com.xm.word.service.FileHandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yousj
 * @since 2022-06-01
 */
@RestController
@RequiredArgsConstructor
public class FileHandleController implements IAssetFileHandleServiceClient {

    private final FileHandleService fileHandleService;

    @Override
    public Res<String> fileConvert(@RequestBody FileConvertRequest request) {
        return Res.success(fileHandleService.fileConvert(request));
    }

    @Override
    public Res<String> pdfExtract(@RequestBody PdfExtractRequest request) {
        return Res.success(fileHandleService.pdfExtract(request));
    }

    @Override
    public Res<String> pdf2Html(@RequestBody PdfExtractRequest request) {
        return Res.success(fileHandleService.pdf2Html(request));
    }
}
