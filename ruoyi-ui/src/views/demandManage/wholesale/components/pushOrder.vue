<template>
  <el-dialog
    title="定向推单"
    width="560px"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :append-to-body="true"
    :modal="true"
    :z-index="3000"
    @close="handleClose"
    class="push-order-dialog"
  >
    <div class="push-order-wrapper">

      <el-form
        ref="pushOrderForm"
        :model="form"
        :rules="rules"
        label-width="96px"
        class="push-order-form"
      >
        <el-form-item label="订单列表" prop="orderCodeList">
          <div v-if="orderCodes.length" class="order-code-card">
            <el-scrollbar class="order-code-scroll">
              <el-tag
                v-for="code in orderCodes"
                :key="code"
                size="small"
                class="order-code-tag"
              >
                {{ code }}
              </el-tag>
            </el-scrollbar>
          </div>
          <el-empty
            v-else
            description="暂无订单编号"
            class="order-code-empty"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
              <el-input-number
                v-model="form.price"
                :precision="2"
                :min="0"
                :step="1"
                controls-position="right"
                placeholder="请输入价格"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="账期" prop="accountingPeriod">
              <el-select
              style="width:100%;"
                v-model="form.accountingPeriod"
                placeholder="请选择账期"
                filterable
              >
                <el-option
                  v-for="item in accountingPeriodOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>

        <el-form-item label="公司" prop="companyId">
          <el-select
            style="width:100%;"
            v-model="form.companyId"
            placeholder="请选择公司"
            filterable
            remote
            reserve-keyword
            :remote-method="handleCompanyRemote"
            :loading="companyLoading"
            @visible-change="handleCompanyVisible"
            @change="handleCompanyChange"
          >
            <el-option
              v-for="item in companyOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="指定接单人" prop="userId">
          <el-select
            style="width:100%;"
            v-model="form.userId"
            filterable
            placeholder="请选择接单人"
            :disabled="!form.companyId"
          >
            <el-option
              v-for="item in receiverOptions"
              :key="item.userId"
              :value="item.userId"
              :label="item.nickName + '--' + item.phone"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="发货时效" prop="deliveryTime">
          <el-select
            v-model="form.deliveryTime"
            placeholder="请选择发货时效"
             style="width:100%;"
            filterable
          >
            <el-option
              v-for="item in deliveryTimeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getBusinessCompanyListApi,getBusinessUserListApi } from "@/api/business";
