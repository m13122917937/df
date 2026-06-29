<template>
  <div class="search-section">
    <div class="search-content">
      <div class="search-container">
        <!-- 第一行 -->
        <div class="search-row">
          <div v-if="showOrderCode" class="search-item">
            <label class="search-label">内部单号</label>
            <div class="search-input-wrapper">
              <el-input
                v-model="orderCodeValue"
                placeholder="请输入内部单号"
                @keyup.enter.native="handleSearch"
                clearable
                class="search-input"
                prefix-icon="el-icon-search"
              />
            </div>
          </div>

          <div v-if="showOriginalOrderId" class="search-item">
            <label class="search-label">商家单号</label>
            <div class="search-input-wrapper">
              <el-input
                v-model="originalOrderIdValue"
                placeholder="请输入商家单号"
                @keyup.enter.native="handleSearch"
                clearable
                class="search-input"
                prefix-icon="el-icon-search"
              />
            </div>
          </div>

          <!-- 商品名称不在第二行时，显示在第一行 -->
          <div v-if="showProductNameLike && !productNameLikeRow2" class="search-item">
            <label class="search-label">商品名称</label>
            <div class="search-input-wrapper">
              <el-input
                v-model="productNameLikeValue"
                placeholder="请输入商品名称"
                @keyup.enter.native="handleSearch"
                clearable
                class="search-input"
                prefix-icon="el-icon-search"
              />
            </div>
          </div>

          <div v-if="showSkuNameLike" class="search-item">
            <label class="search-label">SKU</label>
            <div class="search-input-wrapper">
              <el-input
                v-model="skuNameLikeValue"
                placeholder="请输入SKU名称"
                @keyup.enter.native="handleSearch"
                clearable
                class="search-input"
                prefix-icon="el-icon-search"
              />
            </div>
          </div>
          <!-- <div v-if="showCategory" class="search-item">
            <label class="search-label">品类</label>
            <div class="search-input-wrapper">
              <el-select
                v-model="categoryValue"
                placeholder="请选择品类"
                filterable
                clearable
                class="category-select"
              >
                <el-option
                  v-for="(item, index) in categoryList"
                  :key="index"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </div>
          </div> -->
        </div>

        <!-- 第二行 -->
        <div class="search-row">
          <!-- <div v-if="showShopName" class="search-item">
            <label class="search-label">店铺名称</label>
            <div class="search-input-wrapper">
              <el-select
                v-model="shopNameValue"
                placeholder="请选择店铺名称"
                filterable
                clearable
                class="shop-name-select"
              >
                <el-option
                  v-for="(item, index) in categoryList"
                  :key="index"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </div>
          </div> -->
          <div v-if="showTime" class="search-item">
            <label class="search-label">最晚发货时间</label>
            <div class="search-input-wrapper">
              <el-date-picker
                v-model="searchForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                :picker-options="datePickerOptions"
                class="search-input"
              />
            </div>
          </div>

          <!-- 商品名称在第二行时，显示在供应商前面 -->
          <div v-if="showProductNameLike && productNameLikeRow2" class="search-item">
            <label class="search-label">商品名称</label>
            <div class="search-input-wrapper">
              <el-input
                v-model="productNameLikeValue"
                placeholder="请输入商品名称"
                @keyup.enter.native="handleSearch"
                clearable
                class="search-input"
                prefix-icon="el-icon-search"
              />
            </div>
          </div>

          <div v-if="showCompany" class="search-item">
            <label class="search-label">供应商</label>
            <div class="search-input-wrapper">
              <el-select
                v-model="companyValue"
                placeholder="请选择供应商"
                clearable
                filterable
                remote
                reserve-keyword
                :remote-method="onCompanyRemote"
                :loading="companyLoadingProp"
                @visible-change="onCompanyVisible"
                @change="onCompanyChange"
                class="search-select"
              >
                <el-option v-for="s in companyOptionsProp" :key="s.value" :label="s.label" :value="s.value" />
              </el-select>
            </div>
          </div>

          <div v-if="showPayerName" class="search-item">
            <label class="search-label">付款主体</label>
            <div class="search-input-wrapper">
              <el-input
                v-model="payerNameValue"
                placeholder="请输入付款主体"
                @keyup.enter.native="handleSearch"
                clearable
                class="search-input"
                prefix-icon="el-icon-search"
              />
            </div>
          </div>

          <div v-if="showCreateBy" class="search-item">
            <label class="search-label">采购人</label>
            <div class="search-input-wrapper">
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
                class="search-select"
              >
                <el-option v-for="u in userOptions" :key="u.value" :label="u.label" :value="u.value" />
              </el-select>
            </div>
          </div>

          <div class="search-item" v-if="showEmpty">
            <label class="search-label placeholder-label"></label>
            <div class="search-input-wrapper"></div>
          </div>

          <div class="search-item button-wrap">
            <label class="search-label placeholder-label"></label>
            <div class="search-input-wrapper button-group">
              <el-button
                icon="el-icon-refresh"
                @click="handleReset"
                class="reset-btn"
              >
                重置
              </el-button>
              <el-button
                type="primary"
                icon="el-icon-search"
                v-throttle-click="600"
                @click="handleSearch"
                class="search-action-btn"
              >
                搜索
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 工具栏插槽 -->
    <div v-if="$slots.toolbar" class="toolbar-slot">
      <slot name="toolbar"></slot>
    </div>
  </div>
