package com.xm.word.service;

import com.xm.word.domain.FileConvertRequest;
import com.xm.word.domain.PdfExtractRequest;

/**
 * @author yousj
 * @since 2022-06-01
 */
public interface FileHandleService {

    String fileConvert(FileConvertRequest request);

    String pdfExtract(PdfExtractRequest request);

    String pdf2Html(PdfExtractRequest request);

}
