import Vue from "vue"
import Vuex from "vuex"
import app from "./modules/app"
import user from "./modules/user"
import tagsView from "./modules/tagsView"
import permission from "./modules/permission"
import settings from "./modules/settings"
import globalData from "./modules/globalData"
import getters from "./getters"

import commodity from "./modules/commodity"
import vendorAudit from "./modules/vendorAudit"
import provinceAndCity from "./modules/provinceAndCity";

Vue.use(Vuex)
const store = new Vuex.Store({
  modules: {
    app,
    user,
    tagsView,
    permission,
    settings,
    globalData,
    commodity,
    vendorAudit,
    provinceAndCity,
  },
  getters,
});

export default store
