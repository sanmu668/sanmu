export interface User {
  id?: number
  username: string
  email: string
  number: string
  passwordHash?: string
  photo?: string | null
  role?: string
  createdAt?: string
  updateAt?: string
  lastLogin?: string | null
  photoBase64?: string | null
}

export interface UserDTO extends Omit<User, 'passwordHash'> {
  id: number
  photoBase64: string | null
  createdAt: string
} 
