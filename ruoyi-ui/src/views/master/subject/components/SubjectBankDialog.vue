<template>
  <el-dialog
    title="主体银行卡管理"
    :visible.sync="dialogVisible"
    width="1200px"
    :before-close="handleClose"
    append-to-body
    :modal-append-to-body="false"
    top="5vh"
    class="subject-bank-dialog"
  >
    <div class="bank-toolbar">
      <span class="bank-subject-label">
        主体编码：<b>{{ subjectCode }}</b>
      </span>
      <el-button
        type="primary"
        size="small"
        icon="el-icon-plus"
        @click="handleAdd"
      >新增银行卡</el-button>
    </div>
    <el-table v-loading="loading" :data="bankList" border stripe size="small" max-height="60vh">
      <el-table-column prop="bankName" label="开户行" min-width="160" show-overflow-tooltip />
      <el-table-column prop="payNo" label="银行账号" min-width="180" show-overflow-tooltip />
      <el-table-column prop="payName" label="户名" min-width="160" show-overflow-tooltip />
      <el-table-column prop="outCode" label="吉客云编号" min-width="130" show-overflow-tooltip />
      <el-table-column label="默认" width="80" align="center">
        <template slot-scope="scope">
          <el-tag v-if="isDefault(scope.row)" type="warning" size="mini">默认</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right" align="center">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="mini"
            :disabled="isDefault(scope.row)"
            @click="handleSetDefault(scope.row)"
            v-hasPermi="['master:subject:setDefaultBank']"
          >设为默认</el-button>
          <el-button
            type="text"
            size="mini"
            @click="handleEdit(scope.row)"
          >编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="formIsEdit ? '修改银行卡' : '新增银行卡'"
      :visible.sync="formVisible"
      width="560px"
      append-to-body
    >
      <el-form
        ref="bankForm"
        :model="bankForm"
        :rules="bankRules"
        label-width="110px"
        size="small"
      >
        <el-form-item label="收款户名" prop="payName">
          <el-input v-model.trim="bankForm.payName" placeholder="请输入收款户名" clearable />
        </el-form-item>
        <el-form-item label="简称" prop="nickName">
          <el-input v-model.trim="bankForm.nickName" placeholder="请输入简称" clearable />
        </el-form-item>
        <el-form-item label="吉客云编号" prop="outCode">
          <el-input v-model.trim="bankForm.outCode" disabled />
        </el-form-item>
        <el-form-item label="开户行" prop="bankName">
          <el-input v-model.trim="bankForm.bankName" placeholder="请输入开户行全称" clearable />
        </el-form-item>
        <el-form-item label="银行账号" prop="payNo">
          <el-input v-model.trim="bankForm.payNo" placeholder="请输入银行账号" clearable />
        </el-form-item>
        <el-form-item label="账户余额" prop="balance">
          <el-input-number
            v-model.number="bankForm.balance"
            :min="0"
            :step="1"
            :precision="2"
            :disabled="formIsEdit"
            placeholder="请输入账户余额"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="是否激活" prop="actived">
          <el-radio-group v-model="bankForm.actived">
            <el-radio :label="0">激活</el-radio>
            <el-radio :label="1">弃用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="formVisible = false">取消</el-button>
        <el-button type="primary" size="small" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { getSubjectBankList, setSubjectDefaultBank } from '@/api/master'
import { addPayerApi, updatePayerApi } from '@/api/monery'

export default {
  name: 'SubjectBankDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    subjectId: {
      type: [Number, String],
      default: null
    },
    subjectCode: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      bankList: [],
      defaultPayerId: null,
      formVisible: false,
      formIsEdit: false,
      bankForm: this.buildForm(),
      bankRules: {
        payName: [{ required: true, message: '请输入收款户名', trigger: 'blur' }],
        outCode: [{ required: true, message: '请输入吉客云编号', trigger: 'blur' }],
        bankName: [{ required: true, message: '请输入开户行全称', trigger: 'blur' }],
        payNo: [{ required: true, message: '请输入银行账号', trigger: 'blur' }],
        actived: [{ required: true, message: '请选择是否激活', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.subjectId) {
        this.loadList()
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    buildForm() {
      return {
        id: null,
        payName: '',
        nickName: '',
        outCode: '',
        bankName: '',
        payNo: '',
        balance: 0,
        actived: 0
      }
    },
    loadList() {
      this.loading = true
      getSubjectBankList(this.subjectId).then(response => {
        const data = response.data || {}
        this.bankList = data.list || []
        this.defaultPayerId = data.defaultPayerId != null ? data.defaultPayerId : null
      }).finally(() => {
        this.loading = false
      })
    },
    isDefault(row) {
      return row && this.defaultPayerId !== null && row.id === this.defaultPayerId
    },
    handleSetDefault(row) {
      if (!this.subjectId || !row || !row.id) {
        return
      }
      const subjectId = this.subjectId
      const payerId = row.id
      this.$confirm(`确认将该银行卡设为默认？`, '提示', {
        type: 'warning',
        lockScroll: false
      }).then(() => {
        setSubjectDefaultBank(subjectId, payerId).then(() => {
          this.$message.success('已设为默认银行卡')
          this.loadList()
          this.$emit('changed')
        })
      }).catch(() => {})
    },
    handleAdd() {
      this.formIsEdit = false
      this.bankForm = this.buildForm()
      this.bankForm.outCode = this.subjectCode
      this.formVisible = true
      this.$nextTick(() => {
        if (this.$refs.bankForm) {
          this.$refs.bankForm.clearValidate()
        }
      })
    },
    handleEdit(row) {
      this.formIsEdit = true
      this.bankForm = { ...this.buildForm(), ...row }
      this.formVisible = true
      this.$nextTick(() => {
        if (this.$refs.bankForm) {
          this.$refs.bankForm.clearValidate()
        }
      })
    },
    handleSubmit() {
      this.$refs.bankForm.validate(valid => {
        if (!valid) {
          this.$message.error('请检查表单信息')
          return
        }
        const payload = { ...this.bankForm }
        if (this.formIsEdit) {
          updatePayerApi(payload).then(() => {
            this.$message.success('修改成功')
            this.formVisible = false
            this.loadList()
            this.$emit('changed')
          })
        } else {
          addPayerApi(payload).then(() => {
            this.$message.success('新增成功')
            this.formVisible = false
            this.loadList()
            this.$emit('changed')
          })
        }
      })
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.bank-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.bank-subject-label {
  font-size: 13px;
  color: #606266;
}
</style>

<style lang="scss">
// 非 scoped 样式：el-dialog 使用 append-to-body 移出组件作用域，需全局样式生效
.subject-bank-dialog {
  .el-dialog__body {
    max-height: 68vh;
    overflow-y: auto;
  }
}
</style>
