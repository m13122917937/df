<template>
  <div class="confirmed-orders orders-wrap">
    <!-- 品牌筛选 -->
    <BrandFilter
      :status="status"
      :current-brand="currentBrand"
      :province-id="currentLocation"
      @brand-change="handleBrandChange"
    />

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧筛选面板 -->
      <div class="filter-panels">
        <FilterPanel
          title="收货地点"
          :status="status"
          :current-value="currentLocation"
          :current-brand="currentBrand"
          @change="handleLocationChange"
        />
      </div>

      <!-- 右侧内容区域 -->
      <div class="content-area">
        <!-- 搜索区域 -->
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
            ref="table"
            :data="tableData"
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
              <EmptyState text="暂无已确认订单数据" />
            </template>
            <el-table-column prop="orderStyle" label="订单类型" width="90" fixed="left" align="center">
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
                     {{
                      scope.row.orderCode || "-"
                    }}
                    <i
                      v-if="scope.row.orderCode"
                      class="el-icon-copy-document copy-icon"
                      @click="copyText(scope.row.orderCode)"
                    ></i>
                  </div>
                  <div class="order-number-item"> 商家单号:
                    {{ scope.row.originalOrderId || "-" }}
                    <i
                      v-if="scope.row.originalOrderId"
                      class="el-icon-copy-document copy-icon"
                      @click="copyText(scope.row.originalOrderId)"
                    ></i>
                  </div>
                </div>
              </template>
            </el-table-column>

            <!-- 最晚发货时间 -->
            <el-table-column
              label="发货时间"
              prop="sendTime"
              min-width="200"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.sendTime }}</span>
              </template>
            </el-table-column>

            <!-- 物流信息 -->
             <el-table-column prop="trackingCompany" label="物流信息" width="240" align="center">
              <template slot-scope="scope">
                <TrackingInfo
                  :company="scope.row.trackingCompany"
                  :number="scope.row.trackingNumber"
                  :data="scope.row"
                  @click="openLogisticsDialog(scope.row)"
                />
              </template>
            </el-table-column>

            <!-- 平台 -->
            <el-table-column label="平台/店铺" prop="platform" min-width="200" align="center">
              <template slot-scope="scope">
                <div>{{ scope.row.platform || "-" }}</div>
                <div>{{ scope.row.shopName || "-" }}</div>
              </template>
            </el-table-column>

            <!-- 品牌 -->
            <el-table-column label="品牌/品类" prop="brand" min-width="200" align="center">
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
              :show-overflow-tooltip="true"
              align="center"
            >
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
              prop="accountingPeriod"
              min-width="130"
              align="center"
            >
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
              align="center"
              :show-overflow-tooltip="true"
            >
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

            <!-- 操作 -->
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template slot-scope="scope">
                <!-- <el-button
                  type="success"
                  size="mini"
                  @click="handleConfirmReceipt(scope.row)"
                  >确认收货</el-button
                > -->
                <el-button
                    type="warning"
                    size="small"
                    @click="openImeiDialog(scope.row)"
                    >查看串码</el-button
                  >
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
     <!-- 串码弹窗组件 -->
    <ImeiDialog
      :visible.sync="imeiDialogVisible"
      :current-order="currentOrder"
      title="串码信息"
    />
    <!-- 物流信息弹框组件 -->
    <LogisticsDialog
      :visible.sync="logisticsDialogVisible"
      :current-order="currentOrder"
    />
  </div>
</template>

<script>
import BrandFilter from "@/views/demandManage/wholesale/components/brandFilter.vue";
import FilterPanel from "@/views/demandManage/wholesale/components/filterPanel.vue";
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import PriceChips from "@/views/demandManage/wholesale/components/priceChips.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import ImeiDialog from "@/views/demandManage/wholesale/components/imeiDialog.vue";
import LogisticsDialog from "@/views/demandManage/wholesale/components/logisticsDialog.vue";
import TrackingInfo from "@/views/demandManage/wholesale/components/trackingInfo.vue";
import OrderStyleBadge from '@/components/OrderStyleBadge'
import { getOrderSendListApi, confirmEndingOrderApi } from "@/api/wholesale";
import {
  createFormatDateTimeMethod,
  createCopyTextMethod,
  formatPrice,
  formatNumber,
  formatAccountingPeriod,
} from "@/utils/wholesaleUtils";

