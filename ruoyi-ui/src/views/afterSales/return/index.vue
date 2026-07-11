<template>
  <div class="sales-return">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">
          <i class="el-icon-refresh"></i>
        </div>
        <div class="header-info">
          <h2 class="header-title">销售退货</h2>
          <span class="header-subtitle">销售退货管理与跟踪</span>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-plus" size="small" @click="dialogVisible = true" class="add-btn">
          新增销售退货
        </el-button>
        <el-tag size="medium" type="warning" effect="plain" class="status-tag">
          <i class="el-icon-document"></i> 退货管理
        </el-tag>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section-wrapper">
      <SearchSection
        :status="1"
        :order-code="orderCode"
        :original-order-id="originalOrderId"
        :return-code="returnCode"
        :product-name-like="productNameLike"
        :sku-name-like="skuNameLike"
        :show-order-code="true"
        :show-original-order-id="true"
        :show-return-code="true"
        :show-product-name-like="true"
        :show-sku-name-like="true"
        :show-company="true"
        :company-id="companyId"
        :show-keyword-search="false"
        :show-category="false"
        :show-shop-name="false"
        :show-time="false"
        @company-change="handleCompanyChange"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <el-table
        ref="table"
        :data="tableData"
        v-loading="loading"
        stripe
        size="medium"
        center
        style="width: 100%"
        :fit="true"
        height="100%"
        :header-cell-style="{
          background: 'var(--bg-table-header)',
          color: 'var(--adm-text-secondary)',
          fontWeight: 600,
        }"
        :cell-style="{ padding: '8px 0' }"
      >
        <!-- 空数据状态 -->
        <template slot="empty">
          <EmptyState text="暂无销售退货数据" />
        </template>

        <!-- 退货单号 -->
        <el-table-column label="退货单号" prop="returnCode" min-width="180" fixed="left">
          <template slot-scope="scope">
            <span class="return-code-text">{{ scope.row.returnCode || '-' }}</span>
          </template>
        </el-table-column>

        <!-- 单号 -->
        <el-table-column label="单号" min-width="280" fixed="left">
          <template slot-scope="scope">
            <div class="order-numbers">
              <div class="order-number-item">内部单号: {{ scope.row.orderCode || "-" }}</div>
              <div class="order-number-item">商家单号: {{ scope.row.originalOrderId || "-" }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 品牌 -->
        <el-table-column label="品牌" prop="brand" min-width="120" :show-overflow-tooltip="true" />

        <!-- 品类 -->
        <el-table-column label="品类" prop="category" min-width="120" :show-overflow-tooltip="true" />

        <!-- 商品名/型号 -->
        <el-table-column label="商品名/型号" prop="productName" min-width="180" :show-overflow-tooltip="true" />

        <!-- 规格名 -->
        <el-table-column label="规格名" prop="skuName" min-width="160" :show-overflow-tooltip="true" />

        <!-- 退货数量 / 原订单数量 -->
        <el-table-column label="退货/原数量" min-width="130" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.quantity || 0 }} / {{ scope.row.originalQuantity || 0 }}</span>
          </template>
        </el-table-column>

        <!-- 退货单价 / 总金额 -->
        <el-table-column label="单价/总金额" min-width="160" align="center">
          <template slot-scope="scope">
            <div class="price-column">
              <span>单价: ￥{{ formatPrice(scope.row.returnPrice) }}</span>
              <span class="total-price">总金额: ￥{{ formatPrice(scope.row.totalAmount) }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 供应商 -->
        <el-table-column label="供应商" prop="companyName" min-width="180" :show-overflow-tooltip="true" />

        <!-- 退货日期 -->
        <el-table-column label="退货日期" prop="returnDate" min-width="160" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.returnDate || '-' }}</span>
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="mini">
              {{ scope.row.status === 1 ? '已扣款' : '已创建' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="handleViewDetail(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[30, 50, 100]"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 新增销售退货弹窗 -->
    <SalesReturnDialog
      :visible.sync="dialogVisible"
      @success="handleSuccess"
    />
  </div>
</template>

<script>
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import SalesReturnDialog from './components/salesReturnDialog.vue';
import { listSalesReturnApi } from "@/api/salesReturn";

export default {
  name: 'SalesReturn',
  components: {
    SearchSection,
    EmptyState,
    SalesReturnDialog
  },
  data() {
    return {
      loading: false,
      dialogVisible: false,
      // 搜索相关数据
      returnCode: "",
      orderCode: "",
      originalOrderId: "",
      companyId: '',
      productNameLike: "",
      skuNameLike: "",
      // 表格数据
      tableData: [],
      // 分页信息
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0,
      },
    };
  },
  methods: {
    formatPrice(val) {
      if (val === null || val === undefined) return '0.00';
      return Number(val).toFixed(2);
    },

    // 搜索处理
    handleSearch(searchData) {
      this.returnCode = searchData.returnCode || "";
      this.orderCode = searchData.orderCode || "";
      this.originalOrderId = searchData.originalOrderId || "";
      this.productNameLike = searchData.productNameLike || "";
      this.skuNameLike = searchData.skuNameLike || "";
      this.companyId = searchData.companyId || "";
      this.pagination.currentPage = 1;
      this.loadData();
    },

    // 重置搜索条件
    handleReset(resetData) {
      this.returnCode = resetData.returnCode || "";
      this.orderCode = resetData.orderCode || "";
      this.originalOrderId = resetData.originalOrderId || "";
      this.productNameLike = resetData.productNameLike || "";
      this.skuNameLike = resetData.skuNameLike || "";
      this.companyId = resetData.companyId || "";
      this.pagination.currentPage = 1;
      this.loadData();
    },

    handleCompanyChange(val) {
      this.pagination.currentPage = 1;
      this.companyId = val;
      this.loadData();
    },

    // 加载数据
    async loadData() {
      this.loading = true;
      try {
        const pageData = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
        };
        const params = {
          returnCode: this.returnCode || undefined,
          orderCode: this.orderCode || undefined,
          originalOrderId: this.originalOrderId || undefined,
          productNameLike: this.productNameLike || undefined,
          skuNameLike: this.skuNameLike || undefined,
          companyId: this.companyId || undefined,
        };

        const res = await listSalesReturnApi(pageData, params);

        if (res && res.code === 200) {
          this.tableData = res.rows || [];
          this.pagination.total = res.total || 0;
        } else {
          this.$message.error(res?.msg || "获取销售退货列表失败");
          this.tableData = [];
          this.pagination.total = 0;
        }
      } catch (error) {
        console.error("加载销售退货数据失败:", error);
        this.tableData = [];
        this.pagination.total = 0;
      } finally {
        this.loading = false;
      }
    },

    // 分页
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.loadData();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadData();
    },

    handleSuccess() {
      this.loadData();
    },

    handleViewDetail(row) {
      this.$message.info(`退货单号: ${row.returnCode}`);
    },
  },
  mounted() {
    this.loadData();
  },
};
</script>

