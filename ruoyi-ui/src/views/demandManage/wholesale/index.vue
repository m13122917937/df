<template>
  <div class="wholesale-container">
    <!-- 顶部状态栏 -->
    <div class="status-bar">
      <div
        v-for="(config, statusKey) in statusConfig"
        :key="statusKey"
        class="status-item"
        :class="{ active: currentStatus === statusKey }"
        @click="handleStatusChange(statusKey)"
      >
        <div class="status-content">
          <div class="status-icon">
            <i :class="config.icon"></i>
          </div>
          <span class="status-label">{{ config.label }}</span>
        </div>
        <span class="status-count">{{ getStatusCount(statusKey) }}</span>
      </div>
    </div>
    <!-- 子页面内容区域 -->
    <div class="main-content">
      <component :is="currentPageComponent" />
    </div>
  </div>
</template>

<script>
import { getCountHeaderApi } from '@/api/wholesale'
export default {
  name: 'WholesaleIndex',
  data() {
    return {
      currentStatus: 'creatingOrders',
      currentBrand: 'all',
      countHeader: [],
      // 状态映射配置
      statusConfig: {
        'creatingOrders': { status: '1', label: '新建中', icon: 'el-icon-edit-outline' },
        'pendingOrders': { status: '2', label: '待发布', icon: 'el-icon-time' },
        'quotingOrders': { status: '3', label: '报价中', icon: 'el-icon-money' },
        'shippingOrders': { status: '4', label: '发货中', icon: 'el-icon-truck' },
        'shippedOrders': { status: '5', label: '当日发货', icon: 'el-icon-s-promotion' },
        'transitOrders': { status: '6', label: '在途', icon: 'el-icon-location' },
        'returnOrders': { status: '7', label: '追单', icon: 'el-icon-warning' },
        'abnormalOrders': { status: '8', label: '异常订单', icon: 'el-icon-refresh-left' },
        'confirmedOrders': { status: '10', label: '确认收货', icon: 'el-icon-circle-check' },
        'cancelledOrders': { status: '11', label: '撤销', icon: 'el-icon-close' }
      }
    }
  },
  computed: {
    // 根据当前状态动态加载对应的子页面组件
    currentPageComponent() {
      const pageMap = {
        'creatingOrders': () => import('./pages/creatingOrders/index'),
        'pendingOrders': () => import('./pages/pendingOrders/index'),
        'quotingOrders': () => import('./pages/quotingOrders/index'),
        'shippingOrders': () => import('./pages/shippingOrders/index'),
        'shippedOrders': () => import('./pages/shippedOrders/index'),
        'transitOrders': () => import('./pages/transitOrders/index'),
        'returnOrders': () => import('./pages/returnOrders/index'),
        'abnormalOrders': () => import('./pages/abnormalOrders/index'),
        'confirmedOrders': () => import('./pages/confirmedOrders/index'),
        'cancelledOrders': () => import('./pages/cancelledOrders/index'),
        'ruleList': () => import('./pages/ruleList/index'),
      }
      return pageMap[this.currentStatus] || null
    },
    // 根据状态获取对应的数量
    getStatusCount() {
      return (statusKey) => {
        const statusInfo = this.statusConfig[statusKey]
        if (!statusInfo || !this.countHeader || !Array.isArray(this.countHeader)) return 0

        const statusData = this.countHeader.find(item => item.status === statusInfo.status)
        return statusData ? statusData.count : 0
      }
    }
  },
  watch: {
    '$route'(to) {
      // 根据路由更新当前状态
      this.updateStatusFromRoute(to)
    }
  },
  created() {
    this.getCountHeader()
  },
  mounted() {
    // 初始化时根据路由设置状态
    this.updateStatusFromRoute(this.$route)
  },
  methods: {
    async getCountHeader() {
      try {
      const res = await getCountHeaderApi()
        //NEW(1, "新建中"),WAIT(2, "待发布"),TRADING(3, "报价中"),DELIVERY_ING(4, "发货中"),DELIVERY_END(5, "当日发货"),TRANSIT(6, "在途"),CHASE_ORDER(7, "追单"),ERROR(8, "异常订单"),RECEIPT(9, "待确定收货"),ENDING(10, "已完成"),REVOKE(11, "撤销");
      if (res && res.code === 200) {
          // 确保res.data是数组格式
          this.countHeader = Array.isArray(res.data) ? res.data : []
        }
      } catch (error) {
        console.error('获取状态统计数据失败:', error)
        this.countHeader = []
      }
    },
    handleStatusChange(status) {
      this.currentStatus = status
      // 用 query 参数切换状态，不改变路径，避免 TagsView 开新 tab
      this.$router.push({
        path: '/demandManage/wholesale',
        query: { status }
      })
    },
    handleBrandChange(brandId) {
      this.currentBrand = brandId
      // 这里可以添加品牌筛选逻辑
      console.log('选择品牌:', brandId)
    },
    updateStatusFromRoute(route) {
      // 从 query 参数中读取状态
      const status = route.query && route.query.status
      if (status && this.statusConfig[status]) {
        this.currentStatus = status
      }
    }
  }
}
</script>

<style scoped>
.wholesale-container {
  padding: 6px;
  position: relative;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 112px);
  box-sizing: border-box;
  overflow: hidden;
}

.wholesale-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="10" cy="60" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="90" cy="40" r="0.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  pointer-events: none;
}

/* 状态栏样式 */
.status-bar {
  display: flex;
  background: var(--bg-card);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  /* padding: 6px; */
  margin-bottom: 6px;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--adm-border);
  overflow-x: auto;
  gap: 20px;
}

.status-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 8px;
  min-width: 100px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.status-content {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.status-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.status-item:hover::before {
  left: 100%;
}

.status-item:hover {
  background: var(--menu-hover-bg);
  transform: translateY(-1px);
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.status-item.active {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4);
}

.status-icon {
  font-size: 16px;
  opacity: 0.8;
  display: flex;
  align-items: center;
}

.status-label {
  font-size: 12px;
  font-weight: 500;
  text-align: center;
  white-space: nowrap;
}

.status-count {
  font-size: 16px;
  font-weight: bold;
  text-align: center;
}

/* 品牌筛选样式 */
.brand-filter {
  background: var(--bg-card);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--adm-border);
}

.brand-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
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

/* 主内容区域 */
.main-content {
  background: var(--bg-card);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--adm-border);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .status-bar {
    overflow-x: auto;
    padding: 10px;
  }

  .status-item {
    min-width: 70px;
    padding: 6px 10px;
  }

  .brand-buttons {
    justify-content: flex-start;
  }

  .action-bar {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .pagination-container {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 768px) {
  .main-content {
    min-height: 300px;
  }
}
</style>