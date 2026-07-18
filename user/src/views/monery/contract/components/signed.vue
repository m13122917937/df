<template>
  <div class="signed-contract">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>已签署合同</span>
      </div>
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
        <el-table-column prop="ourPayerName" label="我方主体" min-width="160" show-overflow-tooltip />
        <el-table-column prop="signedTime" label="签署完成时间" width="170" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.signedTime || '--' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="170" align="center">
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
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope.row)">
              详情
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
          <div class="detail-cell full">
            <span class="detail-label">我方主体</span>
            <span class="detail-value">{{ detailData.ourPayerName || '-' }}</span>
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
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listContract,
  getContract,
  CONTRACT_TYPE_MAP,
  CONTRACT_STATUS_MAP,
  CONTRACT_STATUS_TAG_MAP
} from '@/api/contract'

export default {
  name: 'SignedContract',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      contractTypeMap: CONTRACT_TYPE_MAP,
      contractStatusMap: CONTRACT_STATUS_MAP,
      contractStatusTagMap: CONTRACT_STATUS_TAG_MAP,
      contractTypeOptions: CONTRACT_TYPE_MAP,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contractName: '',
        contractTypeList: []
      },
      detailVisible: false,
      detailData: null
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    buildQuery() {
      const { pageNum, pageSize, contractName, contractTypeList } = this.queryParams
      const payload = {
        pageNum,
        pageSize,
        // 已签署 = 状态已完成
        statusList: [2]
      }
      if (contractName && contractName.trim()) {
        payload.contractName = contractName.trim()
      }
      if (Array.isArray(contractTypeList) && contractTypeList.length) {
        payload.contractTypeList = contractTypeList
      }
      return payload
    },
    async loadData() {
      this.loading = true
      try {
        const res = await listContract(this.buildQuery())
        this.tableData = res.rows || []
        this.total = res.total || 0
      } catch (error) {
        console.error('查询已签署合同失败:', error)
        this.tableData = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.loadData()
    },
    handleRefresh() {
      this.loadData()
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
    async handleDetail(row) {
      try {
        const res = await getContract(row.id)
        this.detailData = res.data || res
        this.detailVisible = true
      } catch (error) {
        console.error('查询合同详情失败:', error)
      }
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
