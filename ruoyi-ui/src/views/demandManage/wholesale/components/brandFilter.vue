<template>
  <div class="brand-filter">
    <div class="brand-chips">
      <button
        v-for="item in brandData"
        :key="item.brand"
        class="brand-chip"
        :class="{ active: currentBrand === item.brand }"
        @click="handleBrandChange(item.brand)"
      >
        <svg v-if="item.brand" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="7" height="7" rx="1"/>
          <rect x="14" y="3" width="7" height="7" rx="1"/>
          <rect x="3" y="14" width="7" height="7" rx="1"/>
          <rect x="14" y="14" width="7" height="7" rx="1"/>
        </svg>
        <span>{{ item.brand || '全部品牌' }}</span>
        <span class="chip-count">{{ item.sum }}</span>
      </button>
    </div>
  </div>
</template>

<script>
import { getBrandCountApi } from '@/api/wholesale'
export default {
  name: 'BrandFilter',
  props: {
    status: { type: Number, required: true },
    currentBrand: { type: [String, Number], default: '' },
    provinceId: { type: [String, Number], default: '' }
  },
  data() {
    return {
      brandData: [],
      loading: false
    }
  },
  mounted() {
    this.getBrandData()
  },
  watch: {
    status() { this.getBrandData() },
    provinceId() { this.getBrandData() }
  },
  methods: {
    async getBrandData() {
      this.loading = true
      try {
        const params = { status: this.status, province: this.provinceId }
        const res = await getBrandCountApi(params)
        if (res && res.code === 200) {
          const brandList = res.data || []
          const totalSum = brandList.reduce((total, item) => total + (item.sum || 0), 0)
          const sortedBrandList = brandList
            .map(item => ({ brand: item.brand, sum: item.sum || 0 }))
            .sort((a, b) => b.sum - a.sum)
          this.brandData = [
            { brand: '', sum: totalSum },
            ...sortedBrandList
          ]
        } else {
          this.$message.error(res?.msg || '获取品牌数据失败')
        }
      } catch (error) {
        console.error('获取品牌数据失败:', error)
        this.$message.error('获取品牌数据失败')
      } finally {
        this.loading = false
      }
    },
    handleBrandChange(brandId) {
      this.$emit('brand-change', brandId)
    }
  }
}
</script>

<style scoped lang="scss">
.brand-filter {
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow: var(--shadow-card);
  padding: 8px 12px;
}

.brand-chips {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  gap: 6px;
  &::-webkit-scrollbar { display: none; }
}

.brand-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 14px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
  color: var(--adm-text-secondary);
  background: transparent;
  border: 1px solid var(--adm-border);
  cursor: pointer;
  transition: all 180ms cubic-bezier(0.25, 0.1, 0.25, 1);
  white-space: nowrap;
  font-family: inherit;
  flex-shrink: 0;

  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
    background: rgba(var(--primary-rgb), 0.04);
  }

  &.active {
    border-color: var(--color-primary);
    color: var(--color-primary);
    background: rgba(var(--primary-rgb), 0.08);
  }

  .chip-count {
    font-size: 11px;
    font-weight: 600;
    color: var(--adm-text-tertiary);
    padding: 1px 6px;
    border-radius: 4px;
    background: var(--bg-hover);
  }

  &.active .chip-count {
    background: rgba(var(--primary-rgb), 0.12);
    color: var(--color-primary);
  }
}
</style>
