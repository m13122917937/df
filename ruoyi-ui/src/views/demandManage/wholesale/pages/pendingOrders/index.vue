<!-- 待发布 -->
<template>
  <div class="wrap">
    <div class="wrap_btn">
      <!-- 品牌筛选组件 -->
      <BrandFilter
        :status="2"
        :current-brand="searchParams.brand"
        :province-id="searchParams.province"
        @brand-change="handleBrandChange"
      />
    </div>
    <div class="body">
      <!-- 左侧省份筛选 -->
      <div class="filter-panels">
        <FilterPanel
          title="收货地点"
          :status="2"
          :current-value="searchParams.province"
          :current-brand="searchParams.brand"
          @change="handleLocationChange"
        />
      </div>
      <!-- 列表 -->
      <div class="tableList">
        <SearchSection
          :status="2"
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
                  icon="el-icon-download" 
                  @click="handleExport" 
                  :loading="exportLoading"
                  >导出</el-button>
                 <el-button
                  type="cancel"
                  @click="batchRevocation"
                  :disabled="multipleSelection.length == 0"
                  :loading="revocationLoading"
                  >撤销</el-button>
                 <el-button
                  class="batch-order-btn"
                  type="primary"
                  plain
                  @click="openBatchOrderDialog"
                  >批量内部单号搜索</el-button>
                <el-button
                  class="batch-order-btn"
                  type="success"
                  plain
                  @click="openBatchOriginalDialog"
                  >批量商家单号搜索</el-button>
                 <el-button
                  class="push-order-btn"
                  type="primary"
                  plain
                  :disabled="multipleSelection.length === 0"
                  @click="openPushOrderDialog"
                  >定向推单</el-button>
              </div>
            </div>
          </template>
        </SearchSection>
        <!-- 订单表格 -->
        <div class="table-section">
          <el-table
            :key="tableFilterKey"
            ref="multipleTable"
            v-loading="tableLoading"
            :fit="true"
            stripe
            center
            size="medium"
            :data="filteredTableData"
            style="width: 100%"
            @selection-change="handleSelectionChange"
            height="100%"
            element-loading-text="数据加载中"
          >
            <!-- 空数据状态 -->
            <template slot="empty">
              <EmptyState text="暂无待发布数据" />
            </template>
            <el-table-column type="selection" width="55"> </el-table-column>
            <el-table-column
              v-for="(item, index) in columnData"
              :key="index"
              :minWidth="item.minWidth"
              :prop="item.prop"
              :fixed="item.fixed || null"
              :align="item.align || 'center'"
            >
              <!-- 表头 -->
              <template #header>
                <FilterHeader v-if="isFilterable(item.prop)" :label="item.label" :value="columnSearch[getFilterProp(item.prop)] || []" :options="colFilterOptions[getFilterProp(item.prop)] || []" @update:value="columnSearch[getFilterProp(item.prop)] = $event" />
                <span v-else>{{ item.label }}</span>
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
                    <span class="pn-model" v-if="row.skuName">{{
                      row.productName || "-"
                    }}</span>
                  </div>
                  <div class="order-sku-line">
                    <!-- <i
                      v-if="row.productName || row.skuName"
                      class="el-icon-copy-document copy-icon"
                      @click="
                        copyText(`${row.productName}${row.skuName}`)
                      "
                    ></i> -->
                    {{
                      row.skuName
                    }}
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
                  {{ getSubStatus(row[item.prop]) }}
                </div>
                <div v-else-if="item.prop === 'price'">
                 <PriceChips :row="row" />
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
            <el-table-column fixed="right" width="230">
              <template slot="header">
                <div slot="reference">操作</div>
              </template>
              <template slot-scope="{ row }">
                <div>
                  <el-button
                    type="primary"
                    size="mini"
                    @click="offerConfirm(row)"
                    >编辑报价</el-button
                  >
                  <el-button
                    type="danger"
                    size="mini"
                    @click="handleReturn(row)"
                    >撤销</el-button
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
            :current-page="searchParams.pageNum"
            :page-sizes="[30, 50, 100]"
            :page-size="searchParams.pageSize"
            :total="totalNum"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </div>
    <ReturnOrderDialog
      :visible.sync="returnOrderDialog"
      :current-order="currentOrder"
      :return-reason.sync="returnReason"
      @success="initTable"
    >
    </ReturnOrderDialog>
    <AddQuotation
      :visible.sync="procurementDialogVisible"
      :orderData="orderData"
      :isEdit="true"
      @confirm="handleProcurementConfirm"
    />
    <PushOrderDialog
      :visible.sync="pushOrderDialogVisible"
      :order-code-list="pushOrderOrderCodes"
      @confirm="handlePushOrderConfirm"
      @close="handlePushOrderClose"
    />
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
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import BrandFilter from "../../components/brandFilter.vue";
import FilterPanel from "../../components/filterPanel.vue";
import { apiGetListWait, apiOrderWaitPush, exportCreatingOrdersApi } from "@/api/creatingOrders";
import { getOrderListApi }  from "@/api/wholesale";
import ReturnOrderDialog from "../../components/returnOrderDialog.vue";
import { column } from "./config";
import { SUB_STATUS } from "../../enum/index.js";
import AddQuotation from "../../components/AddQuotation.vue";
import PushOrderDialog from "../../components/pushOrder.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import PriceChips from "@/views/demandManage/wholesale/components/priceChips.vue";
import OrderStyleBadge from '@/components/OrderStyleBadge';
import { saveAs } from "file-saver";
import { blobValidate } from "@/utils/ruoyi";
import { formatDate } from "@/utils";

