package com.ruoyi.esign.model.sign;

import lombok.Data;

import java.util.List;

/**
 * 基于文件发起签署请求参数。
 *
 * @author ruoyi
 */
@Data
public class CreateByFileRequest {

    private List<Doc> docs;

    private SignFlowConfig signFlowConfig;

    private List<Signer> signers;

    @Data
    public static class Doc {
        private String fileId;
        private String fileName;
    }

    @Data
    public static class SignFlowConfig {
        private String signFlowTitle;
        private Long signFlowExpireTime;
        private Boolean autoFinish;
        private String notifyUrl;
        private NoticeConfig noticeConfig;
        private RedirectConfig redirectConfig;
    }

    @Data
    public static class NoticeConfig {
        private String noticeTypes;
        private Boolean examineNotice;
    }

    @Data
    public static class RedirectConfig {
        private String redirectUrl;
    }

    @Data
    public static class Signer {
        private SignConfig signConfig;
        private NoticeConfig noticeConfig;
        private Integer signerType;
        private OrgSignerInfo orgSignerInfo;
        private PsnSignerInfo psnSignerInfo;
        private List<SignField> signFields;
    }

    @Data
    public static class SignConfig {
        private Integer signOrder;
        private Integer forcedReadingTime;
    }

    @Data
    public static class OrgSignerInfo {
        private String orgName;
        private OrgInfo orgInfo;
        private TransactorInfo transactorInfo;
    }

    @Data
    public static class OrgInfo {
        private String orgIDCardNum;
        private String orgIDCardType;
    }

    @Data
    public static class TransactorInfo {
        private String psnAccount;
        private PsnInfo psnInfo;
    }

    @Data
    public static class PsnSignerInfo {
        private String psnAccount;
        private PsnInfo psnInfo;
    }

    @Data
    public static class PsnInfo {
        private String psnName;
        private String psnIDCardNum;
        private String psnIDCardType;
    }

    @Data
    public static class SignField {
        private String fileId;
        private String customBizNum;
        private NormalSignFieldConfig normalSignFieldConfig;
    }

    @Data
    public static class NormalSignFieldConfig {
        private Boolean freeMode;
        private Boolean autoSign;
        private String assignedSealId;
        private Float signFieldSize;
        private Integer signFieldStyle;
        private SignFieldPosition signFieldPosition;
    }

    @Data
    public static class SignFieldPosition {
        private String acrossPageMode;
        private String positionPage;
        private Float positionX;
        private Float positionY;
    }
}
