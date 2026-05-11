import request from './index'

export function getCustomers() {
  return request.get('/customers')
}

export function searchCustomers(keyword) {
  return request.get('/customers/search', { params: { keyword } })
}

export function getCustomer(id) {
  return request.get(`/customers/${id}`)
}

export function createCustomer(data) {
  return request.post('/customers', data)
}

export function updateCustomer(id, data) {
  return request.put(`/customers/${id}`, data)
}

export function deleteCustomer(id) {
  return request.delete(`/customers/${id}`)
}
