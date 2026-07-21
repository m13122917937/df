<template>
  <div class="navbar">
    <!-- Logo区域 -->
    <!-- <div class="logo-container">
      <span class="logo-text">风扬管理系统</span>
    </div> -->

    <!-- 主导航菜单 -->
    <div class="main-nav">
      <el-menu
        mode="horizontal"
        background-color="#f8f9fa"
        text-color="#495057"
        active-text-color="#007bff"
        class="main-nav-menu"
        :default-active="activeMenu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/o2o">
          <i class="el-icon-house" />
          <span>交易市场</span>
        </el-menu-item>
        <el-menu-item index="/order/pending">
          <i class="el-icon-document" />
          <span>订单管理</span>
          <!-- <el-tag
            type="danger"
            size="mini"
            style="margin-left: 5px"
          >特处理2</el-tag> -->
        </el-menu-item>
        <!-- 暂时隐藏财务管理模块 -->
        <!-- <el-menu-item index="/finance/index">
          <i class="el-icon-money"></i>
          <span>财务管理</span>
        </el-menu-item> -->
        <el-menu-item v-permission="['admin']" index="/monery/earnest">
          <i class="el-icon-money" />
          <span>财务管理</span>
          <!-- <el-tag
            type="danger"
            size="mini"
            style="margin-left: 5px"
          >特处理111万</el-tag> -->
        </el-menu-item>
        <el-menu-item v-permission="['admin']" index="/set/user">
          <i class="el-icon-setting" />
          <span>基础配置</span>
        </el-menu-item>
        <el-menu-item index="/rules">
          <i class="el-icon-warning" />
          <span>平台规则</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧用户菜单 -->
    <div class="right-menu">
      <!-- 企业切换下拉菜单 -->
      <el-dropdown
        v-if="hasMultipleCompanies"
        class="company-switcher right-menu-item hover-effect"
        trigger="click"
        @command="changeCompany"
      >
        <div class="company-switcher-wrapper">
          <i class="el-icon-switch-button" />
          <span>{{ currentCompany.companyName }}</span>
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="company in companyVOList"
            :key="company.id"
            :command="company.id"
            :class="{
              'is-current':
                company.id === (currentCompany && currentCompany.id),
            }"
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

      <!-- 退出登录按钮 -->
      <div
        class="logout-btn right-menu-item hover-effect"
        @click="handleLogout"
      >
        <i class="el-icon-switch-button logout-icon" />
        <span>退出登录</span>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { changeCompany } from '@/api/login'

