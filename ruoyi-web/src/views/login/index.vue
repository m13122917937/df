<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 微信扫码区域 -->
      <div v-if="!isLogin" class="qr-section">
        <div class="qr-container">
          <div class="qr-title">
            <h2>微信扫码登录</h2>
            <p>请使用微信扫描二维码</p>
          </div>
          <div class="qr-code">
            <div v-if="!qrCodeUrl" class="qr-placeholder">
              <div class="qr-loading">
                <i class="el-icon-loading" />
                <p>正在生成二维码...</p>
              </div>
            </div>
            <div v-else class="qr-image">
              <img :src="qrCodeUrl" alt="微信登录二维码">
              <!-- 刷新蒙层 -->
              <div v-if="showRefreshOverlay" class="qr-refresh-overlay">
                <div class="refresh-content">
                  <i class="el-icon-loading" />
                  <p>二维码已过期</p>
                  <el-button type="primary" size="small" @click="refreshQRCode">
                    请刷新
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="company-list-container">
        <div class="company-title">
          <h2>选择企业</h2>
        </div>
        <div v-if="companyVOList.length > 0 && !newUser" ref="companyList" class="company-list">
          <!-- 滚动提示 -->
          <div v-if="companyVOList.length > 3 && showScrollHint" class="scroll-hint">
            <i class="el-icon-arrow-down" />
            <span>滚动查看更多企业</span>
          </div>

          <div
            v-for="company in companyVOList"
            :key="company.id"
            class="company-item"
            :class="{
              'loading': loadingCompanyId === company.id,
              'disabled': company.grabStatus === 1
            }"
            @click="handleCompanyClick(company)"
          >
            <div class="company-icon">
              <i class="el-icon-office-building" />
            </div>
            <div class="company-info">
              <h3 class="company-name">{{ company.companyName }}</h3>
              <p class="company-desc">点击进入企业工作台</p>
            </div>
            <div class="company-arrow">
              <i class="el-icon-arrow-right" />
            </div>
            <div v-if="loadingCompanyId === company.id" class="loading-overlay">
              <i class="el-icon-loading" />
            </div>
          </div>
        </div>
        <div v-else class="company-list-empty">
          <div class="company-list-empty-icon">
            <i class="el-icon-warning" />
            <p>暂无企业请联系管理员，注册账号</p>
            <div class="wxadmincode">
              <img src="../../assets/wxadmincode.png" alt="">
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 版权信息 -->
    <div class="copyright">
      <p>&copy; 2024 無界. All rights reserved.</p>
    </div>
  </div>
</template>

