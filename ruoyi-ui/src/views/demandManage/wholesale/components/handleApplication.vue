<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="处理申请"
    width="900px"
    :close-on-click-modal="false"
    :modal="true"
    :append-to-body="true"
    :z-index="3000"
    @close="handleClose"
    class="procurement-dialog-wrapper"
  >
    <!-- 明细 -->
    <div class="apply-detail">
      <div class="detail-title">
        <i class="el-icon-document"></i>
        <span>申请明细</span>
      </div>

      <div class="detail-content">
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="12">
            <div class="detail-item">
              <label>申请ID：</label>
              <span class="detail-value">{{ applyData.id || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>公司名称：</label>
              <span class="detail-value">{{ applyData.companyName || '-' }}</span>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="12">
            <div class="detail-item">
              <label>申请类型：</label>
              <span class="detail-value apply-type">{{ applyType || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <label>审批状态：</label>
              <span class="detail-value" :class="getStatusClass(applyData.status)">
                {{ applyStatus || '-' }}
              </span>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <label>申请备注：</label>
              <span class="detail-value apply-type">{{ applyData.remark || '-' }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 同意/拒绝 -->
    <div class="action-section" v-if="applyData.status != 2">
      <div class="action-title">
        <i class="el-icon-s-check"></i>
        <span>处理操作</span>
      </div>

      <div class="action-content">
        <el-radio-group v-model="handleResult">
          <el-radio :label="1">
            <i class="el-icon-check"></i>
            同意申请
          </el-radio>
          <el-radio :label="2">
            <i class="el-icon-close"></i>
            拒绝申请
          </el-radio>
        </el-radio-group>

        <div v-if="handleResult === 2" class="reject-reason">
          <el-input
            v-model="refuseRemark	"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝原因"
            maxlength="200"
            show-word-limit
          ></el-input>
        </div>
      </div>
    </div>

    <div slot="footer" class="dialog-footer" v-if="applyData.status != 2">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import {apiGetApply,apiGetApplyFail,apiGetApplyAgreement} from "@/api/creatingOrders"
export default {
  name: 'handleApplication',
  props: {
    // 可以接收外部传入的申请ID
    applyId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      dialogVisible: false,
      applyData:{
        id:"",
        companyName:"",
        type:"", //申请类型
        status: "",
        remark:""
      },
      handleResult: null, // 处理结果 1-同意 2-拒绝
      refuseRemark: '', // 拒绝原因
    }
  },
  computed: {
    // 申请类型(1立即毁单 2免责毁单 4修改物流单号申请 )
    applyType() {
      let obj={
        1:"免责毁单",
        2:"免责毁单",
        4:"修改物流单号申请"
      }
      return obj[this.applyData.type]
    },
    // 审批状态 1 申请中 2 已拒绝 3 已审批
    applyStatus(){
      let obj={
        1:"申请中",
        2:"已拒绝",
        3:"已审批"
      }
      return obj[this.applyData.status]
    },

  },
  mounted(){

  },
  methods: {
    open(data){
      // 如果传入了申请ID，则使用传入的ID，否则使用传入的data
      const orderCode = data.orderCode

      if (orderCode) {
        apiGetApply(orderCode).then(res=>{
          const { id="", companyName="", type="", status="", remark=""} = res.data
          this.applyData={
            id:id,
            companyName:companyName,
            type:type,
            status:status,
            remark:remark
          }
        }).catch(err => {
          this.$message.error('获取申请详情失败')
          console.error(err)
        })
      } else if (data) {
        // 直接使用传入的数据
        this.applyData = { ...data }
      }

      this.dialogVisible = true
      this.resetForm()
    },

    // 重置表单
    resetForm() {
      this.handleResult = null
      this.refuseRemark	 = ''
    },

    // 获取状态样式类
    getStatusClass(status) {
      const statusMap = {
        1: 'status-pending',
        2: 'status-rejected',
        3: 'status-approved'
      }
      return statusMap[status] || ''
    },

    // 关闭弹窗
    handleClose() {
      this.dialogVisible = false
      this.resetForm()
    },

    // 取消
    handleCancel() {
      this.handleClose()
    },

    // 确定处理
    handleConfirm() {
      if(this.handleResult==1){
        apiGetApplyAgreement(this.applyData.id).then(res=>{
          if(res.code){
            this.$message.success("操作成功")
            this.handleClose()
            this.$emit("confirm")
          }
        })
        return
      }

      if(this.handleResult==2){
        apiGetApplyFail({
          applyId:this.applyData.id,
          refuseRemark:this.refuseRemark
        }).then(res=>{
          if(res.code){
            this.$message.success("操作成功")
            this.handleClose()
            this.$emit("confirm")
          }
        })
        return
      }
    }
  }
}
</script>

<style scoped lang="scss">
// 明细区域样式
.apply-detail {
  margin-bottom: 24px;

  .detail-title {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 8px;
      color: #409eff;
    }
  }

  .detail-content {
    background: #f8f9fa;
    padding: 20px;
    border-radius: 6px;
    border: 1px solid #e9ecef;

    .detail-item {
      display: flex;
      align-items: center;
      margin-bottom: 16px;

      &:last-child {
        margin-bottom: 0;
      }

      label {
        min-width: 80px;
        font-weight: 500;
        color: #606266;
        margin-right: 12px;
      }

      .detail-value {
        color: #303133;
        font-weight: 500;

        &.apply-type {
          color: #409eff;
        }

        &.status-pending {
          color: #e6a23c;
        }

        &.status-rejected {
          color: #f56c6c;
        }

        &.status-approved {
          color: #67c23a;
        }
      }
    }
  }
}

// 操作区域样式
.action-section {
  margin-bottom: 24px;

  .action-title {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 8px;
      color: #67c23a;
    }
  }

  .action-content {
    .el-radio-group {
      margin-bottom: 16px;

      .el-radio {
        margin-right: 24px;

        i {
          margin-right: 6px;
        }
      }
    }

    .reject-reason {
      margin-top: 16px;
    }
  }
}

// 备注区域样式
.remark-section {
  .remark-title {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 8px;
      color: #909399;
    }
  }
}

.dialog-footer {
  text-align: right;

  .el-button {
    margin-left: 12px;
    padding: 8px 20px;
    border-radius: 4px;
    font-weight: 500;

    &.el-button--default {
      background: #ffffff;
      border-color: #d9d9d9;
      color: #4e5969;

      &:hover {
        border-color: #409eff;
        color: #409eff;
      }
    }

    &.el-button--primary {
      background: #409eff;
      border-color: #409eff;

      &:hover {
        background: #66b1ff;
        border-color: #66b1ff;
      }

      &:disabled {
        background: #c0c4cc;
        border-color: #c0c4cc;
        cursor: not-allowed;
      }
    }
  }
}


</style>
