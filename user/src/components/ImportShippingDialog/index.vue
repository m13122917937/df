<!-- 导入发货信息弹窗组件 -->
<template>
  <el-dialog
    title="批量发货"
    :visible.sync="dialogVisible"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="import-shipping-content">
      <el-upload
        class="upload-demo"
        drag
        accept=".xlsx"
        :headers="headers"
        :action="actionUrl"
        :before-upload="beforeUpload"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :show-file-list="false"
        :disabled="uploading"
      >
        <i class="el-icon-upload" />
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip">只能上传 xls 或 xlsx 格式的文件</div>
      </el-upload>
    </div>

  </el-dialog>
</template>

<script>
import { getToken } from '@/utils/auth'
// import { importShippingInfo } from '@/api/order'
export default {
  name: 'ImportShippingDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      uploading: false
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
    actionUrl() {
      return (process.env.VUE_APP_BASE_API || '') + '/order/delivery/ing/list/import'
    },
    headers() {
      return {
        Authorization: 'Bearer ' + getToken()
      }
    }
  },
  methods: {
    /**
     * Element UI Upload 的 before-upload 钩子
     * 在这里做文件类型校验
     */
    beforeUpload(file) {
      const name = file && file.name ? file.name : ''
      const ext = name.split('.').pop().toLowerCase()
      if (!['xls', 'xlsx'].includes(ext)) {
        this.$message.error('仅支持 xls 或 xlsx 格式的文件')
        return false
      }
      this.uploading = true
      return true // 允许上传
    },

    /**
     * 上传成功处理
     */
    handleUploadSuccess(response, file, fileList) {
      this.uploading = false
      if (response.code === 200) {
        this.$message.success('发货信息导入成功')
        this.dialogVisible = false
        // 刷新父组件的表格数据
        this.$emit('refresh-table')
      } else {
        this.$message.error(response.msg || '导入失败')
      }
    },

    /**
     * 上传失败处理
     * 处理上传失败时的错误
     */
    handleUploadError(err, file, fileList) {
      this.uploading = false
      // 优先展示后端返回的错误信息
      let errorMsg = '文件上传失败'
      if (err && err.response && err.response.data && err.response.data.msg) {
        errorMsg = err.response.data.msg
      } else if (err && err.message) {
        errorMsg = err.message
      }
      // 控制台打印详细错误方便调试
      // eslint-disable-next-line no-console
      console.error('Upload Error:', err)
      this.$message.error(errorMsg)
    },

    /**
     * 关闭弹窗
     */
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
// 导入发货信息弹窗样式
.import-shipping-content {
  padding: 20px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
