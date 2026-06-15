<template>
  <el-dialog
    :title="isEdit ? '修改收款主体' : '新增收款主体'"
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
      <el-form-item label="收款企业名称" prop="payName">
        <el-input
          v-model.trim="form.payName"
          placeholder="请输入收款企业名称"
          :clearable="!isEdit"
          :disabled="isEdit"
        />
      </el-form-item>
      <el-form-item label="简称" prop="nickName">
        <el-input
          v-model.trim="form.nickName"
          placeholder="请输入简称"
          clearable
        />
      </el-form-item>
      <el-form-item label="吉客云编号" prop="outCode">
        <el-input
          v-model.trim="form.outCode"
          placeholder="请输入吉客云编号"
          clearable
        />
      </el-form-item>
      <el-form-item label="开户行全称" prop="bankName">
        <el-input
          v-model.trim="form.bankName"
          placeholder="请输入开户行全称"
          clearable
        />
      </el-form-item>
      <el-form-item label="收款账号" prop="payNo">
        <el-input
          v-model.trim="form.payNo"
          placeholder="请输入收款账号"
          clearable
        />
      </el-form-item>
      <el-form-item label="账户余额" prop="balance">
        <el-input-number
          v-model.number="form.balance"
          :min="0"
          :step="1"
          :precision="2"
          :disabled="isEdit"
          placeholder="请输入账户余额"
          style="width: 100%;"
          :formatter="formatCurrency"
          :parser="parseCurrency"
          @mousewheel.native.prevent
          @keydown.native="handleNumberKeydown"
          @paste.native="handleNumberPaste"
        />
      </el-form-item>
      <el-form-item label="是否激活" prop="actived">
        <el-radio-group v-model="form.actived">
          <el-radio :label="0">激活</el-radio>
          <el-radio :label="1">弃用</el-radio>
        </el-radio-group>
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
  name: 'PaymentEntityDialog',
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
        payName: '',
        nickName: '',
        outCode: '',
        bankName: '',
        payNo: '',
        balance: 0,
        actived: 0
      })
    },
    companyOptions: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      dialogVisible: false,
      form: {
        payName: '',
        nickName: '',
        outCode: '',
        bankName: '',
        payNo: '',
        balance: 0,
        actived: 0
      },
      formRules: {
        payName: [
          { required: true, message: '请输入收款企业名称', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '请输入简称', trigger: 'blur' }
        ],
        outCode: [
          { required: true, message: '请输入吉客云编号', trigger: 'blur' }
        ],
        bankName: [
          { required: true, message: '请输入开户行全称', trigger: 'blur' }
        ],
        payNo: [
          { required: true, message: '请输入收款账号', trigger: 'blur' }
        ],
        balance: [
          { required: true, message: '请输入账户余额', trigger: ['blur', 'change'] }
        ],
        actived: [
          { required: true, message: '请选择是否激活', trigger: 'change' }
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
        payName: '',
        nickName: '',
        outCode: '',
        bankName: '',
        payNo: '',
        balance: 0,
        actived: 0
      }
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    formatCurrency(value) {
      if (value === undefined || value === null || value === '') return ''
      const num = Number(value)
      if (!isFinite(num)) return ''
      return '¥ ' + num.toFixed(2)
    },
    parseCurrency(value) {
      if (!value) return 0
      return Number(String(value).replace(/[^\d.]/g, '')) || 0
    },
    // 仅允许数字和一个小数点
    handleNumberKeydown(e) {
      const allowKeys = ['Backspace', 'Tab', 'Delete', 'ArrowLeft', 'ArrowRight', 'Home', 'End', 'Enter']
      if (allowKeys.includes(e.key) || e.ctrlKey || e.metaKey) {
        return
      }
      const isNumber = /^[0-9]$/.test(e.key)
      const isDot = e.key === '.' && !String(e.target.value).includes('.')
      if (!isNumber && !isDot) {
        e.preventDefault()
      }
    },
    // 粘贴时校验，仅允许数字和小数点
    handleNumberPaste(e) {
      const text = (e.clipboardData || window.clipboardData).getData('text')
      if (!/^\d*(\.\d*)?$/.test(text)) {
        e.preventDefault()
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

