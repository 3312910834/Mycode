<template>
  <div class="suggestion-add-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>提交建议</h2>
      <p>为系统改进提供宝贵意见</p>
    </div>

    <!-- 表单 -->
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="suggestion-form"
      >
        <el-form-item prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入您的建议或意见，例如功能改进、界面优化、bug反馈等..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <!-- 建议分类 -->
        <el-form-item label="建议分类" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="function">功能建议</el-radio>
            <el-radio label="ui">界面优化</el-radio>
            <el-radio label="bug">Bug反馈</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 联系方式 -->
        <el-form-item label="联系方式（可选）">
          <el-input
            v-model="form.contact"
            placeholder="如需回复，请留下联系方式（电话/邮箱）"
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <div class="form-actions">
            <el-button type="primary" :loading="loading" @click="submitForm">
              提交建议
            </el-button>
            <el-button @click="resetForm">重置</el-button>
            <el-button @click="goBack">返回</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <!-- 提交说明 -->
    <div class="submit-info">
      <h3>提交说明</h3>
      <div class="info-list">
        <div class="info-item">
          <el-icon><Check /></el-icon>
          <div class="info-content">
            <h4>建议明确</h4>
            <p>请尽量详细描述您的建议，包含具体场景和期望效果</p>
          </div>
        </div>
        <div class="info-item">
          <el-icon><Check /></el-icon>
          <div class="info-content">
            <h4>问题重现</h4>
            <p>如果是Bug反馈，请说明操作步骤和重现条件</p>
          </div>
        </div>
        <div class="info-item">
          <el-icon><Check /></el-icon>
          <div class="info-content">
            <h4>联系方式</h4>
            <p>留下联系方式以便我们及时与您沟通反馈</p>
          </div>
        </div>
        <div class="info-item">
          <el-icon><Check /></el-icon>
          <div class="info-content">
            <h4>处理时间</h4>
            <p>我们会在3个工作日内处理您的建议并给予回复</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { createSuggestion } from '@/api/suggestion'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const formRef = ref()
const loading = ref(false)

// 表单数据
const form = reactive({
  content: '',
  type: 'function',
  contact: ''
})

// 表单验证规则
const rules = {
  content: [
    { required: true, message: '请输入建议内容', trigger: 'blur' },
    { min: 10, message: '建议内容至少10个字符', trigger: 'blur' },
    { max: 1000, message: '建议内容不能超过1000个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择建议分类', trigger: 'change' }
  ]
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const submitData = {
          content: form.content,
          type: form.type
        }
        
        if (form.contact) {
          submitData.contact = form.contact
        }
        
        await createSuggestion(submitData)
        ElMessage.success('建议提交成功！感谢您的宝贵意见')
        
        // 重置表单
        resetForm()
        
        // 延迟跳转
        setTimeout(() => {
          router.push('/user/suggestions')
        }, 1500)
      } catch (error) {
        console.error('提交建议失败:', error)
        ElMessage.error(error.response?.data?.message || '提交失败，请重试')
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
  form.type = 'function'
}

// 返回上一页
const goBack = () => {
  router.push('/user/suggestions')
}
</script>

<style scoped>
.suggestion-add-container {
  padding: 20px;
}

/* 页面标题 */
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

/* 表单容器 */
.form-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.suggestion-form {
  max-width: 800px;
  margin: 0 auto;
}

/* 表单操作 */
.form-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

/* 提交说明 */
.submit-info {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.submit-info h3 {
  margin: 0 0 25px 0;
  color: #2c3e50;
  font-size: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f2f5;
}

.info-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.info-item {
  display: flex;
  gap: 15px;
  align-items: flex-start;
}

.info-item .el-icon {
  font-size: 20px;
  color: #67c23a;
  flex-shrink: 0;
  margin-top: 5px;
}

.info-content h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
}

.info-content p {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-container {
    padding: 20px;
  }
  
  .suggestion-form {
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .info-list {
    grid-template-columns: 1fr;
  }
}
</style>