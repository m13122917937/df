<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="`${productName} ${specName} ${provinceName}`"
    width="900px"
    :close-on-click-modal="false"
    :modal="true"
    :append-to-body="true"
    :z-index="3000"
    @close="handleClose"
    class="procurement-dialog-wrapper"
  >
    <div class="procurement-dialog">
      <!-- 订单基本信息 -->
      <!-- <div class="form-section">
        <h3>商品信息</h3>
        <div class="form-row">
          <div class="form-item">
            <label>产品型号</label>
            <el-select
              disabled
              v-model="productName"
              filterable
              placeholder="请选择产品型号"
              remote
              :loading="loadingProduct"
              :remote-method="remoteMethod"
              @change="getProductSku"
            >
              <el-option
                v-for="item in productList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </div>
          <div class="form-item">
            <label>规格</label>
            <el-select disabled v-model="specName" filterable placeholder="请选择规格">
              <el-option
                v-for="item in skuList"
                :key="item.skuCode"
                :label="item.specName"
                :value="item.skuCode"
              >
                <div @click="selectSku(item)">{{ item.specName }}</div>
              </el-option>
            </el-select>
          </div>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label>sku</label>
            <el-input
              disabled
              @blur="changeSkuCode"
              v-model="formData.skuCode"
              placeholder="请输入sku"
            ></el-input>
          </div>
        </div>
      </div> -->
      <!-- 价格配置 -->
      <div class="form-section">
        <h3>价格配置</h3>
        <div class="price-config">
          <div class="price-row">
            <div class="price-item">
              <label>价1</label>
              <el-input-number
                v-model="formData.priceHighest"
                :precision="0"
                :min="0"
                placeholder="0.00"
                controls-position="right"
                @change="calculatePrices"
              />
            </div>
            <div class="price-item">
              <label>间隔差价</label>
              <el-input-number
                v-model="formData.intervalSpread"
                :precision="0"
                :min="1"
                :step="5"
                placeholder="0.00"
                controls-position="right"
                @change="calculatePrices"
              />
            </div>
            <div class="price-item">
              <label>价2</label>
              <el-input-number
                v-model="formData.priceHign"
                :precision="0"
                disabled
                :min="0"
                placeholder="0.00"
                controls-position="right"
              />
            </div>
            <div class="price-item">
              <label>价3</label>
              <el-input-number
                v-model="formData.priceLow"
                :precision="0"
                disabled
                :min="0"
                placeholder="0.00"
                controls-position="right"
              />
            </div>
            <div class="price-item">
              <label>价4</label>
              <el-input-number
                v-model="formData.priceLowest"
                :precision="0"
                disabled
                :min="0"
                placeholder="0.00"
                controls-position="right"
              />
            </div>
          </div>
          <div class="price-row">
            <!-- <div class="price-item">
              <label>省份</label>
              <el-select v-model="formData.province" placeholder="请选择省份">
                <el-option
                  v-for="item in provinceData"
                  :key="item.districtId"
                  :label="item.district"
                  :value="item.districtId"
                ></el-option>
              </el-select>
            </div> -->
            <div class="price-item">
              <label>账期类型</label>
              <el-select
                v-model="formData.accountingPeriod"
                placeholder="请选择账期类型"
              >
                <el-option label="现款" :value="0"></el-option>
                <el-option label="T+1" :value="1"></el-option>
                <el-option label="T+2" :value="2"></el-option>
                <el-option label="T+3" :value="3"></el-option>
                <el-option label="T+4" :value="4"></el-option>
                <el-option label="T+5" :value="5"></el-option>
                <el-option label="T+6" :value="6"></el-option>
                <el-option label="T+7" :value="7"></el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>
      <!-- 发货配置 -->
      <div class="form-section">
        <h3>发货配置</h3>
        <div class="form-row">
          <div class="form-item">
            <label>发货时效</label>
            <el-select
              v-model="formData.deliveryTime"
              placeholder="请选择发货时效"
            >
              <el-option label="当天" :value="0"></el-option>
              <el-option label="明天" :value="1"></el-option>
              <el-option label="后天" :value="2"></el-option>
            </el-select>
          </div>
          <div class="form-item">
            <label>串码选项</label>
            <el-select
              v-model="formData.codeOptions"
              placeholder="请选择串码选项"
            >
              <el-option label="发货前提供串码" :value="0"></el-option>
              <el-option label="发货后提供串码" :value="1"></el-option>
              <el-option label="不需要串码" :value="2"></el-option>
            </el-select>
          </div>
        </div>
      </div>

      <!-- 成交数据展示 -->
      <div class="form-section">
        <h3>成交数据</h3>
        <div class="trade-data-container">
          <div class="trade-data-row">
            <!-- 今天成交数据 -->
            <div class="trade-data-section">
              <h4>今天成交数据</h4>
              <div class="trade-data-table">
                <el-table
                  :data="tradeListToday"
                  border
                  size="mini"
                  height="300px"
                  style="width: 100%"
                  :show-header="true"
                  empty-text="暂无数据"
                >
                  <el-table-column prop="tradePrice" label="价格" min-width="120" align="center">
                    <template slot-scope="scope">
                      <span class="price-text">¥{{ scope.row.tradePrice }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="quantity" label="数量" min-width="120" align="center">
                    <template slot-scope="scope">
                      <span class="count-text">{{ scope.row.quantity }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>

            <!-- 昨天成交数据 -->
            <div class="trade-data-section">
              <h4>昨天成交数据</h4>
              <div class="trade-data-table">
                <el-table
                  :data="tradeListYesterday"
                  border
                  size="mini"
                  height="300px"
                  style="width: 100%"
                  :show-header="true"
                  empty-text="暂无数据"
                >
                  <el-table-column prop="tradePrice" label="价格" min-width="120" align="center">
                    <template slot-scope="scope">
                      <span class="price-text">¥{{ scope.row.tradePrice }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="quantity" label="数量" min-width="120" align="center">
                    <template slot-scope="scope">
                      <span class="count-text">{{ scope.row.quantity }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 时间配置 -->
      <!-- <div class="form-section">
        <h3>时间配置</h3>
        <div class="form-row">
          <div class="form-item">
            <label>每次报价时间(分钟)</label>
            <el-input-number
              v-model="formData.quotationInterval"
              :min="1"
              :max="1440"
              placeholder="5"
              controls-position="right"
            />
          </div>
          <div class="form-item">
            <label>发布时间</label>
            <el-date-picker
              v-model="formData.releaseTime"
              type="datetime"
              placeholder="选择发布时间"
              format="yyyy-MM-dd HH:mm"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </div>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label>发布信息类型</label>
            <el-select v-model="formData.releaseType" placeholder="请选择发布类型">
              <el-option label="立即发布" :value="0"></el-option>
            </el-select>
          </div>

        </div>
      </div> -->

      <!-- 其他要求 -->
      <!-- <div class="form-section">
        <h3>其他要求</h3>
        <div class="form-row">
          <div class="form-item full-width">
            <label>其他要求</label>
            <el-input
              v-model="formData.otherRequire"
              type="textarea"
              :rows="2"
              placeholder="请输入其他要求"
              maxlength="20"
              show-word-limit
            />
          </div>
        </div>
      </div> -->
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { apiRuleSave } from "@/api/creatingOrders";
import { apiQuotation } from "@/api/creatingOrders";
import { mapGetters, mapActions } from "vuex";
import {
  apiGetProduct,
  apiGetProductSku,
  apiGetProductInfo,
  apiGetTradeListToday,
  apiGetTradeListYesterday
} from "@/api/goods";
import { Select } from "element-ui";
export default {
  name: "ProcurementDialog",
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    isEdit:{
      type: Boolean,
      default: false,
    },
    orderData: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      dialogVisible: false,
      brand: "",
      category: "",
      specName: "",
      productName: "",
      provinceName:"",
      sku: "",
      brandList: [],
      categoryList: [],
      productList: [],
      skuList: [],
      loadingProduct: false,
      tradeListToday:[],
      tradeListYesterday:[],
      formData: {
        // 订单基本信息
        accountingPeriod: 2,
        skuCode: null,
        // 价格配置
        priceLowest: null,
        priceLow: null,
        priceHign: null,
        priceHighest: null,
        intervalSpread: 10,

        // 发货配置
        deliveryTime: 0,
        codeOptions: 0,
        province: null,
        // 时间配置
        quotationInterval: 5,
        releaseTime: "",
        releaseType: 0,
        ruleRange: 0,
        // 其他要求
        otherRequire: "",
      },
    };
  },
  computed: {
    ...mapGetters(["provinceList"]),
    // 从Vuex获取省份列表
    provinceData() {
      return this.provinceList || [];
    },
  },
  watch: {
    visible: {
      handler(newVal) {
        this.dialogVisible = newVal;
        // 只有在对话框可见且有orderData时才初始化表单数据
        if (newVal && this.orderData) {
          this.initFormData();
          this.getTradeListToday();
          this.getTradeListYesterday();
        }
      },
      immediate: true,
    },
  },
  mounted() {
    this.getProvinceList();
  },
  methods: {
    ...mapActions(["GET_PROVINCE_LIST"]),
    async getProvinceList() {
      // 确保Vuex中有省份数据
      await this.GET_PROVINCE_LIST();
    },
    getTradeListToday(){
      apiGetTradeListToday({orderCode:this.orderData.orderCode,province:this.orderData.province}).then(res=>{
        if(res.code == 200){
          const tradeListToday = res.data || [];
          this.tradeListToday = tradeListToday
        }
      });
    },
    getTradeListYesterday(){
      apiGetTradeListYesterday({orderCode:this.orderData.orderCode,province:this.orderData.province}).then(res=>{
        if(res.code == 200){
          const tradeListYesterday = res.data || [];
          this.tradeListYesterday = tradeListYesterday
        }
      });
    },
    remoteMethod(query) {
      if (query) {
        this.loadingProduct = true;
        apiGetProduct({ productName: query }).then((res) => {
          if (res.code == 200) {
            let productList = res.data || [];
            this.productList = productList.map((val) => {
              return {
                label: val.productName,
                value: val.productName,
              };
            });
            this.loadingProduct = false;
          }
        });
      } else {
        this.productList = [];
      }
    },

    getProductSku() {
      this.skuList = [];
      apiGetProductSku({ productName: this.productName }).then((res) => {
        if (res.code == 200) {
          this.skuList = res.data;
        }
      });
    },
    selectSku(data) {
      console.log("data", data);
      this.formData.skuCode = data.skuCode;
    },

    changeSkuCode() {
      if (this.formData.skuCode) {
        apiGetProductInfo({ skuCode: this.formData.skuCode }).then((res) => {
          if (res.code == 200) {
            const { data = {} } = res;
            this.brand = data.brand || null;
            this.category = data.category || null;
            this.productName = data.productName || null;
            this.specName = data.specName || null;
            if (this.productName) {
              this.remoteMethod(this.productName);
              this.getProductSku();
            } else {
              this.productList = [];
            }
          }
        });
      }
    },

    // 计算价格方法
    calculatePrices() {
      const priceHighest = this.formData.priceHighest || 0;
      const intervalSpread = this.formData.intervalSpread || 0;

      // 价2 = 价1 - 间隔差价
      this.formData.priceHign = priceHighest - intervalSpread;

      // 价3 = 价2 - 间隔差价
      this.formData.priceLow = this.formData.priceHign - intervalSpread;

      // 价4 = 价3 - 间隔差价
      this.formData.priceLowest = this.formData.priceLow - intervalSpread;

      // 确保价格不为负数
      if (this.formData.priceHign < 0) this.formData.priceHign = 0;
      if (this.formData.priceLow < 0) this.formData.priceLow = 0;
      if (this.formData.priceLowest < 0) this.formData.priceLowest = 0;
    },

    initFormData() {
      this.provinceName = this.orderData.provinceName || null;

      this.formData = {
        // 订单基本信息
        accountingPeriod: 0,
        skuCode: null,
        // 价格配置
        priceLowest: null,
        priceLow: null,
        priceHign: null,
        priceHighest: null,
        intervalSpread: 10,

        // 发货配置
        deliveryTime: 0,
        codeOptions: 0,
        province: null,
        // 时间配置
        quotationInterval: 5,
        releaseTime: "",
        releaseType: 0,
        ruleRange: 0,
        // 其他要求
        otherRequire: "",
      };
      if(this.orderData.brand == "苹果"){
        this.formData.accountingPeriod = 1
      }
      else{
        this.formData.accountingPeriod = 3
      }
      // 添加空值检查，防止orderData为null时出错
      if (this.orderData) {
        this.productName = this.orderData.productName || "";
        this.specName = this.orderData.skuName || "";
      } else {
        this.productName = "";
        this.specName = "";
      }
      // 设置默认发布时间为当前时间
      if (!this.formData.releaseTime) {
        this.formData.releaseTime = this.formatDateTime(new Date());
      }
      // 根据传入的订单数据初始化表单
      if (this.orderData && Object.keys(this.orderData).length > 0) {
        Object.assign(this.formData, this.orderData);

        // this.changeSkuCode()
      }
    },


    formatDateTime(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      const seconds = String(date.getSeconds()).padStart(2, "0");
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },

    handleClose() {
      this.$emit("update:visible", false);
    },

    handleCancel() {
      this.$emit("update:visible", false);
    },

    handleConfirm() {
      // 表单验证
      if (!this.validateForm()) {
        return;
      }

      apiQuotation(this.formData).then((res) => {
        if (res.code == 200) {
          this.$emit("confirm");
          this.$emit("update:visible", false);
        }
      });
    },

    validateForm() {
      // 价格验证
      const prices = [
        this.formData.priceLowest,
        this.formData.priceLow,
        this.formData.priceHign,
        this.formData.priceHighest,
      ].filter((price) => price !== null && price !== undefined);

      if (
        this.formData.priceLowest <= 0 ||
        this.formData.priceLow <= 0 ||
        this.formData.priceHign <= 0 ||
        this.formData.priceHighest <= 0
      ) {
        this.$message.error("价格填写有误");
        return false;
      }
      if (prices.length === 0) {
        this.$message.error("请至少填写一个价格");
        return false;
      }
      if (!this.formData.skuCode) {
        this.$message.error("请选择商品sku");
        return false;
      }
      // 验证价格逻辑
      if (
        this.formData.priceLowest &&
        this.formData.priceLow &&
        this.formData.priceLowest > this.formData.priceLow
      ) {
        this.$message.error("价4不能大于价3");
        return false;
      }

      if (
        this.formData.priceLow &&
        this.formData.priceHign &&
        this.formData.priceLow > this.formData.priceHign
      ) {
        this.$message.error("价3不能大于价2");
        return false;
      }

      if (
        this.formData.priceHign &&
        this.formData.priceHighest &&
        this.formData.priceHign > this.formData.priceHighest
      ) {
        this.$message.error("价2不能大于价1");
        return false;
      }

      return true;
    },
  },
};
</script>

<style scoped lang="scss">
.procurement-dialog-wrapper {
  ::v-deep .el-dialog {
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);

    .el-dialog__header {
      padding: 20px 24px 16px;
      border-bottom: 1px solid #e8eaec;
      background: #fafbfc;
      border-radius: 8px 8px 0 0;

      .el-dialog__title {
        font-size: 16px;
        font-weight: 600;
        color: #1f2329;
      }

      .el-dialog__close {
        font-size: 18px;
        color: #8f959e;

        &:hover {
          color: #1f2329;
        }
      }
    }

    .el-dialog__body {
      padding: 24px;
      max-height: 75vh;
      overflow-y: auto;
      background: #ffffff;
    }

    .el-dialog__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid #e8eaec;
      background: #fafbfc;
      border-radius: 0 0 8px 8px;
    }
  }
}

