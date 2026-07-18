<template>
  <div class="payment-details">
    <!-- 页面头部 -->
    <div class="page-header-card">
      <div class="header-left">
        <div class="header-icon"><i class="el-icon-money" /></div>
        <div>
          <div class="header-title">收款详情</div>
          <div class="header-desc">查看和管理所有收款记录，支持多维度筛选</div>
        </div>
      </div>
      <!-- Tab切换 先删除-->
      <!-- <div class="tab-section">
        <div class="tab-container">
          <div
            v-for="tab in tabs"
            :key="tab.value"
            class="tab-item"
            :class="{ active: activeTab === tab.value }"
            @click="handleTabChange(tab.value)"
          >
            {{ tab.label }}
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button
          class="normal-btn"
          type="default"
          icon="el-icon-document"
          @click="handleExportAll"
        >导出流水</el-button>
      </div> -->
    </div>

    <!-- 筛选条件 -->
    <div v-if="activeTab !== 'frozen'" class="filter-section">
      <!-- 扣罚订单明细的筛选条件 -->
      <template v-if="activeTab === 'penalty'">
        <div class="filter-row">
          <div class="filter-item">
            <label>扣罚单号:</label>
            <el-input
              v-model.trim="penaltyFilters.penaltyOrderNumber"
              placeholder="请输入"
              clearable
              style="width: 180px"
            />
          </div>
          <div class="filter-item">
            <label>创建时间:</label>
            <el-date-picker
              v-model="penaltyFilters.createTime"
              type="datetimerange"
              range-separator="至"
              start-placeholder="请选择开始时间"
              end-placeholder="请选择结束时间"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              style="width: 360px"
            />
          </div>
          <div class="filter-item">
            <label>无仓单号:</label>
            <el-input
              v-model.trim="penaltyFilters.noWarehouseOrderNumber"
              placeholder="请输入"
              clearable
              style="width: 180px"
            />
          </div>
          <div class="filter-item">
            <label>扣罚主体:</label>
            <el-input
              v-model.trim="penaltyFilters.penaltySubject"
              placeholder="请输入"
              clearable
              style="width: 180px"
            />
          </div>
          <div class="filter-item">
            <label>错误归类:</label>
            <el-select
              v-model="penaltyFilters.errorCategory"
              placeholder="请选择"
              clearable
              style="width: 180px"
            >
              <el-option label="归类1" value="category1" />
              <el-option label="归类2" value="category2" />
            </el-select>
          </div>
          <div class="filter-item">
            <label>错误详情:</label>
            <el-select
              v-model="penaltyFilters.errorDetails"
              placeholder="请选择"
              clearable
              style="width: 180px"
            >
              <el-option label="详情1" value="detail1" />
              <el-option label="详情2" value="detail2" />
            </el-select>
          </div>
          <div class="filter-item">
            <label>扣罚状态:</label>
            <el-select
              v-model="penaltyFilters.penaltyStatus"
              placeholder="请选择"
              clearable
              style="width: 180px"
            >
              <el-option label="待处理" value="pending" />
              <el-option label="已处理" value="processed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </div>
          <div class="filter-item">
            <label>扣款凭证:</label>
            <el-select
              v-model="penaltyFilters.deductionVoucher"
              placeholder="请选择扣款凭证"
              clearable
              style="width: 180px"
            >
              <el-option label="有凭证" value="yes" />
              <el-option label="无凭证" value="no" />
            </el-select>
          </div>
          <div class="filter-item">
            <label>凭证号:</label>
            <el-input
              v-model.trim="penaltyFilters.voucherNumber"
              placeholder="请输入凭证号"
              clearable
              style="width: 180px"
            />
          </div>
          <div class="filter-actions">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
        </div>
      </template>
      <!-- 其他标签页的筛选条件 -->
      <template v-else>
        <div class="filter-row">
          <template v-if="activeTab === 'all' || activeTab === 'abnormal' || activeTab === 'unconfirmed'">
            <!-- 全部状态下：付款时间和采购方 -->
            <div class="filter-item">
              <label>付款时间:</label>
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
            <!-- <div class="filter-item">
              <label>采购方:</label>
              <el-select
                v-model="payerEntity"
                placeholder="请选择付款主体"
                clearable
                filterable
                style="width: 260px"
              >
                <el-option
                  v-for="item in payerEntityOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </div> -->
          </template>
          <template v-else>
            <!-- 其他状态下：显示原来的筛选条件 -->
            <div class="filter-item">
              <label>付款时间:</label>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                style="width: 260px"
              />
            </div>
            <div class="filter-item">
              <label>账户类型:</label>
              <el-select
                v-model="accountType"
                placeholder="请选择账户类型"
                style="width: 180px"
              >
                <el-option label="全部" value="" />
                <el-option label="对公账户" value="corporate" />
                <el-option label="对私账户" value="private" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>确认状态:</label>
              <el-select
                v-model="confirmStatus"
                placeholder="请选择确认状态"
                style="width: 180px"
              >
                <el-option label="全部" value="" />
                <el-option label="已确认" value="confirmed" />
                <el-option label="待确认" value="pending" />
                <el-option label="异常" value="abnormal" />
              </el-select>
            </div>
          </template>
          <div class="filter-actions">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
        </div>
      </template>
    </div>

    <!-- 数据表格 -->
    <div v-if="activeTab === 'penalty'" class="table-section penalty-table">
      <!-- 批量操作栏 -->
      <div class="batch-actions-bar">
        <span class="selected-count">已选择{{ selectedRows.length }}条记录</span>
        <div class="batch-buttons">
          <el-button type="danger" @click="handleApplyVoucher">申请凭证</el-button>
          <el-button @click="handleCancelSelection">取消</el-button>
        </div>
      </div>
      <el-table
        ref="penaltyTable"
        v-loading="loading"
        :data="tableData"
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
        @selection-change="handleSelectionChange"
      >
        <template slot="empty">
          <EmptyState text="暂无数据" />
        </template>
        <el-table-column
          type="selection"
          width="55"
        />
        <el-table-column
          prop="penaltyOrderNumber"
          label="扣罚单号"
          width="150"
          align="center"
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="200"
          align="center"
        />
        <el-table-column
          prop="noWarehouseOrderNumber"
          label="无仓单号"
          width="150"
          align="center"
        />
        <el-table-column
          prop="orderAmount"
          label="订单金额"
          width="120"
          align="center"
        >
          <template slot-scope="scope">
            ¥{{ scope.row.orderAmount ? scope.row.orderAmount.toLocaleString() : '0' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="brand"
          label="品牌"
          width="120"
          align="center"
        />
        <el-table-column
          prop="violationCount"
          label="违规次数"
          width="100"
          align="center"
        />
        <el-table-column
          prop="penaltyAmount"
          label="扣罚金额"
          width="120"
          align="center"
        >
          <template slot-scope="scope">
            ¥{{ scope.row.penaltyAmount ? scope.row.penaltyAmount.toLocaleString() : '0' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="penaltySubject"
          label="扣罚主体"
          min-width="180"
          align="center"
        />
        <el-table-column
          prop="errorDetails"
          label="错误详情"
          min-width="150"
          align="center"
        />
        <el-table-column
          prop="errorCategory"
          label="错误归类"
          width="120"
          align="center"
        />
        <el-table-column
          prop="penaltyStatus"
          label="扣罚状态"
          width="120"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag
              :type="getPenaltyStatusType(scope.row.penaltyStatus)"
              size="small"
            >
              {{ scope.row.penaltyStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="billDeductionTime"
          label="账单扣款时间"
          width="200"
          align="center"
        />
        <el-table-column
          prop="remarks"
          label="备注"
          min-width="150"
          align="center"
          show-overflow-tooltip
        />
        <el-table-column
          prop="deductionVoucher"
          label="扣款凭证"
          width="120"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.deductionVoucher === '有凭证' ? 'success' : 'info'"
              size="small"
            >
              {{ scope.row.deductionVoucher || '无凭证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="voucherNumber"
          label="凭证号"
          width="150"
          align="center"
        />
        <el-table-column
          label="操作"
          width="150"
          align="center"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-link
              type="primary"
              size="small"
              @click="handleViewPenalty(scope.row)"
            >查看</el-link>
            <el-link
              type="success"
              size="small"
              style="margin-left: 8px"
              @click="handleEditPenalty(scope.row)"
            >编辑</el-link>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
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
    <div v-else class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
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
        <template slot="empty">
          <EmptyState text="暂无收款记录" />
        </template>
        <el-table-column
          prop="payCompanyName"
          label="付款主体"
          width="200"
          align="center"
        />
        <el-table-column
          prop="status"
          label="状态"
          width="150"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ formatStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="billType"
          label="账单类型"
          min-width="100"
          align="center"
        >
          <template slot-scope="scope">
            {{ scope.row.billType === 1 ? '批量采购' : '一件代发' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="payVoucherUrls"
          label="收款凭证"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <div v-if="scope.row.payVoucherUrls">
              <el-image
                :src="getImageUrl(scope.row.payVoucherUrls)"
                fit="cover"
                style="width: 100px; height: 100px; cursor: pointer; border-radius: 4px;"
                :lazy="true"
                @click="handlePreviewImage(scope.row.payVoucherUrls)"
              />
            </div>
            <span v-else style="color: #909399;">暂无凭证</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="payAmount"
          label="排款金额"
          min-width="150"
          align="center"
        >
          <template slot-scope="scope">
            <div class="amount-cell">
              <span
                class="amount"
              >¥ {{ scope.row.payAmount || '0' }}</span>
              <el-link
                type="primary"
                size="small"
                @click="handleExport(scope.row)"
              >导出</el-link>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="confirmReceipt"
          label="确认收款"
          min-width="200"
          align="center"
        >
          <template slot-scope="scope">
            <div class="confirm-actions">
              <span
                v-if="scope.row.status === 3"
                class="confirmed-text"
              >已确认</span>
              <span
                v-if="scope.row.status === 4"
                class="system-confirmed-text"
              >系统自动确认</span>
              <span
                v-if="scope.row.status === 5"
                class="abnormal-text"
              >异常</span>
              <template v-if="scope.row.status === 2">
                <el-link
                  type="success"
                  size="small"
                  @click="handleConfirm(scope.row)"
                >确认</el-link>
                <el-link
                  type="danger"
                  size="small"
                  @click="handleAbnormal(scope.row)"
                >异常</el-link>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
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

    <!-- 图片预览对话框 -->
    <el-dialog
      :visible.sync="imageViewerVisible"
      :close-on-click-modal="true"
      :show-close="false"
      width="90%"
      top="5vh"
      custom-class="image-preview-dialog"
      @close="handleClosePreview"
    >
      <div class="image-preview-container">
        <div class="image-preview-header">
          <span class="image-preview-title">图片预览</span>
          <div class="image-preview-close" @click="handleClosePreview">
            <i class="el-icon-close" />
          </div>
        </div>
        <div class="image-preview-content">
          <div v-if="previewImageList.length > 1" class="image-preview-nav">
            <el-button
              :disabled="previewImageIndex === 0"
              icon="el-icon-arrow-left"
              circle
              @click="handlePrevImage"
            />
            <span class="image-index">{{ previewImageIndex + 1 }} / {{ previewImageList.length }}</span>
            <el-button
              :disabled="previewImageIndex === previewImageList.length - 1"
              icon="el-icon-arrow-right"
              circle
              @click="handleNextImage"
            />
          </div>
          <div class="image-preview-wrapper">
            <img
              :src="previewImageList[previewImageIndex]"
              alt="预览图片"
              class="preview-image"
            >
          </div>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import EmptyState from '@/components/EmptyState'
import { getBillTodayList, todayConfirm, todayError, todayExport } from '@/api/monery'
export default {
  name: 'PaymentDetails',
  components: {
    EmptyState
  },
  data() {
    return {
      loading: false,
      activeTab: 'all', // 当前激活的tab
      tabs: [
        { label: '全部', value: 'all' },
        { label: '异常', value: 'abnormal' },
        { label: '冻结货款', value: 'frozen' },
        { label: '未确认', value: 'unconfirmed' },
        { label: '扣罚订单明细', value: 'penalty' }
      ],
      dateRange: [],
      accountType: '',
      confirmStatus: '',
      payerEntity: '', // 付款主体（采购方）
      // 扣罚订单明细的筛选条件
      penaltyFilters: {
        penaltyOrderNumber: '', // 扣罚单号
        createTime: [], // 创建时间
        noWarehouseOrderNumber: '', // 无仓单号
        penaltySubject: '', // 扣罚主体
        errorCategory: '', // 错误归类
        errorDetails: '', // 错误详情
        penaltyStatus: '', // 扣罚状态
        deductionVoucher: '', // 扣款凭证
        voucherNumber: '' // 凭证号
      },
      pagination: {
        pageNum: 1,
        pageSize: 30,
        total: 100
      },
      selectedRows: [], // 选中的行数据
      allTableData: [],
      // 图片预览相关
      imageViewerVisible: false, // 预览是否显示
      previewImageList: [], // 预览图片列表
      previewImageIndex: 0, // 当前预览图片索引
      // 冻结货款表格数据示例
      penaltyTableData: []
    }
  },
  computed: {
    tableData() {
      if (this.activeTab === 'all') {
        return this.allTableData
      } else if (this.activeTab === 'abnormal') {
        // 修复：status 是数字类型，5 表示异常
        return this.allTableData.filter((item) => item.status === 5)
      } else if (this.activeTab === 'penalty') {
        return this.penaltyTableData
      } else if (this.activeTab === 'frozen') {
        // 修复：status 是数字类型，需要确认冻结状态对应的数字（暂时保留，需要确认业务逻辑）
        return this.allTableData.filter((item) => item.status === 'frozen')
      } else if (this.activeTab === 'unconfirmed') {
        // 修复：status 是数字类型，2 表示待确认
        return this.allTableData.filter((item) => item.status === 2)
      }
      return this.allTableData
    },
    // 从表格数据中提取所有唯一的付款主体
    payerEntityOptions() {
      const payerEntities = this.allTableData
        .map(item => item.payerEntity)
        .filter(item => item) // 过滤空值
      // 去重并排序
      return [...new Set(payerEntities)].sort()
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    handleTabChange(tabValue) {
      this.activeTab = tabValue
      this.pagination.pageNum = 1
      // 切换标签页时清空选中状态
      this.selectedRows = []
      this.loadData()
    },
    handleSearch() {
      if (this.activeTab === 'penalty') {
        console.log('搜索条件 (扣罚订单明细):', {
          activeTab: this.activeTab,
          filters: { ...this.penaltyFilters }
        })
      } else {
        console.log('搜索条件:', {
          activeTab: this.activeTab,
          dateRange: this.dateRange,
          accountType: this.accountType,
          confirmStatus: this.confirmStatus,
          payerEntity: this.payerEntity
        })
      }
      // 修复：重置页码
      this.pagination.pageNum = 1
      this.loadData()
    },
    handleReset() {
      if (this.activeTab === 'penalty') {
        // 重置扣罚订单明细的筛选条件
        this.penaltyFilters = {
          penaltyOrderNumber: '',
          createTime: [],
          noWarehouseOrderNumber: '',
          penaltySubject: '',
          errorCategory: '',
          errorDetails: '',
          penaltyStatus: '',
          deductionVoucher: '',
          voucherNumber: ''
        }
      } else {
        // 重置其他标签页的筛选条件
        this.dateRange = []
        this.accountType = ''
        this.confirmStatus = ''
        this.payerEntity = ''
      }
      // 修复：统一重置逻辑
      this.pagination.pageNum = 1
      this.loadData()
    },
    async handleExport(row) {
      this.$confirm('确认导出该笔收款记录？', '导出确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        // 添加 loading 状态
        const loading = this.$loading({
          lock: true,
          text: '导出中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        try {
          console.log('导出收款记录, 记录ID:', row.id)
          const blobData = await todayExport(row.id)

          const blob = new Blob([blobData], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
          })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url

          const now = new Date()
          const timestamp =
            now.getFullYear() +
            String(now.getMonth() + 1).padStart(2, '0') +
            String(now.getDate()).padStart(2, '0') +
            '_' +
            String(now.getHours()).padStart(2, '0') +
            String(now.getMinutes()).padStart(2, '0')

          const companyName = row && row.payCompanyName ? row.payCompanyName : '收款记录'
          link.download = `${companyName}_${timestamp}.xlsx`

          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)

          this.$message.success('导出成功')
        } catch (error) {
          console.error('导出收款记录失败:', error)
          this.$message.error('导出失败，请稍后重试')
        } finally {
          loading.close()
        }
      }).catch(() => {
        this.$message.info('已取消导出')
      })
    },
    handleExportAll() {
      console.log('导出所有支付记录')
      this.$message.success('导出成功')
    },
    handleConfirm(row) {
      this.$confirm('确认已收到该笔款项？', '确认收款', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        // 添加 loading 状态
        const loading = this.$loading({
          lock: true,
          text: '处理中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        try {
          const res = await todayConfirm(row.id)
          if (res.code === 200) {
            console.log('确认收款:', row)
            this.$message.success('确认收款成功')
            this.loadData()
          } else {
            this.$message.error(res?.msg || '确认收款失败')
          }
        } catch (error) {
          console.error('确认收款失败:', error)
          this.$message.error('确认收款失败，请稍后重试')
        } finally {
          loading.close()
        }
      }).catch(() => {
        this.$message.info('已取消确认')
      })
    },
    handleAbnormal(row) {
      this.$confirm('确认将该笔款项标记为异常？', '标记异常', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        // 添加 loading 状态
        const loading = this.$loading({
          lock: true,
          text: '处理中...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        try {
          console.log('标记异常:', row)
          const res = await todayError(row.id)
          if (res.code === 200) {
            console.log('标记异常:', row)
            this.$message.success('标记异常成功')
            this.loadData()
          } else {
            this.$message.error(res?.msg || '标记异常失败')
          }
        } catch (error) {
          console.error('标记异常失败:', error)
          this.$message.error('标记异常失败，请稍后重试')
        } finally {
          loading.close()
        }
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    // 表格选择变化
    handleSelectionChange(selection) {
      this.selectedRows = selection
    },
    // 申请凭证
    handleApplyVoucher() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要申请凭证的记录')
        return
      }
      console.log('申请凭证:', this.selectedRows)
      this.$message.success(`已为${this.selectedRows.length}条记录申请凭证`)
      // 这里可以调用API申请凭证
    },
    // 取消选择
    handleCancelSelection() {
      this.selectedRows = []
      // 清空表格选择
      this.$nextTick(() => {
        if (this.$refs.penaltyTable) {
          this.$refs.penaltyTable.clearSelection()
        }
      })
    },
    // 获取扣罚状态标签类型
    getPenaltyStatusType(status) {
      const statusMap = {
        '待处理': 'warning',
        '已处理': 'success',
        '已取消': 'info'
      }
      return statusMap[status] || ''
    },
    // 收款状态文案
    formatStatusText(status) {
      const statusMap = {
        1: '待付款',
        2: '待确认',
        3: '手工确认',
        4: '默认确认',
        5: '异常'
      }
      const normalized = Number(status)
      return statusMap[normalized] || '未知状态'
    },
    // 收款状态对应标签类型
    getStatusTagType(status) {
      const normalized = Number(status)
      const typeMap = {
        1: 'info',
        2: 'primary',
        3: 'success',
        4: 'success',
        5: 'danger'
      }
      return typeMap[normalized] || 'info'
    },
    // 查看扣罚详情
    handleViewPenalty(row) {
      console.log('查看扣罚详情:', row)
      this.$message.info(`查看扣罚单号：${row.penaltyOrderNumber}`)
      // 这里可以打开详情弹窗或跳转到详情页
    },
    // 编辑扣罚信息
    handleEditPenalty(row) {
      console.log('编辑扣罚信息:', row)
      this.$message.info(`编辑扣罚单号：${row.penaltyOrderNumber}`)
      // 这里可以打开编辑弹窗
    },
    // 获取图片URL（处理字符串或数组）
    getImageUrl(urls) {
      if (!urls) return ''
      if (Array.isArray(urls)) {
        return urls.length > 0 ? urls[0] : ''
      }
      return urls
    },
    // 获取预览列表（处理字符串或数组）
    getPreviewList(urls) {
      if (!urls) return []
      if (Array.isArray(urls)) {
        return urls.filter(url => url) // 过滤空值
      }
      return [urls]
    },
    // 打开图片预览
    handlePreviewImage(urls) {
      const imageList = this.getPreviewList(urls)
      if (imageList.length === 0) {
        this.$message.warning('暂无图片可预览')
        return
      }
      this.previewImageList = imageList
      this.previewImageIndex = 0
      this.imageViewerVisible = true
    },
    // 关闭图片预览
    handleClosePreview() {
      this.imageViewerVisible = false
      this.previewImageList = []
      this.previewImageIndex = 0
    },
    // 上一张图片
    handlePrevImage() {
      if (this.previewImageIndex > 0) {
        this.previewImageIndex--
      }
    },
    // 下一张图片
    handleNextImage() {
      if (this.previewImageIndex < this.previewImageList.length - 1) {
        this.previewImageIndex++
      }
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.loadData()
    },
    handleCurrentChange(current) {
      this.pagination.pageNum = current
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        // 这里可以调用API加载数据，需要传入activeTab进行过滤
        const filters = this.activeTab === 'penalty'
          ? { ...this.penaltyFilters }
          : {
            dateRange: this.dateRange,
            accountType: this.accountType,
            confirmStatus: this.confirmStatus,
            payerEntity: this.payerEntity
          }
        console.log(this.dateRange)

        const formatDateToSlash = (value) => {
          if (!value) return ''
          const date = value instanceof Date ? value : new Date(value)
          if (Number.isNaN(date.getTime())) return ''
          const year = date.getFullYear()
          const month = `${date.getMonth() + 1}`.padStart(2, '0')
          const day = `${date.getDate()}`.padStart(2, '0')
          return `${year}-${month}-${day}`
        }

        // 修复：添加空值检查，防止访问 null/undefined 的索引
        const startDate = this.dateRange && this.dateRange[0] ? formatDateToSlash(this.dateRange[0]) : ''
        const endDate = this.dateRange && this.dateRange[1] ? formatDateToSlash(this.dateRange[1]) : ''

        const res = await getBillTodayList({
          startDate,
          endDate
        }, {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        })
        if (res.code === 200) {
          this.allTableData = res.rows || []
          this.pagination.total = res.total || 0
        } else {
          this.$message.error(res?.msg || '加载数据失败')
        }
        console.log('加载数据:', {
          activeTab: this.activeTab,
          pagination: this.pagination,
          filters
        })
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败，请稍后重试')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.payment-details {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 130px);
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;

  .page-header-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 16px 0 rgba(74, 127, 255, 0.07),
      0 1.5px 8px rgba(0, 0, 0, 0.03);
    padding: 28px 36px 14px 36px;
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

    .tab-section {
      flex: 1;
      text-align: center;
      margin: 0 40px;

      .tab-container {
        display: flex;
        align-items: center;
        gap: 0;
        background: rgba(195, 232, 255, 0.3);
        border-radius: 16px;
        padding: 4px 20px;
        box-shadow: 0 2px 10px #9acfff15;

        .tab-item {
          padding: 10px 20px;
          margin-right: 10px;
          cursor: pointer;
          font-size: 16px;
          color: #606266;
          border-radius: 12px;
          transition: all 0.3s;
          white-space: nowrap;
          background: transparent;

          &:hover {
            color: #227cff;
            background: rgba(34, 124, 255, 0.35);
          }

          &.active {
            // color: #0962e8;
            font-weight: bold;
            background: #fff;
            box-shadow: 0 2px 8px rgba(58, 140, 255, 0.2);
          }
        }
      }
    }

    .header-actions {
      display: flex;
      gap: 18px;

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

  .penalty-table {
    .batch-actions-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background: #fff;
      border-bottom: 1px solid #ebeef5;
      margin-bottom: 0;

      .selected-count {
        font-size: 14px;
        color: #606266;
      }

      .batch-buttons {
        display: flex;
        gap: 10px;
      }
    }
  }

  .filter-section {
    background: white;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 12px;

    .filter-row {
      display: flex;
      align-items: center;
      gap: 20px;
      flex-wrap: wrap;

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
        margin-left: auto;
        display: flex;
        gap: 10px;
      }
    }
  }

  .table-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    background: white;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: auto;

    .amount-cell {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;

      .amount {
        font-weight: 500;
        color: #333;
      }
    }

    .confirm-actions {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;

      .confirmed-text {
        color: #67c23a;
        font-weight: 500;
      }
      .system-confirmed-text {
        color: #909399;
        font-weight: 500;
      }
      .abnormal-text {
        color: #f56c6c;
        font-weight: 500;
      }
    }
    .pagination-section {
      display: flex;
      justify-content: right;
      background: white;
      padding: 10px 24px;
      border-radius: 4px;
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
}

// 表格样式优化
:deep(.el-table) {
  .el-table__header {
    background: #f8f9fa;
  }

  .el-table__row {
    &:hover {
      background: #f8f9fa;
    }
  }

  .el-table__cell {
    padding: 12px 8px;
  }

  .el-link {
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

// 图片预览对话框样式
:deep(.image-preview-dialog) {
  .el-dialog__body {
    padding: 0;
    background: rgba(0, 0, 0, 0.8);
  }

  .el-dialog__header {
    display: none;
  }
}

.image-preview-container {
  display: flex;
  flex-direction: column;
  height: 90vh;
  background: rgba(0, 0, 0, 0.9);

  .image-preview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background: rgba(0, 0, 0, 0.7);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .image-preview-title {
      color: #fff;
      font-size: 16px;
      font-weight: 500;
    }

    .image-preview-close {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      transition: all 0.3s;
      color: #fff;
      font-size: 20px;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
        transform: rotate(90deg);
      }
    }
  }

  .image-preview-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;

    .image-preview-nav {
      position: absolute;
      top: 20px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      align-items: center;
      gap: 20px;
      z-index: 10;
      background: rgba(0, 0, 0, 0.5);
      padding: 8px 16px;
      border-radius: 20px;

      .image-index {
        color: #fff;
        font-size: 14px;
        min-width: 60px;
        text-align: center;
      }

      .el-button {
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.3);
        color: #fff;

        &:hover:not(:disabled) {
          background: rgba(255, 255, 255, 0.3);
          border-color: rgba(255, 255, 255, 0.5);
        }

        &:disabled {
          opacity: 0.3;
          cursor: not-allowed;
        }
      }
    }

    .image-preview-wrapper {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px;

      .preview-image {
        max-width: 100%;
        max-height: 100%;
        object-fit: contain;
        border-radius: 4px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
      }
    }
  }
}
</style>
