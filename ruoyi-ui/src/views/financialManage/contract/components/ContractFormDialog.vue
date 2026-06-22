<!-- 合同新增/编辑弹窗 -->
<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    width="700px"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form
      :model="form"
      :rules="rules"
      ref="contractForm"
      label-width="120px"
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="合同名称" prop="contractName">
            <el-input
              v-model="form.contractName"
              placeholder="请输入合同名称"
              clearable
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="合同类型" prop="contractType">
            <el-select
              v-model="form.contractType"
              placeholder="请选择合同类型"
              clearable
              style="width: 100%"
            >
              <el-option label="采购合同" :value="1" />
              <el-option label="框架协议" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="我方主体" prop="ourPayerId">
            <el-select
              v-model="form.ourPayerId"
              placeholder="请选择我方主体"
              clearable
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="p in payerOptions"
                :key="p.id"
                :label="p.payName"
                :value="p.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="供应商" prop="vendorCompanyId">
            <el-select
              v-model="form.vendorCompanyId"
              placeholder="请输入供应商名称搜索"
              filterable
              remote
              clearable
              :remote-method="searchCompany"
              :loading="companyLoading"
              style="width: 100%"
            >
              <el-option
                v-for="c in companyOptions"
                :key="c.id"
                :label="c.companyName"
                :value="c.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="过期时间" prop="expireTime">
            <el-date-picker
              v-model="form.expireTime"
              type="datetime"
              placeholder="请选择过期时间"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="form.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave" v-throttle-click="600"
        >保存</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import {
  saveContractApi,
  updateContractApi,
  listPayerApi,
  listCompanyApi,
} from "@/api/contract";

export default {
  name: "ContractFormDialog",
  props: {
    visible: { type: Boolean, default: false },
    detail: { type: Object, default: () => null },
  },
  data() {
    return {
      dialogVisible: false,
      payerOptions: [],
      companyOptions: [],
      companyLoading: false,
      form: this.buildEmpty(),
      rules: {
        contractName: [
          { required: true, message: "请输入合同名称", trigger: "blur" },
        ],
        contractType: [
          { required: true, message: "请选择合同类型", trigger: "change" },
        ],
        ourPayerId: [
          { required: true, message: "请选择我方主体", trigger: "change" },
        ],
        vendorCompanyId: [
          { required: true, message: "请选择供应商", trigger: "change" },
        ],
      },
    };
  },
  computed: {
    isEdit() {
      return !!(this.form && this.form.id);
    },
    dialogTitle() {
      return this.isEdit ? "编辑合同" : "新增合同";
    },
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val) {
        this.initForm();
        this.loadPayer();
      }
    },
  },
  methods: {
    buildEmpty() {
      return {
        id: null,
        contractName: "",
        contractType: null,
        ourPayerId: null,
        vendorCompanyId: null,
        expireTime: null,
        remark: "",
      };
    },
    initForm() {
      if (this.detail && this.detail.id) {
        this.form = {
          id: this.detail.id,
          contractName: this.detail.contractName,
          contractType: this.detail.contractType,
          ourPayerId: this.detail.ourPayerId,
          vendorCompanyId: this.detail.vendorCompanyId,
          expireTime: this.detail.expireTime,
          remark: this.detail.remark,
        };
        if (this.detail.vendorCompanyId && this.detail.vendorCompanyName) {
          this.companyOptions = [
            {
              id: this.detail.vendorCompanyId,
              companyName: this.detail.vendorCompanyName,
            },
          ];
        }
      } else {
        this.form = this.buildEmpty();
        this.companyOptions = [];
      }
      this.$nextTick(() => {
        if (this.$refs.contractForm) this.$refs.contractForm.clearValidate();
      });
    },
    async loadPayer() {
      const res = await listPayerApi();
      this.payerOptions = res.data || res || [];
    },
    async searchCompany(keyword) {
      if (!keyword) {
        this.companyOptions = [];
        return;
      }
      this.companyLoading = true;
      try {
        const res = await listCompanyApi({ companyName: keyword });
        this.companyOptions = res.rows || res.data || [];
      } finally {
        this.companyLoading = false;
      }
    },
    handleClose() {
      this.dialogVisible = false;
      this.$emit("update:visible", false);
    },
    handleSave() {
      this.$refs.contractForm.validate(async (valid) => {
        if (!valid) return;
        if (this.isEdit) {
          await updateContractApi(this.form);
          this.$message.success("修改成功");
        } else {
          await saveContractApi(this.form);
          this.$message.success("保存成功");
        }
        this.$emit("success");
        this.handleClose();
      });
    },
  },
};
</script>
