<!-- UserLostAddTest.vue -->
<template>
    <div>
      <h2>测试发布接口</h2>
      <el-input v-model="testTitle" placeholder="标题" />
      <el-input v-model="testLocation" placeholder="地点" />
      <el-button @click="testPublish">测试发布</el-button>
      <div>{{ testResult }}</div>
    </div>
  </template>
  
  <script setup>
  import { createLostItem } from '@/api/lost'
  import { useUserStore } from '@/store/user'
  import { ref } from 'vue'
  
  const userStore = useUserStore()
  const testTitle = ref('测试物品')
  const testLocation = ref('图书馆')
  const testResult = ref('')
  
  const testPublish = async () => {
    try {
      const data = {
        userId: userStore.userInfo?.id || 1,
        title: testTitle.value,
        lostLocation: testLocation.value,
        category: 'books',
        description: '测试描述',
        contactInfo: '123456789',
        lostTime: new Date().toISOString()
      }
      
      console.log('测试数据:', data)
      const response = await createLostItem(data)
      testResult.value = JSON.stringify(response)
      console.log('测试响应:', response)
    } catch (error) {
      testResult.value = '错误: ' + error.message
      console.error('测试错误:', error)
    }
  }
  </script>