.procurement-dialog {
  .form-section {
    margin-bottom: 28px;

    h3 {
      margin: 0 0 20px 0;
      font-size: 16px;
      font-weight: 600;
      color: #1f2329;
      padding-bottom: 8px;
      border-bottom: 2px solid #f0f2f5;
    }
  }

  .form-row {
    display: flex;
    gap: 20px;
    margin-bottom: 16px;
    align-items: flex-start;

    .form-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;

      &.full-width {
        flex: 1 1 100%;
      }

      label {
        font-size: 14px;
        color: #4e5969;
        font-weight: 500;
        white-space: nowrap;
        margin-bottom: 4px;
      }

      .el-input,
      .el-select,
      .el-input-number,
      .el-date-picker {
        width: 100%;
      }
    }
  }

  .price-config {
    margin: 16px 0;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e8eaec;

    .price-row {
      display: flex;
      gap: 20px;
      margin-bottom: 16px;

      &:last-child {
        margin-bottom: 0;
      }

      .price-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 8px;

        label {
          font-size: 14px;
          color: #4e5969;
          font-weight: 500;
        }
      }
    }
  }

  .trade-data-container {
    margin: 16px 0;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e8eaec;

    .trade-data-row {
      display: flex;
      gap: 20px;

      .trade-data-section {
        flex: 1;
        margin-bottom: 0;

        h4 {
          margin: 0 0 12px 0;
          font-size: 14px;
          font-weight: 600;
          color: #1f2329;
          padding-bottom: 6px;
          border-bottom: 1px solid #e8eaec;
        }

        .trade-data-table {
          ::v-deep .el-table {
            border-radius: 6px;
            overflow: hidden;

            .el-table__header {
              background: #f5f7fa;

              th {
                background: #f5f7fa;
                color: #606266;
                font-weight: 600;
                font-size: 13px;
                padding: 8px 0;
              }
            }

            .el-table__body {
              tr {
                &:hover {
                  background: #f5f7fa;
                }

                td {
                  padding: 8px 0;
                  font-size: 13px;
                }
              }
            }
          }

          .price-text {
            color: #e6a23c;
            font-weight: 600;
          }

          .count-text {
            color: #409eff;
            font-weight: 600;
          }
        }
      }
    }
  }
}

.dialog-footer {
  text-align: right;

  .el-button {
    margin-left: 12px;
    padding: 8px 20px;
    border-radius: 4px;
    font-weight: 500;

    &.el-button--default {
      background: #ffffff;
      border-color: #d9d9d9;
      color: #4e5969;

      &:hover {
        border-color: #409eff;
        color: #409eff;
      }
    }

    &.el-button--primary {
      background: #409eff;
      border-color: #409eff;

      &:hover {
        background: #66b1ff;
        border-color: #66b1ff;
      }
    }
  }
}

// 全局样式调整
::v-deep .el-input__inner {
  border-radius: 4px;
  border-color: #d9d9d9;
  transition: border-color 0.3s, box-shadow 0.3s;

  &:hover {
    border-color: #c0c4cc;
  }

  &:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
}

::v-deep .el-input-number {
  width: 100%;

  .el-input__inner {
    text-align: left;
  }
}

::v-deep .el-textarea__inner {
  border-radius: 4px;
  border-color: #d9d9d9;
  transition: border-color 0.3s, box-shadow 0.3s;

  &:hover {
    border-color: #c0c4cc;
  }

  &:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
}
</style>