<style scoped lang="scss">
.sales-return {
  padding: 8px;
  background: var(--bg-page);
  height: calc(100vh - 112px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

// 页面标题栏
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--bg-card);
  border-radius: 8px;
  padding: 16px 24px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-card);
  flex-shrink: 0;

  .header-left {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .header-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #ff6b6b, #ee5a24);
    border-radius: 10px;
    color: #fff;
    font-size: 20px;
    box-shadow: 0 4px 10px rgba(238, 90, 36, 0.25);
  }

  .header-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .header-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #1a1a2e;
    line-height: 1.3;
  }

  .header-subtitle {
    font-size: 13px;
    color: #909399;
  }

  .status-tag {
    font-size: 13px;
    padding: 0 14px;
    height: 32px;
    line-height: 32px;
    border-radius: 16px;
    i {
      margin-right: 4px;
    }
  }

  .add-btn {
    border-radius: 6px;
    margin-right: 8px;
  }
}

// 搜索区域包裹
.search-section-wrapper {
  flex-shrink: 0;
  margin-bottom: 12px;
}

// 表格卡片
.table-section {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  background: var(--bg-card);
  border-radius: 8px;
  box-shadow: var(--shadow-card);

  :deep(.el-table) {
    border: none;

    th.el-table__cell {
      background: var(--bg-table-header) !important;
      border-bottom: 2px solid var(--border-tags);

      .cell {
        color: #5f6b7a;
        font-weight: 600;
        font-size: 13px;
        letter-spacing: 0.3px;
      }
    }

    td.el-table__cell {
      border-bottom: 1px solid var(--border-tags);
    }

    .el-table__body tr {
      transition: background 0.15s;

      &:hover td {
        background: var(--bg-hover);
      }
    }

    .el-table__fixed {
      &::before {
        height: 0;
      }
    }
  }
}

// 分页区域
.pagination-section {
  flex-shrink: 0;
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  background: var(--bg-card);
  border-radius: 8px;
  box-shadow: var(--shadow-card);
  margin-top: 12px;

  :deep(.el-pagination) {
    font-weight: 400;

    .el-pagination__total {
      color: #5f6b7a;
    }

    .el-pagination__jump {
      color: #5f6b7a;
    }

    button:not(.disabled):hover {
      color: #409eff;
    }

    .el-pager li.active {
      background: #409eff;
      border-color: #409eff;
      color: #fff;
      border-radius: 4px;
    }

    .el-pager li:not(.active):hover {
      color: #409eff;
    }
  }
}

.order-numbers {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.order-number-item {
  line-height: 1.8;
  font-size: 12px;
}

.return-code-text {
  font-weight: 600;
  color: #409eff;
}

.price-column {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;

  .total-price {
    font-weight: 600;
    color: #ff6b6b;
  }
}
</style>
