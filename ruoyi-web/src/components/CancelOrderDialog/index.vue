<!-- 毁单弹窗组件 -->
<template>
  <el-dialog
    title="提示"
    :visible.sync="dialogVisible"
    width="800px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="cancel-order-content">
      <!-- 取消方式选择 -->
      <div class="cancellation-options">
        <div class="option-item" :class="{ active: applyType === 1 }">
          <el-radio
            v-model="applyType"
            :label="1"
            :aria-hidden="false"
            @change="handleapplyTypeChange"
          >
            <span class="radio-label">立即毁单</span>
          </el-radio>
          <div class="option-content">
            <div class="option-warning">
              毁单后,将被扣除40元保证金!
            </div>
          </div>
        </div>

        <div class="option-item" :class="{ active: applyType === 2 }">
          <el-radio
            v-model="applyType"
            :label="2"
            :aria-hidden="false"
            @change="handleapplyTypeChange"
          >
            <span class="radio-label">免责毁单</span>
          </el-radio>
          <div class="option-content">
            <div class="option-description">
              若采购方同意,将不扣除保证金。
            </div>
          </div>
          <!-- 毁单理由选择 - 仅在免责毁单时显示 -->
          <div class="reason-section">
            <div class="reason-label">毁单理由:</div>
            <el-select
              v-model="cancelReason"
              placeholder="请选择毁单理由"
              class="reason-select"
              @change="handleReasonChange"
            >
              <el-option
                v-for="reason in cancelReasons"
                :key="reason.value"
                :label="reason.label"
                :value="reason.value"
              />
            </el-select>
          </div>

          <!-- 自定义理由输入 - 仅在免责毁单且选择自定义时显示 -->
          <div v-if="cancelReason === 'custom'" class="custom-reason-section">
            <div class="custom-reason-label">自定义理由:</div>
            <el-input
              v-model.trim="customReason"
              type="textarea"
              :rows="3"
              placeholder="请输入自定义毁单理由"
              maxlength="200"
              show-word-limit
            />
          </div>
        </div>
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel" :disabled="confirming">取消</el-button>
      <el-button type="danger" @click="handleConfirm" :loading="confirming" :disabled="confirming">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { cancelOrder } from '@/api/order'