</template>

<script>
import { getBusinessCompanyListApi } from "@/api/business";
import { listUser } from "@/api/system/user";
export default {
  name: "SearchSection",
  props: {
    // 状态：6-在途
    status: {
      type: Number,
      default: 6,
      required: true,
    },
    // 内部单号
    orderCode: {
      type: String,
      default: "",
    },
    // 商家单号
    originalOrderId: {
      type: String,
      default: "",
    },
    // 品类
    category: {
      type: [String, Number],
      default: "",
    },
    // 店铺名称
    shopName: {
      type: [String, Number],
      default: "",
    },
    // 商品名称
    productNameLike: {
      type: String,
      default: "",
    },
    // SKU名称
    skuNameLike: {
      type: String,
      default: "",
    },
    // 显示配置
    showOrderCode: {
      type: Boolean,
      default: true,
    },
    showOriginalOrderId: {
      type: Boolean,
      default: true,
    },
    showCategory: {
      type: Boolean,
      default: true,
    },
    showShopName: {
      type: Boolean,
      default: true,
    },
    showProductNameLike: {
      type: Boolean,
      default: true,
    },
    showSkuNameLike: {
      type: Boolean,
      default: true,
    },
    showTime: {
      type: Boolean,
      default: false,
    },
    // 空box  满足ui对齐需求,当showTime为false时会自动替换showtime的空间，showTime传true，showEmpty传false
    showEmpty: {
      type: Boolean,
      default: true,
    },
    // company select support
    showCompany: {
      type: Boolean,
      default: false,
    },
    companyId: {
      type: [String, Number],
      default: undefined,
    },
    // 付款主体搜索
    showPayerName: {
      type: Boolean,
      default: false,
    },
    // 采购人搜索
    showCreateBy: {
      type: Boolean,
      default: false,
    },
    // 商品名称是否显示在第二行（与供应商同行）
    productNameLikeRow2: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      categoryList: [],
      shopNameList: [],
      orderCodeValue: this.orderCode,
      originalOrderIdValue: this.originalOrderId,
      categoryValue: this.category,
      shopNameValue: this.shopName,
      productNameLikeValue: this.productNameLike,
      skuNameLikeValue: this.skuNameLike,
      // 最晚发货时间范围（[start, end]）
      searchForm: {
        dateRange: [],
      },
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
    companyOptionsProp() {
      return this.companyOptions || [];
    },
    companyLoadingProp() {
      return this.companyLoading;
    }
  },
  created() {
    this.companyValue = this.companyId;
  },
  watch: {
    companyId(newVal) {
      this.companyValue = newVal;
    },
    status(newVal) {
      this.status = newVal;
      this.getCategoryList();
      // this.getShopNameList();
    },
    orderCode(newVal) {
      this.orderCodeValue = newVal;
    },
    originalOrderId(newVal) {
      this.originalOrderIdValue = newVal;
    },
    category(newVal) {
      this.categoryValue = newVal;
    },
    shopName(newVal) {
      this.shopNameValue = newVal;
    },
    productNameLike(newVal) {
      this.productNameLikeValue = newVal;
    },
    skuNameLike(newVal) {
      this.skuNameLikeValue = newVal;
    },
  },
  mounted() {
    // this.getCategoryList();
    // this.getShopNameList();
  },
  methods: {
    // 查询品类列表
    // async getCategoryList() {
    //   const res = await getCategoryListApi({
    //     statusList: [this.status],
    //   });
    //   if (res.code === 200) {
    //     this.categoryList = res.data;
    //   }
    // },
    // 查询店铺名称列表
    // async getShopNameList() {
    //   const res = await getShopNameListApi({
    //     statusList: [this.status],
    //   });
    //   if (res.code === 200) {
    //     this.shopNameList = res.data;
    //   }
    // },
    // 搜索处理
    handleSearch() {
      const searchData = {};
      if (this.showOrderCode) {
        searchData.orderCode = this.orderCodeValue;
      }
      if (this.showOriginalOrderId) {
        searchData.originalOrderId = this.originalOrderIdValue;
      }
      if (this.showCategory) {
        searchData.category = this.categoryValue;
      }
      if (this.showShopName) {
        searchData.shopName = this.shopNameValue;
      }
      if (this.showProductNameLike) {
        searchData.productNameLike = this.productNameLikeValue;
      }
      if (this.showSkuNameLike) {
        searchData.skuNameLike = this.skuNameLikeValue;
      }
      if (this.showCompany) {
        searchData.companyId = this.companyValue;
      }
      if (this.showPayerName) {
        searchData.payerName = this.payerNameValue;
      }
      if (this.showCreateBy) {
        searchData.createBy = this.createByValue;
      }
      if (
        this.showTime &&
        this.searchForm &&
        this.searchForm.dateRange &&
        this.searchForm.dateRange.length === 2
      ) {
        searchData.lastShippingTimeStart = this.searchForm.dateRange[0];
        searchData.lastShippingTimeEnd = this.searchForm.dateRange[1];
      }

      this.$emit("search", searchData);
    },

    // 重置搜索条件
    handleReset() {
      this.orderCodeValue = "";
      this.originalOrderIdValue = "";
      this.categoryValue = "";
      this.shopNameValue = "";
      this.productNameLikeValue = "";
      this.skuNameLikeValue = "";
      this.companyValue = "";
      this.payerNameValue = "";
      this.createByValue = "";
      this.userOptions = [];
      this.userLoading = false;
      if (this.showTime && this.searchForm) {
        this.searchForm.dateRange = [];
      }

      const resetData = {};
      if (this.showOrderCode) {
        resetData.orderCode = "";
      }
      if (this.showOriginalOrderId) {
        resetData.originalOrderId = "";
      }
      if (this.showCategory) {
        resetData.category = "";
      }
      if (this.showShopName) {
        resetData.shopName = "";
      }
      if (this.showProductNameLike) {
        resetData.productNameLike = "";
      }
      if (this.showSkuNameLike) {
        resetData.skuNameLike = "";
      }
      if (this.showTime) {
        resetData.lastShippingTimeStart = "";
        resetData.lastShippingTimeEnd = "";
      }
      if (this.showCompany) {
        resetData.companyId = "";
      }
      if (this.showPayerName) {
        resetData.payerName = "";
      }
      if (this.showCreateBy) {
        resetData.createBy = "";
      }

      // clear local company input and options to avoid stale dropdown results after reset
      if (this.showCompany) {
        this.companyValue = '';
        this.companyOptions = [];
        this.companyLoading = false;
        // notify parent immediately that company was cleared
      }

      this.$emit("reset", resetData);
    },
    // company handling inside SearchSection
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
          label: item.companyName,
          value: item.id,
          raw: item
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
    // 采购人远程搜索
    async onUserRemote(keyword) {
      const trimmed = (keyword || "").trim();
      this.userLoading = true;
      try {
        const params = { pageNum: 1, pageSize: 30 };
        if (trimmed) params.nickName = trimmed;
        const res = await listUser(params);
        const list = res?.rows || res?.data || [];
        this.userOptions = Array.isArray(list) ? list.map(item => ({
          label: item.nickName,
          value: item.userId,
          raw: item
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
  margin-bottom: 16px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #fcfdff 0%, #f5f8fc 100%);
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
}

.search-section::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff 0%, #667eea 100%);
}

.search-content {
  display: flex;
  align-items: stretch;
}

.search-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 16px;
  width: 100%;
}

.search-row {
  display: flex;
  align-items: flex-end;
  width: 100%;
  gap: 20px;
  flex-wrap: wrap;
}

.search-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 140px;
  &.button-wrap {
    justify-content: flex-end;
    flex: 0 0 auto;
    min-width: 220px;
  }
}

.search-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
  line-height: 1.5;
  padding-left: 2px;
}

.search-label::before {
  content: "";
  width: 4px;
  height: 4px;
  background: #409eff;
  border-radius: 50%;
}

.placeholder-label {
  visibility: hidden;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;

  &.button-group {
    justify-content: flex-end;
    gap: 12px;
  }
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 10px 14px 10px 38px;
  height: 40px;
  line-height: 40px;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.search-input :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  background: #fff;
}

.search-input :deep(.el-input__prefix) {
  left: 12px;
  color: #909399;
}

/* search-select 风格与 search-input 一致，改进下拉输入框的视觉 */
.search-select {
  flex: 1;
  width: 100%;
}

.search-select :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 10px 14px;
  height: 40px;
  line-height: 40px;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  min-height: 40px;
}

.search-select :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  background: #fff;
}

