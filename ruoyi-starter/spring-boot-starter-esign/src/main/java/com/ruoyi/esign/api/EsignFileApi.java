package com.ruoyi.esign.api;

import com.ruoyi.esign.model.file.GetFileDownloadUrlRequest;
import com.ruoyi.esign.model.file.GetFileDownloadUrlResponse;
import com.ruoyi.esign.model.file.GetFileInfoRequest;
import com.ruoyi.esign.model.file.GetFileInfoResponse;
import com.ruoyi.esign.model.file.UploadFileRequest;
import com.ruoyi.esign.model.file.UploadFileResponse;

/**
 * e签宝文件服务API接口
 * 包含文件上传、查询文件信息、获取文件下载链接
 *
 * @author ruoyi
 */
public interface EsignFileApi {

    /**
     * 上传文件
     * 将PDF/Word文件上传到e签宝服务器，获取fileId用于后续签署流程
     *
     * @param request 上传文件请求参数
     * @return 上传文件响应结果
     */
    UploadFileResponse uploadFile(UploadFileRequest request);

    /**
     * 查询文件信息
     * 根据文件ID查询已上传文件的详细信息
     *
     * @param request 查询文件信息请求参数
     * @return 查询文件信息响应结果
     */
    GetFileInfoResponse getFileInfo(GetFileInfoRequest request);

    /**
     * 获取文件下载链接
     * 根据文件ID获取文件的下载链接，链接可设置有效期
     *
     * @param request 获取文件下载链接请求参数
     * @return 获取文件下载链接响应结果
     */
    GetFileDownloadUrlResponse getFileDownloadUrl(GetFileDownloadUrlRequest request);
}
