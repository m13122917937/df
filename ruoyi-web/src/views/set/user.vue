<template>
  <div class="sub-account-page">
    <div class="page-header">
      <div class="header-left">
        <div class="title">子账号管理</div>
        <div class="subtitle">可添加员工账号并配置使用权限</div>
      </div>

      <div class="header-actions">
        <el-button type="primary" size="small" @click="handleAddSubAccount">
          添加子账号
        </el-button>
        <div class="search-bar">
          <el-input
            v-model.trim="searchForm.name"
            size="small"
            class="search-input"
            clearable
            placeholder="请输入用户名"
            @keyup.enter.native="handleSearch"
          />
          <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
          <el-button type="" size="small" @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <div class="table-card">
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
        class="sub-account-table"
        header-cell-class-name="table-header"
      >
        <el-table-column prop="nickName" label="姓名" min-width="120" align="center" />
        <el-table-column prop="phone" label="手机号" min-width="160" align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="200" align="center" />
        <!--        <el-table-column label="权限" width="120" align="center">-->
        <!--          <template slot-scope="{ row }">-->
        <!--            <el-tag :type="row.owner === 0 ? 'success' : 'info'" effect="dark" size="medium">-->
        <!--              {{ row.owner === 0 ? '主账号' : '子账号' }}-->
        <!--            </el-tag>-->
        <!--          </template>-->
        <!--        </el-table-column>-->
        <el-table-column label="主账号">
          <template slot-scope="scope">
            <div class="switch-container">
              <el-switch
                :value="scope.row.owner == 0"
                active-color="#1677FF"
                inactive-color="#dcdfe6"
                @change="handleChangeOwner(scope.row)"
              />
              <span class="switch-label">{{ scope.row.owner == 0 ? '是' : '否' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.master !== 0"
              type="text"
              style="color: #FF3B30;font-size: 12px; font-weight: 600;"
              size="mini"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <!-- <el-button
              v-if="row.master !== 0"
              type="text"
              size="mini"
              @click="handleTransfer(row)"
            >
              管理员权限转让
            </el-button> -->
            <span v-else class="master-ops">
              -
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :current-page="pagination.pageNum"
          :page-size="pagination.pageSize"
          :page-sizes="[30, 50, 100]"
          :total="pagination.total"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog
      title="管理员权限转让"
      width="420px"
      :visible.sync="transferDialog.visible"
      :close-on-click-modal="false"
    >
      <div class="dialog-body">
        确认将管理员权限转让给
        <span class="highlight">{{ transferDialog.target && transferDialog.target.nickName }}</span>
        ？
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="transferDialog.visible = false">取 消</el-button>
        <el-button type="primary" :loading="transferDialog.loading" @click="confirmTransfer">
          确 定
        </el-button>
      </span>
    </el-dialog>

    <!-- 新增用户弹框 -->
    <add-user-dialog
      :visible.sync="addUserDialogVisible"
      @save="handleAddUserSuccess"
    />
  </div>
</template>

<script>
import AddUserDialog from './componentys/addUserDialog.vue'
import {
  getSubAccountList,
  removeSubAccount,
  transferSubAccountAdmin,
  updateBusinessUserOwnerApi
} from '@/api/set'

export default {
  name: 'SetUser',
  components: {
    AddUserDialog
  },
  data() {
    return {
      loading: false,
      tableData: [],
      searchForm: {
        name: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 30,
        total: 0
      },
      addUserDialogVisible: false,
      transferDialog: {
        visible: false,
        target: null,
        loading: false
      }
    }
  },
  mounted() {
    this.fetchTableData()
  },
  methods: {
    async fetchTableData() {
      this.loading = true
      try {
        const params = {
          name: this.searchForm.name,
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        }
        const res = await getSubAccountList(params)
        this.tableData = (res && (res.rows || res.data || [])) || []
        this.pagination.total = res && (res.total || (res.data && res.data.total) || 0)
      } catch (error) {
        console.error('获取子账号列表失败', error)
      } finally {
        this.loading = false
      }
    },
    handleAddSubAccount() {
      this.addUserDialogVisible = true
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchTableData()
    },
    handleReset() {
      this.searchForm = {
        name: ''
      }
      this.pagination.pageNum = 1
      this.fetchTableData()
    },
    handlePageChange(page) {
      this.pagination.pageNum = page
      this.fetchTableData()
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.pageNum = 1
      this.fetchTableData()
    },
    /**
     * @description: 修改用户是否主账号
     * @param {*} row 用户信息 row.owner 0:主账号 1:子账号
     * @returns
     */
    async handleChangeOwner(row) {
      const newOwner = row.owner === 0 ? 1 : 0
      const actionText = newOwner === 0 ? '设置为主账号' : '设置为子账号'

      try {
        // 确认操作
        await this.$confirm(`确定要${actionText}吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await updateBusinessUserOwnerApi(row.userId, newOwner)
        console.log('res', res)
        if (res && res.code === 200) {
          this.$message.success(`${actionText}成功`)
          this.fetchTableData()
        } else {
          this.$message.error(res.message || '修改失败')
        }
      } catch (error) {
        if (error === 'cancel') {
          // 用户取消操作，不需要处理
          return
        }
        this.$message.error('修改失败，请重试')
      }
    },
    handleDelete(row) {
      this.$confirm('确认要删除该用户吗？', '删除确认', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      })
        .then(async() => {
          const res = await removeSubAccount(row.userId)
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.pagination.pageNum = 1
            this.fetchTableData()
          } else {
            this.$message.error(res.message)
          }
        })
        .catch(() => {
        })
    },
    handleTransfer(row) {
      this.transferDialog.target = row
      this.transferDialog.visible = true
    },
    async confirmTransfer() {
      if (!this.transferDialog.target) return
      this.transferDialog.loading = true
      try {
        await transferSubAccountAdmin(this.transferDialog.target.userId)
        this.$message.success('管理员权限转让成功')
        this.transferDialog.visible = false
        this.fetchTableData()
      } catch (error) {
        console.error('管理员权限转让失败', error)
      } finally {
        this.transferDialog.loading = false
      }
    },
    handleAddUserSuccess() {
      this.addUserDialogVisible = false
      this.pagination.pageNum = 1
      this.fetchTableData()
    }
  }
}
</script>

<style lang="scss" scoped>
.sub-account-page {
  padding: 20px;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 130px);
  overflow-y: auto;

  .page-header {
    background: #fff;
    border-radius: 12px;
    padding: 18px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    box-shadow: 0 10px 30px rgba(76, 111, 191, 0.08);

    .header-left {
      .title {
        font-size: 20px;
        font-weight: 600;
        color: #1f2a37;
      }

      .subtitle {
        margin-top: 4px;
        color: #6b7280;
        font-size: 13px;
      }
    }

    .header-actions {
      display: flex;
      align-items: center;

      .search-bar {
        display: flex;
        align-items: center;
        margin-left: 16px;

        .search-select {
          width: 110px;
          margin-right: 8px;
        }

        .search-input {
          width: 220px;
          margin-right: 8px;
        }
      }
    }
  }

  .table-card {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(20, 80, 200, 0.08);
    padding: 16px;
    flex: 1;
    overflow: auto;
    display: flex;
    flex-direction: column;
    height: 100%;

    .sub-account-table {
      width: 100%;

      ::v-deep .table-header .cell {
        color: #4b5563;
        font-weight: 600;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 12px;
    }
  }

  .dialog-body {
    font-size: 14px;
    color: #4b5563;

    .highlight {
      color: #FF3B30;
      font-weight: 600;
      margin: 0 6px;
    }
  }
}
</style>
