<template>
  <div class="search-section">
    <!-- Search Toolbar -->
    <div class="search-toolbar">
      <!-- Search Input -->
      <div v-if="showKeywordSearch" class="toolbar-search">
        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input
          v-model="keyword"
          placeholder="搜索内部单号"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button v-if="keyword" class="clear-btn" @click="keyword = ''">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>

      <!-- Filter Chips -->
      <div class="toolbar-chips" v-if="hasActiveFilters">
        <div
          v-for="chip in activeChips"
          :key="chip.key"
          class="filter-chip"
          @click="removeChip(chip.key)"
        >
          <span class="chip-label">{{ chip.label }}:</span>
          <span class="chip-value">{{ chip.value }}</span>
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </div>
      </div>

      <!-- Actions -->
      <div class="toolbar-actions">
        <button class="btn-ghost" @click="drawerVisible = true">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="4" y1="21" x2="4" y2="14"/>
            <line x1="4" y1="10" x2="4" y2="3"/>
            <line x1="12" y1="21" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12" y2="3"/>
            <line x1="20" y1="21" x2="20" y2="16"/>
            <line x1="20" y1="12" x2="20" y2="3"/>
            <line x1="1" y1="14" x2="7" y2="14"/>
            <line x1="9" y1="8" x2="15" y2="8"/>
            <line x1="17" y1="16" x2="23" y2="16"/>
          </svg>
          高级筛选
          <span v-if="activeFilterCount" class="filter-badge">{{ activeFilterCount }}</span>
        </button>
        <div class="btn-divider"></div>
        <button class="btn-ghost btn-reset" @click="handleReset">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="23 4 23 10 17 10"/>
            <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/>
          </svg>
          重置
        </button>
        <button class="btn-search" @click="handleSearch">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          搜索
        </button>
      </div>
    </div>

    <!-- Toolbar Slot -->
    <div v-if="$slots.toolbar" class="toolbar-slot">
      <slot name="toolbar"></slot>
    </div>

    <!-- Advanced Filter Drawer -->
    <el-drawer
      title="高级筛选"
      :visible.sync="drawerVisible"
      direction="rtl"
      size="420px"
      :modal="true"
      :close-on-click-modal="true"
    >
      <div class="drawer-body">
        <!-- 退货单号 -->
        <div v-if="showReturnCode" class="drawer-field">
          <label class="drawer-label">退货单号</label>
          <el-input
            v-model="returnCodeValue"
            placeholder="请输入退货单号"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="drawerVisible = false; handleSearch()"
          />
        </div>

        <!-- 内部单号 -->
        <div v-if="showOrderCode" class="drawer-field">
          <label class="drawer-label">内部单号</label>
          <el-input
            v-model="orderCodeValue"
            placeholder="请输入内部单号"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="drawerVisible = false; handleSearch()"
          />
        </div>

        <!-- 商家单号 -->
        <div v-if="showOriginalOrderId" class="drawer-field">
          <label class="drawer-label">商家单号</label>
          <el-input
            v-model="originalOrderIdValue"
            placeholder="请输入商家单号"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="drawerVisible = false; handleSearch()"
          />
        </div>

        <!-- 商品名称（未单独在第二行时） -->
        <div v-if="showProductNameLike && !productNameLikeRow2" class="drawer-field">
          <label class="drawer-label">商品名称</label>
          <el-input
            v-model="productNameLikeValue"
            placeholder="请输入商品名称"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="drawerVisible = false; handleSearch()"
          />
        </div>

        <!-- SKU -->
        <div v-if="showSkuNameLike" class="drawer-field">
          <label class="drawer-label">SKU</label>
          <el-input
            v-model="skuNameLikeValue"
            placeholder="请输入SKU名称"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="drawerVisible = false; handleSearch()"
          />
        </div>

        <!-- 商品名称（第二行） -->
        <div v-if="showProductNameLike && productNameLikeRow2" class="drawer-field">
          <label class="drawer-label">商品名称</label>
          <el-input
            v-model="productNameLikeValue"
            placeholder="请输入商品名称"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="drawerVisible = false; handleSearch()"
          />
        </div>

        <!-- 最晚发货时间 -->
        <div v-if="showTime" class="drawer-field">
          <label class="drawer-label">最晚发货时间</label>
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="datePickerOptions"
            style="width: 100%"
          />
        </div>

        <!-- 供应商 -->
        <div v-if="showCompany" class="drawer-field">
          <label class="drawer-label">供应商</label>
          <el-select
            v-model="companyValue"
            placeholder="请选择供应商"
            clearable
            filterable
            remote
            reserve-keyword
            :remote-method="onCompanyRemote"
            :loading="companyLoading"
            @visible-change="onCompanyVisible"
            @change="onCompanyChange"
          >
            <el-option v-for="s in companyOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </div>

        <!-- 付款主体 -->
        <div v-if="showPayerName" class="drawer-field">
          <label class="drawer-label">付款主体</label>
          <el-input
            v-model="payerNameValue"
            placeholder="请输入付款主体"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="drawerVisible = false; handleSearch()"
          />
        </div>

        <!-- 采购人 -->
        <div v-if="showCreateBy" class="drawer-field">
          <label class="drawer-label">采购人</label>
          <el-select
            v-model="createByValue"
            placeholder="请选择采购人"
            clearable
            filterable
            remote
            reserve-keyword
            :remote-method="onUserRemote"
            :loading="userLoading"
            @visible-change="onUserVisible"
            @change="onUserChange"
          >
            <el-option v-for="u in userOptions" :key="u.value" :label="u.label" :value="u.value" />
          </el-select>
        </div>
      </div>

      <div class="drawer-footer">
        <el-button @click="drawerVisible = false; handleReset()">重置</el-button>
        <el-button type="primary" @click="drawerVisible = false; handleSearch()">应用筛选</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getBusinessCompanyListApi } from "@/api/business";
