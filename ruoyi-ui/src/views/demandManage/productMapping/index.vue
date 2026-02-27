<template>
  <div class="product-mapping-wrap">
    <!-- 状态切换 -->
    <div class="tabs-section">
      <el-tabs v-model="query.status" @tab-click="initTable" >
        <el-tab-pane label="待处理" name="1" />
        <el-tab-pane label="已确认" name="2" />
        <el-tab-pane label="已拒绝" name="3" />
      </el-tabs>
    </div>
   <div class="search-card">
        <div class="search-row">
          <div class="search-item">
            <label class="search-label">商品名称</label>
            <el-input
              v-model.trim="query.productNameLike"
              placeholder="请输入商品名称"
              clearable
              size="small"
              class="search-input"
              @keyup.enter.native="handleSearch"
            />
          </div>
          <div class="search-actions">
            <el-button type="primary" icon="el-icon-search" size="small" @click="handleSearch">
              搜索
            </el-button>
            <el-button icon="el-icon-refresh-left" size="small" @click="handleReset">
              重置
            </el-button>
          </div>
        </div>
      </div>
    <!-- 查询区 + 表格 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tableData"
       :header-cell-style="{
              background: '#f7f8fa',
              color: '#606266',
              fontWeight: 600,
            }"
            :cell-style="{ padding: '8px 0' }"
        stripe
        border
        size="medium"
        height="100%"
        element-loading-text="数据加载中"
      >
         <!-- 空数据状态 -->
        <template slot="empty">
          <EmptyState text="暂无映射数据" />
        </template>
        <el-table-column prop="id" label="id" min-width="50" align="center" />
        <el-table-column
          prop="category"
          label="类别"
          min-width="120"
          align="center"
        />
        <el-table-column
          prop="brand"
          label="品牌"
          min-width="120"
          align="center"
        />
        <el-table-column
          prop="productName"
          label="商品名"
          min-width="120"
          align="center"
          show-overflow-tooltip
        />
        <el-table-column
          prop="skuName"
          label="sku名"
          min-width="120"
          align="center"
          show-overflow-tooltip
        />
        <el-table-column
          prop="snModel"
          label="识别型号"
          min-width="250"
          align="center"
          show-overflow-tooltip
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
          min-width="180"
          align="center"
        />
        <el-table-column  label="操作" fixed="right" width="180" align="center">
        <!-- <el-table-column  label="操作" fixed="right" width="180" align="center"> -->
          <template slot-scope="{ row }">
            <el-link type="primary" :underline="false" @click="approve(row)" v-if="query.status === '1'"
              >同意</el-link
            >
            <el-divider direction="vertical" v-if="query.status === '1'" />
            <el-link type="danger" :underline="false" @click="reject(row)" v-if="query.status === '1'"
              >拒绝</el-link
            >
            <el-divider direction="vertical" v-if="query.status === '1'" />
            <el-link type="info" :underline="false" @click="remove(row)"
              >删除</el-link
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
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
</template>

<script>
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import {
  getRelListApi,
  agreeApi,
  delApi,
  refuseApi,
} from "@/api/productMapping";
export default {
  name: "ProductMappingIndex",
  components: {
    EmptyState
  },
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
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
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
    initTable() {
      this.query.pageNum = 1;
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
      const pageData = {
        pageNum: this.query.pageNum,
        pageSize: this.query.pageSize,
      }
      const queryData = {
        // brand: this.query.brand,
        // category: this.query.category,
        productNameLike: this.query.productNameLike,
        status: Number(this.query.status),
      }
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

<style lang="scss" scoped>
@import "@/assets/styles/common/order-components.scss";
.product-mapping-wrap {
  padding: 16px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 90px);
}

.tabs-section {
  background: #fff;
  padding: 0 12px;
}

.table-section {
  flex: 1;
  overflow: auto;
}

.search-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(15, 35, 95, 0.08);
  border: 1px solid #eef2ff;
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.search-row {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.search-item {
 width: 400px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.search-input {
  flex: 1;
}

.search-actions {
  display: flex;
  gap: 8px;
}

.pagination-section {
  text-align: right;
  padding: 20px;
  padding-bottom: 0;

  .el-pagination {
    .el-pagination__total {
      color: #606266;
      font-weight: 500;
    }

    .el-pagination__sizes {
      .el-select .el-input__inner {
        height: 28px;
        line-height: 28px;
      }
    }

    .el-pager li {
      min-width: 28px;
      height: 28px;
      line-height: 28px;
      border-radius: 4px;
      margin: 0 2px;

      &.active {
        background-color: #409eff;
        color: #fff;
      }

      &:hover {
        color: #409eff;
      }
    }

    .btn-prev,
    .btn-next {
      height: 28px;
      line-height: 28px;
      border-radius: 4px;
      margin: 0 2px;

      &:hover {
        color: #409eff;
      }
    }

    .el-pagination__jump {
      color: #606266;

      .el-input__inner {
        height: 28px;
        line-height: 28px;
        width: 50px;
      }
    }
  }
}

.sub {
  color: #909399;
  font-size: 12px;
}
</style>
