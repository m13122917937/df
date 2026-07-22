<template>
  <div class="filter-panel">
    <div class="filter-title">
      <i class="el-icon-location-outline" />
      <span>{{ title }}</span>
    </div>
    <div class="location-list">
      <div
        v-for="(item, index) in showProvince"
        :key="index"
        class="location-item"
        :class="{ active: currentValue === item.province }"
        @click="handleItemChange(item.province)"
      >
        <span class="location-name">{{ item.provinceName }}</span>
        <el-tag size="mini" type="info" class="location-count">{{ item.sum }}</el-tag>
      </div>
    </div>
  </div>
</template>

<script>
import { getProvinceCountApi } from '@/api/order'
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'FilterPanel',
  props: {
    // 面板标题
    title: {
      type: String,
      default: '收货地点'
    },
    status: {
      type: Number,
      required: true
    },
    // 当前选中的值
    currentValue: {
      type: [String, Number],
      default: null
    },
    // 图标类名
    itemIcon: {
      type: String,
      default: 'el-icon-location-information'
    },
    // 当前选中的品牌
    currentBrand: {
      type: [String, Number],
      default: ''
    }
  },
  data() {
    return {
      data: [],
      showProvince: [],
      loading: false
    }
  },

  computed: {
    ...mapGetters(['provinceList']),
    // 从Vuex获取省份列表
    provinceData() {
      return this.provinceList || []
    }
  },

  watch: {
    status() {
      this.getProvinceData()
    },
    currentBrand() {
      this.getProvinceData()
    }
  },

  async mounted() {
    await this.loadProvinceList()
    this.getProvinceData()
  },

  methods: {
    ...mapActions('province', ['getProvinceList']),
    async loadProvinceList() {
      // 确保Vuex中有省份数据
      await this.getProvinceList()
    },
    // 获取省份数据
    async getProvinceData() {
      this.loading = true
      try {
        const params = {
          status: this.status,
          brand: this.currentBrand
        }

        const res = await getProvinceCountApi(params)
        if (res && res.code === 200) {
          // 处理省份数据
          const provinceList = res.data || []

          // 添加"全部"选项
          this.data = [
            { province: '', provinceName: '全部地区', sum: provinceList.reduce((total, item) => total + (item.sum || 0), 0) },
            ...provinceList.map(item => ({
              province: item.province,
              provinceName: item.provinceName,
              sum: item.sum || 0
            }))
          ]

          // 调用initData进行数据拼接和排序
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
      // 将provinceList和data数据拼接，根据数量从多到少排序
      if (this.provinceData && this.provinceData.length > 0) {
        // 创建一个映射，用于快速查找data中已存在的省份
        const dataMap = new Map()
        this.data.forEach(item => {
          if (item.province) {
            dataMap.set(item.province, item)
          }
        })

        // 合并provinceList和data，确保所有省份都显示
        const mergedData = this.provinceData.map(item => {
          // getProvinceListApi返回的数据结构是 { districtId, district }
          const provinceCode = item.districtId
          const provinceName = item.district
          const existingData = dataMap.get(provinceCode)
          return {
            province: provinceCode,
            provinceName: provinceName,
            sum: existingData ? existingData.sum : 0
          }
        })

        // 添加"全部"选项
        const totalSum = mergedData.reduce((total, item) => total + (item.sum || 0), 0)
        const allOption = {
          province: '',
          provinceName: '全部地区',
          sum: totalSum
        }

        // 按数量从多到少排序
        mergedData.sort((a, b) => (b.sum || 0) - (a.sum || 0))

        // 更新data，包含"全部"选项和排序后的省份列表
        this.showProvince = [allOption, ...mergedData]
      }
    },
    // 选项变化
    handleItemChange(value) {
      this.$emit('change', value)
    }
  }
}
</script>

<style scoped>
.filter-panel {
  width: 180px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 210px);
  overflow-y: auto;
}

.filter-title {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.filter-title i {
  margin-right: 6px;
  color: #1677FF;
  font-size: 16px;
}

.location-list {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow-y: auto;
  padding-right: 3px;
}

/* 自定义滚动条样式 */
.location-list::-webkit-scrollbar {
  width: 4px;
}

.location-list::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 2px;
}

.location-list::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #1677FF, #3395FF);
  border-radius: 2px;
}

.location-list::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #3395FF, #1677FF);
}

.location-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 3px;
  transition: all 0.3s ease;
  color: #606266;
  flex-shrink: 0;
  position: relative;
}

.location-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 2px;
  background: #1677FF;
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.location-item:hover::before {
  transform: scaleY(1);
}

.location-item:hover {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  color: #1677FF;
  transform: translateX(3px);
}

.location-item.active {
  background: linear-gradient(135deg, #1677FF 0%, #3395FF 100%);
  color: white;
  transform: translateX(3px);
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.3);
}

.location-name {
  flex: 1;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.location-count {
  margin-left: 6px;
  font-size: 11px;
  min-width: 20px;
  text-align: center;
}

.location-item.active .location-count {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-panel {
    width: 100%;
    margin-bottom: 12px;
  }
}
</style>
