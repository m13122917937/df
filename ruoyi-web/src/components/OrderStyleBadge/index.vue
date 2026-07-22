<template>
  <div>
    <div v-if="orderStyle === 1" class="order-style-badge-wrap">
      <el-tooltip class="item" effect="dark" content="签收激活" placement="bottom">
        <div class="order-style-badge">
          <div class="ticket-icon">签</div>
        </div>
      </el-tooltip>
    </div>
    <div v-else class="order-style-badge-wrap">
      <span class="order-style-default-text">-</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderStyleBadge',
  props: {
    orderStyle: {
      type: Number,
      default: 0
    }
  }
}
</script>

<style scoped>
.order-style-badge-wrap {
     display: flex;
    justify-content: center;
}
.order-style-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid #1677FF;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1677FF;
  font-weight: 600;
  background: #fff;
  box-shadow: 0 2px 6px rgba(64,158,255,0.08);
  cursor: pointer;
  text-align: center;
}
.order-style-img {
  width: 20px;
  height: 20px;
  object-fit: contain;
  display: block;
}
.order-style-default-text {
  font-size: 12px;
  line-height: 1;
}
.ticket-icon {
  font-size: 12px;
  color: #1677FF;
  font-weight: bold;
  width: 24px;
  height: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  border: 1px solid #1677FF;
}
</style>

