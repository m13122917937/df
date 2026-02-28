<template>
    <div class="commodity-manage">
        <!-- 搜索面板 -->
        <div class="search-card">
            <el-form ref="searchFormRef" :model="searchForm" size="small">
                <div class="search-content">
                    <div class="search-container">
                        <!-- 第一行 -->
                        <div class="search-row">
                            <div class="search-item">
                                <label class="search-label">品牌</label>
                                <el-select v-model="searchForm.brand" class="search-input" placeholder="请选择品牌" filterable clearable>
                                    <el-option v-for="item in brandOptions" :key="item.value" :label="item.label" :value="item.value" />
                                </el-select>
                            </div>
                            <div class="search-item">
                                <label class="search-label">品类</label>
                                <el-select v-model="searchForm.category" class="search-input" placeholder="请选择品类" filterable clearable>
                                    <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
                                </el-select>
                            </div>
                            <div class="search-item">
                                <label class="search-label">商品名称</label>
                                <el-input v-model.trim="searchForm.productName" placeholder="请输入产品名称"
                                    class="search-input" @keyup.enter.native="handleSearch" />
                            </div>
                        </div>
                    </div>

                    <div class="search-actions">
                        <div class="primary-actions">
                            <el-button type="primary" icon="el-icon-search" @click="handleSearch"
                                class="search-action-btn">搜索</el-button>
                            <el-button icon="el-icon-refresh-left" @click="handleReset" class="reset-btn">重置</el-button>
                        </div>
                    </div>
                </div>
                <div>
                    <div class="secondary-actions">
                        <el-button icon="el-icon-document-copy" @click="addGoods" class="batch-btn">
                            添加商品
                        </el-button>

                    </div>
                </div>
            </el-form>
        </div>

        <!-- 表格 -->
        <div class="table-wrapper table-section">
            <el-table class="sku-table" :data="skuList" v-loading="loading" border stripe size="small" style="width: 100%"
                height="100%" :header-cell-style="headerCellStyle" :cell-style="{ padding: '8px 0' }">
                <template slot="empty">
                    <EmptyState text="暂无商品数据" />
                </template>

                <el-table-column prop="brand" label="品牌" min-width="200"></el-table-column>
                <el-table-column prop="category" label="类别"min-width="200"></el-table-column>
                <el-table-column prop="productName" label="商品名称" min-width="200"
                show-overflow-tooltip></el-table-column>
                <el-table-column prop="specName" label="规格名称" min-width="200"
                show-overflow-tooltip></el-table-column>
                
                <el-table-column prop="skuCode" label="SKU编码" min-width="200"></el-table-column>
          
           
                <el-table-column label="是否启用序列号" min-width="200">
                    <template slot-scope="{ row }">
                        {{ row.snType === 1 ? '是' : '否' }}
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
            <el-pagination :current-page="pagination.current" :page-size="pagination.size"
                :page-sizes="[30, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
                :total="pagination.total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>

        <!-- 新增商品弹窗 -->
        <el-dialog title="新增商品" :visible.sync="addDialogVisible" width="600px" :close-on-click-modal="false">
            <el-form ref="productSkuFormRef" :model="productSkuForm" :rules="productSkuRules" label-width="120px"
                size="small">

                <el-form-item label="品牌" prop="brand">
                    <el-select style="width: 100%" v-model="productSkuForm.brand" placeholder="请选择品牌" filterable clearable>
                        <el-option v-for="item in brandOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>


                <el-form-item label="品类" prop="category">
                    <el-select style="width: 100%" v-model="productSkuForm.category" placeholder="请选择品类" filterable clearable>
                        <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>


                <el-form-item label="商品名称" prop="productName">
                    <el-input v-model.trim="productSkuForm.productName" placeholder="请输入商品名称" />
                </el-form-item>

                <el-form-item label="规格名称" prop="specName">
                    <el-input v-model.trim="productSkuForm.specName" placeholder="请输入规格名称" />
                </el-form-item>



                <el-form-item label="序列号类型" prop="snType">
                    <el-select style="width: 100%" v-model="productSkuForm.snType" placeholder="请选择序列号类型">
                        <el-option :label="'不启用'" :value="0" />
                        <el-option :label="'序列号'" :value="1" />
                        <el-option :label="'随机序列号'" :value="2" />
                    </el-select>
                </el-form-item>

            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="cancelAddGoods">取 消</el-button>
                <el-button type="primary" @click="submitAddGoods">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import { throttle } from '@/utils'
import EmptyState from '@/views/demandManage/wholesale/components/emptyState.vue'
import { getSkuListApi, createProductSkuApi, getBrandListApi, getProductCategoryListApi } from '@/api/wholesale'

