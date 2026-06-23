import client from './client'

export function register(data) {
  return client.post('/users/register', data)
}

export function login(data) {
  return client.post('/users/login', data)
}
