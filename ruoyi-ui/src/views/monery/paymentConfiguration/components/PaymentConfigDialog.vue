<template>
  <el-dialog
    :title="isEdit ? '修改配置' : '新增配置'"
    :visible.sync="dialogVisible"
    width="600px"
    :before-close="handleClose"
  >
    <el-form
      :model="form"
      :rules="formRules"
      ref="form"
      label-width="120px"
    >
      <el-form-item label="店铺名称" prop="keyWord">
        <el-input
          v-model.trim="form.keyWord"
          placeholder="请输入店铺名称"
          :clearable="!isEdit"
          :disabled="isEdit"
        />
      </el-form-item>
      <el-form-item label="付款主体" prop="payerId">
        <el-select
          v-model="form.payerId"
          placeholder="请选择付款主体"
          filterable
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="item in payerNameOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platform">
        <el-select
          v-model="form.platform"
          placeholder="请选择平台"
          filterable
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="item in platformOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'PaymentConfigDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    isEdit: {
      type: Boolean,
      default: false
    },
    formData: {
      type: Object,
      default: () => ({
        keyWord: '',
        payerId: '',
        platform: ''
      })
    },
    keyWordOptions: {
      type: Array,
      default: () => []
    },
    payerNameOptions: {
      type: Array,
      default: () => []
    },
    platformOptions: {
      type: Array,
      default: () => []
    },
  },
  data() {
    return {
      dialogVisible: false,
      form: {
        keyWord: '',
        payerName: ''
      },
      formRules: {
        keyWord: [
          { required: true, message: '请输入店铺名称', trigger: 'blur' }
        ],
        payerId: [
          { required: true, message: '请选择付款主体', trigger: 'change' }
        ],
        platform: [
          { required: true, message: '请选择平台', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form = { ...this.formData }
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    },
    formData: {
      handler(val) {
        if (this.dialogVisible) {
          this.form = { ...val }
        }
      },
      deep: true
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.resetForm()
    },
    resetForm() {
      this.form = {
        keyWord: '',
        payerId: '',
        platform: ''
      }
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.$emit('submit', {
            isEdit: this.isEdit,
            formData: { ...this.form }
          })
        } else {
          this.$message.error('请检查表单信息')
          return false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
</style>

