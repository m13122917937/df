<!--等待拣货-->
<template>
  <div class="order-detail today-shipped-order">
    <!-- 订单内容布局 -->
    <div class="order-content-layout">
      <div class="order-table-container-wrapper">
        <!-- 品牌筛选 -->
        <BrandFilter
          ref="brandFilter"
          :status="currentStatus"
          :current-brand="selectedBrand"
          :province-id="selectedRegion"
          @change="handleBrandChange"
        />
        <OrderSearch
          :value="searchForm"
          @search="handleSearch"
          @reset="handleReset"
        >
        </OrderSearch>
        <!-- 订单表格 -->
        <div class="order-table-container table-section">
          <el-table
            ref="table"
            v-loading="loading"
            :data="orderList"
            stripe
            size="medium"
            center
            style="width: 100%"
            :fit="true"
            height="100%"
            header-cell-class-name="table-header-cell"
            :cell-style="{ padding: '8px 0' }"
          >
            <!-- 空数据状态 -->
            <template slot="empty">
              <EmptyState text="暂无等待捡货订单数据" />
            </template>
            <el-table-column
              label="订单编号"
              prop="orderCode"
              min-width="200"
              fixed="left"
            >
              <template slot-scope="scope">
                <div class="order-numbers">
                  <div class="order-number-item">
                    {{ scope.row.orderCode || "-" }}
                    <i
                      v-if="scope.row.orderCode"
                      class="el-icon-copy-document copy-icon"
                      @click="copyText(scope.row.orderCode)"
                    ></i>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="tradeCompanyName"
              label="供应商"
              width="300"
              align="center"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                {{ scope.row.tradeCompanyName || "-" }}
              </template>
            </el-table-column>
             <el-table-column
              label="采购人/成交时间"
              prop="createTime"
              min-width="200"
            >
              <template slot-scope="scope">
                <div class="order-numbers">
                  <div class="order-number-item">
                    {{ scope.row.createBy || "-" }}
                  </div>
                  <div class="order-number-item">
                    {{ scope.row.createTime || "-" }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="brand"
              label="品牌/品类"
              width="120"
              align="center"
            >
              <template slot-scope="scope">
                <div class="order-productName">
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{
                      scope.row.brand || "-"
                    }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span v-if="scope.row.category" class="pn-model">{{
                      scope.row.category
                    }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              label="产品型号"
              prop="productName"
              min-width="200"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot-scope="scope">
                <div class="order-productName">
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{
                      scope.row.productName || "-"
                    }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{
                      scope.row.skuName
                    }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="quantity"
              label="入库数量/成交数量"
              width="180"
              align="center"
            >
              <template slot-scope="scope">
                {{ scope.row.warehouseQuantity || 0 }} /
                {{ scope.row.quantity || 0 }}
              </template>
            </el-table-column>
            <el-table-column
              label="成交价"
              prop="tradePrice"
              min-width="120"
              align="center"
            />
            <el-table-column
              prop="deliveryCode"
              label="送货码"
              width="120"
              align="center"
            >
              <template slot-scope="scope">
                {{ scope.row.deliveryCode || "-" }}
              </template>
            </el-table-column>
            <el-table-column
              prop="remark"
              label="备注"
              width="120"
              align="center"
            >
              <template slot-scope="scope">
                {{ scope.row.remark || "-" }}
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              label="操作"
              width="220"
              fixed="right"
              align="center"
            >
              <template slot-scope="scope">
                <div class="operation-buttons">
                  <el-button
                    size="mini"
                    type="primary"
                    @click="openPickDialog(scope.row)"
                    >拣货</el-button
                  >
                  <el-button
                    size="mini"
                    type="success"
                    @click="handleCompletePick(scope.row)"
                    >完成拣货</el-button
                  >
                </div>
              </template>
            </el-table-column>
          </el-table>
          <!-- 分页器 -->
          <div class="pagination-wrapper">
            <el-pagination
              :current-page="pagination.current"
              :page-sizes="[30, 50, 100]"
              :page-size="pagination.size"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              class="custom-pagination"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 拣货弹窗（纵向排列） -->
    <el-dialog
      title="拣货入仓"
      :visible.sync="pickDialogVisible"
      width="700px"
      :append-to-body="true"
      :before-close="handleCancelPick"
    >
      <div class="pick-dialog-body">
        <el-form
          :model="pickForm"
          label-width="80px"
          class="pick-form vertical-pick-form"
        >
          <el-form-item label="拣货方式">
            <el-radio-group v-model="pickMode" @change="handlePickModeChange">
              <el-radio label="imei">串码</el-radio>
              <el-radio label="quantity">数量</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="串码">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入串码，每行一个"
              v-model="pickForm.imeiText"
              :disabled="pickMode === 'quantity'"
              style="width: 90%"
            />
          </el-form-item>

          <el-form-item label="数量">
            <el-input-number
              v-model.number="pickForm.quantity"
              :min="1"
              :disabled="pickMode === 'imei'"
              controls-position="right"
              style="width: 90%"
            />
          </el-form-item>

          <el-form-item label="收货仓">
            <el-select
              v-model="pickForm.warehouseId"
              placeholder="请选择收货仓"
              clearable
              style="width: 90%"
            >
              <el-option
                v-for="w in warehouseOptions"
                :key="w.value"
                :label="w.label"
                :value="w.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="入库备注">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入入库备注（选填）"
              v-model="pickForm.remark"
              maxlength="200"
              show-word-limit
              style="width: 90%"
            />
          </el-form-item>

          <div class="pick-info-card">
            <div class="pi-row">
              <span class="pi-label">订单号：</span
              ><span class="pi-value">{{
                pickDialogData.orderCode || "-"
              }}</span>
            </div>
            <div class="pi-row">
              <span class="pi-label">SKU：</span
              ><span class="pi-value"
                >{{ pickDialogData.productName || "-" }}
                {{ pickDialogData.skuName || "" }}</span
              >
            </div>
            <div class="pi-row">
              <span class="pi-label">供应商：</span
              ><span class="pi-value">{{
                pickDialogData.tradeCompanyName || "-"
              }}</span>
            </div>
             <div class="pi-row">
              <span class="pi-label">送货码：</span
              ><span class="pi-value" style="color: red;">{{
                pickDialogData.deliveryCode || "-"
              }}</span>
            </div>
             <div class="pi-row">
              <span class="pi-label">成交时间：</span
              ><span class="pi-value">{{
                pickDialogData.createTime || "-"
              }}</span>
            </div>
             <div class="pi-row">
              <span class="pi-label">备注：</span
              ><span class="pi-value">{{
                pickDialogData.remark || "-"
              }}</span>
            </div>
          </div>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-row type="flex" justify="end" align="middle" :gutter="12">
          <el-col>
            <el-button type="primary" @click="handleConfirmPick"
              >拣货入仓</el-button
            >
          </el-col>
          <el-col>
            <el-button @click="handleCancelPick">取消</el-button>
          </el-col>
        </el-row>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import EmptyState from "./components/EmptyState";
import BrandFilter from "./components/brandFilter";
import OrderSearch from "./components/OrderSearch";
import {
  getPickList,
  setPicked,
  getWarehouseType,
  setPicking,
} from "@/api/pickManage";
import { getDeliveryTimeText } from "@/utils/deliveryTime";

export default {
  name: "TodayOrder",
  components: {
    EmptyState,
    BrandFilter,
    OrderSearch,
  },
  data() {
    return {
      loading: false,
      orderList: [],
      selectedRegion: "",
      selectedBrand: "",
      currentStatus: [5,6],  // 5，6
      // 搜索表单
      searchForm: {
        productName: "",
        companyId: "",
        deliveryCode: "",
      },
      pagination: {
        current: 1,
        size: 30,
        total: 0,
      },
      // 拣货弹窗相关
      pickDialogVisible: false,
      pickDialogData: {},
      pickForm: {
        imeiText: "",
        remark: "",
        warehouseId: "",
        quantity: 1,
      },
      pickMode: "imei",
      // 选择项（可由接口替换）
      warehouseOptions: [],
    };
  },
  computed: {},
  mounted() {
    this.fetchOrderList();
  },
  methods: {
    // 发货时效显示方法
    getDeliveryTimeText,
    handleRegionChange(region) {
      console.log("区域切换:", region);
      this.selectedRegion = region;
      this.pagination.current = 1;
      // 重新获取订单列表
      this.fetchOrderList();
    },
    handleBrandChange(brand) {
      console.log("品牌切换:", brand);
      this.selectedBrand = brand;
      this.pagination.current = 1;
      // 重新获取订单列表
      this.fetchOrderList();
    },
    // 搜索处理
    handleSearch(searchData) {
      this.searchForm = { ...searchData };
      this.pagination.current = 1;
      this.fetchOrderList();
    },
    // 重置搜索
    handleReset(searchData) {
      this.searchForm = { ...searchData };
      this.pagination.current = 1;
      this.fetchOrderList();
    },
    async fetchOrderList() {
      this.loading = true;
      const res = await getPickList(
        {
        // province: this.selectedRegion,
        brand: this.selectedBrand,
        companyId: this.searchForm.companyId,
        productName: this.searchForm.productName,
          deliveryCode: this.searchForm.deliveryCode,
          statusList: this.currentStatus,
        },
        {
        pageNum: this.pagination.current,
          pageSize: this.pagination.size,
        }
      );
      if (res && res.code === 200) {
        this.orderList = res.rows;
        this.pagination.total = res.total;
      } else {
        this.$message.error(res?.msg || "获取订单列表失败");
      }
      this.loading = false;
    },
    handleSizeChange(size) {
      this.pagination.size = size;
      this.fetchOrderList();
    },
    handleCurrentChange(current) {
      this.pagination.current = current;
      this.fetchOrderList();
    },
    // 打开拣货弹窗
    openPickDialog(row) {
      this.pickDialogData = { ...row };
      this.pickForm = {
        imeiText: "",
        remark: "",
        warehouseId: "",
      };
      // load warehouse options if not loaded
      if (!this.warehouseOptions || !this.warehouseOptions.length) {
        this.loadWarehouseOptions().catch((err) => {
          console.warn("加载仓库列表失败", err);
        });
      }
      this.pickDialogVisible = true;
      // refresh BrandFilter when opening dialog
      if (this.$refs.brandFilter && typeof this.$refs.brandFilter.getBrandData === 'function') {
        try {
          this.$refs.brandFilter.getBrandData()
        } catch (err) {
          console.warn('刷新 BrandFilter 失败', err)
        }
      }
    },
    async loadWarehouseOptions() {
      try {
        const res = await getWarehouseType();
        const list = res?.rows || res?.data || [];
        this.warehouseOptions = Array.isArray(list)
          ? list.map((item) => {
              // dict entries often have dictLabel/dictValue
              const label =
                item.dictLabel ||
                item.label ||
                item.name ||
                item.remark ||
                item.companyName ||
                item.value ||
                item;
              const value =
                item.dictValue ||
                item.value ||
                item.id ||
                item.dictValue ||
                item;
              return { label, value };
            })
          : [];
      } catch (error) {
        console.error("获取仓库类型失败", error);
        this.warehouseOptions = [];
      }
    },
    handlePickModeChange(mode) {
      if (mode === "imei") {
        // clear quantity and enable imei
        this.pickForm.quantity = 1;
      } else if (mode === "quantity") {
        // clear imei textarea
        this.pickForm.imeiText = "";
      }
    },
    handleCancelPick() {
      this.pickDialogVisible = false;
    },
    async handleConfirmPick() {
      // 简单校验，实际可以根据需求增强
      // 验证：当选择串码时必须填写串码；选择数量时必须填写数量
      if (this.pickMode === "imei") {
        if (!this.pickForm.imeiText || !this.pickForm.imeiText.trim()) {
          this.$message.warning("请输入串码");
          return;
        }
      } else {
        if (!this.pickForm.quantity || Number(this.pickForm.quantity) <= 0) {
          this.$message.warning("请输入有效的数量");
          return;
        }
      }
      if (!this.pickForm.warehouseId) {
        this.$message.warning("请选择收货仓");
        return;
      }
      // 构建请求体并调用拣货入仓接口
      try {
        const snList =
          this.pickMode === "imei"
            ? String(this.pickForm.imeiText || "")
                .split(/[\r\n,;，；\s]+/)
                .map((s) => s.trim())
                .filter(Boolean)
            : [];

        const payload = {
          orderCode: this.pickDialogData.orderCode || "",
          quantity:
            this.pickMode === "quantity"
              ? Number(this.pickForm.quantity || 0)
              : '',
          remark: this.pickForm.remark || "",
          snList,
          warehouseCode: this.pickForm.warehouseId || "",
        };

        const res = await setPicking(payload);
        if (res && res.code === 200) {
          this.$message.success(res.msg || "拣货入仓成功");
          this.pickDialogVisible = false;
          this.fetchOrderList();
        } else {
          this.$message.error(res?.msg || "拣货入仓失败");
        }
      } catch (err) {
        console.error("拣货入仓失败", err);
        this.$message.error("拣货入仓失败，请稍后重试");
      }
    },
    // 完成拣货：需要两次确认
    async handleCompletePick(row) {
      try {
        await this.$confirm("确认要完成拣货？", "请确认", {
          confirmButtonText: "确认",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }
      try {
        const res = await setPicked(row.orderCode);
        if (res && res.code === 200) {
          this.$message.success("完成拣货成功");
          this.fetchOrderList();
          // refresh BrandFilter data after complete pick
          if (this.$refs.brandFilter && typeof this.$refs.brandFilter.getBrandData === 'function') {
            try {
              this.$refs.brandFilter.getBrandData()
            } catch (err) {
              console.warn('刷新 BrandFilter 失败', err)
            }
          }
        } else {
          this.$message.error(res?.msg || "完成拣货失败");
        }
      } catch (error) {
        console.error("完成拣货失败", error);
        this.$message.error("完成拣货失败，请稍后重试");
      }
    },
    handleGrab(row) {
      this.$message.success(`抢单成功：${row.orderNo}`);
    },
    handleView(row) {
      this.$message.info(`查看订单：${row.orderNo}`);
    },
    // 串码选项相关方法
    getCodeOptionType(codeOptions) {
      const typeMap = {
        0: "success", // 发货前提供串码
        1: "warning", // 发货后提供串码
        2: "info", // 不需要串码
      };
      return typeMap[codeOptions] || "info";
    },
    getCodeOptionText(codeOptions) {
      const textMap = {
        0: "发货前提供",
        1: "发货后提供",
        2: "不需要",
      };
      return textMap[codeOptions] || "未知";
    },
    // 格式化账期
    formatAccountingPeriod(val) {
      const n = Number(val);
      if (!isFinite(n) || n < 0) return "-";
      return n === 0 ? "当天" : `T+${n}`;
    },
    // 复制文本到剪贴板
    copyText(text) {
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard
          .writeText(text)
          .then(() => {
            this.$message.success("复制成功");
          })
          .catch(() => {
            this.fallbackCopyText(text);
          });
      } else {
        this.fallbackCopyText(text);
      }
    },
    // 降级复制方法
    fallbackCopyText(text) {
      const textArea = document.createElement("textarea");
      textArea.value = text;
      textArea.style.position = "fixed";
      textArea.style.left = "-999999px";
      textArea.style.top = "-999999px";
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      try {
        document.execCommand("copy");
        this.$message.success("复制成功");
      } catch (err) {
        this.$message.error("复制失败");
      }
      document.body.removeChild(textArea);
    },
    // 标签筛选相关方法
    handleTagFilterConfirm() {
      this.$message.success("标签筛选已应用");
    },
    handleTagFilterCancel() {
      console.log("取消标签筛选");
    },
    // 订单编号搜索相关方法
    handleOrderNumberFilterConfirm() {
      this.$message.success("订单编号搜索已应用");
    },
    handleOrderNumberFilterCancel() {
      console.log("取消订单编号搜索");
    },
    // 发货时效筛选相关方法
    handleShippingTimeFilterConfirm() {
      this.$message.success("发货时效筛选已应用");
    },
    handleShippingTimeFilterCancel() {
      console.log("取消发货时效筛选");
    },
    // 到货时效筛选相关方法
    handleArrivalTimeFilterConfirm() {
      this.$message.success("到货时效筛选已应用");
    },
    handleArrivalTimeFilterCancel() {
      console.log("取消到货时效筛选");
    },
    // 产品型号筛选相关方法
    handleProductFilterConfirm() {
      this.$message.success("产品型号筛选已应用");
    },
    handleProductFilterCancel() {
      console.log("取消产品型号筛选");
    },
    // 揽收状态筛选相关方法
    handlePickupStatusFilterConfirm() {
      this.$message.success("揽收状态筛选已应用");
    },
    handlePickupStatusFilterCancel() {
      console.log("取消揽收状态筛选");
    },
    // 操作相关方法已移除
  },
};
</script>

<style lang="scss" scoped>
@import "./style/table-common.scss";

// 覆盖 table-common.scss 中的固定高度，适应 flex 布局
.order-table-container-wrapper {
  height: auto !important;
  flex: 1;
  min-height: 0;
}

// 筛选面板布局
.filter-panels {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

/* 订单信息样式 */
.order-numbers {
  display: flex;
  flex-direction: column;

  .order-number-item {
    padding: 0;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 12px;
    line-height: 20px;
  }
}

// 产品型号样式
.order-productName {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
}

/* 操作按钮样式 */
.operation-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
  .el-button {
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    &.el-button--mini {
      padding: 6px 12px;
    font-size: 12px;
    }
  }
}

.order-productName-line {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  line-height: 20px;
}

.order-sku-line {
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  color: var(--adm-text-tertiary);
}

/* 复制图标样式 */
.copy-icon {
  color: var(--primary-color);
  cursor: pointer;
  font-size: 14px;
  margin-left: 4px;
  transition: color 0.3s;

  &:hover {
    color: var(--primary-hover);
  }
}

/* 地址弹框样式 */
:deep(.addr-popover) {
  max-width: 520px;
  padding: 12px;
}

.addr-content {
  min-width: 200px;
}

.addr-line {
  line-height: 22px;
  margin-bottom: 4px;

  &.main {
    font-size: 14px;
    color: var(--adm-text-primary);
    font-weight: 500;
  }

  &.sub {
    font-size: 13px;
    color: var(--adm-text-secondary);
  }
}
.pending-order {
  // 订单内容布局
  .order-content-layout {
    display: flex;
    gap: 16px;
    flex: 1;
    min-height: 0; // 确保flex子元素可以收缩
  }

  .order-table-container {
    flex: 1;
    background: var(--bg-card);
    border-radius: 8px;
    box-shadow: var(--shadow-card);
    overflow: hidden;
  }

  // 响应式布局
  @media (max-width: 1200px) {
    .order-content-layout {
      flex-direction: column;
      gap: 12px;
    }

    .order-table-container {
      overflow-x: auto;
    }
  }

  @media (max-width: 768px) {
    .page-header {
      flex-direction: column;
      gap: 16px;
      text-align: center;

      .header-actions {
        justify-content: center;
      }
    }

    .order-content-layout {
      margin-bottom: 16px;
    }

    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
      padding: 20px;
      background: var(--bg-card);
      border-radius: 6px;
      box-shadow: var(--shadow-card);
      border: 1px solid var(--border-tags);

      .custom-pagination {
        .el-pagination__sizes,
        .el-pagination__jump {
          display: none;
        }
      }
    }
  }

  @media (max-width: 480px) {
    .page-header {
      padding: 16px;
      margin-bottom: 16px;

      .page-title {
        font-size: 18px;
      }

      .page-subtitle {
        font-size: 12px;
      }
    }

    .operation-buttons {
      flex-direction: column;
      gap: 4px;

      .el-button {
        width: 100%;
      }
    }
  }
}

// 弹窗样式优化
::v-deep .el-dialog {
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 20px 24px;
    border-radius: 12px 12px 0 0;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: white;
        font-size: 20px;

        &:hover {
          color: #ffd700;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  .el-dialog__footer {
    padding: 20px 24px;
    background: var(--bg-card);
    border-radius: 0 0 12px 12px;
    border-top: 1px solid var(--border-tags);

    .el-button {
      padding: 10px 20px;
      border-radius: 8px;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }
  }
}

.operation-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;

  .el-button {
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    &.el-button--mini {
      padding: 6px 12px;
    font-size: 12px;
    }
  }
}

.status-message {
  color: #f56c6c;
  font-size: 12px;
  line-height: 1.4;
  text-align: center;
  padding: 8px 0;
  background: rgba(245, 108, 108, 0.1);
  border-radius: 4px;
  border-left: 3px solid #f56c6c;
  margin-top: 8px;
}

// 动画效果
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes shake {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(2px);
  }
}

