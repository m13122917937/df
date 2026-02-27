<template>
  <div class="wrap">
    <div class="wrap_btn">
      <!-- 品牌筛选组件 -->

      <BrandFilter
        :status="currentStatus"
        :current-brand="searchParams.brand"
        :province-id="searchParams.province"
        @brand-change="handleBrandChange"
      />
    </div>
    <div class="body">
      <!-- 左侧省份筛选 -->
      <div class="filter-panels-small">
        <FilterPanel
          title="收货地点"
          :status="currentStatus"
          :current-value="searchParams.province"
          :current-brand="searchParams.brand"
          @change="handleLocationChange"
        />
      </div>
      <!-- 列表 -->
      <div class="tableList">
        <SearchSection
          :status="currentStatus"
          :order-code="searchParams.orderCode"
          :original-order-id="searchParams.originalOrderId"
          :category="searchParams.category"
          :shop-name="searchParams.shopName"
          :product-name-like="searchParams.productNameLike"
          :sku-name-like="searchParams.skuNameLike"
          :show-order-code="true"
          :show-original-order-id="true"
          :show-category="true"
          :show-shop-name="true"
          :show-product-name-like="true"
          :show-sku-name-like="true"
          :show-company="true"
          :company-id="searchParams.companyId"
          @company-change="handleCompanyChange"
          @search="handleSearch"
          @reset="handleReset"
        >
          <template #toolbar>
            <div class="toolbar-content">
              <div class="toolbar-left">
                <!-- 可以添加其他工具栏项目 -->
              </div>
              <div class="toolbar-right">
                <el-button
                  type="primary"
                  @click="handleOrderToAfterSales"
                >
                  订单转售后
                </el-button>
              </div>
            </div>
          </template>
        </SearchSection>
        <!-- 订单表格 -->
        <div class="table-section">
          <el-table
            ref="table"
            v-loading="tableLoading"
            stripe
            size="medium"
            center
            :cell-style="{ height: '60px' }"
            :header-cell-style="{
              height: '48px',
              background: '#f7f8fa',
              color: '#606266',
              fontWeight: 600,
            }"
            :data="tableDataList"
            style="width: 100%"
            class="nl-table-default"
            element-loading-text="数据加载中"
            :height="tableHeight"
            v-table-height="{ bottomOffset: 94 }"
          >
            <!-- 空数据状态 -->
            <template slot="empty">
              <EmptyState text="暂无发货中数据" />
            </template>
            <el-table-column
              v-for="(item, index) in columnData"
              :key="index"
              :minWidth="item.minWidth"
              :prop="item.prop"
              :fixed="item.fixed || null"
              :align="item.align || 'center'"
              :show-overflow-tooltip="true"
            >
              <!-- 表头 -->
              <template #header>
                <div>
                  <div class="text">{{ item.label }}</div>
                </div>
              </template>
              <!-- 表身 -->
              <template slot-scope="{ row }">
                <div v-if="item.prop === 'orderStyle'">
                  <OrderStyleBadge
                    :order-style="row.orderStyle"
                  />
                </div>
                <!-- 单号 -->
                <div v-else-if="item.prop === 'orderNumbers'">
                  <div class="order-numbers">
                    <div class="order-number-item">内部单号:
                      {{ row.orderCode || "-" }}
                      <i
                        v-if="row.orderCode"
                        class="el-icon-copy-document copy-icon"
                        @click="copyText(row.orderCode)"
                      ></i>
                    </div>
                    <div class="order-number-item"> 商家单号:
                      {{ row.originalOrderId || "-" }}
                      <i
                        v-if="row.originalOrderId"
                        class="el-icon-copy-document copy-icon"
                        @click="copyText(row.originalOrderId)"
                      ></i>
                    </div>
                  </div>
                </div>
                <div v-else-if="item.prop === 'platform'">
                  <div>{{ row.platform }}</div>
                  <div>{{ row.shopName }}</div>
                </div>
                <div v-else-if="item.prop === 'brand'">
                  <div>{{ row.brand }}</div>
                  <div>{{ row.category }}</div>
                </div>
                <div v-else-if="item.prop === 'sku'">
                  <div class="order-productName">
                    <div class="order-productName-line">
                      <span class="pn-model" v-if="row.productName">{{
                        row.productName || "-"
                      }}</span>
                    </div>
                    <div class="order-productName-line">
                      <span class="pn-model" v-if="row.skuName">{{
                        row.skuName
                      }}</span>
                      <!-- <i
                        v-if="row.productName || row.skuName"
                        class="el-icon-copy-document copy-icon"
                        @click="copyText(`${row.productName}${row.skuName}`)"
                      ></i> -->
                    </div>
                  </div>
                </div>
                 <div v-else-if="item.prop === 'subStatus'">
                  {{ getSubStatus(row[item.prop]) }}
                </div>
                <div v-else-if="item.prop === 'address'">
                  <el-popover
                    placement="top"
                    trigger="hover"
                    popper-class="addr-popover"
                    :open-delay="150"
                    :close-delay="100"
                  >
                    <div class="addr-content">
                      <div class="addr-line main">
                        收件人: {{ row.addressee || "无" }}
                      </div>
                      <div class="addr-line sub">
                        电话: {{ row.phone || "无" }}
                      </div>
                      <div class="addr-line sub">
                        地址: {{ row.receivingAddress || "无" }}
                        <i
                          v-if="row.receivingAddress"
                          class="el-icon-copy-document copy-icon"
                          @click="handleCopyAddress(row)"
                        ></i>
                      </div>
                    </div>
                    <span slot="reference"
                      >{{ row.provinceName }} {{ row.cityName }}</span
                    >
                  </el-popover>
                </div>
                <div v-else-if="item.prop === 'tradeCompanyName'">
                  <el-popover
                    placement="top"
                    trigger="hover"
                    popper-class="addr-popover"
                    :open-delay="150"
                    :close-delay="100"
                  >
                    <div class="addr-content">
                      <div class="addr-line main">
                        姓名: {{ row.tradeUserName || "无" }}
                        <i
                          v-if="row.tradeUserName"
                          class="el-icon-copy-document copy-icon"
                          @click="copyText(`${row.tradeUserName}`)"
                        ></i>
                      </div>
                      <div class="addr-line sub">
                        电话: {{ row.tradeUserPhone || "无" }}
                        <i
                          v-if="row.tradeUserPhone"
                          class="el-icon-copy-document copy-icon"
                          @click="copyText(`${row.tradeUserPhone}`)"
                        ></i>
                      </div>
                    </div>
                    <span slot="reference">
                      <el-tag
                        v-if="row.tradeTypeName"
                        size="mini"
                        type="warning"
                        >{{ row.tradeTypeName.slice(0, 1) }}</el-tag
                      >
                      {{ row.tradeCompanyName || "-" }}
                    </span>
                  </el-popover>
                </div>

                <!-- <div v-else-if="item.prop === 'tradeprice'">
                  <PriceChips :row="row" />
                </div> -->
                <!-- 最晚发货时间 - 特殊颜色处理 -->
                <div v-else-if="item.prop === 'lastShippingTime'" :class="getTimeStatusClass(row)">
                  {{ row[item.prop] || "-" }}
                </div>
                <div v-else>
                  {{ row[item.prop] || "-" }}
                </div>
              </template>
            </el-table-column>
            <!-- <el-table-column
              fixed="right"
              width="220"
              class-name="action-btn-list"
            >
              <template slot="header">
                <div slot="reference">
                  <div class="text">操作</div>
                </div>
              </template>
              <template slot-scope="{ row }">
                <div class="btn">
                  <el-button
                    type="primary"
                    size="mini"
                    @click="handleReturn(row)"
                    >退货追单</el-button
                  >
                  <el-button
                    type="primary"
                    v-if="row.handleApply == 0"
                    size="mini"
                    @click="handleApply(row)"
                    >处理申请</el-button
                  >
                </div>
              </template>
            </el-table-column> -->
          </el-table>
        </div>
        <div class="pagination-section">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="searchParams.pageNum"
            :page-sizes="[30, 50, 100]"
            :page-size="searchParams.pageSize"
            :total="totalNum"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </div>
    <!-- 退货追单 -->
    <ReturnOrderDialog
      :visible.sync="returnOrderDialog"
      :current-order="currentOrder"
      :return-reason.sync="returnReason"
      @success="initTable"
    />
    <!-- 订单转售后 -->
    <OrderToAfterSalesDialog
      :visible.sync="orderToAfterSalesDialog"
      @success="initTable"
    />
    <!-- 验证拼多多销售 -->
    <PddDialog ref="pddDialog" @upload-success="getData" />
    <handleApplication ref="handleApplicationRef" @confirm="getData"></handleApplication>
    <!-- 补单发货 -->
  </div>
