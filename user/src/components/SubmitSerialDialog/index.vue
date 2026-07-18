<!-- 提交串码弹窗组件 -->
<template>
  <el-dialog
    title="提交串码"
    :visible.sync="dialogVisible"
    width="800px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="submit-serial-content">
      <!-- 产品信息 -->
      <ProductInfo :current-order="currentOrder" />

      <!-- 串码输入 -->
      <SerialInputSection
        :current-order="currentOrder"
        :serial-codes="serialCodes"
        :serial-input-list="serialInputList"
        :format-correct-count="formatCorrectCount"
        :validation-summary="validationSummary"
        :validation-status="validationStatus"
        :has-validation-results="hasValidationResults"
        :validating-serial="validatingSerial"
        :serial-input-expanded="serialInputExpanded"
        :initial-show-tips="showTips"
        :imei="imei"
        :serial-validation-errors="serialValidationErrors"
        :serial-validation-results="serialValidationResults"
        :is-phone-category="isPhoneCategory"
        @validate-serial="handleValidateSerial"
        @toggle-input="toggleSerialInput"
        @input-change="handleSerialInputChange"
      />

      <!-- 发货信息 -->
      <ShippingInfo
        :shipping-form="shippingForm"
        :serial-validation-passed="serialValidationPassed"
        @waybill-change="handleWaybillValidation"
        @sender-phone-change="handleSenderPhoneValidation"
        @courier-change="handleCourierChange"
        @enter-save="handleSave"
      />

      <!-- 提醒信息 -->
      <ReminderSection />
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button :disabled="saving" type="danger" @click="handleCancel">取消</el-button>
      <el-button :loading="saving" :disabled="saving" type="success" @click="handleSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { imeiSave, getExpressCompany, saveOrderLogistics, getOrderImei, getExpressCompanyInfoShow } from '@/api/order'

// 快递号/手机号验证规则配置（集中维护）
const COURIER_RULES = {
  sf: {
    name: '顺丰快递',
    validateWaybill(formatted) {
      // 顺丰：SF 开头 15 或 非 SF 12
      if (formatted.startsWith('SF')) {
        return formatted.length === 15
      }
      return formatted.length === 12
    }
  },
  debangwuliu: {
    name: '德邦快递',
    validateWaybill(formatted) {
      return formatted.length === 15
    }
  },
  zhongtong: {
    name: '中通快递',
    validateWaybill(formatted) {
      return formatted.length >= 12 && formatted.length <= 13
    }
  }
}
import ProductInfo from './components/ProductInfo.vue'
import SerialInputSection from './components/SerialInputSection.vue'
import ShippingInfo from './components/ShippingInfo.vue'
import ReminderSection from './components/ReminderSection.vue'

