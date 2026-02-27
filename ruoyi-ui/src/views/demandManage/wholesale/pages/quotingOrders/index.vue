<!-- 报价中-->
<template>
  <div class="wrap inTheQuotationCount">
    <div class="wrap_btn">
      <!-- 品牌筛选组件 -->
      <BrandFilter
        :status="3"
        :current-brand="searchParams.brand"
        :province-id="searchParams.province"
        @brand-change="handleBrandChange"
      />
    </div>
    <div class="body">
      <div class="filter-panels">
        <FilterPanel
          title="收货地点"
          :status="3"
          :current-value="searchParams.province"
          :current-brand="searchParams.brand"
          @change="handleLocationChange"
        />
      </div>
      <div class="tableList">
        <SearchSection
          :status="3"
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
          :show-time="true"
          :show-empty="false"
          @search="handleSearch"
          @reset="handleReset"
        >
          <template #toolbar>
            <div class="toolbar-content">
              <div class="toolbar-left">
                <!-- 可以添加其他工具栏项目 -->
              </div>
              <div class="toolbar-right">
                <el-popconfirm
                  title="确认撤销报价吗？"
                  @confirm="batchRevocation"
                >
                  <el-button
                    type="cancel"
                    :disabled="multipleSelection.length == 0"
                    :loading="revocationLoading"
                    slot="reference"
                    >撤销报价</el-button
                  >
                </el-popconfirm>
                <el-button
                  class="batch-order-btn"
                  type="primary"
                  plain
                @click="openBatchOrderDialog"
                  >批量内部单号搜索</el-button
                >
              <el-button
                class="batch-order-btn"
                type="success"
                plain
                @click="openBatchOriginalDialog"
                >批量商家单号搜索</el-button
              >
                <el-button
                  class="export-btn"
                  type="primary"
                  :loading="exportLoading"
                  @click="handleExport"
                  >导出</el-button
                >
              </div>
            </div>
          </template>
        </SearchSection>
        <!-- 订单表格 -->
        <div class="table-section">
          <el-table
            ref="multipleTable"
            stripe
            size="medium"
            center
            :fit="true"
            v-loading="tableLoading"
            v-table-height="{ bottomOffset: 100 }"
            :data="tableDataList"
            style="width: 100%"
            element-loading-text="数据加载中"
            :height="tableHeight"
            :min-width="180"
            :header-cell-style="{
              height: '48px',
              background: '#f7f8fa',
              color: '#606266',
              fontWeight: 600,
            }"
            @selection-change="handleSelectionChange"
          >
            <!-- 空数据状态 -->
            <template slot="empty">
              <EmptyState text="暂无报价中数据" />
            </template>
            <el-table-column type="selection" width="55"> </el-table-column>

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
                <div v-else-if="item.prop === 'subStatus'">
                  {{ getSubStatus(row.subStatus) }}
                </div>
                <div v-else-if="item.prop === 'price'">
                  <div class="price-list">
                      <span
                        v-if="row.priceHighest > 0"
                        class="price-item"
                        :class="{
                          'price-item-active': row.priceHighestStatus == 1
                        }"

                      >
                        ¥ {{ row.priceHighest }}<i class="el-icon-ticket" />
                      </span>
                      <div v-if="row.lastCompeteTime && row.priceHighestStatus == 1 && shouldShowCountdown(row, 'highest')" class="countdown-text">
                        <OptimizedCountdownText :data="row" @countdown-end="handleCountdownEnd" />
                      </div>
                      <span
                        v-if="row.priceHign > 0"
                        class="price-item"
                        :class="{
                          'price-item-active': row.priceHignStatus == 1
                        }"

                      >¥ {{ row.priceHign }}</span>
                      <div v-if="row.lastCompeteTime && row.priceHignStatus == 1 && shouldShowCountdown(row, 'hign')" class="countdown-text">
                        <OptimizedCountdownText :data="row" @countdown-end="handleCountdownEnd" />
                      </div>
                      <span
                        v-if="row.priceLow > 0"
                        class="price-item"
                        :class="{
                          'price-item-active': row.priceLowStatus == 1
                        }"

                      >¥ {{ row.priceLow }}</span>
                      <div v-if="row.lastCompeteTime && row.priceLowStatus == 1 && shouldShowCountdown(row, 'low')" class="countdown-text">
                        <OptimizedCountdownText :data="row" @countdown-end="handleCountdownEnd" />
                      </div>
                      <span
                        v-if="row.priceLowest > 0"
                        class="price-item last-price"

                      >¥ {{ row.priceLowest }}</span>
                    </div>
                </div>
                <div v-else-if="item.prop === 'accountingPeriod'">
                   <span>{{
                  formatAccountingPeriod(row.accountingPeriod)
                }}</span>
                </div>
                <div v-else-if="item.prop === 'deliveryTime'">
                  <span>{{ formatDeliveryTime(row.deliveryTime) }}</span>
                </div>
                <!-- 最晚发货时间 - 特殊颜色处理 -->
                <div v-else-if="item.prop === 'lastShippingTime'" :class="getTimeStatusClass(row)">
                  {{ row[item.prop] || "-" }}
                </div>
                <div v-else>
                  {{ row[item.prop] || "-" }}
                </div>
              </template>
            </el-table-column>

            <el-table-column fixed="right" width="230" align="left">
              <template slot="header">
                <div slot="reference">操作</div>
              </template>
              <template slot-scope="{ row }">
                <div class="operation-buttons">
                  <el-button
                    v-if=" row.priceHighestStatus != 1 && row.priceHignStatus != 1 && row.priceLowStatus != 1&& row.priceLowestStatus != 1"
                    type="primary"
                    size="mini"
                    @click="offerConfirm(row)"
                    >编辑报价</el-button
                  >
                  <el-button
                  v-if="
                        row.priceLowestStatus == 1 ||
                        row.priceLowStatus == 1 ||
                        row.priceHignStatus == 1 ||
                        row.priceHighestStatus == 1
                      "
                    type="success"
                    size="mini"
                    @click="handleReturn(row)"
                    >退货追单</el-button
                  >
                  <el-popconfirm
                    title="确认撤销此订单吗？"
                    @confirm="revocation(row)"
                  >
                    <!-- priceLowestStatus、priceLowStatus、priceHignStatus、priceHighestStatus -->
                    <el-button
                      v-if="
                        row.priceLowestStatus ==1 &&
                        row.priceLowStatus == 1 &&
                        row.priceHignStatus == 1 &&
                        row.priceHighestStatus == 1
                      "
                      slot="reference"
                      type="danger"
                      size="mini"
                      >撤销报价</el-button
                    >
                  </el-popconfirm>
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
            :current-page="searchParams.pageNum"
            :page-sizes="[30, 50, 100]"
            :page-size="searchParams.pageSize"
            :total="totalNum"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </div>
    <AddQuotation
      :visible.sync="procurementDialogVisible"
      :orderData="orderData"
      :isEdit="true"
      @confirm="handleProcurementConfirm"
    />
    <ReturnOrderDialog :visible.sync="returnOrderDialog" :current-order="currentOrder" :return-reason.sync="returnReason" @success="initTable" />
    <el-dialog
      title="批量内部单号搜索"
      :visible.sync="batchOrderDialogVisible"
      width="420px"
      @close="handleBatchDialogClose"
      :close-on-click-modal="false"
    :modal="true"
    :append-to-body="true"
    :z-index="3000"
    >
      <el-input
        type="textarea"
        :rows="6"
        v-model="batchOrderCodeInput"
        maxlength="2000"
        show-word-limit
        placeholder="请输入单号"
      />
      <div class="batch-order-tips">
        每行输入一条单号,不要出现，、。等符号。
      </div>
      <div slot="footer">
        <el-button @click="handleBatchDialogClose">取消</el-button>
        <el-button type="primary" @click="handleBatchSearch">查询</el-button>
      </div>
    </el-dialog>
      <el-dialog
        title="批量商家单号搜索"
        :visible.sync="batchOriginalDialogVisible"
        width="420px"
        @close="handleBatchOriginalClose"
        :close-on-click-modal="false"
        :modal="true"
        :append-to-body="true"
        :z-index="3000"
      >
        <el-input
          type="textarea"
          :rows="6"
          v-model="batchOriginalInput"
          maxlength="2000"
          show-word-limit
          placeholder="请输入商家单号"
        />
        <div class="batch-order-tips">
          每行输入一条商家单号,不要出现，、。等符号。
        </div>
        <div slot="footer">
          <el-button @click="handleBatchOriginalClose">取消</el-button>
          <el-button type="primary" @click="handleBatchOriginalSearch">查询</el-button>
        </div>
      </el-dialog>
  </div>
