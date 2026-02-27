<template>
  <div class="user-found-add-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>发布招领信息</h2>
      <p>详细描述拾获物品信息，帮助失主找回</p>
    </div>

    <!-- 表单 -->
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="found-form"
      >
        <!-- 基本物品信息 -->
        <el-form-item label="物品名称" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入物品名称，如：黑色钱包、iPhone 13"
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

        <!-- 拾获信息 -->
        <el-form-item label="拾获地点" prop="foundLocation">
          <el-input
            v-model="form.foundLocation"
            placeholder="请输入详细拾获地点，如：图书馆三楼阅览室"
            clearable
          />
        </el-form-item>

        <el-form-item label="拾获时间" prop="foundTime">
          <el-date-picker
            v-model="form.foundTime"
            type="datetime"
            placeholder="选择拾获时间"
            style="width: 100%"
            :default-value="new Date()"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <!-- 保管信息 -->
        <el-form-item label="保管地点" prop="storageLocation">
          <el-input
            v-model="form.storageLocation"
            placeholder="请输入物品当前保管地点，如：宿舍楼XXX室"
            clearable
          />
        </el-form-item>

        <!-- 详细描述 -->
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述物品特征、品牌、颜色、大小、内含物品等信息"
            clearable
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 联系方式 -->
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="form.contactInfo"
            placeholder="请输入您的联系方式，如：电话、微信、QQ等"
            clearable
          />
          <div class="form-tip">
            <el-text type="info">系统将默认使用您的账户联系方式，如需其他联系方式请在此填写</el-text>
          </div>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <div class="form-actions">
            <el-button type="primary" :loading="loading" @click="submitForm">
              提交发布
            </el-button>
            <el-button @click="resetForm">重置</el-button>
            <el-button @click="$router.push('/user/found')">返回列表</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { createFoundItem } from '@/api/found'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const formRef = ref()

// 表单数据
const form = reactive({
  title: '',
  category: 'others',
  foundLocation: '',
  foundTime: '',  // 初始化为空，会自动格式化为正确的字符串格式
  storageLocation: '',
  description: '',
  contactInfo: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入物品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  foundLocation: [
    { required: true, message: '请输入拾获地点', trigger: 'blur' }
  ],
  foundTime: [
    { required: true, message: '请选择拾获时间', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' },
    { min: 10, message: '描述至少需要10个字符', trigger: 'blur' }
  ]
}

const loading = ref(false)

// 格式化时间为后端接受的格式（双重保障）
const formatTimeForBackend = (time) => {
  if (!time) {
    // 如果没有时间，使用当前时间
    const now = new Date()
    const year = now.getFullYear()
    const month = (now.getMonth() + 1).toString().padStart(2, '0')
    const day = now.getDate().toString().padStart(2, '0')
    const hours = now.getHours().toString().padStart(2, '0')
    const minutes = now.getMinutes().toString().padStart(2, '0')
    const seconds = now.getSeconds().toString().padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }
  
  // 如果已经是正确的格式，直接返回
  if (typeof time === 'string' && time.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/)) {
    return time
  }
  
  // 如果是Date对象，转换
  const date = new Date(time)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 获取用户ID
        const userId = userStore.userInfo?.id || 0
        if (!userId) {
          ElMessage.error('用户信息异常，请重新登录')
          loading.value = false
          return
        }
        
        // 格式化时间（双重保障）
        const formattedTime = formatTimeForBackend(form.foundTime)
        
        // 准备提交数据
        const submitData = {
          userId: userId,
          title: form.title,
          category: form.category,
          foundLocation: form.foundLocation,
          foundTime: formattedTime,  // 使用格式化后的时间
          storageLocation: form.storageLocation || '',
          description: form.description,
          contactInfo: form.contactInfo || userStore.userInfo?.phone || ''
        }
        
        console.log('发布数据:', submitData)
        console.log('时间格式检查:', submitData.foundTime)
        console.log('时间格式是否匹配后端要求:', 
          submitData.foundTime.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/) ? '✓ 正确' : '✗ 错误')
        
        const response = await createFoundItem(submitData)
        console.log('发布响应:', response)
        
        if (response.success) {
          ElMessage.success('发布成功！')
          
          // 重置表单
          resetForm()
          
          // 跳转到列表页
          setTimeout(() => {
            router.push('/user/found')
          }, 1500)
        } else if (response.error) {
          ElMessage.error(response.error)
        } else {
          ElMessage.error('发布失败，请重试')
        }
      } catch (error) {
        console.error('发布失败:', error)
        
        // 解析错误信息
        if (error.message) {
          try {
            const errorObj = JSON.parse(error.message)
            if (errorObj.error) {
              ElMessage.error(errorObj.error)
            } else {
              ElMessage.error('发布失败，请重试')
            }
          } catch (e) {
            ElMessage.error(error.message || '发布失败，请重试')
          }
        } else {
          ElMessage.error('发布失败，请重试')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
  
  // 重置表单数据
  form.title = ''
  form.category = 'others'
  form.foundLocation = ''
  form.foundTime = ''  // 重置为空
  form.storageLocation = ''
  form.description = ''
  form.contactInfo = ''
}
</script>

<style scoped>
.user-found-add-container {
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

.form-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.found-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-tip {
  margin-top: 8px;
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
  
  .found-form {
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