export default {
    name: 'CommodityManage',
    components: {
        EmptyState
    },
    data() {
        return {
            loading: false,
            skuList: [],
            searchForm: {
                brand: '',
                category: '',
                productName: '',
       
            },
            // 下拉选项
            brandOptions: [],
            categoryOptions: [],
            // 新增商品弹窗
            addDialogVisible: false,
            productSkuForm: {
                brand: '',
                category: '',
                productName: '',
                specName:'',
                snType: 1
            },
            productSkuRules: {
                brand: [{ required: true, message: '请选择品牌', trigger: 'change' }],
                category: [{ required: true, message: '请选择品类', trigger: 'change' }],
                productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
                snType: [{ required: true, message: '请选择序列号类型', trigger: 'change' }],
                specName: [{ required: true, message: '请输入规格名称', trigger: 'blur' }]
            },
            pagination: {
                current: 1,
                size: 30,
                total: 0
            }
        }
    },
    mounted() {
        this.fetchBrandOptions()
        this.fetchCategoryOptions()
        this.fetchSkuList()
    },
    methods: {
        headerCellStyle() {
            return {
                background: '#f7f8fa',
                color: '#303133',
                fontWeight: 600
            }
        },
        handleSearch: throttle(function () {
            this.performSearch()
        }, 300),
        // 新增商品相关
        addGoods() {
            this.addDialogVisible = true
            // reset form
            this.$nextTick(() => {
                if (this.$refs.productSkuFormRef) {
                    this.$refs.productSkuFormRef.resetFields()
                }
            })
        },
        async submitAddGoods() {
            this.$refs.productSkuFormRef.validate(async valid => {
                if (!valid) return
                try {
                    const payload = { ...this.productSkuForm }
                    const res = await createProductSkuApi(payload)
                    if (res && res.code === 200) {
                        this.$message.success('新增成功')
                        this.addDialogVisible = false
                        this.fetchSkuList()
                    } else {
                        this.$message.error(res?.msg || '新增失败')
                    }
                } catch (err) {
                    console.error('submitAddGoods error', err)
                    this.$message.error('新增失败，请稍后重试')
                }
            })
        },
        cancelAddGoods() {
            this.addDialogVisible = false
        },
        // 获取品牌下拉
        async fetchBrandOptions() {
            try {
                const res = await getBrandListApi()
                const list = res && res.data ? res.data : (res && res.rows ? res.rows : (Array.isArray(res) ? res : []))
                this.brandOptions = list.map(item => {
                    return {
                        label:  item,
                        value:  item
                    }
                })
            } catch (err) {
                // eslint-disable-next-line no-console
                console.error('fetchBrandOptions error', err)
                this.brandOptions = []
            }
        },
        // 获取品类下拉
        async fetchCategoryOptions() {
            try {
                const res = await getProductCategoryListApi()
                const list = res && res.data ? res.data : (res && res.rows ? res.rows : (Array.isArray(res) ? res : []))
                this.categoryOptions = list.map(item => {
                    return {
                        label: item,
                        value: item
                    }
                })
            } catch (err) {
                // eslint-disable-next-line no-console
                console.error('fetchCategoryOptions error', err)
                this.categoryOptions = []
            }
        },

        handleReset() {
            this.searchForm = {
                brand: '',
                category: '',
                productName: ''
            }
            this.pagination.current = 1
            this.fetchSkuList()
        },

        performSearch() {
            this.pagination.current = 1
            this.fetchSkuList()
        },

        buildSearchParams() {
            const params = {}
            params.brand = this.searchForm.brand
            params.category = this.searchForm.category
            params.productName = this.searchForm.productName
            return params
        },

        async fetchSkuList() {
            const data = this.buildSearchParams()
            this.loading = true
            try {
                const res = await getSkuListApi(data, {
                    pageNum: this.pagination.current,
                    pageSize: this.pagination.size
                })
                if (res && res.rows) {
                    this.skuList = res.rows || []
                    this.pagination.total = res.total || 0
                } else if (Array.isArray(res)) {
                    // fallback if API returns array directly
                    this.skuList = res
                    this.pagination.total = res.length
                } else {
                    this.skuList = []
                    this.pagination.total = 0
                }
            } catch (err) {
                // eslint-disable-next-line no-console
                console.error('fetchSkuList error', err)
                this.skuList = []
                this.pagination.total = 0
            } finally {
                this.loading = false
            }
        },

        handleSizeChange(size) {
            this.pagination.size = size
            this.fetchSkuList()
        },

        handleCurrentChange(current) {
            this.pagination.current = current
            this.fetchSkuList()
        }
    }
}
</script>
<style lang="scss" scoped>
@import "@/assets/styles/common/order-components.scss";

  .commodity-manage {
    display: flex;
    flex-direction: column;
    padding: 20px;
    min-height: calc(100vh - 100px);
    box-sizing: border-box;
  }