<script>
import { checkLoginQrCode, getLoginToken, getLoginQrCode } from '@/api/login'
export default {
  name: 'Login',
  data() {
    return {
      redirect: undefined,
      companyVOList: [],
      // 微信扫码登录相关
      qrCodeUrl: '',
      uuid: '',
      isLogin: false,
      qrTimer: null,
      checkTimer: null,
      refreshTimer: null, // 1分钟刷新提示定时器
      qrStartTime: null, // 二维码生成时间
      showRefreshOverlay: false, // 是否显示刷新蒙层
      newUser: false,
      // 企业选择相关
      loadingCompanyId: null,
      // 滚动相关
      showScrollHint: true
    }
  },
  created() {
    this.companyVOList = []
    this.qrCodeUrl = ''
    this.uuid = ''
    this.qrTimer = null
    this.checkTimer = null
    this.refreshTimer = null
    this.qrStartTime = null
    this.showRefreshOverlay = false
    this.generateQRCode()
  },
  mounted() {
    // 页面加载完成后自动生成二维码
    this.$nextTick(() => {
      this.addScrollListener()
    })
  },
  beforeDestroy() {
    this.clearTimers()
    this.removeScrollListener()
  },
  methods: {
    // 生成微信登录二维码
    generateQRCode() {
      // 重置状态
      this.showRefreshOverlay = false
      this.qrCodeUrl = ''
      this.uuid = ''
      this.clearTimers()

      getLoginQrCode().then(res => {
        console.log(res)
        const { data } = res || {}
        this.qrCodeUrl = data.url
        this.uuid = data.uuid
        this.qrStartTime = Date.now() // 记录二维码生成时间
        this.startQRStatusCheck()
        this.startRefreshTimer() // 启动1分钟刷新提示定时器
      })
    },

    // 开始检查二维码状态
    startQRStatusCheck() {
      this.checkTimer = setInterval(() => {
        checkLoginQrCode(this.uuid).then(res => {
          const { data } = res || {}
          const { isLogin = true, companyVOList = [], newUser = false } = data || {}
          console.log('newUser', newUser)
          this.newUser = newUser
          this.isLogin = isLogin
          if (isLogin) {
            this.companyVOList = companyVOList
            this.clearTimers()
          }
        })
      }, 2000)
      // 5分钟后自动过期
      this.qrTimer = setTimeout(() => {
        this.clearTimers()
      }, 300000)
    },

    // 启动1分钟刷新提示定时器
    startRefreshTimer() {
      // 清除之前的定时器
      if (this.refreshTimer) {
        clearTimeout(this.refreshTimer)
      }
      // 1分钟后显示刷新蒙层
      this.refreshTimer = setTimeout(() => {
        // 如果还未登录，显示刷新蒙层
        if (!this.isLogin && this.qrCodeUrl) {
          this.showRefreshOverlay = true
        }
      }, 60000) // 1分钟 = 60000毫秒
    },

    // 刷新二维码
    refreshQRCode() {
      this.showRefreshOverlay = false
      this.generateQRCode()
    },

    // 处理企业点击事件
    handleCompanyClick(company) {
      // 如果已被禁枪，提示并阻止登录
      if (company.grabStatus === 1) {
        this.$message.warning('该供应商已被禁枪')
        return
      }
      // 正常登录流程
      this.getLoginToken(company)
    },
    getLoginToken(company) {
      const { id, owner } = company
      console.log('company', company)
      // 直接设置从点击时获取的 owner 到 store
      this.$store.commit('user/SET_OWNER', owner)
      this.loadingCompanyId = id
      getLoginToken(this.uuid, id).then(res => {
        const { data = '' } = res || {}
        this.$store.dispatch('user/setAccessToken', data)
        // 选择企业后，重新获取用户信息并设置当前企业
        this.$store.dispatch('user/getInfo').then(() => {
          console.log('getInfo success')
          this.$router.push({ path: '/o2o' })
        }).catch(error => {
          console.error('获取用户信息失败:', error)
          this.$message.error('获取用户信息失败，请重试')
        })
      }).catch(error => {
        console.error('登录失败:', error)
        this.$message.error('登录失败，请重试')
      }).finally(() => {
        this.loadingCompanyId = null
      })
    },
    // 清除定时器
    clearTimers() {
      if (this.qrTimer) {
        clearTimeout(this.qrTimer)
        this.qrTimer = null
      }
      if (this.checkTimer) {
        clearInterval(this.checkTimer)
        this.checkTimer = null
      }
      if (this.refreshTimer) {
        clearTimeout(this.refreshTimer)
        this.refreshTimer = null
      }
    },

    // 添加滚动监听
    addScrollListener() {
      const companyList = this.$refs.companyList
      if (companyList) {
        companyList.addEventListener('scroll', this.handleScroll)
      }
    },

    // 移除滚动监听
    removeScrollListener() {
      const companyList = this.$refs.companyList
      if (companyList) {
        companyList.removeEventListener('scroll', this.handleScroll)
      }
    },

    // 处理滚动事件
    handleScroll() {
      if (this.showScrollHint) {
        this.showScrollHint = false
      }
    }

  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;

  .login-wrapper {
    display: flex;
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    max-width: 500px;
    width: 100%;
    justify-content: center;
    align-items: center;

    .qr-section {
      width: 100%;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 60px 40px;

      .qr-container {
        text-align: center;
        width: 100%;
        max-width: 300px;

        .qr-title {
          margin-bottom: 30px;

          h2 {
            font-size: 24px;
            color: #333;
            margin: 0 0 10px 0;
            font-weight: 600;
          }

          p {
            font-size: 14px;
            color: #666;
            margin: 0;
          }
        }

        .qr-code {
          position: relative;
          margin-bottom: 20px;

          .qr-placeholder {
            width: 200px;
            height: 200px;
            margin: 0 auto;
            background: #fff;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

            .qr-loading {
              text-align: center;

              i {
                font-size: 32px;
                color: #409eff;
                margin-bottom: 10px;
                display: block;
              }

              p {
                font-size: 14px;
                color: #666;
                margin: 0;
              }
            }
          }

          .qr-image {
            position: relative;
            width: 200px;
            height: 200px;
            margin: 0 auto;
            background: #fff;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .qr-refresh-overlay {
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              bottom: 0;
              background: rgba(255, 255, 255, 0.95);
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: 12px;
              z-index: 10;

              .refresh-content {
                text-align: center;
                padding: 20px;

                i {
                  font-size: 32px;
                  color: #409eff;
                  margin-bottom: 12px;
                  display: block;
                  animation: spin 1s linear infinite;
                }

                p {
                  font-size: 14px;
                  color: #666;
                  margin: 0 0 16px 0;
                }

                .el-button {
                  min-width: 100px;
                }
              }
            }

            .qr-refresh {
              position: absolute;
              top: 8px;
              right: 8px;
              width: 32px;
              height: 32px;
              background: rgba(255, 255, 255, 0.9);
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              cursor: pointer;
              transition: all 0.3s;
              z-index: 5;

              &:hover {
                background: #fff;
                transform: rotate(180deg);
              }

              i {
                font-size: 16px;
                color: #409eff;
              }
            }
          }
        }

        .qr-status {
          .status-tip {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 12px 20px;
            background: #f8f9fa;
            border-radius: 8px;
            font-size: 14px;
            color: #666;

            i {
              margin-right: 8px;
              font-size: 16px;
            }

            &.scanning {
              background: #e6f7ff;
              color: #1890ff;

              i {
                color: #1890ff;
              }
            }

            &.success {
              background: #f6ffed;
              color: #52c41a;

              i {
                color: #52c41a;
              }
            }

            &.expired {
              background: #fff2e8;
              color: #fa8c16;

              i {
                color: #fa8c16;
              }
            }
          }
        }
      }
    }

    .company-list-container {
      width: 100%;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 60px 40px;

      .company-title {
        text-align: center;
        margin-bottom: 40px;

        h2 {
          font-size: 28px;
          color: #333;
          margin: 0 0 10px 0;
          font-weight: 600;
        }

        p {
          font-size: 16px;
          color: #666;
          margin: 0;
        }
      }

      .company-list {
        width: 100%;
        max-width: 400px;
        max-height: 400px;
        overflow-y: auto;
        overflow-x: hidden;
        padding-right: 8px;

        // 自定义滚动条样式
        &::-webkit-scrollbar {
          width: 6px;
        }

        &::-webkit-scrollbar-track {
          background: #f1f1f1;
          border-radius: 3px;
        }

        &::-webkit-scrollbar-thumb {
          background: #c1c1c1;
          border-radius: 3px;
          transition: background 0.3s;
        }

        &::-webkit-scrollbar-thumb:hover {
          background: #a8a8a8;
        }

        .scroll-hint {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 12px 20px;
          margin-bottom: 16px;
          background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
          border-radius: 8px;
          font-size: 14px;
          color: #666;
          border: 1px dashed #409eff;
          animation: bounce 2s infinite;

          i {
            margin-right: 8px;
            font-size: 16px;
            color: #409eff;
          }
        }

        .company-item {
          position: relative;
          display: flex;
          align-items: center;
          padding: 20px 24px;
          margin-bottom: 16px;
          background: #ffffff;
          border-radius: 12px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
          cursor: pointer;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          border: 2px solid transparent;
          overflow: hidden;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            border-color: #409eff;
          }

          &:active {
            transform: translateY(0);
          }

          &.loading {
            pointer-events: none;
            opacity: 0.7;
          }

          &.disabled {
            background: #fff5f5;
            border-color: #f56c6c;
            cursor: not-allowed;
            opacity: 0.8;

            .company-icon {
              background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
            }

            .company-name {
              color: #f56c6c;
            }

            .company-desc {
              color: #f56c6c;
            }

            .company-arrow i {
              color: #f56c6c;
            }

            &:hover {
              transform: none;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
              border-color: #f56c6c;
            }
          }

          .company-icon {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 16px;
            flex-shrink: 0;

            i {
              font-size: 24px;
              color: #ffffff;
            }
          }

          .company-info {
            flex: 1;
            min-width: 0;

            .company-name {
              font-size: 18px;
              font-weight: 600;
              color: #333;
              margin: 0 0 4px 0;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }

            .company-desc {
              font-size: 14px;
              color: #999;
              margin: 0;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }

          .company-arrow {
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-left: 12px;
            flex-shrink: 0;

            i {
              font-size: 16px;
              color: #c0c4cc;
              transition: all 0.3s;
            }
          }

          &:hover .company-arrow i {
            color: #409eff;
            transform: translateX(2px);
          }

          .loading-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(255, 255, 255, 0.9);
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 12px;

            i {
              font-size: 20px;
              color: #409eff;
              animation: spin 1s linear infinite;
            }
          }
        }
      }

      .company-list-empty {
        width: 100%;
        max-width: 400px;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0px 20px;

        .company-list-empty-icon {
          text-align: center;
          color: #999;

          i {
            font-size: 48px;
            color: hsl(42, 100%, 63%);
            margin-bottom: 16px;
            display: block;
          }

          p {
            font-size: 16px;
            color: #666;
            margin: 0;
            line-height: 1.5;
          }
          .wxadmincode{
            width: 300px;
            margin-top: 20px;
            border-radius: 10px;
            overflow: hidden;
            img{
              width: 100%;
            }
          }
        }
      }
    }

  }

  .copyright {
    margin-top: 30px;
    text-align: center;

    p {
      color: rgba(255, 255, 255, 0.8);
      font-size: 14px;
      margin: 0;
    }
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

@keyframes bounce {

  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }

  40% {
    transform: translateY(-4px);
  }

  60% {
    transform: translateY(-2px);
  }
}
</style>
