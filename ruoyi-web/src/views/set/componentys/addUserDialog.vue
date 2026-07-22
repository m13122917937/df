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
        <el-step title="填写信息" description="输入用户基本信息" />
        <el-step title="扫码添加" description="微信扫码完成注册" />
      </el-steps>
    </div>

    <!-- 二维码显示区域 -->
    <div v-show="imgUrl" class="qr-code-section">
      <div class="qr-code-container">
        <div class="qr-code-wrapper">
          <img :src="imgUrl" alt="注册二维码" class="qr-code-image">
        </div>
        <div class="qr-code-text">
          <h3>请注意：请被添加子账号，使用微信扫码完成注册</h3>
        </div>
      </div>
    </div>

    <!-- 表单区域 -->
    <el-form
      v-show="!imgUrl"
      ref="addUserForm"
      :model="form"
      :rules="rules"
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
        <el-radio-group v-model="form.owner" disabled>
          <el-radio :label="0">是</el-radio>
          <el-radio :label="1">否</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <!-- 底部按钮区域 -->
    <div slot="footer" class="dialog-footer">
      <el-button :disabled="loading" @click="handleClose">
        {{ imgUrl ? '取消' : '关闭' }}
      </el-button>
      <el-button
        v-if="!imgUrl"
        type="primary"
        :loading="loading"
        @click="handleSave"
      >
        生成二维码
      </el-button>
      <el-button
        v-if="imgUrl"
        type="primary"
        :disabled="loading"
        @click="handleComplete"
      >
        完成添加
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addSubAccount } from '@/api/set'
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
        owner: 1
      })
    }
  },
  data() {
    return {
      form: {
        nickName: '',
        phone: '',
        owner: 1
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
          console.log('formData changed:', this.form)
        }
      },
      immediate: true,
      deep: true
    },
    visible: {
      handler(newVal) {
        if (newVal && this.formData) {
          this.form = { ...this.form, ...this.formData }
          console.log('dialog opened, form updated:', this.form)
        }
      }
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
      this.$refs.addUserForm.validate((valid) => {
        if (valid) {
          this.loading = true
          addSubAccount(this.form).then(res => {
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
      this.$confirm('确认已完成子账号注册？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.$message.success('子账号添加完成')
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
          color: #1677FF;
        }
      }
    }
  }

  ::v-deep .el-dialog__body {
    padding: 24px;
    background: #fff;
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
        color: #909399;
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
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .qr-code-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(255, 255, 255, 0.9);
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 8px;

          i {
            font-size: 24px;
            color: #1677FF;
            animation: rotate 2s linear infinite;
          }
        }
      }

      .qr-code-text {
        h3 {
          margin: 0 0 8px 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        p {
          margin: 0;
          font-size: 14px;
          color: #606266;
          line-height: 1.5;
        }
      }
    }
  }

  .add-user-form {
    ::v-deep .el-form-item {
      margin-bottom: 24px;

      .el-form-item__label {
        color: #303133;
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
            border: 1px solid #dcdfe6;
            transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
            height: 32px;
            line-height: 32px;

            &:focus {
              border-color: #1677FF;
            }
          }
        }

        .el-radio-group {
          .el-radio {
            margin-right: 20px;

            .el-radio__label {
              color: #606266;
              font-weight: 500;
              font-size: 14px;
            }

            .el-radio__input.is-checked .el-radio__inner {
              background-color: #1677FF;
              border-color: #1677FF;
            }
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
        background-color: #1677FF;
        border-color: #1677FF;

        &:hover {
          background-color: #3395FF;
          border-color: #3395FF;
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