import { listUser } from "@/api/system/user";
export default {
  name: "SearchSection",
  props: {
    status: { type: Number, default: 6, required: true },
    orderCode: { type: String, default: "" },
    originalOrderId: { type: String, default: "" },
    category: { type: [String, Number], default: "" },
    shopName: { type: [String, Number], default: "" },
    productNameLike: { type: String, default: "" },
    skuNameLike: { type: String, default: "" },
    returnCode: { type: String, default: "" },
    showKeywordSearch: { type: Boolean, default: true },
    showOrderCode: { type: Boolean, default: true },
    showOriginalOrderId: { type: Boolean, default: true },
    showCategory: { type: Boolean, default: true },
    showShopName: { type: Boolean, default: true },
    showProductNameLike: { type: Boolean, default: true },
    showSkuNameLike: { type: Boolean, default: true },
    showReturnCode: { type: Boolean, default: false },
    showTime: { type: Boolean, default: false },
    showEmpty: { type: Boolean, default: true },
    showCompany: { type: Boolean, default: false },
    companyId: { type: [String, Number], default: undefined },
    showPayerName: { type: Boolean, default: false },
    showCreateBy: { type: Boolean, default: false },
    productNameLikeRow2: { type: Boolean, default: false },
  },
  data() {
    return {
      drawerVisible: false,
      keyword: "",
      orderCodeValue: this.orderCode,
      originalOrderIdValue: this.originalOrderId,
      categoryValue: this.category,
      shopNameValue: this.shopName,
      productNameLikeValue: this.productNameLike,
      skuNameLikeValue: this.skuNameLike,
      returnCodeValue: this.returnCode,
      dateRange: [],
      datePickerOptions: {
        disabledDate(time) {
          const today = new Date();
          return time.getTime() > today.getTime();
        },
      },
      companyOptions: [],
      companyLoading: false,
      companyValue: this.companyId,
      payerNameValue: '',
      createByValue: '',
      userOptions: [],
      userLoading: false,
    };
  },
  computed: {
    hasActiveFilters() {
      return this.activeFilterCount > 0;
    },
    activeFilterCount() {
      let count = 0;
      if (this.returnCodeValue) count++;
      if (this.orderCodeValue) count++;
      if (this.originalOrderIdValue) count++;
      if (this.productNameLikeValue) count++;
      if (this.skuNameLikeValue) count++;
      if (this.dateRange && this.dateRange.length === 2) count++;
      if (this.companyValue) count++;
      if (this.payerNameValue) count++;
      if (this.createByValue) count++;
      return count;
    },
    activeChips() {
      const chips = [];
      if (this.returnCodeValue) chips.push({ key: 'returnCode', label: '退货单号', value: this.returnCodeValue });
      if (this.orderCodeValue) chips.push({ key: 'orderCode', label: '内部单号', value: this.orderCodeValue });
      if (this.originalOrderIdValue) chips.push({ key: 'originalOrderId', label: '商家单号', value: this.originalOrderIdValue });
      if (this.productNameLikeValue) chips.push({ key: 'productNameLike', label: '商品', value: this.productNameLikeValue });
      if (this.skuNameLikeValue) chips.push({ key: 'skuNameLike', label: 'SKU', value: this.skuNameLikeValue });
      if (this.companyValue) {
        const opt = this.companyOptions.find(o => o.value === this.companyValue);
        chips.push({ key: 'company', label: '供应商', value: opt ? opt.label : this.companyValue });
      }
      if (this.payerNameValue) chips.push({ key: 'payerName', label: '付款主体', value: this.payerNameValue });
      if (this.createByValue) {
        const opt = this.userOptions.find(o => o.value === this.createByValue);
        chips.push({ key: 'createBy', label: '采购人', value: opt ? opt.label : this.createByValue });
      }
      return chips;
    }
  },
  created() {
    this.companyValue = this.companyId;
  },
  watch: {
    companyId(val) { this.companyValue = val; },
    returnCode(val) { this.returnCodeValue = val; },
    orderCode(val) { this.orderCodeValue = val; },
    originalOrderId(val) { this.originalOrderIdValue = val; },
    category(val) { this.categoryValue = val; },
    shopName(val) { this.shopNameValue = val; },
    productNameLike(val) { this.productNameLikeValue = val; },
    skuNameLike(val) { this.skuNameLikeValue = val; },
  },
  methods: {
    buildSearchData() {
      const data = {};
      if (this.showReturnCode) data.returnCode = this.returnCodeValue;
      if (this.showOrderCode) data.orderCode = this.orderCodeValue;
      if (this.showOriginalOrderId) data.originalOrderId = this.originalOrderIdValue;
      if (this.showCategory) data.category = this.categoryValue;
      if (this.showShopName) data.shopName = this.shopNameValue;
      if (this.showProductNameLike) data.productNameLike = this.productNameLikeValue;
      if (this.showSkuNameLike) data.skuNameLike = this.skuNameLikeValue;
      if (this.showCompany) data.companyId = this.companyValue;
      if (this.showPayerName) data.payerName = this.payerNameValue;
      if (this.showCreateBy) data.createBy = this.createByValue;
      if (this.showTime && this.dateRange && this.dateRange.length === 2) {
        data.lastShippingTimeStart = this.dateRange[0];
        data.lastShippingTimeEnd = this.dateRange[1];
      }
      // keyword search: apply to orderCode and originalOrderId
      if (this.keyword) {
        if (!data.orderCode) data.orderCode = this.keyword;
      }
      return data;
    },
    handleSearch() {
      this.$emit("search", this.buildSearchData());
    },
    handleReset() {
      this.keyword = "";
      this.returnCodeValue = "";
      this.orderCodeValue = "";
      this.originalOrderIdValue = "";
      this.categoryValue = "";
      this.shopNameValue = "";
      this.productNameLikeValue = "";
      this.skuNameLikeValue = "";
      this.companyValue = "";
      this.payerNameValue = "";
      this.createByValue = "";
      this.companyOptions = [];
      this.userOptions = [];
      this.dateRange = [];

      const resetData = this.buildSearchData();
      this.$emit("reset", resetData);
    },
    removeChip(key) {
      if (key === 'returnCode') this.returnCodeValue = "";
      else if (key === 'orderCode') this.orderCodeValue = "";
      else if (key === 'originalOrderId') this.originalOrderIdValue = "";
      else if (key === 'productNameLike') this.productNameLikeValue = "";
      else if (key === 'skuNameLike') this.skuNameLikeValue = "";
      else if (key === 'company') { this.companyValue = ""; this.companyOptions = []; }
      else if (key === 'payerName') this.payerNameValue = "";
      else if (key === 'createBy') { this.createByValue = ""; this.userOptions = []; }
      this.handleSearch();
    },
    async onCompanyRemote(keyword) {
      const trimmed = (keyword || "").trim();
      this.companyLoading = true;
      try {
        const pageData = { pageNum: 1, pageSize: 30 };
        const params = {};
        if (trimmed) params.companyNameLike = trimmed;
        const res = await getBusinessCompanyListApi(params, pageData);
        const list = res?.rows || res?.data || [];
        this.companyOptions = Array.isArray(list) ? list.map(item => ({
          label: item.companyName, value: item.id, raw: item
        })) : [];
      } catch (error) {
        console.error('获取公司列表失败', error);
        this.companyOptions = [];
      } finally {
        this.companyLoading = false;
      }
    },
    onCompanyVisible(visible) {
      if (visible && !this.companyOptions.length) {
        this.onCompanyRemote();
      }
    },
    onCompanyChange(val) {
      this.companyValue = val;
      this.$emit('company-change', val);
    },
    async onUserRemote(keyword) {
      const trimmed = (keyword || "").trim();
      this.userLoading = true;
      try {
        const params = { pageNum: 1, pageSize: 30 };
        if (trimmed) params.nickName = trimmed;
        const res = await listUser(params);
        const list = res?.rows || res?.data || [];
        this.userOptions = Array.isArray(list) ? list.map(item => ({
          label: item.nickName, value: item.userId, raw: item
        })) : [];
      } catch (error) {
        console.error('获取用户列表失败', error);
        this.userOptions = [];
      } finally {
        this.userLoading = false;
      }
    },
    onUserVisible(visible) {
      if (visible && !this.userOptions.length) {
        this.onUserRemote();
      }
    },
    onUserChange(val) {
      this.createByValue = val;
      this.$emit('create-by-change', val);
    }
  },
};
</script>

