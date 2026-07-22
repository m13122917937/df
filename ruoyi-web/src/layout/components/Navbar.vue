<template>
  <div class="navbar">
    <!-- Logo 区域 -->
    <div class="logo-section">
      <div class="logo-wrapper">
        <img class="logo-icon" src="@/assets/logo.png" alt="無界电商">
        <span class="system-name">無界电商</span>
      </div>
      <div class="logo-divider" />
    </div>

    <!-- 主导航 -->
    <nav class="main-navigation">
      <div
        :class="['nav-item', { 'is-active': activeMenu === '/df' }]"
        @click="handleMenuSelect('/df')"
      >
        <i class="el-icon-house" />
        <span class="nav-text">交易市场</span>
        <div class="nav-underline" />
      </div>
      <div
        :class="['nav-item', { 'is-active': activeMenu === '/order/send' }]"
        @click="handleMenuSelect('/order/send')"
      >
        <i class="el-icon-document" />
        <span class="nav-text">订单管理</span>
        <div class="nav-underline" />
      </div>
      <div
        v-permission="['admin']"
        :class="['nav-item', { 'is-active': activeMenu === '/monery/earnest' }]"
        @click="handleMenuSelect('/monery/earnest')"
      >
        <i class="el-icon-money" />
        <span class="nav-text">财务管理</span>
        <div class="nav-underline" />
      </div>
      <div
        v-permission="['admin']"
        :class="['nav-item', { 'is-active': activeMenu === '/set/user' }]"
        @click="handleMenuSelect('/set/user')"
      >
        <i class="el-icon-setting" />
        <span class="nav-text">基础配置</span>
        <div class="nav-underline" />
      </div>
    </nav>

    <!-- 右侧用户区域 -->
    <div class="user-actions">
      <!-- 企业信息 -->
      <el-dropdown
        class="company-dropdown"
        trigger="click"
        @command="changeCompany"
      >
        <div class="company-trigger">
          <i class="el-icon-office-building" />
          <span class="company-name-text">{{ (currentCompany || {}).companyName || '企业信息' }}</span>
          <i class="el-icon-arrow-down" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="company in companyVOList"
            :key="company.id"
            :command="company.id"
            :class="{ 'is-current': company.id === (currentCompany && currentCompany.id) }"
          >
            <i class="el-icon-office-building" />
            {{ company.companyName }}
            <i
              v-if="company.id === (currentCompany && currentCompany.id)"
              class="el-icon-check"
            />
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!-- 消息通知 -->
      <div class="notification-btn">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <i class="el-icon-bell" />
        </el-badge>
      </div>

      <!-- 退出登录 -->
      <div class="logout-btn" @click="handleLogout">
        <i class="el-icon-switch-button" />
        <span>退出登录</span>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { changeCompany } from '@/api/login'

