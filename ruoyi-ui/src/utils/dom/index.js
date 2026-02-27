export const windowResize = {
  cache: new Map(),
  resize(event) {
    windowResize.cache.forEach(callback => callback(event))
  },

  addListener(callback, once = false) {
    if (!this.cache.size) window.addEventListener("resize", this.resize, once)
    if (!this.cache.get(callback)) this.cache.set(callback, callback)
  },
  removeListener(callback) {
    if (this.cache.get(callback)) this.cache.delete(callback)
    if (!this.cache.size) window.removeEventListener("resize", this.resize)
  }
}

export const domComputed = {
  getTotalHeight(styles, arr = []) {
    return arr.reduce((total, item) => total + parseFloat(styles.getPropertyValue(item)), 0)
  },
  toDom(dom) {
    return typeof dom === "string" ? document.querySelector(dom) : dom
  },
  getHeight(obj) {
    const target = this.toDom(obj.target)
    if (!target) return 0
    const allHeight = document.documentElement.clientHeight
    const top = target.getBoundingClientRect()?.top ?? 0
    // const marginHeight = this.getTotalHeight(window.getComputedStyle(target),['margin-top', 'margin-bottom']);
    const bottom = obj.bottom ?? 24
    return allHeight - top - bottom
  }
}
