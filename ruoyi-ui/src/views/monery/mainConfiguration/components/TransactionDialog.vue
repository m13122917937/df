<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    width="720px"
    :append-to-body="appendToBody"
    :z-index="zIndex"
    @close="handleClose"
  >
    <div class="transaction-dialog">
      <div class="transaction-dialog__summary">
        <div class="summary-item">
          <span class="label">收款主体：</span>
          <span class="value">{{ payName || '--' }}</span>
        </div>
        <div class="summary-item">
          <span class="label">方向：</span>
          <span
            class="value"
            :class="{'value--income': form.category === 0, 'value--expense': form.category === 1}"
          >
            {{ form.category === 0 ? '收入' : form.category === 1 ? '支出' : '未选择' }}
          </span>
        </div>
      </div>

      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="96px"
        class="transaction-form"
      >
        <div class="form-grid">
          <div class="form-col">
            <el-form-item label="收款主体" prop="accountId">
              <el-select
                v-model="form.accountId"
                placeholder="请选择收款主体"
                filterable
                class="full-width"
                disabled
              >
                <el-option
                  v-for="item in payerOptions"
                  :key="item.id"
                  :label="item.payName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="交易日期" prop="transactionDate">
              <el-date-picker
                v-model="form.transactionDate"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择交易日期"
                class="full-width"
              />
            </el-form-item>

            <el-form-item label="金额" prop="amount">
              <el-input-number
                v-model.number="form.amount"
                :min="0"
                :step="1"
                :precision="2"
                placeholder="请输入交易金额"
                class="full-width amount-input"
                :formatter="formatCurrency"
                :parser="parseCurrency"
                @mousewheel.native.prevent
                @keydown.native="handleNumberKeydown"
                @paste.native="handleNumberPaste"
              />
            </el-form-item>
          </div>

          <div class="form-col">
            <el-form-item label="类别" prop="category">
              <el-radio-group v-model="form.category">
                <el-radio :label="0">收入</el-radio>
                <el-radio :label="1">支出</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="子类别" prop="subCategory">
               <el-select
                v-model="form.subCategory"
                placeholder="编辑后请按回车确认"
                filterable
                allow-create
                default-first-option
                class="full-width"
              >
                <el-option
                  v-for="item in subCategoryOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.name"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="支付方式" prop="paymentMethod">
              <!-- <el-input
                v-model.trim="form.paymentMethod"
                placeholder="如 转账、刷卡、现金等"
              /> -->
              <el-select
                v-model="form.paymentMethod"
                placeholder="请选择支付方式"
                filterable
                class="full-width"
              >
                <el-option
                  v-for="item in paymentMethodOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.name"
                />
              </el-select>
            </el-form-item>
          </div>
        </div>

        <el-form-item label="交易对方" prop="counterparty">
            <el-autocomplete
              v-model="form.counterparty"
              placeholder="请输入交易对方（可模糊搜索，回车确认）"
              :fetch-suggestions="queryCounterparty"
              :trigger-on-focus="false"
              :debounce="300"
              clearable
              class="full-width"
              @select="handleCounterpartySelect"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            type="textarea"
            :rows="3"
            v-model.trim="form.remark"
            placeholder="补充说明本次收支的背景、用途等"
          />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" v-throttle-click="600">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getCompanyListApi } from "@/api/monery";
