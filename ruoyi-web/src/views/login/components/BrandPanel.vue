<template>
  <aside class="brand-panel">
    <!-- Tech background -->
    <div class="bg-grid" />
    <svg class="bg-svg" viewBox="0 0 600 900" preserveAspectRatio="xMidYMid slice">
      <path d="M80,150 C250,100 300,280 500,200" stroke="rgba(100,180,255,0.08)" stroke-width="2" fill="none" />
      <path d="M30,350 C200,280 280,450 480,380" stroke="rgba(100,180,255,0.06)" stroke-width="1.5" fill="none" stroke-dasharray="8 8" />
      <path d="M150,500 C300,450 380,600 550,520" stroke="rgba(100,180,255,0.04)" stroke-width="1.5" fill="none" />
      <path d="M60,650 C220,580 300,750 500,680" stroke="rgba(100,180,255,0.05)" stroke-width="1.5" fill="none" stroke-dasharray="6 6" />
      <path d="M200,50 C350,120 400,220 550,150" stroke="rgba(100,180,255,0.03)" stroke-width="1" fill="none" />
      <circle cx="80" cy="150" r="3" fill="rgba(100,180,255,0.25)" />
      <circle cx="500" cy="200" r="4" fill="rgba(100,180,255,0.18)" />
      <circle cx="30" cy="350" r="3.5" fill="rgba(100,180,255,0.18)" />
      <circle cx="480" cy="380" r="3" fill="rgba(100,180,255,0.25)" />
      <circle cx="150" cy="500" r="4" fill="rgba(100,180,255,0.18)" />
      <circle cx="550" cy="520" r="3" fill="rgba(100,180,255,0.15)" />
      <circle cx="60" cy="650" r="3.5" fill="rgba(100,180,255,0.22)" />
      <circle cx="500" cy="680" r="4" fill="rgba(100,180,255,0.18)" />
      <circle cx="200" cy="50" r="3" fill="rgba(100,180,255,0.3)" />
      <circle cx="400" cy="220" r="2.5" fill="rgba(100,180,255,0.25)" />
      <circle cx="300" cy="450" r="3" fill="rgba(100,180,255,0.18)" />
      <circle cx="220" cy="580" r="2.5" fill="rgba(100,180,255,0.25)" />
    </svg>
    <div class="bg-glow" />
    <div class="bg-nodes">
      <span v-for="i in 8" :key="i" class="node" :style="{
        left: `${10 + (i * 17) % 75}%`,
        top: `${8 + (i * 23) % 80}%`,
        animationDelay: `${i * 0.7}s`,
        animationDuration: `${5 + (i % 3) * 2}s`
      }" />
    </div>

    <!-- Content -->
    <div class="brand-content">
      <div class="brand-header">
        <svg viewBox="0 0 36 36" width="36" height="36">
          <rect width="36" height="36" rx="8" fill="#1677FF"/>
          <path d="M10 18 L15 23 L26 12" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
        </svg>
        <span class="brand-name">無界电商</span>
      </div>

      <div class="brand-tagline">
        <h1>智能连接电商业务<br/>让管理更高效</h1>
      </div>

      <p class="brand-desc">
        为企业提供订单、商品、库存、数据分析<br/>与协同管理的一体化平台
      </p>

      <div class="capabilities">
        <div class="cap-item">
          <span class="cap-check">✓</span>
          <span>数据智能分析</span>
        </div>
        <div class="cap-item">
          <span class="cap-check">✓</span>
          <span>企业流程自动化</span>
        </div>
        <div class="cap-item">
          <span class="cap-check">✓</span>
          <span>多端协同管理</span>
        </div>
      </div>
    </div>
  </aside>
</template>

<script>
export default { name: 'BrandPanel' }
</script>

<style lang="scss" scoped>
$apple-ease: cubic-bezier(0.16, 1, 0.3, 1);

.brand-panel {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(150deg, #0A1628 0%, #12284A 35%, #0D3B6E 65%, #0A5C9E 100%);
  overflow: hidden;
  user-select: none;

  .bg-grid {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255,255,255,0.025) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.025) 1px, transparent 1px);
    background-size: 48px 48px;
  }

  .bg-svg {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
  }

  .bg-glow {
    position: absolute;
    top: -20%;
    right: -10%;
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, rgba(37,99,255,0.08) 0%, transparent 70%);
    pointer-events: none;
  }

  .bg-nodes {
    position: absolute;
    inset: 0;
    pointer-events: none;

    .node {
      position: absolute;
      width: 5px;
      height: 5px;
      background: rgba(100, 180, 255, 0.35);
      border-radius: 50%;
      box-shadow: 0 0 8px rgba(100, 180, 255, 0.2);
      animation: node-pulse 6s ease-in-out infinite;
    }
  }

  .brand-content {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 400px;
    padding: 0 60px;
    animation: fade-in 0.8s $apple-ease both;
  }

  .brand-header {
    display: flex;
    align-items: center;
    margin-bottom: 56px;

    svg {
      flex-shrink: 0;
      filter: drop-shadow(0 2px 10px rgba(37,99,255,0.45));
    }

    .brand-name {
      margin-left: 14px;
      font-size: 22px;
      font-weight: 600;
      color: #fff;
      letter-spacing: 1px;
    }
  }

  .brand-tagline {
    margin-bottom: 20px;

    h1 {
      font-size: 32px;
      font-weight: 700;
      color: #fff;
      line-height: 1.45;
      margin: 0;
      letter-spacing: 0.5px;
    }
  }

  .brand-desc {
    font-size: 14px;
    line-height: 1.7;
    color: rgba(255, 255, 255, 0.5);
    margin: 0 0 48px 0;
  }

  .capabilities {
    display: flex;
    flex-direction: column;
    gap: 18px;
  }

  .cap-item {
    display: flex;
    align-items: center;
    font-size: 15px;
    color: rgba(255, 255, 255, 0.78);
    font-weight: 400;
    letter-spacing: 0.3px;
  }

  .cap-check {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: rgba(52, 199, 89, 0.18);
    color: #34C759;
    font-size: 12px;
    font-weight: 700;
    margin-right: 14px;
    flex-shrink: 0;
  }
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes node-pulse {
  0%, 100% { opacity: 0.25; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.6); }
}

@media (prefers-reduced-motion: reduce) {
  .brand-content { animation: none; }
  .node { animation: none; }
}
</style>
