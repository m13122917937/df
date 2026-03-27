<template>
  <div class="cancelled-orders orders-wrap">
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
          @search="handleSearch"
          @reset="handleReset"
        />

        <!-- 表格区域 -->
        <div class="table-section">
          <el-table
            ref="table"
            :data="tableData"
            v-loading="loading"
            stripe
            size="medium"
            center
            style="width: 100%; min-height: 400px"
            :fit="true"
            height="100%"
            :header-cell-style="{
              background: '#f7f8fa',
              color: '#606266',
              fontWeight: 600,
            }"
            :cell-style="{ padding: '8px 0' }"
          >
            <!-- 空数据状态 -->
            <template slot="empty">
              <EmptyState text="暂无撤销订单数据" />
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
                    <span class="pn-model" v-if="scope.row.productName">{{
                      scope.row.productName || "-"
                    }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span class="pn-model">{{ scope.row.skuName }}</span>
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

            <!-- 最晚发货时间 -->
            <el-table-column label="最晚发货时间" min-width="200" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.lastShippingTime }}</span>
              </template>
            </el-table-column>

            <!-- 最后一次修改时间 -->
            <el-table-column label="撤销时间" min-width="200" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.updateTime }}</span>
              </template>
            </el-table-column>

            <!-- 前置状态 -->
            <el-table-column label="前置状态" min-width="200" align="center">
              <template slot-scope="scope">
                <span>{{ getSubStatus(scope.row.subStatus) }}</span>
              </template>
            </el-table-column>

            <el-table-column label="撤销原因" min-width="200" prop="revokeType" align="center">
              <template slot-scope="scope">
                <span>{{ getRevokeReasonText(scope.row.revokeType) }}</span>
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
  </div>
</template>

<script>
import BrandFilter from "@/views/demandManage/wholesale/components/brandFilter.vue";
import FilterPanel from "@/views/demandManage/wholesale/components/filterPanel.vue";
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import OrderStyleBadge from '@/components/OrderStyleBadge'
import { getOrderListApi } from "@/api/wholesale";
import {
  createFormatDateTimeMethod,
  createCopyTextMethod,
} from "@/utils/wholesaleUtils";
import { SUB_STATUS } from "@/views/demandManage/wholesale/enum/index.js";

export default {
  name: "CancelledOrders",
  components: {
    BrandFilter,
    FilterPanel,
    SearchSection,
    EmptyState,
    OrderStyleBadge
  },
  data() {
    return {
      status: 11, // 已撤销订单状态
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
      SUB_STATUS: SUB_STATUS,
    };
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

    // 根据前置状态获取状态文本
    getSubStatus(status) {
      return this.SUB_STATUS[status] || "-";
    },

    // 品牌筛选变化
    handleBrandChange(brandId) {
      this.currentBrand = brandId;
      this.loadData();
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
          status: this.status, // 已撤销订单状态
          orderCodeList: this.orderCode?[this.orderCode] : [], // 内部单号搜索
          originalOrderId: this.originalOrderId, // 商家单号搜索
          category: this.category, // 品类搜索
          shopName: this.shopName, // 店铺名称搜索
          productNameLike: this.productNameLike, // 商品名称搜索
          skuNameLike: this.skuNameLike, // SKU名称搜索
          province: this.currentLocation,
        };

        const response = await await getOrderListApi(pageData,params);

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

    // 根据撤销类型获取撤销原因文本
    getRevokeReasonText(revokeType) {
      const revokeReasonMap = {
        0: "店铺后台急速退款",
        1: "顾客拒签/拒收",
        2: "派送未联系到顾客，退回",
        3: "24小时物流无揽收信息",
        4: "供应商私自拦截",
        5: "已经从其他渠道发货",
        6: "手动追单",
      };
      return revokeReasonMap[revokeType] || "-";
    },

  },
  mounted() {
    this.loadData();
  },
};
</script>

<style scoped lang="scss">
@import "@/assets/styles/common/order-components.scss";


/* 响应式设计 */
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
