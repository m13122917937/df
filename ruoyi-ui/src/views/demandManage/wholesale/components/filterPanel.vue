<template>
  <div class="tree-panel">
    <div class="tree-header">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" :stroke="'var(--primary-color)'" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
        <circle cx="12" cy="10" r="3"/>
      </svg>
      <span>{{ title }}</span>
    </div>
    <div class="tree-list">
      <div
        v-for="(item, index) in showProvince"
        :key="index"
        class="tree-item"
        :class="{ active: currentValue === item.province }"
        @click="handleItemChange(item.province)"
      >
        <div class="tree-indicator"></div>
        <span class="tree-label">{{ item.provinceName }}</span>
        <span class="tree-badge">{{ item.sum }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { getProvinceCountApi } from '@/api/wholesale'
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'FilterPanel',
  props: {
    title: { type: String, default: '收货地点' },
    status: { type: Number, required: true },
    currentValue: { type: [String, Number], default: null },
    itemIcon: { type: String, default: 'el-icon-location-information' },
    currentBrand: { type: [String, Number], default: '' }
  },
  computed: {
    ...mapGetters(['provinceList']),
    provinceData() {
      return this.provinceList || []
    }
  },
  data() {
    return {
      data: [],
      showProvince: [],
      loading: false
    }
  },
  async mounted() {
    await this.getProvinceList()
    this.getProvinceData()
  },
  watch: {
    status() { this.getProvinceData() },
    currentBrand() { this.getProvinceData() }
  },
  methods: {
    ...mapActions(['GET_PROVINCE_LIST']),
    async getProvinceList() {
      await this.GET_PROVINCE_LIST()
    },
    async getProvinceData() {
      this.loading = true
      try {
        const params = { status: this.status, brand: this.currentBrand }
        const res = await getProvinceCountApi(params)
        if (res && res.code === 200) {
          const provinceList = res.data || []
          this.data = [
            { province: '', provinceName: '全部地区', sum: provinceList.reduce((total, item) => total + (item.sum || 0), 0) },
            ...provinceList.map(item => ({
              province: item.province,
              provinceName: item.provinceName,
              sum: item.sum || 0
            }))
          ]
          this.initData()
        } else {
          this.$message.error(res?.msg || '获取地区数据失败')
        }
      } catch (error) {
        console.error('获取地区数据失败:', error)
        this.$message.error('获取地区数据失败')
      } finally {
        this.loading = false
      }
    },
    initData() {
      if (this.provinceData && this.provinceData.length > 0) {
        const dataMap = new Map();
        this.data.forEach(item => {
          if (item.province) {
            dataMap.set(item.province, item);
          }
        });
        const mergedData = this.provinceData.map(item => {
          const provinceCode = item.districtId;
          const provinceName = item.district;
          const existingData = dataMap.get(provinceCode);
          return {
            province: provinceCode,
            provinceName: provinceName,
            sum: existingData ? existingData.sum : 0
          };
        });
        const totalSum = mergedData.reduce((total, item) => total + (item.sum || 0), 0);
        mergedData.sort((a, b) => (b.sum || 0) - (a.sum || 0));
        this.showProvince = [
          { province: '', provinceName: '全部地区', sum: totalSum },
          ...mergedData
        ];
      }
    },
    handleItemChange(value) {
      this.$emit('change', value)
    }
  }
}
</script>

<style scoped lang="scss">
.tree-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.tree-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  font-weight: 600;
  color: var(--adm-text-primary);
  padding: 8px 12px 12px;
  border-bottom: 1px solid var(--adm-border);
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.tree-list {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}

.tree-list::-webkit-scrollbar { width: 4px; }
.tree-list::-webkit-scrollbar-track { background: transparent; }
.tree-list::-webkit-scrollbar-thumb { background: var(--adm-text-disabled); border-radius: 2px; }
.tree-list::-webkit-scrollbar-thumb:hover { background: var(--adm-text-tertiary); }

.tree-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 12px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 1px;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  color: var(--adm-text-secondary);
  font-size: 13px;
  position: relative;
  flex-shrink: 0;

  &:hover {
    background: var(--bg-hover);
    color: var(--adm-text-primary);
  }

  &.active {
    background: var(--bg-active);
    color: var(--color-primary);
    font-weight: 500;
  }
}

.tree-indicator {
  width: 3px;
  height: 16px;
  border-radius: 2px;
  flex-shrink: 0;
  background: transparent;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);

  .tree-item.active & {
    background: var(--primary-color);
  }
}

.tree-label {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tree-badge {
  font-size: 11px;
  font-weight: 500;
  min-width: 20px;
  text-align: center;
  color: var(--adm-text-tertiary);
  background: var(--bg-hover);
  border-radius: 4px;
  padding: 1px 5px;
  flex-shrink: 0;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);

  .tree-item.active & {
    background: rgba(var(--primary-rgb), 0.12);
    color: var(--color-primary);
  }
}
</style>
