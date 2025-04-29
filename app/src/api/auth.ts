import request from '@/utils/request'

interface AuthRequest {
  email: string
  password: string
}

interface AuthResponse {
  token?: string
  success?: boolean
  message?: string
}

export function login(data: AuthRequest): Promise<AuthResponse> {
  return request.post('/api/auth/login', data)
}

export function register(data: AuthRequest): Promise<AuthResponse> {
  return request.post('/api/auth/register', data)
}