<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never">
          <div slot="header" class="card-header"><span>分类管理</span><el-button type="primary" size="mini" @click="openDialog()">新增分类</el-button></div>
          <el-table v-loading="loading" :data="rows" row-key="id" border>
            <el-table-column prop="categoryName" label="分类名称" min-width="150" />
            <el-table-column prop="discountRate" label="优惠比例" width="110" />
            <el-table-column prop="discountCapAmount" label="优惠封顶" width="120" />
            <el-table-column prop="saleProvinces" label="可售省份" min-width="180" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="90"><template slot-scope="scope"><el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '停用' }}</el-tag></template></el-table-column>
            <el-table-column label="操作" width="80"><template slot-scope="scope"><el-button type="text" @click="openDialog(scope.row)">编辑</el-button></template></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog :title="form.id ? '编辑分类' : '新增分类'" :visible.sync="visible" width="560px">
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="分类名称" prop="categoryName"><el-input v-model="form.categoryName" /></el-form-item>
        <el-form-item label="优惠比例" prop="discountRate"><el-input-number v-model="form.discountRate" :min="0" :max="1" :step="0.01" :precision="2" /></el-form-item>
        <el-form-item label="优惠封顶" prop="discountCapAmount"><el-input-number v-model="form.discountCapAmount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="可售省份" prop="saleProvinces"><el-input v-model="form.saleProvinces" placeholder='例如：["广东省","湖南省"]' /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :label="1">启用</el-radio><el-radio :label="0">停用</el-radio></el-radio-group></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="visible = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { getSubsidyCategories, saveSubsidyCategory, updateSubsidyCategory } from '@/api/subsidy'

export default {
  name: 'SubsidyCategories',
  data() {
    return { loading: false, visible: false, rows: [], form: {}, rules: { categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }], saleProvinces: [{ required: true, message: '请输入可售省份 JSON 数组', trigger: 'blur' }] } }
  },
  created() { this.load() },
  methods: {
    load() { this.loading = true; getSubsidyCategories().then(response => { this.rows = response.data || [] }).finally(() => { this.loading = false }) },
    openDialog(row) { this.form = Object.assign({ parentId: 0, categoryName: '', discountRate: 0, discountCapAmount: 0, saleProvinces: '[]', sortOrder: 0, status: 1 }, row || {}); this.visible = true },
    save() { this.$refs.form.validate(valid => { if (!valid) return; const request = this.form.id ? updateSubsidyCategory(this.form.id, this.form) : saveSubsidyCategory(this.form); request.then(() => { this.$message.success('保存成功'); this.visible = false; this.load() }) }) }
  }
}
</script>

<style lang="scss" scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }
</style>
