import request from './index'

export function getApplications(params) {
  return request.get('/applications', { params })
}

export function getApplication(id) {
  return request.get(`/applications/${id}`)
}

export function createApplication(data) {
  return request.post('/applications', data)
}

export function updateApplication(id, data) {
  return request.put(`/applications/${id}`, data)
}

export function deleteApplication(id) {
  return request.delete(`/applications/${id}`)
}

export function submitApplication(id) {
  return request.post(`/applications/${id}/submit`)
}

export function approveApplication(id, data) {
  return request.post(`/applications/${id}/approve`, data)
}
