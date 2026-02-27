const imgSrc = fn => {
  const src = process.env.VUE_APP_IMAGE_DOMAIN
  if (fn) {
    return fn(src)
  }
  return src
}

export default imgSrc
