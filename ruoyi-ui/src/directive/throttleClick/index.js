const DEFAULT_WAIT = 600

function resolveOptions(binding) {
  if (typeof binding.value === "number") {
    return { wait: Math.max(binding.value, 0) || DEFAULT_WAIT }
  }

  if (binding.value && typeof binding.value === "object") {
    return {
      wait: Math.max(Number(binding.value.wait) || DEFAULT_WAIT, 0)
    }
  }

  return { wait: DEFAULT_WAIT }
}

function setup(el, binding) {
  const options = resolveOptions(binding)
  const state = {
    locked: false,
    wait: options.wait,
    timer: null,
    listener: null
  }

  const onClick = function (event) {
    if (!state.locked) {
      state.locked = true
      state.timer = setTimeout(() => {
        state.locked = false
        state.timer = null
      }, state.wait)
      return
    }

    if (event) {
      event.stopImmediatePropagation()
      if (typeof event.preventDefault === "function") {
        event.preventDefault()
      }
    }
  }

  state.listener = onClick
  el.__throttleClick__ = state
  el.addEventListener("click", onClick, true)
}

function cleanup(el) {
  const state = el.__throttleClick__
  if (!state) return

  if (state.listener) {
    el.removeEventListener("click", state.listener, true)
  }
  if (state.timer) {
    clearTimeout(state.timer)
  }
  delete el.__throttleClick__
}

export default {
  bind(el, binding) {
    setup(el, binding)
  },
  update(el, binding) {
    if (binding.value === binding.oldValue) return
    cleanup(el)
    setup(el, binding)
  },
  unbind(el) {
    cleanup(el)
  }
}

