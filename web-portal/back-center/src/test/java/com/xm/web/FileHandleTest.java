package com.xm.web;

import com.xm.word.domain.FileConvertRequest;
import com.xm.word.domain.PdfExtractRequest;
import com.xm.word.domain.wrap.FileTypeEnum;
import com.xm.word.service.FileHandleService;
import com.xm.word.utils.WordUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yousj
 * @since 2022-11-24
 */
@SpringBootTest(classes = BackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(value = SpringRunner.class)
@ActiveProfiles(value = "test")
public class FileHandleTest {

    @Autowired
    FileHandleService fileHandleService;
    @Autowired
    WordUtil wordUtil;

    private String result = null;

    @Test
    public void fileConvertTest() {
        FileConvertRequest request = new FileConvertRequest();
        request.setFrom(FileTypeEnum.DOCX);
        request.setTo(FileTypeEnum.PDF);
        request.setTargetFile(wordUtil.getBytes("static/file/testFileConvert.docx"));
        request.setWatermark("测试一下");
        result = fileHandleService.fileConvert(request);
    }

    @Test
    public void pdfExtractTest() {
        result = fileHandleService.pdfExtract(assembleExtractRequest());
    }

    @Test
    public void pdf2HtmlTest() {
        result = fileHandleService.pdf2Html(assembleExtractRequest());
    }

    @After
    public void after() {
        System.out.println(result);
    }

    private PdfExtractRequest assembleExtractRequest() {
        PdfExtractRequest request = new PdfExtractRequest();
        request.setCenter(true);
        request.setTargetFile(wordUtil.getBytes("static/file/testExtract.pdf"));
        return request;
    }

}
