<!-- 新增/编辑企业弹框 -->
<template>
  <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="860px" :before-close="handleClose"
    class="company-form-dialog">
    <el-form :model="form" :rules="rules" ref="companyForm" label-width="140px" class="company-form">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="企业名称" prop="companyName">
            <el-input v-model="form.companyName" placeholder="请输入企业名称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="企业别名" prop="nickName">
            <el-input v-model="form.nickName" placeholder="请输入企业别名" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="法人名称" prop="corporateName">
            <el-input v-model="form.corporateName" placeholder="请输入法人名称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="统一社会信用代码" prop="creditCode">
            <el-input v-model="form.creditCode" placeholder="请输入统一社会信用代码" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="省" prop="provinceId">
            <el-select v-model="form.provinceId" placeholder="请选择省" clearable filterable style="width: 100%"
              @change="handleProvinceChange">
              <el-option v-for="item in provinceOptions" :key="item.districtId" :label="item.district"
                :value="item.districtId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="市" prop="cityId">
            <el-select v-model="form.cityId" placeholder="请选择市" clearable filterable style="width: 100%"
              :disabled="!form.provinceId" @change="handleCityChange">
              <el-option v-for="item in cityOptions" :key="item.districtId" :label="item.district"
                :value="item.districtId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="企业成立时间" prop="establishedTime">
            <el-date-picker v-model="form.establishedTime" type="date" placeholder="请选择企业成立时间" format="yyyy-MM-dd"
              value-format="yyyy-MM-dd" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="停款状态" prop="stopPurchase">
            <el-radio-group v-model="form.stopPurchase">
              <el-radio :label="0">停款</el-radio>
              <el-radio :label="1">正常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="账期类型" prop="accountingPeriod">
            <el-select v-model="form.accountingPeriod" placeholder="请选择账期类型" clearable style="width: 100%">
              <el-option label="现款" :value="0" />
              <el-option label="T+1" :value="1" />
              <el-option label="T+2" :value="2" />
              <el-option label="T+3" :value="3" />
              <el-option label="T+4" :value="4" />
              <el-option label="T+5" :value="5" />
              <el-option label="T+6" :value="6"></el-option>
              <el-option label="T+7" :value="7"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否禁抢" prop="grabStatus">
            <el-radio-group v-model="form.grabStatus">
              <el-radio :label="0">未禁止</el-radio>
              <el-radio :label="1">禁止</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave" v-throttle-click="600">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addBusinessCompanyApi, editBusinessCompanyApi } from '@/api/business'
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'CompanyFormDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editData: {
      type: Object,
      default: null
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      form: {
        companyName: '',
        nickName: '',
        corporateName: '',
        creditCode: '',
        province: '',
        provinceId: '',
        city: '',
        cityId: '',
        establishedTime: '',
        stopPurchase: 1,
        accountingPeriod: null,
        grabStatus: 0
      },
      rules: {
        companyName: [
          { required: true, message: '请输入企业名称', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '请输入企业别名', trigger: 'blur' }
        ],
        corporateName: [
          { required: true, message: '请输入法人名称', trigger: 'blur' }
        ],
        creditCode: [
          { required: true, message: '请输入统一社会信用代码', trigger: 'blur' }
        ],
        provinceId: [
          { required: true, message: '请选择省', trigger: 'change' }
        ],
        cityId: [
          { required: true, message: '请选择市', trigger: 'change' }
        ],
        establishedTime: [
          { required: true, message: '请选择企业成立时间', trigger: 'change' }
        ],
        stopPurchase: [
          { required: true, message: '请选择停款状态', trigger: 'change' }
        ],
        accountingPeriod: [],
        grabStatus: [
          { required: true, message: '请选择是否禁抢', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['provinceList', 'cityList']),
    // 从Vuex获取省份列表
    provinceOptions() {
      return this.provinceList || []
    },
    // 从Vuex获取城市列表
    cityOptions() {
      return this.cityList || []
    },
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    dialogTitle() {
      return this.isEdit ? '编辑企业信息' : '新增企业信息'
    }
  },
  created() {
    this.getProvinceList()
  },
  watch: {
    editData: {
      async handler(newVal) {
        if (newVal && this.isEdit) {
          this.form = { ...newVal }
          // 如果编辑数据中有省份，需要加载对应的城市列表
          if (newVal.provinceId) {
            await this.getCityList(newVal.provinceId)
          }
        } else if (!this.isEdit) {
          this.resetForm()
        }
      },
      immediate: true,
      deep: true
    },
    isEdit: {
      handler(newVal) {
        if (!newVal) {
          this.resetForm()
        }
      }
    }
  },
  methods: {
    ...mapActions(['GET_PROVINCE_LIST', 'GET_CITY_LIST']),
    async getProvinceList() {
      // 确保Vuex中有省份数据
      await this.GET_PROVINCE_LIST()
    },
    async getCityList(provinceId) {
      await this.GET_CITY_LIST(provinceId)
    },
    async handleProvinceChange(value) {
      this.form.cityId = ''
      this.form.city = ''

      if (value) {
        // 根据选中的省份ID找到对应的省份名称
        const selectedProvince = this.provinceOptions.find(item => item.districtId === value)
        if (selectedProvince) {
          this.form.province = selectedProvince.district
        }

        // 根据省份获取城市列表
        await this.getCityList(value)
      } else {
        this.form.province = ''
        // 清空城市列表通过store action
        this.$store.commit('SET_CITY_LIST', [])
      }
    },
    handleCityChange(value) {
      if (value) {
        // 根据选中的城市ID找到对应的城市名称
        const selectedCity = this.cityOptions.find(item => item.districtId === value)
        if (selectedCity) {
          this.form.city = selectedCity.district
        }
      } else {
        this.form.city = ''
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.resetForm()
    },
    handleSave() {
      this.$refs.companyForm.validate(async (valid) => {
        if (valid) {
          const formData = { ...this.form }
          let res = null;
          if (this.isEdit) {
            res = await editBusinessCompanyApi(formData)
            if (res.code === 200) {
              this.$message.success('编辑成功')
              this.$emit('save')
              this.dialogVisible = false
              this.resetForm()
            }
          } else {
            res = await addBusinessCompanyApi(formData)
            if (res.code === 200) {
              this.$message.success('新增成功')
              this.$emit('save')
              this.dialogVisible = false
              this.resetForm()
            }
          }
        } else {
          this.$message.error('请检查表单信息')
          return false
        }
      })
    },
    resetForm() {
      this.form = {
        companyName: '',
        nickName: '',
        corporateName: '',
        creditCode: '',
        province: '',
        provinceId: '',
        city: '',
        cityId: '',
        establishedTime: '',
        stopPurchase: 1,
        accountingPeriod: null,
        grabStatus: 0
      }
      // 清空城市列表通过store
      this.$store.commit('SET_CITY_LIST', [])
      if (this.$refs.companyForm) {
        this.$refs.companyForm.clearValidate()
      }
    }
  }
}
</script>

<style scoped lang="scss">
.company-form-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    overflow: hidden;
  }

  ::v-deep .el-dialog__header {
    background: #f5f7fa;
    color: #303133;
    padding: 20px 24px;
    border-bottom: 1px solid #e4e7ed;

    .el-dialog__title {
      color: #303133;
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: #909399;
        font-size: 20px;

        &:hover {
          color: #409eff;
        }
      }
    }
  }

  ::v-deep .el-dialog__body {
    padding: 24px;
    background: #fff;
  }

  .company-form {
    ::v-deep .el-form-item {
      margin-bottom: 20px;

      .el-form-item__label {
        color: #303133;
        font-weight: 500;
        font-size: 14px;
      }

      .el-form-item__content {
        .el-input {
          .el-input__inner {
            border-radius: 4px;
            border: 1px solid #dcdfe6;
            transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
            height: 32px;
            line-height: 32px;

            &:focus {
              border-color: #409eff;
            }
          }
        }

        .el-select {
          .el-input__inner {
            height: 32px;
            line-height: 32px;
          }
        }

        .el-date-editor {
          .el-input__inner {
            height: 32px;
            line-height: 32px;
          }
        }
      }
    }
  }

  .dialog-footer {
    background: #f8f9fa;
    padding: 16px 24px;
    border-top: 1px solid #e9ecef;
    text-align: right;

    .el-button {
      margin-left: 10px;
      padding: 8px 20px;
      border-radius: 4px;
      font-weight: 500;
      min-width: 80px;

      &:first-child {
        margin-left: 0;
      }

      &.el-button--primary {
        background-color: #409eff;
        border-color: #409eff;

        &:hover {
          background-color: #66b1ff;
          border-color: #66b1ff;
        }
      }
    }
  }
}
</style>
