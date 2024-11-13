import request from '@/utils/request'

// 关闭用户连接
export function closeUserConnect(token) {
  return request({
    url: '/chat/close',
    method: 'get',
    params: token
  })
}

export function getMessageNoticeList() {
  return request({
    url: '/chat/getMessageNoticeList',
    method: 'get'
  })
}