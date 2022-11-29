package com.xm.word.service.impl;

import com.xm.word.service.FileHandleService;
import com.xm.word.utils.PdfUtil;
import com.xm.word.utils.WordUtil;
import com.xm.word.domain.FileConvertRequest;
import com.xm.word.domain.PdfExtractRequest;
import org.springframework.stereotype.Service;

/**
 * @author yousj
 * @since 2022-06-01
 */
@Service
public class FileHandleServiceImpl implements FileHandleService {

    @Override
    public String fileConvert(FileConvertRequest request) {
        return WordUtil.saveFile2Base64(request);
    }

    @Override
    public String pdfExtract(PdfExtractRequest request) {
        return PdfUtil.extract(request.getTargetFile());
    }

    @Override
    public String pdf2Html(PdfExtractRequest request) {
        return PdfUtil.pdf2Html(request.getTargetFile(), request.isCenter());
    }
}