import {
  createFormatDateTimeMethod,
  createCopyTextMethod,
  createGetTimeStatusClassMethod,
} from "@/utils/wholesaleUtils";
import tableFilterMixin from "@/mixins/tableFilter";

export default {
  name: "TobereLeased",
  mixins: [tableFilterMixin],
  components: {
    PriceChips,
    AddQuotation,
    PushOrderDialog,
    BrandFilter,
    FilterPanel,
    SearchSection,
    ReturnOrderDialog,
    EmptyState,
    OrderStyleBadge
  },

  data() {
    return {
      procurementDialogVisible: false,
      pushOrderDialogVisible: false,
      pushOrderOrderCodes: [],
      pushOrderSubmitting: false,
      SUB_STATUS: SUB_STATUS,
      columnData: column,
      tableFilterKey: 0,
      tableLoading: false,
      returnOrderDialog: false,
      revocationLoading: false,
      exportLoading: false,
      tableDataList: [],
      totalNum: 0,
      currentOrder: null,
      returnReason: 5, // 退货追单原因
      multipleSelection: [],
      orderData: null,
      batchOrderDialogVisible: false,
      batchOrderCodeInput: "",
      batchOrderCodeList: [],
      batchOriginalDialogVisible: false,
      batchOriginalInput: "",
      batchOriginalList: [],
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
        erpOrderId: "",
      },
    };
  },

  mounted() {
    this.initColumnSearch(['orderStyle', 'subStatus'], {
      orderStyle: { display: row => ({ 0: '百补', 1: '百亿微派', 2: '国补' })[row.orderStyle] ?? '-' },
      platformShop: { display: row => `${row.platform} - ${row.shopName}` },
      brandCategory: { display: row => `${row.brand} - ${row.category}` },
      productSku: { display: row => `${row.productName} - ${row.skuName}` },
      addressDisplay: { display: row => `${row.provinceName || ''} ${row.cityName || ''}`.trim() },
    });
    this.getData();
  },

  methods: {
    isFilterable(prop) {
      const noFilterProps = ['orderNumbers', 'quantity', 'price', 'lastShippingTime', 'erpTradeTime', 'deliveryTime', 'lastCompeteTime', 'tradeCompanyName']
      return !noFilterProps.includes(prop)
    },
    getFilterProp(prop) {
      const propMap = {
        platform: 'platformShop',
        brand: 'brandCategory',
        sku: 'productSku',
        address: 'addressDisplay',
      }
      return propMap[prop] || prop
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
    offerConfirm(row) {
      this.orderData = row;
      this.procurementDialogVisible = true;
    },
    handleProcurementConfirm(formData) {
      this.getData();
      // 这里可以处理表单提交逻辑
      this.$message.success("配置保存成功");
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
    openPushOrderDialog() {
      if (!this.multipleSelection.length) {
        this.$message.warning("请选择至少一条订单");
        return;
      }
      const codes = this.multipleSelection
        .map((item) => item.orderCode)
        .filter((code) => !!code);
      if (!codes.length) {
        this.$message.warning("所选订单缺少内部单号，无法定向推单");
        return;
      }
      // 检查SKU是否相同
      const skuNames = this.multipleSelection
        .map((item) => item.skuName)
        .filter((sku) => !!sku);
      if (skuNames.length > 0) {
        const uniqueSkus = [...new Set(skuNames)];
        if (uniqueSkus.length > 1) {
          this.$message.warning("不支持不同SKU的订单定向推单，请选择相同SKU的订单");
          return;
        }
      }
      this.pushOrderOrderCodes = Array.from(new Set(codes));
      this.pushOrderDialogVisible = true;
    },
    async handlePushOrderConfirm(formData) {
      if (this.pushOrderSubmitting) {
        return;
      }
      const orderCodeList = formData.orderCodeList || this.pushOrderOrderCodes;
      if (!orderCodeList || !orderCodeList.length) {
        this.$message.warning("请选择需要推送的订单");
        return;
      }
      this.pushOrderSubmitting = true;
      try {
        const res = await apiOrderWaitPush({
          ...formData,
          orderCodeList,
        });
        this.$message.success("定向推单成功");
        this.handlePushOrderClose();
        // 推单成功后刷新列表
        this.getData();
      } catch (error) {
        console.error(error);
        this.$message.error("定向推单失败，请稍后重试");
      } finally {
        this.pushOrderSubmitting = false;
      }
    },
    handlePushOrderClose() {
      this.pushOrderDialogVisible = false;
      this.pushOrderOrderCodes = [];
    },
    initTable() {
      this.searchParams.pageNum = 1;
      this.getData();
    },
    handleReturn(row) {
      this.currentOrder = [row];
      this.returnOrderDialog = true;
    },
    openComplAints(data) {
      this.$refs.complAints.bnormalDialogFn(data);
    },
    // 品牌筛选变化
    handleBrandChange(brandId) {
      this.searchParams.brand = brandId;
      this.getData();
    },
    async batchRevocation() {
      this.currentOrder = this.multipleSelection;
      this.returnOrderDialog = true;
    },
    // 收货地点筛选变化
    handleLocationChange(locationId) {
      this.searchParams.province = locationId;
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
      
      let params = {
        province: this.searchParams.province,
        brand: this.searchParams.brand,
        orderCodeList, // 内部单号搜索
        originalOrderIdList, // 商家单号搜索
        category: this.searchParams.category, // 品类搜索
        shopName: this.searchParams.shopName, // 店铺名称搜索
        productNameLike: this.searchParams.productNameLike, // 商品名称搜索
        skuNameLike: this.searchParams.skuNameLike, // SKU名称搜索
        status: 2, // 新建采购订单状态
      };
      // 构建API参数
      // const params = {
      //   ...this.searchParams,
      //   status: 2, // 待发布订单状态
      // };

      let res = await getOrderListApi(pageData,params);
      const { code, rows = [], total = 0 } = res;
      if (code != 200) {
        return;
      }
      this.tableLoading = false;
      this.tableDataList = rows || [];
      this.tableFilterKey++;
      this.totalNum = total || 0;
    },
    offerConfirm(row) {
      this.orderData = row;
      this.procurementDialogVisible = true;
    },
    cancelClick(row) {},
    // 搜索处理
    handleSearch(searchData) {
      this.searchParams.orderCode = searchData.orderCode || "";
      this.searchParams.originalOrderId = searchData.originalOrderId || "";
      this.searchParams.category = searchData.category || "";
      this.searchParams.shopName = searchData.shopName || "";
      this.searchParams.productNameLike = searchData.productNameLike || "";
      this.searchParams.skuNameLike = searchData.skuNameLike || "";
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
        erpOrderId: "",
      };
      this.batchOrderCodeList = [];
      this.batchOrderCodeInput = "";
      this.batchOriginalList = [];
      this.batchOriginalInput = "";
      this.getData();
    },
    // 撤销取消
    handleCancel(row) {
      let refObj = this.$refs[`popover${row.id}`];
      refObj.doClose();
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
    async handleExport() {
      if (this.exportLoading) return;
      
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

      let params = {
        pageNum: this.searchParams.pageNum,
        pageSize: this.searchParams.pageSize,
        province: this.searchParams.province,
        brand: this.searchParams.brand,
        orderCodeList, // 内部单号搜索
        originalOrderIdList, // 商家单号搜索
        category: this.searchParams.category, // 品类搜索
        shopName: this.searchParams.shopName, // 店铺名称搜索
        productNameLike: this.searchParams.productNameLike, // 商品名称搜索
        skuNameLike: this.searchParams.skuNameLike, // SKU名称搜索
        status: 2, // 待发布订单状态
      };
      
      this.exportLoading = true;
      try {
        const res = await exportCreatingOrdersApi(params);
        const isBlob = blobValidate(res);
        if (isBlob) {
          const blob = new Blob([res]);
          const supplierName = this.selectedSupplierName || '待发布订单';
          const now = new Date();
          const timestamp =
            now.getFullYear() +
            String(now.getMonth() + 1).padStart(2, '0') +
            String(now.getDate()).padStart(2, '0') +
            '_' +
            String(now.getHours()).padStart(2, '0') +
            String(now.getMinutes()).padStart(2, '0')
          const fileName = `${supplierName}_${timestamp}.xlsx`;
          saveAs(blob, fileName);
          this.$message.success('导出成功');
        } else {
          const reader = new FileReader();
          reader.onload = (e) => {
            try {
              const result = JSON.parse(e.target.result || '{}');
              this.$message.error(result.msg || '导出失败');
            } catch (error) {
              this.$message.error('导出失败');
            }
          };
          reader.readAsText(res);
        }
      } catch (error) {
        console.error('导出待发布订单失败', error);
        this.$message.error('导出失败，请稍后重试');
      } finally {
        this.exportLoading = false;
      }
    },
  },
};
</script>
<style lang="scss" scoped>
@import "~@/assets/styles/variables.scss";
@import "~@/assets/styles/warp.scss";
@import "@/assets/styles/common/order-components.scss";
@import "@/assets/styles/common/time-status.scss";
@import "./index.scss";

.batch-order-btn {
  margin-left: 12px;
}

.push-order-btn {
  margin-left: 12px;
}

.batch-order-tips {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 18px;
}
</style>
