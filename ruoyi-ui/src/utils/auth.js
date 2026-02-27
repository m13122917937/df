import Cookies from "js-cookie";

import store from "@/store/index";
import { isNull, notNull } from "./index";




const TokenKey = "Admin-Token";

export function getToken() {
  return Cookies.get(TokenKey);
}

export function setToken(token) {
  return Cookies.set(TokenKey, token);
}

export function removeToken() {
  return Cookies.remove(TokenKey);
}

/**
 * 判断当前角色支不支持某个按钮权限
 * @param { string[] } permissionsArr 授权字符串数组
 * @param { string[] | undefined } storePermissions 筛选字符串数组 不传为 当前用户所有权限
 * @returns
 */
export function hasPermission(permissionsArr, storePermissions) {
  const all_permission = "*:*:*";
  let permissions;
  if (notNull(storePermissions)) {
    permissions = storePermissions;
  } else {
    permissions = store.getters && store.getters.permissions;
  }

  if (
    permissionsArr &&
    permissionsArr instanceof Array &&
    permissionsArr.length > 0
  ) {
    const permissionFlag = permissionsArr;

    const hasPermissions = permissions.some((permission) => {
      return (
        all_permission === permission || permissionFlag.includes(permission)
      );
    });

    return hasPermissions;
  } else {
    throw new Error(`请设置操作权限标签值`);
  }
}

/**
 * 取出一个数组中有授权的一部分元素
 * @param {*} permissionsArr 授权字符串数组
 * @param {*} key 元素的权限key值
 * @returns
 */
export function filterHasPermissionList(list = [], key = "permissions") {
  if (!list.length) return;
  return list.filter((item) => {
    if (isNull(item[key])) return true;
    return hasPermission(item[key]);
  });
}
