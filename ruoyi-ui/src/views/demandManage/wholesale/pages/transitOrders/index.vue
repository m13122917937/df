<template>
  <div class="orders-wrap transit-orders">
    <BrandFilter
      :status="status"
      :current-brand="brand"
      :province-id="provinceId"
      @brand-change="handleBrandChange"
    />
    <div class="main-content">
      <!-- 左侧筛选面板 -->
      <div class="filter-panels">
        <!-- 地点筛选 -->
        <FilterPanel
          title="收货地点"
          :status="status"
          :current-value="provinceId"
          :current-brand="brand"
          item-icon="el-icon-location-information"
          @change="handleLocationChange"
        />
      </div>

      <!-- 右侧内容区域 -->
      <div class="content-area">
        <!-- 搜索 -->
        <SearchSection
          :status="status"
          :order-code="orderCode"
          :original-order-id="originalOrderId"
          :category="category"
          :shop-name="shopName"
          :product-name-like="productNameLike"
          :sku-name-like="skuNameLike"
          :show-order-code="true"
          :show-original-order-id="true"
          :show-category="true"
          :show-shop-name="true"
          :show-product-name-like="true"
          :show-sku-name-like="true"
          :show-company="true"
          :company-id="companyId"
          @company-change="handleCompanyChange"
          @search="handleSearch"
          @reset="handleReset"
        />
        <!-- 订单表格 -->
        <div class="table-section">
          <el-table
            :key="tableFilterKey"
            ref="table"
            :data="filteredTableData"
            v-loading="loading"
            stripe
            size="medium"
            center
            style="width: 100%"
            :fit="true"
            height="100%"
          >
            <!-- 空数据状态 -->
            <template slot="empty">
              <EmptyState text="暂无在途订单数据" />
            </template>
            <!-- 单号：(订单编码) + (原始订单号) -->
             <el-table-column prop="orderStyle" label="订单类型" width="90" fixed="left" align="center">
              <template slot="header">
                <FilterHeader label="订单类型" :value="columnSearch.orderStyle || []" :options="colFilterOptions.orderStyle || []" @update:value="columnSearch.orderStyle = $event" />
              </template>
              <template slot-scope="scope">
                <OrderStyleBadge
                  :order-style="scope.row.orderStyle"
                />
              </template>
            </el-table-column>
            <!-- 单号：(订单编码) + (原始订单号) -->
            <el-table-column
              label="单号"
              prop="orderCode"
              min-width="300"
              fixed="left"
            >
              <template slot-scope="scope">
                <div class="order-numbers">
                  <div class="order-number-item">内部单号:
                    {{ scope.row.orderCode || "-" }}
                    <i v-if="scope.row.orderCode"
                       class="el-icon-copy-document copy-icon"
                       @click="copyText(scope.row.orderCode)"></i>
                  </div>
                  <div class="order-number-item"> 商家单号:
                    {{ scope.row.originalOrderId || "-" }}
                    <i v-if="scope.row.originalOrderId"
                       class="el-icon-copy-document copy-icon"
                       @click="copyText(scope.row.originalOrderId)"></i>
                  </div>
                </div>
              </template>
            </el-table-column>

            <!-- 发货时间 -->
            <el-table-column
              label="发货时间"
              prop="sendTime"
              min-width="200"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.sendTime || "-"}}</span>
              </template>
            </el-table-column>

            <el-table-column prop="trackingNumber" label="物流信息" width="240" align="center">
              <template slot="header">
                <FilterHeader label="物流信息" :value="columnSearch.trackingNumber || []" :options="colFilterOptions.trackingNumber || []" @update:value="columnSearch.trackingNumber = $event" />
              </template>
              <template slot-scope="scope">
                <TrackingInfo
                  :number="scope.row.trackingNumber"
                  :show-company="false"
                  :data="scope.row"
                  @click="handleReturn(scope.row, 2)"
                />
              </template>
            </el-table-column>

             <!-- 平台 -->
            <el-table-column label="平台/店铺" prop="platform" min-width="200" align="center">
              <template slot="header">
                <FilterHeader label="平台/店铺" :value="columnSearch.platformShop || []" :options="colFilterOptions.platformShop || []" @update:value="columnSearch.platformShop = $event" />
              </template>
              <template slot-scope="scope">
                <div>{{ scope.row.platform || "-" }}</div>
                <div>{{ scope.row.shopName || "-" }}</div>
              </template>
            </el-table-column>


            <!-- 品牌 -->
            <el-table-column label="品牌/品类" prop="brand" min-width="200" align="center">
              <template slot="header">
                <FilterHeader label="品牌/品类" :value="columnSearch.brandCategory || []" :options="colFilterOptions.brandCategory || []" @update:value="columnSearch.brandCategory = $event" />
              </template>
              <template slot-scope="scope">
                <div>{{ scope.row.brand || "-" }}</div>
                <div>{{ scope.row.category || "-" }}</div>
              </template>
            </el-table-column>

            <!-- 产品型号 -->
            <el-table-column
              label="产品型号"
              prop="productName"
              min-width="200"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <FilterHeader label="产品型号" :value="columnSearch.productSku || []" :options="colFilterOptions.productSku || []" @update:value="columnSearch.productSku = $event" />
              </template>
              <template slot-scope="scope">
                <div class="order-productName">
                  <div class="order-productName-line">
                    <span class="pn-model" v-if="scope.row.skuName">{{
                      scope.row.productName || "-"
                    }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span class="pn-model" v-if="scope.row.skuName">{{
                      scope.row.skuName
                    }}</span>
                    <!-- <i
                      v-if="scope.row.productName || scope.row.skuName"
                      class="el-icon-copy-document copy-icon"
                      @click="
                        copyText(`${scope.row.productName}${scope.row.skuName}`)
                      "
                    ></i> -->
                  </div>
                </div>
              </template>
            </el-table-column>

            <!-- 数量 -->
            <el-table-column
              label="数量"
              prop="quantity"
              min-width="130"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.quantity || 0 }}</span>
              </template>
            </el-table-column>

            <!-- 收货地（悬浮查看收件信息） -->
            <el-table-column
              label="收货地"
              prop="address"
              min-width="200"
              align="center"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <FilterHeader label="收货地" :value="columnSearch.addressDisplay || []" :options="colFilterOptions.addressDisplay || []" @update:value="columnSearch.addressDisplay = $event" />
              </template>
              <template slot-scope="scope">
                <el-popover
                  placement="top"
                  trigger="hover"
                  popper-class="addr-popover"
                  :open-delay="150"
                  :close-delay="100"
                >
                  <div class="addr-content">
                    <div class="addr-line main">
                      收件人: {{ scope.row.addressee || "无" }}
                    </div>
                    <div class="addr-line sub">
                      电话: {{ scope.row.phone || "无" }}
                    </div>
                    <div class="addr-line sub">
                      地址: {{ scope.row.receivingAddress || "无" }}
                      <i
                        v-if="scope.row.receivingAddress"
                        class="el-icon-copy-document copy-icon"
                        @click="handleCopyAddress(scope.row)"
                      ></i>
                    </div>
                  </div>
                  <span slot="reference"
                    >{{ scope.row.provinceName }} {{ scope.row.cityName }}
                  </span>
                </el-popover>
              </template>
            </el-table-column>

            <!-- 成交价格 -->
            <el-table-column label="成交价" prop="tradePrice" min-width="300" align="center">
              <template slot-scope="scope">
                <PriceChips :row="scope.row" />
              </template>
            </el-table-column>

            <!-- 账期 -->
            <el-table-column
              label="账期"
              prop="accountingPeriodType"
              min-width="130"
              align="center"
            >
              <template slot="header">
                <FilterHeader label="账期" :value="columnSearch.accountingPeriodType || []" :options="colFilterOptions.accountingPeriodType || []" @update:value="columnSearch.accountingPeriodType = $event" />
              </template>
              <template slot-scope="scope">
                <span>{{
                  formatAccountingPeriod(scope.row.accountingPeriod)
                }}</span>
              </template>
            </el-table-column>

            <!-- 供应商 -->
            <el-table-column
              label="供应商"
              prop="tradeNickName"
              min-width="200"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot="header">
                <FilterHeader label="供应商" :value="columnSearch.supplierDisplay || []" :options="colFilterOptions.supplierDisplay || []" @update:value="columnSearch.supplierDisplay = $event" />
              </template>
              <template slot-scope="scope">
                <el-popover
                  placement="top"
                  trigger="hover"
                  popper-class="addr-popover"
                  :open-delay="150"
                  :close-delay="100"
                >
                  <div class="addr-content">
                    <div class="addr-line main">
                      姓名: {{ scope.row.tradeUserName || "无" }}
                    </div>
                    <div class="addr-line sub">
                      电话: {{ scope.row.tradeUserPhone || "无" }}
                    </div>
                  </div>
                  <span slot="reference">
                    <el-tag
                      v-if="scope.row.tradeTypeName"
                      size="mini"
                      type="warning"
                      >{{ scope.row.tradeTypeName.slice(0, 1) }}</el-tag
                    >
                    {{ scope.row.tradeNickName || "-" }}
                  </span>
                </el-popover>
              </template>
            </el-table-column>

            <el-table-column fixed="right" width="160" align="center">
              <template slot="header">
                <span class="operation-header">操作</span>
              </template>
              <template slot-scope="scope">
                <div class="operation-buttons">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleReturn(scope.row, 1)"
                    >退货追单</el-button
                  >
                  <!-- <el-button
                    type="danger"
                    size="small"
                    @click="handleAbnormal(scope.row)"
                    >转为异常</el-button
                  > -->
                  <el-button
                    type="warning"
                    size="small"
                    @click="handleReturn(scope.row, 3)"
                    >查看串码</el-button
                  >
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pagination.currentPage"
            :page-sizes="[30, 50, 100]"
            :page-size="pagination.pageSize"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </div>

    <!-- 转为异常订单弹框组件 -->
    <AbnormalOrderDialog
      :visible.sync="abnormalDialogVisible"
      :current-order="currentOrder"
      @success="handleAbnormalSuccess"
    />

    <!-- 退货追单弹框组件 -->
    <ReturnOrderDialog
      :visible.sync="returnOrderDialog"
      :current-order="returnOrderList"
      :return-reason.sync="returnReason"
      @success="handleReturnOrderSuccess"
    />

    <!-- 物流信息弹框组件 -->
    <LogisticsDialog
      :visible.sync="logisticsDialogVisible"
      :current-order="currentOrder"
    />

    <!-- 串码弹窗组件 -->
    <ImeiDialog
      :visible.sync="imeiDialogVisible"
      :current-order="currentOrder"
      title="串码信息"
    />
  </div>
