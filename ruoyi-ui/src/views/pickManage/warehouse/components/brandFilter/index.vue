<template>
  <div class="brand-filter">
    <div v-loading="loading" class="brand-buttons">
      <el-button
        v-for="item in brandData"
        :key="item.brand"
        :type="currentBrand === item.brand ? 'primary' : 'default'"
        :disabled="loading"
        size="mini"
        class="brand-btn"
        @click="handleBrandChange(item.brand)"
      >
        <i v-if="item.brand !== ''" class="el-icon-goods" />
        {{ item.brand || '全部品牌' }}
        <el-tag size="mini" :type="currentBrand === item.brand ? 'info' : 'success'" class="count-tag">
          {{ item.sum }}
        </el-tag>
      </el-button>
    </div>
  </div>
</template>

<script>
// 在途订单页面
// 订单状态枚举：
// DELIVERY_ING(4, "发货中"),
// DELIVERY_END(5, "当日发货"),
// TRANSIT(6, "在途"),
// CHASE_ORDER(7, "追单"),
// ERROR(8, "异常订单"),
// RECEIPT(9, "待确定收货"),
// ENDING(10, "已完成"),
import { getPickBrandCount } from '@/api/pickManage'
export default {
  name: 'BrandFilter',
  props: {
    status: {
      type: [Array, Number],
      required: true
    },
    currentBrand: {
      type: [String, Number],
      default: ''
    },
    provinceId: {
      type: [String, Number],
      default: ''
    }
  },

  data() {
    return {
      brandData: [],
      loading: false
    }
  },

  watch: {
    status() {
      this.getBrandData()
    },
    provinceId() {
      this.getBrandData()
    }
  },
  mounted() {
    this.getBrandData()
  },

  methods: {
    /**
     * 获取品牌数据
     */
    async getBrandData() {
      this.loading = true
      try {
        const params = {
          province: this.provinceId
        }
        if ( Array.isArray(this.status)) {
          params.statusList = this.status
        } else {
          params.status = this.status
        }

        const res = await getPickBrandCount(params)
        if (res && res.code === 200) {
          // 处理品牌数据
          const brandList = res.data || []

          // 计算总数
          const totalSum = brandList.reduce((total, item) => total + (item.sum || 0), 0)

          // 添加"全部"选项
          this.brandData = [
            { brand: '', sum: totalSum },
            ...brandList.map(item => ({
              brand: item.brand,
              sum: item.sum || 0
            }))
          ]

        } else {
          console.error('获取品牌数据失败:', res?.msg)
          this.$message.error(res?.msg || '获取品牌数据失败')
          // 设置默认数据
          this.brandData = [{ brand: '', sum: 0 }]
        }
      } catch (error) {
        console.error('获取品牌数据异常:', error)
        this.$message.error('获取品牌数据失败，请检查网络连接')
        // 设置默认数据
        this.brandData = [{ brand: '', sum: 0 }]
      } finally {
        this.loading = false
      }
    },

    /**
     * 品牌选择变化
     */
    handleBrandChange(brandId) {
      // 兼容两种事件名称
      this.$emit('brand-change', brandId)
      this.$emit('change', brandId)
    }
  }
}
</script>

<style scoped>
.brand-filter {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 8px;
  margin-bottom: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 6px 25px rgba(0, 0, 0, 0.12);
  }
}

.brand-buttons {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  gap: 8px;
}

.brand-btn {
  border-radius: 16px;
  padding: 6px 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  font-size: 12px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.5s;
  }

  &:hover::before {
    left: 100%;
  }
}

.brand-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.brand-btn.is-primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  border-color: var(--primary-color);
  color: white;

  &:hover {
    background: linear-gradient(135deg, var(--primary-hover) 0%, var(--primary-color) 100%);
  }
}

.brand-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

.count-tag {
  margin-left: 4px;
  border-radius: 8px;
  font-size: 10px;
}
</style>
