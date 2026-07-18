<template>
  <el-dialog
    title="新增销售退货"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    width="600px"
    :append-to-body="true"
    :z-index="99999"
    @close="handleClose"
  >
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="right"
      size="small"
    >
      <!-- 订单查询区块 -->
      <div class="section-title">订单查询（可选）</div>
      <div class="order-query-row">
        <el-input
          v-model="queryOrderCode"
          placeholder="输入内部单号"
          clearable
          class="query-input"
          size="small"
          @keyup.enter.native="handleQueryOrder"
        />
        <el-button type="primary" size="small" @click="handleQueryOrder" :loading="queryLoading">查询</el-button>
      </div>
      <div class="order-query-row">
        <el-input
          v-model="queryOriginalOrderId"
          placeholder="输入商家单号"
          clearable
          class="query-input"
          size="small"
          @keyup.enter.native="handleQueryOrder"
        />
        <el-button type="primary" size="small" @click="handleQueryOrder" :loading="queryLoading">查询</el-button>
      </div>

      <!-- 退货信息区块 -->
      <div class="section-title" style="margin-top: 16px;">退货信息</div>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="form.brand" placeholder="请输入品牌" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="品类" prop="category">
            <el-input v-model="form.category" placeholder="请输入品类" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="商品名/型号" prop="productName">
            <el-input v-model="form.productName" placeholder="请输入商品名/型号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="规格名" prop="skuName">
            <el-input v-model="form.skuName" placeholder="请输入规格名" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="供应商" prop="companyId">
        <el-select
          v-model="form.companyId"
          placeholder="请选择供应商"
          clearable
          filterable
          remote
          reserve-keyword
          :remote-method="onCompanyRemote"
          :loading="companyLoading"
          style="width: 100%"
          @change="onCompanyChange"
        >
          <el-option v-for="s in companyOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="原订单数量">
            <el-input-number v-model="form.originalQuantity" :min="0" :max="99999" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="退货数量" prop="quantity">
            <el-input-number v-model="form.quantity" :min="1" :max="99999" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="退货单价" prop="returnPrice">
            <el-input-number v-model="form.returnPrice" :min="0.01" :precision="2" :step="1" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="总金额">
        <span class="total-amount">￥{{ computedTotalAmount.toFixed(2) }}</span>
      </el-form-item>
      <el-form-item label="退货日期" prop="returnDate">
        <el-date-picker
          v-model="form.returnDate"
          type="datetime"
          placeholder="选择退货日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选" />
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel" size="small">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm" size="small">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { saveSalesReturnApi, getOrderDetailApi } from "@/api/salesReturn";
import { getBusinessCompanyListApi } from "@/api/business";