</template>

<script>
import BrandFilter from "@/views/demandManage/wholesale/components/brandFilter.vue";
import FilterPanel from "@/views/demandManage/wholesale/components/filterPanel.vue";
import AbnormalOrderDialog from "@/views/demandManage/wholesale/components/abnormalOrderDialog.vue";
import ReturnOrderDialog from "@/views/demandManage/wholesale/components/returnOrderDialog.vue";
import LogisticsDialog from "@/views/demandManage/wholesale/components/logisticsDialog.vue";
import ImeiDialog from "@/views/demandManage/wholesale/components/imeiDialog.vue";
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import PriceChips from "@/views/demandManage/wholesale/components/priceChips.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import tableFilterMixin from "@/mixins/tableFilter";
import TrackingInfo from "@/views/demandManage/wholesale/components/trackingInfo.vue";
import OrderStyleBadge from '@/components/OrderStyleBadge';
import { getOrderSendListApi } from "@/api/wholesale";
import {
  createFormatDateTimeMethod,
  createCopyTextMethod,
} from "@/utils/wholesaleUtils";

export default {
  name: "transitOrders",
  mixins: [tableFilterMixin],
  components: {
    BrandFilter,
    FilterPanel,
    AbnormalOrderDialog,
    ReturnOrderDialog,
    LogisticsDialog,
    ImeiDialog: ImeiDialog,
    SearchSection,
    PriceChips,
    EmptyState,
    TrackingInfo,
    OrderStyleBadge
  },
  data() {
    return {
      loading: false,
      tableFilterKey: 0,
      // 表格数据
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0,
      },
      cityId: "", // 城市ID
      brand: "", // 品牌
      provinceId: "", // 省份ID
      status: 6, // 订单状态：6-在途
      orderCode: "", // 内部单号
      originalOrderId: "", // 商家单号
      category: "", // 品类
      shopName: "", // 店铺名称
      productNameLike: "", // 商品名称
      skuNameLike: "", // SKU名称
      companyId: '', // 供应商
      // 异常订单弹框
      abnormalDialogVisible: false,
      currentOrder: null, // 用于 AbnormalOrderDialog (Object)
      // 退货追单弹框
      returnOrderDialog: false,
      returnOrderList: [], // 用于 ReturnOrderDialog (Array)
      returnReason: 5, // 退货追单原因
      // 物流信息弹框
      logisticsDialogVisible: false,
      // 串码弹窗
      imeiDialogVisible: false,
    };
  },
  mounted() {
    this.initColumnSearch(
      ['orderStyle', 'trackingNumber', 'accountingPeriodType'],
      {
        orderStyle: { display: row => ({ 0: '百补', 1: '百亿微派', 2: '国补' })[row.orderStyle] ?? '-' },
        brandCategory: { display: row => `${row.brand} - ${row.category}` },
        platformShop: { display: row => `${row.platform} - ${row.shopName}` },
        productSku: { display: row => `${row.productName} - ${row.skuName}` },
        addressDisplay: { display: row => `${row.provinceName || ''} ${row.cityName || ''}`.trim() },
        supplierDisplay: { display: row => row.tradeNickName || '-' },
      }
    );
    this.loadData();
  },
  methods: {
    // 金额格式化
    formatPrice(val) {
      const n = Number(val);
      if (!isFinite(n)) return "-";
      return n.toLocaleString("zh-CN", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
    },
    formatNumber(val) {
      const n = Number(val);
      if (!isFinite(n)) return "-";
      return n.toLocaleString("zh-CN", { maximumFractionDigits: 0 });
    },
    formatAccountingPeriod(val) {
      const n = Number(val);
      if (!isFinite(n) || n < 0) return "-";
      return n === 0 ? "当天" : `T+${n}`;
    },
    /**
     * 格式化日期时间 - 使用公用工具函数
     * @param {string} dateTime - 日期时间字符串
     * @returns {string} 格式化后的日期时间
     */
    formatDateTime: createFormatDateTimeMethod(),

    /**
     * 复制文本到剪贴板 - 使用公用工具函数
     * @param {string} text - 要复制的文本
     */
    copyText: createCopyTextMethod(this),

    /**
     * 处理地址复制，避免可访问性警告
     * @param {Object} row - 行数据
     */
    handleCopyAddress(row) {
      const addressText = `收件人:${row.addressee},电话:${row.phone},地址: ${row.receivingAddress}`;
      this.copyText(addressText);

      // 延迟移除焦点，确保复制操作完成后再处理焦点
      this.$nextTick(() => {
        // 移除所有可能的焦点元素
        const activeElement = document.activeElement;
        if (activeElement && activeElement.blur) {
          activeElement.blur();
        }

        // 将焦点转移到 body 或一个安全的元素
        document.body.focus();
      });
    },

    // 加载数据
    async loadData() {
      this.loading = true;
      try {
        let pageData = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
        }
        const params = {
          cityId: this.cityId,
          brand: this.brand,
          companyId: this.companyId,
          orderCodeList: this.orderCode?[this.orderCode] : [], // 内部单号搜索
          originalOrderId: this.originalOrderId, // 商家单号搜索
          category: this.category, // 品类搜索
          shopName: this.shopName, // 店铺名称搜索
          productNameLike: this.productNameLike, // 商品名称搜索
          skuNameLike: this.skuNameLike, // SKU名称搜索
          province: this.provinceId,
          status: this.status,
        };
        let res = await getOrderSendListApi(pageData,params);
        if (res.code == 200) {
          this.tableData = res.rows;
          this.pagination.total = res.total;
          this.tableFilterKey++
        } else {
          this.$message.error(res.msg);
        }
      } catch (error) {
        this.loading = false;
      } finally {
        this.loading = false;
      }
    },

    // 品牌筛选变化
    handleBrandChange(brandId) {
      this.brand = brandId;
      // 重置分页到第一页
      this.pagination.currentPage = 1;
      this.loadData();
    },

    // 地点筛选变化
    handleLocationChange(locationId) {
      this.provinceId = locationId;
      // 重置分页到第一页
      this.pagination.currentPage = 1;
      this.loadData();
    },

    // 搜索处理
    handleSearch(searchData) {
      this.orderCode = searchData.orderCode || "";
      this.originalOrderId = searchData.originalOrderId || "";
      this.category = searchData.category || "";
      this.shopName = searchData.shopName || "";
      this.productNameLike = searchData.productNameLike || "";
      this.skuNameLike = searchData.skuNameLike || "";
      this.companyId = searchData.companyId || "";
      // 重置分页到第一页
      this.pagination.currentPage = 1;
      this.loadData();
    },

    // 重置搜索条件
    handleReset(resetData) {
      this.orderCode = resetData.orderCode || "";
      this.originalOrderId = resetData.originalOrderId || "";
      this.category = resetData.category || "";
      this.shopName = resetData.shopName || "";
      this.productNameLike = resetData.productNameLike || "";
      this.skuNameLike = resetData.skuNameLike || "";
      // also reset supplier filter
      this.companyId = resetData.companyId || "";
      this.pagination.currentPage = 1;
      this.loadData();
    },

    handleCompanyChange(val) {
      this.pagination.currentPage = 1;
      this.companyId = val;
      this.loadData();
    },

    // 转异常
    handleAbnormal(row) {
      this.abnormalDialogVisible = true;
      this.currentOrder = row;
    },

    // 退货追单
    async handleReturn(row, status) {
      if (status === 2) {
        // 查看单号 - 显示物流信息
        this.currentOrder = row;
        this.logisticsDialogVisible = true;
        return;
      }
      if (status === 3) {
        // 查看串码
        this.openImeiDialog(row);
        return;
      }
      // 退货追单
      this.returnOrderList = [row];
      this.returnOrderDialog = true;
    },

    // 打开串码弹窗
    openImeiDialog(row) {
      this.currentOrder = row;
      this.imeiDialogVisible = true;
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.loadData();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadData();
    },

    // 异常订单弹框成功回调
    handleAbnormalSuccess() {
      this.pagination.currentPage = 1;
      this.loadData();
    },

    // 退货追单弹框成功回调
    handleReturnOrderSuccess() {
      this.pagination.currentPage = 1;
      this.loadData();
    },
  },
};
</script>

<style scoped lang="scss">
@import "@/assets/styles/common/order-components.scss";

.orders-wrap {
  flex: 1;
  min-height: 0;
}

.table-section {
  max-height: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
    height: auto;
  }
}

.order-productName {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 操作列样式 */
.operation-header {
  font-weight: 600;
  color: #606266;
}

.operation-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
  align-items: center;
}

.operation-buttons .el-button {
  margin: 0;
  padding: 5px 8px;
  font-size: 12px;
  min-width: 60px;
}
</style>
