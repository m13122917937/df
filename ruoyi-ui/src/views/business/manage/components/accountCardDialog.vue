<!-- 银行开户行 -->
<template>
  <div>
  <el-dialog
    title="银行开户行"
    :visible.sync="dialogVisible"
    width="1200px"
    :before-close="handleClose"
    class="account-card-dialog"
  >
    <div class="dialog-content">
      <div class="table-header">
        <div class="header-left">
          <h3 class="table-title">银行账户列表</h3>
          <span class="table-count">共 {{ accountList.length }} 条记录</span>
        </div>
        <div class="header-right">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增账户</el-button>
        </div>
      </div>
      
      <el-table
      :data="accountList"
      border
      style="width: 100%"
      stripe
      class="account-table"

    >
      <el-table-column prop="companyName" label="企业名称" min-width="150" show-overflow-tooltip/>
      <el-table-column prop="nickName" label="企业别称"  min-width="150" show-overflow-tooltip/>
      <el-table-column prop="bankAccount" label="银行账号"  min-width="150"/>
      <el-table-column prop="accountBank" label="开户银行"  min-width="150" show-overflow-tooltip/>
       <el-table-column label="开户地" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.provinceName }}{{ scope.row.cityName}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createByName" label="创建人"  min-width="150"/>
      <el-table-column prop="createTime" label="创建时间"  min-width="150"/>
      <!-- <el-table-column prop="accountBankName" label="	银行开户名"  /> -->
      <el-table-column label="是否默认" min-width="150">
        <template slot-scope="scope">
          <el-tag 
            :type="scope.row.defaulted === 1 ? 'success' : 'danger'"
            size="mini"
          >
            {{ scope.row.defaulted === 1 ? '默认' : '非默认' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="150">
        <template slot-scope="scope">
          <el-tag 
            :type="scope.row.valid === 1 ? 'success' : 'danger'"
            size="mini"
          >
            {{ scope.row.valid === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-edit"
            @click="handleEdit(scope.row, scope.$index)"
            class="action-btn"
          >
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
  </div>
</template>

<script>
import { getBusinessBankListApi } from '@/api/business'

export default {
  name: 'AccountCardDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    companyId: {
      type: [Number, String],
      default: 0
    }
  },
  data() {
    return {
      accountList: []
    }
  },
  watch: {
    visible: {
      handler(newVal) {
        if (newVal && this.companyId && this.companyId > 0) {
          this.getAccountList()
        } else if (!newVal) {
          // 主弹框关闭时，确保表单弹框也关闭
          if (this.formDialogVisible) {
            this.formDialogVisible = false
            this.resetForm()
            this.$nextTick(() => {
              this.isEdit = false
              this.editIndex = -1
            })
          }
        }
      }
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
    formDialogTitle() {
      return this.isEdit ? '编辑银行账户' : '新增银行账户'
    }
  },
  methods: {
    async getAccountList() {
      const res = await getBusinessBankListApi(this.companyId)
      if (res.code === 200) {
        this.accountList = res.data
      }
    },
    handleClose() {
      // 先关闭表单弹框
      if (this.formDialogVisible) {
        this.formDialogVisible = false
        this.resetForm()
      }
      // 再关闭主弹框
      this.dialogVisible = false
    },
    // 新增银行账户
    handleAdd() {
      this.$emit('open-form', {}, false)
    },
    // 编辑银行账户
    handleEdit(row, index) {
      this.$emit('open-form', { ...row }, true)
    }
  }
}
</script>

<style scoped lang="scss">
.account-card-dialog {
  ::v-deep .el-dialog {
    border-radius: 12px;
    overflow: hidden;
    background: var(--bg-card);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    backdrop-filter: blur(10px);
  }
  
  ::v-deep .el-dialog__header {
    background: var(--bg-table-header);
    color: var(--nl-color-title);
    padding: 20px 24px;
    border-bottom: 1px solid var(--border-tags);
    
    .el-dialog__title {
      color: var(--nl-color-title);
      font-size: 18px;
      font-weight: 600;
    }
    
    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;
      
      .el-dialog__close {
        color: var(--nl-color-weak);
        font-size: 20px;
        
        &:hover {
          color: #409eff;
        }
      }
    }
  }
  
  ::v-deep .el-dialog__body {
    padding: 0;
    background: var(--bg-card);
  }
  
  .dialog-content {
    padding: 24px;
    background: var(--bg-card);
  }
  
  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: var(--bg-table-header);
    border-radius: 8px;
    border: 1px solid var(--border-tags);
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .table-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: var(--nl-color-title);
        line-height: 1.4;
      }
      
      .table-count {
        font-size: 13px;
        color: var(--nl-color-weak);
        background: var(--bg-card);
        padding: 4px 8px;
        border-radius: 12px;
        border: 1px solid var(--border-tags);
      }
    }
    
    .header-right {
      .el-button {
        padding: 10px 20px;
        border-radius: 6px;
        font-weight: 500;
        box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
        }
        
        i {
          margin-right: 6px;
        }
      }
    }
  }
  
  .account-table {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    ::v-deep .el-table,
    ::v-deep .el-table__body-wrapper,
    ::v-deep .el-table__fixed-body-wrapper,
    ::v-deep .el-table__fixed,
    ::v-deep .el-table__fixed-right {
      background: var(--bg-card);
      color: var(--nl-color);
    }
    
    ::v-deep .el-table__header {
      th {
        background: var(--bg-table-header);
        color: var(--nl-color-title);
        font-weight: 600;
        border-bottom: 2px solid var(--border-tags);
        padding: 16px 0;
        text-align: center;
        font-size: 14px;
      }
    }
    
    ::v-deep .el-table__body {
      tr {
        background: var(--bg-card);
        transition: all 0.3s ease;
        
        &:hover {
          background: var(--bg-hover);
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        }

        &:hover > td {
          background: var(--bg-hover);
        }
        
        td {
          background: var(--bg-card);
          border-bottom: 1px solid var(--border-tags);
          padding: 14px 0;
          text-align: center;
          font-size: 13px;
          color: var(--nl-color);
        }
      }
    }
    
    ::v-deep .el-table__empty-block {
      padding: 40px 0;
      
      .el-table__empty-text {
        color: var(--nl-color-weak);
        font-size: 14px;
      }
    }
    
    .company-cell {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
      
      .company-name {
        font-size: 14px;
        font-weight: 600;
        color: var(--nl-color-title);
        line-height: 1.4;
      }
      
      .company-alias {
        .alias-text {
          font-size: 12px;
          color: var(--nl-color-weak);
          font-style: italic;
          line-height: 1.2;
        }
      }
      
      .tags {
        display: flex;
        gap: 6px;
        
        .account-type-tag {
          background-color: #ff9500;
          border-color: #ff9500;
          color: white;
          font-weight: 500;
        }
        
        .default-tag {
          background-color: var(--bg-card);
          border-color: #f56c6c;
          color: var(--nl-color-title);
          font-weight: 500;
        }
      }
    }
    
    .action-btn {
      padding: 6px 12px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
      }
      
      i {
        margin-right: 4px;
      }
    }
  }
  
  .dialog-footer {
    background: var(--bg-table-header);
    padding: 16px 24px;
    border-top: 1px solid var(--border-tags);
    text-align: right;
    
    .el-button {
      margin-left: 10px;
      padding: 8px 20px;
      border-radius: 4px;
      font-weight: 500;
      min-width: 80px;
      
      &:first-child {
        margin-left: 0;
      }
      
      &.el-button--primary {
        background-color: #409eff;
        border-color: #409eff;
        
        &:hover {
          background-color: #66b1ff;
          border-color: #66b1ff;
        }
      }
    }
  }
}

</style>
