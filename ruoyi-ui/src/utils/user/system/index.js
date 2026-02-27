function getUserSystem() {
  const platform = navigator.platform
  if (["Win32", "Windows"].includes(platform)) return "Windows"
  if (["Mac68K", "MacPPC", "Macintosh", "MacIntel"].includes(platform)) return "Mac"
  if (["X11"].includes(platform)) return "Unix"
  if (("" + platform).indexOf("Linux") > -1) return "Linux"
  return "NonMainstream"
}

export const userSystem = getUserSystem()
