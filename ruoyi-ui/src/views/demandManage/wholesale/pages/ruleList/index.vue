<template>
  <div class="rule-list-container">
    <!-- 头部操作区域 -->
    <div class="header-section">
      <div class="left-info">
        <span>已选{{ selectedCount }}项</span>
        <!-- <el-button type="primary" @click="handleEnable(selectedItems,'multiple')" :disabled="selectedCount === 0">启用</el-button>
        <el-button type="primary" @click="handleDisable(selectedItems,'multiple')" :disabled="selectedCount === 0">暂停</el-button> -->
        <el-button type="primary" @click="deleteRule(selectedItems,'multiple')" :disabled="selectedCount === 0">删除</el-button>
      </div>
      <div class="right-actions">
        <el-button  @click="goCreatingOrders">返回列表</el-button>
        <!-- <el-button type="primary" icon="el-icon-plus" @click="showProcurementDialog">新建规则</el-button> -->
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        :data="tableDataList"
        style="width: 100%"
        v-loading="tableLoading"
        border
         @selection-change="handleSelectionChange"
      >
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column prop="brand" label="品牌" min-width="200" align="center">
      </el-table-column>
      <el-table-column prop="category" label="品类" min-width="200" align="center">
      </el-table-column>
      <el-table-column prop="provinceName" label="省" min-width="200" align="center">
      </el-table-column>
      <el-table-column prop="productName" label="产品型号" min-width="200" align="center">
        <template slot-scope="{ row }">
          <div>
            <div>{{ row.productName }}</div>
            <div>{{ row.skuName}}</div>
          </div>
        </template>
      </el-table-column>
        <el-table-column prop="accountingPeriod" label="账期类型" width="100" align="center">
          <template slot-scope="{ row }">
          <div>
            {{ row.accountingPeriodType | accountingFilters}}
          </div>
        </template>
        </el-table-column>
        <el-table-column label="价格"  min-width="300" align="center">
          <template slot-scope="{ row }">
            <el-tag
              type="danger"
              effect="dark"
              style="margin-right: 5px;"
              size="small"
            >
            ￥{{ row.priceHighest }}
            </el-tag>
            <el-tag
              type="danger"
              effect="dark"
              style="margin-right: 5px;"
              size="small"
            >
            ￥{{ row.priceHign }}
            </el-tag>
            <el-tag
              type="danger"
              effect="dark"
              style="margin-right: 5px;"
              size="small"
            >
            ￥{{ row.priceLow }}
            </el-tag>
            <el-tag
              type="danger"
              effect="dark"
              size="small"
            >
            ￥{{ row.priceLowest }}
            </el-tag>
          </template>
        </el-table-column>


        <el-table-column prop="status" label="状态" min-width="200" align="center">
          <template slot-scope="scope">
            <div class="action-buttons">
              {{ scope.row.status == '1' ? '启用' : '暂停' }}
            </div>
          </template>
        </el-table-column>
        <!-- 操作 -->
        <el-table-column label="操作" fixed="right" width="220" align="center">
          <template slot-scope="scope">
            <div class="action-buttons">
              <!-- <el-button type="text" size="small" @click="editRule(scope.row)">编辑</el-button> -->
              <!-- <el-button
                type="text"
                size="small"
                v-if="scope.row.status == '1'"
                @click="handleDisable(scope.row,'single')"
              >
              暂停
              </el-button> -->
              <!-- <el-button
                type="text"
                size="small"
                v-else
                @click="handleEnable(scope.row,'single')"
              >
              启用
              </el-button> -->
              <el-button type="text" size="small"
                @click="deleteRule(scope.row,'single')"
              >删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 采购商品配置弹窗 -->
    <AddRule
      :visible.sync="procurementDialogVisible"
      :orderData="orderData"
      @confirm="handleProcurementConfirm"
    />
  </div>
</template>

