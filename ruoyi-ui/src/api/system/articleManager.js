import request from '@/utils/request'

// 增加发布文章
export function addArticle(data) {
  return request({
    url: '/articleManager/insertPostList',
    method: 'post',
    data: data
  })
}
