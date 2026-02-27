// 服务商审核
const vendorAudit = {
  state: {
    vendorAuditNumber: 0 // 待审核数量
  },
  mutations: {
    SET_VENDOR_AUDIT_NUMBER: (state, data) => (state.vendorAuditNumber = data)
  }
}
export default vendorAudit
