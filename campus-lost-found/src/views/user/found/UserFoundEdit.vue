<template>
  <div class="user-found-edit-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>编辑招领信息</h2>
      <p>修改拾获物品信息</p>
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
        class="found-form"
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

        <!-- 拾获信息 -->
        <el-form-item label="拾获地点" prop="foundLocation">
          <el-input
            v-model="form.foundLocation"
            placeholder="请输入详细拾获地点"
            clearable
          />
        </el-form-item>

        <el-form-item label="拾获时间" prop="foundTime">
          <el-date-picker
            v-model="form.foundTime"
            type="datetime"
            placeholder="选择拾获时间"
            style="width: 100%"
          />
        </el-form-item>

        <!-- 保管信息 -->
        <el-form-item label="保管地点" prop="storageLocation">
          <el-input
            v-model="form.storageLocation"
            placeholder="请输入物品当前保管地点"
            clearable
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
            <el-option label="已认领" value="claimed" />
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
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { getFoundDetail, updateFoundItem } from '@/api/found'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
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
  foundLocation: '',    // 后端字段名：foundLocation
  foundTime: '',
  storageLocation: '',  // 后端字段名：storageLocation
  description: '',
  contactInfo: '',      // 后端字段名：contactInfo
  status: 'pending',
  userId: 0
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
  ],
  status: [
    { required: true, message: '请选择物品状态', trigger: 'change' }
  ]
}

onMounted(() => {
  fetchItemDetail()
})

// 获取物品详情
const fetchItemDetail = async () => {
  const itemId = route.params.id
  if (!itemId) {
    ElMessage.error('物品ID不存在')
    router.push('/user/found')
    return
  }

  try {
    const response = await getFoundDetail(itemId)
    
    // 适配后端返回格式
    // 后端返回：{"code":200,"message":"success","data":{...}}
    const data = response.data || response
    
    // 填充表单数据 - 注意字段映射
    form.title = data.title || ''
    form.category = data.category || ''
    form.foundLocation = data.foundLocation || ''
    form.foundTime = data.foundTime ? new Date(data.foundTime) : ''
    form.storageLocation = data.storageLocation || ''
    form.description = data.description || ''
    form.contactInfo = data.contactInfo || data.contact_info || ''
    form.status = data.status || 'pending'
    form.userId = data.userId || userStore.userInfo?.id || 0
    
  } catch (error) {
    console.error('获取物品详情失败:', error)
    ElMessage.error('加载失败')
    router.push('/user/found')
  } finally {
    loading.value = false
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
        
        // 准备提交数据
        const submitData = {
          id: itemId,
          title: form.title,
          category: form.category,
          foundLocation: form.foundLocation,
          foundTime: form.foundTime ? form.foundTime.toISOString() : new Date().toISOString(),
          storageLocation: form.storageLocation,
          description: form.description,
          contactInfo: form.contactInfo,
          status: form.status,
          userId: form.userId
        }
        
        console.log('提交数据:', submitData)
        
        const response = await updateFoundItem(itemId, submitData)
        
        if (response.success) {
          ElMessage.success('修改成功')
          router.push('/user/found')
        } else if (response.note) {
          ElMessage.warning(response.note)
        } else {
          ElMessage.error(response.error || '修改失败')
        }
      } catch (error) {
        console.error('修改失败:', error)
        ElMessage.error('修改失败，请重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 返回上一页
const goBack = () => {
  router.push('/user/found')
}
</script>

<style scoped>
/* 样式保持不变 */
.user-found-edit-container {
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

.found-form {
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