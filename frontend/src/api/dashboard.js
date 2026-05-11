import request from './index'

export function getTodoStats() {
  return request.get('/dashboard/todo')
}

export function getMetrics() {
  return request.get('/dashboard/metrics')
}

export function getStatusDistribution() {
  return request.get('/dashboard/status-distribution')
}