.search-select :deep(.el-input__suffix) {
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
}

.category-select {
  flex: 1;
  min-width: 0;
}

.category-select :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 10px 14px;
  height: 40px;
  line-height: 40px;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.category-select :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  background: #fff;
}

.category-select :deep(.el-input__suffix) {
  right: 12px;
}

.shop-name-select {
  flex: 1;
  min-width: 0;
}

.shop-name-select :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 10px 14px;
  height: 40px;
  line-height: 40px;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  min-height: 40px;
}

.shop-name-select :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  background: #fff;
}

.shop-name-select :deep(.el-input__suffix) {
  right: 12px;
}

.shop-name-select :deep(.el-tag) {
  margin: 2px 4px 2px 0;
  background: linear-gradient(135deg, #f0f9ff, #e1f5fe);
  border-color: #b3d8ff;
  color: #409eff;
  border-radius: 6px;
  font-weight: 500;
}

.shop-name-select :deep(.el-tag__close) {
  color: #409eff;
  font-weight: bold;
}

.shop-name-select :deep(.el-tag__close:hover) {
  background: #409eff;
  color: #fff;
}

.search-actions {
  display: flex;
  width: 200px;
  justify-content: space-between;
  align-items: center;
}

.toolbar-slot {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-height: 36px;
  gap: 12px;
}

.search-action-btn {
  height: 40px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.25);
  transition: all 0.3s ease;
  width: 90px;
  padding: 0 16px;
}

