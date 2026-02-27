<template>
  <el-dialog :visible.sync="visible" width="720px" title="新建订单" @close="handleCancel">
    <div class="new-order-form">
      <el-form :model="form" label-width="110px" ref="formRef" :rules="rules">
        <el-form-item label="供应商" prop="supplierId">
          <el-select
            v-model="form.supplierId"
            placeholder="请输入供应商名称"
            clearable
            filterable
            remote
            reserve-keyword
            :remote-method="handleSupplierRemote"
            :loading="supplierLoading"
            @visible-change="handleSupplierVisible"
            @clear="handleSupplierClear"
            class="search-select"
          >
            <el-option v-for="s in supplierOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="采购商品">
          <el-row :gutter="12" type="flex" align="middle">
            <el-col :span="8">
              <el-select
                v-model="form.brandId"
                placeholder="选择品牌"
                filterable
                clearable
                @change="loadProductOptions"
              >
                <el-option v-for="b in brandOptions" :key="b.value" :label="b.label" :value="b.value" />
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-select
                v-model="form.productId"
                placeholder="选择商品"
                filterable
                clearable
                :disabled="!productOptions.length"
                @change="loadSkuOptions"
              >
                <el-option v-for="p in productOptions" :key="p.value" :label="p.label" :value="p.value" />
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-select
                v-model="form.skuId"
                placeholder="选择sku"
                filterable
                clearable
                :disabled="!skuOptions.length"
                @change="onSkuChange"
              >
                <el-option v-for="s in skuOptions" :key="s.value" :label="s.label" :value="s.value" />
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="商品编码" prop="productCode">
          <el-input v-model="form.productCode" placeholder="由 SKU 填充" disabled />
        </el-form-item>
        <el-form-item label="需求数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="采购价格" prop="purchasePrice">
          <el-input-number v-model="form.purchasePrice" :min="0" placeholder="输入采购价格" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="账期" prop="accountingPeriod">
          <el-select v-model="form.accountingPeriod" placeholder="请选择账期" clearable style="width: 100%">
            <el-option label="现款" :value="0" />
            <el-option label="T+1" :value="1" />
            <el-option label="T+2" :value="2" />
            <el-option label="T+3" :value="3" />
            <el-option label="T+4" :value="4" />
            <el-option label="T+5" :value="5" />
            <el-option label="T+6" :value="6" />
            <el-option label="T+7" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="输入备注" />
        </el-form-item>
      </el-form>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="handleCancel">取 消</el-button>
      <el-button size="small" type="primary" @click="handleSave">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getProductBrandList, getProductNameList, getSkuList, saveOrder } from '@/api/putin'
import { getBusinessCompanyListApi } from '@/api/business'

