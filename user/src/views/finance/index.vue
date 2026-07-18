<template>
  <div class="finance-container">
    <!-- 核心统计指标 -->
    <div class="main-stats">
      <div class="stat-item highlight">
        <div class="stat-value">0</div>
        <div class="stat-label">收数工总</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">16,5175</div>
        <div class="stat-label">收数详情</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">1,703</div>
        <div class="stat-label">今日收数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">0</div>
        <div class="stat-label">应用发票</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">0</div>
        <div class="stat-label">全部开票</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">0</div>
        <div class="stat-label">合同管理</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">0</div>
        <div class="stat-label">钱包</div>
      </div>
    </div>

    <!-- 筛选和分类 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <div
          class="tab-item"
          :class="{ active: activeTab === '采购商' }"
          @click="handleTabClick('采购商')"
        >采购商</div>
        <div
          class="tab-item"
          :class="{ active: activeTab === '未收货数' }"
          @click="handleTabClick('未收货数')"
        >未收货数</div>
        <div
          class="tab-item"
          :class="{ active: activeTab === '今日应收' }"
          @click="handleTabClick('今日应收')"
        >今日应收</div>
      </div>

      <div class="filter-options">
        <div class="option-item">包收总额 ⓘ</div>
        <div class="option-value">¥0</div>
      </div>

      <div class="filter-options">
        <div class="option-item">今日收数 ⓘ</div>
        <div class="option-value">¥0</div>
      </div>
    </div>

    <!-- 数据统计表格区域 -->
    <div class="stats-table-section">
      <div class="date-columns">
        <div class="date-column">
          <div class="date-header">今日已收 ⓘ</div>
          <div class="date-value">0</div>
        </div>
        <div class="date-column">
          <div class="date-header">今日未收 ⓘ</div>
          <div class="date-value">0</div>
        </div>
        <div class="date-column">
          <div class="date-header">今日到期 ⓘ</div>
          <div class="date-value">0</div>
        </div>
        <div class="date-column">
          <div class="date-header">逾期1天</div>
          <div class="date-value">-</div>
        </div>
        <div class="date-column">
          <div class="date-header">逾期2天</div>
          <div class="date-value">-</div>
        </div>
        <div class="date-column">
          <div class="date-header">逾期3天</div>
          <div class="date-value">-</div>
        </div>
        <div class="date-column">
          <div class="date-header">逾期4天</div>
          <div class="date-value">-</div>
        </div>
        <div class="date-column">
          <div class="date-header">逾期5天</div>
          <div class="date-value">-</div>
        </div>
        <div class="date-column">
          <div class="date-header">逾期6天以上</div>
          <div class="date-value">-</div>
        </div>
      </div>
    </div>

    <!-- 操作按钮和提示信息 -->
    <div class="operation-section">
      <div class="operation-buttons">
        <el-button type="danger" plain size="small" @click="handleO2OClick">O2O</el-button>
        <el-button type="default" size="small" @click="handlePurchaseClick">采购入仓</el-button>
        <el-button type="default" size="small" @click="handleGroupClick">群接龙</el-button>
      </div>

      <div class="action-buttons">
        <el-button type="danger" plain size="small" @click="handleExportExcel">📤 导出Excel</el-button>
        <el-button type="warning" size="small" @click="handleHelp">🔍 常见问题</el-button>
      </div>
    </div>

    <!-- 提示信息 -->
    <div class="notice-section">
      <div class="notice-text">
        *O2O模块系统每千单日更新1000回测O2O门店的物流信息，可能会有延迟，点击可预览列表可进行详细信息。
      </div>
    </div>

    <!-- 数据加载状态 -->
    <div v-if="loading" class="loading-section">
      <div class="loading-spinner" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'FinanceIndex',
  data() {
    return {
      loading: false,
      activeTab: '采购商',
      stats: {
        totalOrders: 0,
        detailOrders: 16517,
        todayOrders: 1703,
        invoiceApplications: 0,
        allInvoices: 0,
        contractManagement: 0,
        wallet: 0
      },
      filters: {
        totalAmount: 0,
        todayAmount: 0
      },
      dailyStats: {
        todayReceived: 0,
        todayUnreceived: 0,
        todayDue: 0,
        overdue: {
          day1: 0,
          day2: 0,
          day3: 0,
          day4: 0,
          day5: 0,
          day6Plus: 0
        }
      }
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      this.loading = true
      // 模拟数据加载
      setTimeout(() => {
        this.loading = false
      }, 1000)
    },
    handleTabClick(tab) {
      this.activeTab = tab
      console.log('切换到标签:', tab)
    },
    handleO2OClick() {
      console.log('O2O模块点击')
    },
    handlePurchaseClick() {
      console.log('采购入仓点击')
    },
    handleGroupClick() {
      console.log('群接龙点击')
    },
    handleExportExcel() {
      console.log('导出Excel')
      this.$message.success('Excel导出功能开发中...')
    },
    handleHelp() {
      console.log('常见问题')
      this.$message.info('常见问题帮助文档开发中...')
    }
  }
}
</script>