export default {
  name: 'SubmitSerialDialog',
  components: {
    ProductInfo,
    SerialInputSection,
    ShippingInfo,
    ReminderSection
  },
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
      currentOrder: {},
      serialCodes: [],
      serialCodesText: '',
      serialInputList: [],
      serialInputExpanded: false,
      validatingSerial: false,
      saving: false, // 保存loading状态
      shippingForm: {
        quantity: '1',
        courier: '',
        courierList: [],
        waybillNumber: '',
        senderPhone: ''
      },
      // 串码验证相关数据
      imei: {
        min: 10,
        max: 20,
        test: null,
        handle: null,
        resTest: null,
        resWrong: '串码格式不符合要求，请检查输入'
      },
      repeatCode: [], // 重复串码数组
      serialValidationErrors: {}, // 串码验证错误信息
      serialValidationResults: {}, // 串码验证结果，存储每个串码的activated状态
      validationSummary: {
        total: 0,
        valid: 0,
        invalid: 0,
        empty: 0,
        duplicate: 0
      }, // 验证结果汇总
      showTips: false, // 是否显示格式要求提示
      validationStatus: 'pending', // 验证状态: pending, success, error
      imeiSaveValidated: false, // 是否已经通过 imeiSave API 验证
      historyValidationPassed: false, // 回显的历史验证是否已通过
      // 快递验证相关数据
      expressValidation: {
        errors: {}, // 快递验证错误信息
        waybillNumber: '', // 运单号
        senderPhone: '' // 寄件人手机号后4位
      },
      historyValidatedCodes: [] // 历史验证通过的串码集合（大写）
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

    // 判断是否为手机类目
    isPhoneCategory() {
      const category = (this.currentOrder.category || '').toString().trim()
      // 判断是否包含"手机"关键字（可根据实际情况调整判断逻辑）
      return category.includes('手机')
    },

    // 实时计算格式正确的串码数量（同时有正确SN码和86码的条目数）
    formatCorrectCount() {
      return this.serialInputList.reduce((count, item, index) => {
        if (!item) return count
        const snCode = (item.sn || '').trim()
        const code86 = (item.code86 || '').trim()

        // 检查SN码
        if (!snCode) return count
        if (!this.isCorrectLength(snCode)) return count
        if (this.imei.resTest && !this.imei.resTest.test(snCode)) return count

        // 如果是手机类目，需要检查86码
        if (this.isPhoneCategory) {
          if (!code86) return count
          if (!code86.startsWith('86') || code86.length !== 15 || !/^\d+$/.test(code86)) return count
        }

        // 检查是否有错误
        const error = this.serialValidationErrors[index]
        if (error && (error.includes('格式') || error.includes('长度') || error.includes('86码'))) return count

        return count + 1
      }, 0)
    },

    // 判断串码验证是否通过
    serialValidationPassed() {
      // 检查是否有足够的串码
      const validCodes = this.serialInputList.reduce((arr, item, index) => {
        if (!item || !item.sn) return arr
        const trimmed = (item.sn || '').trim()
        if (!trimmed) return arr
        if (!this.isCorrectLength(trimmed)) return arr
        if (this.imei.resTest && !this.imei.resTest.test(trimmed)) return arr
        const error = this.serialValidationErrors[index]
        if (error && (error.includes('格式') || error.includes('长度'))) return arr
        arr.push(trimmed)
        return arr
      }, [])

      // 检查串码数量是否足够
      const requiredQty = Number(this.currentOrder.quantity) || 0
      if (validCodes.length !== requiredQty) {
        return false
      }

      // 检查是否有格式错误
      if (Object.keys(this.serialValidationErrors).length > 0) {
        return false
      }

      // 检查验证状态：
      // 1. 如果 imeiSaveValidated 为 true，必须 validationStatus 为 'success'
      // 2. 如果 historyValidationPassed 为 true，必须 historyValidationConsistent() 为 true
      if (this.imeiSaveValidated) {
        return this.validationStatus === 'success'
      }
      if (this.historyValidationPassed) {
        return this.historyValidationConsistent()
      }

      // 没有任何验证状态，返回 false
      return false
    },

    // 判断是否需要填写手机号（顺丰和中通）

    // 判断是否有验证结果
    hasValidationResults() {
      return Object.keys(this.serialValidationResults).length > 0
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.initializeDialog()
      }
    },
    'currentOrder.quantity': {
      handler(newVal, oldVal) {
        // 只有在quantity真正变化时才重新初始化，避免初始化时执行
        if (newVal && newVal !== oldVal) {
          console.log('quantity变化，重新初始化串码输入框:', newVal)
          this.initSerialInputs()
          this.shippingForm.quantity = String(newVal)
        }
      }
    },
    // 监听快递公司变化，重新验证手机号（如果已输入）
    'shippingForm.courier': {
      handler(newCourier, oldCourier) {
        // 检查是否从必填快递切换到无需填快递
        const oldRequiresPhone = oldCourier && (this.normalizeCourierCode(oldCourier) === 'sf' || this.normalizeCourierCode(oldCourier) === 'zhongtong')
        const newRequiresPhone = newCourier && (this.normalizeCourierCode(newCourier) === 'sf' || this.normalizeCourierCode(newCourier) === 'zhongtong')

        // 如果从必填切换到无需填，清空手机号
        if (oldRequiresPhone && !newRequiresPhone) {
          this.shippingForm.senderPhone = ''
          this.$delete(this.expressValidation.errors, 'senderPhone')
        } else if (newRequiresPhone && this.shippingForm.senderPhone) {
          // 如果切换到必填快递且已输入手机号，重新验证格式
          const validation = this.phoneLimit(this.shippingForm.senderPhone, newCourier)
          this.handleSenderPhoneValidation(validation)
        }
      }
    }
  },
  // eslint-disable-next-line vue/return-in-computed-property
  mounted() {
    // 添加全局回车键监听
    document.addEventListener('keydown', this.handleGlobalKeydown)
  },
  // eslint-disable-next-line vue/return-in-computed-property
  beforeDestroy() {
    // 移除全局回车键监听
    document.removeEventListener('keydown', this.handleGlobalKeydown)
  },
  methods: {
    /**
     * 处理全局键盘事件
     */
    handleGlobalKeydown(event) {
      // 只在弹窗打开时处理
      if (!this.dialogVisible) {
        return
      }

      // 只处理回车键，且忽略按住键盘重复触发
      if (event.key !== 'Enter' || event.repeat) {
        return
      }

      console.log('全局回车事件触发', event)

      // 检查当前聚焦的元素类型和上下文
      const target = event.target
      const isSerialInput = this.isSerialInputElement(target)
      const isShippingInput = this.isShippingInputElement(target)

      console.log('isSerialInput:', !!isSerialInput, 'isShippingInput:', !!isShippingInput)

      // 在串码输入框中按回车：不阻止默认行为，让组件自己处理验证
      if (isSerialInput) {
        console.log('在串码输入框中按回车，跳过弹窗保存')
        return
      }

      // 在发货信息输入框中按回车：阻止默认行为，不触发保存
      if (isShippingInput) {
        console.log('在发货信息输入框中按回车，阻止默认保存行为')
        event.preventDefault()
        return
      }

      // 在 el-select 下拉菜单或 el-button 上按回车：由这些组件自行处理，不触发额外保存
      if (target.closest('.el-select-dropdown') || target.closest('.el-button')) {
        console.log('在下拉菜单或按钮上按回车，跳过弹窗保存')
        return
      }

      // 在其他区域按回车：触发保存
      console.log('在其他区域按回车，触发保存')
      event.preventDefault()
      this.handleSave()
    },

    /**
     * 检查元素是否为串码输入框
     */
    isSerialInputElement(target) {
      if (!target) return false

      // 通过DOM层级和CSS类名识别串码输入框
      // 检查是否在 .serial-input-wrapper 内
      const wrapper = target.closest('.serial-input-wrapper')
      if (wrapper) return true

      // 备用检查：通过输入框的maxlength属性（串码输入框有maxlength限制）
      const input = target.closest('input, textarea')
      if (input && input.maxLength && parseInt(input.maxLength) > 10) {
        // 串码最大长度通常大于10，而其他输入框较短
        return true
      }

      return false
    },

    /**
     * 检查元素是否为发货信息输入框
     */
    isShippingInputElement(target) {
      if (!target) return false

      // 通过DOM层级识别发货信息输入框
      // 检查是否在 ShippingInfo 组件区域内
      const shippingInfo = target.closest('.shipping-info')
      if (shippingInfo) {
        const input = target.closest('input, textarea')
        return !!input // 只要是在shipping-info区域内的输入框
      }

      return false
    },

    /**
     * 获取订单串码,进行回显
     */
    async getOrderImei() {
      try {
        const res = await getOrderImei(this.currentOrder.orderCode)
        if (res.code === 200 && res.data && Array.isArray(res.data)) {
          console.log('获取到的串码数据:', res.data)

          // 处理验证结果，res.data是数组，包含每个串码的验证状态
          // 回显时不显示toast消息
          this.handleValidationResult(res.data, false)

          // 回显串码到输入框
          const imeiList = res.data.map(item => ({
            sn: item.sn || '',
            code86: item.imel || ''
          }))
          // 如果API返回的数据不为空，则使用API数据；否则保持已初始化的输入框
          if (imeiList.length > 0) {
            this.serialInputList = imeiList
            console.log('使用API返回的串码数据:', imeiList)
            // 如果有串码数据，自动展开输入区域
            this.serialInputExpanded = true
          } else {
            console.log('API返回空数据，保持已初始化的输入框')
          }

          // 更新serialCodes数组用于验证（使用sn字段）
          this.serialCodes = this.serialInputList
            .map(item => (item && item.sn ? item.sn : ''))
            .filter(code => code && code.trim())

          // 更新验证汇总
          this.updateValidationSummary()

          console.log('串码回显完成:', this.serialInputList)
        } else {
          console.warn('未获取到串码数据或数据格式不正确:', res)
          // 确保有基本的输入框可用
          this.ensureSerialInputsInitialized()
        }
        return true // 成功标识
      } catch (error) {
        console.error('获取订单串码失败:', error)
        // 确保有基本的输入框可用
        this.ensureSerialInputsInitialized()
        throw error // 重新抛出错误
      }
    },

    /**
     * 确保串码输入框已初始化
     */
    ensureSerialInputsInitialized() {
      if (this.serialInputList.length === 0) {
        console.log('serialInputList为空，重新初始化')
        this.initSerialInputs()
      }
    },
    /**
     * 查询快递，电话信息回显
     */
    async getExpressCompanyInfoFn() {
      try {
        const res = await getExpressCompanyInfoShow(this.currentOrder.orderCode)
        if (res.code === 200 && res.data) {
          // 回显用户已填写的快递信息
          // 先回显快递公司，避免触发 courier watch 时手机号还未格式化
          if (res.data.logisticsCode) {
            const rawCode = String(res.data.logisticsCode)
            this.shippingForm.courier = rawCode
            // 如果 courierList 已加载，立即同步；否则等待 getExpressCompany 完成后同步
            if (this.shippingForm.courierList.length > 0) {
              this.syncCourierCodeWithList()
            }
          }

          // 快递单号（格式化但不显示错误）
          if (res.data.logisticsNo) {
            const waybillValue = String(res.data.logisticsNo)
            const waybillValidation = this.getExpressNo(waybillValue, this.shippingForm.courier)
            this.shippingForm.waybillNumber = waybillValidation.formatted
            // 回显时不显示错误，即使格式不正确也清除错误状态
            this.$delete(this.expressValidation.errors, 'waybillNumber')
          }

          // 最后回显手机号，并格式化验证（确保格式正确）
          if (res.data.phone) {
            const phoneValue = String(res.data.phone)
            const phoneValidation = this.phoneLimit(phoneValue, this.shippingForm.courier)
            this.shippingForm.senderPhone = phoneValidation.formatted
            // 回显时不显示错误，即使格式不正确也清除错误状态
            this.$delete(this.expressValidation.errors, 'senderPhone')
          }

          console.log('快递信息回显完成')
        } else {
          console.warn('获取快递信息返回异常:', res)
        }
        return true // 成功标识
      } catch (error) {
        console.error('获取快递信息回显失败:', error)
        // 快递信息获取失败不影响基本功能，继续执行
        throw error // 重新抛出错误
      }
    },
    // 获取验证状态图标

    // 复制地址时添加收件人和电话信息

    /**
     * 查询快递公司
     */
    async getExpressCompany() {
      // 防止短时间内重复发起相同请求：如果已有未完成的请求，直接复用它
      if (this._getExpressCompanyPromise) {
        return this._getExpressCompanyPromise
      }

      const doFetch = async() => {
        const maxAttempts = 3
        let attempt = 0
        let lastError = null

        while (attempt < maxAttempts) {
          try {
            const res = await getExpressCompany({ orderCode: this.currentOrder.orderCode })
            if (res && res.code === 200 && res.data) {
              // res.data的结构为：{shunfeng: "顺丰", ems: "邮政"}
              this.shippingForm.courierList = Object.entries(res.data).map(([key, value]) => ({
                code: key,
                name: value
              }))

              // 如果已有回显的快递公司代码，先尝试同步（可能在 getExpressCompanyInfoFn 中已设置）
              if (this.shippingForm.courier) {
                this.syncCourierCodeWithList()
              }

              // 设置默认选中第一个快递公司
              // 仅当尚未有回显的快递类型时才设置默认值，避免覆盖getExpressCompanyInfoFn回显
              if (this.shippingForm.courierList.length > 0 && !this.shippingForm.courier) {
                this.shippingForm.courier = this.shippingForm.courierList[0].code
              }
            } else {
              console.warn('获取快递公司列表返回异常:', res)
              // 接口返回异常：清空列表，回显逻辑会独立尝试回显已有快递信息
              this.shippingForm.courierList = []
            }

            return true // 成功标识（不论是否有数据）
          } catch (error) {
            attempt++
            lastError = error
            console.warn(`获取快递公司列表失败，第 ${attempt} 次重试...`, error)
            // 只有在失败时才重试（按指数退避）
            const delayMs = 200 * Math.pow(2, attempt - 1)
            // eslint-disable-next-line no-await-in-loop
            await new Promise(resolve => setTimeout(resolve, delayMs))
          }
        }

        // 超过重试次数仍然失败
        console.error('获取快递公司列表最终失败:', lastError)
        this.shippingForm.courierList = []
        throw lastError
      }

      // 缓存进行中的 promise，避免短时间内重复请求
      this._getExpressCompanyPromise = doFetch()
      try {
        const result = await this._getExpressCompanyPromise
        return result
      } finally {
        // 完成后清理缓存，允许后续重试
        this._getExpressCompanyPromise = null
      }
    },

    /**
     * 初始化弹窗数据
     */
    async initializeDialog() {
      try {
        // 1. 设置订单数据
        this.currentOrder = { ...this.orderData }
        console.log('弹窗打开，订单数据:', this.currentOrder)

        // 2. 重置表单状态
        this.resetForm()

        // 3. 等待DOM更新后初始化串码输入框
        await this.$nextTick()
        this.initSerialInputs()

        // 4. 获取快递公司并确保始终回显快递信息（即使获取失败）
        //    同时并行获取串码信息以提升性能
        const imeiPromise = this.getOrderImei().catch(err => {
          console.warn('串码信息获取失败:', err)
        })

        try {
          await this.getExpressCompany()
        } catch (err) {
          console.warn('获取快递公司列表失败（初始化）:', err)
        }

        // 无论 getExpressCompany 是否成功，都尝试回显快递信息
        try {
          await this.getExpressCompanyInfoFn()
        } catch (err) {
          console.warn('回显快递信息失败（初始化）:', err)
        }

        // 等待串码回显完成（如果仍在进行）
        await imeiPromise
      } catch (error) {
        console.error('弹窗初始化失败:', error)
        // 即使初始化失败也要确保基本的UI状态
        if (this.serialInputList.length === 0) {
          this.initSerialInputs()
        }
      }
    },

    /**
     * 初始化串码输入框
     */
    initSerialInputs() {
      const quantity = Number(this.currentOrder.quantity) || 1
      this.serialInputList = Array(quantity).fill('').map(() => ({ sn: '', code86: '' }))
      console.log('初始化串码输入框，数量:', quantity)
    },
    /**
     * 重置表单
     */
    resetForm() {
      console.log('resetForm被调用 - 清空serialInputList')
      this.serialCodes = []
      this.serialCodesText = ''
      this.serialInputList = []
      console.log('resetForm完成 - serialInputList:', this.serialInputList)
      this.serialInputExpanded = false
      this.repeatCode = []
      this.serialValidationErrors = {}
      this.serialValidationResults = {}
      this.showTips = false
      this.resetValidationState()
      this.saving = false
      // 重置快递验证数据
      this.expressValidation = {
        errors: {},
        waybillNumber: '',
        senderPhone: ''
      }
      this.validationSummary = {
        total: 0,
        valid: 0,
        invalid: 0,
        empty: 0,
        duplicate: 0
      }
      this.shippingForm = {
        quantity: String(this.currentOrder.quantity || 1),
        courier: '',
        courierList: [],
        waybillNumber: '',
        senderPhone: ''
      }
    },

    /**
     * 统一的重置验证状态方法
     */
    resetValidationState() {
      this.validationStatus = 'pending'
      this.imeiSaveValidated = false
      this.historyValidationPassed = false
      this.historyValidatedCodes = []
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
     * 确保当前选择的快递编码在快递列表中存在
     */
    syncCourierCodeWithList() {
      if (!this.shippingForm.courier || !Array.isArray(this.shippingForm.courierList)) return
      const normalized = this.normalizeCourierCode(this.shippingForm.courier)
      const matched = this.shippingForm.courierList.find(item => this.normalizeCourierCode(item.code) === normalized)
      if (matched) {
        this.shippingForm.courier = matched.code
      }
    },

    /**
     * 获取快递公司名称
     * @param {string} code - 快递公司代码
     * @returns {string} 快递公司名称
     */
    getCourierName(code) {
      if (!code) return '该快递公司'
      const normalized = this.normalizeCourierCode(code)
      const rule = COURIER_RULES[normalized]
      if (rule && rule.name) return rule.name
      if (normalized === 'shunfeng') return '顺丰快递'
      if (normalized === 'zhongtong') return '中通快递'
      // 从快递列表中查找
      const courier = this.shippingForm.courierList.find(item => this.normalizeCourierCode(item.code) === normalized)
      return courier ? courier.name : '该快递公司'
    },

    /**
     * 判断当前输入是否仍与历史验证通过的串码完全一致
     * @returns {boolean}
     */
    historyValidationConsistent() {
      if (!this.historyValidationPassed || !Array.isArray(this.historyValidatedCodes) || this.historyValidatedCodes.length === 0) {
        return false
      }

      // 获取当前有效的串码列表（过滤空值和无效值）
      const currentCodes = this.serialInputList
        .map(item => (item.sn || '').trim().toUpperCase())
        .filter(code => code && code.length > 0)

      // 获取历史验证的串码列表（确保都是大写，去重）
      const historyCodes = this.historyValidatedCodes
        .map(code => (code || '').trim().toUpperCase())
        .filter(code => code && code.length > 0)

      // 数量必须相等
      if (currentCodes.length !== historyCodes.length) {
        return false
      }

      // 创建Set进行比较（自动去重且忽略顺序）
      const currentSet = new Set(currentCodes)
      const historySet = new Set(historyCodes)

      // Set大小必须相等（说明没有重复项）
      if (currentSet.size !== historySet.size) {
        return false
      }

      // 逐个检查每个元素是否都存在于历史集合中
      for (const code of currentSet) {
        if (!historySet.has(code)) {
          return false
        }
      }

      return true
    },

    /**
     * Excel导入
     */

    /**
     * 切换串码输入展开状态
     */
    toggleSerialInput() {
      this.serialInputExpanded = !this.serialInputExpanded
    },

    /**
     * 串码输入变化
     */
    handleSerialCodesChange(value) {
      this.serialCodes = value.split('\n').filter(code => code.trim())
    },

    /**
     * 验证串码
     */
    async handleValidateSerial(event) {
      console.log('触发串码验证', event)
      console.log('validatingSerial 状态:', this.validatingSerial)
      console.log('当前串码列表:', this.serialCodes)

      // 如果正在验证中，阻止重复触发
      if (this.validatingSerial) {
        console.log('正在验证中，跳过')
        return
      }

      // 先进行本地验证
      if (!this.codeTestFun()) {
        console.log('本地验证失败')
        return
      }

      // 设置验证锁
      this.validatingSerial = true

      try {
        // 重置历史验证状态（在try块内，确保异常时不执行）
        this.resetValidationState()

        // sanitize serial codes: trim, uppercase, unique
        // 从 serialInputList 实时提取，确保数据一致性
        const currentSerialCodes = this.serialInputList
          .map(item => (item && item.sn ? item.sn : ''))
          .filter(code => code && code.trim())
        const sanitizedImei = Array.from(new Set(
          currentSerialCodes
            .map(s => (s || '').toString().trim().toUpperCase())
            .filter(Boolean)
        ))

        // 同时提交SN码和86码
        const snCodes = sanitizedImei
        const code86List = this.serialInputList
          .map(item => (item.code86 || '').toString().trim())
          .filter(code => code)

        // 根据类目决定提交的数据
        const submitData = {
          orderCode: this.currentOrder.orderCode,
          sn: snCodes[0],
          imeiCode: ''
        }

        // 如果是手机类目，才提交86码
        if (this.isPhoneCategory) {
          submitData.imeiCode = code86List[0]
        }

        const res = await imeiSave(submitData)

        if (res.code === 200) {
          console.log('res', res.data)
          // 处理验证结果，res.data是数组，包含每个串码的验证状态
          // 第三个参数为 true，表示这是通过用户点击验证按钮触发的
          this.handleValidationResult(res.data, true, true)
        } else {
          this.$message.error('串码验证失败')
          // 处理验证失败后的UI更新
          const repeatCodes = res.data && res.data.repetitionImeiSet ? res.data.repetitionImeiSet : []
          this.handleValidationFailure(repeatCodes)
        }
      } catch (error) {
        this.$message.error('验证失败，请重试')
        console.error('串码验证失败:', error)
        // 处理验证失败后的UI更新
        const repeatCodes = Array.isArray(error?.response?.data?.repetitionImeiSet) ? error.response.data.repetitionImeiSet : []
        this.handleValidationFailure(repeatCodes)
      } finally {
        // 确保状态被正确重置
        this.validatingSerial = false
        console.log('验证完成，validatingSerial设置为false')
      }
    },
    /**
     * 取消
     */
    handleCancel() {
      if (this.saving) {
        this.$message.warning('正在保存，请稍候再关闭')
        return
      }
      this.$emit('update:visible', false)
      this.$emit('cancel')
    },

    /**
     * 保存
     */
    async handleSave() {
      // 防止重复提交（saving 标记在函数最开头设置，确保第一时间锁定）
      if (this.saving) {
        return
      }
      this.saving = true

      try {
        // 验证串码
        if (!this.codeTestFun()) {
          return
        }

        // 检查串码是否已通过服务器验证
        if (!this.serialValidationPassed) {
          this.$message.warning('请先验证串码通过后再保存')
          return
        }

        // 验证快递信息
        if (!this.checkCondition()) {
          return
        }
        // 提交数据
        const courierItem = this.shippingForm.courier
          ? this.shippingForm.courierList.find(item => item.code === this.shippingForm.courier)
          : null
        const submitData = {
          cellphone: this.shippingForm.senderPhone,
          orderCode: this.currentOrder.orderCode,
          trackingNumber: this.shippingForm.waybillNumber,
          trackingCompany: courierItem ? courierItem.name : '',
          trackingCompanyCode: this.shippingForm.courier
        }
        console.log(submitData)
        const res = await saveOrderLogistics(submitData)
        if (res.code === 200) {
          this.$emit('update:visible', false)
          this.$emit('save')
        } else {
          this.$message.error(res.message || '保存失败')
        }
      } catch (error) {
        this.$message.error('保存失败，请重试')
      } finally {
        this.saving = false
      }
    },

    /**
     * 关闭弹窗
     */
    handleClose() {
      if (this.saving) {
        this.$message.warning('正在保存，请稍候再关闭')
        return
      }
      this.$emit('update:visible', false)
      this.$emit('close')
    },

    // ========== 快递验证相关方法 ==========

    /**
     * 核心快递验证方法 - expressTestFun()
     * @returns {boolean} 验证是否通过
     */
    expressTestFun() {
      // 检查运单号
      if (!this.shippingForm.waybillNumber.trim()) {
        this.$set(this.expressValidation.errors, 'waybillNumber', '请输入运单号')
        return false
      }

      // 验证运单号格式
      const waybillValidation = this.getExpressNo(this.shippingForm.waybillNumber, this.shippingForm.courier)
      if (!waybillValidation.valid) {
        this.$set(this.expressValidation.errors, 'waybillNumber', waybillValidation.message)
        return false
      }

      // 验证手机号格式（所有快递都支持输入，如果输入了则进行格式验证；顺丰和中通是必填的）
      const phoneValidation = this.phoneLimit(this.shippingForm.senderPhone, this.shippingForm.courier)
      if (!phoneValidation.valid) {
        this.$set(this.expressValidation.errors, 'senderPhone', phoneValidation.message)
        return false
      }

      // 清除所有错误
      this.expressValidation.errors = {}
      return true
    },

    /**
     * 运单号格式化 - getExpressNo()
     * @param {string} waybillNumber - 运单号
     * @param {string} courier - 快递公司代码
     * @returns {object} 验证结果
     */
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

      // 格式化：先去除前后空格，转大写，清空非字母数字字符
      const formatted = waybillNumber.trim().toUpperCase().replace(/[^A-Z0-9]/g, '')

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

      const normalizedCourier = this.normalizeCourierCode(courier)

      // 如果存在 COURIER_RULES 中的规则，优先使用其 validateWaybill 方法
      const rule = COURIER_RULES[normalizedCourier]
      if (rule && typeof rule.validateWaybill === 'function') {
        // 如果非顺丰快递却以 SF 开头，提示用户选择顺丰
        if (formatted.startsWith('SF') && normalizedCourier !== 'sf') {
          return {
            valid: false,
            message: '请选择顺丰或更换其他快递单号',
            formatted
          }
        }
        if (!rule.validateWaybill(formatted)) {
          result.valid = false
          result.message = `${rule.name} 运单号格式不符合要求`
        }
      } else {
        // fallback: 原有的轻量规则（保持兼容）
        const isSf = normalizedCourier === 'sf'
        if (!isSf && formatted.startsWith('SF')) {
          return {
            valid: false,
            message: '请选择顺丰或更换其他快递单号',
            formatted
          }
        }
        switch (normalizedCourier) {
          case 'sf':
            if (formatted.includes('SF')) {
              if (formatted.length !== 15) {
                result.valid = false
                result.message = '顺丰SF单号必须为15位'
              }
            } else {
              if (formatted.length !== 12) {
                result.valid = false
                result.message = '顺丰单号必须为12位'
              }
            }
            break
          case 'debangwuliu':
            if (formatted.length !== 15) {
              result.valid = false
              result.message = '德邦快递单号必须为15位'
            }
            break
          default:
            break
        }
      }

      result.formatted = formatted
      return result
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
     * 综合验证 - checkCondition()
     * @returns {boolean} 验证是否通过
     */
    checkCondition() {
      // 检查快递公司
      if (!this.shippingForm.courier) {
        this.$message.warning('请选择快递公司')
        return false
      }

      // 执行快递验证
      if (!this.expressTestFun()) {
        const errors = Object.values(this.expressValidation.errors)
        if (errors.length > 0) {
          this.$message.warning(errors[0])
        }
        return false
      }

      return true
    },

    /**
     * 验证快递单号格式
     * @param {string} waybillNumber - 运单号
     * @param {string} courier - 快递公司代码
     * @returns {object} 验证结果
     */
    validateWaybillNumber(waybillNumber, courier) {
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

      const normalizedCourier = this.normalizeCourierCode(courier)
      const rule = COURIER_RULES[normalizedCourier]
      if (rule && typeof rule.validateWaybill === 'function') {
        if (formatted.startsWith('SF') && normalizedCourier !== 'sf') {
          return {
            valid: false,
            message: '请选择顺丰或更换其他快递单号',
            formatted
          }
        }
        if (!rule.validateWaybill(formatted)) {
          result.valid = false
          result.message = `${rule.name} 运单号格式不符合要求`
        }
      } else {
        // fallback legacy rules
        switch (normalizedCourier) {
          case 'sf':
            if (formatted.includes('SF')) {
              if (formatted.length !== 15) {
                result.valid = false
                result.message = '顺丰SF单号必须为15位'
              }
            } else {
              if (formatted.length !== 12) {
                result.valid = false
                result.message = '顺丰单号必须为12位'
              }
            }
            break
          case 'debangwuliu':
            if (formatted.length !== 15) {
              result.valid = false
              result.message = '德邦快递单号必须为15位'
            }
            break
          default:
            if (formatted.length < 8) {
              result.valid = false
              result.message = '运单号长度不能少于8位'
            }
        }
      }

      result.formatted = formatted
      return result
    },

    /**
     * 验证寄件人手机号（11位）
     * @param {string} phone - 手机号（11位）
     * @param {string} courier - 快递公司代码
     * @returns {object} 验证结果
     */
    validateSenderPhone(phone, courier) {
      // 使用 phoneLimit 方法进行统一验证
      return this.phoneLimit(phone, courier)
    },

    /**
     * 处理运单号输入变化
     * @param {string} value - 输入值
     */
    handleWaybillValidation(validation) {
      // 处理运单号验证结果
      if (validation.valid) {
        this.$delete(this.expressValidation.errors, 'waybillNumber')
      } else {
        this.$set(this.expressValidation.errors, 'waybillNumber', validation.message)
      }
    },

    handleSenderPhoneValidation(validation) {
      // 处理手机号验证结果
      if (validation.valid) {
        this.$delete(this.expressValidation.errors, 'senderPhone')
      } else {
        this.$set(this.expressValidation.errors, 'senderPhone', validation.message)
      }
    },

    /**
     * 处理快递公司变化
     * @param {string} courier - 快递公司代码（新值）
     */
    handleCourierChange(courier) {
      // 处理快递公司变化
      // 重新验证运单号
      if (this.shippingForm.waybillNumber) {
        const validation = this.getExpressNo(this.shippingForm.waybillNumber, courier)
        this.handleWaybillValidation(validation)
      }

      // 如果新快递需要手机号且已输入，重新验证手机号
      const newRequiresPhone = courier && (this.normalizeCourierCode(courier) === 'sf' || this.normalizeCourierCode(courier) === 'zhongtong')
      if (newRequiresPhone && this.shippingForm.senderPhone) {
        const validation = this.phoneLimit(this.shippingForm.senderPhone, courier)
        this.handleSenderPhoneValidation(validation)
      }
    },

    /**
     * 验证所有快递信息
     * @returns {boolean} 验证是否通过
     */
    validateExpressInfo() {
      const waybillValidation = this.validateWaybillNumber(this.shippingForm.waybillNumber, this.shippingForm.courier)
      const phoneValidation = this.validateSenderPhone(this.shippingForm.senderPhone, this.shippingForm.courier)

      // 更新错误信息
      if (waybillValidation.valid) {
        this.$delete(this.expressValidation.errors, 'waybillNumber')
      } else {
        this.$set(this.expressValidation.errors, 'waybillNumber', waybillValidation.message)
      }

      if (phoneValidation.valid) {
        this.$delete(this.expressValidation.errors, 'senderPhone')
      } else {
        this.$set(this.expressValidation.errors, 'senderPhone', phoneValidation.message)
      }

      return waybillValidation.valid && phoneValidation.valid
    },

    // ========== 串码验证相关方法 ==========

    /**
     * 检查串码长度是否正确
     * @param {string} imei - 串码
     * @param {object} test - 验证配置，默认为this.imei
     * @returns {boolean} 长度是否正确
     */
    isCorrectLength(imei, test = this.imei) {
      return imei && imei.length >= test.min && imei.length <= test.max
    },

    /**
     * 获取串码样式类名
     * @param {string} imei - 串码
     * @returns {Array} 样式类名数组
     */

    /**
     * 处理串码输入
     * @param {number} index - 输入框索引
     * @param {string} field - 字段名 ('sn' 或 'code86')
     * @param {string} value - 输入值
     */
    handleSerialInputChange(index, field, value) {
      let nowValue = value

      // 记录修改前的值（用于清理旧的验证结果）
      const prevItem = this.serialInputList[index] ? { sn: this.serialInputList[index].sn, code86: this.serialInputList[index].code86 } : { sn: '', code86: '' }
      const prevSnUpper = (prevItem.sn || '').toUpperCase()

      if (field === 'sn') {
        nowValue = this.imei.handle
          ? this.imei.handle(nowValue)
          : nowValue.toUpperCase()

        // 长度限制
        if (nowValue.length > this.imei.max) {
          nowValue = nowValue.slice(0, this.imei.max)
        }
      } else if (field === 'code86') {
        // 86码格式化：只能包含数字，长度限制为15
        nowValue = nowValue.replace(/[^0-9]/g, '')
        if (nowValue.length > 15) {
          nowValue = nowValue.slice(0, 15)
        }
      }

      // 确保对象存在
      if (!this.serialInputList[index]) {
        this.$set(this.serialInputList, index, { sn: '', code86: '' })
      }

      // 记录修改前是否有验证结果或验证状态（必须在清空之前记录）
      const hadValidationResults = Object.keys(this.serialValidationResults).length > 0
      const hadValidationStatus = this.imeiSaveValidated || this.historyValidationPassed

      // 更新对应字段
      this.$set(this.serialInputList[index], field, nowValue)

      // 检查历史验证是否仍然一致
      if (this.historyValidationPassed && !this.historyValidationConsistent()) {
        this.historyValidationPassed = false
        this.historyValidatedCodes = []
      }

      // 更新serialCodes数组（使用sn字段）
      this.serialCodes = this.serialInputList.map(item => (item.sn || '').trim()).filter(code => code)

      // 清除该索引的错误信息
      this.$delete(this.serialValidationErrors, index)

      // 清除旧的验证结果（如果sn被修改）
      if (field === 'sn') {
        const newSnUpper = (nowValue || '').toUpperCase()
        if (prevSnUpper && prevSnUpper !== newSnUpper) {
          // 删除以 SN 为 key 的旧入口
          this.$delete(this.serialValidationResults, prevSnUpper)
          // 同时删除旧的 composite key（prevSn-prevImel），防止残留
          const prevImelUpper = (prevItem.code86 || '').toString().trim().toUpperCase()
          if (prevImelUpper) {
            const prevComposite = `${prevSnUpper}-${prevImelUpper}`
            this.$delete(this.serialValidationResults, prevComposite)
          }
        }
        // 注意：不要删除 newSnUpper 的全局 entry，这可能会影响其他行的兼容性
      }

      // 如果用户修改了SN码或86码，且之前有验证状态或结果，需要重置验证状态
      // 这样可以确保修改后必须重新验证
      // 注意：非手机类目修改86码不触发重置（因为不需要86码）
      const shouldReset = (field === 'sn') || (field === 'code86' && this.isPhoneCategory)
      if (shouldReset && (hadValidationStatus || hadValidationResults)) {
        this.resetValidationState()
        // 清空运单号，确保只有验证成功后才能输入（保留已输入的寄件人手机号，避免打断编辑）
        this.shippingForm.waybillNumber = ''
        this.shippingForm.senderPhone = ''
        this.$delete(this.expressValidation.errors, 'waybillNumber')
        this.$delete(this.expressValidation.errors, 'senderPhone')
      }

      // 实时验证SN码或86码（并显示行级错误）
      if (field === 'sn') {
        this.validateSerialCode(index, nowValue)

        // 当 SN 变更时，尝试用当前行的 SN+86 与已保存快照比对并恢复行级/整体验证（如果匹配）
        try {
          const newSnUpper = (nowValue || '').toString().trim().toUpperCase()
          if (newSnUpper && this._savedValidationSnapshot && this._savedValidationSnapshot.results) {
            let saved = null
            let keyToUse = newSnUpper

            // 如果是手机类目，使用 composite key (SN-86码)
            if (this.isPhoneCategory) {
              const imUpper = (this.serialInputList[index] && this.serialInputList[index].code86) ? this.serialInputList[index].code86.toString().trim().toUpperCase() : ''
              if (imUpper) {
                const compositeKeyNew = `${newSnUpper}-${imUpper}`
                saved = this._savedValidationSnapshot.results[compositeKeyNew]
                keyToUse = compositeKeyNew
              }
            } else {
              // 非手机类目，只使用 SN 作为 key
              saved = this._savedValidationSnapshot.results[newSnUpper]
            }

            if (saved) {
              // 恢复该行的验证结果
              this.$set(this.serialValidationResults, keyToUse, saved)
              this.$set(this.serialValidationResults, newSnUpper, saved)

              // 检查是否所有行都能从快照恢复为已验证（activated === 4）
              const pairs = this.serialInputList
                .map(it => {
                  const s = (it.sn || '').toString().trim().toUpperCase()
                  if (!s) return null
                  // 手机类目需要同时有 SN 和 86 码
                  if (this.isPhoneCategory) {
                    const im = (it.code86 || '').toString().trim().toUpperCase()
                    return s && im ? `${s}-${im}` : null
                  }
                  // 非手机类目只需要 SN
                  return s
                })
                .filter(Boolean)
              let allMatch = pairs.length > 0
              for (const p of pairs) {
                const r = this._savedValidationSnapshot.results[p]
                if (!r || Number(r.activated) !== 4) {
                  allMatch = false
                  break
                }
              }
              if (allMatch) {
                this.validationStatus = 'success'
                this.imeiSaveValidated = true
                this.historyValidationPassed = false
                // 手机类目：从 composite key 提取 SN    非手机类目：直接使用 SN
                this.historyValidatedCodes = this.isPhoneCategory ? pairs.map(p => p.split('-')[0]) : pairs
                this.$message.success('检测到恢复为上一次已验证的串码，已自动恢复验证状态;    请重新输入运单号和寄件人手机号')
              }
            }
          }
        } catch (e) {
          // ignore snapshot restore errors
        }
      } else if (field === 'code86') {
        // 如果是手机类目，才处理 86 码相关的验证逻辑
        if (this.isPhoneCategory) {
          // 当 86 码变更时，需删除该行以 SN 为 key 的入口，避免在 composite key 变更时通过 SN 回退错误地显示旧状态
          const currentSnUpper = (this.serialInputList[index] && this.serialInputList[index].sn) ? this.serialInputList[index].sn.toUpperCase() : ''
          const prevImelUpper = (prevItem.code86 || '').toString().trim().toUpperCase()
          if (currentSnUpper) {
            this.$delete(this.serialValidationResults, currentSnUpper)
          }
          // 删除旧的 composite key（sn-prevImel）
          if (currentSnUpper && prevImelUpper) {
            const prevComposite = `${currentSnUpper}-${prevImelUpper}`
            this.$delete(this.serialValidationResults, prevComposite)
          }
          // 执行格式校验
          this.validateCode86(index, nowValue)

          // 尝试用已保存的验证快照恢复该行状态（如果存在）
          // 只在手机类目时执行，因为非手机类目不使用 composite key
          try {
            const newImelUpper = (nowValue || '').toString().trim().toUpperCase()
            if (currentSnUpper && newImelUpper && this._savedValidationSnapshot && this._savedValidationSnapshot.results) {
              const compositeKeyNew = `${currentSnUpper}-${newImelUpper}`
              const saved = this._savedValidationSnapshot.results[compositeKeyNew]
              if (saved) {
                // 恢复该行的验证结果
                this.$set(this.serialValidationResults, compositeKeyNew, saved)
                this.$set(this.serialValidationResults, currentSnUpper, saved)

                // 检查是否所有行都能从快照恢复为已验证（activated === 4）
                const pairs = this.serialInputList
                  .map(it => {
                    const s = (it.sn || '').toString().trim().toUpperCase()
                    const im = (it.code86 || '').toString().trim().toUpperCase()
                    return s && im ? `${s}-${im}` : null
                  })
                  .filter(Boolean)
                let allMatch = pairs.length > 0
                for (const p of pairs) {
                  const r = this._savedValidationSnapshot.results[p]
                  if (!r || Number(r.activated) !== 4) {
                    allMatch = false
                    break
                  }
                }
                if (allMatch) {
                  this.validationStatus = 'success'
                  this.imeiSaveValidated = true
                  this.historyValidationPassed = false
                  this.historyValidatedCodes = pairs.map(p => p.split('-')[0])
                  this.$message.success('检测到恢复为上一次已验证的串码，已自动恢复验证状态;   请重新输入运单号和寄件人手机号')
                }
              }
            }
          } catch (e) {
            // ignore snapshot restore errors
          }
        }
      }

      // 实时更新验证汇总
      this.updateValidationSummary()
    },

    /**
     * 验证单个86码
     * @param {number} index - 码索引
     * @param {string} code86 - 86码值
     */
    validateCode86(index, code86) {
      // 如果不是手机类目，不需要验证86码
      if (!this.isPhoneCategory) {
        this.$delete(this.serialValidationErrors, index)
        return true
      }

      if (!code86) {
        this.$set(this.serialValidationErrors, index, '第' + (index + 1) + '个86码未输入')
        return false
      }

      // 检查是否以86开头
      if (!code86.startsWith('86')) {
        this.$set(this.serialValidationErrors, index, `第${index + 1}个86码格式错误：应以 "86" 开头`)
        return false
      }

      // 检查长度是否为15位
      if (code86.length !== 15) {
        this.$set(this.serialValidationErrors, index, `第${index + 1}个86码长度错误：应为15位数字，当前为 ${code86.length} 位`)
        return false
      }

      // 检查是否只包含数字
      if (!/^\d+$/.test(code86)) {
        this.$set(this.serialValidationErrors, index, `第${index + 1}个86码格式错误：只能包含数字`)
        return false
      }

      // 清除错误信息
      this.$delete(this.serialValidationErrors, index)
      return true
    },

    /**
     * 验证单个串码
     * @param {number} index - 串码索引
     * @param {string} imei - 串码
     */
    validateSerialCode(index, imei) {
      if (!imei) {
        // 清除错误信息，因为空值不算错误（会在 codeTestFun 中统一提示）
        this.$delete(this.serialValidationErrors, index)
        return
      }

      // 长度验证
      if (!this.isCorrectLength(imei)) {
        if (imei.length < this.imei.min) {
          const need = this.imei.min - imei.length
          const lengthMsg = `SN码长度不足：需 ${this.imei.min}-${this.imei.max} 位，当前 ${imei.length} 位，尚需补全 ${need} 位`
          this.$set(this.serialValidationErrors, index, lengthMsg)
        } else {
          const extra = imei.length - this.imei.max
          const lengthMsg = `SN码长度过长：需 ${this.imei.min}-${this.imei.max} 位，当前 ${imei.length} 位，请删除多余 ${extra} 位`
          this.$set(this.serialValidationErrors, index, lengthMsg)
        }
        return false
      }

      // 字母验证：SN码必须包含至少一个字母
      if (!/[a-zA-Z]/.test(imei)) {
        this.$set(this.serialValidationErrors, index, 'SN码必须包含至少一个字母')
        return false
      }

      // 正则验证
      if (this.imei.resTest && !this.imei.resTest.test(imei)) {
        this.$set(this.serialValidationErrors, index, this.imei.resWrong)
        return false
      }

      // 重复验证
      const codeArr = this.serialInputList
        .map(item => (item && item.sn ? item.sn : '').toUpperCase())
        .filter(item => !!item)
      const repeatCount = codeArr.filter(item => item === imei.toUpperCase()).length
      if (repeatCount > 1) {
        this.$set(this.serialValidationErrors, index, '当前输入中存在重复SN码，请检查')
        return false
      }

      // 全局重复验证
      if (this.repeatCode.some(item => item.toUpperCase() === imei.toUpperCase())) {
        this.$set(this.serialValidationErrors, index, '该串码已在系统中存在，请使用其他串码')
        return false
      }

      // 验证通过，清除错误信息
      this.$delete(this.serialValidationErrors, index)
      return true
    },

    /**
     * 综合验证所有串码
     * @returns {boolean} 验证是否通过
     */
    codeTestFun() {
      // 更新验证汇总
      this.updateValidationSummary()

      // 检查是否有输入
      const hasSnInput = this.serialInputList.some(item => item && item.sn && item.sn.trim())
      if (!hasSnInput) {
        this.$message.warning('请至少输入一个SN码')
        return false
      }

      // 如果是手机类目，检查86码输入
      if (this.isPhoneCategory) {
        const hasCode86Input = this.serialInputList.some(item => item && item.code86 && item.code86.trim())
        if (!hasCode86Input) {
          this.$message.warning('请至少输入一个86码')
          return false
        }
      }

      // 检查数量是否足够
      const validSnCodes = this.serialInputList.filter(item => item && item.sn && item.sn.trim())
      const requiredQty = Number(this.currentOrder.quantity) || 0
      if (validSnCodes.length < requiredQty) {
        this.$message.warning(`SN码数量不足，需要${this.currentOrder.quantity}个，当前已输入${validSnCodes.length}个，还需输入${this.currentOrder.quantity - validSnCodes.length}个`)
        return false
      }

      // 如果是手机类目，检查86码数量
      if (this.isPhoneCategory) {
        const validCode86Codes = this.serialInputList.filter(item => item && item.code86 && item.code86.trim())
        if (validCode86Codes.length < requiredQty) {
          this.$message.warning(`86码数量不足，需要${this.currentOrder.quantity}个，当前已输入${validCode86Codes.length}个，还需输入${this.currentOrder.quantity - validCode86Codes.length}个`)
          return false
        }
      }

      // 检查重复
      const codeArr = this.serialInputList
        .map(item => (item && item.sn ? item.sn : '').toUpperCase())
        .filter(item => !!item)
      const newArr = Array.from(new Set(codeArr))
      if (newArr.length < codeArr.length) {
        const duplicateCount = codeArr.length - newArr.length
        this.$message.error(`发现${duplicateCount}个重复串码，请检查并修改`)
        return false
      }

      // 检查全局重复
      if (this.repeatCode.some(item => codeArr.includes(item.toUpperCase()))) {
        this.$message.error('部分串码在系统中已存在，请使用其他串码')
        return false
      }

      // 逐个验证串码
      for (let i = 0; i < this.serialInputList.length; i++) {
        const item = this.serialInputList[i]
        const imei = item.sn
        if (!imei) {
          this.$set(this.serialValidationErrors, i, '第' + (i + 1) + '个SN码未输入')
          continue
        }

        // 如果是手机类目，验证86码
        if (this.isPhoneCategory) {
          if (!item.code86) {
            this.$set(this.serialValidationErrors, i, '第' + (i + 1) + '个86码未输入')
            continue
          }

          if (!this.validateCode86(i, item.code86)) {
            continue
          }
        }

        if (!this.isCorrectLength(imei)) {
          if (imei.length < this.imei.min) {
            const need = this.imei.min - imei.length
            const lengthMsg = `第${i + 1}个SN码长度不足：需 ${this.imei.min}-${this.imei.max} 位，当前 ${imei.length} 位，尚需补全 ${need} 位`
            this.$set(this.serialValidationErrors, i, lengthMsg)
          } else {
            const extra = imei.length - this.imei.max
            const lengthMsg = `第${i + 1}个SN码长度过长：需 ${this.imei.min}-${this.imei.max} 位，当前 ${imei.length} 位，请删除多余 ${extra} 位`
            this.$set(this.serialValidationErrors, i, lengthMsg)
          }
          continue
        }

        // 字母验证：SN码必须包含至少一个字母
        if (!/[a-zA-Z]/.test(imei)) {
          this.$set(this.serialValidationErrors, i, `第${i + 1}个SN码必须包含至少一个字母`)
          continue
        }

        if (this.imei.resTest && !this.imei.resTest.test(imei)) {
          this.$set(this.serialValidationErrors, i, `第${i + 1}个串码格式不正确，${this.imei.resWrong}`)
          continue
        }
      }

      // 检查是否有验证错误
      const hasErrors = Object.keys(this.serialValidationErrors).length > 0
      if (hasErrors) {
        const errorCount = Object.keys(this.serialValidationErrors).length
        this.$message.error(`发现${errorCount}个串码存在问题，请检查并修正`)
        return false
      }

      return true
    },

    /**
     * 更新验证结果汇总
     */
    updateValidationSummary() {
      // 只计算实际需要的串码数量，而不是所有输入框
      const requiredQuantity = Number(this.currentOrder.quantity) || 1
      const total = requiredQuantity

      // 统计每一行的完整性（同时有SN码和86码）
      let formatValid = 0 // 格式验证通过的条目数
      let duplicate = 0 // 重复错误的条目数
      let invalid = 0 // 其他错误的条目数
      let empty = 0 // 完全为空的条目数

      this.serialInputList.forEach((item, index) => {
        const snCode = (item.sn || '').trim()
        const code86 = (item.code86 || '').trim()

        // 检查是否完全为空
        if (!snCode && !code86) {
          empty++
          return
        }

        // 如果是手机类目，需要检查是否同时有SN码和86码
        if (this.isPhoneCategory) {
          if (!snCode || !code86) {
            invalid++ // 如果只有其中一个有值，也算作无效
            return
          }
        } else {
          // 非手机类目，只需要SN码
          if (!snCode) {
            invalid++
            return
          }
        }

        // 此时该行有必需的码，检查验证错误
        const error = this.serialValidationErrors[index]
        if (error) {
          if (error.includes('重复') || error.includes('已存在')) {
            duplicate++
          } else {
            invalid++
          }
          return
        }

        // 检查SN码格式是否正确
        if (!this.isCorrectLength(snCode)) {
          invalid++
          return
        }
        // 检查SN码是否包含字母
        if (!/[a-zA-Z]/.test(snCode)) {
          invalid++
          return
        }
        if (this.imei.resTest && !this.imei.resTest.test(snCode)) {
          invalid++
          return
        }

        // 如果是手机类目，检查86码格式
        if (this.isPhoneCategory) {
          if (!code86.startsWith('86') || code86.length !== 15 || !/^\d+$/.test(code86)) {
            invalid++
            return
          }
        }

        // 所有验证都通过
        formatValid++
      })

      // 调整empty计数：完全为空的条目数应该是总需求量减去有任何输入的条目数
      const entriesWithAnyInput = this.serialInputList.length - empty
      empty = Math.max(total - entriesWithAnyInput, 0)

      this.validationSummary = {
        total,
        valid: formatValid,
        invalid: duplicate + invalid,
        empty,
        duplicate
      }
    },

    /**
     * 获取串码验证消息
     * @param {string} imei - 串码
     * @returns {string} 验证消息
     */

    /**
     * 显示验证提示
     */
    showValidationTips() {
      this.showTips = !this.showTips
    },

    /**
     * 验证快递信息
     */
    handleValidateExpress() {
      if (this.checkCondition()) {
        this.$message.success('快递信息验证通过！')
      }
    },

    /**
     * 处理验证结果
     * @param {Array} validationData - 验证结果数组，格式：[{imel: 'K6WT6GJF43', activated: 4, platformImei: 0}]
     * @param {boolean} showToast - 是否显示toast消息，默认为true
     * @param {boolean} isUserTriggered - 是否是通过用户点击验证按钮触发的，默认为false
     */
    handleValidationResult(validationData, showToast = true, isUserTriggered = false) {
      console.log('处理验证结果:', validationData)

      // 存储每个串码的验证结果
      this.serialValidationResults = {}

      // 统计各种状态的数量
      let activatedCount = 0
      let modelMismatchCount = 0
      let verifiedCount = 0
      let hasErrors = false

      const normalizedValidationCodes = []
      // sn-imel
      // 处理每个串码的验证结果
      validationData.forEach((item, index) => {
        // 按你的要求，使用 `${item.sn}-${item.imel}` 作为主键（兼容 imei）
        const snRaw = item.sn || ''
        const imelRaw = item.imel || item.imei || ''
        const sn = snRaw.toString().trim()
        const imel = imelRaw.toString().trim()
        const activated = item.activated
        const platformImei = item.platformImei || 0

        if (!sn && !imel) {
          console.warn(`第${index + 1}个串码数据无效:`, item)
          return
        }

        const resultObj = {
          activated: activated,
          platformImei: platformImei,
          message: this.getActivatedMessage(activated, platformImei, item.recognizedProductName, item.recognizedSkuName)
        }

        // 根据类目决定使用的 key
        if (this.isPhoneCategory && imel) {
          // 手机类目：使用 composite key (SN-IMEL)
          const compositeKey = `${(sn || '').toUpperCase()}-${(imel || '').toUpperCase()}`
          this.serialValidationResults[compositeKey] = resultObj
          // 兼容性：保留以 SN 为 key 的入口
          if (sn) {
            this.serialValidationResults[sn.toUpperCase()] = resultObj
          }
        } else {
          // 非手机类目：只使用 SN 作为 key
          if (sn) {
            this.serialValidationResults[sn.toUpperCase()] = resultObj
          }
        }

        // 统计各种状态
        switch (activated) {
          case 1: // 已被激活
            activatedCount++
            hasErrors = true
            break
          case 2: // 型号不一致
            modelMismatchCount++
            hasErrors = true
            break
          case 3: // 型号不一致
            modelMismatchCount++
            hasErrors = true
            break
          case 4: // 验证通过
            verifiedCount++
            break
          case 5: // 验证失败
            hasErrors = true
            break
          case 6: // 已经使用
            hasErrors = true
            break
          default:
            hasErrors = true
        }

        // 根据类目决定使用哪个码进行归一化
        // 非手机类目优先使用 sn，手机类目使用 sn（imel 作为辅助信息）
        const codeForNormalized = this.isPhoneCategory
          ? (sn || imel).toString().trim().toUpperCase()
          : (sn || '').toString().trim().toUpperCase()
        if (codeForNormalized) {
          normalizedValidationCodes.push(codeForNormalized)
        }
      })

      const allVerified = verifiedCount === validationData.length && activatedCount === 0 && modelMismatchCount === 0

      if (isUserTriggered) {
        // 用户手动触发验证
        if (allVerified) {
          // 验证成功
          this.validationStatus = 'success'
          this.imeiSaveValidated = true
          this.historyValidationPassed = false // 清除历史状态，因为这是新的验证结果
          // 清除所有错误信息，确保 serialValidationPassed 能正确返回 true
          this.serialValidationErrors = {}
          // 使用 normalizedValidationCodes 保持与 API 返回数据的一致性
          this.historyValidatedCodes = Array.from(new Set(normalizedValidationCodes.map(c => (c || '').toUpperCase())))
          console.log('验证状态设置为success')
        } else {
          // 验证失败
          this.handleValidationFailure()
          console.log('验证状态设置为error')
        }
      } else {
        // 回显历史数据
        this.resetValidationState()
        const quantity = Number(this.currentOrder.quantity) || normalizedValidationCodes.length
        const hasValidHistory = allVerified &&
          normalizedValidationCodes.length === quantity &&
          normalizedValidationCodes.every(code => !!code)
        if (hasValidHistory) {
          this.historyValidationPassed = true
          this.historyValidatedCodes = Array.from(new Set(normalizedValidationCodes.map(c => (c || '').toUpperCase())))
        }
        console.log('回显串码，保持验证状态为pending，historyValidationPassed =', this.historyValidationPassed)
      }

      // 显示验证结果消息
      this.showValidationResultMessage(activatedCount, modelMismatchCount, verifiedCount, showToast)

      // 更新验证汇总
      this.updateValidationSummary()

      // 保存当前验证结果快照，用于后续输入恢复时比对（以 composite key 为准）
      try {
        this._savedValidationSnapshot = {
          results: JSON.parse(JSON.stringify(this.serialValidationResults))
        }
      } catch (e) {
        this._savedValidationSnapshot = { results: { ...this.serialValidationResults }}
      }

      // 重新验证所有串码以更新UI状态
      this.serialInputList.forEach((item, index) => {
        if (item && item.sn && item.sn.trim()) {
          this.validateSerialCode(index, item.sn)
        }
      })

      // 如果有错误，展开串码输入区域
      if (hasErrors && !this.serialInputExpanded) {
        this.serialInputExpanded = true
      }
    },

    /**
     * 根据activated状态获取对应的提示消息
     * @param {number} activated - 激活状态
     * @param {number} platformImei - 平台串码状态
     * @returns {string} 提示消息
     */
    getActivatedMessage(activated, platformImei = 0, recognizedProductName = '', recognizedSkuName = '') {
      // 根据类目决定错误提示中的码类型描述
      const codeType = this.isPhoneCategory ? 'SN/86码' : 'SN码'
      let baseMessage = ''

      switch (activated) {
        case 1:
          baseMessage = `该串码${codeType}已经被激活，请重新输入`
          break
        case 2:
          baseMessage = '商品型号不一致，请联系采购审核！商品名：' + this.currentOrder.productName + ' ' + this.currentOrder.skuName
          break
        case 3:
          baseMessage = `该串码${codeType}型号不一致，系统识别结果：`
          if (recognizedProductName) {
            baseMessage += `\n商品名称：${recognizedProductName}`
          }
          if (recognizedSkuName) {
            baseMessage += `\n型号规格：${recognizedSkuName}`
          }
          baseMessage += '\n请核对后重新输入'
          break
        case 4:
          baseMessage = '该串码验证通过'
          break
        case 5:
          baseMessage = `该串码${codeType}验证失败，请重新输入`
          break
        case 6:
          baseMessage = '该串码已经使用，今日不能再次使用该串码'
          break
        default:
          baseMessage = `该串码${codeType}验证失败，请重新输入`
      }

      // 如果有平台信息，添加平台状态说明
      if (platformImei !== undefined && platformImei !== null) {
        if (platformImei === 1) {
          baseMessage += ' (平台已绑定)'
        }
      }

      return baseMessage
    },

    /**
     * 显示验证结果消息
     * @param {number} activatedCount - 已激活数量
     * @param {number} modelMismatchCount - 型号不一致数量
     * @param {number} verifiedCount - 验证通过数量
     * @param {boolean} showToast - 是否显示toast消息，默认为true
     */
    showValidationResultMessage(activatedCount, modelMismatchCount, verifiedCount, showToast = true) {
      let message = ''

      if (verifiedCount > 0) {
        message += `${verifiedCount}个串码验证通过`
      }

      if (activatedCount > 0) {
        if (message) message += '，'
        message += `${activatedCount}个串码已被激活`
      }

      if (modelMismatchCount > 0) {
        if (message) message += '，'
        message += `${modelMismatchCount}个串码型号不一致`
      }

      // 只有在需要显示toast时才显示消息
      if (showToast && message) {
        // 如果只有验证通过，显示成功消息
        if (activatedCount === 0 && modelMismatchCount === 0) {
          this.$message.success(message)
        } else {
          this.$message.warning(message)
        }
      }
    },

    /**
     * 处理验证成功后的UI更新
     */
    handleValidationSuccess() {
      // 设置验证状态为成功
      this.validationStatus = 'success'
      this.imeiSaveValidated = true
      this.historyValidationPassed = false

      // 清除所有错误信息
      this.serialValidationErrors = {}

      // 更新验证汇总
      this.updateValidationSummary()

      // 重新验证所有串码以更新UI状态
      this.serialInputList.forEach((item, index) => {
        if (item && item.sn && item.sn.trim()) {
          this.validateSerialCode(index, item.sn)
        }
      })

      // 自动收起串码输入区域
      if (this.serialInputExpanded) {
        this.serialInputExpanded = false
      }

      // 显示成功状态
      this.$nextTick(() => {
        this.$message.success('所有串码验证通过！')
      })
    },

    /**
     * 处理验证失败后的UI更新
     */
    handleValidationFailure(repeatCodes = []) {
      // 重置所有验证状态，然后设置为失败
      this.resetValidationState()
      this.validationStatus = 'error'

      // 清空运单号，确保只有验证成功后才能输入；保留用户已输入的手机号
      this.shippingForm.waybillNumber = ''
      this.shippingForm.senderPhone = ''
      this.$delete(this.expressValidation.errors, 'waybillNumber')
      this.$delete(this.expressValidation.errors, 'senderPhone')

      // 更新重复码列表
      if (repeatCodes.length > 0) {
        this.repeatCode = repeatCodes
      }

      // 重新验证所有串码以显示错误状态
      this.serialInputList.forEach((item, index) => {
        if (item && item.sn && item.sn.trim()) {
          this.validateSerialCode(index, item.sn)
        }
      })

      // 更新验证汇总
      this.updateValidationSummary()

      // 展开串码输入区域以显示错误
      if (!this.serialInputExpanded) {
        this.serialInputExpanded = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
// 提交串码弹窗样式
.submit-serial-content {
  height: 500px;
  overflow-y: auto;

  .input-tip {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 4px;
    font-size: 12px;
    color: #909399;

    i {
      font-size: 12px;
    }
  }

  .express-error-message {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 4px;
    font-size: 12px;
    color: #f56c6c;

    i {
      font-size: 12px;
    }
  }
}

@keyframes warning-pulse {
  0% {
    background: linear-gradient(135deg, #fff2e8 0%, #ffd591 100%);
    border-color: #ffb366;
  }
  50% {
    background: linear-gradient(135deg, #ffe7d3 0%, #ffc53d 100%);
    border-color: #ff8c00;
  }
  100% {
    background: linear-gradient(135deg, #fff2e8 0%, #ffd591 100%);
    border-color: #ffb366;
  }
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-2px);
  }
  75% {
    transform: translateX(2px);
  }
}

@keyframes slideDown {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse-success {
  0% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(103, 194, 58, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0);
  }
}

@keyframes fadeInUp {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
