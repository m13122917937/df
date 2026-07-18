<template>
  <div class="signed-contract">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>已签署合同</span>
      </div>
      <div class="table-operations">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索合同名称/签署人"
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
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="contractName" label="合同名称" min-width="200" />
        <el-table-column prop="signer" label="签署人" width="120" />
        <el-table-column prop="signDate" label="签署日期" width="120" />
        <el-table-column prop="effectiveDate" label="生效日期" width="120" />
        <el-table-column prop="expireDate" label="到期日期" width="120" />
        <el-table-column prop="contractType" label="合同类型" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDownload(scope.row)">
              下载
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
  name: 'SignedContract',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
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
            contractName: '用户服务协议',
            signer: '张三',
            signDate: '2024-01-15',
            effectiveDate: '2024-01-15',
            expireDate: '2025-01-14',
            contractType: '服务协议'
          },
          {
            id: 2,
            contractName: '合作意向书',
            signer: '李四',
            signDate: '2024-01-20',
            effectiveDate: '2024-01-20',
            expireDate: '2024-07-19',
            contractType: '合作协议'
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
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.queryParams.page = val
      this.loadData()
    },
    handleDownload(row) {
      this.$message.info(`下载合同: ${row.contractName}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.signed-contract {
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
