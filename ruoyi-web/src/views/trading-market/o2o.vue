<template>
  <div class="o2o-page">
    <!-- 顶部导航标签 -->
    <div class="page-layout">
      <!-- 左侧收货地点筛选区域 -->
      <div class="filter-sidebar">
        <div class="filter-title">
          <i class="el-icon-location" />
          <span>收货地点</span>
        </div>
        <div class="location-tree">
          <el-tree
            :data="locationTreeData"
            :props="locationTreeProps"
            node-key="district"
            class="location-tree-component"
            :default-expand-all="false"
            :expand-on-click-node="false"
            :highlight-current="true"
            @node-click="handleLocationClick"
          >
            <div
              slot-scope="{ node, data }"
              class="custom-tree-node"
              :class="{
                'custom-tree-node-active' : data.district === searchParams.city || data.district === searchParams.province
              }"
            >
              <div class="node-content">
                <div class="node-label">{{ node.label }}</div>
                <div class="node-right">
                  <span class="node-count">({{ data.quantity }})</span>
                  <div class="select-active">
                    <i v-if="data.district === searchParams.city || data.district === searchParams.province" class="el-icon-success" />
                    <i v-else style="opacity: 0.1; color: #000000;" class="el-icon-success" />

                  </div>
                </div>

              </div>
            </div>
          </el-tree>
        </div>
      </div>

      <!-- 中间主内容区域 -->
      <div class="o2o-content">
        <!-- 顶部筛选器 -->
        <div class="content-filters">
          <!-- 品牌筛选 -->
          <div class="filter-group">
            <span class="filter-label">品牌：</span>
            <div class="filter-options">
              <span
                v-for="brand in brands"
                :key="brand.id"
                :class="['filter-option', { active: searchParams.tabName == brand.dictValue }]"
                @click="selectBrand(brand)"
              >
                <i class="el-icon-collection brand-icon" />
                <span class="brand-text">{{ brand.dictLabel }}</span>
                <span class="brand-count">({{ brand.num > 99 ? '99+' : brand.num }})</span>
              </span>
            </div>
          </div>

          <!-- SKU搜索框 -->
          <div v-if="showSkuCards" class="filter-group search-group">
            <span class="filter-label">搜索：</span>
            <div class="search-container">
              <el-input
                v-model.trim="searchKeyword"
                placeholder="请输入产品名称进行搜索"
                prefix-icon="el-icon-search"
                clearable
                size="small"
                class="sku-search-input"
                @keyup.enter.native="handleSearch"
                @clear="handleSearchClear"
              />
              <el-button style="margin-left: 10px;" size="mini" @click="handleSearch">搜索</el-button>
            </div>
          </div>
        </div>
        <!-- 表格和树形结构布局 -->
        <div v-if="showProductTable" class="table-layout">
          <div class="product-tree-sidebar">
            <div class="tree-header">
              <span class="tree-title">{{ searchParams.productName }}</span>
            </div>
            <div class="tree-content">
              <div class="product-sku-list">
                <div
                  v-for="item in productSkuList"
                  :key="item.skuName"
                  :class="['sku-item', { 'selected': selectedSku === item.skuName }]"
                  @click="selectSku(item)"
                >
                  <span class="sku-name">{{ item.skuName }}</span>
                  <span class="sku-quantity">({{ item.quantity > 10 ? '10+' : item.quantity }})</span>
                  <i v-if="selectedSku === item.skuName" class="el-icon-check selection-icon" />
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧产品表格 -->
          <div class="product-table-container">
            <!-- 返回按钮 -->
            <div class="table-header">
              <el-button type="text" icon="el-icon-arrow-left" class="back-btn" @click="backToSkuCards">
                返回SKU卡片
              </el-button>
            </div>
            <!-- skuListTable 表格 -->
            <el-table
              ref="scrollTable"
              :data="skuListTable"
              size="small"
              :height="tableContainerHeight"
              border
              stripe
              class="product-table"
            >
              <el-table-column prop="orderStyle" label="订单类型" width="90">
                <template slot-scope="scope">
                  <OrderStyleBadge
                    :order-style="scope.row.orderStyle"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="productName" label="产品型号" min-width="200">
                <template slot-scope="scope">
                  <div class="product-name">{{ scope.row.productName }}</div>
                  <div class="product-sku">{{ scope.row.skuName }}</div>
                </template>
              </el-table-column>
              <el-table-column label="收货地" width="120">
                <template slot-scope="scope">
                  {{ scope.row.provinceName | setProvinceName }} {{ scope.row.cityName | setCityName }}
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="80" />
              <el-table-column prop="deliveryTime" label="发货时效" width="120" sortable>
                <template slot-scope="scope">
                  <span class="delivery-time-badge">{{ getDeliveryTimeText(scope.row.deliveryTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="抢单" min-width="400">
                <template slot-scope="scope">
                  <div class="bid-section">
                    <el-tooltip class="item" effect="dark" content="含税（型号对应）" placement="bottom">
                      <div class="ticket-icon">票</div>
                    </el-tooltip>
                    <div class="price-list">
                      <span
                        v-if="scope.row.priceHighest > 0"
                        class="price-item"
                        :class="{
                          'price-item-active': scope.row.priceHighestStatus == 1
                        }"
                        @click="handleBid(scope.row, scope.row.priceHighest)"
                      >
                        ¥{{ scope.row.priceHighest }}<i class="el-icon-ticket" />
                      </span>
                      <div v-if="scope.row.lastCompeteTime && scope.row.priceHighestStatus == 1 && shouldShowCountdown(scope.row, 'highest')" class="countdown-text">
                        <OptimizedCountdownText :data="scope.row" @countdown-end="handleCountdownEnd" />
                      </div>
                      <span
                        v-if="scope.row.priceHign > 0"
                        class="price-item"
                        :class="{
                          'price-item-active': scope.row.priceHignStatus == 1
                        }"
                        @click="handleBid(scope.row, scope.row.priceHign)"
                      >¥{{ scope.row.priceHign }}</span>
                      <div v-if="scope.row.lastCompeteTime && scope.row.priceHignStatus == 1 && shouldShowCountdown(scope.row, 'hign')" class="countdown-text">
                        <OptimizedCountdownText :data="scope.row" @countdown-end="handleCountdownEnd" />
                      </div>
                      <span
                        v-if="scope.row.priceLow > 0"
                        class="price-item"
                        :class="{
                          'price-item-active': scope.row.priceLowStatus == 1
                        }"
                        @click="handleBid(scope.row, scope.row.priceLow)"
                      >¥{{ scope.row.priceLow }}</span>
                      <div v-if="scope.row.lastCompeteTime && scope.row.priceLowStatus == 1 && shouldShowCountdown(scope.row, 'low')" class="countdown-text">
                        <OptimizedCountdownText :data="scope.row" @countdown-end="handleCountdownEnd" />
                      </div>
                      <span
                        v-if="scope.row.priceLowest > 0"
                        class="price-item last-price"
                        @click="handleBid(scope.row, scope.row.priceLowest)"
                      >¥{{ scope.row.priceLowest }}</span>
                    </div>

                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="accountingPeriod" label="账期" width="100">
                <template slot-scope="scope">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="`发货后的第${scope.row.accountingPeriod}天，且用户收货`"
                    placement="bottom"
                  >
                    <span class="credit-text">{{ getAccountingPeriodText(scope.row.accountingPeriod) }}</span>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column prop="otherRequire" label="质量要求" min-width="300">
                <template slot-scope="scope">
                  <div v-if="scope.row.otherRequire.length > 0">
                    <el-tag
                      v-for="value in scope.row.otherRequire"
                      :key="value"
                      class="other-require-tag"
                      size="mini"
                    >{{ value }}</el-tag>
                  </div>
                  <div v-else>
                    {{ '无特殊要求' }}
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 滚动加载状态 -->
            <div v-if="loading" class="loading-container">
              <i class="el-icon-loading" />
              <span>加载中...</span>
            </div>
            <div v-if="!hasMore && skuListTable.length > 0" class="no-more-container">
              <span>没有更多数据了</span>
            </div>
          </div>

        </div>
        <!-- sku卡片内容区域 -->

        <div v-if="showSkuCards" class="table-layout">
          <div class="sku-table-container">
            <!-- 搜索结果为空时的提示 -->
            <div v-if="productData.length === 0" class="empty-search-result">
              <div class="empty-icon">
                <i class="el-icon-search" />
              </div>
              <div class="empty-text">
                <p>未找到匹配的SKU</p>
                <p class="empty-tip">请尝试调整搜索关键词或品牌筛选条件</p>
              </div>
            </div>

            <!-- SKU卡片网格 -->
            <div v-else class="sku-cards-grid">
              <div v-for="card in productData" :key="card.id" class="sku-card" @click="handleSkuCardClick(card)">
                <div class="card-header">
                  <h4 class="product-name">
                    <div>{{ card.productName }}</div>
                    <div class="ticket-icon">
                      票
                    </div>
                  </h4>
                </div>
                <div class="card-body">
                  <div class="location-info">
                    <span class="location-icon">📍</span>
                    <span class="location-text">{{ getLocationInfo() }}</span>
                  </div>
                  <div class="quantity-section">
                    <span class="quantity-label">数量:</span>
                    <span class="quantity-value">{{ card.quantity > 10 ? '10+' : card.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 抢单弹窗 -->
    <GrabTheOrder ref="grabOrderRef" @confirm="handleGrabOrderConfirm" />
  </div>
</template>

<script>
import { apiGetBrandList, apiGetProvince, apiGetProduct, apiGetProductSku, apiGetProductSkuTable, apiGetProvinceList } from '@/api/tradingMarket'
import GrabTheOrder from './components/GrabTheOrder.vue'
import OptimizedCountdownText from './components/OptimizedCountdownText.vue'
import OrderStyleBadge from '@/components/OrderStyleBadge/index.vue'
export default {
  name: 'O2OPage',
  components: {
    GrabTheOrder,
    OptimizedCountdownText,
    OrderStyleBadge
  },
  filters: {
    setProvinceName(provinceName) {
      if (!provinceName) return ''
      return provinceName.substring(0, 2)
    },
    setCityName(cityName) {
      if (!cityName) return ''
      return cityName.substring(0, 2)
    }
  },
  data() {
    return {
      tableContainerHeight: 0,
      tableFlag: false,
      brands: [],
      skuList: [],
      searchParams: {
        city: '',
        productName: '',
        province: 0,
        skuName: '',
        tabName: ''
      },

      productSkuList: [], // 产品型号
      selectedSku: null, // 选中的产品型号
      locationTreeData: [],
      provinceList: [],
      locationTreeProps: {
        children: 'child',
        label: 'name'
      },
      productData: [],
      productTreeData: [],
      skuListTable: [],
      // SKU卡片数据
      skuCards: [],
      showSkuCards: true,
      showProductTable: false,
      // 滚动加载参数
      pagination: {
        pageNum: 1,
        pageSize: 20,
        total: 0
      },
      loading: false,
      hasMore: true,
      // 抢单弹窗相关
      grabOrderVisible: false,
      selectedProduct: null,
      // 滚动处理器
      scrollHandler: null,
      // 搜索关键词（仅用于搜索卡片列表，不更新productName）
      searchKeyword: ''
    }
  },
  watch: {
    'searchParams.productName'() {
      // 当 productName 变化时，刷新数量
      if (this.searchParams.tabName) {
        this.getProvince()
      }
    },
    'searchParams.skuName'() {
      // 当 skuName 变化时，刷新数量
      if (this.searchParams.tabName) {
        this.getProvince()
      }
    }
  },
  mounted() {
    this.getProvinceList()
    // 每日弹窗提示：提醒及时上传串码
    this.showDailyImeiReminder()
  },
  beforeDestroy() {
    // 清理所有定时器和事件监听器
    this.cleanup()
  },
  methods: {

    // 每日平台公告提醒
    showDailyImeiReminder() {
      const today = new Date().toDateString()
      const lastShowDate = localStorage.getItem('o2o_platform_notice_date')
      // 如果今天已经显示过，则不再显示
      if (lastShowDate === today) {
        return
      }
      // 显示弹窗提醒
      const now = new Date()
      const timeStr = `${now.getFullYear()}年${String(now.getMonth() + 1).padStart(2, '0')}月${String(now.getDate()).padStart(2, '0')}日 `

      // 打开弹窗后手动修改弹窗高度
      this.$nextTick(() => {
        const dialog = document.querySelector('.el-message-box')
        if (dialog) {
          dialog.style.width = '600px'
          dialog.style.maxWidth = '90vw'
          dialog.style.maxHeight = '90vh'
          const content = dialog.querySelector('.el-message-box__content')
          if (content) {
            content.style.maxHeight = '70vh'
            content.style.overflow = 'hidden'
          }
        }
      })

      this.$alert(
        `<div style="text-align: left; line-height: 1.8; width: 100%;">
          <div style="font-size: 18px; font-weight: 700; color: #FF9500; margin-bottom: 20px; text-align: center; padding-bottom: 12px; border-bottom: 2px solid #fbe6cd;">📢 無界电商平台重要公告</div>

          <div style="margin-bottom: 16px;">
            <div style="font-weight: 600; color: #FF3B30; margin-bottom: 8px; font-size: 15px;">🔴 合规经营提醒</div>
            <div style="color: #606266; font-size: 14px; background: #fef0f0; padding: 10px 14px; border-radius: 6px; border-left: 4px solid #FF3B30;">
              尊敬的供应商，为维护平台健康运营环境，严禁将已通过国家补贴渠道销售或备案的商品再次供应至本平台。如违反本规定，由此产生的一切法律后果及经济损失均由贵方自行承担。请您务必遵守相关法律法规，合规经营。
            </div>
          </div>

          <div style="margin-bottom: 16px;">
            <div style="font-weight: 600; color: #34C759; margin-bottom: 8px; font-size: 15px;">🟢 串码及时回传</div>
            <div style="color: #606266; font-size: 14px; background: #f0f9eb; padding: 10px 14px; border-radius: 6px; border-left: 4px solid #34C759;">
              请您在订单发货后及时回传相关串码信息。串码的及时性将直接影响您的回款效率，为保障您的资金周转顺畅，建议您在发货后 24 小时内完成串码上传操作。
            </div>
          </div>

          <div style="margin-bottom: 16px;">
            <div style="font-weight: 600; color: #1677FF; margin-bottom: 8px; font-size: 15px;">🔵 异常订单处理</div>
            <div style="color: #606266; font-size: 14px; background: rgba(37,99,255,0.04); padding: 10px 14px; border-radius: 6px; border-left: 4px solid #1677FF;">
              请您每日关注平台的异常订单，并及时进行处理。异常订单包括但不限于：订单取消、地址变更、商品破损、客户投诉等情况。逾期未处理的异常订单可能会影响您的回款时效和店铺评级，请您务必重视。
            </div>
          </div>

          <div style="margin-top: 20px; padding-top: 14px; border-top: 1px dashed #dcdfe6; text-align: right; color: #909399; font-size: 13px; background: #fafafa; border-radius: 6px;">
            <div>📅 公告时间：${timeStr}</div>
            <div style="margin-top: 4px;">👤 公告人：無界电商运营部</div>
          </div>
        </div>`,
        '平台温馨提示',
        {
          confirmButtonText: '我已知晓并遵守',
          type: 'warning',
          dangerouslyUseHTMLString: true,
          customClass: 'platform-notice-dialog'
        }
      ).then(() => {
        // 记录今天已显示
        localStorage.setItem('o2o_platform_notice_date', today)
      }).catch(() => {
        // 用户点击关闭也记录今天已显示
        localStorage.setItem('o2o_platform_notice_date', today)
      })
    },

    getProvinceList() {
      apiGetProvinceList().then(res => {
        const { data = [] } = res
        console.log('data', data)
        this.provinceList = data
        this.getBrandList()
      })
    },
    getProvince() {
      apiGetProvince({
        tabName: this.searchParams.tabName,
        productName: this.searchParams.productName,
        skuName: this.searchParams.skuName
      }).then((res) => {
        console.log('mergedDataRes', res)
        const { data = [] } = res

        // 结合全量省级数据补全
        const mergedData = this.mergeProvinceData(data)
        console.log('mergedData', mergedData)
        // 处理数据格式， 添加全国选项 quantity 是所有省份数量的总和 默认选中全国选项
        const totalQuantity = mergedData.reduce((acc, province) => acc + parseInt(province.quantity), 0)
        this.locationTreeData = [
          {
            district: 0,
            name: '全国',
            quantity: totalQuantity,
            child: [],
            selected: true
          },
          ...this.formatLocationData(mergedData)
        ]
      })
    },

    // 合并省级数据，用全量数据补全
    mergeProvinceData(apiData) {
      if (!this.provinceList || this.provinceList.length === 0) {
        return apiData
      }

      // 创建API数据的映射，以district为key
      const apiDataMap = new Map()
      apiData.forEach(item => {
        apiDataMap.set(item.district, item)
      })

      // 遍历全量省级数据，补全缺失的省份
      const mergedData = this.provinceList.map(province => {
        const apiItem = apiDataMap.get(province.districtId)
        console.log('apiItem', apiItem)
        if (apiItem) {
          // 如果API数据中存在，使用API数据（包含数量信息）
          return apiItem
        } else {
          // 如果API数据中不存在，使用全量数据但数量设为0
          return {
            district: province.districtId,
            name: province.district,
            quantity: 0,
            child: []
          }
        }
      })

      // 按数量降序排序
      return mergedData.sort((a, b) => {
        const quantityA = parseInt(a.quantity) || 0
        const quantityB = parseInt(b.quantity) || 0
        return quantityB - quantityA
      })
    },
    // 格式化地区数据
    formatLocationData(data) {
      return data.map(province => {
        // 对城市按数量排序
        const sortedCities = province.child ? province.child
          .map(city => ({
            district: city.district,
            name: city.cityName ? city.cityName.substring(0, 2) : '',
            quantity: this.formatQuantity(city.quantity),
            rawQuantity: parseInt(city.quantity) || 0
          }))
          .sort((a, b) => b.rawQuantity - a.rawQuantity)
          .map(city => ({
            district: city.district,
            name: city.name,
            quantity: city.quantity
          })) : []

        return {
          district: province.district,
          name: province.name ? province.name.substring(0, 2) : '',
          quantity: this.formatQuantity(province.quantity),
          child: sortedCities
        }
      })
    },
    // 格式化数量显示
    formatQuantity(quantity) {
      const num = parseInt(quantity)
      return num > 99 ? '99+' : num.toString()
    },
    getBrandList() {
      apiGetBrandList().then((res) => {
        const { data = [] } = res

        const processedBrands = data
          .map((brand) => ({
            ...brand,
            num: Number(brand.num) || 0
          }))
          .filter(brand => brand.num > 0)
          .sort((a, b) => b.num - a.num)

        this.brands = processedBrands
        this.searchParams.tabName = this.brands.length > 0 ? this.brands[0].dictValue : ''
        if (this.brands.length > 0) {
          this.getProvince()
          this.getProductSkuCard()
        }
      })
    },
    selectBrand(brand) {
      this.searchParams.productName = ''
      this.searchParams.skuName = ''
      this.searchKeyword = '' // 清空搜索关键词
      this.searchParams.tabName = brand.dictValue

      this.showSkuCards = true
      this.showProductTable = false
      this.getProvince()
      this.getProductSkuCard()
    },

    handleLocationClick(data) {
      console.log('Location clicked:', data)

      // 单选逻辑

      if (data.district === 0) {
        // 选择了全国选项
        this.searchParams.province = 0
        this.searchParams.city = ''
      } else {
        // 判断是省份还是城市
        const isProvince = this.locationTreeData.some(province =>
          province.district === data.district && province.child
        )
        if (isProvince) {
          // 选择了省份
          this.searchParams.province = data.district
          this.searchParams.city = ''
        } else {
          // 选择了城市，需要找到对应的省份
          const parentProvince = this.locationTreeData.find(province =>
            province.child && province.child.some(city => city.district === data.district)
          )
          if (parentProvince) {
            this.searchParams.province = parentProvince.district
            this.searchParams.city = data.district
          }
        }
      }

      // 触发数据刷新
      this.getProductSkuCard()
      if (this.showProductTable) {
        this.getProductSkuTable()
      }
    },

    // sku卡片数据
    getProductSkuCard() {
      apiGetProduct(this.searchParams).then(res => {
        const { data = [] } = res
        this.productData = data
      })
    },
    // 使用搜索关键词获取sku卡片数据（不更新searchParams.productName）
    getProductSkuCardWithKeyword() {
      const params = {
        ...this.searchParams,
        productName: this.searchKeyword // 使用searchKeyword作为临时搜索参数
      }
      apiGetProduct(params).then(res => {
        const { data = [] } = res
        this.productData = data
      })
    },
    // sku
    getProductSku() {
      apiGetProductSku(this.searchParams).then(res => {
        const { data = [] } = res
        this.productSkuList = data
        // 重置选择状态
        this.selectedSku = null
      })
    },
    //  sku table
    getProductSkuTable(isLoadMore) {
      console.log('isLoadMore', isLoadMore)
      if (this.loading) return

      this.loading = true
      const params = {
        ...this.searchParams,
        pageNum: this.pagination.pageNum,
        pageSize: 10
      }
      apiGetProductSkuTable(params).then(res => {
        const { rows = [], total = 0 } = res

        // 确保每一行都有 orderStyle 字段，默认 0（不展示）
        const normalizedRows = (rows || []).map(r => ({
          orderStyle: typeof r.orderStyle === 'number' ? r.orderStyle : (r.orderStyle ? Number(r.orderStyle) : 0),
          ...r
        }))

        if (isLoadMore) {
          // 追加数据
          this.skuListTable = [...this.skuListTable, ...normalizedRows]
          console.log('this.skuListTable', this.skuListTable)
        } else {
          // 重置数据
          this.skuListTable = normalizedRows
        }

        this.pagination.total = total
        console.log('this.skuListTable.length', this.skuListTable.length)
        console.log('total', total)

        this.hasMore = this.skuListTable.length < total
        this.loading = false

        // 数据加载完成后重新设置表格高度
      }).catch(() => {
        this.loading = false
      })
    },
    // 选择产品型号
    selectSku(item) {
      this.selectedSku = item.skuName
      this.searchParams.skuName = item.skuName
      // 选择型号后刷新产品数据
      this.resetPagination()
      this.getProductSkuTable()
    },

    // 重置分页
    resetPagination() {
      this.pagination.pageNum = 1
      this.hasMore = true
      this.skuListTable = []
    },

    // 判断是否为选中的省份
    isSelectedProvince(district) {
      return this.searchParams.province === district && this.searchParams.city === 0
    },

    // 判断是否为选中的城市
    isSelectedCity(district) {
      return this.searchParams.city === district
    },

    // 判断是否为选中城市的父省份
    isParentProvinceOfSelectedCity(district) {
      if (this.searchParams.city === 0) return false

      return this.locationTreeData.some(province =>
        province.district === district &&
        province.child &&
        province.child.some(city => city.district === this.searchParams.city)
      )
    },

    // 获取选中的省份名称
    getSelectedProvinceName() {
      if (this.searchParams.province === 0) return '全国'

      const province = this.locationTreeData.find(p => p.district === this.searchParams.province)
      return province ? province.name : ''
    },

    // 获取选中的城市名称
    getSelectedCityName() {
      if (this.searchParams.city === 0) return ''

      for (const province of this.locationTreeData) {
        if (province.child) {
          const city = province.child.find(c => c.district === this.searchParams.city)
          if (city) return city.name
        }
      }
      return ''
    },

    // 获取完整的地区信息
    getLocationInfo() {
      const provinceName = this.getSelectedProvinceName()
      const cityName = this.getSelectedCityName()
      if (!provinceName && !cityName) {
        return '全国'
      }
      if (cityName) {
        return `${provinceName} - ${cityName}`
      } else {
        return provinceName
      }
    },

    // 获取发货时效显示文本
    getDeliveryTimeText(deliveryTime) {
      const timeMap = {
        0: '当天',
        1: '次日',
        2: '后天'
      }
      return timeMap[deliveryTime] || '未知'
    },

    // 获取账期显示文本
    getAccountingPeriodText(accountingPeriod) {
      const periodMap = {
        0: 'T+0',
        1: 'T+1',
        2: 'T+2',
        3: 'T+3',
        4: 'T+4',
        5: 'T+5',
        6: 'T+6',
        7: 'T+7'
      }
      return periodMap[accountingPeriod] || '-'
    },

    // 处理倒计时结束事件
    handleCountdownEnd() {
      // 倒计时结束后的处理逻辑，比如刷新数据
      this.resetPagination()
      this.getProductSkuTable()
    },

    // 判断是否应该显示倒计时
    shouldShowCountdown(row, priceType) {
      if (!row.lastCompeteTime) return false

      const now = new Date()
      const lastTime = new Date(row.lastCompeteTime)
      const diffMs = now.getTime() - lastTime.getTime()
      const quotationInterval = row.quotationInterval

      let size = 0
      if (priceType === 'highest' && row.priceHighestStatus === 1) {
        size = 3
      } else if (priceType === 'hign' && row.priceHignStatus === 1) {
        size = 2
      } else if (priceType === 'low' && row.priceLowStatus === 1) {
        size = 1
      } else if (priceType === 'lowest' && row.priceLowestStatus === 1) {
        size = 0
      }

      const quotationTime = 1000 * 60 * quotationInterval * size
      const remainingMs = quotationTime - diffMs

      // 只有剩余时间大于0且小于5分钟才显示倒计时
      return remainingMs > 0
    },

    handleBid(product, price) {
      // 检查是否有倒计时时间，如果有剩余时间则不允许抢单
      if (product.lastCompeteTime) {
        const now = new Date()
        const lastTime = new Date(product.lastCompeteTime)
        const diffMs = now.getTime() - lastTime.getTime()
        const quotationInterval = product.quotationInterval

        let size = 0
        if (product.priceHighestStatus === 1) {
          size = 3
        } else if (product.priceHignStatus === 1) {
          size = 2
        } else if (product.priceLowStatus === 1) {
          size = 1
        } else if (product.priceLowestStatus === 1) {
          size = 0
        }

        const quotationTime = 1000 * 60 * quotationInterval * size
        const remainingMs = quotationTime - diffMs

        // 如果还有剩余时间，不允许抢单
        if (remainingMs < 0) {
          this.$message.warning('当前抢单时间已结束')
          return
        }
      }

      this.$refs.grabOrderRef.open(product, price)
    },
    // 点击sku 卡片
    handleSkuCardClick(card) {
      this.searchParams.productName = card.productName
      this.showSkuCards = false
      this.showProductTable = true
      this.searchParams.skuName = ''
      this.getProductSku()
      this.getProductSkuTable()
      this.resetPagination()

      // 切换显示后重新设置表格高度
      this.$nextTick(() => {
        this.setTableHeight()
      })
    },
    backToSkuCards() {
      this.showSkuCards = true
      this.showProductTable = false
      this.searchParams.skuName = ''
      this.searchParams.productName = ''
      this.searchKeyword = '' // 清空搜索关键词
      this.resetPagination()
      this.getProductSkuCard()
    },
    handleSearch() {
      this.searchParams.skuName = ''
      // 使用searchKeyword搜索，但不更新searchParams.productName
      this.getProductSkuCardWithKeyword()
    },
    handleSearchClear() {
      this.searchKeyword = ''
      this.getProductSkuCard()
    },

    // 确认并刷新表格
    handleGrabOrderConfirm() {
      setTimeout(() => {
        this.resetPagination()
        this.getProductSkuTable()
        // 刷新收货地点中的数量
        this.getProvince()
      }, 1000)
    },

    // 设置表格高度
    setTableHeight() {
      this.tableFlag = false
      this.$nextTick(() => {
        const tableContainer = document.querySelector('.product-table-container')
        if (tableContainer) {
          // 先移除旧的滚动监听器
          // this.removeTableScrollListener()

          // 获取页面总高度
          const windowHeight = window.innerHeight

          // 获取导航栏高度（如果有的话）
          const navHeight = 48 // 根据实际情况调整

          // 获取品牌筛选区域高度
          const filterHeight = document.querySelector('.content-filters')?.offsetHeight || 0

          // 获取返回按钮区域高度

          // 计算可用高度
          const availableHeight = windowHeight - navHeight - filterHeight - 100 // 40px 为额外边距

          // 设置表格容器高度
          this.tableContainerHeight = availableHeight + 'px'

          // 添加滚动监听器
          // this.addTableScrollListener()

          this.tableFlag = true

          const table = this.$refs.scrollTable.bodyWrapper
          this.scrollHandler = (e) => {
            const { scrollTop, clientHeight, scrollHeight } = e.target
            // 检查是否滚动到底部
            if (scrollHeight - scrollTop <= clientHeight) {
              if (!this.hasMore || this.loading) return
              this.pagination.pageNum += 1
              this.getProductSkuTable(true)
            }
          }
          table.addEventListener('scroll', this.scrollHandler)
        }
      })
    },

    // 清理资源
    cleanup() {
      // 清理滚动监听器
      if (this.scrollHandler && this.$refs.scrollTable && this.$refs.scrollTable.bodyWrapper) {
        this.$refs.scrollTable.bodyWrapper.removeEventListener('scroll', this.scrollHandler)
        this.scrollHandler = null
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.o2o-page {
  height: 100vh;
  background: radial-gradient(1200px 600px at 80% -10%, rgba(37,99,255,0.04) 0%, rgba(236,245,255,0) 60%),
              linear-gradient(180deg, #f9fbff 0%, #f7f8fa 100%);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  position: relative;
}

.o2o-page::before,
.o2o-page::after {
  content: '';
  position: fixed;
  pointer-events: none;
  z-index: 0;
  filter: blur(60px);
  opacity: 0.35;
}

.o2o-page::before {
  top: -120px;
  right: -120px;
  width: 360px;
  height: 360px;
  background: radial-gradient(closest-side, rgba(37,99,255,0.15), rgba(37,99,255,0));
}

.o2o-page::after {
  bottom: -140px;
  left: -120px;
  width: 420px;
  height: 420px;
  background: radial-gradient(closest-side, rgba(88,86,214,0.12), rgba(88,86,214,0));
}

// 去除水印，保持与订单管理页面一致

// 页头样式（统一到订单管理风格）
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px 0 16px;
  .header-left {
    display: flex;
    align-items: center;
  }
  .page-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    .el-icon-s-goods {
      background: linear-gradient(135deg, #3395FF, #1677FF);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }
  }
  .header-actions {
    display: flex;
    align-items: center;
    .action-btn {
      padding: 8px 14px;
      border-radius: 6px;
      font-weight: 500;
      transition: all .2s ease;
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.18);
      }
    }
  }
}

.top-navigation {
  background: #fff;
  border-bottom: 1px solid #e1e5e9;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;

  .nav-tabs {
    display: flex;
    gap: 0;

    .nav-tab {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 20px;
      cursor: pointer;
      border-radius: 0;
      transition: all 0.2s;
      position: relative;
      font-size: 14px;
      font-weight: 500;

      &:hover {
        background: #f8f9fa;
      }

      &.active {
        background: #1677FF;
        color: #fff;

        .tab-badge {
          background: rgba(255, 255, 255, 0.2);
          color: #fff;
        }
      }

      .tab-badge {
        font-size: 11px;
        padding: 2px 6px;
        border-radius: 8px;
        background: #1677FF;
        color: #fff;
        font-weight: 600;
        min-width: 20px;
        text-align: center;
      }
    }
  }

  .right-tabs {
    display: flex;
    gap: 0;

    .brand-section {
      display: flex;
      gap: 0;

      &:first-child {
        border-right: 1px solid #e1e5e9;
        padding-right: 16px;
        margin-right: 16px;
      }

      .tab-item {
        padding: 6px 12px;
        cursor: pointer;
        border-radius: 0;
        transition: all 0.2s;
        font-size: 14px;
        color: #495057;
        white-space: nowrap;

        &:hover {
          background: #f7f8fa;
          color: #1677FF;
        }

        &.active {
          background: #1677FF;
          color: #fff;
        }
      }
    }
  }
}

.page-layout {
  display: flex;
  position: relative;
  z-index: 2;
}

.filter-sidebar {
  width: 260px;
  background: #fff;
  border-right: 1px solid #e1e5e9;
  padding: 20px;
  box-shadow: 1px 0 3px rgba(0, 0, 0, 0.1);
  height: calc(100vh - 64px);

    .filter-title {
      display: flex;
      align-items: center;
      font-size: 15px;
      font-weight: 600;
      color: #212529;
      border-bottom: 1px solid #e9ecef;
      margin-bottom: 10px;
      height: 40px;
      i {
        margin-right: 8px;
        color: #666;
      }
    }

    .location-tree {
      height: calc(100% - 60px);
      overflow-y: scroll;
      /* 精致滚动条美化，仅作用于收货地点区域 */
      scrollbar-width: thin;
      scrollbar-color: #1677FF transparent;
      &::-webkit-scrollbar {
        width: 10px;
        height: 10px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
        border-radius: 10px;
      }
      &::-webkit-scrollbar-thumb {
        border-radius: 10px;
        background: #1677FF;
        border: 2px solid transparent;
        background-clip: padding-box;
        box-shadow: inset 0 0 0 1px rgba(24,144,255,0.25);
        transition: background .2s ease, box-shadow .2s ease;
      }
      &::-webkit-scrollbar-thumb:hover {
        background: #147ad6;
        box-shadow: inset 0 0 0 1px rgba(24,144,255,0.4);
      }
      &::-webkit-scrollbar-corner {
        background: transparent;
      }

      .location-tree-component {
        font-size: 13px;
        background: transparent;

        .el-tree-node {
          .el-tree-node__content {
            height: 28px;
            padding: 0 8px;
            padding-right: 18px !important;
            border-radius: 4px;
            margin: 2px 0;
            transition: all 0.2s;

            &:hover {
              background: #f8f9fa;
            }

            .el-checkbox {
              margin-right: 8px;
            }

            .el-tree-node__expand-icon {
              color: #6c757d;
              font-size: 12px;
              margin-right: 4px;
            }

            .custom-tree-node {
              display: flex;
              align-items: center;
              justify-content: space-between;
              width: 100%;
              flex: 1;

              .node-content {
                display: flex;
                align-items: center;
                flex: 1;

                .node-label {
                  font-size: 13px;
                  color: #495057;
                  flex: 1;
                }

                .node-count {
                  font-size: 12px;
                  color: #6c757d;
                  margin-left: 8px;
                  padding-right: 18px;
                }
                .node-right{
                  display: flex;
                  justify-content: flex-end;
                  align-items: center;
                }
                .select-active{
                  padding-right: 10px;
                  display: flex;
                  align-items: center;
                  flex-direction: column;
                  justify-content: flex-end;
                  width: 20px;
                  color: #1677FF !important;
                  font-size: 20px;
                }
              }
            }
          }

          .el-tree-node__children {
            padding-left: 20px;
          }
        }

      }

      ::v-deep .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
        background-color: #fff !important;
        border-radius: 4px;
      }
      .custom-tree-node{
        height: 100%;
        width: 100%;
      }
      .custom-tree-node-active {
        border-radius: 4px;

        .node-label {
          font-weight: bold;
          color: #1677FF !important;
        }

        .node-count {
          font-weight: bold;
          color: #1677FF !important;
        }

      }
    }

}

.o2o-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f8f9fa;
}

.content-filters {
  background: linear-gradient(#ffffff, #ffffff) padding-box,
              linear-gradient(135deg, #e9f3ff, #f3e9ff) border-box;
  padding: 16px 20px;
  border-radius: 6px;
  margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  border: 1px solid transparent;
  position: sticky;
  top: 8px;
  z-index: 3;
  backdrop-filter: saturate(1.1);
}

.filter-group {
  display: flex;
  align-items: center;

  .filter-label {
    font-weight: 500;
    color: #333;
    min-width: 60px;
  }

  .filter-options {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  .filter-option {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border: 1px solid #e4e7ed;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s;
    background: linear-gradient(180deg, #ffffff, #fafbfc);
    white-space: nowrap;
    max-width: none;

    &:hover {
      border-color: #1677FF;
      color: #1677FF;
      box-shadow: 0 6px 16px rgba(64, 158, 255, 0.12);
      transform: translateY(-1px);
    }

    &.active {
      background: linear-gradient(135deg, #3395FF, #1677FF);
      color: #fff;
      border-color: transparent;
      box-shadow: 0 12px 28px rgba(64, 158, 255, 0.24);
    }

    .brand-icon {
      font-size: 14px;
      color: #909399;
    }

    .brand-text {
      font-weight: 500;
    }

    .brand-count {
      color: #909399;
    }

    &:hover .brand-icon { color: #1677FF; }
    &.active .brand-icon,
    &.active .brand-count { color: #ffffff; }
  }
}

.search-group {
  margin-top: 16px;
  align-items: center;

  .search-container {
    flex: 1;
    max-width: 400px;
    display: flex;
    justify-content: flex-start;

    .sku-search-input {
      .el-input__inner {
        border-radius: 6px;
        border: 1px solid #ddd;
        transition: all 0.3s;

        &:focus {
          border-color: #1677FF;
          box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.12);
        }
      }

      .el-input__prefix {
        color: #909399;
      }
    }
  }
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;

  .filter-tip {
    color: #666;
    font-size: 14px;
  }
}

// 表格和树形结构布局
.table-layout {
  display: flex;
}

.product-tree-sidebar {
  width: 280px;
  background: linear-gradient(#ffffff, #ffffff) padding-box,
              linear-gradient(135deg, #e9f3ff, #f3e9ff) border-box;
  border-radius: 10px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
  border: 1px solid transparent;
  overflow: hidden;
  flex-shrink: 0;
  height: calc(100vh - 160px);
  margin-right: 20px;
  .tree-header {
  padding: 12px 16px;
  background: #f7f8fa;
  border-bottom: 1px solid #e4e7ed;
  position: sticky;
  top: 0;
  z-index: 2;

    .tree-title {
      font-size: 14px;
      font-weight: 600;
      color: #495057;
    }
  }
.tree-content {
    padding: 16px 8px;
    height: calc(100% - 48px);
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #d0d3d7 transparent;
    &::-webkit-scrollbar { width: 8px; height: 8px; }
    &::-webkit-scrollbar-thumb { background: #d0d3d7; border-radius: 8px; }
    &::-webkit-scrollbar-track { background: transparent; }

     .product-sku-list {
       padding: 0 10px;
     }
  }
}

.sku-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  margin-bottom: 5px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #fff;

  &:hover {
    border-color: #1677FF;
    background: #f5f9ff;
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(64,158,255,0.12);
  }

  &.selected {
    border-color: #1677FF;
    background: #1677FF;
    color: #fff;
    box-shadow: 0 6px 16px rgba(64,158,255,0.18);

    .sku-name,
    .sku-quantity {
      color: #fff;
    }

    .selection-icon {
      color: #fff;
    }
  }

  .sku-name {
    font-size: 13px;
    color: #495057;
    font-weight: 500;
    flex: 1;
  }

  .sku-quantity {
    font-size: 12px;
    color: #6c757d;
    margin-left: 8px;
  }

  .selection-icon {
    color: #1677FF;
    font-size: 14px;
    margin-left: 8px;
  }
}

.product-tree {
  .custom-tree-node {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;

    .node-label {
      font-size: 13px;
      color: #495057;
    }

    .node-count {
      font-size: 12px;
      color: #6c757d;
      margin-left: 8px;
    }

  }

  .el-tree-node__content {
    height: 32px;
    padding: 0 8px;

    &:hover {
      background: #f8f9fa;
    }
  }

  .el-tree-node.is-current>.el-tree-node__content {
    background: #1677FF;
    color: #fff;

    .node-label,
    .node-count {
      color: #fff;
    }
  }
}

 .product-table-container {
   flex: 1;
   background: linear-gradient(#ffffff, #ffffff) padding-box,
               linear-gradient(135deg, #e9f3ff, #f3e9ff) border-box;
   border-radius: 10px;
   box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
   overflow: hidden;
   border: 1px solid transparent;
   display: flex;
   flex-direction: column;
   height: calc(100vh - 160px);
  :deep(.el-table) {
    border-radius: 6px;
    overflow: hidden;
  }
 }

.table-header {
  padding: 12px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;

  .back-btn {
    color: #1677FF;
    font-size: 14px;
    padding: 0;

    &:hover {
      color: #3395FF;
    }

    i {
      margin-right: 4px;
    }
  }
}

 .product-table {
   font-size: 13px;
   flex: 1;
   display: flex;
   flex-direction: column;

   .el-table__header {
     flex-shrink: 0;
     th {
       background: #f7f8fa !important;
       color: #606266 !important;
       font-weight: 600 !important;
       font-size: 13px !important;
       border-bottom: 1px solid #ebeef5 !important;
     }
   }

   .el-table__body-wrapper {
     flex: 1;
     overflow-y: auto !important;
   }

   .el-table__body {
     td {
       border-bottom: 1px solid #f2f3f5 !important;
       color: #606266;
     }

     tr:hover td {
       background: #f7f8fa !important;
     }
   }

  .bid-section {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    gap: 8px;

    .ticket-icon {
      font-size: 12px;
      color: #1677FF;
      font-weight: bold;
      width: 24px;
      height: 24px;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 50%;
      border: 1px solid transparent;
      background: linear-gradient(#fff, #fff) padding-box,
                  linear-gradient(135deg, #3395FF, #5856D6) border-box;
      margin-right: 8px;
      cursor: pointer;
    }

    .price-list {
      display: flex;
      gap: 4px;
      flex-wrap: wrap;
      justify-content: center;
      cursor: pointer;

      .price-item {
        font-size: 12px;
        color: #606266;
        padding: 2px 6px;
        background: #f7f8fa;
        border-radius: 3px;
        border: 1px solid #e4e7ed;
        transition: all .15s ease;
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 10px rgba(31, 45, 61, 0.06);
        }

        &.last-price {
          color: #1677FF;
          font-weight: 500;
          background: #f5f9ff;
          border-color: #1677FF;
        }

      }
      .price-item-active{
        background: linear-gradient(135deg, #3395FF, #1677FF);
        border: 1px solid #1677FF;
        color: #fff;
        box-shadow: 0 12px 28px rgba(64, 158, 255, 0.26);
        animation: glowPulse 1.8s ease-in-out infinite alternate;
        &.last-price {
          color: #fff;
          font-weight: 500;
        }
      }
    }

  .countdown-info {
      font-size: 11px;
      color: #FF9500;
      background: #fdf6ec;
      padding: 2px 6px;
      border-radius: 3px;
      border: 1px solid #faecd8;
      text-align: center;
      margin-top: 4px;
    }

    .bid-btn {
      padding: 8px 12px;
      font-size: 11px;
      border-radius: 3px;

      &:disabled {
        background: #6c757d;
        border-color: #6c757d;
        color: #fff;
      }
    }
  }

  .delivery-time-badge {
    background: #ffc107;
    color: #856404;
    padding: 2px 6px;
    border-radius: 3px;
    font-size: 11px;
    font-weight: 500;
  }

  /* 订单类型图标签样式 */
  .order-style-badge {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: 1px solid #1677FF;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #1677FF;
    font-weight: 600;
    background: #fff;
    box-shadow: 0 2px 6px rgba(64,158,255,0.08);
  }

  .order-style-img {
    width: 20px;
    height: 20px;
    object-fit: contain;
    display: block;
  }

  .order-style-default-text {
    font-size: 12px;
    line-height: 1;
  }

  .credit-text {
    margin-right: 4px;
    font-weight: 500;
  }

  .credit-icon {
    color: #28a745;
    font-size: 12px;
  }
}

// 滚动加载样式
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  background: #fff;
  border-top: 1px solid #e9ecef;
  color: #666;
  font-size: 14px;

  i {
    margin-right: 8px;
    font-size: 16px;
    animation: rotating 2s linear infinite;
  }
}

.no-more-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  background: #fff;
  border-top: 1px solid #e9ecef;
  color: #999;
  font-size: 14px;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

// SKU卡片样式
.sku-table-container {
  flex: 1;
  background: linear-gradient(#ffffff, #ffffff) padding-box,
              linear-gradient(135deg, #e9f3ff, #f3e9ff) border-box;
  border-radius: 10px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
  border: 1px solid transparent;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 210px);
}

.empty-search-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  color: #6c757d;
  border-radius: 12px;
  background: linear-gradient(#ffffff, #ffffff) padding-box,
              linear-gradient(135deg, #e9f3ff, #f3e9ff) border-box;
  border: 1px solid transparent;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -40px;
    right: -60px;
    width: 180px;
    height: 180px;
    background: radial-gradient(closest-side, rgba(37,99,255,0.06), rgba(37,99,255,0));
    filter: blur(30px);
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -50px;
    left: -60px;
    width: 220px;
    height: 220px;
    background: radial-gradient(closest-side, rgba(88,86,214,0.05), rgba(88,86,214,0));
    filter: blur(30px);
    pointer-events: none;
  }

  .empty-icon {
    font-size: 56px;
    color: #a0aec0;
    margin-bottom: 16px;
    animation: pulseIcon 1.8s ease-in-out infinite;
  }

  .empty-text {
    text-align: center;

    p {
      margin: 0;
      font-size: 16px;
      color: #303133;
      font-weight: 600;
    }

    .empty-tip {
      font-size: 13px;
      color: #909399;
      margin-top: 8px;
      line-height: 1.6;
    }
  }
}

.sku-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  overflow-y: auto;
}

.sku-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: border-color .25s ease, box-shadow .25s ease, transform .25s ease;
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.06);
  position: relative;
  overflow: hidden;

  /* 顶部动效色条 */
  &::before {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    height: 3px;
    background: linear-gradient(90deg, #3395FF, #5856D6, #3395FF);
    background-size: 200% 100%;
    animation: barMove 6s linear infinite;
    opacity: .55;
    pointer-events: none;
  }

  &:hover {
    border-color: #1677FF;
    box-shadow: 0 16px 36px rgba(64, 158, 255, 0.18);
    transform: translateY(-2px);
  }

  &.selected {
    border-color: #1677FF;
    background: #f5f9ff;
    box-shadow: 0 20px 44px rgba(64, 158, 255, 0.22), inset 0 0 0 1px rgba(64,158,255,0.25);
  }
}

.card-header {
  margin-bottom: 12px;

  .product-name {
    font-size: 14px;
    font-weight: 600;
    color: #212529;
    margin: 0;
    line-height: 1.4;
    word-break: break-word;
    letter-spacing: .2px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .ticket-icon {
      font-size: 12px;
      color: #1677FF;
      font-weight: bold;
      width: 24px;
      height: 24px;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 50%;
      border: 1px solid #1677FF;
    }
  }
}

.card-body {
  .product-spec {
    font-size: 13px;
    color: #6c757d;
    margin-bottom: 8px;
    line-height: 1.3;
  }

  .location-info {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    padding: 6px 10px;
    background: #f5f9ff;
    border: 1px solid #e4e7ed;
    border-radius: 999px;

    .location-icon {
      font-size: 12px;
      color: #1677FF;
      margin-right: 4px;
    }

    .location-text {
      font-size: 12px;
      color: #495057;
      font-weight: 500;
    }
  }

  .quantity-section {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    gap: 6px;
    padding: 4px 8px;
    background: #fafbfd;
    border: 1px solid #edeff2;
    border-radius: 10px;

    .quantity-label {
      font-size: 12px;
      color: #6c757d;
    }

    .quantity-value {
      font-size: 13px;
      color: #212529;
      font-weight: 600;
      margin-left: 4px;
    }
  }
}

.other-require-tag {
  margin-right: 4px;
}

.countdown-text {
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes glowPulse {
  0% {
    box-shadow: 0 8px 20px rgba(64, 158, 255, 0.18), 0 0 0 0 rgba(64, 158, 255, 0.22);
  }
  100% {
    box-shadow: 0 16px 36px rgba(64, 158, 255, 0.28), 0 0 0 8px rgba(64, 158, 255, 0.08);
  }
}

@keyframes barMove {
  0% { background-position: 0% 0; }
  100% { background-position: 200% 0; }
}

@keyframes pulseIcon {
  0% { transform: scale(1); opacity: .9; }
  50% { transform: scale(1.06); opacity: 1; }
  100% { transform: scale(1); opacity: .9; }
}
</style>