export default {
  name: 'SalesReturnDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    const validateQuantity = (rule, value, callback) => {
      if (value === null || value === undefined || value < 1) {
        callback(new Error('退货数量必须大于0'));
      } else {
        callback();
      }
    };
    const validatePrice = (rule, value, callback) => {
      if (value === null || value === undefined || value <= 0) {
        callback(new Error('退货单价必须大于0'));
      } else {
        callback();
      }
    };
    return {
      loading: false,
      queryLoading: false,
      companyLoading: false,
      companyOptions: [],
      queryOrderCode: '',
      queryOriginalOrderId: '',
      form: {
        orderCode: '',
        originalOrderId: '',
        companyId: null,
        companyName: '',
        brand: '',
        category: '',
        productName: '',
        skuName: '',
        skuCode: '',
        quantity: 1,
        originalQuantity: 0,
        returnPrice: null,
        returnDate: null,
        remark: '',
      },
      rules: {
        brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
        category: [{ required: true, message: '请输入品类', trigger: 'blur' }],
        productName: [{ required: true, message: '请输入商品名/型号', trigger: 'blur' }],
        skuName: [{ required: true, message: '请输入规格名', trigger: 'blur' }],
        companyId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
        quantity: [{ required: true, validator: validateQuantity, trigger: 'blur' }],
        returnPrice: [{ required: true, validator: validatePrice, trigger: 'blur' }],
        returnDate: [{ required: true, message: '请选择退货日期', trigger: 'change' }],
      }
    };
  },
  computed: {
    dialogVisible: {
      get() { return this.visible; },
      set(val) { this.$emit('update:visible', val); }
    },
    computedTotalAmount() {
      const qty = this.form.quantity || 0;
      const price = this.form.returnPrice || 0;
      return Math.round(qty * price * 100) / 100;
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm();
      }
    }
  },
  methods: {
    async handleQueryOrder() {
      if (!this.queryOrderCode && !this.queryOriginalOrderId) {
        this.$message.warning('请输入内部单号或商家单号');
        return;
      }
      this.queryLoading = true;
      try {
        const params = {};
        if (this.queryOrderCode) params.orderCode = this.queryOrderCode;
        if (this.queryOriginalOrderId) params.originalOrderId = this.queryOriginalOrderId;
        const res = await getOrderDetailApi(params);
        if (res && res.code === 200 && res.data) {
          const data = res.data;
          this.form.orderCode = data.orderCode || this.queryOrderCode;
          this.form.originalOrderId = data.originalOrderId || this.queryOriginalOrderId;
          this.form.brand = data.brand || '';
          this.form.category = data.category || '';
          this.form.productName = data.productName || '';
          this.form.skuName = data.skuName || '';
          this.form.skuCode = data.skuCode || '';
          this.form.originalQuantity = data.quantity || 0;
          if (data.companyId) {
            this.form.companyId = data.companyId;
            this.form.companyName = data.companyName || '';
            if (data.companyName) {
              this.companyOptions = [{ label: data.companyName, value: data.companyId }];
            }
          }
          if (data.tradePrice) {
            this.form.returnPrice = data.tradePrice;
          }
          this.$message.success('已自动填充订单信息');
        } else {
          this.$message.warning('未找到订单信息，请手动填写');
        }
      } catch (error) {
        console.error('查询订单失败:', error);
        this.$message.warning('查询订单失败，请手动填写');
      } finally {
        this.queryLoading = false;
      }
    },

    async onCompanyRemote(keyword) {
      const trimmed = (keyword || "").trim();
      this.companyLoading = true;
      try {
        const pageData = { pageNum: 1, pageSize: 30 };
        const params = {};
        if (trimmed) params.companyNameLike = trimmed;
        const res = await getBusinessCompanyListApi(params, pageData);
        const list = res?.rows || res?.data || [];
        this.companyOptions = Array.isArray(list) ? list.map(item => ({
          label: item.companyName, value: item.id, raw: item
        })) : [];
      } catch (error) {
        console.error('获取供应商列表失败', error);
        this.companyOptions = [];
      } finally {
        this.companyLoading = false;
      }
    },

    onCompanyChange(val) {
      const selected = this.companyOptions.find(o => o.value === val);
      this.form.companyName = selected ? selected.label : '';
    },

    handleConfirm() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return false;

        this.loading = true;
        try {
          const params = { ...this.form };
          const res = await saveSalesReturnApi(params);
          if (res.code === 200) {
            this.$message.success('新增销售退货成功');
            this.$emit('success');
            this.handleClose();
          } else {
            this.$message.error(res.msg || '新增销售退货失败');
          }
        } catch (error) {
          console.error('新增销售退货失败:', error);
          this.$message.error('新增销售退货失败');
        } finally {
          this.loading = false;
        }
      });
    },

    handleCancel() {
      this.handleClose();
    },

    handleClose() {
      this.$emit('update:visible', false);
      this.resetForm();
    },

    resetForm() {
      this.queryOrderCode = '';
      this.queryOriginalOrderId = '';
      this.form = {
        orderCode: '',
        originalOrderId: '',
        companyId: null,
        companyName: '',
        brand: '',
        category: '',
        productName: '',
        skuName: '',
        skuCode: '',
        quantity: 1,
        originalQuantity: 0,
        returnPrice: null,
        returnDate: null,
        remark: '',
      };
      this.companyOptions = [];
      this.$nextTick(() => {
        if (this.$refs.form) {
          this.$refs.form.clearValidate();
        }
      });
    }
  }
};
</script>

<style scoped lang="scss">
.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--primary-color);
  padding-bottom: 8px;
  margin-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-query-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;

  .query-input {
    flex: 1;
  }
}

.total-amount {
  font-size: 16px;
  font-weight: 700;
  color: #ff6b6b;
}
</style>