</template>
<script>
import BrandFilter from "../../components/brandFilter.vue";
import FilterPanel from "../../components/filterPanel.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import ReturnOrderDialog from "../../components/returnOrderDialog.vue";
import { apiGetTradingExport, apiChaseHanging } from "@/api/creatingOrders";
import { getOrderListApi }  from "@/api/wholesale";
import { column } from "./config";
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import OrderStyleBadge from '@/components/OrderStyleBadge';
import { SUB_STATUS } from "../../enum/index.js";
import { formatDate } from "@/utils"
import { saveAs } from "file-saver";
import { blobValidate } from "@/utils/ruoyi";

import {
  createFormatDateTimeMethod,
  createCopyTextMethod,
  createGetTimeStatusClassMethod,
} from "@/utils/wholesaleUtils";
import PriceChips from "@/views/demandManage/wholesale/components/priceChips.vue";
import AddQuotation from "../../components/AddQuotation.vue"
import OptimizedCountdownText from "../../components/OptimizedCountdownText.vue";
export default {
  name: "quotingOrders",
  components: {
    BrandFilter,
    FilterPanel,
    SearchSection,
    ReturnOrderDialog,
    EmptyState,
    PriceChips,
    AddQuotation,
    OptimizedCountdownText,
    OrderStyleBadge
  },

  data() {
    return {
      procurementDialogVisible: false,
      orderData:null,
      SUB_STATUS:SUB_STATUS,
      columnData: column,
      disabledLoading: false,
      tableLoading: false,
      tableDataList: [],
      addressList: [],
      totalNum: 0,
      tableHeight: 500,
      returnOrderDialog: false,
      currentOrder: [],
      revocationLoading: false,
      multipleSelection: [],
      returnReason: 5, // 退货追单原因
      exportLoading: false,
      batchOrderDialogVisible: false,
      batchOrderCodeInput: "",
      batchOrderCodeList: [],
      batchOriginalDialogVisible: false,
      batchOriginalInput: "",
      batchOriginalList: [],
      // 倒计时相关状态
      countdownRefreshTimer: null,
      countdownEndNotifications: new Set(), // 防止重复通知
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
        lastShippingTimeStart: "",
        lastShippingTimeEnd: "",
      },
    };
  },
  mounted() {
    this.getData();
    // this.startCountdownRefresh();
  },

  beforeDestroy() {
    this.stopCountdownRefresh();
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
    getSubStatus(status) {
      return this.SUB_STATUS[status] || "";
    },
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 批量撤销报价 不
    async batchRevocation() {
      this.revocationLoading = true;
      const codeArr = this.multipleSelection.map((val) => {
        return val.orderCode;
      });
      const params = {
        orderCode: codeArr,
      };
      let res = await apiChaseHanging(params);
      console.log("res", res);
      if (res.code == 200) {
        this.$message.success("撤销成功");
        setTimeout(() => {
          this.getData();
          this.revocationLoading = false;
        }, 500);
      } else {
        this.revocationLoading = false;
      }
    },

    async revocation(row) {
      if (this.disabledLoading) {
        this.$message.error("订单撤销中，请勿重复点击");
        return;
      }
      this.disabledLoading = true;
      const params = {
        orderCode: [row.orderCode],
      };
      let res= await apiChaseHanging(params);
      if (res.code == 200)  {
        this.$message.success("撤销成功");
        setTimeout(()=> {
          this.getData();
          this.disabledLoading = false;
        }, 500);
      } else {
        this.disabledLoading = false;
      }
    },

    initTable() {
      this.searchParams.pageNum = 1;
      this.getData();
    },

    handleReturn(row) {
      this.currentOrder = [row];
      this.returnOrderDialog = true;
    },
    // 品牌筛选变化
    handleBrandChange(brandId) {
      this.searchParams.brand = brandId;
      this.getData();
    },
    handleProcurementConfirm(formData) {
      this.getData()
      // 这里可以处理表单提交逻辑
      this.$message.success('配置保存成功')
    },
    // 收货地点筛选变化
    handleLocationChange(locationId) {
      this.searchParams.province = locationId;
      this.getData();
    },
    // 获取列表数据
    buildListRequestPayload() {
      const pageData = {
        pageNum: this.searchParams.pageNum,
        pageSize: this.searchParams.pageSize,
      };
      const orderCodeList =
        this.batchOrderCodeList.length > 0
          ? this.batchOrderCodeList
          : this.searchParams.orderCode
          ? [this.searchParams.orderCode]
          : [];
      const originalOrderIdList =
        this.batchOriginalList.length > 0
          ? this.batchOriginalList
          : this.searchParams.originalOrderId
          ? [this.searchParams.originalOrderId]
          : [];
      const params = {
        province: this.searchParams.province,
        brand: this.searchParams.brand,
        orderCodeList, // 内部单号搜索
        originalOrderId: this.searchParams.originalOrderId, // 商家单号搜索 (单个)
        originalOrderIdList, // 批量商家单号搜索
        category: this.searchParams.category, // 品类搜索
        shopName: this.searchParams.shopName, // 店铺名称搜索
        productNameLike: this.searchParams.productNameLike, // 商品名称搜索
        skuNameLike: this.searchParams.skuNameLike, // SKU名称搜索
        lastShippingTimeStart: this.searchParams.lastShippingTimeStart || undefined,
        lastShippingTimeEnd: this.searchParams.lastShippingTimeEnd || undefined,
        status: 3,
      };
      return {
        pageData,
        params,
      };
    },
    async getData() {
      if (this.tableLoading) {
        this.$message.error("数据加载中，请勿操作！！！");
        return;
      }
      this.tableLoading = true;

      const { pageData, params } = this.buildListRequestPayload();
      let res = await getOrderListApi(pageData, params);
      const { code, rows = [], total = 0 } = res;
      if (code != 200) {
        return;
      }
      this.tableLoading = false;
      this.tableDataList = rows || [];
      this.totalNum = total || 0;
    },

    //编辑报价
    offerConfirm(row) {
      this.orderData = row;
      this.procurementDialogVisible = true;
    },
    // 搜索处理
    handleSearch(searchData) {
      this.searchParams.orderCode = searchData.orderCode || "";
      this.searchParams.originalOrderId = searchData.originalOrderId || "";
      this.searchParams.category = searchData.category || "";
      this.searchParams.shopName = searchData.shopName || "";
      this.searchParams.productNameLike = searchData.productNameLike || "";
      this.searchParams.skuNameLike = searchData.skuNameLike || "";
      this.searchParams.lastShippingTimeStart = searchData.lastShippingTimeStart || "";
      this.searchParams.lastShippingTimeEnd = searchData.lastShippingTimeEnd || "";
      this.searchParams.pageNum = 1;
      this.getData();
    },
    // 重置搜索条件
    handleReset() {
      this.searchParams = {
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
      };
      this.batchOrderCodeList = [];
      this.batchOrderCodeInput = "";
      this.batchOriginalList = [];
      this.batchOriginalInput = "";
      // 清理最晚发货时间范围
      this.searchParams.lastShippingTimeStart = "";
      this.searchParams.lastShippingTimeEnd = "";
      this.getData();
    },

    // 格式化账期
    formatAccountingPeriod(val) {
      const n = Number(val);
      if (!isFinite(n) || n < 0) return "-";
      return n === 0 ? "当天" : `T+${n}`;
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
    async handleExport() {
      if (this.exportLoading) return;
      this.exportLoading = true;
      const { params } = this.buildListRequestPayload();
      const pageData = {
        pageNum: this.searchParams.pageNum,
        pageSize: this.searchParams.pageSize,
      };
      let date = formatDate(new Date(), "yyyyMMdd")
      apiGetTradingExport(params,pageData).then((res) => {
        const isBlob = blobValidate(res);
        if (isBlob) {
          const blob = new Blob([res]);
          saveAs(blob, `报价中订单_${date}.xlsx`);
          this.exportLoading = false;
        }
      }).catch(() => {
        this.exportLoading = false;
      })
    },
    openBatchOrderDialog() {
      this.batchOrderCodeInput = this.batchOrderCodeList.join("\n");
      this.batchOrderDialogVisible = true;
    },
    handleBatchDialogClose() {
      this.batchOrderDialogVisible = false;
      this.batchOrderCodeInput = "";
    },
    handleBatchSearch() {
      if (!this.batchOrderCodeInput.trim()) {
        this.$message.warning("请输入至少一个内部单号");
        return;
      }
      const orderCodes = this.batchOrderCodeInput
        .split(/\n+/)
        .map((code) => code.trim())
        .filter((code) => !!code);
      if (!orderCodes.length) {
        this.$message.warning("请输入有效的内部单号");
        return;
      }
      this.batchOrderCodeList = Array.from(new Set(orderCodes));
      this.searchParams.pageNum = 1;
      this.batchOrderDialogVisible = false;
      this.getData();
    },

    openBatchOriginalDialog() {
      this.batchOriginalInput = this.batchOriginalList.join("\n");
      this.batchOriginalDialogVisible = true;
    },
    handleBatchOriginalClose() {
      this.batchOriginalDialogVisible = false;
      this.batchOriginalInput = "";
    },
    handleBatchOriginalSearch() {
      if (!this.batchOriginalInput.trim()) {
        this.$message.warning("请输入至少一个商家单号");
        return;
      }
      const orderCodes = this.batchOriginalInput
        .split(/\n+/)
        .map((code) => code.trim())
        .filter((code) => !!code);
      if (!orderCodes.length) {
        this.$message.warning("请输入有效的商家单号");
        return;
      }
      this.batchOriginalList = Array.from(new Set(orderCodes));
      this.searchParams.pageNum = 1;
      this.batchOriginalDialogVisible = false;
      this.getData();
    },

    /**
     * 判断是否应该显示倒计时
     * @param {Object} row - 行数据
     * @param {String} priceType - 价格类型 ('highest', 'hign', 'low')
     * @returns {Boolean} 是否显示倒计时
     */

    shouldShowCountdown(row, priceType) {
      if (!row.lastCompeteTime) return false

      const now = new Date()
      const lastTime = new Date(row.lastCompeteTime)
      const diffMs = now.getTime() - lastTime.getTime()
      const quotationInterval = row.quotationInterval

      let size = 0
      if (priceType === 'highest' && row.priceHighestStatus === 1) {
        size = 3
      } else if (priceType === 'hign' && row.priceHignStatus === 1) {
        size = 2
      } else if (priceType === 'low' && row.priceLowStatus === 1) {
        size = 1
      } else if (priceType === 'lowest' && row.priceLowestStatus === 1) {
        size = 0
      }

      const quotationTime = 1000 * 60 * quotationInterval * size
      const remainingMs = quotationTime - diffMs

      // 只有剩余时间大于0且小于5分钟才显示倒计时
      return remainingMs > 0
    },
    /**
     * 处理倒计时结束事件
     * @param {Object} data - 倒计时结束的数据
     */
    handleCountdownEnd(data) {
      this.getData();
    },

    /**
     * 启动倒计时刷新定时器
     */
    startCountdownRefresh() {
      // 每30秒检查一次数据，确保倒计时准确性
      this.countdownRefreshTimer = setInterval(() => {
        this.refreshCountdownData();
      }, 30000);
    },

    /**
     * 停止倒计时刷新定时器
     */
    stopCountdownRefresh() {
      if (this.countdownRefreshTimer) {
        clearInterval(this.countdownRefreshTimer);
        this.countdownRefreshTimer = null;
      }
    },

    /**
     * 刷新倒计时数据
     */
    async refreshCountdownData() {
      try {
        // 静默刷新数据，不显示loading
        // const params = {
        //   ...this.searchParams,
        //   status: 3,
        // };
        const { pageData, params } = this.buildListRequestPayload();
        let res = await getOrderListApi(pageData, params);
        // const res = await apiGetTrading(params);
        if (res.code === 200) {
          // 只更新倒计时相关的数据，避免影响用户操作
          this.tableDataList = res.rows || [];
          this.totalNum = res.total || 0;
        }
      } catch (error) {
        console.warn('倒计时数据刷新失败:', error);
      }
    },
  },
};
</script>
<style lang="scss" scoped>
@import "~@/assets/styles/warp.scss";
@import "@/assets/styles/common/order-components.scss";
@import "@/assets/styles/common/time-status.scss";
@import "./index.scss";

.batch-order-btn {
  margin: 0 12px;
}

.batch-order-tips {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 18px;
}
</style>
