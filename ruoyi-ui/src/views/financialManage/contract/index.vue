<!-- 合同管理 -->
<template>
  <div class="app-container contract-page">
    <!-- 筛选区 -->
    <div class="search-card">
    <el-form
      :model="queryForm"
      ref="queryForm"
      :inline="true"
      @submit.native.prevent
      class="query-form"
    >
      <el-form-item label="我方主体">
        <el-select
          v-model="queryForm.ourPayerId"
          placeholder="全部"
          clearable
          filterable
          style="width: 200px"
        >
          <el-option
            v-for="p in payerOptions"
            :key="p.id"
            :label="p.payName"
            :value="p.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="供应商">
        <el-select
          v-model="queryForm.vendorCompanyId"
          placeholder="搜索供应商"
          filterable
          remote
          clearable
          :remote-method="searchCompany"
          :loading="companyLoading"
          style="width: 220px"
        >
          <el-option
            v-for="c in companyOptions"
            :key="c.id"
            :label="c.companyName"
            :value="c.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="合同类型">
        <el-select
          v-model="queryForm.contractTypeList"
          placeholder="全部"
          multiple
          collapse-tags
          clearable
          style="width: 200px"
        >
          <el-option label="采购合同" :value="1" />
          <el-option label="框架协议" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select
          v-model="queryForm.statusList"
          placeholder="全部"
          multiple
          collapse-tags
          clearable
          style="width: 220px"
        >
          <el-option
            v-for="s in statusOptions"
            :key="s.value"
            :label="s.label"
            :value="s.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery"
          >查询</el-button
        >
        <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-wrapper">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        height="100%"
      >
      <el-table-column label="合同编号" prop="contractNo" min-width="150" />
      <el-table-column label="合同名称" prop="contractName" min-width="180" />
      <el-table-column label="类型" width="100">
        <template slot-scope="scope">
          {{ contractTypeText(scope.row.contractType) }}
        </template>
      </el-table-column>
      <el-table-column
        label="我方主体"
        prop="ourPayerName"
        min-width="160"
      />
      <el-table-column
        label="供应商"
        prop="vendorCompanyName"
        min-width="180"
      />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusTag(scope.row.status)" size="small">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="签署完成时间" prop="signedTime" width="160" />
      <el-table-column label="操作" width="320" fixed="right">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="mini"
            @click="handleDetail(scope.row)"
            >详情</el-button
          >
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            size="mini"
            @click="handleEdit(scope.row)"
            >编辑</el-button
          >
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            size="mini"
            @click="handleLaunch(scope.row)"
            >发起签署</el-button
          >
          <el-button
            v-if="scope.row.status === 1"
            type="text"
            size="mini"
            @click="handleCopySignUrl(scope.row, 'our')"
            >我方签署链接</el-button
          >
          <el-button
            v-if="scope.row.status === 1"
            type="text"
            size="mini"
            @click="handleCopySignUrl(scope.row, 'vendor')"
            >供应商签署链接</el-button
          >
          <el-button
            v-if="scope.row.status === 1"
            type="text"
            size="mini"
            @click="handleSync(scope.row)"
            >同步状态</el-button
          >
          <el-button
            v-if="scope.row.status === 1"
            type="text"
            size="mini"
            class="danger-text"
            @click="handleCancel(scope.row)"
            >撤销</el-button
          >
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            size="mini"
            class="danger-text"
            @click="handleRemove(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    </div>

    <div class="pagination-wrapper">
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryForm.pageNum"
        :limit.sync="queryForm.pageSize"
        :page-sizes="[30, 50, 100]"
        @pagination="loadData"
      />
    </div>

    <contract-form-dialog
      :visible.sync="formVisible"
      :detail="currentDetail"
      @success="loadData"
    />

    <contract-launch-dialog
      :visible.sync="launchVisible"
      :contract="currentDetail"
      @success="loadData"
    />
  </div>
</template>

<script>
import {
  pageContractApi,
  getContractApi,
  removeContractApi,
  cancelContractApi,
  syncContractStatusApi,
  getSignUrlApi,
  listPayerApi,
  listCompanyApi,
} from "@/api/contract";
import ContractFormDialog from "./components/ContractFormDialog.vue";
import ContractLaunchDialog from "./components/ContractLaunchDialog.vue";

const STATUS_OPTIONS = [
  { value: 0, label: "草稿", tag: "info" },
  { value: 1, label: "签署中", tag: "warning" },
  { value: 2, label: "已完成", tag: "success" },
  { value: 3, label: "已拒签", tag: "danger" },
  { value: 4, label: "已过期", tag: "info" },
  { value: 5, label: "已撤销", tag: "info" },
];