<style scoped lang="scss">
.search-section {
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow: var(--shadow-card);
  padding: 12px 16px;
}

.search-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

/* ==================== Search Input ==================== */

.toolbar-search {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1.5px solid var(--adm-border);
  background: var(--bg-hover);
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  width: 240px;
  flex-shrink: 0;

  &:focus-within {
    border-color: #5B7CFA;
    background: var(--bg-card);
    box-shadow: 0 0 0 3px rgba(91,124,250,0.08);
  }
}

.search-icon {
  flex-shrink: 0;
}

.search-input {
  border: none;
  background: transparent;
  outline: none;
  height: 36px;
  font-size: 13px;
  color: var(--adm-text-primary);
  width: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'HarmonyOS Sans', 'PingFang SC', system-ui, sans-serif;

  &::placeholder {
    color: var(--adm-text-tertiary);
  }
}

.clear-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 4px;

  &:hover svg {
    stroke: #64748B;
  }
}

/* ==================== Filter Chips ==================== */

.toolbar-chips {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.filter-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
  color: #5B7CFA;
  background: rgba(91,124,250,0.08);
  cursor: pointer;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  white-space: nowrap;

  &:hover {
    background: rgba(91,124,250,0.15);
  }

  .chip-label {
    color: #64748B;
  }

  .chip-value {
    color: #5B7CFA;
    font-weight: 500;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

/* ==================== Actions ==================== */

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.btn-ghost {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--adm-text-secondary);
  background: transparent;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  font-family: inherit;
  white-space: nowrap;

  &:hover {
    background: var(--bg-hover);
    color: var(--adm-text-primary);
  }

  &.btn-reset:hover {
    color: #FF5E57;
    background: rgba(255,94,87,0.06);
  }
}

.filter-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: #5B7CFA;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
}

