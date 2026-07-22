<template>
  <div class="sign-info">
    <!-- 签署状态信息卡片 -->
    <el-card class="status-card">
      <div class="status-header">
        <span class="status-title">认证状态</span>
        <el-button type="warning" size="small" icon="el-icon-refresh" @click="handleResetAuth">
          重置认证状态
        </el-button>
      </div>
      <div class="status-content">
        <div class="status-item">
          <span class="status-label">当前认证状态</span>
          <el-tag :type="authStatus.type" class="status-value">
            {{ authStatus.text }}
          </el-tag>
          <a
            v-if="authStatus.type === 'danger'"
            class="go-auth-link"
            @click="handleGoAuth"
          >
            点击去认证
          </a>
        </div>
        <div class="status-item">
          <span class="status-label">待签署数量</span>
          <span class="status-count">{{ pendingCount }}</span>
        </div>
      </div>
    </el-card>

    <!-- 待签署合同列表 -->
    <el-card class="list-card" style="margin-top: 16px;">
      <div slot="header" class="clearfix">
        <span>待签署合同列表</span>
      </div>

      <!-- 搜索条件 -->
      <div class="table-operations">
        <el-input
          v-model="queryParams.contractName"
          placeholder="搜索合同名称"
          prefix-icon="el-icon-search"
          size="small"
          clearable
          style="width: 240px; margin-right: 10px;"
          @keyup.enter.native="handleQuery"
          @clear="handleQuery"
        />
        <el-select
          v-model="queryParams.contractTypeList"
          placeholder="合同类型"
          size="small"
          multiple
          collapse-tags
          clearable
          style="width: 200px; margin-right: 10px;"
          @change="handleQuery"
        >
          <el-option
            v-for="(label, value) in contractTypeOptions"
            :key="value"
            :label="label"
            :value="Number(value)"
          />
        </el-select>
        <el-select
          v-model="queryParams.statusList"
          placeholder="合同状态"
          size="small"
          multiple
          collapse-tags
          clearable
          style="width: 220px; margin-right: 10px;"
          @change="handleQuery"
        >
          <el-option
            v-for="value in pendingStatusValues"
            :key="value"
            :label="contractStatusMap[value]"
            :value="value"
          />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-search" @click="handleQuery">
          查询
        </el-button>
        <el-button size="small" icon="el-icon-refresh" @click="handleRefresh">
          刷新
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%; margin-top: 16px;"
      >
        <el-table-column prop="contractNo" label="合同编号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="contractName" label="合同名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contractType" label="合同类型" width="110" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.contractType === 1 ? 'primary' : 'success'" size="mini">
              {{ contractTypeMap[scope.row.contractType] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="contractStatusTagMap[scope.row.status] || 'info'" size="mini">
              {{ contractStatusMap[scope.row.status] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="签署过期时间" width="170" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.expireTime || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              style="color: #34C759"
              :disabled="scope.row.status !== 1"
              :loading="signLoadingId === scope.row.id"
              @click="handleSign(scope.row)"
            >
              签署
            </el-button>
            <el-button type="text" size="small" @click="handleDownload(scope.row)">
              下载合同
            </el-button>
            <el-button type="text" size="small" @click="handleViewContract(scope.row)">
              查看合同
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 合同详情弹窗 -->
    <el-dialog
      title="合同详情"
      :visible.sync="detailVisible"
      width="640px"
      :close-on-click-modal="false"
    >
      <div v-if="detailData" class="detail-grid">
        <div class="detail-row">
          <div class="detail-cell">
            <span class="detail-label">合同编号</span>
            <span class="detail-value">{{ detailData.contractNo || '-' }}</span>
          </div>
          <div class="detail-cell">
            <span class="detail-label">合同名称</span>
            <span class="detail-value">{{ detailData.contractName || '-' }}</span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-cell">
            <span class="detail-label">合同类型</span>
            <span class="detail-value">{{ contractTypeMap[detailData.contractType] || '-' }}</span>
          </div>
          <div class="detail-cell">
            <span class="detail-label">状态</span>
            <span class="detail-value">
              <el-tag :type="contractStatusTagMap[detailData.status] || 'info'" size="mini">
                {{ contractStatusMap[detailData.status] || '-' }}
              </el-tag>
            </span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-cell">
            <span class="detail-label">签署完成时间</span>
            <span class="detail-value">{{ detailData.signedTime || '--' }}</span>
          </div>
          <div class="detail-cell">
            <span class="detail-label">签署过期时间</span>
            <span class="detail-value">{{ detailData.expireTime || '--' }}</span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-cell full">
            <span class="detail-label">创建时间</span>
            <span class="detail-value">{{ detailData.createTime || '--' }}</span>
          </div>
        </div>
        <div class="detail-row">
          <div class="detail-cell full">
            <span class="detail-label">备注</span>
            <span class="detail-value">{{ detailData.remark || '-' }}</span>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
        <el-button
          v-if="detailData && detailData.status === 1"
          type="primary"
          :loading="signLoadingId === (detailData && detailData.id)"
          @click="handleSign(detailData)"
        >
          去签署
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getCompanyInfo, getCompanyAuthUrl } from '@/api/set'
import {
  listContract,
  getContract,
  getContractSignUrl,
  CONTRACT_TYPE_MAP,
  CONTRACT_STATUS_MAP,
  CONTRACT_STATUS_TAG_MAP
} from '@/api/contract'

// 待签署列表所聚焦的状态：草稿 / 签署中 / 已拒签 / 已过期 / 已撤销
const PENDING_STATUS_VALUES = [0, 1, 3, 4, 5]
const DEFAULT_STATUS_LIST = [0, 1]

export default {
  name: 'SignInfo',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      pendingCount: 0,
      authStatus: {
        type: 'danger',
        text: '未认证'
      },
      contractTypeMap: CONTRACT_TYPE_MAP,
      contractStatusMap: CONTRACT_STATUS_MAP,
      contractStatusTagMap: CONTRACT_STATUS_TAG_MAP,
      contractTypeOptions: CONTRACT_TYPE_MAP,
      pendingStatusValues: PENDING_STATUS_VALUES,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contractName: '',
        contractTypeList: [],
        statusList: [...DEFAULT_STATUS_LIST]
      },
      detailVisible: false,
      detailData: null,
      signLoadingId: null
    }
  },
  mounted() {
    this.getAuthStatus()
    this.loadData()
    this.fetchPendingCount()
  },
  methods: {
    // 获取企业认证状态
    async getAuthStatus() {
      try {
        const res = await getCompanyInfo()
        const { contractAuthStatus } = res.data || res
        if (contractAuthStatus === 1) {
          this.authStatus = { type: 'success', text: '已认证' }
        } else {
          this.authStatus = { type: 'danger', text: '未认证' }
        }
      } catch (error) {
        console.error('获取认证状态失败:', error)
        this.authStatus = { type: 'danger', text: '未认证' }
      }
    },
    // 去认证
    async handleGoAuth() {
      try {
        const res = await getCompanyAuthUrl()
        const authUrl = res.data || res
        if (authUrl) {
          window.open(authUrl, '_blank')
        } else {
          this.$message.error('获取认证地址失败')
        }
      } catch (error) {
        console.error('获取认证地址失败:', error)
        this.$message.error('获取认证地址失败，请稍后重试')
      }
    },
    // 构造请求参数
    buildQuery() {
      const { pageNum, pageSize, contractName, contractTypeList, statusList } = this.queryParams
      const payload = { pageNum, pageSize }
      if (contractName && contractName.trim()) {
        payload.contractName = contractName.trim()
      }
      if (Array.isArray(contractTypeList) && contractTypeList.length) {
        payload.contractTypeList = contractTypeList
      }
      // 默认仅查询待签署相关状态，避免和"已签署"页面重复
      const statuses = (Array.isArray(statusList) && statusList.length)
        ? statusList
        : [...DEFAULT_STATUS_LIST]
      payload.statusList = statuses
      return payload
    },
    // 加载列表
    async loadData() {
      this.loading = true
      try {
        const res = await listContract(this.buildQuery())
        this.tableData = res.rows || []
        this.total = res.total || 0
      } catch (error) {
        console.error('查询合同列表失败:', error)
        this.tableData = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    // 拉取顶部"待签署数量"（仅 status=1 签署中）
    async fetchPendingCount() {
      try {
        const res = await listContract({
          pageNum: 1,
          pageSize: 1,
          statusList: [1]
        })
        this.pendingCount = res.total || 0
      } catch (error) {
        console.error('查询待签署数量失败:', error)
        this.pendingCount = 0
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.loadData()
    },
    handleRefresh() {
      this.loadData()
      this.fetchPendingCount()
      this.getAuthStatus()
    },
    handleResetAuth() {
      this.$confirm('确定要重置认证状态吗？重置后需要重新认证签署。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // TODO: 调用重置认证API
        this.$message.success('认证状态已重置，请重新认证签署')
        this.getAuthStatus()
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNum = 1
      this.loadData()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.loadData()
    },
    // 查看合同
    async handleViewContract(row) {
      try {
        const res = await getContract(row.id)
        this.detailData = res.data || res
        this.detailVisible = true
      } catch (error) {
        console.error('查询合同详情失败:', error)
      }
    },
    // 下载合同
    handleDownload(row) {
      this.$message.warning('后端暂未提供合同下载接口')
    },
    // 去签署
    async handleSign(row) {
      if (!row || !row.id) return
      if (row.status !== 1) {
        this.$message.warning('当前合同状态不可签署')
        return
      }
      this.signLoadingId = row.id
      try {
        const res = await getContractSignUrl(row.id)
        const signUrl = res.data || res
        if (signUrl && typeof signUrl === 'string') {
          window.open(signUrl, '_blank')
          this.detailVisible = false
        } else {
          this.$message.error('获取签署链接失败')
        }
      } catch (error) {
        console.error('获取签署URL失败:', error)
      } finally {
        this.signLoadingId = null
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.sign-info {
  .status-card {
    .status-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .status-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .status-content {
      display: flex;
      gap: 60px;

      .status-item {
        display: flex;
        align-items: center;
        gap: 12px;

        .status-label {
          font-size: 14px;
          color: #606266;
        }

        .go-auth-link {
          color: #1677FF;
          cursor: pointer;
          font-size: 14px;
          text-decoration: underline;
          margin-left: 8px;

          &:hover {
            color: #3395FF;
          }
        }

        .status-count {
          font-size: 24px;
          font-weight: bold;
          color: #1677FF;
        }
      }
    }
  }

  .list-card {
    .table-operations {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 8px;
    }
    .pagination-wrapper {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

::v-deep .detail-label {
  width: 110px;
  background-color: #fafafa;
}

.detail-grid {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;

  .detail-row {
    display: flex;

    & + .detail-row {
      border-top: 1px solid #ebeef5;
    }
  }

  .detail-cell {
    flex: 1;
    display: flex;
    align-items: stretch;
    min-height: 40px;

    & + .detail-cell {
      border-left: 1px solid #ebeef5;
    }

    &.full {
      flex: 1 1 100%;
    }

    .detail-label {
      width: 110px;
      flex-shrink: 0;
      padding: 10px 12px;
      background-color: #fafafa;
      color: #606266;
      font-size: 13px;
      display: flex;
      align-items: center;
    }

    .detail-value {
      flex: 1;
      padding: 10px 12px;
      color: #303133;
      font-size: 13px;
      word-break: break-all;
      display: flex;
      align-items: center;
    }
  }
}
</style>