export default {
  name: "PushOrderDialog",
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    orderCodeList: {
      type: [Array, String],
      default: () => [],
    },
  },
  data() {
    return {
      dialogVisible: false,
      companyLoading: false,
      companyOptions: [],
      receiverLoading: false,
      receiverOptions: [],
      accountingPeriodOptions: [
        { label: "现款", value: 0 },
        { label: "T+1", value: 1 },
        { label: "T+2", value: 2 },
        { label: "T+3", value: 3 },
        { label: "T+4", value: 4 },
        { label: "T+5", value: 5 },
        { label: "T+6", value: 6 },
        { label: "T+7", value: 7 },
      ],
      deliveryTimeOptions: [
        {
            label: '当天',
            value: 0
        },
        {
            label: '次日',
            value: 1
        },
        {
            label: '后天',
            value: 2
        }
      ],
      lastOpenedAt: null,
      form: {
        orderCodeList: [],
        price: null,
        accountingPeriod: null,
        companyId: null,
        userId: null,
        deliveryTime: 0,
      },
      rules: {
        orderCodeList: [
          {
            required: true,
            message: "请选择需要推送的订单",
            trigger: "change",
          },
        ],
        price: [
          { required: true, message: "请输入价格", trigger: "blur" },
          {
            type: "number",
            message: "价格需为数字",
            trigger: ["blur", "change"],
          },
          {
            validator: (rule, value, callback) => {
              if (value !== null && value !== undefined && value < 0) {
                callback(new Error("价格最小值为0"));
              } else {
                callback();
              }
            },
            trigger: ["blur", "change"],
          },
        ],
        accountingPeriod: [
          { required: true, message: "请选择账期", trigger: "change" },
        ],
        companyId: [
          { required: true, message: "请选择公司", trigger: "change" },
        ],
        userId: [
          { required: true, message: "请选择指定接单人", trigger: "change" },
        ],
        deliveryTime: [
          { required: true, message: "请选择发货时效", trigger: "change" },
        ],
      },
    };
  },
  computed: {
    orderCodes() {
      if (Array.isArray(this.form.orderCodeList)) {
        return this.form.orderCodeList;
      }
      if (typeof this.form.orderCodeList === "string") {
        return this.form.orderCodeList
          .split(/\n|,|，|\s/)
          .map((item) => item.trim())
          .filter(Boolean);
      }
      return [];
    },
    orderCount() {
      return this.orderCodes.length;
    },
    lastOperationTime() {
      if (!this.lastOpenedAt) return "";
      const diff = Date.now() - this.lastOpenedAt;
      const minute = 60 * 1000;
      const hour = 60 * minute;
      if (diff < minute) return "刚刚";
      if (diff < hour) {
        return `${Math.floor(diff / minute)}分钟前`;
      }
      if (diff < 24 * hour) {
        return `${Math.floor(diff / hour)}小时前`;
      }
      const date = new Date(this.lastOpenedAt);
      return `${date.getFullYear()}-${(date.getMonth() + 1)
        .toString()
        .padStart(2, "0")}-${date
        .getDate()
        .toString()
        .padStart(2, "0")} ${date
        .getHours()
        .toString()
        .padStart(2, "0")}:${date
        .getMinutes()
        .toString()
        .padStart(2, "0")}`;
    },
    deliveryTimeLabel() {
      const target = this.deliveryTimeOptions.find(
        (item) => item.value === this.form.deliveryTime
      );
      return target ? target.label : "未选择";
    },
  },
  watch: {
    visible: {
      handler(val) {
        this.dialogVisible = val;
        if (val) {
          this.prepareDialog();
          this.initCompanyOptions();
          this.initReceiverOptions();
        }
      },
      immediate: true,
    },
    'form.companyId': {
      handler(newVal) {
        if (newVal) {
          this.initReceiverOptions();
        }
      },
    },
    orderCodeList: {
      handler() {
        this.form.orderCodeList = this.normalizeOrderCodeList(this.orderCodeList);
      },
      immediate: true,
    },
  },
  methods: {
    prepareDialog() {
      this.lastOpenedAt = Date.now();
      this.resetForm({ keepOrders: true });
    },
    normalizeOrderCodeList(list) {
      return Array.isArray(list) ? [...list] : list;
    },

    async fetchCompanyOptions(keyword = "") {
      this.companyLoading = true;
      const params = {
      };
      const pageData = {
        pageNum: 1,
        pageSize: 30,
      }
      if (keyword) {
        params.companyNameLike = keyword;
      }
      try {
        const res = await getBusinessCompanyListApi(params,pageData);
        const list = res?.rows || res?.data || [];
        this.companyOptions = Array.isArray(list)
          ? list.map((item) => ({
              label: item.companyName,
              value: item.id,
            }))
          : [];
      } catch (error) {
        console.error("获取公司列表失败", error);
        this.companyOptions = [];
      } finally {
        this.companyLoading = false;
        // 不自动选择第一个公司，保持默认为 null，用户需手动选择
      }
    },
    initCompanyOptions() {
      if (!this.companyOptions.length && !this.companyLoading) {
        this.fetchCompanyOptions();
      }
    },
    handleCompanyRemote(keyword = "") {
      const term = (keyword || "").trim();
      this.fetchCompanyOptions(term);
    },
    handleCompanyVisible(visible) {
      if (visible && !this.companyOptions.length && !this.companyLoading) {
        this.fetchCompanyOptions();
      }
    },
    handleCompanyChange() {
      // 当公司改变时，清空接单人选择
      this.form.userId = null;
      this.receiverOptions = [];
    },
    async fetchReceiverOptions() {
      if (!this.form.companyId) {
        this.receiverOptions = [];
        return;
      }
      this.receiverLoading = true;
      const params = {
        pageNum: 1,
        pageSize: 999,
      };
      try {
        const res = await getBusinessUserListApi(this.form.companyId, params);
        const list = res?.rows || [];
        this.receiverOptions = list
      } catch (error) {
        console.error("获取接单人列表失败", error);
        this.receiverOptions = [];
      } finally {
        this.receiverLoading = false;
      }
    },
    initReceiverOptions() {
      if (this.form.companyId && !this.receiverOptions.length && !this.receiverLoading) {
        this.fetchReceiverOptions();
      }
    },
    resetForm({ keepOrders = false } = {}) {
      const baseOrderCodes = keepOrders
        ? this.normalizeOrderCodeList(this.orderCodeList)
        : [];
      this.form = {
        orderCodeList: baseOrderCodes,
        price: null,
        accountingPeriod: null,
        companyId: null,
        userId: null,
        deliveryTime: 0,
      };
      this.$nextTick(() => {
        this.$refs.pushOrderForm && this.$refs.pushOrderForm.clearValidate();
      });
    },
    emitVisible(value) {
      this.$emit("update:visible", value);
    },
    handleClose() {
      this.emitVisible(false);
      this.$emit("close");
      this.resetForm();
    },
    handleCancel() {
      this.dialogVisible = false;
      this.handleClose();
    },
    handleConfirm() {
      this.$refs.pushOrderForm.validate((valid) => {
        if (!valid) return;
        const payload = {
          ...this.form,
          orderCodeList: this.orderCodes,
        };
        this.$emit("confirm", payload);
        this.dialogVisible = false;
        this.emitVisible(false);
      });
    },
  },
};
</script>

<style scoped>
.push-order-wrapper {
  padding: 4px 6px 2px;
}
.push-order-summary {
  display: flex;
  justify-content: space-between;
  background: #f6f8fb;
  border: 1px solid #e5e9f2;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 12px;
}
.summary-item {
  flex: 1;
  text-align: center;
}
.summary-label {
  font-size: 12px;
  color: #909399;
}
.summary-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-top: 4px;
}
.push-order-divider {
  margin: 12px 0 18px;
}
.push-order-form {
  padding-right: 6px;
}
.order-code-card {
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  padding: 10px;
  background-color: #fafcff;
}
.order-code-scroll {
  max-height: 160px;
}
.order-code-tag {
  margin: 4px 6px 0 0;
}
.order-code-empty {
  padding: 10px 0;
}
.dialog-footer {
  text-align: right;
}
</style>
