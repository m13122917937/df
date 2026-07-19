<template>
  <div class="app-container rebate-page">
    <section class="rebate-card">
      <div class="header-row">
        <div><h3>{{ pageTitle }}</h3><p>按活动维护品牌、供应商、核算规则以及商品或串码明细。</p></div>
        <el-button type="primary" icon="el-icon-plus" @click="openDialog()">新增活动</el-button>
      </div>
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="activityCode" label="活动编码" min-width="130" />
        <el-table-column prop="activityName" label="活动名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" width="110" />
        <el-table-column prop="supplierName" label="供应商" min-width="140" />
        <el-table-column prop="scene" label="场景" width="100" />
        <el-table-column prop="granularity" label="颗粒度" width="100" />
        <el-table-column prop="calculationMethod" label="计算方式" width="110" />
        <el-table-column prop="startTime" label="开始时间" min-width="165" />
        <el-table-column prop="endTime" label="结束时间" min-width="165" />
        <el-table-column prop="totalAmount" label="总金额" width="110" align="right" />
        <el-table-column label="明细" width="80" align="center"><template slot-scope="scope">{{ (scope.row.details || []).length }}</template></el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
            <el-button type="text" class="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog :title="`${form.id ? '编辑' : '新增'}返利价保活动`" :visible.sync="visible" width="920px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="活动编码" prop="activityCode"><el-input v-model="form.activityCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="活动名称" prop="activityName"><el-input v-model="form.activityName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="供应商"><el-input v-model="form.supplierName" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="业务场景" prop="scene"><el-select v-model="form.scene"><el-option label="返利" value="REBATE" /><el-option label="价保" value="PRICE_PROTECTION" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="颗粒度" prop="granularity"><el-select v-model="form.granularity"><el-option label="商品" value="GOODS" /><el-option label="串码" value="SN" /><el-option label="活动" value="ACTIVITY" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="计算方式" prop="calculationMethod"><el-select v-model="form.calculationMethod"><el-option label="固定金额" value="AMOUNT" /><el-option label="点位" value="POINT" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="开始时间" prop="startTime"><el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束时间" prop="endTime"><el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="核算时点"><el-input v-model="form.calculationTime" placeholder="如：发货日/完成日" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="活动总金额"><el-input-number v-model="form.totalAmount" :precision="4" :controls="false" /></el-form-item></el-col>
        </el-row>
        <div class="detail-title"><strong>商品/串码明细</strong><el-button type="text" icon="el-icon-plus" @click="addDetail">添加明细</el-button></div>
        <el-table :data="form.details" border size="mini">
          <el-table-column label="货品编码"><template slot-scope="scope"><el-input v-model="scope.row.goodsNo" /></template></el-table-column>
          <el-table-column label="商品名称"><template slot-scope="scope"><el-input v-model="scope.row.goodsName" /></template></el-table-column>
          <el-table-column label="串码"><template slot-scope="scope"><el-input v-model="scope.row.snCode" /></template></el-table-column>
          <el-table-column label="金额"><template slot-scope="scope"><el-input-number v-model="scope.row.amount" :precision="4" :controls="false" /></template></el-table-column>
          <el-table-column label="点位"><template slot-scope="scope"><el-input-number v-model="scope.row.pointRate" :precision="8" :controls="false" /></template></el-table-column>
          <el-table-column width="60"><template slot-scope="scope"><el-button type="text" class="danger" @click="form.details.splice(scope.$index, 1)">删除</el-button></template></el-table-column>
        </el-table>
      </el-form>
      <span slot="footer"><el-button @click="visible = false">取消</el-button><el-button type="primary" :loading="saving" @click="submit">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { deleteAnalysisRebate, getAnalysisRebateList, saveAnalysisRebate } from '@/api/analysis'

export default {
  name: 'AnalysisRebate',
  data() {
    return {
      loading: false, saving: false, visible: false, rows: [], form: {},
      rules: {
        activityCode: [{ required: true, message: '请输入活动编码', trigger: 'blur' }],
        activityName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
        scene: [{ required: true, message: '请选择业务场景', trigger: 'change' }],
        granularity: [{ required: true, message: '请选择颗粒度', trigger: 'change' }],
        calculationMethod: [{ required: true, message: '请选择计算方式', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      }
    }
  },
  computed: { pageTitle() { return this.$route.meta.title || '返利价保' } },
  created() { this.loadData() },
  methods: {
    async loadData() { this.loading = true; try { const res = await getAnalysisRebateList(); this.rows = res.data || [] } finally { this.loading = false } },
    openDialog(row) {
      this.form = Object.assign({ activityCode: '', activityName: '', brand: '', supplierName: '', scene: '', granularity: '', calculationMethod: '', calculationTime: '', totalAmount: null, startTime: '', endTime: '', status: 'PENDING', details: [] }, JSON.parse(JSON.stringify(row || {})))
      if (!this.form.details) this.form.details = []
      this.visible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    addDetail() { this.form.details.push({ goodsNo: '', goodsName: '', snCode: '', amount: null, pointRate: null }) },
    submit() { this.$refs.form.validate(async valid => { if (!valid) return; this.saving = true; try { await saveAnalysisRebate(this.form); this.$message.success('保存成功'); this.visible = false; this.loadData() } finally { this.saving = false } }) },
    async remove(row) { await this.$confirm('确认删除该返利价保活动吗？', '删除确认', { type: 'warning' }); await deleteAnalysisRebate(row.id); this.$message.success('删除成功'); this.loadData() }
  }
}
</script>

<style lang="scss" scoped>
.rebate-page { min-height: 100%; background: var(--bg-page); color: var(--nl-color); }
.rebate-card { background: var(--bg-card); border: 1px solid var(--border-tags); border-radius: var(--radius); padding: var(--page-card-padding); box-shadow: var(--shadow-card); }
.header-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.header-row h3 { margin: 0 0 7px; color: var(--nl-color-title); }.header-row p { margin: 0; color: var(--nl-color-tip); }
.detail-title { display: flex; justify-content: space-between; align-items: center; margin: 10px 0; color: var(--nl-color-title); }.danger { color: #f56c6c; }
</style>
