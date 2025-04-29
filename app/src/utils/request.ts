import axios  from "axios";
import { error } from "console";

const request = axios.create({
  baseURL: import.meta.env.VITE_BASE_API || '',
  timeout: 30000
})

// 添加请求拦截器
request.interceptors.request.use(config =>{
  const token = localStorage.getItem('token')
  if (token){
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
},error =>{
  return Promise.reject(error)
})


// 添加响应拦截器
request.interceptors.response.use(response => {
  return response.data
},error =>{
  return Promise.reject(error)
})

export default request
