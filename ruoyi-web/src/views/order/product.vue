<template>
  <div class="product-management">
    <div class="page-header">
      <h3>基础信息管理</h3>
      <p>管理产品、供应商、价格等基础信息</p>
    </div>

    <div class="product-content">
      <div class="management-tabs">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="产品管理" name="product">
            <div class="tab-content">
              <div class="content-header">
                <el-button type="primary" size="small">
                  <i class="el-icon-plus" />
                  添加产品
                </el-button>
                <el-input
                  placeholder="搜索产品名称、型号等"
                  size="small"
                  style="width: 300px;"
                >
                  <i slot="prefix" class="el-input__icon el-icon-search" />
                </el-input>
              </div>

              <div class="no-data">
                <div class="no-data-content">
                  <i class="el-icon-box" />
                  <p>暂无产品信息</p>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="供应商管理" name="supplier">
            <div class="tab-content">
              <div class="content-header">
                <el-button type="primary" size="small">
                  <i class="el-icon-plus" />
                  添加供应商
                </el-button>
                <el-input
                  placeholder="搜索供应商名称、联系人等"
                  size="small"
                  style="width: 300px;"
                >
                  <i slot="prefix" class="el-input__icon el-icon-search" />
                </el-input>
              </div>

              <div class="no-data">
                <div class="no-data-content">
                  <i class="el-icon-office-building" />
                  <p>暂无供应商信息</p>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="价格管理" name="price">
            <div class="tab-content">
              <div class="content-header">
                <el-button type="primary" size="small">
                  <i class="el-icon-plus" />
                  设置价格
                </el-button>
                <el-input
                  placeholder="搜索产品名称等"
                  size="small"
                  style="width: 300px;"
                >
                  <i slot="prefix" class="el-input__icon el-icon-search" />
                </el-input>
              </div>

              <div class="no-data">
                <div class="no-data-content">
                  <i class="el-icon-money" />
                  <p>暂无价格信息</p>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="库存管理" name="inventory">
            <div class="tab-content">
              <div class="content-header">
                <el-button type="primary" size="small">
                  <i class="el-icon-plus" />
                  库存调整
                </el-button>
                <el-input
                  placeholder="搜索产品名称等"
                  size="small"
                  style="width: 300px;"
                >
                  <i slot="prefix" class="el-input__icon el-icon-search" />
                </el-input>
              </div>

              <div class="inventory-summary">
                <div class="summary-card">
                  <div class="summary-number">0</div>
                  <div class="summary-label">总商品数</div>
                </div>
                <div class="summary-card">
                  <div class="summary-number">0</div>
                  <div class="summary-label">库存充足</div>
                </div>
                <div class="summary-card">
                  <div class="summary-number">0</div>
                  <div class="summary-label">库存不足</div>
                </div>
                <div class="summary-card">
                  <div class="summary-number">0</div>
                  <div class="summary-label">缺货商品</div>
                </div>
              </div>

              <div class="no-data">
                <div class="no-data-content">
                  <i class="el-icon-goods" />
                  <p>暂无库存信息</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductManagement',
  data() {
    return {
      activeTab: 'product'
    }
  }
}
</script>

<style lang="scss" scoped>
.product-management {
  padding: 20px;
}

.page-header {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  h3 {
    margin: 0 0 10px 0;
    color: #303133;
  }

  p {
    margin: 0;
    color: #909399;
    font-size: 14px;
  }
}

.product-content {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.management-tabs {
  ::v-deep .el-tabs__header {
    margin-bottom: 20px;
  }

  ::v-deep .el-tabs__content {
    min-height: 500px;
  }
}

.tab-content {
  .content-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #e4e7ed;
  }
}

.inventory-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;

  .summary-card {
    text-align: center;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 4px;
    border-left: 4px solid #1677FF;

    &:nth-child(2) {
      border-left-color: #34C759;
    }

    &:nth-child(3) {
      border-left-color: #FF9500;
    }

    &:nth-child(4) {
      border-left-color: #FF3B30;
    }

    .summary-number {
      font-size: 28px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 5px;
    }

    .summary-label {
      color: #666;
      font-size: 14px;
    }
  }
}

.no-data {
  padding: 60px 20px;
  text-align: center;
  min-height: 300px;

  .no-data-content {
    i {
      font-size: 64px;
      color: #ddd;
      margin-bottom: 20px;
    }

    p {
      color: #999;
      font-size: 16px;
      margin: 0;
    }
  }
}
</style>
