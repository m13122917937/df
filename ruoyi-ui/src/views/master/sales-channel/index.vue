<template>
  <div class="app-container sales-channel-page">
    <section class="sales-channel-card sales-channel-filter-card">
      <el-form :inline="true" :model="queryParams" size="small" @submit.native.prevent>
        <el-form-item label="渠道编码">
          <el-input v-model="queryParams.channelCodeLike" clearable placeholder="请输入渠道编码" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="渠道名称">
          <el-input v-model="queryParams.channelNameLike" clearable placeholder="请输入渠道名称" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="平台名称">
          <el-input v-model="queryParams.platformNameLike" clearable placeholder="请输入平台名称" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="经营主体">
          <el-input v-model="queryParams.subjectNameLike" clearable placeholder="请输入经营主体" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item class="sales-channel-filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="sales-channel-card sales-channel-table-card">
      <div class="sales-channel-card-title">
        <div>
          <h3>销售渠道</h3>
          <p>数据由吉客云每日定时同步维护，仅供业务查询使用。</p>
        </div>
      </div>
      <el-table v-loading="loading" :data="channelList" border stripe>
        <el-table-column prop="channelCode" label="渠道编码" min-width="130" show-overflow-tooltip />
        <el-table-column prop="channelName" label="渠道名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="platformName" label="平台" min-width="130" show-overflow-tooltip />
        <el-table-column prop="subjectName" label="经营主体" min-width="200" show-overflow-tooltip />
        <el-table-column prop="warehouseName" label="仓库" min-width="160" show-overflow-tooltip />
        <el-table-column prop="contactName" label="联系人" min-width="100" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" min-width="130" show-overflow-tooltip />
        <el-table-column prop="lastSyncTime" label="最后同步时间" min-width="180" />
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </section>
  </div>
</template>

<script>
import { getMasterSalesChannelList } from '@/api/master'

export default {
  name: 'MasterSalesChannel',
  data() {
    return {
      loading: false,
      total: 0,
      channelList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        channelCodeLike: '',
        channelNameLike: '',
        platformNameLike: '',
        subjectNameLike: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getMasterSalesChannelList(this.queryParams).then(response => {
        this.channelList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        channelCodeLike: '',
        channelNameLike: '',
        platformNameLike: '',
        subjectNameLike: ''
      }
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
.sales-channel-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 100%;
  color: var(--nl-color);
}

.sales-channel-card {
  padding: 20px;
  border-radius: 8px;
  background: var(--bg-card);
}

.sales-channel-filter-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.sales-channel-filter-actions {
  margin-right: 0;
}

.sales-channel-card-title {
  margin-bottom: 16px;
}

.sales-channel-card-title h3,
.sales-channel-card-title p {
  margin: 0;
}

.sales-channel-card-title h3 {
  font-size: 16px;
}

.sales-channel-card-title p {
  margin-top: 6px;
  color: var(--nl-color-weak);
  font-size: 13px;
}

@media (max-width: 768px) {
  .sales-channel-card {
    padding: 16px;
  }
}
</style>