import {formatDate} from "@/utils";
export default {
  name: 'TransactionDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    payerOptions: {
      type: Array,
      default: () => []
    },
    payName: {
      type: String,
      default: ''
    },
    // 默认选中的收款主体ID（从父表格行传入）
    defaultAccountId: {
      type: [Number, String],
      default: null
    },
    // 弹窗是否追加到 body，便于嵌套时不被遮挡
    appendToBody: {
      type: Boolean,
      default: false
    },
    // 可自定义 zIndex，嵌套弹窗时提高层级
    zIndex: {
      type: [Number, String],
      default: 2000
    },
    // 编辑时回填的数据
    editData: {
      type: Object,
      default: () => null
    }
  },
  data() {
    return {
      inCategoryOptions: [
        { id: '0', name: '收款快手平台货款' },
        { id: '1', name: '收款抖音平台货款' },
        { id: '2', name: '收款拼多多平台货款' },
        { id: '3', name: '收款淘宝平台货款' },
        { id: '4', name: '收入' },
        { id: '5', name: '转入' },
        { id: '6', name: '转出' },
      ],
      outCategoryOptions: [
        { id: '0', name: '付款' },
        { id: '1', name: '支出' },
        { id: '2', name: '转入' },
        { id: '3', name: '转出' },
      ],
      paymentMethodOptions: [
        { id: '0', name: '转账' },
        { id: '1', name: '刷卡' },
        { id: '2', name: '现金' },
        { id: '3', name: '其他' },
      ],
      dialogVisible: false,
      // 先占位，created 时重置为默认表单，避免 render 期 undefined
      form: {
        accountId: null,
        transactionDate: null,
        category: null,
        amount: null,
        subCategory: '',
        paymentMethod: '',
        counterparty: '',
        remark: ''
      },
      counterpartyLoading: false,
      rules: {
        accountId: [{ required: true, message: '请选择收款主体', trigger: 'change' }],
        transactionDate: [{ required: true, message: '请选择交易日期', trigger: 'change' }],
        category: [{ required: true, message: '请选择类别', trigger: 'change' }],
        amount: [{ required: true, message: '请输入交易金额', trigger: 'blur' }],
        counterparty: [{ required: true, message: '请输入交易对方', trigger: 'blur' }]
      }
    }
  },
  created() {
    // 初始化表单，避免首屏取默认值时报未定义
    this.resetFormByData()
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.resetFormByData()
      }
    },
    'form.category'(val) {
      // 当类别切换时，如果当前子类别不在新列表中，则重置为默认
      const list = val === 0 ? this.inCategoryOptions : this.outCategoryOptions
      if (!list.find(item => item.name === this.form.subCategory)) {
        this.form.subCategory = this.getDefaultSubCategory(val)
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    resetFormByData() {
      const base = this.getDefaultForm()
      // 编辑回填
      if (this.editData && typeof this.editData === 'object') {
        const merged = { ...base, ...this.editData }
        // 兼容后端返回的时间格式（字符串/时间戳），统一转换为 yyyy-MM-dd HH:mm:ss 格式字符串
        if (merged.transactionDate) {
          if (merged.transactionDate instanceof Date) {
            merged.transactionDate = this.formatDateTime(merged.transactionDate)
          } else if (typeof merged.transactionDate === 'string') {
            // 如果已经是字符串，检查格式是否为 yyyy-MM-dd HH:mm:ss
            // 如果不是，则转换；如果是，则保持不变（el-date-picker 需要这个格式）
            if (!/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(merged.transactionDate)) {
              const date = new Date(merged.transactionDate)
              if (!isNaN(date.getTime())) {
                merged.transactionDate = this.formatDateTime(date)
              }
            }
          } else if (typeof merged.transactionDate === 'number') {
            // 时间戳
            merged.transactionDate = this.formatDateTime(new Date(merged.transactionDate))
          }
        }
        this.form = merged
      } else {
        this.form = base
      }
      // 默认账户（仅在新增时设置，编辑时使用 editData 中的 accountId）
      if (!this.editData && this.defaultAccountId !== null && this.defaultAccountId !== undefined) {
        this.form.accountId = this.defaultAccountId
      }
      // 修正子类别合法性
      const list = this.form.category === 0 ? this.inCategoryOptions : this.outCategoryOptions
      if (!list.find(item => item.name === this.form.subCategory)) {
        this.form.subCategory = this.getDefaultSubCategory(this.form.category)
      }
    },
    getDefaultForm() {
      const paymentDefault = this.paymentMethodOptions.length ? this.paymentMethodOptions[0].name : ''
      // 格式化日期为 yyyy-MM-dd HH:mm:ss
      const now = new Date()
      const formattedDate = this.formatDateTime(now)
      return {
        accountId: null,
        transactionDate: formattedDate,
        category: 1,
        amount: null,
        subCategory: this.getDefaultSubCategory(1),
        paymentMethod: paymentDefault,
        counterparty: '',
        remark: ''
      }
    },
    getDefaultSubCategory(category) {
      const list = category === 0 ? this.inCategoryOptions : this.outCategoryOptions
      return Array.isArray(list) && list.length ? list[0].name : ''
    },
    handleClose() {
      this.dialogVisible = false
      if (this.$refs.form) {
        this.$refs.form.resetFields()
      }
      this.form = this.getDefaultForm()
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (!valid) return
        // 确保 transactionDate 格式为 yyyy-MM-dd HH:mm:ss
        // 由于 el-date-picker 设置了 value-format="yyyy-MM-dd HH:mm:ss"，
        // 所以 form.transactionDate 应该已经是字符串格式，但为了保险起见，还是做一次检查
        const submitData = { ...this.form }
        if (submitData.transactionDate) {
          if (submitData.transactionDate instanceof Date) {
            // 如果仍然是 Date 对象（理论上不应该发生，因为 value-format 会转换）
            submitData.transactionDate = this.formatDateTime(submitData.transactionDate)
          } else if (typeof submitData.transactionDate === 'string') {
            // 验证格式是否为 yyyy-MM-dd HH:mm:ss，如果不是则转换
            if (!/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(submitData.transactionDate)) {
              const date = new Date(submitData.transactionDate)
              if (!isNaN(date.getTime())) {
                submitData.transactionDate = this.formatDateTime(date)
              } else {
                // 如果无法解析，使用当前时间
                submitData.transactionDate = this.formatDateTime(new Date())
              }
            }
          }
        }
        this.$emit('submit', submitData)
      })
    },
    // 格式化日期时间为 yyyy-MM-dd HH:mm:ss
    formatDateTime(date) {
      if (!date || !(date instanceof Date) || isNaN(date.getTime())) {
        return ''
      }
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    formatCurrency(value) {
      if (value === undefined || value === null || value === '') return ''
      const num = Number(value)
      if (!isFinite(num)) return ''
      return '¥ ' + num.toFixed(2)
    },
    parseCurrency(value) {
      if (!value) return 0
      return Number(String(value).replace(/[^\d.]/g, '')) || 0
    },
    // 仅允许数字和一个小数点
    handleNumberKeydown(e) {
      const allowKeys = ['Backspace', 'Tab', 'Delete', 'ArrowLeft', 'ArrowRight', 'Home', 'End', 'Enter']
      if (allowKeys.includes(e.key) || e.ctrlKey || e.metaKey) {
        return
      }
      const isNumber = /^[0-9]$/.test(e.key)
      const isDot = e.key === '.' && !String(e.target.value).includes('.')
      if (!isNumber && !isDot) {
        e.preventDefault()
      }
    },
    // 粘贴时校验，仅允许数字和小数点
    handleNumberPaste(e) {
      const text = (e.clipboardData || window.clipboardData).getData('text')
      if (!/^\d*(\.\d*)?$/.test(text)) {
        e.preventDefault()
      }
    },
    async queryCounterparty(queryString, cb) {
      const keyword = (queryString || '').trim()
      if (!keyword) {
        cb([])
        return
      }
      this.counterpartyLoading = true
      try {
        const params = {
          companyNameLike: keyword
        }
        const res = await getCompanyListApi(params,{pageNum: 1,pageSize: 10})
        if (res && res.code === 200) {
          const list = res.rows || res.data || []
          const mapped = list.map(item => ({
            value: item.companyName
          }))
          cb(mapped)
        } else {
          cb([{ value: keyword }])
        }
      } catch (err) {
        console.error('获取交易对方候选失败:', err)
        cb([{ value: keyword }])
      } finally {
        this.counterpartyLoading = false
      }
    },
    handleCounterpartySelect(item) {
      this.form.counterparty = item?.value || ''
    }
  },
  computed: {
    // 根据是否有 editData 动态切换弹窗标题
    dialogTitle() {
      return this.editData ? '编辑流水记录' : '新增流水记录'
    },
    subCategoryOptions() {
      return this.form.category === 0 ? this.inCategoryOptions : this.outCategoryOptions
    },
    currentPayerName() {
      if (!this.form.accountId) return ''
      const target = this.payerOptions.find(p => p.id === this.form.accountId)
      return target ? target.payName : ''
    }
  }
}
</script>

<style scoped>
.transaction-dialog__summary {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 10px 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.summary-item .label {
  color: #909399;
  margin-right: 4px;
}

.summary-item .value {
  color: #303133;
}

.summary-item .value--income {
  color: #67c23a;
}

.summary-item .value--expense {
  color: #f56c6c;
}

.transaction-form {
  margin-top: 8px;
}

.form-grid {
  display: flex;
  gap: 16px;
}

.form-col {
  flex: 1;
}

.full-width {
  width: 100%;
}

.amount-input :deep(.el-input-number__decrease),
.amount-input :deep(.el-input-number__increase) {
  display: none;
}

.dialog-footer {
  text-align: right;
}
</style>