</template>
<script>
import FilterPanel from "../../components/filterPanel.vue";
import BrandFilter from "../../components/brandFilter.vue";
import PddDialog from "../../components/PddDialog.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import { exportError } from "@/api/creatingOrders";
import {getOrderSendListApi}  from "@/api/wholesale";
import { column } from "./config";
import { saveAs } from "file-saver";
import { blobValidate } from "@/utils/ruoyi";
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import ReturnOrderDialog from "../../components/returnOrderDialog.vue";
import OrderToAfterSalesDialog from "../../components/orderToAfterSalesDialog.vue";
import handleApplication from "../../components/handleApplication.vue";
import OrderStyleBadge from '@/components/OrderStyleBadge'
import { SUB_STATUS } from "../../enum/index.js";
import {
  createFormatDateTimeMethod,
  createCopyTextMethod,
  createGetTimeStatusClassMethod,
} from "@/utils/wholesaleUtils";
import PriceChips from "@/views/demandManage/wholesale/components/priceChips.vue";

export default {
  name: "Consignment",
  components: {
    FilterPanel,
    BrandFilter,
    ReturnOrderDialog,
    OrderToAfterSalesDialog,
    SearchSection,
    handleApplication,
    PddDialog,
    EmptyState,
    PriceChips,
    OrderStyleBadge
  },
  data() {
    return {
      currentStatus: 12,
      SUB_STATUS: SUB_STATUS,
      tableHeight: 500,
      returnOrderDialog: false,
      orderToAfterSalesDialog: false,
      tableLoading: false,
      tableDataList: [],
      columnData: column,

      searchParams: {
        //table列表query入参
        pageNum: 1,
        pageSize: 30,
        brand: "",
        province: "",
        orderCode: "", // 内部单号
        originalOrderId: "", // 商家单号
        category: "", // 品类
        shopName: "", // 店铺名称
        productNameLike: "", // 商品名称
        skuNameLike: "", // SKU名称
        companyId: "", // 供应商
      },
      totalNum: 0,
      currentOrder: null,
      returnReason: 5, // 退货追单原因
    };
  },

  mounted() {
    this.getData();
  },
  methods: {
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

    /**
     * 获取时间状态样式类 - 使用公用工具函数
     * @param {Object} row - 行数据对象
     * @returns {string} CSS 类名
     */
    getTimeStatusClass: createGetTimeStatusClassMethod(),

    handleSizeChange(val) {
      this.searchParams.pageSize = val;
      this.searchParams.pageNum = 1;
      this.getData();
    },
    handleCurrentChange(val) {
      this.searchParams.pageNum = val;
      this.getData();
    },
    handleApply(row) {
      this.$refs.handleApplicationRef.open(row);
    },
    getSubStatus(status) {
      return this.SUB_STATUS[status] || "-";
    },
    // 格式化发货时效
    formatDeliveryTime(val) {
      const n = Number(val);
      if (!isFinite(n) || n < 0) return "-";
      const deliveryTimeMap = {
        0: "当日",
        1: "明天",
        2: "后天"
      };
      return deliveryTimeMap[n] || "-";
    },
    // 品牌筛选变化
    handleBrandChange(brandId) {
      this.searchParams.brand = brandId;
      this.getData();
    },
    // 收货地点筛选变化
    handleLocationChange(locationId) {
      this.searchParams.province = locationId;
      this.getData();
    },
    // 退货追单
    handleReturn(row) {
      this.currentOrder = [row];
      this.returnOrderDialog = true;
    },

    // 订单转售后
    handleOrderToAfterSales() {
      this.orderToAfterSalesDialog = true;
    },

    initTable() {
      this.searchParams.pageNum = 1;
      this.getData();
    },


    // 搜索处理
    handleSearch(searchData) {
      this.searchParams.orderCode = searchData.orderCode || "";
      this.searchParams.originalOrderId = searchData.originalOrderId || "";
      this.searchParams.category = searchData.category || "";
      this.searchParams.shopName = searchData.shopName || "";
      this.searchParams.productNameLike = searchData.productNameLike || "";
      this.searchParams.skuNameLike = searchData.skuNameLike || "";
      this.searchParams.companyId = searchData.companyId || "";
      this.searchParams.pageNum = 1;
      this.getData();
    },

    handleCompanyChange(val) {
      this.searchParams.pageNum = 1;
      this.searchParams.companyId = val;
      this.getData();
    },

    // 重置搜索条件
    handleReset() {
      (this.searchParams = {
        //table列表query入参
        pageNum: 1,
        pageSize: 30,
        brand: "",
        province: "",
        orderCode: "", // 内部单号
        originalOrderId: "", // 商家单号
        category: "", // 品类
        shopName: "", // 店铺名称
        productNameLike: "", // 商品名称
        skuNameLike: "", // SKU名称
        companyId: "", // 供应商
      }),
        this.getData();
    },

    // 获取列表数据
    async getData() {
      if (this.tableLoading) {
        this.$message.error("数据加载中，请勿操作！！！");
        return;
      }
      this.tableLoading = true;

       let pageData = {
        pageNum: this.searchParams.pageNum,
        pageSize: this.searchParams.pageSize,
      }
      let params = {
        province: this.searchParams.province,
        brand: this.searchParams.brand,
        orderCodeList: this.searchParams.orderCode?[this.searchParams.orderCode]:[], // 内部单号搜索
        originalOrderId: this.searchParams.originalOrderId, // 商家单号搜索
        category: this.searchParams.category, // 品类搜索
        shopName: this.searchParams.shopName, // 店铺名称搜索
        productNameLike: this.searchParams.productNameLike, // 商品名称搜索
        skuNameLike: this.searchParams.skuNameLike, // SKU名称搜索
        companyId: this.searchParams.companyId, // 供应商
        status: this.currentStatus,
      };
      let res = await getOrderSendListApi(pageData,params);
      const { code, rows = [], total = 0 } = res;
      if (code != 200) {
        return;
      }
      this.tableLoading = false;
      this.tableDataList = rows || [];
      this.totalNum = total || 0;
    },
  },
};
</script>
<style lang="scss" scoped>
@import "~@/assets/styles/warp.scss";
@import "@/assets/styles/common/order-components.scss";
@import "@/assets/styles/common/time-status.scss";
@import "./index.scss";
</style>
