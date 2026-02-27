// Lightweight stub for attaching custom scrollbar behavior.
// This ensures import '@/utils/attachCustomScrollbar' resolves even if
// advanced custom scrollbar directive is not present.
export default function startAutoAttach() {
  // No-op: keep for compatibility. If you later add a customScrollbar directive
  // you can implement DOM observation and attachment here.
  return {
    stop() {
      // placeholder
    },
    checkNow() {
      // placeholder
    }
  }
}