.btn-divider {
  width: 1px;
  height: 20px;
  background: var(--adm-border);
  margin: 0 4px;
}

.btn-search {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #FFFFFF;
  background: #5B7CFA;
  border: none;
  cursor: pointer;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  font-family: inherit;
  white-space: nowrap;

  &:hover {
    background: #6C89FF;
    box-shadow: 0 4px 16px rgba(91,124,250,0.18);
  }

  &:active {
    background: #486AF4;
  }
}

/* ==================== Toolbar Slot ==================== */

.toolbar-slot {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid var(--adm-border);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-height: 36px;
  gap: 12px;
}

/* ==================== Drawer ==================== */

::v-deep .el-drawer {
  .el-drawer__header {
    font-size: 15px;
    font-weight: 600;
    color: var(--adm-text-primary);
    padding: 20px 24px 0;
    margin-bottom: 0;
    border-bottom: 1px solid var(--adm-border);
    padding-bottom: 16px;
  }

  .el-drawer__body {
    padding: 20px 24px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  .el-drawer__close-btn {
    font-size: 16px;
    color: var(--adm-text-tertiary);
    &:hover {
      color: var(--adm-text-primary);
    }
  }
}

.drawer-body {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 4px;
}

.drawer-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.drawer-label {
  font-size: 12px;
  font-weight: 500;
  color: var(--adm-text-secondary);
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid var(--adm-border);
  margin-top: 0;
  flex-shrink: 0;
}

/* ==================== Responsive ==================== */

@media (max-width: 1200px) {
  .toolbar-search {
    width: 180px;
  }
}

@media (max-width: 900px) {
  .search-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-search {
    width: 100%;
  }

  .toolbar-actions {
    margin-left: 0;
    justify-content: flex-end;
  }
}
</style>
