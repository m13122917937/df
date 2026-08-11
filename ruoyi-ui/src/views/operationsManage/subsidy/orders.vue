<template>
  <div class="app-container">
    <el-card shadow="never">
      <el-form :inline="true"><el-form-item label="订单状态"><el-select v-model="query.orderStatus" clearable placeholder="全部"><el-option label="待发货" value="PAID" /><el-option label="已发货" value="SHIPPED" /><el-option label="已完成" value="COMPLETED" /></el-select></el-form-item><el-button type="primary" @click="load">查询</el-button></el-form>
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column prop="orderNo" label="订单号" min-width="210" />
        <el-table-column prop="memberId" label="会员" width="90" />
        <el-table-column prop="payAmount" label="实付金额" width="110" />
        <el-table-column prop="orderStatus" label="状态" width="130" />
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="100"><template slot-scope="scope"><el-button v-if="scope.row.orderStatus === 'PAID'" type="text" @click="openShipment(scope.row)">发货</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="录入发货信息" :visible.sync="visible" width="460px"><el-form ref="shipment" :model="shipment" :rules="rules" label-width="90px"><el-form-item label="物流公司" prop="logisticsCompany"><el-input v-model="shipment.logisticsCompany" /></el-form-item><el-form-item label="运单号" prop="trackingNo"><el-input v-model="shipment.trackingNo" /></el-form-item></el-form><span slot="footer"><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="ship">确认发货</el-button></span></el-dialog>
  </div>
</template>

<script>
import { getSubsidyOrders, shipSubsidyOrder } from '@/api/subsidy'

export default {
  name: 'SubsidyOrders',
  data() { return { loading: false, visible: false, rows: [], query: { orderStatus: '' }, currentOrderNo: '', shipment: {}, rules: { logisticsCompany: [{ required: true, message: '请输入物流公司', trigger: 'blur' }], trackingNo: [{ required: true, message: '请输入运单号', trigger: 'blur' }] } } },
  created() { this.load() },
  methods: {
    load() { this.loading = true; getSubsidyOrders(this.query).then(response => { this.rows = response.data || [] }).finally(() => { this.loading = false }) },
    openShipment(row) { this.currentOrderNo = row.orderNo; this.shipment = { logisticsCompany: '', trackingNo: '' }; this.visible = true },
    ship() { this.$refs.shipment.validate(valid => { if (!valid) return; shipSubsidyOrder(this.currentOrderNo, this.shipment).then(() => { this.$message.success('发货成功'); this.visible = false; this.load() }) }) }
  }
}
</script>
