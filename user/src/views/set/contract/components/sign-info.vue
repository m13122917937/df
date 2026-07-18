<template>
  <div class="sign-info">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>签署信息管理</span>
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
        <el-table-column prop="contractType" label="合同类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">
              查看
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
  name: 'SignInfo',
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
            contractType: '服务协议',
            status: 1
          },
          {
            id: 2,
            contractName: '合作意向书',
            signer: '李四',
            signDate: '2024-01-20',
            contractType: '合作协议',
            status: 1
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
    getStatusType(status) {
      const types = {
        0: 'warning',
        1: 'success',
        2: 'info'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '待签署',
        1: '已签署',
        2: '已过期'
      }
      return texts[status] || '未知'
    },
    handleView(row) {
      this.$message.info(`查看合同: ${row.contractName}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.sign-info {
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