.search-action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.35);
}

.reset-btn {
  height: 40px;
  font-size: 14px;
  border-radius: 8px;
  color: #606266;
  border-color: #dcdfe6;
  transition: all 0.3s ease;
  width: 80px;
  padding: 0 16px;
}

.reset-btn:hover {
  color: #409eff;
  border-color: #409eff;
  background: #f0f9ff;
}

/* 响应式设计 */
@media (min-width: 1600px) {
  .search-item {
    flex: 1 1 20%;
  }
}

@media (max-width: 1400px) {
  .search-row {
    flex-wrap: wrap;
  }
  .search-item {
    flex: 1 1 calc(50% - 10px);
  }
}

@media (max-width: 1100px) {
  .search-row {
    flex-direction: column;
    gap: 12px;
  }

  .search-item {
    width: 100%;
    min-width: auto;
  }

  .search-item.button-wrap {
    min-width: auto;
  }
}

@media (max-width: 900px) {
  .search-content {
    flex-direction: column;
    gap: 16px;
  }

  .search-container {
    flex-direction: column;
    gap: 12px;
    overflow-x: visible;
  }

  .search-row {
    flex-direction: column;
    gap: 12px;
  }

  .search-item {
    width: 100%;
    min-width: auto;
  }

  .search-input-wrapper.button-group {
    justify-content: flex-end;
  }
}

@media (max-width: 600px) {
  .search-section {
    padding: 12px;
  }
  .search-input-wrapper.button-group {
    width: 100%;
    justify-content: stretch;
  }
  .reset-btn,
  .search-action-btn {
    flex: 1;
  }
}
</style>
