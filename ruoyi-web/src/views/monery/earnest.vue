<template>
  <div class="earnest-management">
    <!-- 更高级金融感Header卡片 -->
    <div class="earnest-header-card">
      <div class="header-left">
        <div class="header-icon"><i class="el-icon-wallet" /></div>
        <div>
          <div class="header-title">保证金账户</div>
          <div class="header-desc">
            资金安全保障，充值实时到账，支持多业务结算
          </div>
        </div>
      </div>
      <!-- 当前余额和充值 -->
      <div class="current-balance better-ui">
        <div class="balance-value-main">
          <span class="cny-symbol">¥</span>
          <span class="main-amount">{{ capitalMoner || 0 }}</span>
        </div>
        <div class="balance-sub-label">当前余额 (元)</div>
      </div>
      <div class="header-actions">
        <el-button
          class="main-btn"
          type="primary"
          icon="el-icon-credit-card"
          @click="showRechargeDialog"
        >充值</el-button>
        <el-button
          class="normal-btn"
          type="default"
          icon="el-icon-document"
          :loading="exportLoading"
          @click="handleExport"
        >导出流水</el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-item">
          <label>记录时间:</label>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            :picker-options="{ disabledDate: time => time.getTime() > Date.now() }"
            style="width: 260px"
          />
        </div>
        <div class="filter-item">
          <label>业务名称:</label>
          <el-select
            v-model="type"
            placeholder="请选择业务名称"
            style="width: 180px"
          >
            <el-option label="全部" value="" />
            <el-option
              v-for="option in typeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>
        <!-- <div class="filter-item">
          <label>业务单号:</label>
          <el-input
            v-model.trim="orderNumber"
            placeholder="请输入业务单号"
            style="width: 200px"
          />
        </div> -->
        <div class="filter-actions">
          <el-button
            type="primary"
            size="small"
            @click="handleSearch"
          >搜索</el-button>
          <el-button size="small" @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 交易记录表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="transactionRecords"
        style="width: 100%; font-size: 14px"
        height="100%"
        size="small"
        :cell-style="{ padding: '8px 0' }"
        :header-cell-style="{
          background: '#f7f8fa',
          color: '#606266',
          fontWeight: 600,
          fontSize: '18px',
        }"
        border
        stripe
        class="order-table"
      >
        <!-- 空数据状态 -->
        <template slot="empty">
          <EmptyState text="暂无交易记录" /> </template>
        <el-table-column
          prop="orderNo"
          label="业务单号"
          min-width="200"
          align="center"
        />
        <el-table-column
          prop="outAmount"
          label="出项"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <span
              v-if="scope.row.outAmount"
              :class="
                scope.row.outAmount > 0 ? 'amount-positive' : 'amount-negative'
              "
            >
              {{ scope.row.outAmount }}
            </span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="addAmount"
          label="进项"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <span
              v-if="scope.row.addAmount"
              :class="
                scope.row.addAmount > 0 ? 'amount-positive' : 'amount-negative'
              "
            >
              {{ scope.row.addAmount }}
            </span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="subtotal"
          label="小计"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <span
              :class="
                scope.row.subtotal > 0 ? 'amount-positive' : 'amount-negative'
              "
            >
              {{ scope.row.subtotal }}
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="availableAmount"
          label="账户余额"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <span
              class="balance-amount"
            >¥ {{ scope.row.availableAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="type"
          label="说明"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag
              :type="getBusinessTypeTagByValue(scope.row.type)"
              size="small"
            >
              {{ getBusinessTypeName(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-section">
        <el-pagination
          :current-page="pagination.pageNum"
          :page-sizes="[30, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          background
          class="custom-pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 充值弹框 -->
    <el-dialog
      title="输入充值金额"
      :visible.sync="amountInputDialogVisible"
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="resetAmountDialog"
    >
      <div class="amount-step-indicator">
        <div class="label active">输入金额</div>
        <div class="step-circles">
          <span class="circle active">1</span>
          <span class="line" />
          <span class="circle">2</span>
        </div>
        <div class="label">扫码支付</div>
      </div>

      <div class="amount-form-main beautiful">
        <div class="input-area-wrap">
          <span class="input-cny">¥</span>
          <el-input
            ref="amountInput"
            v-model.trim="rechargeAmount"
            class="amount-input-modern"
            placeholder="请输入充值金额，至少50元"
            type="text"
            inputmode="decimal"
            :class="{ 'has-error': !!inputErrorMsg }"
            @focus="handleFocus"
            @input="handleAmountInput"
            @keyup.enter.native="handleAmountNext"
          />
        </div>
        <transition name="error-shake">
          <div v-if="inputErrorMsg" class="helper-error">
            {{ inputErrorMsg }}
          </div>
        </transition>
        <div class="quick-btns">
          <el-tag
            v-for="n in [50, 100, 500, 1000]"
            :key="n"
            class="quick-chip"
            @click="quickSelectAmount(n)"
          >￥{{ n }}</el-tag>
        </div>
      </div>

      <div class="actions">
        <el-button
          class="cacle-action"
          @click="amountInputDialogVisible = false"
        >取消</el-button>
        <el-button
          type="primary"
          class="main-action"
          @click="handleAmountNext"
        >下一步</el-button>
      </div>
    </el-dialog>
    <el-dialog
      title="充值二维码"
      :visible.sync="qrCodeDialogVisible"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="resetAmountDialog"
    >
      <div class="amount-step-indicator">
        <div class="label">输入金额</div>
        <div class="step-circles">
          <span class="circle completed">1</span>
          <span class="line" />
          <span class="circle active">2</span>
        </div>
        <div class="label active">扫码支付</div>
      </div>
      <div class="recharge-dialog-content">
        <div class="recharge-info">
          <div class="recharge-amount">
            <span class="label">充值金额：</span>
            <span class="amount">¥ {{ rechargeAmount || "0.00" }}</span>
          </div>
          <div class="recharge-tip">
            <i class="el-icon-info" />
            <span>请使用微信扫描下方二维码完成支付</span>
          </div>
        </div>
        <div class="qr-code-section">
          <div class="qr-code-container">
            <!-- 二维码加载中 -->
            <div v-if="qrcodeLoading" class="qr-code-loading">
              <el-icon class="is-loading">
                <i class="el-icon-loading" />
              </el-icon>
              <p>正在生成二维码...</p>
            </div>
            <!-- 二维码显示 -->
            <div v-else-if="qrcode && !qrcodeError" class="qr-code-image">
              <img :src="qrcode" alt="支付二维码">
              <div class="qr-code-overlay">
                <el-button
                  class="refresh-btn"
                  type="text"
                  size="small"
                  @click="getCapitalQrcode"
                >
                  <i class="el-icon-refresh" />
                  刷新二维码
                </el-button>
              </div>
            </div>
            <!-- 二维码生成失败 -->
            <div v-else-if="qrcodeError" class="qr-code-error">
              <i class="el-icon-warning" />
              <p>二维码生成失败</p>
              <el-button
                class="retry-btn"
                type="primary"
                size="small"
                @click="getCapitalQrcode"
              >
                <i class="el-icon-refresh" />
                重新生成
              </el-button>
            </div>
            <!-- 默认占位 -->
            <div v-else class="qr-code-placeholder">
              <i class="el-icon-picture-outline" />
              <p>等待生成二维码...</p>
            </div>
          </div>
          <div class="qr-code-tips">
            <p>1. 请确保充值金额正确</p>
            <p>2. 支付完成后，余额将自动到账</p>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="qrCodeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRecharge">支付完成</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import EmptyState from '@/components/EmptyState'
import {
  getCapitalInfo,
  getCapitalList,
  exportTransactionRecords,
  payCapital,
  getPayStatus,
  getCapitalTypeInfo
} from '@/api/monery'
import { toQrcodeOnline } from '@/utils/qrcode'
export default {
  name: 'EarnestManagement',
  components: {
    EmptyState
  },
  data() {
    return {
      typeOptions: [],
      loading: false,
      capitalMoner: 0,
      rechargeAmount: '',
      dateRange: this.getDefaultDateRange(),
      type: '',
      orderNumber: '',
      pagination: {
        pageNum: 1,
        pageSize: 30,
        total: 0
      },
      rechargeDialogVisible: false,
      exportLoading: false,
      transactionRecords: [],
      qrcode: '', // 二维码base64数据
      tradeNo: '', // 交易号
      qrcodeLoading: false, // 二维码加载状态
      qrcodeError: false, // 二维码生成错误状态
      amountInputDialogVisible: false,
      qrCodeDialogVisible: false,
      inputErrorMsg: '',
      suppressAutoSelect: false
    }
  },
  created() {
    this.getCapitalInfo() // 获取保证金信息
    this.getCapitalList() // 获取保证金列表
    this.getCapitalTypeInfo() // 获取保证金类型
  },
  methods: {
    async getCapitalTypeInfo() {
      const res = await getCapitalTypeInfo()
      console.log('获取保证金类型', res)
      if (res.code === 200) {
        // 将后端返回的数据转换为数组格式
        this.typeOptions = Object.keys(res.data).map((key) => ({
          label: res.data[key],
          value: parseInt(key)
        }))
      }
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.getCapitalList()
    },
    handleCurrentChange(current) {
      this.pagination.pageNum = current
      this.getCapitalList()
    },
    showRechargeDialog() {
      this.rechargeAmount = ''
      this.qrcode = ''
      this.qrcodeError = false
      this.qrcodeLoading = false
      this.tradeNo = ''
      this.amountInputDialogVisible = true
      this.qrCodeDialogVisible = false
    },
    handleAmountInput(value) {
      // 实时校验输入格式
      if (value && !/^\d+(\.\d{1,2})?$/.test(value)) {
        // 如果格式不正确，自动修正
        const corrected = value
          .replace(/[^\d.]/g, '')
          .replace(/\.{2,}/g, '.')
          .replace(/(\..*)\./g, '$1')
        if (corrected !== value) {
          this.$nextTick(() => {
            this.rechargeAmount = corrected
          })
        }
      }
    },
    handleAmountNext() {
      // 金额输入弹窗校验（在弹框内友好提示）
      const amount = parseFloat(this.rechargeAmount)
      this.inputErrorMsg = ''
      if (!this.rechargeAmount) {
        this.inputErrorMsg = '请输入充值金额'
        return
      }
      if (isNaN(amount) || amount <= 0) {
        this.inputErrorMsg = '请输入有效的充值金额'
        return
      }
      if (amount < 50) {
        this.inputErrorMsg = '充值金额不能少于50元'
        return
      }
      if (amount > 100000) {
        this.inputErrorMsg = '单次充值金额不能超过10万元'
        return
      }
      if (!/^\d+(\.\d{1,2})?$/.test(this.rechargeAmount)) {
        this.inputErrorMsg = '金额格式不正确，最多保留两位小数'
        return
      }
      // 先打开二维码弹窗，再关闭金额弹窗，避免遮罩闪烁
      this.qrCodeDialogVisible = true
      // 重置二维码状态并生成
      this.qrcode = ''
      this.qrcodeError = false
      this.qrcodeLoading = false
      this.tradeNo = ''
      this.$nextTick(() => {
        this.amountInputDialogVisible = false
        this.getCapitalQrcode()
      })
    },
    /**
     * 获取保证金二维码
     */
    async getCapitalQrcode() {
      // 重置状态
      this.qrcodeLoading = true
      this.qrcodeError = false
      this.qrcode = ''

      try {
        const res = await payCapital(
          {
            amount: Number(this.rechargeAmount),
            tradeType: 1 // 支付类型 1.二维码支付 2.JSAPI支付 3.小程序支付
          },
          2
        ) // type: 1 // 支付方式 1.模拟支付 2.微信支付

        console.log('获取保证金二维码', res)

        if (res.code === 200 && res.data?.data) {
          // 保存交易号
          this.tradeNo = res.data?.tradeNo || ''

          // 生成二维码
          try {
            const qrcodeDataURL = await toQrcodeOnline(res.data.data, {
              size: 200,
              margin: 2,
              color: {
                dark: '#000000',
                light: '#FFFFFF'
              }
            })
            this.qrcode = qrcodeDataURL
            console.log('二维码生成成功')
          } catch (qrcodeError) {
            console.error('二维码生成失败:', qrcodeError)
            this.qrcodeError = true
            this.$message.error('二维码生成失败，请重试')
          }
        } else {
          this.qrcodeError = true
          this.$message.error('获取支付信息失败，请重试')
        }
      } catch (error) {
        console.error('获取支付二维码失败:', error)
        this.qrcodeError = true
        this.$message.error('网络错误，请重试')
      } finally {
        this.qrcodeLoading = false
      }
    },
    /**
     * 支付保证金
     */
    async confirmRecharge() {
      if (!this.rechargeAmount) {
        this.$message.warning('请输入充值金额')
        return
      }

      const amount = parseFloat(this.rechargeAmount)
      if (isNaN(amount) || amount <= 0) {
        this.$message.warning('请输入有效的充值金额')
        return
      }

      if (amount < 50) {
        this.$message.warning('充值金额不能少于50元')
        return
      }

      if (amount > 100000) {
        this.$message.warning('单次充值金额不能超过10万元')
        return
      }

      if (!/^\d+(\.\d{1,2})?$/.test(this.rechargeAmount)) {
        this.$message.warning('充值金额格式不正确，最多保留两位小数')
        return
      }

      if (!this.tradeNo) {
        this.$message.warning('交易号获取失败,请刷新页面重试')
        return
      }

      const res = await getPayStatus(this.tradeNo)
      console.log('获取支付状态', res)
      if (res.code === 200) {
        this.payStatus = res.data
        this.getCapitalInfo()
        this.getCapitalList()
        this.$message.success('支付完成')

        // 重置状态
        this.qrCodeDialogVisible = false
        this.rechargeDialogVisible = false
        this.rechargeAmount = ''
        this.qrcode = ''
        this.qrcodeError = false
        this.qrcodeLoading = false
        this.tradeNo = ''
      } else {
        this.$message.error('支付状态查询失败，请重试')
      }
    },
    /**
     * 导出保证金交易记录
     */
    async handleExport() {
      try {
        this.exportLoading = true

        // 构建导出参数
        const exportParams = {
          startDate: this.dateRange && this.dateRange[0] ? this.formatDateToSlash(this.dateRange[0]) : '',
          endDate: this.dateRange && this.dateRange[1] ? this.formatDateToSlash(this.dateRange[1]) : '',
          type: this.type
        }

        // 调用导出API
        const response = await exportTransactionRecords(exportParams)

        // 创建下载链接
        const blob = new Blob([response], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url

        // 生成文件名
        const now = new Date()
        const timestamp =
          now.getFullYear() +
          String(now.getMonth() + 1).padStart(2, '0') +
          String(now.getDate()).padStart(2, '0') +
          '_' +
          String(now.getHours()).padStart(2, '0') +
          String(now.getMinutes()).padStart(2, '0')
        link.download = `保证金交易记录_${timestamp}.xlsx`

        // 触发下载
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        this.$message.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败，请稍后重试')
      } finally {
        this.exportLoading = false
      }
    },
    formatDateToSlash(value) {
      if (!value) return ''
      const date = value instanceof Date ? value : new Date(value)
      if (Number.isNaN(date.getTime())) return ''
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    /**
     * 获取保证金信息
     */
    async getCapitalInfo() {
      const res = await getCapitalInfo()
      console.log('获取保证金信息', res)
      if (res.code === 200) {
        this.capitalMoner = res.data
      }
    },
    /**
     * 获取保证金列表
     */
    async getCapitalList() {
      const res = await getCapitalList(
        {
          startDate: this.dateRange && this.dateRange[0] ? this.formatDateToSlash(this.dateRange[0]) : '',
          endDate: this.dateRange && this.dateRange[1] ? this.formatDateToSlash(this.dateRange[1]) : '',
          type: this.type
        },
        {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        }
      )
      console.log('获取保证金列表', res)
      if (res.code === 200) {
        this.transactionRecords = res.rows
        this.pagination.total = res.total
      }
    },
    /**
     * 搜索
     */
    handleSearch() {
      this.pagination.pageNum = 1
      this.getCapitalList()
    },
    /**
     * 重置
     */
    handleReset() {
      this.dateRange = this.getDefaultDateRange()
      this.type = ''
      this.pagination.pageNum = 1
      this.pagination.pageSize = 30
      this.getCapitalList()
    },
    /**
     * 获取业务类型名称
     */
    getBusinessTypeName(type) {
      // 从 typeOptions 数组中查找对应的标签
      const option = this.typeOptions.find((item) => item.value === type)
      return option ? option.label : '未知'
    },
    /**
     * 根据 typeOptions 的名称动态匹配UI样式（不写死枚举）
     * 传入后端返回的 value，根据本地 typeOptions 找到对应 label 后匹配样式
     */
    getBusinessTypeTagByValue(typeValue) {
      const option = this.typeOptions.find((item) => item.value === typeValue)
      const name = option ? String(option.label) : ''
      if (!name) return 'info'

      // 规则：
      // - 含“充”或“充值” => success
      // - 含“退”或“退款/退回” => primary
      // - 含“扣/费/罚” => danger
      // - 含“订单/交易/消费” => info
      // - 其它 => warning
      if (/充/.test(name)) return 'success'
      if (/退/.test(name)) return 'primary'
      if (/[扣费罚]/.test(name)) return 'danger'
      if (/(订单|交易|消费)/.test(name)) return 'info'
      return 'warning'
    },
    /**
     * 获取默认日期范围（当月第一天到今天）
     */
    getDefaultDateRange() {
      const now = new Date()
      const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
      const today = new Date()

      const formatDate = (date) => {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }

      return [formatDate(firstDay), formatDate(today)]
    },
    resetAmountDialog() {
      this.inputErrorMsg = ''
    },
    quickSelectAmount(n) {
      this.rechargeAmount = String(n)
      this.$nextTick(() => {
        const inputEl =
          this.$refs.amountInput &&
          this.$refs.amountInput.$el &&
          this.$refs.amountInput.$el.querySelector('input')
        if (inputEl) {
          this.suppressAutoSelect = true
          inputEl.focus()
          const len = String(this.rechargeAmount).length
          if (typeof inputEl.setSelectionRange === 'function') {
            inputEl.setSelectionRange(len, len)
          }
          this.suppressAutoSelect = false
        }
      })
    },
    handleFocus(e) {
      if (this.suppressAutoSelect) return
      if (e && e.target && e.target.select) e.target.select()
    }
  }
}
</script>

<style lang="scss" scoped>
// 旋转动画
@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.earnest-management {
  padding: 20px;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 130px);
  overflow-y: auto;

  .earnest-header-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 16px 0 rgba(74, 127, 255, 0.07),
      0 1.5px 8px rgba(0, 0, 0, 0.03);
    padding: 20px 36px 8px 36px;
    margin-bottom: 12px;

    .header-left {
      display: flex;
      align-items: center;

      .header-icon {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        background: linear-gradient(130deg, #63a1fd 0%, #5edfff 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 23px;
        box-shadow: 0 2px 16px #abbfff33, 0 1px 1.5px #99e2ef22;

        i {
          font-size: 32px;
          color: #fff;
        }
      }

      .header-title {
        font-size: 26px;
        font-weight: bold;
        color: #2c365a;
        letter-spacing: 0.5px;
        margin-bottom: 6px;
      }

      .header-desc {
        color: #8c99b5;
        font-size: 14px;
        letter-spacing: 0.5px;
      }
    }

    .current-balance {
      display: flex;
      align-items: center;
      justify-content: flex-end;

      .balance-amount {
        font-size: 22px;
        font-weight: 500;
        color: #303133;
      }

      .balance-label {
        margin-left: 10px;
        font-size: 12px;
        color: #606266;
      }
    }

    .current-balance.better-ui {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      justify-content: center;
      min-width: 180px;
      padding: 6px 30px 6px 18px;
      background: rgba(195, 232, 255, 0.2);
      border-radius: 16px;
      box-shadow: 0 2px 10px #9acfff15;
      position: relative;

      .balance-value-main {
        display: flex;
        align-items: flex-end;
        font-size: 30px;
        font-weight: 800;
        color: #227cff;
        letter-spacing: 2px;

        .cny-symbol {
          font-size: 22px;
          font-weight: 500;
          opacity: 0.75;
          margin-bottom: 3px;
          margin-right: 2px;
          color: #67b4fe;
        }

        .main-amount {
          background: linear-gradient(90deg, #3a8cff 8%, #5edfff 92%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          font-size: 38px;
          font-family: "DIN Alternate", "Microsoft Yahei", sans-serif;
          line-height: 1;
        }
      }

      .balance-sub-label {
        margin-top: 6px;
        font-size: 13px;
        color: #86a2c7;
        letter-spacing: 0.5px;
        opacity: 0.87;
        font-weight: 500;
      }
    }

    .header-actions {
      display: flex;
      gap: 18px;

      .main-btn {
        padding: 0 28px;
        height: 38px;
        background: linear-gradient(95deg, #4b79fd 0%, #38c3fa 80%);
        color: #fff;
        font-size: 16px;
        border-radius: 18px;
        font-weight: 500;
        border: none;
        box-shadow: 0 2px 8px #99e2ef43;
      }

      .normal-btn {
        padding: 0 22px;
        height: 38px;
        border-radius: 18px;
        font-size: 16px;
        color: #637ca1;
        background: #f3f6fa;
        border: none;

        &:hover {
          background: #e8f6ff;
          color: #3d4d80;
        }
      }
    }
  }

  .balance-section {
    margin-bottom: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .filter-section {
    margin-bottom: 12px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);

    .filter-row {
      display: flex;
      align-items: center;
      gap: 20px;

      .filter-item {
        display: flex;
        align-items: center;
        gap: 8px;

        label {
          font-size: 14px;
          color: #606266;
          white-space: nowrap;
        }
      }

      .filter-actions {
        flex: 1;
        display: flex;
        gap: 10px;
        justify-content: flex-end;
      }
    }
  }

  .table-section {
    flex: 1;
    overflow: auto;
    display: flex;
    flex-direction: column;
    height: 100%;

    .pagination-section {
      display: flex;
      justify-content: right;
      background: white;
      padding: 10px 24px;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      .custom-pagination {
        .el-pagination__total {
          color: #606266;
          font-size: 14px;
        }

        .el-pagination__sizes {
          .el-select .el-input__inner {
            font-size: 14px;
          }
        }

        .el-pager li {
          font-size: 14px;
        }

        .el-pagination__jump {
          color: #606266;
          font-size: 14px;

          .el-input__inner {
            font-size: 14px;
          }
        }
      }
    }
  }

  .amount-positive {
    color: #13ce66;
    font-weight: 500;
  }

  .amount-negative {
    color: #ff4949;
    font-weight: 500;
  }

  .balance-amount {
    font-weight: 600;
    color: #303133;
  }
}

// 充值弹框样式
.recharge-dialog-content {
  .recharge-info {
    margin-bottom: 30px;

    .recharge-amount {
      display: flex;
      align-items: center;
      margin-bottom: 15px;

      .label {
        font-size: 16px;
        color: #606266;
        margin-right: 10px;
      }

      .amount {
        font-size: 24px;
        font-weight: 600;
        color: #FF3B30;
      }
    }

    .recharge-tip {
      display: flex;
      align-items: center;
      padding: 12px;
      background: #f0f9ff;
      border: 1px solid #b3d8ff;
      border-radius: 4px;
      color: #1677FF;

      i {
        margin-right: 8px;
        font-size: 16px;
      }
    }
  }

  .qr-code-section {
    text-align: center;

    .qr-code-container {
      margin-bottom: 20px;

      .qr-code-placeholder {
        width: 200px;
        height: 200px;
        margin: 0 auto;
        border: 2px dashed #dcdfe6;
        border-radius: 4px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #909399;

        i {
          font-size: 48px;
          margin-bottom: 10px;
        }

        p {
          margin: 0;
          font-size: 14px;
        }
      }

      .qr-code-loading {
        width: 200px;
        height: 200px;
        margin: 0 auto;
        border: 2px dashed #1677FF;
        border-radius: 4px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #1677FF;
        background: rgba(64, 158, 255, 0.05);

        .el-icon-loading {
          font-size: 32px;
          margin-bottom: 10px;
          animation: rotating 2s linear infinite;
        }

        p {
          margin: 0;
          font-size: 14px;
        }
      }

      .qr-code-image {
        width: 200px;
        height: 200px;
        margin: 0 auto;
        border-radius: 4px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        position: relative;

        img {
          width: 100%;
          height: 100%;
          object-fit: contain;
        }

        .qr-code-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          opacity: 0;
          transition: opacity 0.3s ease;

          .refresh-btn {
            color: #fff;
            font-size: 12px;

            &:hover {
              color: #1677FF;
            }
          }
        }

        &:hover .qr-code-overlay {
          opacity: 1;
        }
      }

      .qr-code-error {
        width: 200px;
        height: 200px;
        margin: 0 auto;
        border: 2px dashed #FF3B30;
        border-radius: 4px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #FF3B30;
        background: rgba(245, 108, 108, 0.05);

        i {
          font-size: 32px;
          margin-bottom: 10px;
        }

        p {
          margin: 0 0 10px 0;
          font-size: 14px;
        }

        .retry-btn {
          font-size: 12px;
        }
      }
    }

    .qr-code-tips {
      text-align: left;
      background: #f8f9fa;
      padding: 15px;
      border-radius: 4px;

      p {
        margin: 5px 0;
        font-size: 14px;
        color: #606266;
        line-height: 1.5;
      }
    }
  }
}

.dialog-footer {
  text-align: right;
}

.amount-dialog {
  padding: 6px 6px 0 6px;

  .stepper {
    display: flex;
    align-items: center;
    justify-content: center;

    .step {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #e9eef5;
      color: #8aa1c1;
      font-weight: 600;

      &.active {
        background: #1677FF;
        color: #fff;
      }
    }

    .line {
      width: 120px;
      height: 2px;
      background: #e6edf7;
      margin: 0 10px;
    }
  }

  .step-labels {
    display: flex;
    justify-content: space-between;
    margin: 6px 18px 18px;

    .label {
      color: #9aa9bf;
      font-size: 12px;
    }

    .label.active {
      color: #53627a;
      font-weight: 600;
    }
  }

  .amount-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;

    .field {
      .field-label {
        font-size: 13px;
        color: #606266;
        margin-bottom: 6px;
      }

      .helper-text {
        margin-top: 6px;
        font-size: 12px;
        color: #9aa9bf;
      }
    }

    .quick-amounts {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-top: 6px;

      .qa-label {
        color: #7a8ba6;
        font-size: 12px;
        margin-right: 2px;
      }

      .qa-chip {
        cursor: pointer;
        background: #f3f6fa;
        border: none;
        color: #53627a;
      }

      .qa-chip:hover {
        background: #e8f6ff;
        color: #3d4d80;
      }
    }
  }
}

.amount-step-indicator {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;

  .label {
    font-size: 16px;
    color: #8aa1c1;
  }

  .label.active {
    color: #377aff;
    font-weight: bold;
  }

  .step-circles {
    display: flex;
    align-items: center;
    gap: 7px;

    .circle {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #e5ecfb;
      color: #8cbfff;
      font-weight: bold;
      font-size: 17px;
      display: flex;
      align-items: center;
      justify-content: center;

      &.active {
        background: linear-gradient(135deg, #34c5ff 0%, #377aff 83%);
        color: #fff;
        box-shadow: 0 2px 7px #60e0fa50;
      }

      &.completed {
        background: #e0f4ff;
        color: #377aff;
        border: 2px solid #51bcff;
      }
    }

    .line {
      width: 44px;
      height: 2.5px;
      background: #e0e9f7;
    }
  }
}

.amount-form-main {
  text-align: center;
  margin-top: 40px;
  .form-label {
    color: #3a6ec0;
    font-size: 15px;
    margin-bottom: 9px;
    font-weight: 600;
  }
}

.amount-input-modern {
  width: 232px;
  font-size: 20px;
  font-family: "DIN Alternate", "Microsoft Yahei", sans-serif;
  font-weight: bold;
  border-radius: 4px;
  border: 1.5px solid #dbeafe;
  padding: 7px 0 7px;
  text-align: left;
  transition: border 0.16s;
}

.amount-input-modern.el-input--focus,
.amount-input-modern:focus {
  border-color: #4cbaff;
}

.amount-input-modern.has-error {
  border-color: #ff4c4c;
  animation: errorShake 0.28s;
}

.amount-input-modern input {
  font-size: 20px;
}

.helper-error {
  color: #ff4c4c;
  font-size: 13px;
  text-align: left;
  margin: 4px 0 0 12px;
}

.quick-btns {
  margin-top: 7px;
  text-align: center;
}

.quick-chip {
  background: #f3f6fa;
  color: #377aff;
  border-radius: 9px;
  font-weight: 600;
  padding: 0 12px;
  font-size: 16px;
  margin: 0 7px;
  cursor: pointer;
  border: none;
}

.quick-chip:hover {
  background: #e3f4ff;
  color: #125fd8;
}

.actions {
  text-align: right;
  margin-top: 40px;

  .cacle-action {
    border-radius: 15px;
  }

  .main-action {
    background: linear-gradient(93deg, #47b5fa 0%, #377aff 100%);
    color: #fff;
    border-radius: 15px;
    font-size: 17px;
    border: none;
    box-shadow: 0 2px 12px #bfdfff31;
  }
}

@keyframes errorShake {
  10%,
  90% {
    transform: translateX(-2px);
  }

  20%,
  80% {
    transform: translateX(2px);
  }

  30%,
  50%,
  70% {
    transform: translateX(-6px);
  }

  40%,
  60% {
    transform: translateX(6px);
  }
}

.error-shake-enter-active {
  animation: errorShake 0.25s;
}

.amount-form-main.beautiful {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 12px;

  .input-area-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fafdff;
    border-radius: 13px;
    box-shadow: 0 2px 8px #c0dbfb1c;
    border: 2px solid #e5ebf5;
    height: 52px;
    width: 90%;
    margin-bottom: 16px;
    transition: border-color 0.18s;

    &:focus-within {
      border: 2px solid #51bcff;
      box-shadow: 0 2px 14px #bcdfff18;
    }

    .input-cny {
      color: #44aaff;
      font-size: 20px;
      font-weight: 800;
      margin: 0 12px 0 16px;
    }

    .amount-input-modern {
      border: none;
      background: transparent;
      font-size: 14px;
      font-weight: bold;
      color: #222;
      text-align: right;
      letter-spacing: 2px;
      width: 100%;
      padding-right: 10px;
      box-shadow: none;

      &.has-error {
        color: #ff4c4c;
      }
    }

    input {
      border: none !important;
      background: transparent !important;
      box-shadow: none !important;
      text-align: right;
    }

    .el-input__inner {
      font-size: 20px;
      font-weight: bold;
      color: #222;
      background: transparent;
      border: none;
      text-align: right;
    }
  }

  .helper-error {
    color: #ff4c4c;
    font-size: 15px;
    text-align: center;
    margin: 8px 0 4px 0;
    min-height: 24px;
  }

  .quick-btns {
    margin: 18px 0 2px 0;
    text-align: center;
  }
}
</style>
