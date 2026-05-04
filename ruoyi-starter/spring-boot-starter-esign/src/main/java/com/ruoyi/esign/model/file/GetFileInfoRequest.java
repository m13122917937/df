package com.ruoyi.esign.model.file;

import lombok.Data;

/**
 * 查询文件信息请求参数
 * 文档：https://open.esign.cn/doc/opendoc/file-and-template/bl03hx
 *
 * @author ruoyi
 */
@Data
public class GetFileInfoRequest {

    /**
     * 文件ID
     */
    private String fileId;
}