<script>
import AddRule from '../../components/AddRule.vue'
import OrderStyleBadge from '@/components/OrderStyleBadge';
import { apiGetRuleList,apiDeleteRule,apiRuleEnable,apiRuleDisable } from '@/api/creatingOrders'
export default {
  name: 'RuleList',
  components: {
    AddRule,
    OrderStyleBadge
  },
  data() {
    return {
      selectedCount: 0,
      selectedItems: [],
      procurementDialogVisible: false,
      tableDataList: [],
      orderData:{},
      tableLoading:false
    }
  },
  filters: {
    accountingFilters(v) {
        let obj={
            0:"当天",
            1:"明天",
            2:"后天",
        }
        return obj[v]
      }
  },
  mounted(){
    this.getData()
  },
  methods: {
    // 启用规则
    handleEnable(row,type){
      let ids = []
      if(type == 'single'){
        ids = [row.id]
      }else{
        ids = row.map(val=>val.id)
      }
      apiRuleEnable({
        ruleIdList:ids
      }).then(res=>{
        this.getData()
      })
    },
    // 禁用规则
    handleDisable(row,type){
      let ids = []
      if(type == 'single'){
        ids = [row.id]
      }else{
        ids = row.map(val=>val.id)
      }
      apiRuleDisable({
        ruleIdList:ids
      }).then(res=>{
        this.getData()
      })
    },
    handleSelectionChange(selection) {
      this.selectedItems = selection
      this.selectedCount = selection.length
    },
    goCreatingOrders(){
      this.$router.push({
        path:"creatingOrders"
      })
    },



    showProcurementDialog() {
      this.procurementDialogVisible = true
    },
    editRule(row) {
      this.orderData = row;
      // 编辑规则逻辑
      this.procurementDialogVisible = true
    },

    async getData() {
      console.log("filterData")

      if (this.tableLoading) {
        this.$message.error("数据加载中，请勿操作！！！")
        return
      }
      this.tableLoading = true

      let params = {
        platform:"",
        province:"",
        ruleRange: "",
        skuCode:"",
        shopName:""
      }
      let res = await apiGetRuleList(params)

      const { code, rows = [], total = 0 } = res
      if (code != 200) {
        return
      }
      this.tableLoading = false
      this.tableDataList = rows || []
      this.totalNum = total || 0
    },
    // 删除规则
    deleteRule(row,type){
      let ids = []
      if(type == 'single'){
        ids = [row.id]
      }else{
        ids = row.map(val=>val.id)
      }

      this.$confirm('是否确认删除规则', '提示', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(() => {
        apiDeleteRule({
          ruleIdList:ids
        }).then(res=>{
          this.getData()
        })
      })
    },
    handleProcurementConfirm(formData) {
      this.getData()
      // 这里可以处理表单提交逻辑
      this.$message.success('配置保存成功')
    }
  }
}
</script>

<style scoped lang="scss">
.rule-list-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background-color: #ffffff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .left-info {
    font-size: 14px;
    color: #606266;
    display: flex;
    align-items: center;
    span{
       margin-right: 10px;
    }
  }

  .middle-actions {
    display: flex;
    gap: 10px;
  }

  .right-actions {
    display: flex;
    gap: 10px;


  }
}

.table-container {
  background-color: #ffffff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.product-model {
  line-height: 1.4;
  word-break: break-all;
}

.quotation {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  justify-content: center;
  align-items: center;

  .strike-through {
    text-decoration: line-through;
    color: #909399;
    font-size: 12px;
  }

  .current-price {
    color: #409eff;
    font-weight: bold;
    font-size: 14px;
  }
}

.quality-requirements {
  line-height: 1.4;
  font-size: 12px;
  color: #606266;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  justify-content: center;

  .el-button {
    padding: 0;
    margin: 0;
    font-size: 12px;
  }

  .warning-text {
    color: #e6a23c;
  }

  .success-text {
    color: #67c23a;
  }

  .danger-text {
    color: #f56c6c;
  }
}

// 表格样式调整
::v-deep .el-table {
  .el-table__header {
    th {
      background-color: #f5f7fa !important;
      color: #606266 !important;
      font-weight: 500;
    }
  }

  .el-table__body {
    tr {
      &:nth-child(even) {
        background-color: #fafafa;
      }
    }
  }

  .el-table__row {
    &:hover {
      background-color: #f5f7fa !important;
    }
  }
}
</style>
