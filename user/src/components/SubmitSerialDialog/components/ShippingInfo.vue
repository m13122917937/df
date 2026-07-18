<template>
  <div class="shipping-info">
    <el-row :gutter="6">
      <el-col :span="12">
        <div class="form-item">
          <div class="shipping-label">发货数量(台):</div>
          <el-input v-model="shippingForm.quantity" placeholder="1" disabled trim />
        </div>
      </el-col>
      <el-col :span="12">
        <div class="form-item">
          <div class="shipping-label">快递公司:</div>
          <el-select v-model="shippingForm.courier" placeholder="顺丰" @change="handleCourierChange">
            <el-option v-for="item in shippingForm.courierList" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="6" style="margin-top: 16px;">
      <el-col :span="12">
        <div class="form-item">
          <div class="shipping-label small-label">运单号:</div>
          <el-input
            v-model.trim="shippingForm.waybillNumber"
            :placeholder="serialValidationPassed ? '请输入运单号' : '请先验证串码通过'"
            :disabled="!serialValidationPassed"
            clearable
            :class="{ 'disabled-input': !serialValidationPassed }"
            @input="(value) => handleWaybillNumberChange(value)"
            @blur="(event) => handleWaybillNumberChange(event.target.value)"
            @keyup.enter.native="$emit('enter-save')"
          />
        </div>
      </el-col>
      <el-col v-if="requiresPhone" :span="12">
        <div class="form-item">
          <div class="shipping-label big-label">寄件人手机号:</div>
          <el-input
            v-model.trim="shippingForm.senderPhone"
            placeholder="请输入寄件人手机号（4位或11位）"
            :disabled="!serialValidationPassed"
            maxlength="11"
            clearable
            style="margin-right: 15px;"
            @input="(value) => handleSenderPhoneChange(value, false)"
            @blur="(event) => handleSenderPhoneChange(event.target.value, true)"
            @keyup.enter.native="$emit('enter-save')"
          >
            <el-popover
              slot="suffix"
              placement="top"
              width="260"
              trigger="hover"
              popper-class="sender-phone-popover"
            >
              <div class="popover-content">
                <div class="popover-item">
                  <strong>1、</strong>可以输入4位或11位手机号
                </div>
                <div class="popover-item">
                  <strong>2、</strong>输入11位时，需要填写完整的有效手机号
                </div>
                <div class="popover-item">
                  <strong>3、</strong>输入4位时，填写手机号的后4位数字
                </div>
              </div>
              <i slot="reference" class="el-icon-question help-icon" />
            </el-popover>
          </el-input>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'ShippingInfo',
  props: {
    shippingForm: {
      type: Object,
      required: true
    },
    serialValidationPassed: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      expressValidation: {
        errors: {}
      }
    }
  },
  computed: {
    requiresPhone() {
      const code = this.normalizeCourierCode(this.shippingForm.courier)
      return code === 'sf' || code === 'shunfeng' || code === 'zhongtong'
    }
  },
  methods: {
    normalizeCourierCode(courier) {
      if (!courier) return ''
      const normalized = courier.toLowerCase()
      // 特殊处理顺丰的各种编码
      if (normalized.includes('shunfeng') || normalized === 'sf') {
        return 'sf'
      }
      return normalized
    },

    handleWaybillNumberChange(value) {
      const courier = this.shippingForm.courier
      const validation = this.getExpressNo(value, courier)

      // 更新运单号
      this.shippingForm.waybillNumber = validation.formatted

      // 更新验证错误
      if (validation.valid) {
        this.$delete(this.expressValidation.errors, 'waybillNumber')
      } else {
        this.$set(this.expressValidation.errors, 'waybillNumber', validation.message)
      }

      this.$emit('waybill-change', validation)
    },

    handleSenderPhoneChange(value, isBlur = false) {
      const courier = this.shippingForm.courier
      const validation = this.phoneLimit(value, courier)

      if (!isBlur) {
        // 输入过程中不覆盖用户输入的值且不显示错误（避免阻止输入）
        this.shippingForm.senderPhone = value
        // 发出当前输入供父组件或其他逻辑使用（不包含格式化结果）
        this.$emit('sender-phone-change', { valid: true, formatted: value })
        return
      }

      // Blur 时使用格式化结果并显示/清除错误
      this.shippingForm.senderPhone = validation.formatted

      if (validation.valid) {
        this.$delete(this.expressValidation.errors, 'senderPhone')
      } else {
        this.$set(this.expressValidation.errors, 'senderPhone', validation.message)
      }

      this.$emit('sender-phone-change', validation)
    },

    handleCourierChange(courier) {
      // 注意：此时 this.shippingForm.courier 已经是新值了
      // 清空手机号的逻辑已经在 watch 'shippingForm.courier' 中处理

      // 重新验证运单号
      this.handleWaybillNumberChange(this.shippingForm.waybillNumber)

      // 如果新快递需要手机号且已输入，重新验证手机号
      const newRequiresPhone = courier && (this.normalizeCourierCode(courier) === 'sf' || this.normalizeCourierCode(courier) === 'zhongtong')
      if (newRequiresPhone && this.shippingForm.senderPhone) {
        this.handleSenderPhoneChange(this.shippingForm.senderPhone)
      }

      this.$emit('courier-change', courier)
    },

    // 快递单号验证和格式化
    getExpressNo(expressNo, courier) {
      if (!expressNo || !expressNo.trim()) {
        return {
          valid: false,
          message: '请输入运单号',
          formatted: ''
        }
      }

      // 格式化：先去除前后空格，再只保留字母和数字
      const trimmed = expressNo.trim().replace(/[^A-Za-z0-9]/g, '')

      // 根据快递公司进行不同的验证
      const normalizedCourier = this.normalizeCourierCode(courier)

      switch (normalizedCourier) {
        case 'sf':
        case 'shunfeng':
          // 顺丰单号：SF开头15位（SF+13位数字） 或 纯12位数字
          if (/^SF/i.test(trimmed)) {
            // SF开头的单号，必须是15位（SF + 13位数字）
            if (!/^SF\d{13}$/i.test(trimmed)) {
              return {
                valid: false,
                message: '顺丰SF单号必须为15位（SF+13位数字）',
                formatted: trimmed
              }
            }
          } else {
            // 非SF开头的单号，必须是纯12位数字
            if (!/^\d{12}$/.test(trimmed)) {
              return {
                valid: false,
                message: '顺丰非SF单号必须为12位数字',
                formatted: trimmed
              }
            }
          }
          break
        case 'zhongtong':
          // 中通单号：12位或13位数字
          if (!/^\d{12,13}$/.test(trimmed)) {
            return {
              valid: false,
              message: '中通运单号必须是12-13位数字',
              formatted: trimmed
            }
          }
          break
        default:
          // 其他快递：至少6位，最多20位，可包含字母数字
          if (trimmed.length < 6 || trimmed.length > 20 || !/^[A-Za-z0-9]+$/.test(trimmed)) {
            return {
              valid: false,
              message: '运单号长度6-20位，只能包含字母和数字',
              formatted: trimmed
            }
          }
      }

      return {
        valid: true,
        message: '',
        formatted: trimmed
      }
    },

    // 手机号验证和格式化
    phoneLimit(phone, courier) {
      const result = {
        valid: true,
        message: '',
        formatted: ''
      }

      // 检查是否为空
      if (!phone || !phone.trim()) {
        const normalizedCourier = this.normalizeCourierCode(courier)
        const requiresPhone = normalizedCourier === 'sf' || normalizedCourier === 'zhongtong'

        if (requiresPhone) {
          // 必填但未输入
          const courierName = this.getCourierName(courier)
          return {
            valid: false,
            message: `${courierName}需要填写寄件人手机号（4位或11位）`,
            formatted: ''
          }
        }

        // 非必填且未输入，验证通过
        return result
      }

      // 只保留数字
      const formatted = phone.replace(/[^0-9]/g, '')

      if (formatted.length === 11) {
        // 输入11位，验证是否为有效的手机号格式
        const mobileRegex = /^1[3-9]\d{9}$/
        if (!mobileRegex.test(formatted)) {
          return {
            valid: false,
            message: '请输入有效的11位手机号',
            formatted
          }
        }
        result.formatted = formatted
      } else if (formatted.length === 4) {
        // 输入4位，验证是否为纯数字
        if (!/^\d{4}$/.test(formatted)) {
          return {
            valid: false,
            message: '4位手机号必须是纯数字',
            formatted
          }
        }
        result.formatted = formatted
      } else if (formatted.length > 0) {
        // 输入了手机号但长度不符合要求
        return {
          valid: false,
          message: '手机号必须为4位或11位数字',
          formatted: ''
        }
      }

      return result
    },

    getCourierName(courier) {
      if (!courier) return '该快递'
      const item = this.shippingForm.courierList.find(item => item.code === courier)
      return item ? item.name : '该快递'
    }
  }
}
</script>

