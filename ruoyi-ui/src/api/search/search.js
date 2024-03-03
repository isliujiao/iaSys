import request from '@/utils/request'

// 聚合搜索
export function searchAll(query) {
  return request({
    url: '/search/all',
    method: 'get',
    params: query
  })
}

// 热度显示词语
export function searchHot() {
  return request({
    url: '/search/hot',
    method: 'get'
  })
}