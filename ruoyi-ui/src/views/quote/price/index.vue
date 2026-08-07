<template>
  <div class="app-container quote-price-page">
    <el-card shadow="never" class="quote-price-filter-card">
      <el-form :inline="true" :model="queryParams" size="small" @submit.native.prevent>
        <el-form-item label="品牌">
          <el-select v-model="queryParams.brandId" clearable filterable placeholder="请选择品牌" style="width: 180px" @change="handleQuery">
            <el-option
              v-for="brand in brandOptions"
              :key="brand.id"
              :label="brand.brandName"
              :value="brand.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="queryParams.productNameLike" clearable placeholder="商品名称/规格" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item class="quote-price-filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-finished" :loading="saving" @click="handleSaveAll">批量保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="quote-price-table-card">
      <div slot="header" class="quote-price-table-header">
        <span>每日报价维护（零售、分销1、分销2；保存后写入当天报价）</span>
        <div>
          <el-button size="small" type="success" icon="el-icon-picture-outline" :loading="imageLoading" @click="generateQuoteImage">生成报价单图片</el-button>
          <span class="quote-price-tip">保存后批发报价页立即生效</span>
        </div>
      </div>

      <el-table
        ref="quotePriceTable"
        v-loading="loading"
        :data="productList"
        border
        stripe
        size="medium"
      >
        <el-table-column prop="brand" label="品牌" min-width="110" show-overflow-tooltip />
        <el-table-column prop="category" label="品类" min-width="110" show-overflow-tooltip />
        <el-table-column prop="productName" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="specName" label="规格/型号" min-width="150" show-overflow-tooltip />
        <el-table-column label="零售价" min-width="140" align="center">
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row._prices.retail"
              :min="0"
              :precision="2"
              :step="1"
              size="small"
              controls-position="right"
              style="width: 120px"
            />
          </template>
        </el-table-column>
        <el-table-column label="分销1价" min-width="140" align="center">
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row._prices.distributor1"
              :min="0"
              :precision="2"
              :step="1"
              size="small"
              controls-position="right"
              style="width: 120px"
            />
          </template>
        </el-table-column>
        <el-table-column label="分销2价" min-width="140" align="center">
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row._prices.distributor2"
              :min="0"
              :precision="2"
              :step="1"
              size="small"
              controls-position="right"
              style="width: 120px"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-time" @click="handleHistory(scope.row)">历史报价</el-button>
            <el-button type="text" icon="el-icon-check" :loading="saving" @click="handleSaveRow(scope.row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :page-sizes="[50, 100, 200]"
        @pagination="getList"
      />
    </el-card>

    <el-dialog
      :title="historyProduct ? `${historyProduct.productName} - 历史报价` : '历史报价'"
      :visible.sync="historyDialogVisible"
      width="700px"
      append-to-body
    >
      <el-table :data="historyList" border stripe size="medium" max-height="420">
        <el-table-column label="报价时间" min-width="170" align="center">
          <template slot-scope="scope">{{ formatDateTime(scope.row.updateTime || scope.row.quoteDate) }}</template>
        </el-table-column>
        <el-table-column label="零售价" min-width="140" align="right">
          <template slot-scope="scope">¥{{ formatPrice(scope.row.retailPrice) }}</template>
        </el-table-column>
        <el-table-column label="批发1价" min-width="140" align="right">
          <template slot-scope="scope">¥{{ formatPrice(scope.row.distributor1Price) }}</template>
        </el-table-column>
        <el-table-column label="批发2价" min-width="140" align="right">
          <template slot-scope="scope">¥{{ formatPrice(scope.row.distributor2Price) }}</template>
        </el-table-column>
      </el-table>
      <div v-if="!loadingHistory && historyList.length === 0" class="quote-history-empty">暂无历史报价</div>
    </el-dialog>

    <el-dialog
      title="生成报价单图片"
      :visible.sync="imageDialogVisible"
      width="420px"
      append-to-body
    >
      <el-form label-width="90px">
        <el-form-item label="价格档位">
          <el-radio-group v-model="imageLevel">
            <el-radio :label="0">零售</el-radio>
            <el-radio :label="1">批发1</el-radio>
            <el-radio :label="2">批发2</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="imageDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmGenerate">生 成</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getQuoteProductPage,
  getQuoteBrandOptions,
  saveQuote,
  getQuoteHistory,
  getQuoteImageData
} from '@/api/quote'

