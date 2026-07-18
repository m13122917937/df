<template>
  <div class="serial-input-section">
    <div class="serial-input">
      <div class="serial-header">
        <span class="serial-label">串码: [{{ serialCodes.length }}/{{ currentOrder.quantity || 0 }}]</span>
        <div v-if="serialInputList.some(item => item.sn && item.sn.trim() || (isPhoneCategory && item.code86 && item.code86.trim()))" class="validation-summary">
          <span v-if="formatCorrectCount > 0" class="summary-item valid">✓ {{ formatCorrectCount }}个格式正确</span>
          <span v-if="validationSummary.invalid > 0" class="summary-item invalid">✗ {{ validationSummary.invalid }}个错误</span>
          <span v-if="validationSummary.empty > 0" class="summary-item empty">○ {{ validationSummary.empty }}个未填</span>
          <span v-if="validationSummary.duplicate > 0" class="summary-item duplicate">⚠ {{ validationSummary.duplicate }}个重复</span>
        </div>
        <div v-if="validationStatus !== 'pending' && hasValidationResults" class="validation-status" :class="getValidationStatusClass()">
          <i v-if="getValidationStatusIcon() === 'success'" class="el-icon-success" />
          <i v-else-if="getValidationStatusIcon() === 'error'" class="el-icon-error" />
          <i v-else-if="getValidationStatusIcon() === 'warning'" class="el-icon-warning" />
          <span>{{ validationStatusMessage }}</span>
        </div>
      </div>
      <div class="serial-buttons">
        <el-button
          size="mini"
          type="primary"
          :loading="validatingSerial"
          :class="{ 'validating': validatingSerial }"
          @click="$emit('validate-serial')"
        >
          <i v-if="!validatingSerial" class="el-icon-check" style="margin-right: 4px;" />
          {{ validatingSerial ? '验证中...请您耐心等待！' : '验证串码' }}
        </el-button>
        <el-button
          v-if="currentOrder.quantity >= 3"
          size="mini"
          @click="$emit('toggle-input')"
        >
          {{ serialInputExpanded ? '收起串码' : '展开串码' }}
        </el-button>
        <el-button size="mini" type="info" @click="showTips = true">
          格式要求
        </el-button>
        <!-- <el-button
          size="mini"
          type="warning"
          :disabled="!serialValidationPassed"
          @click="$emit('validate-express')"
        >
          <i class="el-icon-truck" style="margin-right: 4px;" />
          验证快递
        </el-button> -->
      </div>
    </div>

    <!-- 串码格式要求提示 -->
    <div v-if="showTips" class="serial-tips">
      <div class="tips-header">
        <i class="el-icon-info" />
        <span>串码格式要求</span>
        <i class="el-icon-close tips-close" @click="showTips = false" />
      </div>
      <div class="tips-content">
        <div class="tip-item">
          <strong>长度要求：</strong>串码长度必须在{{ imei.min }}-{{ imei.max }}位之间
        </div>
        <div class="tip-item">
          <strong>SN码字符要求：</strong>支持字母、数字、斜杠等特殊字符，且<strong>必须包含至少一个字母</strong>
        </div>
        <div v-if="isPhoneCategory" class="tip-item">
          <strong>86码格式要求：</strong>必须以"86"开头，共15位纯数字
        </div>
        <div class="tip-item">
          <strong>唯一性：</strong>每个SN码必须唯一，不能重复
        </div>
        <div class="tip-item">
          <strong>数量要求：</strong>必须输入{{ currentOrder.quantity }}个有效的SN码<span v-if="isPhoneCategory">和86码</span>
        </div>
        <div class="tip-item warning">
          <i class="el-icon-warning" />
          <strong>注意事项：</strong>请确保串码准确无误，错误串码可能导致订单异常
        </div>
      </div>
    </div>
    <div v-if="(currentOrder.quantity && currentOrder.quantity < 3) || serialInputExpanded" class="serial-inputs-container">
      <div
        v-for="(item, index) in serialInputList"
        :key="index"
        class="serial-input-item"
      >
        <div class="serial-input-fields">
          <div class="serial-input-field">
            <label class="serial-input-label">SN码 {{ index + 1 }}:</label>
            <div class="serial-input-wrapper">
              <el-input
                v-model.trim="serialInputList[index].sn"
                type="text"
                :placeholder="`请输入第${index + 1}个SN码`"
                :class="getCodeStyle(serialInputList[index].sn)"
                :maxlength="imei.max"
                :disabled="validatingSerial"
                @input="(value) => $emit('input-change', index, 'sn', value)"
                @keyup.enter.native="$emit('validate-serial')"
              />
            </div>
          </div>
          <div v-if="isPhoneCategory" class="serial-input-field">
            <label class="serial-input-label">86码 {{ index + 1 }}:</label>
            <div class="serial-input-wrapper">
              <el-input
                v-model.trim="serialInputList[index].code86"
                type="text"
                :placeholder="`请输入第${index + 1}个86码（以86开头15位数字）`"
                :maxlength="15"
                :disabled="validatingSerial"
                @input="(value) => $emit('input-change', index, 'code86', value)"
                @keyup.enter.native="$emit('validate-serial')"
              />
            </div>
          </div>
        </div>
        <div
          v-if="serialValidationErrors[index]"
          class="serial-error-message"
        >
          {{ serialValidationErrors[index] }}
        </div>
        <div
          v-else-if="getSerialValidationMessage(serialInputList[index].sn)"
          class="serial-validation-message"
          :class="getSerialValidationMessageClass(serialInputList[index].sn)"
        >
          {{ getSerialValidationMessage(serialInputList[index].sn) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SerialInputSection',
  props: {
    currentOrder: {
      type: Object,
      required: true
    },
    serialCodes: {
      type: Array,
      default: () => []
    },
    serialInputList: {
      type: Array,
      default: () => []
    },
    formatCorrectCount: {
      type: Number,
      default: 0
    },
    validationSummary: {
      type: Object,
      default: () => ({})
    },
    validationStatus: {
      type: String,
      default: 'pending'
    },
    hasValidationResults: {
      type: Boolean,
      default: false
    },
    validatingSerial: {
      type: Boolean,
      default: false
    },
    serialInputExpanded: {
      type: Boolean,
      default: false
    },
    initialShowTips: {
      type: Boolean,
      default: false
    },
    imei: {
      type: Object,
      default: () => ({})
    },
    serialValidationErrors: {
      type: Object,
      default: () => ({})
    },
    serialValidationResults: {
      type: Object,
      default: () => ({})
    },
    isPhoneCategory: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      showTips: false
    }
  },
  computed: {
    validationStatusMessage() {
      if (this.validationStatus === 'success') {
        return '验证通过'
      } else if (this.validationStatus === 'error') {
        return '验证失败'
      } else if (this.validationStatus === 'warning') {
        return '部分验证通过'
      }
      return ''
    }
  },
  watch: {
    initialShowTips: {
      handler(newVal) {
        this.showTips = newVal
      },
      immediate: true
    }
  },
  mounted() {
    // mounted 可以为空，因为 watch 会处理初始值
  },
  methods: {
    getValidationStatusClass() {
      return {
        'success': this.validationStatus === 'success',
        'error': this.validationStatus === 'error',
        'warning': this.validationStatus === 'warning'
      }
    },

    getValidationStatusIcon() {
      if (this.validationStatus === 'success') return 'success'
      if (this.validationStatus === 'error') return 'error'
      if (this.validationStatus === 'warning') return 'warning'
      return ''
    },

    getCodeStyle(code) {
      if (!code || !code.trim()) return ''

      const upperSn = code.toString().trim().toUpperCase()

      // 尝试使用复合 key `${SN}-${IMEL}` 优先查找（仅当该行有 86 码时才优先使用复合匹配）
      let result = null
      const item = (this.serialInputList || []).find(it => (it.sn || '').toString().trim().toUpperCase() === upperSn)
      if (item) {
        const im = (item.code86 || '').toString().trim().toUpperCase()
        if (im) {
          // 如果该行已有 86 码，则严格使用 composite key 去找，避免使用老的 SN-only 结果
          const compositeKey = `${upperSn}-${im}`
          result = this.serialValidationResults && (this.serialValidationResults[compositeKey])
        } else {
          // 如果该行没有 86 码，则允许回退到 SN-only 查找（保持兼容）
          result = this.serialValidationResults && (this.serialValidationResults[upperSn])
        }
      }

      if (!result) {
        // 如果没有后端验证结果，仅根据格式判断
        return this.isValidFormat(code) ? 'correct' : 'wrong'
      }

      // 使用后端返回的 numeric 状态判断
      const status = Number(result.activated)
      switch (status) {
        case 1:
          return 'activated'
        case 2:
        case 3:
          return 'model-mismatch'
        case 4:
          return 'verified'
        case 5:
          return 'validation-failed'
        default:
          return this.isValidFormat(code) ? 'correct' : 'wrong'
      }
    },

    isValidFormat(code) {
      if (!code || !code.trim()) return false

      const trimmed = code.trim()
      const length = trimmed.length

      // 检查长度
      if (length < this.imei.min || length > this.imei.max) {
        return false
      }

      // 检查是否包含字母
      if (!/[a-zA-Z]/.test(trimmed)) {
        return false
      }

      // 检查字符
      if (this.imei.test && this.imei.test.test(trimmed)) {
        return false
      }

      return true
    },

    getSerialValidationMessage(code) {
      if (!code || !code.trim()) return ''

      const upperSn = code.toString().trim().toUpperCase()

      // 优先按复合 key 查找（仅当该行有 86 码时使用复合匹配），否则回退到 SN 查找
      let result = null
      const item = (this.serialInputList || []).find(it => (it.sn || '').toString().trim().toUpperCase() === upperSn)
      if (item) {
        const im = (item.code86 || '').toString().trim().toUpperCase()
        if (im) {
          const compositeKey = `${upperSn}-${im}`
          result = this.serialValidationResults && (this.serialValidationResults[compositeKey])
        } else {
          result = this.serialValidationResults && (this.serialValidationResults[upperSn])
        }
      }
      if (!result) return ''

      // 如果后端已经提供了 message 字段，直接返回
      if (result.message) return result.message

      // 根据类目决定错误提示中的码类型描述
      const codeType = this.isPhoneCategory ? 'SN/86码' : 'SN码'
      const status = Number(result.activated)
      switch (status) {
        case 1:
          return `该串码${codeType}已被激活，请重新输入`
        case 2:
        case 3:
          return `该串码${codeType}型号不一致，请重新输入`
        case 4:
          return '验证通过'
        case 5:
          return `该串码${codeType}验证失败，请重新输入`
        default:
          return ''
      }
    },

    getSerialValidationMessageClass(code) {
      if (!code || !code.trim()) return ''

      const upperSn = code.toString().trim().toUpperCase()

      // 优先按复合 key 查找
      let result = null
      const item = (this.serialInputList || []).find(it => (it.sn || '').toString().trim().toUpperCase() === upperSn)
      if (item) {
        const im = (item.code86 || '').toString().trim().toUpperCase()
        const compositeKey = `${upperSn}-${im}`
        result = this.serialValidationResults && (this.serialValidationResults[compositeKey])
      }

      // 回退按 SN 查找
      if (!result) {
        result = this.serialValidationResults && (this.serialValidationResults[upperSn])
      }
      if (!result) return ''

      const status = Number(result.activated)
      if (status === 4) return 'success'
      if (status === 1 || status === 2 || status === 3 || status === 5) return 'error'
      return ''
    }
  }
}
</script>

