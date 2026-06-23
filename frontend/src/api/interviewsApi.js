import client from './client'

// Admin views all scheduled interviews
export function getAllInterviews() {
  return client.get('/admin/interviews')
}

// Admin books a new interview for an application
export function createInterview(data) {
  return client.post('/admin/interviews', data)
}

// Fetch interviews linked to a specific application (used inside ApplicationsPage modal)
export function getInterviewsByApplication(applicationId) {
  return client.get(`/admin/interviews/application/${applicationId}`)
}

// Admin updates the outcome or reschedule status of an interview
export function changeInterviewStatus(id, data) {
  return client.patch(`/admin/interviews/${id}/status`, data)
}
