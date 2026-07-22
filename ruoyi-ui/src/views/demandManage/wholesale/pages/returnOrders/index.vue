<template>
  <div class="return-orders orders-wrap">
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
        >
          <template #toolbar>
            <div class="toolbar-content">
              <div class="toolbar-left">
                <!-- 可以添加其他工具栏项目 -->
              </div>
              <div class="toolbar-right">
                <el-button
                  type="success"
                  icon="el-icon-plus"
                  @click="handleAddReturn"
                  >添加退货</el-button
                >
              </div>
            </div>
          </template>
        </SearchSection>

        <!-- 表格区域 -->
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
              <EmptyState text="暂无退货订单数据" />
            </template>
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
             <!-- 最晚发货时间 -->
             <el-table-column
              label="订单状态"
              prop="sendTime"
              min-width="200"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ getSubstatus(scope.row.subStatus) }}</span>
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
                <span>{{ formatDateTime(scope.row.sendTime) }}</span>
              </template>
            </el-table-column>

            <!-- 平台 -->
            <!-- <el-table-column label="平台/店铺" prop="platform" min-width="200" align="center">
              <template slot-scope="scope">
                <div>{{ scope.row.platform || "-" }}</div>
                <div>{{ scope.row.shopName || "-" }}</div>
              </template>
            </el-table-column> -->

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
              :show-overflow-tooltip="true"
              align="center"
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
              prop="accountingPeriod"
              min-width="130"
              align="center"
            >
              <template slot="header">
                <FilterHeader label="账期" :value="columnSearch.accountingPeriod || []" :options="colFilterOptions.accountingPeriod || []" @update:value="columnSearch.accountingPeriod = $event" />
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

            <!-- 操作 -->
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template slot-scope="scope">
                <el-button
                  type="success"
                  size="mini"
                  @click="handleReturn(scope.row)"
                  >转正常</el-button
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

    <!-- 添加退货追单弹框组件 -->
    <AddReturnDialog
      :visible.sync="addReturnDialogVisible"
      @success="handleAddReturnSuccess"
    />
  </div>
</template>

<script>
import BrandFilter from "@/views/demandManage/wholesale/components/brandFilter.vue";
import FilterPanel from "@/views/demandManage/wholesale/components/filterPanel.vue";
import SearchSection from "@/views/demandManage/wholesale/components/searchSection.vue";
import AddReturnDialog from "./components/addReturnDialog.vue";
import PriceChips from "@/views/demandManage/wholesale/components/priceChips.vue";
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import OrderStyleBadge from '@/components/OrderStyleBadge';
import tableFilterMixin from "@/mixins/tableFilter";
import { getOrderSendListApi,changeNormalApi } from "@/api/wholesale";
import {
  createFormatDateTimeMethod,
  createCopyTextMethod,
  formatPrice,
  formatNumber,
  formatAccountingPeriod,
} from "@/utils/wholesaleUtils";

export default {
  name: "ReturnOrders",
  mixins: [tableFilterMixin],
  components: {
    BrandFilter,
    FilterPanel,
    SearchSection,
    AddReturnDialog,
    PriceChips,
    EmptyState,
    OrderStyleBadge
  },
  data() {
    return {
      status: 7, // 追单状态
      loading: false,
      currentBrand: "",
      currentLocation: "",
      // 搜索相关数据
      orderCode: "", // 内部单号
      originalOrderId: "", // 商家单号
      category: "", // 品类
      shopName: "", // 店铺名称
      productNameLike: "", // 商品名称
      skuNameLike: "", // SKU名称
      companyId: '', // 供应商
      // 表格数据
      tableFilterKey: 0,
      tableData: [],
      // 分页信息
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0,
      },
      // 选中的行
      selectedRows: [],
      // 添加退货追单弹框
      addReturnDialogVisible: false,
    };
  },

  methods: {
    getSubstatus(value){
      const obj = {
        71:"未发货待确认",
        72:"已发货待确认",
        73:"未发货已确认",
        74:"已发货已确认",
      };
      return obj[value] || "-";
    },
    // 格式化方法
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

    // 品牌筛选
    handleBrandChange(brandId) {
      this.currentBrand = brandId;
      this.loadData();
    },

    // 地点筛选
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
          orderCodeList: this.orderCode?[this.orderCode] : [], // 内部单号搜索
          originalOrderId: this.originalOrderId, // 商家单号搜索
          category: this.category, // 品类搜索
          shopName: this.shopName, // 店铺名称搜索
          productNameLike: this.productNameLike, // 商品名称搜索
          skuNameLike: this.skuNameLike, // SKU名称搜索
          companyId: this.companyId,
          status: this.status, // 退货订单状态
          province: this.currentLocation,
        };

        const response = await getOrderSendListApi(pageData,params);

        if (response.code === 200) {
          // 根据transitOrders页面的实现，使用res.rows和res.total
          this.tableData = response.rows || [];
          this.tableFilterKey++;
          this.pagination.total = response.total || 0;
        } else {
          this.$message.error(response.msg || "获取数据失败");
        }
      } catch (error) {
        console.error("加载数据失败:", error);
      } finally {
        this.loading = false;
      }
    },

    // 转正常
    handleReturn(row) {
      this.$confirm("确认将此订单转为正常？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        const params = {
          orderCode: row.orderCode,
        };
        const res = await changeNormalApi(params);
        if (res.code == 200) {
          this.$message.success("订单已转为正常");
          // 这里可以调用API更新订单状态
          this.loadData();
        } else {
          this.$message.error(res.msg || "订单转为正常失败");
        }
      });
    },

    // 分页
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.loadData();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.loadData();
    },

    // 添加退货追单
    handleAddReturn() {
      this.addReturnDialogVisible = true;
    },

    // 添加退货追单弹框成功回调
    handleAddReturnSuccess() {
      this.pagination.currentPage = 1;
      this.loadData();
    },
    
    handleCompanyChange(val) {
      this.pagination.currentPage = 1;
      this.companyId = val;
      this.loadData();
    },
  },
  mounted() {
    this.initColumnSearch(
      ['orderStyle', 'accountingPeriod'],
      {
        orderStyle: { display: row => ({ 0: '百补', 1: '百亿微派', 2: '国补' })[row.orderStyle] ?? '-' },
        brandCategory: { display: row => `${row.brand} - ${row.category}` },
        productSku: { display: row => `${row.productName} - ${row.skuName}` },
        addressDisplay: { display: row => `${row.provinceName || ''} ${row.cityName || ''}`.trim() },
        supplierDisplay: { display: row => row.tradeNickName || '-' },
      }
    );
    this.loadData();
  },
};
</script>

<style scoped lang="scss">
@import "@/assets/styles/common/order-components.scss";

/* 订单来源图标样式 */
.order-source i {
  color: #409eff;
}

/* 复制图标特殊样式 */
.copy-icon {
  margin-left: 8px;
}

/* 订单编号样式 */
.order-code,
.original-order-id,
.erp-order-id {
  font-size: 12px;
  color: #606266;
}

/* Element UI 标签样式 */
:deep(.el-tag--warning) {
  background-color: #fdf6ec;
  border-color: #f5dab1;
  color: #e6a23c;
}

:deep(.el-tag--info) {
  background-color: #f4f4f5;
  border-color: #d3d4d6;
  color: #909399;
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
