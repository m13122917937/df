<template>
  <el-dialog
    :visible.sync="dialogVisible"
    width="1200px"
    title="收付款账户确认"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="account-confirm-content">
      <!-- 供应商信息 -->
      <div class="supplier-info">
        <span class="supplier-label">供应商：</span>
        <span class="supplier-name">{{ supplierName }}</span>
      </div>

      <!-- 表格 -->
      <el-table
        :data="payCompanyList"
        border
        style="margin-top: 16px"
        :show-header="true"
      >
        <!-- <el-table-column label="付款计划" min-width="120" align="right">
          <template slot-scope="scope">
            {{ formatNumber(totalPayPlanAmount) }}
          </template>
        </el-table-column> -->

        <el-table-column label="收款方要求" min-width="500">
          <el-table-column label="收款主体" min-width="230">
            <template slot-scope="scope">
              <!-- <div class="receive-select-row">
                <el-select
                  v-model="scope.row.selectedSupplierId"
                  placeholder="请选择收款主体"
                  filterable
                  :loading="supplierLoading"
                  @change="(val) => handleRowSupplierChange(scope.$index, val)"
                  size="small"
                 style="width: 100%"
                >
                  <el-option
                    v-for="option in supplierOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
              </div> -->
              {{ supplierName }}
            </template>
          </el-table-column>
          <el-table-column label="收款银行账户" min-width="280">
             <template slot-scope="scope">
              <div class="receive-select-row">
                <div class="div-top" style="height: 20px;"></div>
                <el-select
                  v-model="scope.row.selectedSupplierBankId"
                  placeholder="请选择收款银行账户"
                  filterable
                  :loading="scope.row.supplierBankLoading"
                  :disabled="!scope.row.selectedSupplierId || !scope.row.supplierBankList || scope.row.supplierBankList.length === 0"
                  @change="(val) => handleRowSupplierBankChange(scope.$index, val)"
                  size="small"
                  style="width: 100%"
                >
                  <el-option
                    v-for="bank in scope.row.supplierBankList"
                    :key="bank.id"
                    :label="bank.bankLabel"
                    :value="bank.id"
                  />
                </el-select>
              </div>
              <div class="bank-label-tip">银行卡号：{{ getRowSupplierBankAccount(scope.row) }}</div>
            </template>
          </el-table-column>
        </el-table-column>

        <el-table-column label="付款主体明细" min-width="850">
          <el-table-column label="付款金额" min-width="100" align="right">
            <template slot-scope="scope">
              <div style="font-size: 16px; color: #f56c6c; font-weight: bold;">{{ formatNumber(scope.row.totalBillingAmount) }}</div>
            </template>
          </el-table-column>
          <el-table-column label="付款主体" min-width="230">
            <template slot-scope="scope">
              <!-- <el-select
                v-model="scope.row.selectedPayCompanyId"
                placeholder="请选择付款主体"
                filterable
                :loading="payCompanyLoading"
                @visible-change="handlePayCompanyVisible"
                @change="(val) => handlePayCompanyChange(scope.$index, val)"
                size="small"
                style="width: 100%"
                disabled
              >
                <el-option
                  v-for="option in payCompanyOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select> -->
              {{ scope.row.payCompanyName }}
            </template>
          </el-table-column>
          <el-table-column label="付款银行账户" min-width="280">
            <template slot-scope="scope">
              <div class="div-top" style="height: 20px;"></div>
              <el-select
                v-model="scope.row.selectedBankAccountId"
                placeholder="请选择银行账户"
                filterable
                :loading="scope.row.bankAccountLoading"
                :disabled="!scope.row.selectedPayCompanyId || !scope.row.bankAccountList || scope.row.bankAccountList.length === 0"
                @change="(val) => handleBankAccountChange(scope.$index, val)"
                size="small"
                style="width: 100%"
              >
                <el-option
                  v-for="account in scope.row.bankAccountList"
                  :key="account.id"
                  :label="account.bankLabel"
                  :value="account.id"
                >
                  <span>{{ account.bankLabel || '-' }}</span>
                </el-option>
                </el-select>
              <span class="bank-label-tip">银行卡号：{{ getSelectedBankPayNo(scope.row) }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column label="付款明细" min-width="100" align="right">
            <template slot-scope="scope">
              {{ formatNumber(scope.row.totalBillingAmount) }}
            </template>
          </el-table-column> -->
        </el-table-column>
      </el-table>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="handleCancel">取 消</el-button>
      <el-button size="small" type="danger" @click="handleConfirm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getPayCompanyBankListApi, getCompanyBankListApi } from '@/api/monery';
export default {
  name: 'AccountConfirmDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    supplierName: {
      type: String,
      default: ''
    },
    supplierId: {
      type: [Number, String],
      default: null
    },
    bankName: {
      type: String,
      default: ''
    },
    bankAccount: {
      type: String,
      default: ''
    },
    payCompanyList: {
      type: Array,
      default: () => []
    },
    totalPayPlanAmount: {
      type: [Number, String],
      default: 0
    },
    billIds: {
      type: Array,
      default: () => []
    },
    // 付款主体选项列表
    payCompanyOptions: {
      type: Array,
      default: () => []
    },
    // 付款主体加载状态
    payCompanyLoading: {
      type: Boolean,
      default: false
    },
    // 收款方选项（来自 supplierOptions）
    supplierOptions: {
      type: Array,
      default: () => []
    },
    supplierLoading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 收款方选择
      selectedSupplierId: null,
      selectedSupplierBankId: null,
      supplierBankList: [],
      supplierBankLoading: false
    };
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        // 弹窗打开时，初始化每个付款主体行的选中状态
        this.initPayCompanySelection();
        this.initSupplierSelection();
      }
    },
    payCompanyList: {
      immediate: true,
      handler() {
        this.initPayCompanySelection();
      }
    }
  },
  methods: {
    // 初始化收款方（供应商）银行账户选择
    initSupplierSelection() {
      // 初始化选中的供应商和银行账号
      if (this.supplierId) {
        this.selectedSupplierId = this.supplierId;
        this.fetchSupplierBankList(this.supplierId);
      } else {
        this.selectedSupplierId = null;
        this.supplierBankList = [];
        this.selectedSupplierBankId = null;
      }
    },
    formatNumber(val) {
      if (val === null || val === undefined || val === '') return '-';
      return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
    },
    // 初始化付款主体选中状态
    initPayCompanySelection() {
      if (!Array.isArray(this.payCompanyList)) return;

      this.payCompanyList.forEach((item, index) => {
        // 如果还没有 selectedPayCompanyId，使用默认的 payCompanyId
        if (!item.hasOwnProperty('selectedPayCompanyId')) {
          this.$set(item, 'selectedPayCompanyId', item.payCompanyId || null);
        }

        // 初始化银行账户相关字段
        if (!item.hasOwnProperty('bankAccountList')) {
          this.$set(item, 'bankAccountList', []);
        }
        if (!item.hasOwnProperty('bankAccountLoading')) {
          this.$set(item, 'bankAccountLoading', false);
        }
        if (!item.hasOwnProperty('selectedBankAccountId')) {
          this.$set(item, 'selectedBankAccountId', null);
        }

        // 初始化收款方及其银行账户（按行存储）
        if (!item.hasOwnProperty('selectedSupplierId')) {
          this.$set(item, 'selectedSupplierId', this.supplierId || null);
        }
        if (!item.hasOwnProperty('supplierBankList')) {
          this.$set(item, 'supplierBankList', []);
        }
        if (!item.hasOwnProperty('supplierBankLoading')) {
          this.$set(item, 'supplierBankLoading', false);
        }
        if (!item.hasOwnProperty('selectedSupplierBankId')) {
          this.$set(item, 'selectedSupplierBankId', null);
        }

        // 如有默认收款方，加载其银行账户
        if (item.selectedSupplierId) {
          this.fetchRowSupplierBankList(index, item.selectedSupplierId);
        }

        // 如果有默认的付款主体，加载对应的银行账户列表
        if (item.payCompanyName) {
          this.fetchBankAccountList(index, item.payCompanyName);
        }
      });
    },
    // 获取付款主体对应的银行账户列表
    async fetchBankAccountList(index, payName) {
      if (!payName || index < 0 || index >= this.payCompanyList.length) {
        return;
      }

      const item = this.payCompanyList[index];
      if (!item) return;

      item.bankAccountLoading = true;

      try {
        const res = await getPayCompanyBankListApi({
          payName: payName,
          actived: 1
        });

        if (res && res.code === 200) {
          const data = Array.isArray(res.data) ? res.data : [];

          // 格式化银行账户列表
          item.bankAccountList = data.map(account => ({
            id: account.id,
            bankName: account.bankName || '',
            payNo: account.payNo || '',
            bankLabel: `${account.bankName || ''} / ${account.payNo || ''}`
          }));

          // 如果有账户，自动选中第一个
          if (item.bankAccountList.length > 0) {
            item.selectedBankAccountId = item.bankAccountList[0].id;
          } else {
            item.selectedBankAccountId = null;
          }
        } else {
          item.bankAccountList = [];
          item.selectedBankAccountId = null;
          this.$message.error(res?.msg || '获取银行账户列表失败');
        }
      } catch (error) {
        console.error('获取银行账户列表失败:', error);
        this.$message.error('获取银行账户列表失败');
        item.bankAccountList = [];
        item.selectedBankAccountId = null;
      } finally {
        item.bankAccountLoading = false;
      }
    },
    // 获取收款方银行账户（按行）
    async fetchRowSupplierBankList(index, companyId) {
      if (!companyId || index < 0 || index >= this.payCompanyList.length) return;
      const item = this.payCompanyList[index];
      if (!item) return;

      item.supplierBankLoading = true;
      try {
        const res = await getCompanyBankListApi(companyId);
        if (res && res.code === 200) {
          const data = Array.isArray(res.data) ? res.data : [];
          item.supplierBankList = data.map(acc => ({
            id: acc.id,
            bankLabel: `${acc.accountBank || ''} / ${acc.bankAccount || ''}`,
            bankName: acc.accountBank || '',
            bankAccount: acc.bankAccount || ''
          }));
          item.selectedSupplierBankId = item.supplierBankList.length ? item.supplierBankList[0].id : null;
        } else {
          item.supplierBankList = [];
          item.selectedSupplierBankId = null;
          this.$message.error(res?.msg || '获取收款账户失败');
        }
      } catch (error) {
        console.error('获取收款账户失败:', error);
        this.$message.error('获取收款账户失败');
        item.supplierBankList = [];
        item.selectedSupplierBankId = null;
      } finally {
        item.supplierBankLoading = false;
      }
    },
    // 获取收款方（供应商）银行账户列表
    async fetchSupplierBankList(companyId) {
      if (!companyId) {
        this.supplierBankList = [];
        this.selectedSupplierBankId = null;
        return;
      }
      this.supplierBankLoading = true;
      try {
        const res = await getCompanyBankListApi(companyId);
        if (res && res.code === 200) {
          const data = Array.isArray(res.data) ? res.data : [];
          this.supplierBankList = data.map(acc => ({
            id: acc.id,
            bankLabel: `${acc.accountBank || ''} / ${acc.bankAccount || ''}`,
            accountBank: acc.accountBank || '',
            bankAccount: acc.bankAccount || ''
          }));
          if (this.supplierBankList.length > 0) {
            this.selectedSupplierBankId = this.supplierBankList[0].id;
          } else {
            this.selectedSupplierBankId = null;
          }
        } else {
          this.supplierBankList = [];
          this.selectedSupplierBankId = null;
          this.$message.error(res?.msg || '获取收款账户失败');
        }
      } catch (error) {
        console.error('获取收款账户失败:', error);
        this.$message.error('获取收款账户失败');
        this.supplierBankList = [];
        this.selectedSupplierBankId = null;
      } finally {
        this.supplierBankLoading = false;
      }
    },
    // 付款主体切换处理
    handlePayCompanyChange(index, payCompanyId) {
      const item = this.payCompanyList[index];
      if (!item) return;

      // 根据选项获取付款主体名称（payName）
      const payOption = this.payCompanyOptions.find(opt => opt.value === payCompanyId);
      const payName = payOption ? (payOption.label || payOption.payName) : '';
      // 同步名称，便于显示与后续请求
      this.$set(item, 'payCompanyName', payName || '');

      if (payCompanyId) {
        // 加载对应的银行账户列表
        this.fetchBankAccountList(index, payName);
      } else {
        // 清空银行账户列表
        item.bankAccountList = [];
        item.selectedBankAccountId = null;
        item.bankAccountLoading = false;
      }

      // 触发切换事件，让父组件处理
      this.$emit('pay-company-change', {
        index,
        payCompanyId,
        payName,
        item
      });
    },
    // 收款方按行切换
    handleRowSupplierChange(index, companyId) {
      const item = this.payCompanyList[index];
      if (!item) return;
      item.selectedSupplierId = companyId || null;
      if (companyId) {
        this.fetchRowSupplierBankList(index, companyId);
      } else {
        item.supplierBankList = [];
        item.selectedSupplierBankId = null;
        item.supplierBankLoading = false;
      }
    },
    handleRowSupplierBankChange(index, bankId) {
      const item = this.payCompanyList[index];
      if (!item) return;
      item.selectedSupplierBankId = bankId || null;
    },
    getRowSupplierBankLabel(row) {
      if (!row || !Array.isArray(row.supplierBankList)) return '-';
      const hit = row.supplierBankList.find(b => b.id === row.selectedSupplierBankId);
      return hit ? hit.bankLabel : '-';
    },
    getRowSupplierBankAccount(row) {
      if (!row || !Array.isArray(row.supplierBankList)) return '-';
      const hit = row.supplierBankList.find(b => b.id === row.selectedSupplierBankId);
      return hit ? hit.bankAccount : '-';
    },
    // 收款方切换处理
    handleSupplierChange(companyId) {
      this.selectedSupplierId = companyId || null;
      if (companyId) {
        this.fetchSupplierBankList(companyId);
      } else {
        this.supplierBankList = [];
        this.selectedSupplierBankId = null;
      }
    },
    handleSupplierBankChange(bankId) {
      this.selectedSupplierBankId = bankId || null;
    },
    getSelectedSupplierName() {
      const hit = this.supplierOptions.find(opt => opt.value === this.selectedSupplierId);
      return hit ? (hit.label || hit.companyName) : this.supplierName;
    },
    getSelectedSupplierBankLabel() {
      const hit = this.supplierBankList.find(b => b.id === this.selectedSupplierBankId);
      return hit ? hit.bankLabel : '-';
    },
    // 获取选中银行账户的展示文案
    getSelectedBankLabel(row) {
      if (!row || !Array.isArray(row.bankAccountList)) return '-';
      const hit = row.bankAccountList.find(acc => acc.id === row.selectedBankAccountId);
      return hit?.bankLabel || '-';
    },
    getSelectedBankPayNo(row) {
      if (!row || !Array.isArray(row.bankAccountList)) return '-';
      const hit = row.bankAccountList.find(acc => acc.id === row.selectedBankAccountId);
      return hit?.payNo || '-';
    },
    // 银行账户切换处理
    handleBankAccountChange(index, accountId) {
      const item = this.payCompanyList[index];
      if (!item) return;

      const selectedAccount = item.bankAccountList.find(
        account => account.id === accountId
      );

      if (selectedAccount) {
        item.selectedBankAccountId = accountId;
      } else {
        item.selectedBankAccountId = null;
      }
    },
    // 付款主体远程搜索
    handlePayCompanyRemote(keyword) {
      this.$emit('pay-company-remote', keyword);
    },
    // 付款主体下拉框显示/隐藏
    handlePayCompanyVisible(visible) {
      if (visible) {
        this.$emit('pay-company-visible', true);
      }
    },
    handleCancel() {
      this.$emit('update:visible', false);
      this.$emit('cancel');
    },
    handleClose() {
      this.$emit('close');
    },
    handleConfirm() {
      // 验证数据
      if (this.payCompanyList.length === 0) {
        this.$message.warning('没有可付款的数据');
        return;
      }

      if (!this.billIds || this.billIds.length === 0) {
        this.$message.warning('账单ID不能为空');
        return;
      }

      // 验证每个付款主体是否都选择了付款主体
      const invalidPayCompanyItems = this.payCompanyList.filter(
        item => !item.selectedPayCompanyId
      );

      if (invalidPayCompanyItems.length > 0) {
        this.$message.warning('请为每个付款主体选择付款主体');
        return;
      }

      // 验证每个付款主体是否都选择了银行账户
      const invalidBankAccountItems = this.payCompanyList.filter(
        item => !item.selectedBankAccountId
      );

      if (invalidBankAccountItems.length > 0) {
        this.$message.warning('请为每个付款主体选择银行账户');
        return;
      }
      // 验证每行收款方及银行账户
      const invalidReceive = this.payCompanyList.filter(
        item => !item.selectedSupplierId || !item.selectedSupplierBankId
      );
      if (invalidReceive.length > 0) {
        this.$message.warning('请为每条记录选择收款方及收款账户');
        return;
      }
      console.log("this.payCompanyList",this.payCompanyList);

      // 触发确认事件，传递必要的数据（包含选中的付款主体ID和银行账户ID）
      this.$emit('confirm', {
        billIds: this.billIds,
        splitForms: this.payCompanyList.map(item => ({
          billIds: item.billIds,
          // payerId 对应付款主体 id（selectedPayCompanyId）
          payerId: item.selectedBankAccountId,
          // supplierBankId 对应收款方的银行账户 id（selectedSupplierBankId）
          supplierBankId: item.selectedSupplierBankId,
        })),
        totalPayPlanAmount: this.totalPayPlanAmount
      });
    }
  }
};
</script>

<style scoped lang="scss">
.account-confirm-content {
  padding: 0;
}

.supplier-info {
  margin-bottom: 16px;
  font-size: 14px;
}

.supplier-label {
  color: #606266;
  margin-right: 8px;
}

.supplier-name {
  color: #303133;
  font-weight: 500;
}

.dialog-footer {
  text-align: right;
}

.bank-label-tip {
  color: #909399;
  font-size: 14px;
  margin-left: 4px;
}

.supplier-bank-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.supplier-bank-row .label {
  min-width: 64px;
  color: #606266;
}
</style>

