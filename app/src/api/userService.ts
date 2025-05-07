import request from '@/utils/request'
import type { User, UserDTO, UserQueryParams, PageResponse } from '@/types/user'

export const createUser = (user: User) => {
  return request<User>({
    url: '/user',
    method: 'post',
    data: user
  })
}

export const getUserById = (id: number) => {
  return request<UserDTO>({
    url: `/user/${id}`,
    method: 'get'
  })
}

export const getAllUsers = (params?: UserQueryParams) => {
  return request<PageResponse<UserDTO>>({
    url: '/user',
    method: 'get',
    params
  })
}

export const deleteUser = (id: number) => {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export const getUserByUsername = (username: string) => {
  return request<UserDTO[]>({
    url: `/user/username/${username}`,
    method: 'get'
  })
}

export const getUserByEmail = (email: string) => {
  const encodedEmail = encodeURIComponent(email.trim())
  return request<UserDTO>({
    url: `/user/email/${encodedEmail}`,
    method: 'get'
  })
}

export const updateUser = (id: number, user: Partial<User>) => {
  return request<User>({
    url: `/user/${id}`,
    method: 'put',
    data: user
  })
}