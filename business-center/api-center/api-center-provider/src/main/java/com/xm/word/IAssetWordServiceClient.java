package com.xm.word;

import com.xm.word.domain.CreateWordRequest;
import com.xm.word.entity.Res;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yousj
 * @since 2022-05-16
 */
@FeignClient(contextId = "remoteWordService", value = "${word-provider.service.application.name}")
@RequestMapping("/api/asset/provider/word")
public interface IAssetWordServiceClient {

    /**
     * 生成word
     * 为避免其他应用使用造成冲突, 将相关操作进行包装(其中列表数据需要转为json字符串):
     * 1. 列表 (TableDataWrap type=1)
     * 2. 含超链接列表 (TableDataWrap type=2)
     * 3. 图片 (PictureDataWrap type=3)
     * 4. 柱状图 (ChartDataWrap type=4)
     * 5. 饼图 (ChartDataWrap type=5)
     * 6. 嵌套文档 (NestedWrap type=6)
     * 7. 区块对 (SectionsWrap type=7)
     *
     * @param request 请求参数
     * @return base64 str
     * @see <a href="http://deepoove.com/poi-tl">官方文档</a>
     */
    @PostMapping("/create")
    Res<String> create(@RequestBody CreateWordRequest request);

}
