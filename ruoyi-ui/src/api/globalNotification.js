import request from '@/utils/request'

// 拉取是否需要弹全局消息，后端应返回类似：
// { code: 200, data: [{ id: 'abc', type: 'info', title: '标题', message: '内容', duration: 0 }] }
export function fetchGlobalNotifications() {
  return request({
    url: `/apply/imei/sum/1`,
    method: "POST"
  });
}


