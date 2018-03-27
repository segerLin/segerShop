import http from './public'
// 登陆
export const userLogin = (params) => {
  return http.fetchPost('/user/login', params)
}
// 退出登陆
export const loginOut = (params) => {
  return http.fetchGet('/user/logout', params)
}
// 用户信息
export const userInfo = (params) => {
  return http.fetchGet('/users/userInfo', params)
}
// 注册账号
export const register = (params) => {
  return http.fetchPost('/user/register', params)
}
// 上传图片
export const upload = (params) => {
  return http.fetchPost('/users/upload', params)
}
// 修改头像
export const updateheadimage = (params) => {
  return http.fetchPost('/users/updateheadimage', params)
}
// 首页接口
export const productHome = (params) => {
  return http.fetchGet('/goods/productHome', params)
}

