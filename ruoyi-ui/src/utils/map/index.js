const platform = Object.create(null)
platform.getMap = {
  TM: "天猫",
  PDD: "拼多多",
  DY: "抖音",
  KS: "快手",
  JD: "京东",

  JD_MALL: "京东商城",
  BANK: "银行渠道",
  YS: "萤石"
}
platform.shotMap = {}

for (let key in platform.getMap) {
  let shotKey = platform.getMap[key]
  platform.shotMap[shotKey] = key
}

export const platformShotMap = platform.shotMap
