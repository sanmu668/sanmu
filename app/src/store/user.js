import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const username = ref('访客用户')
  const userInfo = ref({
    username: '访客用户',
    subscriptionType: 'free'
  })
  const subscriptionType = ref('free')
  
  const setUserInfo = (info) => {
    userInfo.value = info
    username.value = info.username || '访客用户'
    subscriptionType.value = info.subscriptionType || 'free'
  }
  
  const updateProfile = async (profileData) => {
    try {
      const response = await axios.put('/api/user/profile', profileData)
      setUserInfo(response.data)
      return true
    } catch (error) {
      console.error('Profile update failed:', error)
      return false
    }
  }
  
  const updateSubscription = async (subscriptionData) => {
    try {
      const response = await axios.post('/api/user/subscription', subscriptionData)
      subscriptionType.value = response.data.subscriptionType
      return true
    } catch (error) {
      console.error('Subscription update failed:', error)
      return false
    }
  }
  
  return {
    username,
    userInfo,
    subscriptionType,
    updateProfile,
    updateSubscription
  }
})