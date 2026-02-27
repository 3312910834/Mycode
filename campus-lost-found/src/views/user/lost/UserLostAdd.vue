<template>
  <div class="user-lost-add-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>发布失物信息</h2>
      <p>详细描述丢失物品信息，提高找回几率</p>
    </div>

    <!-- 表单 -->
    <div class="form-container">
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

        <!-- 丢失信息 -->
        <el-form-item label="丢失地点" prop="lostLocation">
          <el-input
            v-model="form.lostLocation"
            placeholder="请输入详细丢失地点，如：图书馆三楼阅览室"
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
            <el-button @click="$router.push('/user/UserLostList')">返回列表</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { createLostItem } from '@/api/lost'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const formRef = ref()

// 表单数据 - 注意字段名与后端保持一致
const form = reactive({
  title: '',
  category: 'others',
  lostLocation: '',  // 后端字段名：lostLocation
  lostTime: new Date(),
  description: '',
  contactInfo: '',    // 后端字段名：contactInfo
  userId: userStore.userInfo?.id || 0
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
  ]
}

const loading = ref(false)

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 设置userId
        form.userId = userStore.userInfo?.id || 0
        if (!form.userId) {
          ElMessage.error('用户信息异常，请重新登录')
          return
        }
        
// 格式化时间：把Date对象转成 "yyyy-MM-dd HH:mm:ss" 格式（后端能识别）
const formatDate = (date) => {
  if (!date) return '';
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从0开始，补零
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 提交数据组装（修改lostTime的格式化方式）
const submitData = {
  userId: form.userId,
  title: form.title,
  category: form.category,
  lostLocation: form.lostLocation,
  lostTime: formatDate(form.lostTime), // 替换toISOString()，用自定义格式化
  description: form.description,
  contactInfo: form.contactInfo || userStore.userInfo?.phone || ''
};
        
        console.log('发布数据:', submitData)
        
        const response = await createLostItem(submitData)
        
        if (response.success) {
          ElMessage.success('发布成功！')
          
          // 重置表单
          resetForm()
          
          // 跳转到列表页
          setTimeout(() => {
            router.push('/user/UserLostList')
          }, 1500)
        } else {
          ElMessage.error(response.error || '发布失败，请重试')
        }
      } catch (error) {
        console.error('发布失败:', error)
        ElMessage.error('发布失败，请重试')
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
  form.lostTime = new Date()
  form.category = 'others'
  form.userId = userStore.userInfo?.id || 0
}
</script>

<style scoped>
/* 样式保持不变 */
.user-lost-add-container {
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

.lost-form {
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