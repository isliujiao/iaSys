import request from '@/utils/request'
import axios from 'axios';
import { getToken } from "@/utils/auth";

// bi生成
// export function genChartByAiUsingPOST(data) {
//   return request({
//     url: '/chart/gen',
//     method: 'post',
//     params: data,
//     headers: {
//       'Content-Type': 'multipart/form-data'
//     }
//   })
// }

// bi生成
export function genChartByAiUsingPOST(formData) {
  return axios.post(process.env.VUE_APP_BASE_API + '/chart/gen', formData, {
    headers: {
      Authorization: "Bearer " + getToken(),
      'Content-Type': 'multipart/form-data'
    }
  });
}
// bi 异步生成
export function genChartByAiAsync(formData) {
  return axios.post(process.env.VUE_APP_BASE_API + '/chart/gen/async/mq', formData, {
    headers: {
      Authorization: "Bearer " + getToken(),
      'Content-Type': 'multipart/form-data'
    }
  });
}

export function listMyChartByPage(searchParams) {
  return request({
    url: '/chart/my/list/page',
    method: 'get',
    params: searchParams,
  })
}