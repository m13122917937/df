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
          <div class="nav-card" @click="$router.push('/order/send')">
            <div class="nav-icon">
              <i class="el-icon-shopping-cart-2" />
            </div>
            <h4>当日抢单</h4>
            <p>查看和管理今日订单</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="nav-card" @click="$router.push('/monery/earnest')">
            <div class="nav-icon">
              <i class="el-icon-coin" />
            </div>
            <h4>财务管理</h4>
            <p>查看财务统计和收支</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="nav-card" @click="$router.push('/df')">
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
  background: var(--bg-page, #F2F2F7);
  min-height: 100vh;
}

.welcome-section {
  margin-bottom: 30px;

  .welcome-card {
    background: var(--bg-card, #FFFFFF);
    padding: 30px;
    border-radius: 14px;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);

    h2 {
      margin: 0 0 10px 0;
      color: var(--text-primary, #000000);
      font-size: 28px;
      letter-spacing: -0.02em;
    }

    p {
      margin: 0;
      color: var(--text-secondary, rgba(60,60,67,0.60));
      font-size: 16px;
    }
  }
}

.quick-nav-section {
  h3 {
    color: var(--text-primary, #000000);
    margin-bottom: 20px;
    font-size: 20px;
    letter-spacing: -0.02em;
  }
}

.nav-card {
  background: var(--bg-card, #FFFFFF);
  padding: 24px;
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 200ms cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0,0,0,0.06), 0 16px 32px rgba(0,0,0,0.08);
  }

  &:active {
    transform: scale(0.97);
  }

  .nav-icon {
    font-size: 36px;
    color: #1677FF;
    margin-bottom: 12px;
  }

  h4 {
    margin: 0 0 8px 0;
    color: var(--text-primary, #000000);
    font-size: 16px;
  }

  p {
    margin: 0;
    color: var(--text-secondary, rgba(60,60,67,0.60));
    font-size: 14px;
    line-height: 1.4;
  }
}
</style>
