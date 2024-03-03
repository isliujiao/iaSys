import request from '@/utils/request'

// 聚合搜索
export function closeUserConnect(token) {
  return request({
    url: '/chat/close',
    method: 'get',
    params: token
  })
}