import request from './request'
import qs from 'qs'

export interface LoginParams {
  username: string
  userpassword: string
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface LoginData {
  token: string
}

export interface UserInfo {
  id: number
  name: string
  roleId: number
}

/**
 * 用户登录
 * 注意：后端接口接收 application/x-www-form-urlencoded 格式
 */
export function login(data: LoginParams): Promise<ApiResponse<LoginData>> {
  return request({
    url: '/user/login',
    method: 'post',
    data: qs.stringify(data),
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo(token: string): Promise<ApiResponse<UserInfo>> {
  return request({
    url: `/user/info/${token}`,
    method: 'get'
  })
}

/**
 * 用户登出
 */
export function logout(token: string): Promise<ApiResponse> {
  return request({
    url: `/user/logout/${token}`,
    method: 'get'
  })
}

/**
 * 用户注册
 */
export function register(data: any): Promise<ApiResponse> {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}
