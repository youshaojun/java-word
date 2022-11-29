## 项目说明

> 基于[POI-TL](http://deepoove.com/poi-tl/)生成word

**由于`POI-TL`依赖`apache.poi`,
为避免应用使用造成冲突(应用中使用到依赖`apache.poi`的其他框架, 如`EasyExcel`等)
本项目将相关的word操作进行包装, 提供简单的Feign接口调用方式完成word模板的处理**

**具体操作请参考`com.xm.web.FileHandleTest` & `com.xm.web.WordTest`**

编号 | 功能 | 代码
--- | ---|---|
1| 生成word| WordController.create
2| 文件转换/更新目录/加水印|FileHandleController.fileConvert
3| 提取pdf文本内容|FileHandleController.pdfExtract
4| pdf转html|FileHandleController.pdf2Html

#### 支持word模板的包装操作:

- [x] 普通列表
- [x] 含超链接列表
- [x] 图片
- [x] 柱状图
- [x] 饼图
- [x] 嵌套文档
- [x] 区块对

> 基于[spire.doc](https://www.e-iceblue.cn/Introduce/Spire-Doc-JAVA.html)操作文件

> 基于`spire.doc`更新word文档目录

`免费版限制内容不超过500个段落, 不出超过25个表格, 超过部分会被截断`

### 项目结构
<details>
<summary>展开查看</summary>
<pre><code>.
├─business-center
│  └─api-center
│      ├─api-center-provider
│      │  └─src
│      │      └─main
│      │          ├─java
│      │          │  └─com
│      │          │      └─xm
│      │          │          └─word
│      │          │              ├─domain
│      │          │              │  └─wrap
│      │          │              └─entity
│      │          └─resources
│      └─api-center-service
│          └─src
│              └─main
│                  ├─java
│                  │  └─com
│                  │      └─xm
│                  │          └─word
│                  │              ├─policy
│                  │              ├─service
│                  │              │  └─impl
│                  │              └─utils
│                  └─resource
└─web-portal
    └─back-center
        └─src
            ├─main
            │  ├─java
            │  │  └─com
            │  │      └─xm
            │  │          └─web
            │  │              └─controller
            │  └─resources
            └─test
                ├─java
                │  └─com
                │      └─xm
                │          └─web
                └─resources
                    ├─static
                    │  ├─file
                    │  └─images
                    └─templates

</code></pre>
</details>