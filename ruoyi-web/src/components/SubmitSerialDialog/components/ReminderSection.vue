<template>
  <div class="reminder-section">
    <div class="reminder-header">
      <i class="el-icon-warning" style="color: #FF3B30; margin-right: 8px;" />
      <span class="reminder-title">提醒:</span>
    </div>
    <div class="reminder-content">
      <!-- 包装及串码要求 -->
      <div class="reminder-item critical">
        <div class="reminder-item-header">
          <i class="el-icon-box" />
          <strong>1. 包装及串码要求</strong>
        </div>
        <div class="requirements-container">
          <div class="requirement-group">
            <span class="requirement-label">包装要求:</span>
            <el-tag type="success" size="mini" class="requirement-tag">原箱</el-tag>
            <el-tag type="success" size="mini" class="requirement-tag">原封</el-tag>
          </div>
          <div class="requirement-group">
            <span class="requirement-label">串码要求:</span>
            <el-tag type="danger" size="mini" class="requirement-tag critical-tag">未激活</el-tag>
            <el-tag type="danger" size="mini" class="requirement-tag critical-tag">未绑未锁</el-tag>
            <el-tag type="danger" size="mini" class="requirement-tag critical-tag">拼多多未销售</el-tag>
            <el-tag type="danger" size="mini" class="requirement-tag critical-tag">非定制机</el-tag>
            <el-tag type="danger" size="mini" class="requirement-tag critical-tag">非租赁机</el-tag>
            <el-tag type="danger" size="mini" class="requirement-tag critical-tag">非国补</el-tag>
          </div>
          <div class="warning-text">
            <i class="el-icon-warning" />
            违规损失由供应商承担
          </div>
        </div>
      </div>

      <!-- 寄件人手机号 -->
      <div class="reminder-item">
        <div class="reminder-item-header">
          <i class="el-icon-phone" />
          <strong>2. 寄件人手机号</strong>
        </div>
        <div class="reminder-text">
          运单输入框内填写寄件人真实11位手机号，否则物流信息可能异常。
        </div>
      </div>

      <!-- 取件截止时间 -->
      <div class="reminder-item">
        <div class="reminder-item-header">
          <i class="el-icon-time" />
          <strong>3. 取件截止时间</strong>
        </div>
        <div class="reminder-text">
          发货后物流超过<span class="highlight-time">24小时</span>无取件信息，扣除<span class="highlight-penalty">200元/单</span>罚款，扣除订单保证金，订单<span class="highlight-action">自动退回并跟踪</span>。
        </div>
      </div>

      <!-- 物流修改窗口 -->
      <div class="reminder-item">
        <div class="reminder-item-header">
          <i class="el-icon-edit" />
          <strong>4. 物流修改窗口</strong>
        </div>
        <div class="reminder-text">
          发货后<span class="highlight-time">22小时内</span>可修改物流，超过22小时无法修改。
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReminderSection'
}
</script>

<style lang="scss" scoped>
.reminder-section {
  background: linear-gradient(135deg, #fff7e6 0%, #ffeaa7 100%);
  border: 2px solid #ffd591;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(255, 213, 145, 0.3);

  .reminder-header {
    margin-bottom: 20px;
    font-weight: 600;
    color: #d46b08;
    display: flex;
    align-items: center;
    font-size: 16px;
    gap: 8px;

    i {
      font-size: 18px;
      animation: shake 0.5s ease-in-out infinite alternate;
    }
  }

  .reminder-content {
    .reminder-item {
      margin-bottom: 20px;
      padding: 16px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      border-left: 4px solid #d46b08;
      transition: all 0.3s ease;

      &:last-child {
        margin-bottom: 0;
      }

      &.critical {
        border-left-color: #FF3B30;
        background: rgba(255, 245, 245, 0.9);
        box-shadow: 0 2px 8px rgba(245, 108, 108, 0.2);
      }

      .reminder-item-header {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        font-size: 15px;
        color: #2c3e50;

        i {
          margin-right: 8px;
          color: #d46b08;
          font-size: 16px;
        }

        strong {
          color: #d46b08;
          font-weight: 600;
        }
      }

      .requirements-container {
        .requirement-group {
          margin-bottom: 12px;
          display: flex;
          align-items: center;
          flex-wrap: wrap;
          gap: 8px;

          &:last-child {
            margin-bottom: 0;
          }

          .requirement-label {
            font-weight: 600;
            color: #2c3e50;
            font-size: 14px;
            min-width: 80px;
          }

          .requirement-tag {
            border-radius: 6px;
            font-weight: 500;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;

            &.critical-tag {
              background: linear-gradient(135deg, #FF3B30 0%, #FF6259 100%);
              color: #fff;
              border: none;
              font-weight: 600;
              animation: pulse 2s ease-in-out infinite;
              box-shadow: 0 2px 6px rgba(245, 108, 108, 0.4);

              &:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(245, 108, 108, 0.6);
              }
            }
          }
        }

        .warning-text {
          margin-top: 12px;
          padding: 8px 12px;
          background: rgba(245, 108, 108, 0.1);
          border-radius: 6px;
          border: 1px solid rgba(245, 108, 108, 0.3);
          color: #FF3B30;
          font-weight: 600;
          font-size: 13px;
          display: flex;
          align-items: center;
          gap: 6px;

          i {
            font-size: 14px;
          }
        }
      }

      .reminder-text {
        line-height: 1.6;
        color: #2c3e50;
        font-size: 14px;

        .highlight-time {
          color: #FF3B30;
          font-weight: 600;
          background: rgba(245, 108, 108, 0.1);
          padding: 2px 6px;
          border-radius: 4px;
        }

        .highlight-penalty {
          color: #FF6259;
          font-weight: 700;
          background: rgba(231, 76, 60, 0.1);
          padding: 2px 6px;
          border-radius: 4px;
        }

        .highlight-action {
          color: #d46b08;
          font-weight: 600;
          background: rgba(212, 107, 8, 0.1);
          padding: 2px 6px;
          border-radius: 4px;
        }
      }
    }
  }
}

// 动画效果
@keyframes shake {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(2px);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 2px 6px rgba(245, 108, 108, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(245, 108, 108, 0.6);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 2px 6px rgba(245, 108, 108, 0.4);
  }
}
</style>
