import request from './index'

export function getCaptcha() {
  return request.get('/auth/captcha')
}

export function login(data) {
  return request.post('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}
