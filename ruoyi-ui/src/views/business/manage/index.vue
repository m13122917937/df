<template>
  <div class="business-manage-container">
    <!-- 搜索组件 -->
    <SearchSection
      :form-data="searchForm"
      @search="handleSearch"
      @reset="handleReset"
    >
      <template #table-btn>
        <el-button
          type="success"
          icon="el-icon-plus"
          @click="handleChange(true)"
          >新增企业</el-button
        >
      </template>
    </SearchSection>

    <!-- 数据表格区域 -->
    <div class="table-section">
      <el-table
        :header-cell-style="{
          background: '#f7f8fa',
          color: '#606266',
          fontWeight: 600,
        }"
        :cell-style="{ padding: '8px 0' }"
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
        height="100%"
        stripe
      >
        <!-- 空数据状态 -->
        <template slot="empty">
          <EmptyState text="暂无企业数据" />
        </template>
        <el-table-column
          prop="companyName"
          label="企业名称"
          min-width="200"
          fixed="left"
        />
        <el-table-column
          prop="nickName"
          label="企业别名"
          width="150"
          show-overflow-tooltip
        />
        <el-table-column
          prop="corporateName"
          label="法人名称"
          width="150"
          show-overflow-tooltip
        />
        <el-table-column
          prop="creditCode"
          label="统一社会信用代码"
          min-width="200"
        />
        <el-table-column
          prop="province"
          label="省"
          width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="city"
          label="市"
          width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="establishedTime"
          label="企业成立时间"
          min-width="150"
        />
        <el-table-column label="停款状态" width="120">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.stopPurchase === 0 ? 'danger' : 'success'"
              size="small"
            >
              {{ scope.row.stopPurchase === 0 ? "停款" : "正常" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账期类型" width="120">
          <template slot-scope="scope">
            {{ formatAccountingPeriod(scope.row.accountingPeriod) }}
          </template>
        </el-table-column>
        <el-table-column label="是否禁抢" width="120">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.grabStatus === 1 ? 'danger' : 'success'"
              size="small"
            >
              {{ scope.row.grabStatus === 1 ? "禁止" : "未禁止" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="300">
          <template slot-scope="scope">
            <div class="table-action-btn">
              <el-button
                size="mini"
                type="primary"
                @click="handleChange(false, scope.row)"
                >编辑企业</el-button
              >
              <el-button size="mini" type="info" @click="handleEdit(scope.row)"
                >子账户信息</el-button
              >
              <el-button size="mini" @click="handleAccountCard(scope.row)"
                >财务卡号</el-button
              >
            </div>
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

    <!-- 子账户信息弹框 -->
    <SubAccountDialog
      ref="subAccountDialog"
      :visible.sync="subAccountDialogVisible"
      :company-name="currentCompanyName"
      :company-id="currentCompanyId"
      @add-user="handleAddUser"
    />

    <!-- 新增用户弹框 -->
    <AddUserDialog
      :visible.sync="addUserDialogVisible"
      :form-data="addUserForm"
      :company-id="currentCompanyId"
      @reset-form="handleResetAddUserForm"
      @save="handleSaveUser"
    />

    <!-- 银行账户信息弹框 -->
    <AccountCardDialog
      ref="accountCardDialog"
      :visible.sync="accountCardDialogVisible"
      :company-id="currentCompanyId"
      @open-form="handleOpenAccountForm"
    />

    <!-- 银行账户表单弹框 -->
    <AccountFormDialog
      :visible.sync="accountFormDialogVisible"
      :edit-data="accountFormData"
      :is-edit="isEditAccount"
      :company-id="currentCompanyId"
      @success="handleAccountFormSuccess"
    />

    <!-- 企业信息表单弹框 -->
    <CompanyFormDialog
      :visible.sync="companyFormDialogVisible"
      :edit-data="companyFormData"
      :is-edit="isEditCompany"
      @save="handleAddOrEditCompanySave"
    />
  </div>
</template>

<script>
import SearchSection from "./components/searchSection.vue";
import SubAccountDialog from "./components/subAccountDialog.vue";
import AddUserDialog from "./components/addUserDialog.vue";
import AccountCardDialog from "./components/accountCardDialog.vue";
import AccountFormDialog from "./components/accountFormDialog.vue";
import CompanyFormDialog from "./components/companyFormDialog.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import { getBusinessCompanyListApi } from "@/api/business";

export default {
  name: "BusinessManage",
  components: {
    SearchSection,
    SubAccountDialog,
    AddUserDialog,
    AccountCardDialog,
    AccountFormDialog,
    CompanyFormDialog,
    EmptyState,
  },
  data() {
    return {
      loading: false,
      // 子账户弹框相关
      subAccountDialogVisible: false,
      currentCompanyName: "",
      currentCompanyId: "",
      // 新增用户弹框相关
      addUserDialogVisible: false,
      addUserForm: {
        nickName: "",
        phone: "",
        owner: 0
      },
      // 银行账户信息弹框相关
      accountCardDialogVisible: false,
      // 银行账户表单弹框相关
      accountFormDialogVisible: false,
      accountFormData: {},
      isEditAccount: false,
      // 企业信息表单弹框相关
      companyFormDialogVisible: false,
      companyFormData: {},
      isEditCompany: false,
      searchForm: {
        companyNameLike: "",
        province: "",
        city: "",
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0,
      },
    };
  },
  created() {
    this.loadTableData();
  },
  methods: {
    // 搜索
    handleSearch(searchData) {
      this.searchForm = { ...searchData };
      // 重置分页到第一页
      this.pagination.currentPage = 1;
      this.loadTableData();
    },
    // 重置
    handleReset(searchData) {
      this.searchForm = { ...searchData };
      // 重置分页到第一页
      this.pagination.currentPage = 1;
      this.loadTableData();
    },
    // 编辑 - 打开子账户信息弹框
    handleEdit(row) {
      this.currentCompanyName = row.companyName;
      this.currentCompanyId = row.id;
      this.subAccountDialogVisible = true;
    },
    // 新增用户
    handleAddUser() {
      // 确保 companyId 被正确设置到表单中
      this.addUserForm = {
        ...this.addUserForm,
        companyId: this.currentCompanyId
      };
      this.addUserDialogVisible = true;
    },
    // 重置新增用户表单
    handleResetAddUserForm() {
      this.addUserForm = {
        nickName: "",
        phone: "",
        companyId: "",
        owner: 0
      };
    },
    // 保存用户
    handleSaveUser() {
      this.$message.success("用户添加成功");
      this.addUserDialogVisible = false;
      // 通知子账户弹框刷新数据
      this.$refs.subAccountDialog &&
        this.$refs.subAccountDialog.getSubAccountList();
    },
    // 加载表格数据
    async loadTableData() {
      this.loading = true;
      const params = {
        ...this.searchForm,
      };
      const pageData = {
        pageNum: this.pagination.currentPage,
        pageSize: this.pagination.pageSize,
      }
      const res = await getBusinessCompanyListApi(params,pageData);
      if (res.code === 200) {
        this.tableData = res.rows;
        this.pagination.total = res.total;
      }
      this.loading = false;
    },
    // 新增/编辑企业信息 弹框
    handleChange(isAdd, row) {
      this.companyFormDialogVisible = true;
      this.isEditCompany = !isAdd;
      if (isAdd) {
        this.companyFormData = {};
      } else {
        this.companyFormData = { ...row };
      }
    },
    // 新增/编辑企业信息保存成功回调
    handleAddOrEditCompanySave() {
      // 刷新表格数据
      this.loadTableData();
    },
    // 银行账户信息弹框
    async handleAccountCard(row) {
      this.currentCompanyId = row.id;
      this.accountCardDialogVisible = true;
    },
    // 打开银行账户表单弹框 新增编辑银行账户
    handleOpenAccountForm(data, isEdit = false) {
      this.accountFormData = data || {};
      this.isEditAccount = isEdit;
      this.accountFormDialogVisible = true;
    },
    // 银行账户表单操作成功回调
    handleAccountFormSuccess() {
      this.accountFormDialogVisible = false;
      this.accountFormData = {};
      this.isEditAccount = false;

      // 刷新银行账户列表
      if (this.$refs.accountCardDialog) {
        this.$refs.accountCardDialog.getAccountList();
      }
    },

    // 分页大小改变
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.loadTableData();
    },

    // 当前页改变
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadTableData();
    },
    // 格式化账期类型
    formatAccountingPeriod(value) {
      if (value === null || value === undefined || value === '') {
        return '-';
      }
      const periodMap = {
        0: '现款',
        1: 'T+1',
        2: 'T+2',
        3: 'T+3',
        4: 'T+4',
        5: 'T+5',
        6: 'T+6',
        7: 'T+7',
      };
      return periodMap[value] || '-';
    },
  },
};
</script>

<style scoped lang="scss">
.business-manage-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);

  .table-section {
    background: #fff;
    padding: 24px;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border: 1px solid #f0f0f0;
    flex: 1;
    overflow: auto;

    .el-table {
      border-radius: 6px;
      overflow: hidden;

      th {
        background-color: #fafafa;
        color: #303133;
        font-weight: 600;
        border-bottom: 1px solid #ebeef5;
      }

      td {
        border-bottom: 1px solid #f0f0f0;
      }

      .el-table__row:hover {
        background-color: #f5f7fa;
      }
    }
  }

  .table-action-btn {
    display: flex;
    gap: 6px;
    justify-content: space-around;

    &:last-of-type {
      margin-top: 10px;
    }
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
}

// 确保所有输入框高度一致
.el-input,
.el-select,
.el-date-editor {
  .el-input__inner {
    height: 32px;
    line-height: 32px;
  }
}

// 确保按钮高度与输入框一致
.el-button {
  height: 32px;
  padding: 7px 15px;
}
</style>
