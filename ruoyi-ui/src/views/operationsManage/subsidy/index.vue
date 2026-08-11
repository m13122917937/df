<template>
  <div class="app-container subsidy-overview">
    <section class="hero-card">
      <div><h2>国补商城</h2><p>管理独立商品目录、库存、订单履约、退款和微信身份冲突。</p></div>
      <el-tag type="warning">平台活动优惠，不代表政府资格核销</el-tag>
    </section>
    <el-row :gutter="16">
      <el-col v-for="item in cards" :key="item.title" :xs="24" :sm="12" :lg="8">
        <section class="summary-card"><p>{{ item.title }}</p><strong>{{ item.value }}</strong><span>{{ item.note }}</span></section>
      </el-col>
    </el-row>
    <section class="guide-card"><h3>运营入口</h3><el-alert :closable="false" type="info" show-icon title="商品、库存、订单、发货、退款与身份冲突页面将通过“国补”菜单逐项配置权限后开放。" /></section>
  </div>
</template>

<script>
import { getSubsidyOverview } from '@/api/subsidy'

export default {
  name: 'SubsidyOverview',
  data() { return { overview: {} } },
  computed: {
    cards() {
      return [
        { title: '上架商品', value: this.overview.productCount == null ? '-' : this.overview.productCount, note: '独立国补商品目录' },
        { title: '待发货订单', value: this.overview.pendingShipmentCount == null ? '-' : this.overview.pendingShipmentCount, note: '需人工录入物流信息' },
        { title: '待审核退款', value: this.overview.pendingRefundCount == null ? '-' : this.overview.pendingRefundCount, note: '仅未发货订单可整单退款' }
      ]
    }
  },
  created() { this.loadOverview() },
  methods: {
    async loadOverview() {
      try { const response = await getSubsidyOverview(); this.overview = response.data || {} } catch (error) { this.overview = {} }
    }
  }
}
</script>

<style lang="scss" scoped>
.subsidy-overview { min-height: 100%; background: var(--bg-page); }
.hero-card,.summary-card,.guide-card { background: var(--bg-card); border: 1px solid var(--border-tags); border-radius: var(--radius); box-shadow: var(--shadow-card); }
.hero-card { display:flex; justify-content:space-between; align-items:center; padding:24px; margin-bottom:16px; }.hero-card h2,.guide-card h3 { margin:0 0 8px; color:var(--nl-color-title); }.hero-card p,.summary-card p,.summary-card span { color:var(--nl-color-tip); }.summary-card { padding:20px; margin-bottom:16px; }.summary-card p { margin:0 0 10px; }.summary-card strong { display:block; font-size:28px; color:var(--nl-color-title); margin-bottom:8px; }.summary-card span { font-size:13px; }.guide-card { padding:20px; }
</style>
