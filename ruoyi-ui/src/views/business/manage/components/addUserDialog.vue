<!-- 新增用户弹框 -->
<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    width="500px"
    :before-close="handleClose"
    class="add-user-dialog"
    :close-on-click-modal="false"
  >
    <!-- 步骤指示器 -->
    <div class="step-indicator">
      <el-steps :active="currentStep" align-center>
        <el-step title="填写信息" description="输入用户基本信息"></el-step>
        <el-step title="扫码注册" description="微信扫码完成注册"></el-step>
      </el-steps>
    </div>

    <!-- 二维码显示区域 -->
    <div v-show="imgUrl" class="qr-code-section">
      <div class="qr-code-container">
        <div class="qr-code-wrapper">
          <img :src="imgUrl" alt="注册二维码" class="qr-code-image" />
        </div>
        <div class="qr-code-text">
          <h3>请使用微信扫码完成注册</h3>
          <p>扫码后请按照提示完成用户注册流程</p>
        </div>
      </div>
    </div>

    <!-- 表单区域 -->
    <el-form
      v-show="!imgUrl"
      :model="form"
      :rules="rules"
      ref="addUserForm"
      label-width="100px"
      class="add-user-form"
    >
      <el-form-item label="* 用户:" prop="nickName">
        <el-input
          v-model="form.nickName"
          placeholder="请输入用户名"
          clearable
          prefix-icon="el-icon-user"
        />
      </el-form-item>
      
      <el-form-item label="* 手机号:" prop="phone">
        <el-input
          v-model="form.phone"
          placeholder="请输入手机号"
          clearable
          prefix-icon="el-icon-phone"
        />
      </el-form-item>
      
      <el-form-item label="* 是否主账号:" prop="owner">
        <el-radio-group v-model="form.owner">
          <el-radio :label="0">是</el-radio>
          <el-radio :label="1">否</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <!-- 底部按钮区域 -->
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose" :disabled="loading">
        {{ imgUrl ? '取消' : '关闭' }}
      </el-button>
      <el-button 
        v-if="!imgUrl" 
        type="primary" 
        @click="handleSave" 
        :loading="loading"
      >
        生成二维码
      </el-button>
      <el-button 
        v-if="imgUrl" 
        type="primary" 
        @click="handleComplete"
        :disabled="loading"
      >
        完成注册
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addBusinessUserApi } from '@/api/business'
export default {
  name: 'AddUserDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    formData: {
      type: Object,
      default: () => ({
        nickName: '',
        phone: '',
        companyId: '',
        owner: 0
      })
    },
    companyId: {
      type: [String, Number],
      default: ''
    }
  },
  data() {
    return {
      form: {
        nickName: '',
        phone: '',
        companyId: '',
        owner: 0
      },
      imgUrl: '',
      loading: false,
      currentStep: 0,
      rules: {
        nickName: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        owner: [
          { required: true, message: '请选择是否为主账号', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    dialogTitle() {
      return this.imgUrl ? '用户注册' : '新增用户'
    }
  },
  watch: {
    formData: {
      handler(newVal) {
        if (newVal) {
          this.form = { ...this.form, ...newVal }
        }
      },
      immediate: true,
      deep: true
    },
    visible: {
      handler(newVal) {
        if (newVal) {
          // 当弹框打开时，确保 companyId 被正确设置
          if (this.companyId) {
            this.form.companyId = this.companyId
          }
          if (this.formData) {
            this.form = { ...this.form, ...this.formData }
          }
        }
      }
    },
    companyId: {
      handler(newVal) {
        if (newVal) {
          this.form.companyId = newVal
        }
      },
      immediate: true
    }
  },
  methods: {
    handleClose() {
      this.resetDialog()
      this.dialogVisible = false
    },
    handleReset() {
      this.form = {
        nickName: '',
        phone: '',
        companyId: '',
        owner: 0
      }
      this.$emit('reset-form', this.form)
    },
    resetDialog() {
      this.imgUrl = ''
      this.currentStep = 0
      this.loading = false
      this.handleReset()
    },
    async handleSave() {
      
      // 确保 companyId 存在
      if (!this.form.companyId && this.companyId) {
        this.form.companyId = this.companyId
      }
      
      // 验证 companyId 是否存在
      if (!this.form.companyId) {
        this.$message.error('企业ID不能为空，请重新打开弹框')
        return
      }
      
      this.$refs.addUserForm.validate((valid) => {
        if (valid) {
          this.loading = true
          addBusinessUserApi(this.form).then(res => {
            if (res.code === 200) {
              this.$message.success('请扫码完成注册')
              this.imgUrl = res.data
              this.currentStep = 1
            } else {
              this.$message.error(res.message || '生成二维码失败')
            }
          }).catch(error => {
            console.error('生成二维码失败:', error)
            this.$message.error('生成二维码失败，请重试')
          }).finally(() => {
            this.loading = false
          })
        } else {
          return false
        }
      })
    },
    handleComplete() {
      this.$confirm('确认已完成用户注册？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.$message.success('用户注册完成')
        this.$emit('save', this.form)
        this.handleClose()
      }).catch(() => {
        // 用户取消
      })
    }
  }
}
</script>

<style scoped lang="scss">
.add-user-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    overflow: hidden;
  }
  
  ::v-deep .el-dialog__header {
    background: var(--bg-table-header);
    color: var(--adm-text-primary);
    padding: 20px 24px;
    border-bottom: 1px solid var(--adm-border);
    
    .el-dialog__title {
      color: var(--adm-text-primary);
      font-size: 18px;
      font-weight: 600;
    }
    
    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;
      
      .el-dialog__close {
        color: var(--adm-text-tertiary);
        font-size: 20px;
        
        &:hover {
          color: var(--primary-color);
        }
      }
    }
  }
  
  ::v-deep .el-dialog__body {
    padding: 24px;
    background: var(--adm-card);
  }
  
  // 步骤指示器
  .step-indicator {
    margin-bottom: 24px;
    
    ::v-deep .el-steps {
      .el-step__head {
        .el-step__icon {
          width: 32px;
          height: 32px;
          line-height: 32px;
        }
      }
      
      .el-step__title {
        font-size: 14px;
        font-weight: 500;
      }
      
      .el-step__description {
        font-size: 12px;
        color: var(--adm-text-tertiary);
      }
    }
  }
  
  // 二维码区域
  .qr-code-section {
    text-align: center;
    padding: 20px 0;
    
    .qr-code-container {
      .qr-code-wrapper {
        position: relative;
        display: inline-block;
        margin-bottom: 20px;
        
        .qr-code-image {
          width: 300px;
          height: 300px;
          border: 1px solid var(--adm-border);
          border-radius: 8px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        .qr-code-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: var(--adm-card);
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 8px;
          
          i {
            font-size: 24px;
            color: var(--primary-color);
            animation: rotate 2s linear infinite;
          }
        }
      }
      
      .qr-code-text {
        h3 {
          margin: 0 0 8px 0;
          font-size: 16px;
          font-weight: 600;
          color: var(--adm-text-primary);
        }
        
        p {
          margin: 0;
          font-size: 14px;
          color: var(--adm-text-secondary);
          line-height: 1.5;
        }
      }
    }
  }
  
  .add-user-form {
    ::v-deep .el-form-item {
      margin-bottom: 24px;
      
      .el-form-item__label {
        color: var(--adm-text-primary);
        font-weight: 500;
        font-size: 14px;
        text-align: right;
        padding-right: 12px;
        
        &::before {
          content: '';
          display: none;
        }
      }
      
      .el-form-item__content {
        .el-input {
          .el-input__inner {
            border-radius: 4px;
            border: 1px solid var(--adm-border);
            transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
            height: 32px;
            line-height: 32px;
            
            &:focus {
              border-color: var(--primary-color);
            }
          }
        }
        
        .el-radio-group {
          .el-radio {
            margin-right: 20px;
            
            .el-radio__label {
              color: var(--adm-text-secondary);
              font-weight: 500;
              font-size: 14px;
            }
            
            .el-radio__input.is-checked .el-radio__inner {
              background-color: var(--primary-color);
              border-color: var(--primary-color);
            }
          }
        }
      }
    }
  }
  
  .dialog-footer {
    background: var(--bg-table-header);
    padding: 16px 24px;
    border-top: 1px solid var(--adm-border);
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
        background-color: var(--primary-color);
        border-color: var(--primary-color);
        
        &:hover {
          background-color: var(--primary-hover);
          border-color: var(--primary-hover);
        }
      }
    }
  }
}

// 动画效果
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .add-user-dialog {
    ::v-deep .el-dialog {
      width: 90% !important;
      margin: 0 auto;
    }
    
    .qr-code-section {
      .qr-code-container {
        .qr-code-wrapper {
          .qr-code-image {
            width: 160px;
            height: 160px;
          }
        }
      }
    }
  }
}
</style>