export default {
  name: "ContractIndex",
  components: { ContractFormDialog, ContractLaunchDialog },
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      queryForm: {
        pageNum: 1,
        pageSize: 30,
        contractTypeList: [],
        statusList: [],
        ourPayerId: null,
        vendorCompanyId: null,
      },
      payerOptions: [],
      companyOptions: [],
      companyLoading: false,
      formVisible: false,
      launchVisible: false,
      currentDetail: null,
      statusOptions: STATUS_OPTIONS,
    };
  },
  created() {
    this.loadPayer();
    this.loadData();
  },
  methods: {
    contractTypeText(t) {
      if (t === 1) return "采购合同";
      if (t === 2) return "框架协议";
      return "-";
    },
    statusText(s) {
      const o = STATUS_OPTIONS.find((i) => i.value === s);
      return o ? o.label : "-";
    },
    statusTag(s) {
      const o = STATUS_OPTIONS.find((i) => i.value === s);
      return o ? o.tag : "info";
    },
    async loadPayer() {
      const res = await listPayerApi();
      this.payerOptions = res.data || res || [];
    },
    async searchCompany(keyword) {
      if (!keyword) {
        this.companyOptions = [];
        return;
      }
      this.companyLoading = true;
      try {
        const res = await listCompanyApi({ companyName: keyword });
        this.companyOptions = res.rows || res.data || [];
      } finally {
        this.companyLoading = false;
      }
    },
    buildPayload() {
      return { ...this.queryForm };
    },
    async loadData() {
      this.loading = true;
      try {
        const res = await pageContractApi(this.buildPayload());
        this.tableData = res.rows || [];
        this.total = res.total || 0;
      } finally {
        this.loading = false;
      }
    },
    handleQuery() {
      this.queryForm.pageNum = 1;
      this.loadData();
    },
    handleReset() {
      this.queryForm.contractTypeList = [];
      this.queryForm.statusList = [];
      this.queryForm.ourPayerId = null;
      this.queryForm.vendorCompanyId = null;
      this.handleQuery();
    },
    async handleEdit(row) {
      const res = await getContractApi(row.id);
      this.currentDetail = res.data || res;
      this.formVisible = true;
    },
    async handleDetail(row) {
      const res = await getContractApi(row.id);
      const d = res.data || res;
      this.$alert(this.formatDetail(d), "合同详情", {
        dangerouslyUseHTMLString: true,
        confirmButtonText: "关闭",
      });
    },
    formatDetail(d) {
      return `
        <div style="line-height:1.8">
          <div>合同编号：${d.contractNo || "-"}</div>
          <div>合同名称：${d.contractName || "-"}</div>
          <div>类型：${this.contractTypeText(d.contractType)}</div>
          <div>我方主体：${d.ourPayerName || "-"}</div>
          <div>供应商：${d.vendorCompanyName || "-"}</div>
          <div>状态：${this.statusText(d.status)}</div>
          <div>e签宝流程ID：${d.esignFlowId || "-"}</div>
          <div>e签宝文件ID：${d.esignFileId || "-"}</div>
          <div>过期时间：${d.expireTime || "-"}</div>
          <div>签署完成时间：${d.signedTime || "-"}</div>
          <div>创建人：${d.createName || "-"}</div>
          <div>创建时间：${d.createTime || "-"}</div>
          <div>备注：${d.remark || "-"}</div>
        </div>`;
    },
    async handleLaunch(row) {
      this.currentDetail = row;
      this.launchVisible = true;
    },
    async handleCancel(row) {
      await this.$confirm("确认撤销当前签署中合同？", "提示", {
        type: "warning",
      });
      await cancelContractApi(row.id);
      this.$message.success("已撤销");
      this.loadData();
    },
    async handleRemove(row) {
      await this.$confirm("确认删除该草稿合同？", "提示", {
        type: "warning",
      });
      await removeContractApi(row.id);
      this.$message.success("已删除");
      this.loadData();
    },
    async handleSync(row) {
      await syncContractStatusApi(row.id);
      this.$message.success("已触发同步");
      this.loadData();
    },
    async handleCopySignUrl(row, signerType) {
      const res = await getSignUrlApi(row.id, signerType);
      const url = res.data || res;
      try {
        await navigator.clipboard.writeText(url);
        this.$message.success("签署链接已复制到剪贴板");
      } catch (e) {
        this.$alert(url, "签署链接", { confirmButtonText: "关闭" });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.contract-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 112px);
  background: var(--bg-page);
  padding: 12px;
  box-sizing: border-box;
  overflow: hidden;

  .search-card {
    flex-shrink: 0;
    background: var(--adm-card);
    border-radius: var(--adm-radius-card);
    box-shadow: var(--adm-shadow-card);
    padding: 16px;
    margin-bottom: 12px;

    .query-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }
  .table-wrapper {
    flex: 1;
    overflow: hidden;
    min-height: 0;
    background: var(--adm-card);
    border-radius: var(--adm-radius-card);
    box-shadow: var(--adm-shadow-card);
  }
  .danger-text {
    color: #f56c6c;
  }
  .pagination-wrapper {
    flex-shrink: 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 12px 24px;
    margin-top: 12px;
    background: var(--adm-card);
    border-radius: var(--adm-radius-card);
    box-shadow: var(--adm-shadow-card);
  }
}
</style>