.search-card {
    background: #fff;
    border-radius: 12px;
    padding: 16px;
    box-shadow: 0 8px 24px rgba(15, 35, 95, 0.08);
    border: 1px solid #eef2ff;
    margin-bottom: 12px;
    position: relative;
    overflow: hidden;

    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, #4b8dff, #55d3ff);
    }
}

.search-content {
    display: flex;
    align-items: flex-end;
}

.secondary-actions {
    display: flex;
    justify-content: flex-end;
    width: 100%;
    padding-top: 8px;
    margin-top: 4px;
    border-top: 1px solid #ebeef5;
}

.search-container {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    flex: 1;
    gap: 12px;
}

.search-row {
    display: flex;
    align-items: flex-end;
    width: 100%;
    gap: 8px;
}

.search-item {
    flex: 0 0 30%;
    max-width: 30%;
    display: flex;
    align-items: center;
    gap: 8px;
}

.search-label {
    font-size: 14px;
    font-weight: 500;
    color: #606266;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 4px;
    white-space: nowrap;
    min-width: fit-content;
    flex-shrink: 0;
}

.search-label::before {
    content: "";
    width: 4px;
    height: 4px;
    background: #409eff;
    border-radius: 50%;
}

.search-input {
    flex: 1;
    min-width: 0;
    width: 100%;
}

.search-input :deep(.el-input__inner),
.search-input :deep(.el-range-input) {
    border-radius: 8px;
    font-size: 14px;
    padding: 6px 10px;
    transition: all 0.3s ease;
    border: 2px solid #e4e7ed;
    background: #fff;
}

.search-input :deep(.el-input__inner:focus),
.search-input :deep(.el-range-editor.is-active) {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
    background: #fafbfc;
}

.table-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(15, 35, 95, 0.08);
    border: 1px solid #eef2ff;
    overflow: hidden;
    min-height: 0;
}

.sku-table {
    min-width: 100%;
    flex: 1;

    .el-table__header {
        th {
            background: #f7f8fa !important;
            color: #606266 !important;
            font-weight: 600 !important;
            font-size: 14px !important;
            border-bottom: 2px solid #e9edf3 !important;
        }
    }

    .el-table__body {
        tr {
            &:hover {
                background-color: #f5f7fa;
            }
        }

        td {
            border-bottom: 1px solid #f1f4f8;
            padding: 10px 0;
        }
    }
}

.pagination-wrapper {
    margin-top: 16px;
    text-align: right;
}

.no-data-content {
    text-align: center;
    padding: 48px 0;
    color: #909399;

    i {
        font-size: 48px;
        margin-bottom: 14px;
        display: block;
    }

    p {
        margin: 0;
        font-size: 16px;
    }
}

/* 搜索栏视觉调整 */
.search-card {
    position: relative;

    .ribbons {
        position: absolute;
        left: 0;
        right: 0;
        top: -6px;
        height: 6px;
        display: flex;
        align-items: center;
        pointer-events: none;
    }

    .ribbon {
        display: block;
        height: 3px;
        flex: 1;
        margin: 0 6px;
        border-radius: 3px;
    }

    .ribbon--blue {
        background: linear-gradient(90deg, #5fb0ff, #4b8dff);
    }

    .ribbon--green {
        background: linear-gradient(90deg, #6dd28a, #49b36a);
    }

    .ribbon--orange {
        background: linear-gradient(90deg, #f6b26b, #e68a2e);
    }

    .field-label {
        display: inline-flex;
        align-items: center;
        color: #5b6b7a;
        font-size: 13px;
        margin-bottom: 6px;
    }

    .field-label .dot {
        display: inline-block;
        width: 6px;
        height: 6px;
        background: #409eff;
        border-radius: 50%;
        margin-right: 8px;
    }

    /* 让输入框看起来像卡片 */
    .el-input__inner {
        background: #fff;
        border-radius: 6px;
        box-shadow: 0 4px 12px rgba(16, 30, 115, 0.04);
        border: 1px solid #e6eef6;
        padding: 8px 12px;
    }

    .actions-col {
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }

    .actions-panel {
        background: #fff;
        border-radius: 8px;
        padding: 8px;
        box-shadow: 0 6px 14px rgba(16, 30, 115, 0.06);
        border: 1px solid #e6eef6;
        display: flex;
        flex-direction: column;
        align-items: flex-end;
    }

    .actions-top {
        display: flex;
        gap: 8px;
    }

    .actions-bottom {
        margin-top: 8px;
        width: 100%;
        display: flex;
        justify-content: flex-end;
    }

    .export-btn {
        background: linear-gradient(90deg, #3b82f6, #2563eb);
        border-color: #2563eb;
        color: #fff;
        border-radius: 6px;
    }
}
</style>