<!-- 发起签署弹窗 -->
<template>
  <el-dialog
    title="发起签署"
    :visible.sync="dialogVisible"
    width="720px"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form
      :model="form"
      :rules="rules"
      ref="launchForm"
      label-width="140px"
    >
      <el-form-item label="合同文件" prop="esignFileId">
        <el-upload
          action=""
          :http-request="uploadFile"
          :show-file-list="true"
          :file-list="fileList"
          :limit="1"
          accept=".pdf,.doc,.docx"
          :on-remove="handleRemove"
        >
          <el-button size="small" type="primary" :loading="uploading">
            选择文件并上传
          </el-button>
          <div slot="tip" class="el-upload__tip">
            支持 pdf / doc / docx，docx 会自动转 PDF；上传后返回 e 签宝 fileId
          </div>
        </el-upload>
        <el-input
          v-model="form.esignFileId"
          v-if="form.esignFileId"
          readonly
          size="mini"
          style="margin-top: 8px"
        />
      </el-form-item>

      <el-divider content-position="left">我方签署人</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="经办人姓名" prop="ourSignerName">
            <el-input v-model="form.ourSignerName" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="经办人手机号" prop="ourOperatorMobile">
            <el-input
              v-model="form.ourOperatorMobile"
              placeholder="请输入"
              maxlength="11"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="经办人证件号" prop="ourOperatorIdNumber">
            <el-input v-model="form.ourOperatorIdNumber" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="我方印章ID" prop="ourSealId">
            <el-input
              v-model="form.ourSealId"
              placeholder="请输入 e 签宝 sealId"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">供应商签署人</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="经办人姓名" prop="vendorSignerName">
            <el-input v-model="form.vendorSignerName" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="经办人手机号" prop="vendorMobile">
            <el-input
              v-model="form.vendorMobile"
              placeholder="请输入"
              maxlength="11"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="经办人证件号" prop="vendorIdNumber">
            <el-input v-model="form.vendorIdNumber" placeholder="请输入" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" v-throttle-click="600">
        确认发起
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import {
  uploadContractFileApi,
  launchContractApi,
} from "@/api/contract";

export default {
  name: "ContractLaunchDialog",
  props: {
    visible: { type: Boolean, default: false },
    contract: { type: Object, default: () => null },
  },
  data() {
    return {
      dialogVisible: false,
      uploading: false,
      fileList: [],
      form: this.buildEmpty(),
      rules: {
        esignFileId: [
          { required: true, message: "请上传合同文件", trigger: "change" },
        ],
        ourOperatorMobile: [
          {
            pattern: /^1\d{10}$/,
            message: "手机号格式不正确",
            trigger: "blur",
          },
        ],
        vendorMobile: [
          { required: true, message: "请输入供应商经办人手机号", trigger: "blur" },
          {
            pattern: /^1\d{10}$/,
            message: "手机号格式不正确",
            trigger: "blur",
          },
        ],
        vendorSignerName: [
          { required: true, message: "请输入供应商经办人姓名", trigger: "blur" },
        ],
      },
    };
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val) this.resetForm();
    },
  },
  methods: {
    buildEmpty() {
      return {
        id: null,
        esignFileId: "",
        ourSignerName: "",
        ourOperatorMobile: "",
        ourOperatorIdNumber: "",
        ourSealId: "",
        vendorSignerName: "",
        vendorMobile: "",
        vendorIdNumber: "",
      };
    },
    resetForm() {
      this.form = this.buildEmpty();
      this.form.id = this.contract && this.contract.id;
      this.fileList = [];
      this.$nextTick(() => {
        if (this.$refs.launchForm) this.$refs.launchForm.clearValidate();
      });
    },
    async uploadFile({ file }) {
      this.uploading = true;
      try {
        const formData = new FormData();
        formData.append("file", file);
        const res = await uploadContractFileApi(formData);
        const fileId = res.data || res;
        this.form.esignFileId = fileId;
        this.fileList = [{ name: file.name, uid: file.uid }];
        this.$message.success("上传成功");
      } catch (e) {
        this.fileList = [];
        this.$message.error("上传失败");
      } finally {
        this.uploading = false;
      }
    },
    handleRemove() {
      this.form.esignFileId = "";
      this.fileList = [];
    },
    handleClose() {
      this.dialogVisible = false;
      this.$emit("update:visible", false);
    },
    handleSubmit() {
      this.$refs.launchForm.validate(async (valid) => {
        if (!valid) return;
        await launchContractApi(this.form);
        this.$message.success("发起签署成功");
        this.$emit("success");
        this.handleClose();
      });
    },
  },
};
</script>
