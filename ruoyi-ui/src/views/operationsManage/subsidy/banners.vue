<template>
  <div class="app-container">
    <el-card shadow="never">
      <div slot="header" class="card-header"><span>轮播图管理</span><el-button type="primary" size="mini" @click="openDialog()">新增轮播图</el-button></div>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column prop="bannerName" label="名称" min-width="150" />
        <el-table-column label="图片" width="120"><template slot-scope="scope"><el-image :src="scope.row.imageUrl" fit="cover" class="banner-image" /></template></el-table-column>
        <el-table-column prop="targetType" label="跳转类型" width="110" /><el-table-column prop="targetValue" label="跳转目标" min-width="160" show-overflow-tooltip /><el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="90"><template slot-scope="scope"><el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '停用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="80"><template slot-scope="scope"><el-button type="text" @click="openDialog(scope.row)">编辑</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-dialog :title="form.id ? '编辑轮播图' : '新增轮播图'" :visible.sync="visible" width="520px"><el-form ref="form" :model="form" :rules="rules" label-width="90px"><el-form-item label="名称" prop="bannerName"><el-input v-model="form.bannerName" /></el-form-item><el-form-item label="图片地址" prop="imageUrl"><el-input v-model="form.imageUrl" /></el-form-item><el-form-item label="跳转类型"><el-select v-model="form.targetType"><el-option label="商品" value="PRODUCT" /><el-option label="页面" value="PAGE" /></el-select></el-form-item><el-form-item label="跳转目标"><el-input v-model="form.targetValue" /></el-form-item><el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :label="1">启用</el-radio><el-radio :label="0">停用</el-radio></el-radio-group></el-form-item></el-form><span slot="footer"><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span></el-dialog>
  </div>
</template>

<script>
import { getSubsidyBanners, saveSubsidyBanner, updateSubsidyBanner } from '@/api/subsidy'

export default {
  name: 'SubsidyBanners',
  data() { return { loading: false, visible: false, rows: [], form: {}, rules: { bannerName: [{ required: true, message: '请输入名称', trigger: 'blur' }], imageUrl: [{ required: true, message: '请输入图片地址', trigger: 'blur' }] } } },
  created() { this.load() },
  methods: {
    load() { this.loading = true; getSubsidyBanners().then(response => { this.rows = response.data || [] }).finally(() => { this.loading = false }) },
    openDialog(row) { this.form = Object.assign({ bannerName: '', imageUrl: '', targetType: 'PRODUCT', targetValue: '', sortOrder: 0, status: 1 }, row || {}); this.visible = true },
    save() { this.$refs.form.validate(valid => { if (!valid) return; const request = this.form.id ? updateSubsidyBanner(this.form.id, this.form) : saveSubsidyBanner(this.form); request.then(() => { this.$message.success('保存成功'); this.visible = false; this.load() }) }) }
  }
}
</script>

<style lang="scss" scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }
.banner-image { width: 80px; height: 48px; }
</style>
