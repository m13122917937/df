<template>
  <div class="pending-contract">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>待签署合同</span>
      </div>
      <div class="table-operations">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索合同名称/待签人"
          prefix-icon="el-icon-search"
          size="small"
          style="width: 300px; margin-right: 10px;"
          @input="handleQuery"
        />
        <el-button type="primary" size="small" @click="handleRefresh">
          刷新
        </el-button>
      </div>
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%; margin-top: 16px;"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="contractName" label="合同名称" min-width="200" />
        <el-table-column prop="signer" label="待签署人" width="120" />
        <el-table-column prop="createDate" label="创建日期" width="120" />
        <el-table-column prop="deadline" label="截止日期" width="120" />
        <el-table-column prop="contractType" label="合同类型" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handlePreview(scope.row)">
              预览
            </el-button>
            <el-button type="text" size="small" style="color: #34C759" @click="handleSign(scope.row)">
              去签署
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'PendingContract',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      multipleSelection: [],
      queryParams: {
        page: 1,
        pageSize: 10,
        keyword: ''
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      // TODO: 这里调用API获取数据
      setTimeout(() => {
        this.tableData = [
          {
            id: 1,
            contractName: '供应商合作协议',
            signer: '王五',
            createDate: '2024-01-25',
            deadline: '2024-02-25',
            contractType: '合作协议'
          },
          {
            id: 2,
            contractName: '保密协议',
            signer: '赵六',
            createDate: '2024-01-28',
            deadline: '2024-02-28',
            contractType: '保密协议'
          }
        ]
        this.total = this.tableData.length
        this.loading = false
      }, 500)
    },
    handleQuery() {
      this.queryParams.page = 1
      this.loadData()
    },
    handleRefresh() {
      this.loadData()
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.queryParams.page = val
      this.loadData()
    },
    handlePreview(row) {
      this.$message.info(`预览合同: ${row.contractName}`)
    },
    handleSign(row) {
      this.$message.info(`去签署合同: ${row.contractName}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.pending-contract {
  .box-card {
    .table-operations {
      display: flex;
      align-items: center;
    }
    .pagination-wrapper {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
