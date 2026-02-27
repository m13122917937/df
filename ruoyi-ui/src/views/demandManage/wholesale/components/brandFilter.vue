<template>
  <div class="brand-filter">
    <div class="brand-buttons">
      <el-button
        v-for="item in brandData"
        :key="item.brand"
        :type="currentBrand === item.brand ? 'primary' : 'default'"
        @click="handleBrandChange(item.brand)"
        size="mini"
        class="brand-btn"
      >
        <i class="el-icon-goods" v-if="item.brand !== ''"></i>
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
  // NEW(1, "新建中"),
  // WAIT(2, "待发布"),
  // TRADING(3, "报价中"),
  // DELIVERY_ING(4, "发货中"),
  // DELIVERY_END(5, "当日发货"),
  // TRANSIT(6, "在途"),
  // CHASE_ORDER(7, "追单"),
  // ERROR(8, "异常订单"),
  // ENDING(9, "已完成"),
  // REVOKE(10, "撤销")
import { getBrandCountApi } from '@/api/wholesale'
export default {
  name: 'BrandFilter',
  props: {
    status: {
      type: Number,
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
  mounted() {
    this.getBrandData()
  },
  
  watch: {
    status() {
      this.getBrandData()
    },
    provinceId() {
      this.getBrandData()
    }
  },
  
  methods: {
    /**
     * 获取品牌数据
     */
    async getBrandData() {
      this.loading = true
      try {
        const params = {
          status: this.status,
          province: this.provinceId
        }
        
        const res = await getBrandCountApi(params)
        if (res && res.code === 200) {
          // 处理品牌数据
          const brandList = res.data || []
          
          // 计算总数量
          const totalSum = brandList.reduce((total, item) => total + (item.sum || 0), 0)
          
          // 将品牌数据转换为数组并按数量降序排序
          const sortedBrandList = brandList
            .map(item => ({
              brand: item.brand,
              sum: item.sum || 0
            }))
            .sort((a, b) => b.sum - a.sum) // 按数量降序排序
          
          // 添加"全部"选项到最前面
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
    
    /**
     * 品牌选择变化
     */
    handleBrandChange(brandId) {
      this.$emit('brand-change', brandId)
    }
  }
}
</script>

<style scoped>
.brand-filter {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 6px;
  margin-bottom: 6px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
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
  padding: 4px 10px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  font-size: 12px;
}

.brand-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.brand-btn.is-primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-color: #409eff;
}

.count-tag {
  margin-left: 4px;
  border-radius: 8px;
  font-size: 10px;
}
</style>
