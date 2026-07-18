<template>
  <el-dialog
    title="修改手机号 / 快递单号"
    :visible.sync="dialogVisible"
    width="520px"
    :close-on-click-modal="false"
    @closed="resetForm"
  >
    <el-form label-width="120px" label-position="right" class="logistics-edit-form">
      <el-form-item label="快递公司">
        <el-select
          v-model="form.logisticsCode"
          placeholder="请选择快递公司"
          style="width: 100%;"
          :loading="loadingCompanies"
          @change="handleCourierChange"
        >
          <el-option
            v-for="item in expressCompanyList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="快递单号">
        <el-input
          v-model.trim="form.logisticsNo"
          clearable
          placeholder="请输入快递单号"
          @blur="validateTrackingNumber"
        />
        <div v-if="errors.logisticsNo" class="form-error">{{ errors.logisticsNo }}</div>
      </el-form-item>

      <el-form-item label="寄件人手机号">
        <el-input
          v-model.trim="form.phone"
          clearable
          :placeholder="requiresPhone ? '请输入寄件人手机号（4位或11位）' : '请输入手机号（可选，4位或11位）'"
          maxlength="11"
          @blur="validatePhone"
        />
        <div v-if="errors.phone" class="form-error">{{ errors.phone }}</div>
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        保 存
      </el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getExpressCompany, updateOrderLogistics } from '@/api/order'