export default {
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
      // 根据当前路由确定激活的菜单项
      if (route.path.startsWith('/order')) {
        return '/order/pending'
      } else if (route.path.startsWith('/monery')) {
        return '/monery/earnest'
      } else if (route.path.startsWith('/set')) {
        return '/set/user'
      } else if (route.path.startsWith('/finance')) {
        return '/finance/index'
      } else if (route.path.startsWith('/rules')) {
        return '/rules'
      } else if (route.path.startsWith('/trading-market')) {
        return '/o2o'
      } else {
        return '/o2o'
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
          console.log('data', res)
          const { data = '' } = res || {}
          this.$store.dispatch('user/setAccessToken', data)
          this.$store
            .dispatch('user/getInfo')
            .then(() => {
              this.$router.push({ path: '/o2o' })
            })
            .catch(() => {
              this.$message.error('获取用户信息失败，请重试')
            })
        })
        .catch(() => {
          this.$message.error('登录失败，请重试')
        })
    },
    // 处理退出登录
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
      // 处理菜单选择事件，进行路由跳转
      console.log('Menu selected:', index)
      if (index && index !== this.$route.path) {
        this.$router.push(index)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 48px;
  overflow: hidden;
  position: relative;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid #dee2e6;

  .logo-container {
    display: flex;
    align-items: center;
    margin-right: 24px;

    .logo-placeholder {
      width: 32px;
      height: 32px;
      margin-right: 8px;
      background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 4px rgba(0, 123, 255, 0.3);

      i {
        font-size: 16px;
        color: #fff;
      }
    }

    .logo-text {
      font-size: 16px;
      font-weight: bold;
      color: #495057;
    }

    .current-company {
      display: flex;
      align-items: center;
      margin-left: 16px;
      padding: 4px 10px;
      background: rgba(0, 123, 255, 0.1);
      border-radius: 4px;
      border: 1px solid rgba(0, 123, 255, 0.2);

      i {
        font-size: 12px;
        color: #007bff;
        margin-right: 4px;
      }

      .company-name {
        font-size: 12px;
        color: #007bff;
        font-weight: 500;
        max-width: 120px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .main-nav {
    flex: 1;

    .main-nav-menu {
      border: none;
      background: transparent !important;

      .el-menu-item {
        height: 40px;
        line-height: 40px;
        border: none !important;
        color: #495057 !important;
        font-weight: 500;
        transition: all 0.3s ease;
        border-radius: 4px;
        margin: 0 2px;
        padding: 0 12px;
        font-size: 14px;

        &:hover {
          background: rgba(0, 123, 255, 0.1) !important;
          color: #007bff !important;
          transform: translateY(-1px);
        }

        &.is-active {
          background: rgba(0, 123, 255, 0.15) !important;
          color: #007bff !important;
          font-weight: 600;
          box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
        }

        i {
          margin-right: 4px;
          transition: color 0.3s ease;
          font-size: 14px;
        }
      }
    }
  }

  .hamburger-container {
    line-height: 48px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    height: 100%;
    line-height: 48px;
    // width: 400px;
    display: flex;
    justify-self: flex-end;
    align-items: center;
    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: flex;
      align-items: center;
      padding: 0 6px;
      font-size: 16px;
      color: #495057;

      &.hover-effect {
        cursor: pointer;
        transition: all 0.3s ease;
        border-radius: 6px;

        &:hover {
          background: rgba(0, 123, 255, 0.1);
          color: #007bff;
        }
      }
    }

    .company-switcher {
      margin-right: 8px;
      display: flex;
      align-items: center;
      height: 32px;

      .company-switcher-wrapper {
        display: flex;
        align-items: center;
        padding: 6px 10px;
        border-radius: 4px;
        transition: all 0.3s ease;
        height: 32px;
        line-height: 1;

        i:first-child {
          margin-right: 4px;
          font-size: 12px;
        }

        span {
          font-size: 12px;
          margin-right: 4px;
        }

        .el-icon-caret-bottom {
          font-size: 10px;
        }
      }
    }

    .logout-btn {
      margin-left: 8px;
      padding: 6px 12px;
      background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
      color: #fff !important;
      border-radius: 6px;
      font-weight: 500;
      box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
      transition: all 0.3s ease;
      cursor: pointer;
      display: flex;
      align-items: center;
      height: 32px;
      line-height: 1;

      &:hover {
        background: linear-gradient(
          135deg,
          #409eff 0%,
          #1890ff 100%
        ) !important;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(64, 158, 255, 0.4);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
      }

      .logout-icon {
        margin-right: 4px;
        font-size: 14px;
        font-weight: 600;
      }

      span {
        font-size: 12px;
      }
    }

    .avatar-container {
      margin-right: 0;

      .avatar-wrapper {
        margin-top: 4px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 32px;
          height: 32px;
          border-radius: 8px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -16px;
          top: 20px;
          font-size: 10px;
        }
      }
    }
  }
}

// 企业切换下拉菜单样式
::v-deep .el-dropdown-menu {
  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    padding: 8px 16px;
    transition: all 0.3s ease;

    i:first-child {
      margin-right: 8px;
      font-size: 14px;
      color: #666;
    }

    i:last-child {
      margin-left: auto;
      font-size: 12px;
      color: #007bff;
    }

    &.is-current {
      background: rgba(0, 123, 255, 0.1);
      color: #007bff;
      font-weight: 500;

      i:first-child {
        color: #007bff;
      }
    }

    &:hover {
      background: rgba(0, 123, 255, 0.05);
    }
  }
}

// 退出确认对话框样式
::v-deep .logout-confirm-dialog {
  .el-message-box {
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  }

  .el-message-box__header {
    background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
    color: #fff;
    padding: 20px 24px;
    border-radius: 12px 12px 0 0;
    margin: -20px -20px 20px -20px;

    .el-message-box__title {
      color: #fff;
      font-weight: 600;
    }

    .el-message-box__headerbtn {
      .el-message-box__close {
        color: #fff;

        &:hover {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }

  .el-message-box__content {
    padding: 0 24px 20px 24px;

    .el-message-box__message {
      color: #606266;
      font-size: 16px;
      line-height: 1.6;
    }
  }

  .el-message-box__btns {
    padding: 0 24px 24px 24px;

    .el-button {
      border-radius: 8px;
      padding: 10px 24px;
      font-weight: 500;

      &.el-button--primary {
        background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
        border: none;

        &:hover {
          background: linear-gradient(135deg, #ff5252 0%, #e53935 100%);
        }
      }

      &.el-button--default {
        border: 1px solid #dcdfe6;
        color: #606266;

        &:hover {
          background: #f5f7fa;
          border-color: #c0c4cc;
        }
      }
    }
  }
}
</style>
