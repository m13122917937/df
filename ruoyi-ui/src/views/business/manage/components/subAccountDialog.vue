<template>
  <el-dialog
    title="企业子账号列表"
    :visible.sync="dialogVisible"
    width="1200px"
    :before-close="handleClose"
    class="sub-account-dialog"
  >
    <div class="dialog-header">
      <div class="company-info">
        <span class="company-label">企业名称:</span>
        <span class="company-name">{{ companyName }}</span>
      </div>
      <el-button type="primary" size="small" @click="handleAddUser">
        新增用户
      </el-button>
    </div>

    <el-table
      :data="subAccountData"
      v-loading="subAccountLoading"
      border
      style="width: 100%"
      stripe
      class="sub-account-table"
      height="400"
    >
      <el-table-column prop="userId" label="用户ID"  />
      <el-table-column prop="nickName" label="用户名" >
        <template slot-scope="scope">
          <span class="nickName-text">{{ scope.row.nickName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" />
      <el-table-column label="主账号" >
        <template slot-scope="scope">
          <div class="switch-container">
            <el-switch
              :value="scope.row.owner == 0"
              active-color="#409eff"
              inactive-color="#dcdfe6"
              @change="handleChangeOwner(scope.row)"
            />
            <span class="switch-label">{{ scope.row.owner == 0 ? '是' : '否' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            class="delete-btn"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

  </el-dialog>
</template>

<script>
import { getBusinessUserListApi, deleteBusinessUserApi, updateBusinessUserOwnerApi } from '@/api/business'

export default {
  name: 'SubAccountDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    companyName: {
      type: String,
      default: ''
    },
    companyId: {
      type: [Number, String],
      default: 0
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
  },
  data() {
    return {
      subAccountData: [],
      subAccountLoading: false,
      pagination: {
        currentPage: 1,
        pageSize: 20,
        total: 0,
      }
    }
  },
  watch: {
    visible: {
      handler(newVal) {
        if (newVal && this.companyId && this.companyId > 0) {
          // 重置分页到第一页
          this.pagination.currentPage = 1
          this.getSubAccountList()
        }
      }
    }
  },
  methods: {
    async getSubAccountList() {
      this.subAccountLoading = true
      try {
        const res = await getBusinessUserListApi(this.companyId, {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        })
        if (res.code === 200) {
          this.subAccountData = res.rows || []
          this.pagination.total = res.total || 0
        }
      } catch (error) {
        console.error('获取子账户列表失败:', error)
        this.$message.error('获取子账户列表失败')
      } finally {
        this.subAccountLoading = false
      }
    },
    handleClose() {
      this.dialogVisible = false
    },
    handleAddUser() {
      this.$emit('add-user')
    },
    handleDelete(row) {
      this.$confirm('确认删除该用户？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里调用删除API
        this.deleteUser(row.userId)
      }).catch(() => {
        // 用户取消删除
      })
    },
    async deleteUser(userId) {
      try {
        const res = await deleteBusinessUserApi(this.companyId,userId)
        if (res.code === 200) {
          this.$message.success('删除成功')
          // 如果当前页没有数据了，且不是第一页，则跳转到上一页
          if (this.subAccountData.length === 1 && this.pagination.currentPage > 1) {
            this.pagination.currentPage--
          }
          this.getSubAccountList() // 刷新列表
        } else {
          this.$message.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除用户失败:', error)
        this.$message.error('删除失败，请重试')
      }
    },
    /**
     * @description: 修改用户是否主账号
     * @param {*} row 用户信息 row.owner 0:主账号 1:子账号
     * @returns
     */
    async handleChangeOwner(row) {
      const newOwner = row.owner == 0 ? 1 : 0;
      const actionText = newOwner == 0 ? '设置为主账号' : '设置为子账号';
      
      try {
        // 确认操作
        await this.$confirm(`确定要${actionText}吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        const res = await updateBusinessUserOwnerApi(this.companyId, row.userId, newOwner);
        if (res.code === 200) {
          this.$message.success(`${actionText}成功`);
          this.getSubAccountList(); // 刷新列表，保持当前页
        } else {
          this.$message.error(res.message || '修改失败');
        }
      } catch (error) {
        if (error === 'cancel') {
          // 用户取消操作，不需要处理
          return;
        }
        console.error('修改用户是否主账号失败:', error);
        this.$message.error('修改失败，请重试');
      }
    },
    // 分页大小改变
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.getSubAccountList()
    },
    // 当前页改变
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.getSubAccountList()
    },
  }
}
</script>

<style scoped lang="scss">
.sub-account-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    overflow: hidden;
  }
  
  ::v-deep .el-dialog__header {
    background: #f5f7fa;
    color: #303133;
    padding: 20px 24px;
    border-bottom: 1px solid #e4e7ed;
    
    .el-dialog__title {
      color: #303133;
      font-size: 18px;
      font-weight: 600;
    }
    
    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;
      
      .el-dialog__close {
        color: #909399;
        font-size: 20px;
        
        &:hover {
          color: #409eff;
        }
      }
    }
  }
  
  ::v-deep .el-dialog__body {
    padding: 20px 24px;
    background: #fff;
  }
  
  .dialog-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 0;
    
    .company-info {
      display: flex;
      align-items: center;
      
      .company-label {
        color: #606266;
        font-size: 14px;
        margin-right: 8px;
      }
      
      .company-name {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
      }
    }
  }
  
  .sub-account-table {
    ::v-deep .el-table__header {
      th {
        background: #fafafa;
        color: #303133;
        font-weight: 600;
        border-bottom: 1px solid #ebeef5;
        padding: 12px 0;
        text-align: center;
      }
    }
    
    ::v-deep .el-table__body {
      tr {
        &:hover {
          background: #f5f7fa;
        }
        
        td {
          border-bottom: 1px solid #f0f0f0;
          padding: 12px 0;
          text-align: center;
        }
      }
    }
    
    .nickName-text {
      display: inline-block;
      max-width: 180px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .switch-container {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      
      .switch-label {
        font-size: 12px;
        color: #606266;
        min-width: 20px;
      }
    }
    
    .delete-btn {
      color: #409eff;
      padding: 0;
      
      &:hover {
        color: #66b1ff;
      }
    }
  }

  .pagination-section {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    
    ::v-deep .el-pagination {
      .el-pagination__total {
        color: #606266;
        font-weight: 400;
      }
      
      .el-pagination__sizes {
        .el-select .el-input {
          width: 110px;
        }
      }
      
      .el-pagination__jump {
        color: #606266;
        
        .el-input {
          width: 50px;
        }
      }
    }
  }

}
</style>