export default {
  data() {
    return {
      unreadCount: 0
    }
  },
  computed: {
    ...mapGetters(['sidebar', 'avatar', 'device']),
    ...mapGetters('user', [
      'uuid',
      'currentCompany',
      'companyVOList',
      'hasMultipleCompanies'
    ]),
    activeMenu() {
      const route = this.$route
      if (route.path.startsWith('/order')) {
        return '/order/send'
      } else if (route.path.startsWith('/monery')) {
        return '/monery/earnest'
      } else if (route.path.startsWith('/set')) {
        return '/set/user'
      } else if (route.path.startsWith('/finance')) {
        return '/finance/index'
      } else if (route.path.startsWith('/trading-market')) {
        return '/df'
      } else {
        return '/df'
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    changeCompany(companyId) {
      changeCompany(companyId)
        .then((res) => {
          const { data = '' } = res || {}
          this.$store.dispatch('user/setAccessToken', data)
          this.$store
            .dispatch('user/getInfo')
            .then(() => {
              this.$router.push({ path: '/df' })
            })
            .catch(() => {
              this.$message.error('获取用户信息失败，请重试')
            })
        })
        .catch(() => {
          this.$message.error('登录失败，请重试')
        })
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'logout-confirm-dialog'
      })
        .then(() => {
          this.$store.dispatch('user/logout')
        })
        .catch(() => {
          // 用户取消退出
        })
    },
    handleMenuSelect(index) {
      if (index && index !== this.$route.path) {
        this.$router.push(index)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: #FFFFFF;
  border-bottom: 1px solid #E5E6EB;
  user-select: none;

  // Logo区域
  .logo-section {
    display: flex;
    align-items: center;
    height: 64px;
    flex-shrink: 0;

    .logo-wrapper {
      display: flex;
      align-items: center;
      gap: 12px;

      .logo-icon {
        width: 32px;
        height: 32px;
        object-fit: contain;
        flex-shrink: 0;
      }

      .system-name {
        font-size: 18px;
        font-weight: 600;
        color: #1D2129;
        white-space: nowrap;
      }
    }

    .logo-divider {
      width: 1px;
      height: 24px;
      background: #E5E6EB;
      margin-left: 24px;
    }
  }

  // 主导航
  .main-navigation {
    flex: 1;
    display: flex;
    align-items: center;
    height: 64px;

    .nav-item {
      position: relative;
      display: flex;
      align-items: center;
      height: 64px;
      padding: 0 24px;
      cursor: pointer;
      gap: 6px;
      color: #4E5969;
      font-size: 17px;
      transition: color 0.2s ease, background 0.2s ease;

      &:hover {
        background: rgba(22, 119, 255, 0.06);
      }

      i {
        font-size: 16px;
        transition: color 0.2s ease;
        color: #86909C;
      }

      .nav-text {
        white-space: nowrap;
      }

      .nav-underline {
        position: absolute;
        bottom: 0;
        left: 24px;
        right: 24px;
        height: 2px;
        background: #1677FF;
        border-radius: 1px 1px 0 0;
        transform: scaleX(0);
        transition: transform 0.2s ease;
      }

      &.is-active {
        color: #1677FF;

        i {
          color: #1677FF;
        }

        .nav-underline {
          transform: scaleX(1);
        }
      }
    }
  }

  // 右侧用户区域
  .user-actions {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-shrink: 0;

    .company-dropdown {
      cursor: pointer;

      .company-trigger {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 10px;
        border-radius: 6px;
        transition: background 0.2s ease;

        &:hover {
          background: #E8F3FF;
        }

        i {
          font-size: 16px;
          color: #4E5969;

          &.el-icon-arrow-down {
            font-size: 12px;
            color: #86909C;
          }
        }

        .company-name-text {
          font-size: 14px;
          color: #4E5969;
          max-width: 200px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .notification-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 32px;
      height: 32px;
      border-radius: 6px;
      cursor: pointer;
      transition: background 0.2s ease;

      &:hover {
        background: #E8F3FF;
      }

      i {
        font-size: 18px;
        color: #4E5969;
      }
    }

    .logout-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 32px;
      padding: 0 14px;
      border: 1px solid #1677FF;
      border-radius: 6px;
      color: #1677FF;
      font-size: 14px;
      gap: 4px;
      cursor: pointer;
      transition: all 0.2s ease;
      background: transparent;
      margin-left: 8px;

      &:hover {
        background: #E8F3FF;
      }

      i {
        font-size: 14px;
        line-height: 1;
      }
    }
  }
}

// 响应式
@media (max-width: 1366px) {
  .navbar {
    padding: 0 16px;

    .main-navigation .nav-item {
      padding: 0 16px;
    }
  }
}

@media (max-width: 1024px) {
  .navbar {
    .main-navigation .nav-item {
      padding: 0 12px;
    }
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 0 12px;

    .main-navigation .nav-item {
      padding: 0 8px;

      .nav-text {
        display: none;
      }
    }

    .user-actions {
      .company-name-text {
        display: none;
      }

      .logout-btn span {
        display: none;
      }
    }
  }
}

// 企业切换下拉菜单样式
::v-deep .el-dropdown-menu {
  background: #FFFFFF;
  border: 1px solid #E5E6EB;
  border-radius: 8px;
  padding: 4px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08), 0 8px 32px rgba(0, 0, 0, 0.04);

  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    transition: background 0.15s ease;
    border-radius: 4px;
    font-size: 14px;
    color: #4E5969;

    i:first-child {
      margin-right: 8px;
      font-size: 14px;
      color: #86909C;
    }

    i:last-child {
      margin-left: auto;
      font-size: 12px;
      color: #1677FF;
    }

    &.is-current {
      background: #E8F3FF;
      color: #1677FF;
      font-weight: 500;

      i:first-child {
        color: #1677FF;
      }
    }

    &:hover {
      background: #F5F5F5;
    }
  }
}

// 退出确认对话框样式
::v-deep .logout-confirm-dialog {
  .el-message-box {
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06), 0 16px 32px rgba(0, 0, 0, 0.08);
  }

  .el-message-box__header {
    background: #1677FF;
    color: #fff;
    padding: 20px 24px;
    border-radius: 12px 12px 0 0;
    margin: -16px -16px 16px -16px;

    .el-message-box__title {
      color: #fff;
      font-weight: 600;
    }

    .el-message-box__headerbtn .el-message-box__close {
      color: #fff;

      &:hover {
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }

  .el-message-box__content {
    padding: 0 24px 16px 24px;

    .el-message-box__message {
      color: #1D2129;
      font-size: 15px;
      line-height: 1.6;
    }
  }

  .el-message-box__btns {
    padding: 0 24px 20px 24px;

    .el-button {
      border-radius: 6px;
      padding: 8px 20px;
      font-weight: 500;

      &.el-button--primary {
        background: #1677FF;
        border: none;

        &:hover {
          background: #4096FF;
        }
      }

      &.el-button--default {
        border: 1px solid #E5E6EB;
        color: #4E5969;

        &:hover {
          background: #F5F5F5;
          border-color: #E5E6EB;
        }
      }
    }
  }
}

@media (prefers-reduced-motion: reduce) {
  .navbar .nav-item,
  .navbar .nav-underline,
  .navbar .logout-btn {
    transition: none !important;
  }
}
</style>
