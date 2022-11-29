package com.xm.word;

import com.xm.word.domain.FileConvertRequest;
import com.xm.word.domain.PdfExtractRequest;
import com.xm.word.entity.Res;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yousj
 * @since 2022-05-16
 */
@FeignClient(contextId = "remoteFileConvertService", value = "${word-provider.service.application.name}")
@RequestMapping("/api/asset/provider/file")
public interface IAssetFileHandleServiceClient {

    /**
     * 文件转换(将文件转为其他类型)
     * <p>
     * 例: doc -> docx docx -> pdf docx -> html ......
     *
     * @param request 请求参数
     * @return base64 str
     */
    @PostMapping("/convert")
    Res<String> fileConvert(@RequestBody FileConvertRequest request);

    /**
     * 提取pdf内容
     */
    @PostMapping("/pdfExtract")
    Res<String> pdfExtract(@RequestBody PdfExtractRequest request);

    /**
     * pdf转html
     */
    @PostMapping("/pdf2Html")
    Res<String> pdf2Html(@RequestBody PdfExtractRequest request);

}
