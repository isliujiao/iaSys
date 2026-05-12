import request from '@/utils/request'

// 查询文档列表
export function listDocument(query) {
  return request({
    url: '/rag/document/list',
    method: 'get',
    params: query
  })
}

// 查询文档详情
export function getDocument(id) {
  return request({
    url: '/rag/document/' + id,
    method: 'get'
  })
}

// 上传文档
export function uploadDocument(data) {
  return request({
    url: '/rag/document/upload',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 删除文档
export function delDocument(id) {
  return request({
    url: '/rag/document/' + id,
    method: 'delete'
  })
}

// 重新处理文档
export function reprocessDocument(id) {
  return request({
    url: '/rag/document/reprocess/' + id,
    method: 'post'
  })
}