<style lang="scss" scoped>
.shipping-info {
  margin-bottom: 24px;
  background: #fff;
  border-radius: 12px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .form-item {
    display: flex;
    .shipping-label {
      font-weight: 600;
      color: #2c3e50;
      font-size: 14px;
      display: flex;
      align-items: center;
      width: 130px;
      justify-content: flex-end;
    }
    .small-label {
      width: 80px;
    }
    .big-label {
      width: 200px;
    }

    ::v-deep .el-input__inner,
    ::v-deep .el-select .el-input__inner {
      margin-left: 10px;
      border-radius: 8px;
      border: 2px solid #e4e7ed;
      transition: all 0.3s ease;
      height: 40px;

      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }

    ::v-deep .el-input-group__append {
      border-radius: 0 8px 8px 0;
      border: 2px solid #e4e7ed;
      border-left: none;
      background: #f5f7fa;
      color: #606266;
      font-weight: 500;
    }

    ::v-deep .el-input__suffix {
      .help-icon {
        color: #909399;
        cursor: help;
        transition: all 0.3s ease;
        font-size: 16px;
        line-height: 40px;

        &:hover {
          color: #409EFF;
          transform: scale(1.1);
        }
      }
    }

    // 禁用状态的运单号输入框样式
    .disabled-input {
      .el-input__inner {
        background-color: #f5f7fa;
        border-color: #e4e7ed;
        color: #c0c4cc;
        cursor: not-allowed;
      }
    }
  }
}

// Popover样式
::v-deep .sender-phone-popover {
  .popover-content {
    padding: 8px 0;

    .popover-item {
      margin: 8px 0;
      line-height: 1.5;
      color: #606266;
      font-size: 13px;

      &:first-child {
        margin-top: 0;
      }

      &:last-child {
        margin-bottom: 0;
      }

      strong {
        color: #409EFF;
        font-weight: 600;
      }
    }
  }
}
</style>