export default {
  name: 'LogisticsEditDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    order: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      form: {
        logisticsCode: '',
        logisticsNo: '',
        phone: ''
      },
      errors: {
        logisticsNo: '',
        phone: ''
      },
      expressCompanyList: [],
      submitting: false,
      loadingCompanies: false
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

    // 判断是否需要填写手机号（顺丰和中通）
    requiresPhone() {
      const code = this.normalizeCourierCode(this.form.logisticsCode)
      return code === 'sf' || code === 'zhongtong'
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        // 先初始化表单（设置回显数据）
        this.initForm()
        // 然后加载快递公司列表（异步，完成后会匹配回显的快递公司）
        this.loadExpressCompanies()
      }
    },
    order: {
      handler(newOrder, oldOrder) {
        if (this.dialogVisible) {
          // 如果 orderCode 变化了，需要重新加载快递公司列表
          if (newOrder && oldOrder && newOrder.orderCode !== oldOrder.orderCode) {
            this.initForm()
            this.loadExpressCompanies()
          } else {
            // 如果只是其他字段变化，只更新表单
            this.initForm()
          }
        }
      },
      deep: true
    }
  },
  methods: {
    initForm() {
      this.form = {
        logisticsCode: this.order.logisticsCode || '',
        logisticsNo: this.order.logisticsNo || '',
        phone: this.order.phone || ''
      }
      this.errors = {
        logisticsNo: '',
        phone: ''
      }

      // 如果回显了运单号，格式化但不显示错误
      if (this.form.logisticsNo) {
        const waybillValidation = this.getExpressNo(this.form.logisticsNo, this.form.logisticsCode)
        this.form.logisticsNo = waybillValidation.formatted
        // 如果格式不正确，清除错误（回显时不显示错误）
        if (!waybillValidation.valid) {
          this.errors.logisticsNo = ''
        }
      }

      // 如果回显了手机号，格式化并验证（确保格式正确）
      if (this.form.phone) {
        const phoneValidation = this.phoneLimit(this.form.phone, this.form.logisticsCode)
        this.form.phone = phoneValidation.formatted
        // 如果格式不正确，清除错误（回显时不显示错误）
        if (!phoneValidation.valid) {
          this.errors.phone = ''
        }
      }
    },
    resetForm() {
      this.form = {
        logisticsCode: '',
        logisticsNo: '',
        phone: ''
      }
      this.errors = {
        logisticsNo: '',
        phone: ''
      }
      this.expressCompanyList = []
    },
    async loadExpressCompanies() {
      if (!this.order || !this.order.orderCode) return
      this.loadingCompanies = true
      try {
        const res = await getExpressCompany({ orderCode: this.order.orderCode })
        if (res && res.code === 200 && res.data) {
          this.expressCompanyList = Object.entries(res.data).map(([code, name]) => ({
            code,
            name
          }))

          // 如果已有回显的快递公司代码，尝试在列表中匹配
          if (this.form.logisticsCode) {
            const normalized = this.normalizeCourierCode(this.form.logisticsCode)
            const matched = this.expressCompanyList.find(item =>
              this.normalizeCourierCode(item.code) === normalized
            )
            if (matched) {
              // 找到匹配的，使用列表中的代码（确保代码格式一致）
              this.form.logisticsCode = matched.code
            } else {
              // 没有匹配的，如果回显的代码不在列表中，清空它
              // 这样用户需要重新选择
              this.form.logisticsCode = ''
            }
          }

          // 如果没有快递公司代码，设置默认值
          if (!this.form.logisticsCode && this.expressCompanyList.length > 0) {
            this.form.logisticsCode = this.expressCompanyList[0].code
          }

          // 如果快递公司代码已设置且手机号已回显，重新验证手机号格式
          if (this.form.logisticsCode && this.form.phone) {
            this.$nextTick(() => {
              this.validatePhone()
            })
          }
        }
      } catch (error) {
        console.error('获取快递公司列表失败', error)
        this.$message.error('获取快递公司列表失败，请稍后重试')
      } finally {
        this.loadingCompanies = false
      }
    },
    handleCourierChange() {
      // 重新验证运单号和手机号
      this.validateTrackingNumber()
      this.validatePhone()
    },
    validateTrackingNumber() {
      // 如果运单号为空，不显示错误（允许用户先选择快递公司）
      if (!this.form.logisticsNo || !this.form.logisticsNo.trim()) {
        this.errors.logisticsNo = ''
        return false
      }
      const { valid, message, formatted } = this.getExpressNo(
        this.form.logisticsNo,
        this.form.logisticsCode
      )
      this.errors.logisticsNo = valid ? '' : message
      if (valid) {
        this.form.logisticsNo = formatted
      }
      return valid
    },
    validatePhone() {
      const courier = this.form.logisticsCode
      const validation = this.phoneLimit(this.form.phone, courier)

      // 更新手机号（所有快递都支持输入）
      this.form.phone = validation.formatted

      // 根据验证结果更新错误信息
      if (validation.valid) {
        // 验证通过，清除错误
        this.errors.phone = ''
      } else {
        // 验证失败，设置错误信息
        this.errors.phone = validation.message
      }

      return validation.valid
    },

    /**
     * 手机号限制 - phoneLimit()
     * @param {string} phone - 手机号（4位或11位）
     * @param {string} courier - 快递公司代码
     * @returns {object} 验证结果
     */
    phoneLimit(phone, courier) {
      const result = {
        valid: true,
        message: '',
        formatted: phone
      }

      const normalizedCourier = this.normalizeCourierCode(courier)
      const requiresPhone = normalizedCourier === 'sf' || normalizedCourier === 'zhongtong'

      // 如果输入了手机号，进行格式验证
      if (phone && phone.trim()) {
        // 只保留数字
        const formatted = phone.replace(/[^0-9]/g, '')

        if (formatted.length === 11) {
          // 输入11位，验证是否为有效的手机号格式
          const mobileRegex = /^1[3-9]\d{9}$/
          if (!mobileRegex.test(formatted)) {
            result.valid = false
            result.message = '请输入有效的11位手机号'
          } else {
            result.formatted = formatted
          }
        } else if (formatted.length === 4) {
          // 输入4位，只验证是否为4位数字
          result.formatted = formatted
        } else if (formatted.length > 0) {
          // 输入了其他长度，报错
          result.valid = false
          result.message = '手机号必须为4位或11位数字'
        } else {
          // 输入了但格式化后为空（全是非数字字符）
          result.valid = false
          result.message = '请输入有效的手机号（4位或11位数字）'
        }
      } else if (requiresPhone) {
        // 必填但未输入
        const courierName = this.getCourierName(courier)
        return {
          valid: false,
          message: `${courierName}需要填写寄件人手机号（4位或11位）`,
          formatted: ''
        }
      }

      return result
    },

    /**
     * 归一化快递公司编码，便于比较
     * @param {string} code - 快递公司编码
     * @returns {string} 归一化后的编码
     */
    normalizeCourierCode(code) {
      if (code === undefined || code === null) return ''
      const normalized = String(code).trim().toLowerCase()
      if (['sf', 'shunfeng', 'shunfengkuaiyun', 'shunfengkuaidi'].includes(normalized)) {
        return 'sf'
      }
      if (['zt', 'zhongtong', 'zhongtongkuaidi'].includes(normalized)) {
        return 'zhongtong'
      }
      return normalized
    },

    /**
     * 获取快递公司名称
     * @param {string} code - 快递公司代码
     * @returns {string} 快递公司名称
     */
    getCourierName(code) {
      if (!code) return '该快递公司'
      const normalized = this.normalizeCourierCode(code)
      if (normalized === 'sf') {
        return '顺丰快递'
      }
      if (normalized === 'zhongtong') {
        return '中通快递'
      }
      // 从快递列表中查找
      const courier = this.expressCompanyList.find(item => this.normalizeCourierCode(item.code) === normalized)
      return courier ? courier.name : '该快递公司'
    },
    getExpressNo(waybillNumber, courier) {
      const result = {
        valid: true,
        message: '',
        formatted: waybillNumber
      }
      if (!waybillNumber || !waybillNumber.trim()) {
        return {
          valid: false,
          message: '请输入运单号',
          formatted: ''
        }
      }
      const formatted = waybillNumber.toUpperCase().replace(/[^A-Z0-9]/g, '')

      // 通用长度检查：最小6位，最大32位
      if (formatted.length < 6) {
        return {
          valid: false,
          message: '运单号长度不能少于6位',
          formatted
        }
      }
      if (formatted.length > 32) {
        return {
          valid: false,
          message: '运单号长度不能超过32位',
          formatted
        }
      }

      // 使用归一化方法处理快递公司代码
      const normalizedCode = this.normalizeCourierCode(courier)
      const isSf = normalizedCode === 'sf'

      // 检查非顺丰快递不能使用SF开头的单号
      if (!isSf && formatted.startsWith('SF')) {
        return {
          valid: false,
          message: '请选择顺丰或更换其他快递单号',
          formatted
        }
      }

      // 根据归一化后的快递公司代码验证
      switch (normalizedCode) {
        case 'sf':
          if (formatted.includes('SF')) {
            if (formatted.length !== 15) {
              result.valid = false
              result.message = '顺丰SF单号必须为15位'
            }
          } else if (formatted.length !== 12) {
            result.valid = false
            result.message = '顺丰单号必须为12位'
          }
          break
        case 'debangwuliu':
          if (formatted.length !== 15) {
            result.valid = false
            result.message = '德邦快递单号必须为15位'
          }
          break
        default:
          // 其他快递公司：已通过通用长度检查（6-32位），无需额外检查
          break
      }
      result.formatted = formatted
      return result
    },
    async handleSubmit() {
      if (!this.order || !this.order.orderCode) {
        this.$message.warning('订单信息缺失')
        return
      }
      if (!this.form.logisticsCode) {
        this.$message.warning('请选择快递公司')
        return
      }

      // 验证运单号（提交时必须填写）
      if (!this.form.logisticsNo || !this.form.logisticsNo.trim()) {
        this.$message.warning('请输入运单号')
        this.errors.logisticsNo = '请输入运单号'
        return
      }

      const trackingValid = this.validateTrackingNumber()
      const phoneValid = this.validatePhone()
      if (!trackingValid || !phoneValid) {
        // 如果验证失败，显示第一个错误
        if (!trackingValid) {
          // 如果运单号验证失败，显示错误（可能是格式错误）
          if (this.errors.logisticsNo) {
            this.$message.warning(this.errors.logisticsNo)
          } else {
            // 如果运单号为空，这个错误应该已经在前面检查过了
            // 但为了保险，再次提示
            this.$message.warning('请输入运单号')
          }
        } else if (!phoneValid && this.errors.phone) {
          this.$message.warning(this.errors.phone)
        }
        return
      }

      const payload = {
        orderCode: this.order.orderCode,
        logisticsCode: this.form.logisticsCode,
        logisticsNo: this.form.logisticsNo,
        phone: this.form.phone || ''
      }
      this.submitting = true
      try {
        const res = await updateOrderLogistics(payload)
        if (res && res.code === 200) {
          this.$message.success('修改成功')
          this.$emit('success', payload)
          this.dialogVisible = false
        } else {
          this.$message.error(res?.msg || '修改失败')
        }
      } catch (error) {
        console.error('修改物流信息失败', error)
        this.$message.error(error?.response?.data?.msg || error?.message || '修改失败，请稍后重试')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.logistics-edit-form {
  .form-error {
    color: #f56c6c;
    font-size: 12px;
    margin-top: 4px;
  }
}
</style>