export default {
  name: 'NewOrderDialog',
  data() {
    return {
      visible: false,
      form: {
        brandId: '',
        productId: '',
        skuId: '',
        productCode: '',
        noWarehouseSkuCode: '',
        quantity: null,
        purchasePrice: '',
        accountingPeriod: 0,
        remark: '',
        supplierId: ''
      },
      brandOptions: [],
      productOptions: [],
      skuOptions: [],
      supplierOptions: [],
      supplierLoading: false,
      rules: {
        supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
        brandId: [{ required: true, message: '请选择品牌', trigger: 'change' }],
        productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
        skuId: [{ validator: (rule, value, callback) => {
            if (!this.form.skuId && !this.form.noWarehouseSkuCode && !this.form.productCode) {
              callback(new Error('请选择SKU或填写商品编码'))
            } else {
              callback()
            }
          }, trigger: 'change'
        }],
        quantity: [{ required: true, message: '请填写需求数量', trigger: 'change' }],
        purchasePrice: [{ validator: (rule, value, callback) => {
            const n = Number(value)
            if (Number.isNaN(n) || n < 0) {
              callback(new Error('采购价格必须为数字且不可小于 0'))
            } else {
              callback()
            }
          }, trigger: 'change'
        }],
        accountingPeriod: [{ required: true, message: '请选择账期', trigger: 'change' }]
      }
    }
  },
  methods: {
    open() {
      this.resetForm()
      this.loadBrandOptions()
      this.visible = true
    },
    resetForm() {
      this.form = {
        brandId: '',
        productId: '',
        skuId: '',
        productCode: '',
        noWarehouseSkuCode: '',
        quantity: null,
        purchasePrice: '',
        accountingPeriod: 0,
        remark: '',
        supplierId: ''
      }
      this.brandOptions = []
      this.productOptions = []
      this.skuOptions = []
      this.supplierOptions = []
      this.supplierLoading = false
    },
    handleCancel() {
      this.visible = false
    },
    async handleSave() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        const f = this.form
        const payload = {
          companyId: f.supplierId ? Number(f.supplierId) : '',
          accountingPeriod: Number(f.accountingPeriod || 0),
          price: Number(f.purchasePrice),
          quantity: Number(f.quantity),
          remark: f.remark || '',
          skuCode: f.productCode || f.noWarehouseSkuCode || ''
        }
        try {
          const res = await saveOrder(payload)
          if (res && res.code === 200) {
            this.$message.success(res.msg || '新建订单成功')
            this.visible = false
            this.$emit('created', res)
          } else {
            this.$message.error(res?.msg || '新建订单失败')
          }
        } catch (error) {
          console.error('saveOrder 调用失败', error)
          this.$message.error('新建订单失败，请稍后重试')
        }
      })
    },
    async loadBrandOptions() {
      try {
        const res = await getProductBrandList()
        const list = res?.rows || res?.data || []
        this.brandOptions = Array.isArray(list)
          ? list.map(item => {
              const v = item.brand || item.brandName || item.name || item.value || item
              return { label: v, value: v }
            })
          : []
      } catch (error) {
        console.error('获取品牌列表失败', error)
        this.brandOptions = []
      }
    },
    async loadProductOptions(brandId) {
      this.productOptions = []
      this.form.productId = ''
      this.form.skuId = ''
      this.skuOptions = []
      if (!brandId) return
      try {
        const res = await getProductNameList({ brand: brandId })
        const list = res?.rows || res?.data || []
        this.productOptions = Array.isArray(list)
          ? list.map(item => {
              const v = item.productName || item.name || item.product || item
              return { label: v, value: v }
            })
          : []
      } catch (error) {
        console.error('获取商品列表失败', error)
        this.productOptions = []
      }
    },
    async loadSkuOptions(productId) {
      this.skuOptions = []
      this.form.skuId = ''
      this.form.productCode = ''
      const brandId = this.form.brandId || ''
      if (!productId && !brandId) return
      try {
        const payload = {
          brand: brandId || '',
          category: '',
          productName: productId || ''
        }
        const res = await getSkuList(payload, { pageNum: 1, pageSize: 1000 })
        const list = res?.rows || res?.data || []
        this.skuOptions = Array.isArray(list)
          ? list.map(item => ({
              label: item.specName || item.skuName || item.skuCode || item.name || item,
              value: item.skuId || item.skuCode || item.specName || item.skuName || item.name || item,
              skuCode: item.skuCode || item.sku || '',
              specName: item.specName || item.mode || ''
            }))
          : []
      } catch (error) {
        console.error('获取 SKU 列表失败', error)
        this.skuOptions = []
      }
    },
    onSkuChange(skuId) {
      const selected = this.skuOptions.find(s => s.value === skuId)
      if (selected) {
        this.form.productCode = selected.skuCode || ''
      } else {
        this.form.productCode = ''
      }
    },
    async handleSupplierRemote(keyword = '') {
      const trimmed = (keyword || '').trim()
      this.supplierLoading = true
      try {
        const pageData = { pageNum: 1, pageSize: 30 }
        const params = {}
        if (trimmed) params.companyNameLike = trimmed
        const res = await getBusinessCompanyListApi(params,pageData)
        const list = res?.rows || res?.data || []
        this.supplierOptions = Array.isArray(list)
          ? list.map(item => ({ label: item.companyName, value: item.id, raw: item }))
          : []
      } catch (error) {
        console.error('获取供应商列表失败', error)
        this.supplierOptions = []
      } finally {
        this.supplierLoading = false
      }
    },
    handleSupplierVisible(visible) {
      if (visible && !this.supplierOptions.length) this.handleSupplierRemote()
    },
    handleSupplierClear() {
      this.form.supplierId = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.new-order-form ::v-deep .el-select {
  width: 100%;
}
</style>


