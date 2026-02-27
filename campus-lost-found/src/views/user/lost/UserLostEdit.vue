<template>
  <div class="user-lost-edit-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>编辑失物信息</h2>
      <p>修改丢失物品信息</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 表单 -->
    <div v-else class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="lost-form"
      >
        <!-- 基本物品信息 -->
        <el-form-item label="物品名称" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入物品名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="物品分类" prop="category">
          <el-select
            v-model="form.category"
            placeholder="请选择物品分类"
            clearable
            style="width: 100%"
          >
            <el-option label="电子产品" value="electronics" />
            <el-option label="学习用品" value="study" />
            <el-option label="证件卡类" value="cards" />
            <el-option label="衣物饰品" value="clothing" />
            <el-option label="书籍资料" value="books" />
            <el-option label="其他物品" value="others" />
          </el-select>
        </el-form-item>

        <!-- 丢失信息 -->
        <el-form-item label="丢失地点" prop="lostLocation">
          <el-input
            v-model="form.lostLocation"
            placeholder="请输入详细丢失地点"
            clearable
          />
        </el-form-item>

        <el-form-item label="丢失时间" prop="lostTime">
          <el-date-picker
            v-model="form.lostTime"
            type="datetime"
            placeholder="选择丢失时间"
            style="width: 100%"
            :default-value="new Date()"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <!-- 详细描述 -->
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述物品特征"
            clearable
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 联系方式 -->
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="form.contactInfo"
            placeholder="请输入您的联系方式"
            clearable
          />
        </el-form-item>

        <!-- 状态 -->
        <el-form-item label="物品状态" prop="status">
          <el-select
            v-model="form.status"
            placeholder="请选择物品状态"
            style="width: 100%"
          >
            <el-option label="待处理" value="pending" />
            <el-option label="已发布" value="published" />
            <el-option label="已找到" value="found" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="submitForm">
              保存修改
            </el-button>
            <el-button @click="goBack">取消</el-button>
            <el-button @click="testApiConnection" type="warning" plain>
              测试API
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { getLostDetail, updateLostItem } from '@/api/lost'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(true)
const submitting = ref(false)