export default {
  name: 'QuotePriceUpdate',
  data() {
    return {
      loading: false,
      saving: false,
      total: 0,
      productList: [],
      brandOptions: [],
      historyDialogVisible: false,
      loadingHistory: false,
        historyList: [],
        historyProduct: null,
        imageLoading: false,
        imageDialogVisible: false,
        imageLevel: 0,
        imageRows: [],
        queryParams: {
        pageNum: 1,
        pageSize: 50,
        brandId: undefined,
        productNameLike: ''
      }
    }
  },
  created() {
    this.loadBrandOptions()
    this.getList()
  },
  methods: {
    loadBrandOptions() {
      getQuoteBrandOptions().then((response) => {
        this.brandOptions = response.data || []
      })
    },
    getList() {
      this.loading = true
      getQuoteProductPage({
        brandId: this.queryParams.brandId,
        productNameLike: this.queryParams.productNameLike,
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }).then((response) => {
        this.productList = (response.rows || []).map((row) => {
          const latest = row.latestQuote || {}
          return {
            ...row,
            _prices: {
              retail: latest.retailPrice,
              distributor1: latest.distributor1Price,
              distributor2: latest.distributor2Price
            }
          }
        })
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 50,
        brandId: undefined,
        productNameLike: ''
      }
      this.handleQuery()
    },
    handleSaveRow(row) {
      if (!this.hasAnyPrice(row._prices)) {
        this.$message.warning('至少需要填写一个价格')
        return
      }
      this.saving = true
      saveQuote(this.buildPayload(row)).then(() => {
        this.$message.success(`商品“${row.productName}”当天报价已保存`)
        this.saving = false
      }).catch(() => {
        this.saving = false
      })
    },
    handleHistory(row) {
      this.historyProduct = row
      this.historyList = []
      this.historyDialogVisible = true
      this.loadingHistory = true
      getQuoteHistory(row.id).then((response) => {
        this.historyList = (response && response.data) || []
        this.loadingHistory = false
      }).catch(() => {
        this.loadingHistory = false
      })
    },
    generateQuoteImage() {
      this.imageLoading = true
      getQuoteImageData().then((response) => {
        const rows = (response && response.data) || []
        if (rows.length === 0) {
          this.$message.warning('暂无可生成的报价数据')
          this.imageLoading = false
          return
        }
        this.imageRows = rows
        this.imageLevel = 0
        this.imageDialogVisible = true
        this.imageLoading = false
      }).catch(() => {
        this.imageLoading = false
        this.$message.error('获取报价数据失败')
      })
    },
    confirmGenerate() {
      this.imageDialogVisible = false
      this.drawQuoteImage(this.imageRows, this.imageLevel)
    },
    drawQuoteImage(rows, level) {
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      const width = 1200
      const padding = 50
      const topHeight = 150
      const headerHeight = 52
      const rowHeight = 44
      const height = topHeight + headerHeight + rows.length * rowHeight + padding
      canvas.width = width
      canvas.height = height

      ctx.fillStyle = '#ffffff'
      ctx.fillRect(0, 0, width, height)

      ctx.textAlign = 'center'
      ctx.fillStyle = '#1a1a1a'
      ctx.font = 'bold 38px "Microsoft YaHei", sans-serif'
      ctx.fillText('无界供应链', width / 2, 78)
      ctx.font = '16px "Microsoft YaHei", sans-serif'
      ctx.fillStyle = '#666666'
      const now = new Date()
      const dateText = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
      ctx.fillText(`报价日期：${dateText}`, width / 2, 112)

      const img = new Image()
      img.onload = () => {
        ctx.drawImage(img, padding, 22, 72, 72)
        this.drawQuoteTable(ctx, rows, level, width, topHeight, headerHeight, rowHeight, padding)
        this.downloadCanvas(canvas, dateText)
      }
      img.onerror = () => {
        this.drawQuoteTable(ctx, rows, level, width, topHeight, headerHeight, rowHeight, padding)
        this.downloadCanvas(canvas, dateText)
      }
      img.src = require('@/assets/logo/logo3.png')
    },
    drawQuoteTable(ctx, rows, level, width, topHeight, headerHeight, rowHeight, padding) {
      const priceLabel = ['零售价', '批发1价', '批发2价'][level] || '价格'
      const columns = [
        { label: '品牌', x: padding, width: 220 },
        { label: '商品名称', x: padding + 220, width: 380 },
        { label: '规格/型号', x: padding + 600, width: 340 },
        { label: priceLabel, x: padding + 940, width: 210 }
      ]
      const tableTop = topHeight

      ctx.textAlign = 'left'
      ctx.font = 'bold 18px "Microsoft YaHei", sans-serif'
      ctx.fillStyle = '#f2f4f7'
      ctx.fillRect(padding, tableTop, width - padding * 2, headerHeight)
      ctx.fillStyle = '#1a1a1a'
      columns.forEach((col) => {
        const isPrice = col.label.indexOf('价') > -1
        ctx.textAlign = isPrice ? 'right' : 'left'
        ctx.fillText(col.label, isPrice ? col.x + col.width - 16 : col.x + 16, tableTop + 34)
      })

      ctx.font = '16px "Microsoft YaHei", sans-serif'
      rows.forEach((row, index) => {
        const y = tableTop + headerHeight + index * rowHeight
        if (index % 2 === 1) {
          ctx.fillStyle = '#fafbfc'
          ctx.fillRect(padding, y, width - padding * 2, rowHeight)
        }
        ctx.fillStyle = '#333333'
        const priceValue = level === 1 ? row.distributor1Price : (level === 2 ? row.distributor2Price : row.retailPrice)
        columns.forEach((col) => {
          let value = ''
          if (col.label === '品牌') value = row.brand || ''
          if (col.label === '商品名称') value = row.productName || ''
          if (col.label === '规格/型号') value = row.specName || ''
          if (col.label.indexOf('价') > -1) value = `¥${this.formatPrice(priceValue)}`
          const isPrice = col.label.indexOf('价') > -1
          ctx.textAlign = isPrice ? 'right' : 'left'
          ctx.fillText(value, isPrice ? col.x + col.width - 16 : col.x + 16, y + 30)
        })
      })

      ctx.strokeStyle = '#e4e7ed'
      ctx.lineWidth = 1
      ctx.beginPath()
      ctx.moveTo(padding, tableTop)
      ctx.lineTo(padding, tableTop + headerHeight + rows.length * rowHeight)
      ctx.stroke()
      ctx.beginPath()
      ctx.moveTo(width - padding, tableTop)
      ctx.lineTo(width - padding, tableTop + headerHeight + rows.length * rowHeight)
      ctx.stroke()
    },
    downloadCanvas(canvas, dateText) {
      canvas.toBlob((blob) => {
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `无界供应链报价单_${dateText.replace(/[:\s]/g, '')}.png`
        a.click()
        URL.revokeObjectURL(url)
      }, 'image/png')
    },
    handleSaveAll() {
      const rows = this.productList
      const invalid = rows.filter((row) => !this.hasAnyPrice(row._prices))
      if (invalid.length > 0) {
        this.$message.warning(`请先为商品“${invalid[0].productName}”填写至少一个价格`)
        return
      }
      this.saving = true
      let success = 0
      let failed = 0
      const errors = []
      const doSave = (index) => {
        if (index >= rows.length) {
          this.saving = false
          let message = `保存完成：成功 ${success} 条，失败 ${failed} 条`
          if (errors.length > 0) {
            message += '；' + errors.slice(0, 5).join('；')
          }
          this.$message({ type: failed > 0 ? 'warning' : 'success', message, duration: 6000 })
          return
        }
        const row = rows[index]
        saveQuote(this.buildPayload(row)).then(() => {
          success++
          doSave(index + 1)
        }).catch((error) => {
          failed++
          errors.push(`商品“${row.productName}”：${(error && error.message) || '保存失败'}`)
          doSave(index + 1)
        })
      }
      doSave(0)
    },
    buildPayload(row) {
      return {
        productId: row.id,
        retailPrice: row._prices.retail,
        distributor1Price: row._prices.distributor1,
        distributor2Price: row._prices.distributor2
      }
    },
    hasAnyPrice(prices) {
      return prices.retail !== null && prices.retail !== undefined && prices.retail !== ''
        || prices.distributor1 !== null && prices.distributor1 !== undefined && prices.distributor1 !== ''
        || prices.distributor2 !== null && prices.distributor2 !== undefined && prices.distributor2 !== ''
    },
    formatPrice(price) {
      if (price === null || price === undefined || price === '') {
        return '-'
      }
      return Number(price).toFixed(2)
    },
    formatDateTime(dateTime) {
      if (!dateTime) {
        return '-'
      }
      return String(dateTime).replace('T', ' ').slice(0, 19)
    }
  }
}
</script>

<style lang="scss" scoped>
.quote-price-page {
  .quote-price-filter-card {
    margin-bottom: 16px;
  }

  .quote-price-table-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quote-price-tip {
    color: #909399;
    font-size: 12px;
  }

  .quote-history-empty {
    padding: 24px 0;
    color: #999999;
    text-align: center;
  }
}
</style>