.status-text {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: var(--adm-text-secondary);

  .status-icon {
    font-size: 12px;
    margin-left: 4px;
    opacity: 0.7;
    transition: all 0.3s ease;
  }

  &:hover {
    color: var(--primary-color);

    .status-icon {
      opacity: 1;
      transform: scale(1.1);
    }
  }
}

.status-tooltip-content {
  p {
    margin: 0 0 8px 0;
    line-height: 1.5;
    font-size: 13px;

    &:last-child {
      margin-bottom: 0;
    }

    strong {
      color: #e6a23c;
    }
  }
}
.new-order-form ::v-deep .el-select {
  width: 100%;
}

/* 拣货弹窗样式优化（纵向排列） */
.pick-dialog-body {
  height: 550px;
  overflow: auto;
  .pick-info-card {
    background: var(--bg-card);
    border: 1px solid var(--border-tags);
    padding: 12px 14px;
    border-radius: 8px;
    color: var(--adm-text-secondary);
    font-size: 14px;
    margin-bottom: 12px;
  }

  .pi-row {
    margin-bottom: 8px;
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .pi-label {
    color: var(--adm-text-tertiary);
    min-width: 100px;
    font-weight: 600;
  }

  .pi-value {
    color: var(--adm-text-primary);
    word-break: break-word;
    flex: 1;
  }

  .pick-field .el-input,
  .pick-field .el-select {
    width: 100%;
  }

  .vertical-pick-form .el-form-item {
    margin-bottom: 14px;
  }

  .vertical-pick-form .el-form-item__label {
    display: block;
    color: var(--adm-text-secondary);
    font-weight: 600;
    margin-bottom: 8px;
  }

  .dialog-footer {
    padding: 12px 24px;
  }

  .el-dialog__body .el-input__inner {
    border-radius: 6px;
  }
}
</style>

<style>
/* 全局状态popover样式 */
.status-popover {
  background: #606266 !important;
  color: #fff !important;
  border: none !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3) !important;
}

.status-popover .el-popover__arrow {
  border-top-color: #606266 !important;
}

.status-popover .status-tooltip-content {
  color: #fff;
}

.status-popover .status-tooltip-content p strong {
  color: #ffd700 !important;
}
</style>
