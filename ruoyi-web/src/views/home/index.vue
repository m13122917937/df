<template>
  <div class="home-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <h2>欢迎使用订单管理系统</h2>
        <p>您可以通过左侧菜单进入不同的功能模块</p>
      </div>
    </div>

    <!-- 快速导航 -->
    <div class="quick-nav-section">
      <h3>快速导航</h3>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="nav-card" @click="$router.push('/order/pending')">
            <div class="nav-icon">
              <i class="el-icon-shopping-cart-2" />
            </div>
            <h4>当日抢单</h4>
            <p>查看和管理今日订单</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="nav-card" @click="$router.push('/finance/index')">
            <div class="nav-icon">
              <i class="el-icon-coin" />
            </div>
            <h4>财务管理</h4>
            <p>查看财务统计和收支</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="nav-card" @click="$router.push('/o2o')">
            <div class="nav-icon">
              <i class="el-icon-s-shop" />
            </div>
            <h4>交易市场</h4>
            <p>O2O和群接龙交易</p>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style lang="scss" scoped>
.home-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.welcome-section {
  margin-bottom: 30px;

  .welcome-card {
    background: white;
    padding: 30px;
    border-radius: 8px;
    text-align: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);

    h2 {
      margin: 0 0 10px 0;
      color: #333;
      font-size: 28px;
    }

    p {
      margin: 0;
      color: #666;
      font-size: 16px;
    }
  }
}

.quick-nav-section {
  h3 {
    color: #333;
    margin-bottom: 20px;
    font-size: 20px;
  }
}

.nav-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  }

  .nav-icon {
    font-size: 36px;
    color: #1890ff;
    margin-bottom: 12px;
  }

  h4 {
    margin: 0 0 8px 0;
    color: #333;
    font-size: 16px;
  }

  p {
    margin: 0;
    color: #666;
    font-size: 14px;
    line-height: 1.4;
  }
}
</style>
