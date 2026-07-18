<template>
  <div class="region-sidebar">
    <div class="region-header">
      <i class="el-icon-location-outline" />
      <span>收货地点</span>
    </div>
    <div class="region-list">
      <div
        v-for="region in regions"
        :key="region.code"
        :class="['region-item', { active: selectedRegion === region.code }]"
        @click="selectRegion(region.code)"
      >
        <el-checkbox
          :value="selectedRegion === region.code"
          @change="selectRegion(region.code)"
        >
          {{ region.name }}({{ region.count }})
        </el-checkbox>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RegionSidebar',
  data() {
    return {
      selectedRegion: 'all',
      regions: [
        { code: 'all', name: '全国', count: 0 },
        { code: 'beijing', name: '北京', count: 0 },
        { code: 'tianjin', name: '天津', count: 0 },
        { code: 'hebei', name: '河北', count: 0 },
        { code: 'shanxi', name: '山西', count: 0 },
        { code: 'neimenggu', name: '内蒙古', count: 0 },
        { code: 'liaoning', name: '辽宁', count: 0 },
        { code: 'jilin', name: '吉林', count: 0 },
        { code: 'heilongjiang', name: '黑龙江', count: 0 },
        { code: 'shanghai', name: '上海', count: 0 },
        { code: 'jiangsu', name: '江苏', count: 0 },
        { code: 'zhejiang', name: '浙江', count: 0 },
        { code: 'anhui', name: '安徽', count: 0 },
        { code: 'fujian', name: '福建', count: 0 },
        { code: 'jiangxi', name: '江西', count: 0 },
        { code: 'shandong', name: '山东', count: 0 },
        { code: 'henan', name: '河南', count: 0 },
        { code: 'hubei', name: '湖北', count: 0 },
        { code: 'hunan', name: '湖南', count: 0 },
        { code: 'guangdong', name: '广东', count: 0 },
        { code: 'guangxi', name: '广西', count: 0 },
        { code: 'hainan', name: '海南', count: 0 },
        { code: 'chongqing', name: '重庆', count: 0 },
        { code: 'sichuan', name: '四川', count: 0 }
      ]
    }
  },
  methods: {
    selectRegion(code) {
      this.selectedRegion = code
      this.$emit('region-change', code)
    }
  }
}
</script>

<style lang="scss" scoped>
.region-sidebar {
  width: 180px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  height: calc(100vh - 240px);
  overflow-y: auto;

  .region-header {
    padding: 15px;
    background: #f5f5f5;
    border-bottom: 1px solid #e6e6e6;
    font-weight: bold;
    color: #333;

    i {
      margin-right: 8px;
      color: #e74c3c;
    }
  }

  .region-list {
    .region-item {
      padding: 8px 15px;
      cursor: pointer;
      border-bottom: 1px solid #f0f0f0;

      &:hover {
        background: #f8f9fa;
      }

      &.active {
        background: #e3f2fd;

        .el-checkbox {
          color: #1976d2;
        }
      }

      .el-checkbox {
        width: 100%;

        ::v-deep .el-checkbox__label {
          color: #333;
          font-size: 14px;
        }

        ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
          background-color: #e74c3c;
          border-color: #e74c3c;
        }
      }
    }

    .region-item:first-child {
      .el-checkbox ::v-deep .el-checkbox__label {
        color: #e74c3c;
        font-weight: bold;
      }
    }
  }
}
</style>
