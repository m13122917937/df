<template>
  <div class="wholesale-container">
    <!-- Metric Cards -->
    <div
      ref="metricsRow"
      class="metrics-row"
      @mousedown="onDragStart"
      @mousemove="onDragging"
      @mouseup="onDragEnd"
      @mouseleave="onDragEnd"
    >
      <div
        v-for="tab in tabs"
        :key="tab.status"
        class="metric-card"
        :class="{ active: query.status === tab.status }"
        @click="switchTab(tab.status)"
      >
        <div class="metric-icon" :style="{ background: tab.bg, color: tab.color }" v-html="tab.icon">
        </div>
        <div class="metric-content">
          <span class="metric-label">{{ tab.label }}</span>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Search -->
      <div class="search-section">
        <div class="search-content">
          <div class="search-item">
            <label class="search-label">商品名称</label>
            <el-input
              v-model.trim="query.productNameLike"
              placeholder="请输入商品名称"
              clearable
              class="search-input"
              prefix-icon="el-icon-search"
              @keyup.enter.native="handleSearch"
            />
          </div>
          <div class="search-actions">
            <el-button
              icon="el-icon-refresh"
              @click="handleReset"
            >
              重置
            </el-button>
            <el-button
              type="primary"
              icon="el-icon-search"
              @click="handleSearch"
            >
              搜索
            </el-button>
          </div>
        </div>
      </div>

      <!-- Table -->
      <div class="table-section">
        <el-table
          v-loading="loading"
          :data="tableData"
          header-cell-class-name="table-header-cell"
          :cell-style="{ padding: '8px 0' }"
          stripe
          border
          size="medium"
          height="100%"
          element-loading-text="数据加载中"
        >
          <template slot="empty">
            <EmptyState text="暂无映射数据" />
          </template>
          <el-table-column prop="id" label="id" min-width="50" align="center" />
          <el-table-column prop="category" label="类别" min-width="120" align="center" />
          <el-table-column prop="brand" label="品牌" min-width="120" align="center" />
          <el-table-column prop="productName" label="商品名" min-width="120" align="center" show-overflow-tooltip />
          <el-table-column prop="skuName" label="sku名" min-width="120" align="center" show-overflow-tooltip />
          <el-table-column prop="snModel" label="识别型号" min-width="250" align="center" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" min-width="180" align="center" />
          <el-table-column prop="confirmName" label="确认人" min-width="120" align="center" />
          <el-table-column label="操作" fixed="right" width="180" align="center">
            <template slot-scope="{ row }">
              <el-link type="primary" :underline="false" @click="approve(row)" v-if="query.status === '1'">同意</el-link>
              <el-divider direction="vertical" v-if="query.status === '1'" />
              <el-link type="danger" :underline="false" @click="reject(row)" v-if="query.status === '1'">拒绝</el-link>
              <el-divider direction="vertical" v-if="query.status === '1'" />
              <el-link type="info" :underline="false" @click="remove(row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Pagination -->
      <div class="pagination-section">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="query.pageNum"
          :page-sizes="[30, 50, 100]"
          :page-size="query.pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>
  </div>
</template>

<script>
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import { getRelListApi, agreeApi, delApi, refuseApi } from "@/api/productMapping";

const icons = {
  clock: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>',
  check: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
  close: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>',
}

