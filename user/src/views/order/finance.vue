<template>
  <div class="finance-order">
    <div class="page-header">
      <h3>财务管理</h3>
      <p>订单财务数据统计与管理</p>
    </div>

    <div class="finance-content">
      <div class="finance-stats">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-coin" />
          </div>
          <div class="stat-info">
            <div class="stat-number">¥ 0.00</div>
            <div class="stat-label">总收入</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-shopping-cart-2" />
          </div>
          <div class="stat-info">
            <div class="stat-number">¥ 0.00</div>
            <div class="stat-label">待结算</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-wallet" />
          </div>
          <div class="stat-info">
            <div class="stat-number">¥ 0.00</div>
            <div class="stat-label">已结算</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-warning" />
          </div>
          <div class="stat-info">
            <div class="stat-number">¥ 0.00</div>
            <div class="stat-label">异常金额</div>
          </div>
        </div>
      </div>

      <div class="finance-table">
        <div class="table-header">
          <h4>财务明细</h4>
          <div class="table-actions">
            <el-button type="primary" size="small">导出报表</el-button>
            <el-button size="small">刷新数据</el-button>
          </div>
        </div>

        <div class="no-data">
          <div class="no-data-content">
            <i class="el-icon-document" />
            <p>暂无财务数据</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderFinance'
}
</script>

<style lang="scss" scoped>
.finance-order {
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

.finance-content {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.finance-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;

  &:nth-child(2) {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  &:nth-child(3) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  &:nth-child(4) {
    background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
    color: #333;
  }

  .stat-icon {
    font-size: 40px;
    margin-right: 20px;
    opacity: 0.8;
  }

  .stat-info {
    flex: 1;

    .stat-number {
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 5px;
    }

    .stat-label {
      font-size: 14px;
      opacity: 0.8;
    }
  }
}

.finance-table {
  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h4 {
      margin: 0;
      color: #303133;
    }

    .table-actions {
      display: flex;
      gap: 10px;
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
