<template>
  <div class="search-section">
    <div class="search-content">
      <div class="search-container">
        <!-- 第一行 -->
        <div class="search-row">
          <div v-if="showOrderCode" class="search-item">
            <label class="search-label">内部单号</label>
            <el-input
              v-model="orderCodeValue"
              placeholder="请输入内部单号"
              @keyup.enter.native="handleSearch"
              clearable
              class="search-input"
              prefix-icon="el-icon-search"
            />
          </div>

          <div v-if="showOriginalOrderId" class="search-item">
            <label class="search-label">商家单号</label>
            <el-input
              v-model="originalOrderIdValue"
              placeholder="请输入商家单号"
              @keyup.enter.native="handleSearch"
              clearable
              class="search-input"
              prefix-icon="el-icon-search"
            />
          </div>

          <div v-if="showProductNameLike" class="search-item">
            <label class="search-label">商品名称</label>
            <el-input
              v-model="productNameLikeValue"
              placeholder="请输入商品名称"
              @keyup.enter.native="handleSearch"
              clearable
              class="search-input"
              prefix-icon="el-icon-search"
            />
          </div>

          <div v-if="showSkuNameLike" class="search-item">
            <label class="search-label">SKU</label>
            <el-input
              v-model="skuNameLikeValue"
              placeholder="请输入SKU名称"
              @keyup.enter.native="handleSearch"
              clearable
              class="search-input"
              prefix-icon="el-icon-search"
            />
          </div>
          <!-- <div v-if="showCategory" class="search-item">
            <label class="search-label">品类</label>
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
          </div> -->


        </div>

        <!-- 第二行 -->
        <div class="search-row">
          <!-- <div v-if="showShopName" class="search-item">
            <label class="search-label">店铺名称</label>
            <el-select
              v-model="shopNameValue"
              placeholder="请选择店铺名称"
              filterable
              clearable
              class="shop-name-select"
            >
              <el-option
                v-for="(item, index) in shopNameList"
                :key="index"
                :label="item"
                :value="item"
              />
            </el-select>
          </div> -->
          <div v-if="showTime" class="search-item">
            <label class="search-label">最晚发货时间</label>
            <el-date-picker
              style="width: 230px"
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

          <div v-if="showCompany" class="search-item">
            <label class="search-label">供应商</label>
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

           <div class="search-item" v-if="showEmpty"></div>


          <div class="search-item button-wrap">
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

      <!-- <div class="search-actions">
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
      </div> -->
    </div>
    <!-- 工具栏插槽 -->
    <div v-if="$slots.toolbar" class="toolbar-slot">
      <slot name="toolbar"></slot>
    </div>
  </div>
</template>

<script>
import { getBusinessCompanyListApi } from "@/api/business";
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
    }
  },
};
</script>

<style scoped lang="scss">
.search-section {
  margin-bottom: 10px;
  padding: 10px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  border: 1px solid #e9ecef;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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
  background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c);
}

.search-content {
  display: flex;
  align-items: flex-end;
}

.search-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex: 1;
  gap: 12px;
}

.search-row {
  display: flex;
  align-items: flex-end;
  width: 100%;
  gap: 20px;
}

.search-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  &.button-wrap {
    justify-content: flex-end;
  }
}

.search-label {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  min-width: fit-content;
  flex-shrink: 0;
}

.search-label::before {
  content: "";
  width: 4px;
  height: 4px;
  background: #409eff;
  border-radius: 50%;
}

.search-input {
  flex: 1;
  min-width: 0;
}

.search-input :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 12px 16px 12px 40px;
  transition: all 0.3s ease;
  border: 2px solid #e4e7ed;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.search-input :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: #fafbfc;
}

.search-input :deep(.el-input__prefix) {
  left: 12px;
  color: #909399;
}

/* search-select 风格与 search-input 一致，改进下拉输入框的视觉 */
.search-select {
  flex: 1;
  min-width: 0;
}

.search-select :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  border: 2px solid #e4e7ed;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  min-height: 44px;
}

.search-select :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: #fafbfc;
}

.search-select :deep(.el-input__suffix) {
  right: 12px;
}



.category-select {
  flex: 1;
  min-width: 0;
}

.category-select :deep(.el-input__inner) {
  border-radius: 8px;
  font-size: 14px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  border: 2px solid #e4e7ed;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.category-select :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: #fafbfc;
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
  padding: 12px 16px;
  transition: all 0.3s ease;
  border: 2px solid #e4e7ed;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  min-height: 44px;
}

.shop-name-select :deep(.el-input__inner):focus {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: #fafbfc;
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
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-height: 40px;
  gap: 12px;
}

.search-action-btn {
  height: 36px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
  width: 100px;
}

.search-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.reset-btn {
  height: 36px;
  font-size: 14px;
  border-radius: 8px;
  color: #606266;
  border-color: #dcdfe6;
  transition: all 0.3s ease;
  width: 100px;
}

.reset-btn:hover {
  color: #409eff;
  border-color: #409eff;
  background: #f0f9ff;
}

/* 响应式设计 */
@media (max-width: 1100px) {
  .search-row {
    flex-direction: column;
    gap: 12px;
  }

  .search-item {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .search-label {
    margin-bottom: 4px;
  }
}

@media (max-width: 900px) {
  .search-content {
    flex-direction: column;
    gap: 16px;
  }

  .search-container {
    flex-direction: column;
    gap: 16px;
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

  .search-actions {
    flex-direction: row;
    justify-content: center;
    gap: 12px;
  }

  .search-action-btn,
  .reset-btn {
    flex: 1;
    max-width: 120px;
  }
}
</style>