// 表单数据 - 注意字段名与后端保持一致
const form = reactive({
  title: '',
  category: '',
  lostLocation: '',
  lostTime: '',
  description: '',
  contactInfo: '',
  status: 'pending',
  userId: 0
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入物品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  lostLocation: [
    { required: true, message: '请输入丢失地点', trigger: 'blur' }
  ],
  lostTime: [
    { required: true, message: '请选择丢失时间', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' },
    { min: 10, message: '描述至少需要10个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择物品状态', trigger: 'change' }
  ]
}

onMounted(() => {
  fetchItemDetail()
})

// 格式化时间函数
const formatDate = (date) => {
  if (!date) return ''
  if (typeof date === 'string') {
    // 如果已经是字符串格式，直接返回
    return date
  }
  if (date instanceof Date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }
  return ''
}

// 获取物品详情
const fetchItemDetail = async () => {
  const itemId = route.params.id
  if (!itemId) {
    ElMessage.error('物品ID不存在')
    router.push('/user/UserLostList')
    return
  }

  try {
    const response = await getLostDetail(itemId)
    
    console.log('获取的详情数据:', response)
    
    // 适配后端返回格式
    let data
    if (response.data) {
      data = response.data // 如果返回格式是 { code: 200, data: {...} }
    } else if (response.result) {
      data = response.result // 如果是 { success: true, result: {...} }
    } else {
      data = response // 直接返回数据
    }
    
    if (!data) {
      throw new Error('未获取到数据')
    }
    
    // 填充表单数据 - 注意字段映射
    form.title = data.title || ''
    form.category = data.category || ''
    form.lostLocation = data.lostLocation || data.lost_location || ''
    form.lostTime = data.lostTime || data.lost_time || ''
    form.description = data.description || ''
    form.contactInfo = data.contactInfo || data.contact_info || ''
    form.status = data.status || 'pending'
    form.userId = data.userId || data.user_id || userStore.userInfo?.id || 0
    
    console.log('填充后的表单数据:', form)
    
  } catch (error) {
    console.error('获取物品详情失败:', error)
    ElMessage.error('加载失败: ' + (error.message || '未知错误'))
    router.push('/user/UserLostList')
  } finally {
    loading.value = false
  }
}

// 测试API连接
const testApiConnection = async () => {
  const itemId = parseInt(route.params.id, 10)
  if (!itemId) {
    ElMessage.error('物品ID无效')
    return
  }

  try {
    // 1. 测试GET请求是否能获取数据
    const getResponse = await axios.get(`/api/lost/${itemId}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    console.log('GET请求成功:', getResponse.data)
    ElMessage.success(`获取数据成功: ${getResponse.data.title}`)
    
    // 2. 测试PUT请求
    const testData = {
      id: itemId,
      title: '测试修改' + Date.now(),
      category: 'others',
      lostLocation: '测试地点',
      lostTime: formatDate(new Date()),
      description: '测试描述',
      contactInfo: '测试联系方式',
      status: 'pending',
      userId: userStore.userInfo?.id || 0
    }
    
    console.log('测试数据:', testData)
    
    const putResponse = await axios.put(`/api/lost/${itemId}`, testData, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    console.log('PUT请求成功:', putResponse.data)
    ElMessage.success('API连接测试成功')
    
  } catch (error) {
    console.error('API测试失败:', {
      错误: error.message,
      响应: error.response?.data,
      状态码: error.response?.status,
      请求URL: error.config?.url,
      请求数据: error.config?.data
    })
    
    if (error.response) {
      ElMessage.error(`API测试失败: ${error.response.status} - ${error.response.data?.message || error.message}`)
    } else {
      ElMessage.error(`API测试失败: ${error.message}`)
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const itemId = route.params.id
        
        if (!itemId) {
          ElMessage.error('物品ID不能为空')
          submitting.value = false
          return
        }
        
        // 准备提交数据 - 关键修改：确保数据类型正确
        const submitData = {
          id: parseInt(itemId, 10), // ✅ 确保是数字类型
          title: form.title,
          category: form.category,
          lostLocation: form.lostLocation,
          lostTime: formatDate(form.lostTime), // 使用统一的格式化函数
          description: form.description,
          contactInfo: form.contactInfo,
          status: form.status,
          userId: parseInt(form.userId, 10) || userStore.userInfo?.id || 0 // ✅ 确保是数字类型
        }
        
        console.log('提交数据详情:', {
          数据: submitData,
          数据类型: {
            id: typeof submitData.id,
            userId: typeof submitData.userId,
            lostTime: submitData.lostTime
          }
        })
        
        // 验证必要字段
        if (isNaN(submitData.id) || submitData.id <= 0) {
          ElMessage.error('物品ID无效')
          submitting.value = false
          return
        }
        
        if (!submitData.userId || isNaN(submitData.userId) || submitData.userId <= 0) {
          ElMessage.error('用户信息异常')
          submitting.value = false
          return
        }
        
        const response = await updateLostItem(itemId, submitData)
        
        console.log('API响应:', response)
        
        if (response.success || response.code === 200) {
          ElMessage({
            message: '修改成功！',
            type: 'success',
            duration: 2000,
            showClose: true
          })
          
          // 延迟跳转，让用户看到成功消息
          setTimeout(() => {
            router.push('/user/UserLostList')
          }, 1500)
        } else {
          // 显示详细的错误信息
          const errorMsg = response.error || response.message || '修改失败'
          ElMessage.error(`修改失败: ${errorMsg}`)
          
          // 如果有错误字段信息，显示出来
          if (response.errors) {
            console.error('字段错误详情:', response.errors)
          }
        }
      } catch (error) {
        console.error('修改失败详情:', {
          错误对象: error,
          错误消息: error.message,
          响应数据: error.response?.data,
          状态码: error.response?.status
        })
        
        // 根据错误类型显示不同的提示
        if (error.response) {
          if (error.response.status === 401) {
            ElMessage.error('登录已过期，请重新登录')
            router.push('/login')
          } else if (error.response.status === 403) {
            ElMessage.error('没有修改权限')
          } else if (error.response.status === 404) {
            ElMessage.error('找不到该物品')
          } else if (error.response.status === 422) {
            ElMessage.error('数据验证失败: ' + (error.response.data?.message || '请检查填写内容'))
          } else {
            ElMessage.error(`服务器错误: ${error.response.status}`)
          }
        } else if (error.request) {
          ElMessage.error('网络错误，请检查网络连接')
        } else {
          ElMessage.error('请求配置错误: ' + error.message)
        }
      } finally {
        submitting.value = false
      }
    }
  })
}

// 返回上一页
const goBack = () => {
  router.push('/user/UserLostList')
}
</script>

<style scoped>
.user-lost-edit-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 30px;
}

.page-title h2 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 24px;
}

.page-title p {
  margin: 0;
  color: #7f8c8d;
}

.loading-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 20px;
}

.form-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.lost-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .form-container {
    padding: 20px;
  }
  
  .lost-form {
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>