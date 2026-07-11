<template>
  <el-dialog :visible.sync="visible" width="1000px" title="Excel 批量导入" @close="handleClose" :close-on-click-modal="false">
    <!-- Step 1: Upload -->
    <div v-if="step === 'upload'" class="import-step">
      <div class="step-header">
        <span class="step-title">第一步：下载模板并上传文件</span>
      </div>
      <div class="template-download">
        <el-button type="text" icon="el-icon-download" @click="handleDownloadTemplate" :loading="templateLoading">
          下载导入模板
        </el-button>
        <span class="template-hint">（.xlsx 格式）</span>
      </div>
      <el-upload
        ref="upload"
        drag
        accept=".xlsx,.xls"
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="fileList"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将 Excel 文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">仅支持 .xlsx 格式</div>
      </el-upload>
      <div class="step-actions">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :disabled="!selectedFile" :loading="validating" @click="handleValidate">
          开始校验
        </el-button>
      </div>
    </div>

    <!-- Step 2: Validation Results -->
    <div v-if="step === 'result'" class="import-step">
      <div class="step-header">
        <span class="step-title">校验结果</span>
        <div class="step-summary">
          <span class="summary-total">共 {{ importResult.totalCount }} 条</span>
          <span class="summary-success">通过 {{ importResult.successCount }} 条</span>
          <span class="summary-error" v-if="importResult.errorCount > 0">错误 {{ importResult.errorCount }} 条</span>
        </div>
      </div>
      <el-table :data="importResult.rows" max-height="600" size="small" style="width: 100%">
        <el-table-column prop="rowIndex" label="行号" width="60" align="center" />
        <el-table-column prop="skuCode" label="SKU编码" min-width="120" />
        <el-table-column prop="productName" label="商品名" min-width="140" :show-overflow-tooltip="true" />
        <el-table-column prop="specName" label="规格名" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="companyName" label="供应商名称" min-width="140" />
        <el-table-column prop="quantity" label="数量" width="70" align="center" />
        <el-table-column prop="price" label="单价" width="90" align="right" />
        <el-table-column prop="accountingPeriod" label="账期(天)" width="80" align="center" />
        <el-table-column prop="payerName" label="付款主体" min-width="120" />
        <el-table-column label="状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.success" type="success" size="small">通过</el-tag>
            <el-tag v-else type="danger" size="small">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="错误信息" min-width="200">
          <template slot-scope="scope">
            <span v-if="scope.row.errorMessage" class="error-msg">{{ scope.row.errorMessage }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="step-actions">
        <el-button @click="handleBack">重新上传</el-button>
        <el-button type="primary" :disabled="importResult.successCount === 0" :loading="importing" @click="handleImport">
          确认导入（{{ importResult.successCount }} 条）
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { downloadImportTemplate, importValidate, importExcel } from '@/api/sale'

export default {
  name: 'ImportOrderDialog',
  data() {
    return {
      visible: false,
      step: 'upload',
      fileList: [],
      selectedFile: null,
      templateLoading: false,
      validating: false,
      importing: false,
      importResult: {
        totalCount: 0,
        successCount: 0,
        errorCount: 0,
        rows: []
      }
    }
  },
  methods: {
    open() {
      this.reset()
      this.visible = true
    },
    reset() {
      this.step = 'upload'
      this.fileList = []
      this.selectedFile = null
      this.importResult = { totalCount: 0, successCount: 0, errorCount: 0, rows: [] }
    },
    handleClose() {
      this.visible = false
      this.$emit('close')
    },
    handleDownloadTemplate() {
      this.templateLoading = true
      downloadImportTemplate().then(res => {
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = '入仓订单导入模板.xlsx'
        link.click()
        URL.revokeObjectURL(link.href)
      }).catch(() => {
        this.$message.error('模板下载失败')
      }).finally(() => {
        this.templateLoading = false
      })
    },
    handleFileChange(file) {
      this.selectedFile = file.raw
    },
    handleFileRemove() {
      this.selectedFile = null
    },
    async handleValidate() {
      if (!this.selectedFile) {
        this.$message.warning('请先选择文件')
        return
      }
      this.validating = true
      try {
        const res = await importValidate(this.selectedFile)
        if (res && res.code === 200) {
          this.importResult = res.data
          this.step = 'result'
          if (res.data.errorCount > 0) {
            this.$message.warning(`校验完成，存在 ${res.data.errorCount} 条错误数据`)
          } else {
            this.$message.success(`校验通过，共 ${res.data.successCount} 条`)
          }
        } else {
          this.$message.error(res?.msg || '校验失败')
        }
      } catch (error) {
        console.error('校验失败', error)
        this.$message.error('校验失败，请稍后重试')
      } finally {
        this.validating = false
      }
    },
    async handleImport() {
      if (!this.selectedFile || this.importResult.successCount === 0) return
      this.importing = true
      try {
        const res = await importExcel(this.selectedFile)
        if (res && res.code === 200) {
          this.$message.success(res.msg || '导入成功')
          this.visible = false
          this.$emit('imported')
        } else {
          this.$message.error(res?.msg || '导入失败')
        }
      } catch (error) {
        console.error('导入失败', error)
        this.$message.error('导入失败，请稍后重试')
      } finally {
        this.importing = false
      }
    },
    handleBack() {
      this.step = 'upload'
      this.fileList = []
      this.selectedFile = null
    }
  }
}
</script>

<style lang="scss" scoped>
.import-step {
  .step-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    .step-title {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }
    .step-summary {
      display: flex;
      gap: 12px;
      font-size: 13px;
      .summary-total { color: #909399; }
      .summary-success { color: #67c23a; font-weight: 600; }
      .summary-error { color: #f56c6c; font-weight: 600; }
    }
  }
  .template-download {
    margin-bottom: 16px;
    padding: 12px 16px;
    background: var(--bg-page);
    border-radius: 6px;
    .template-hint {
      font-size: 12px;
      color: #909399;
    }
  }
  .step-actions {
    margin-top: 20px;
    text-align: right;
  }
  .error-msg {
    color: #f56c6c;
    font-size: 12px;
  }
}
</style>
