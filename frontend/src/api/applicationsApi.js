import client from './client'

// Candidate submits an application for a vacancy
export function applyToVacancy(data) {
  return client.post('/applications', data)
}

// Admin views all applications across every candidate
export function getAllApplications() {
  return client.get('/admin/applications')
}

// Candidate views only their own applications
export function getApplicationsByCandidate(candidateId) {
  return client.get(`/applications/candidate/${candidateId}`)
}

// Admin moves an application through the hiring pipeline
export function changeApplicationStatus(id, data) {
  return client.patch(`/admin/applications/${id}/status`, data)
}
