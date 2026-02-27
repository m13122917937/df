<!-- 搜索组件 -->
<template>
  <div class="search-section">
    <el-form :model="searchForm" :inline="true" class="search-form">
      <!-- 第一行 -->
      <div class="search-row">
        <el-form-item label="企业名称" class="search-item">
          <el-input
            v-model="searchForm.companyNameLike"
            placeholder="请输入企业名称"
            clearable
            trim
            @keyup.enter.native="handleSearch"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="省" class="search-item">
          <el-select
            v-model="searchForm.provinceId"
            placeholder="请选择省"
            clearable
            filterable
            class="search-select"
            @change="handleProvinceChange"
          >
            <el-option
              v-for="item in provinceOptions"
              :key="item.districtId"
              :label="item.district"
              :value="item.districtId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="市" class="search-item">
          <el-select
            v-model="searchForm.cityId"
            class="search-select"
            placeholder="请选择市"
            clearable
            filterable
            :disabled="!searchForm.provinceId"
          >
            <el-option
              v-for="item in cityOptions"
              :key="item.districtId"
              :label="item.district"
              :value="item.districtId"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group search-item">
          <el-button icon="el-icon-refresh" @click="handleReset">
            重置
          </el-button>
          <el-button
            type="primary"
            icon="el-icon-search"
            v-throttle-click="600"
            @click="handleSearch"
          >
            搜索
          </el-button>
        </el-form-item>
      </div>
    </el-form>
    <div class="table-btn">
      <slot name="table-btn"></slot>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'SearchSection',
  props: {
    // 可以接收外部传入的搜索表单数据
    formData: {
      type: Object,
      default: () => ({
        companyNameLike: '',
        provinceId: '',
        cityId: ''
      })
    }
  },
  data() {
    return {
      searchForm: {
        companyNameLike: '',
        provinceId: '',
        cityId: '',
      }
    }
  },
  watch: {
    formData: {
      handler(newVal) {
        if (newVal) {
          this.searchForm = { ...this.searchForm, ...newVal }
        }
      },
      immediate: true,
      deep: true
    }
  },
  computed: {
     ...mapGetters(['provinceList','cityList']),
    // 从Vuex获取省份列表
    provinceOptions() {
      return this.provinceList || []
    },
    // 从Vuex获取城市列表
    cityOptions() {
      return this.cityList || []
    }
  },
  created(){
    this.getProvinceList()
  },
  methods: {
    ...mapActions(['GET_PROVINCE_LIST','GET_CITY_LIST']),
    async getProvinceList() {
      // 确保Vuex中有省份数据
      await this.GET_PROVINCE_LIST()
    },
    async getCityList(provinceId) {
      await this.GET_CITY_LIST(provinceId)
    },
    // 搜索
    handleSearch() {
      this.$emit('search', {
        companyNameLike: this.searchForm.companyNameLike,
        provinceId: this.searchForm.provinceId,
        cityId: this.searchForm.cityId
      })
    },
    // 重置
    handleReset() {
      this.searchForm = {
        companyNameLike: '',
        provinceId: '',
        cityId: ''
      }
      // 清空城市列表数据
      this.$store.commit('SET_CITY_LIST', [])
      this.$emit('reset', this.searchForm)
    },
    // 省份变化
    async handleProvinceChange(value) {
      this.searchForm.cityId = ''
      if (value) {
        // 根据省份获取城市列表
        await this.getCityList(value)
      } else {
        // 清空城市列表数据
        this.$store.commit('SET_CITY_LIST', [])
      }
    }
  }
}
</script>

<style scoped lang="scss">
.search-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
    border-radius: 8px 8px 0 0;
  }
  
  .search-form {
    .search-row {
      display: flex;
      align-items: flex-end;
      
      .search-item {
        flex: 1;
        min-width: 0;
        display: flex;
        
        ::v-deep .el-form-item {
          margin-bottom: 0;
          display: flex;
          align-items: center;
        }
        
        ::v-deep .el-form-item__label {
          width: 82px !important;
          text-align: right !important;
          padding-right: 12px !important;
          font-weight: 500 !important;
          color: #303133 !important;
          font-size: 14px !important;
          flex-shrink: 0 !important;
          line-height: 32px !important;
        }
        
        ::v-deep .el-form-item__content {
          flex: 1;
          margin-left: 0;
          line-height: 32px;
        }
        
        ::v-deep .el-select {
          width: 100%;
        }
        
        ::v-deep .el-date-editor {
          width: 100%;
        }
        
        .search-date-picker {
          flex: 1;
        }
      }
      
      .button-group {
        text-align: right;
      }
    }
  }

  .table-btn {
    padding: 10px;
    text-align: right;
  }
}
</style>