<style lang="scss" scoped>
.finance-container {
  padding: 0;
  background: #f8f9fa;
  min-height: 100vh;
}

// 核心统计指标
.main-stats {
  display: flex;
  align-items: center;
  background: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e9ecef;

  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 40px;
    min-width: 80px;

    &.highlight {
      .stat-value {
        color: #f5222d;
        font-weight: bold;
      }

      .stat-label {
        color: #f5222d;
        border-bottom: 2px solid #f5222d;
      }
    }

    .stat-value {
      font-size: 18px;
      font-weight: 500;
      color: #333;
      margin-bottom: 5px;
    }

    .stat-label {
      font-size: 14px;
      color: #666;
      padding: 2px 0;
    }
  }
}

// 筛选分类区域
.filter-section {
  display: flex;
  align-items: center;
  background: white;
  padding: 10px 20px;
  border-bottom: 1px solid #e9ecef;

  .filter-tabs {
    display: flex;
    margin-right: 40px;

    .tab-item {
      padding: 8px 16px;
      margin-right: 20px;
      cursor: pointer;
      color: #666;
      font-size: 14px;

      &.active {
        color: #1890ff;
        border-bottom: 2px solid #1890ff;
      }
    }
  }

  .filter-options {
    display: flex;
    align-items: center;
    margin-right: 30px;

    .option-item {
      color: #666;
      font-size: 14px;
      margin-right: 10px;
    }

    .option-value {
      color: #333;
      font-weight: 500;
    }
  }
}

// 统计表格区域
.stats-table-section {
  background: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e9ecef;

  .date-columns {
    display: flex;
    align-items: center;

    .date-column {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;
      min-width: 100px;
      border-right: 1px solid #f0f0f0;

      &:last-child {
        border-right: none;
      }

      .date-header {
        font-size: 14px;
        color: #666;
        margin-bottom: 8px;
        text-align: center;
        padding: 0 5px;
      }

      .date-value {
        font-size: 16px;
        font-weight: 500;
        color: #333;

        &.highlight {
          color: #f5222d;
        }
      }
    }
  }
}

// 操作按钮区域
.operation-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e9ecef;

  .operation-buttons {
    display: flex;
    gap: 10px;
  }

  .action-buttons {
    display: flex;
    gap: 10px;
  }
}

// 提示信息区域
.notice-section {
  background: #fff7e6;
  padding: 10px 20px;
  border-left: 3px solid #faad14;
  margin: 0;

  .notice-text {
    color: #fa8c16;
    font-size: 12px;
    line-height: 1.5;
  }
}

// 加载状态
.loading-section {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background: white;

  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #1890ff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// Element UI按钮样式调整
.el-button {
  &--danger.is-plain {
    border-color: #f5222d;
    color: #f5222d;

    &:hover {
      background: #f5222d;
      color: white;
    }
  }

  &--warning {
    background: #faad14;
    border-color: #faad14;

    &:hover {
      background: #ffc53d;
      border-color: #ffc53d;
    }
  }
}
</style>