export default {
  name: "ConfirmedOrders",
  components: {
    BrandFilter,
    FilterPanel,
    SearchSection,
    PriceChips,
    EmptyState,
    ImeiDialog,
    LogisticsDialog,
    TrackingInfo,
    OrderStyleBadge
  },
  data() {
    return {
      status: 10, // 待确认订单状态
      loading: false,
      currentBrand: "",
      currentLocation: "",
      // 表格数据
      tableData: [],
      // 分页信息
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0,
      },
      // 选中的行
      selectedRows: [],
      // 搜索相关数据
      orderCode: "", // 内部单号
      originalOrderId: "", // 商家单号
      category: "", // 品类
      shopName: "", // 店铺名称
      productNameLike: "", // 商品名称
      skuNameLike: "", // SKU名称
      companyId: '', // 供应商
       // 串码弹窗
      imeiDialogVisible: false,
      // 物流弹窗
      logisticsDialogVisible: false,
      currentOrder: null, 
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    formatPrice,
    formatNumber,
    formatAccountingPeriod,
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

    init() {
      this.pagination.currentPage = 1;
      this.pagination.pageSize = 30;
      this.pagination.total = 0;
      this.loadData();
    },
    // 品牌筛选变化
    handleBrandChange(brandId) {
      this.currentBrand = brandId;
      this.init();
    },

    // 收货地点筛选变化
    handleLocationChange(locationId) {
      this.currentLocation = locationId;
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
      this.companyId = resetData.companyId || "";
      this.pagination.currentPage = 1;
      this.loadData();
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
          brand: this.currentBrand,
          status: this.status, // 已确认订单状态
          orderCodeList: this.orderCode?[this.orderCode] : [], // 内部单号搜索
          originalOrderId: this.originalOrderId, // 商家单号搜索
          category: this.category, // 品类搜索
          shopName: this.shopName, // 店铺名称搜索
          productNameLike: this.productNameLike, // 商品名称搜索
          skuNameLike: this.skuNameLike, // SKU名称搜索
          companyId: this.companyId,
          province: this.currentLocation,
        };

        const response = await getOrderSendListApi(pageData,params);

        if (response && response.code === 200) {
          this.tableData = response.rows || [];
          this.pagination.total = response.total || 0;
        } else {
          this.$message.error(response?.msg || "获取数据失败");
          this.tableData = [];
          this.pagination.total = 0;
        }
      } catch (error) {
        console.error("加载数据失败:", error);
        this.tableData = [];
        this.pagination.total = 0;
      } finally {
        this.loading = false;
      }
    },

    // 确认收货
    handleConfirmReceipt(row) {
      this.$confirm("确认要确认收货吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          // 这里应该调用确认收货API
          const params = {
            orderCode: row.orderCode,
          };
          const response = await confirmEndingOrderApi(params);
          if (response && response.code === 200) {
            this.$message.success("确认收货成功");
            this.init(); // 刷新列表
          } else {
            this.$message.error(response?.msg || "确认收货失败");
          }
        })
        .catch((err) => {
          console.log("已取消确认收货", err);
        });
    },

    // 打开串码弹窗
    openImeiDialog(row) {
      this.currentOrder = row;
      this.imeiDialogVisible = true;
    },

    // 打开物流弹框
    openLogisticsDialog(row) {
      this.currentOrder = row;
      this.logisticsDialogVisible = true;
    },

    // 分页大小变化
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.loadData();
    },

    // 当前页变化
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadData();
    },
    
    handleCompanyChange(val) {
      this.pagination.currentPage = 1;
      this.companyId = val;
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
@media (max-width: 1200px) {
  .filter-panels {
    width: 180px;
  }
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
    height: auto;
  }

  .filter-panels {
    width: 100%;
    margin-bottom: 12px;
  }
}
</style>