<style lang="scss" scoped>
.serial-input-section {
  margin-bottom: 24px;
  background: #fff;
  border-radius: 12px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .serial-input {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .serial-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .serial-label {
        font-weight: 600;
        color: #2c3e50;
        font-size: 14px;
        background: rgba(64, 158, 255, 0.1);
        padding: 6px 12px;
        border-radius: 6px;
      }

      .validation-summary {
        display: flex;
        gap: 12px;
        align-items: center;

        .summary-item {
          font-size: 12px;
          padding: 4px 8px;
          border-radius: 4px;
          font-weight: 500;

          &.valid {
            background: rgba(103, 194, 58, 0.1);
            color: #67c23a;
          }

          &.invalid {
            background: rgba(245, 108, 108, 0.1);
            color: #f56c6c;
          }

          &.empty {
            background: rgba(144, 147, 153, 0.1);
            color: #909399;
          }

          &.duplicate {
            background: rgba(230, 162, 60, 0.1);
            color: #e6a23c;
          }
        }
      }

      .validation-status {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 6px;
        font-size: 13px;
        font-weight: 500;

        &.success {
          background: rgba(103, 194, 58, 0.1);
          color: #67c23a;
        }

        &.error {
          background: rgba(245, 108, 108, 0.1);
          color: #f56c6c;
        }

        &.warning {
          background: rgba(230, 162, 60, 0.1);
          color: #e6a23c;
        }

        i {
          font-size: 14px;
        }
      }
    }

    .serial-buttons {
      display: flex;
      gap: 8px;
      align-items: center;

      .el-button {
        border-radius: 6px;
        font-weight: 500;
        transition: all 0.3s ease;

        &.validating {
          animation: pulse 1.5s ease-in-out infinite;
        }
      }
    }
  }

  .serial-tips {
    margin-bottom: 16px;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border: 1px solid #0ea5e9;
    border-radius: 8px;
    overflow: hidden;

    .tips-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 16px;
      background: rgba(14, 165, 233, 0.1);
      border-bottom: 1px solid rgba(14, 165, 233, 0.2);

      i {
        color: #0ea5e9;
        font-size: 16px;
      }

      span {
        font-weight: 600;
        color: #0c4a6e;
        font-size: 14px;
      }

      .tips-close {
        cursor: pointer;
        color: #64748b;
        font-size: 16px;
        transition: all 0.3s ease;

        &:hover {
          color: #0ea5e9;
          transform: scale(1.1);
        }
      }
    }

    .tips-content {
      padding: 16px;

      .tip-item {
        margin-bottom: 8px;
        line-height: 1.5;
        color: #374151;
        font-size: 13px;

        &:last-child {
          margin-bottom: 0;
        }

        &.warning {
          display: flex;
          align-items: flex-start;
          gap: 6px;
          padding: 8px 12px;
          background: rgba(245, 158, 11, 0.1);
          border: 1px solid rgba(245, 158, 11, 0.3);
          border-radius: 4px;
          color: #d97706;

          i {
            color: #f59e0b;
            font-size: 14px;
            margin-top: 1px;
          }

          strong {
            color: #d97706;
          }
        }

        strong {
          color: #0ea5e9;
          font-weight: 600;
        }
      }
    }
  }

  .serial-inputs-container {
    .serial-input-item {
      margin-bottom: 12px;
      padding: 12px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      border: 1px solid #e4e7ed;

      .serial-input-fields {
        display: flex;
        gap: 16px;
        align-items: flex-start;

        .serial-input-field {
          flex: 1;

          &:only-child {
            flex: 1;
            max-width: 100%;
          }
        }
      }

      &:last-child {
        margin-bottom: 0;
      }

      .serial-input-label {
        font-weight: 600;
        color: #374151;
        font-size: 13px;
        min-width: 80px;
        margin-right: 12px;
      }

      .serial-input-wrapper {
        flex: 1;
        position: relative;

        ::v-deep .el-input__inner {
          border-radius: 6px;
          border: 2px solid #e4e7ed;
          transition: all 0.3s ease;
          height: 36px;

          &:focus {
            border-color: #409EFF;
            box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
          }

          &:disabled {
            background-color: #f5f7fa;
            border-color: #e4e7ed;
            color: #c0c4cc;
            cursor: not-allowed;
          }
        }

        .status-icon {
          position: absolute;
          right: 12px;
          top: 50%;
          transform: translateY(-50%);
          font-size: 14px;
          font-weight: bold;

          &.success {
            color: #67c23a;
          }

          &.error {
            color: #f56c6c;
          }

          &.warning {
            color: #e6a23c;
          }
        }
      }

      .serial-error-message {
        margin-left: 12px;
        color: #f56c6c;
        font-size: 14px;
        font-weight: 500;
      }

      .serial-validation-message {
        margin-left: 12px;
        font-size: 14px;
        font-weight: 500;

        &.success {
          color: #67c23a;
        }

        &.error {
          color: #f56c6c;
        }
      }
    }
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 4px 8px rgba(64, 158, 255, 0.4);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
  }
}
</style>