export default {
  name: 'CancelOrderDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    orderData: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      applyType: 1, // 1: 立即毁单, 2: 免责毁单
      cancelReason: '',
      customReason: '',
      confirming: false, // 防重复提交标志
      cancelReasons: [
        { label: '订单抢错', value: '订单抢错' },
        { label: '机器二次销售', value: '机器二次销售' },
        { label: '串码型号不一致', value: '串码型号不一致' },
        { label: '地址不安全', value: '地址不安全' },
        { label: '自定义', value: 'custom' }
      ]
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
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm()
        this.$nextTick(() => {
          this.fixAccessibilityIssue()
        })
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.fixAccessibilityIssue()
    })
  },
  methods: {
    /**
     * 重置表单
     */
    resetForm() {
      this.applyType = 1
      this.cancelReason = ''
      this.customReason = ''
    },

    /**
     * 修复无障碍访问性问题
     */
    fixAccessibilityIssue() {
      // 查找所有 el-radio__original 元素并移除 aria-hidden 属性
      const radioInputs = this.$el.querySelectorAll('.el-radio__original')
      radioInputs.forEach(input => {
        input.removeAttribute('aria-hidden')
        input.setAttribute('tabindex', '-1')
        input.style.position = 'absolute'
        input.style.left = '-9999px'
        input.style.width = '0'
        input.style.height = '0'
        input.style.opacity = '0'
        input.style.pointerEvents = 'none'
      })
    },

    /**
     * 取消方式变化
     */
    handleapplyTypeChange(value) {
      console.log('取消方式变化:', value)
    },

    /**
     * 理由选择变化
     */
    handleReasonChange(value) {
      if (value !== 'custom') {
        this.customReason = ''
      }
    },

    /**
     * 取消
     */
    handleCancel() {
      this.$emit('update:visible', false)
      this.$emit('cancel')
    },

    /**
     * 确定
     */
    handleConfirm() {
      // 验证表单 - 只有免责毁单才需要验证理由
      if (this.applyType === 2) {
        if (!this.cancelReason) {
          this.$message.warning('请选择毁单理由')
          return
        }

        if (this.cancelReason === 'custom' && !this.customReason.trim()) {
          this.$message.warning('请输入自定义毁单理由')
          return
        }
      }

      // 构建提交数据
      const cancelData = {
        orderCode: this.orderData.orderCode,
        applyType: this.applyType,
        cancelReason: this.applyType === 1 ? '立即毁单' : (this.cancelReason === 'custom' ? this.customReason : this.cancelReason)
      }

      // 确认提示
      const confirmMessage = this.applyType === 1
        ? '确定要立即毁单吗？将扣除40元保证金！'
        : '确定要申请免责毁单吗？'

      this.$confirm(confirmMessage, '确认毁单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        this.confirming = true
        try {
          const res = await cancelOrder(cancelData)
          if (res && res.code === 200) {
            this.$message.success('毁单申请已提交')
            this.$emit('confirm', cancelData)
            this.$emit('update:visible', false)
          } else {
            this.$message.error(res?.msg || '毁单申请失败')
          }
        } finally {
          this.confirming = false
        }
      }).catch(() => {
        // 用户取消
      })
    },

    /**
     * 关闭弹窗
     */
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
.cancel-order-content {
  padding: 20px 0;
  max-height: 500px;
  overflow-y: auto;

  .cancellation-options {
    margin-bottom: 24px;

    .option-item {
      margin-bottom: 16px;
      padding: 16px;
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      transition: all 0.3s ease;
      background: #fff;

      // &:hover {
      //   border-color: #409EFF;
      //   background: rgba(64, 158, 255, 0.05);
      // }

      // &.active {
      //   border-color: #f56c6c;
      //   background: rgba(245, 108, 108, 0.05);
      // }

      .option-content {
        flex: 1;
        min-width: 0;
        margin-left: 12px;
        margin-top: 12px;

        .option-warning {
          color: #f56c6c;
          font-size: 14px;
          font-weight: 500;
          background: rgba(245, 108, 108, 0.1);
          padding: 8px 12px;
          border-radius: 6px;
          border-left: 3px solid #f56c6c;
          word-wrap: break-word;
        }

        .option-description {
          color: #606266;
          font-size: 13px;
          line-height: 1.6;
          background: rgba(64, 158, 255, 0.05);
          padding: 12px;
          border-radius: 6px;
          border-left: 3px solid #409EFF;
          word-wrap: break-word;
        }
      }
    }
  }

  .reason-section {
    margin-top: 20px;

    .reason-label {
      font-size: 14px;
      font-weight: 600;
      color: #2c3e50;
      margin-bottom: 8px;
    }

    .reason-select {
      width: 100%;

      ::v-deep .el-input__inner {
        border-radius: 6px;
        border: 2px solid #e4e7ed;
        transition: all 0.3s ease;

        &:focus {
          border-color: #409EFF;
          box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
        }
      }
    }
  }

  .custom-reason-section {
    margin-top: 20px;

    .custom-reason-label {
      font-size: 14px;
      font-weight: 600;
      color: #2c3e50;
      margin-bottom: 8px;
    }

    ::v-deep .el-textarea__inner {
      border-radius: 6px;
      border: 2px solid #e4e7ed;
      transition: all 0.3s ease;

      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }
  }
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;

  .el-button {
    margin-left: 12px;
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:first-child {
      margin-left: 0;
    }

    &.el-button--danger {
      background: #f56c6c;
      border-color: #f56c6c;

      &:hover {
        background: #f78989;
        border-color: #f78989;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(245, 108, 108, 0.3);
      }
    }
  }
}

// Radio按钮样式优化
::v-deep .el-radio {
  margin-right: 0;

  .el-radio__input {
    .el-radio__inner {
      border-color: #dcdfe6;
      transition: all 0.3s ease;
      width: 18px;
      height: 18px;

      &:hover {
        border-color: #409EFF;
      }
    }

    &.is-checked .el-radio__inner {
      background-color: #f56c6c;
      border-color: #f56c6c;

      &::after {
        background-color: #fff;
        width: 6px;
        height: 6px;
      }
    }
  }

  .el-radio__label {
    padding-left: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
  }

  // 修复无障碍访问性问题
  .el-radio__original {
    position: absolute !important;
    opacity: 0 !important;
    outline: none !important;
    z-index: -1 !important;
    width: 0 !important;
    height: 0 !important;
    margin: 0 !important;
    padding: 0 !important;
    border: none !important;
    background: none !important;
    pointer-events: none !important;
    visibility: hidden !important;
    clip: rect(0, 0, 0, 0) !important;
    clip-path: inset(50%) !important;
  }
}
</style>
