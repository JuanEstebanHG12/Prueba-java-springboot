import client from './client'

// Admin-only: full list with all fields (used in VacanciesPage)
export function getAllVacancies() {
  return client.get('/admin/vacancies')
}

export function createVacancy(data) {
  return client.post('/admin/vacancies', data)
}

export function updateVacancy(id, data) {
  return client.patch(`/admin/vacancies/${id}`, data)
}

// Public list — accessible by any logged-in user (candidates, recruiters, admins)
export function getPublicVacancies() {
  return client.get('/vacancies')
}

// Only change the status field, not the full vacancy object
export function changeVacancyStatus(id, data) {
  return client.patch(`/admin/vacancies/${id}/status`, data)
}
