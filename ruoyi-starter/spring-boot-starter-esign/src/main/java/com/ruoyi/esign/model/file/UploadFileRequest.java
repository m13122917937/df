package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 上传文件请求参数
 * 文档：https://open.esign.cn/doc/opendoc/file-and-template/qxq4td
 *
 * @author ruoyi
 */
@Data
public class UploadFileRequest {

    /**
     * 文件名称，需要带文件后缀
     * 例如：test.pdf
     */
    private String fileName;

    /**
     * 文件字节数组，需要转成Base64编码
     * 需要pdf格式文件，文件大小不能超过50MB
     */
    private String fileContent;

    /**
     * 是否转换成pdf，仅针对word文件（.doc/.docx）有效
     * 默认false，如果传true，word将被转换成pdf保存在e签宝服务器
     */
    private Boolean convert2Pdf = false;
}
