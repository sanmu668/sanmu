export function parseJwt(token: string){
  if (!token) return null
  const base64Payload = token.split('.')[1]
  const payload = atob(base64Payload)
  return JSON.parse(payload)
}