export default {
  name: "ProductMappingIndex",
  components: { EmptyState },
  data() {
    return {
      loading: false,
      query: {
        status: "1",
        pageNum: 1,
        pageSize: 30,
        brand: "",
        category: "",
        productNameLike: "",
      },
      tableData: [],
      total: 0,
      isDragging: false,
      dragStartX: 0,
      dragScrollLeft: 0,
      dragMoved: false,
      tabs: [
        { status: "1", label: "待处理", icon: icons.clock, bg: "rgba(255,179,64,0.08)", color: "#FFB340" },
        { status: "2", label: "已确认", icon: icons.check, bg: "rgba(52,199,89,0.08)", color: "#34C759" },
        { status: "3", label: "已拒绝", icon: icons.close, bg: "rgba(148,163,184,0.08)", color: "#94A3B8" },
      ],
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    onDragStart(e) {
      this.isDragging = true
      this.dragStartX = e.pageX
      this.dragScrollLeft = this.$refs.metricsRow.scrollLeft
      this.dragMoved = false
    },
    onDragging(e) {
      if (!this.isDragging) return
      const dx = e.pageX - this.dragStartX
      if (Math.abs(dx) > 5) this.dragMoved = true
      this.$refs.metricsRow.scrollLeft = this.dragScrollLeft - dx
    },
    onDragEnd() {
      this.isDragging = false
    },
    switchTab(status) {
      if (this.dragMoved) return
      this.query.status = status;
      this.query.pageNum = 1;
      this.fetchData();
    },
    handleSearch() {
      this.query.pageNum = 1;
      this.fetchData();
    },
    handleReset() {
      this.query = {
        status: this.query.status,
        pageNum: 1,
        pageSize: 30,
        brand: "",
        category: "",
        productNameLike: "",
      };
      this.fetchData();
    },
    refresh() {
      this.fetchData();
    },
    handleSizeChange(size) {
      this.query.pageSize = size;
      this.query.pageNum = 1;
      this.fetchData();
    },
    handleCurrentChange(page) {
      this.query.pageNum = page;
      this.fetchData();
    },
    approve(row) {
      this.$confirm("确认同意该记录吗？", "提示", { type: "warning" })
        .then(async () => {
          const res = await agreeApi(row.id);
          if (res.code === 200) {
            this.query.status = '2';
            this.query.pageNum = 1;
            this.fetchData();
            this.$message.success("同意成功");
          }
        })
        .catch(() => {});
    },
    reject(row) {
      this.$confirm("确认拒绝该记录吗？", "提示", { type: "warning" })
        .then(async () => {
          const res = await refuseApi(row.id);
          if (res.code === 200) {
            this.query.status = '3';
            this.query.pageNum = 1;
            this.fetchData();
            this.$message.success("拒绝成功");
          }
        })
        .catch(() => {});
    },
    remove(row) {
      this.$confirm("确认删除该记录吗？", "提示", { type: "warning" })
        .then(async () => {
          const res = await delApi(row.id);
          if (res.code === 200) {
            this.fetchData();
            this.$message.success("删除成功");
          }
        })
        .catch(() => {});
    },
    async fetchData() {
      if (this.loading) return;
      this.loading = true;
      const pageData = { pageNum: this.query.pageNum, pageSize: this.query.pageSize };
      const queryData = {
        productNameLike: this.query.productNameLike,
        status: Number(this.query.status),
      };
      const res = await getRelListApi(pageData, queryData);
      if (res.code === 200) {
        this.tableData = res.rows || [];
        this.total = res.total || 0;
      }
      this.loading = false;
    },
  },
};
</script>

<style scoped lang="scss">
.wholesale-container {
  padding: 0;
  background: var(--bg-page);
  height: calc(100vh - 112px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'HarmonyOS Sans', 'PingFang SC', system-ui, sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ==================== Metrics Row ==================== */

.metrics-row {
  display: flex;
  gap: 8px;
  padding: 0 var(--page-padding);
  margin: var(--page-section-gap) 0;
  overflow-x: auto;
  flex-shrink: 0;
  scrollbar-width: none;
  &::-webkit-scrollbar { display: none; }
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: var(--bg-card);
  border-radius: 12px;
  cursor: pointer;
  flex-shrink: 0;
  min-width: 120px;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: var(--shadow-card);
  border: 1px solid transparent;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  }

  &.active {
    border-color: var(--adm-primary);
    box-shadow: 0 4px 16px rgba(var(--primary-rgb), 0.12);
  }
}

.metric-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.metric-icon :deep(svg) {
  width: 14px;
  height: 14px;
}

.metric-content {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.metric-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--adm-text-primary);
  white-space: nowrap;
}

/* ==================== Main Content ==================== */

.main-content {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  padding: 0 var(--page-padding) var(--page-padding);
  display: flex;
  flex-direction: column;
  gap: var(--page-section-gap);
}

/* ==================== Search Section ==================== */

.search-section {
  padding: 12px var(--page-card-padding);
  background: var(--bg-card);
  border-radius: var(--radius);
  border: 1px solid var(--border-tags);
  box-shadow: var(--shadow-card);
  flex-shrink: 0;
}

.search-content {
  display: flex;
  align-items: flex-end;
  gap: 12px;
}

.search-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-label {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  white-space: nowrap;
  flex-shrink: 0;
}

.search-input {
  width: 360px;
}

.search-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* ==================== Table ==================== */

.table-section {
  flex: 1;
  overflow: hidden;
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
}

::v-deep .table-header-cell {
  background: var(--bg-table-header) !important;
  color: var(--adm-text-secondary) !important;
  font-weight: 600;
}

.pagination-section {
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
  padding: 4px 0;
}

/* ==================== Responsive ==================== */

@media (max-width: 1200px) {
  .metrics-row {
    padding: 0 var(--page-padding);
  }
  .main-content {
    padding: 0 var(--page-padding) var(--page-padding);
  }
}
</style>
