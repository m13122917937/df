<!-- 新增银行账户弹框 -->
<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    width="600px"
    :before-close="handleClose"
    class="account-form-dialog"
  >
    <el-form
      :model="form"
      :rules="rules"
      ref="accountForm"
      label-width="100px"
      class="account-form"
    >
      <el-form-item label="开户银行" prop="accountBank">
        <el-input
          v-model="form.accountBank"
          placeholder="请输入开户银行"
          clearable
          trim
        />
      </el-form-item>
      
      <el-form-item label="银行账号" prop="bankAccount">
        <el-input
          v-model="form.bankAccount"
          placeholder="请输入银行账号"
          clearable
          trim
        />
      </el-form-item>
      
      <el-form-item label="注册省" prop="province">
        <el-select v-model="form.province" placeholder="请选择注册省" style="width: 100%" @change="handleProvinceChange">
          <el-option
            v-for="item in provinceOptions"
            :key="item.districtId"
            :label="item.district"
            :value="item.districtId"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="注册市" prop="city">
        <el-select v-model="form.city" placeholder="请选择注册市" style="width: 100%">
          <el-option
            v-for="item in cityOptions"
            :key="item.districtId"
            :label="item.district"
            :value="item.districtId"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="是否默认" prop="defaulted">
        <el-radio-group v-model="form.defaulted">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave" v-throttle-click="600">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addBusinessBankApi, editBusinessBankApi } from '@/api/business'
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'AccountFormDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    companyId: {
      type: [Number, String],
      default: 0
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
        accountBank: '',
        bankAccount: '',
        province: '',
        city: '',
        defaulted: 0
      },
      rules: {
        accountBank: [
          { required: true, message: '请输入开户银行', trigger: 'blur' }
        ],
        bankAccount: [
          { required: true, message: '请输入银行账号', trigger: 'blur' }
        ],
        province: [
          { required: true, message: '请选择注册省', trigger: 'change' }
        ],
        city: [
          { required: true, message: '请选择注册市', trigger: 'change' }
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
      return this.isEdit ? '编辑银行账户' : '新增银行账户'
    }
  },
  watch: {
    editData: {
      handler(newVal) {
        if (newVal && this.isEdit) {
          this.form = { ...newVal }
          if (newVal.province) {
            this.getCityList(newVal.province)
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
  created() {
    this.getProvinceList()
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
    handleProvinceChange(value) {
      this.form.cityId = ''
      this.form.city = ''
      if (value) {
        this.getCityList(value)
      } else {
        // 清空城市列表
        this.$store.commit('SET_CITY_LIST', [])
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.resetForm()
    },
    async handleSave() {
      this.$refs.accountForm.validate(async (valid) => {
        if (valid) {
          try {
            let res = null
            const formData = { 
                accountBank: this.form.accountBank,
                bankAccount: this.form.bankAccount,
                province: this.form.province,
                city: this.form.city,
                defaulted: this.form.defaulted,
                companyId: this.companyId, // 企业id
                id: this.form.id || 0 // 银行账户id
               }
            if (this.isEdit) {
              res = await editBusinessBankApi(formData)
            } else {
              res = await addBusinessBankApi(formData)
            }
            if (res.code === 200) {
              this.$message.success(this.isEdit ? '编辑成功' : '新增成功')
              this.$emit('success')
              this.dialogVisible = false
              this.resetForm()
            }
          } catch (error) {
            this.$message.error('操作失败')
            console.error('保存失败:', error)
          }
        } else {
          this.$message.error('请检查表单信息')
          return false
        }
      })
    },
    resetForm() {
      this.form = {
        accountBank: '',
        bankAccount: '',
        province: '',
        city: '',
        defaulted: 0
      }
      // 清空城市列表通过store
      this.$store.commit('SET_CITY_LIST', [])
      if (this.$refs.accountForm) {
        this.$refs.accountForm.clearValidate()
      }
    }
  }
}
</script>

<style scoped lang="scss">
// 表单弹框样式
.account-form-dialog {
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
  
  .account-form {
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
