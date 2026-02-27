<template>
  <div class="user-password-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>修改密码</h2>
      <p>定期修改密码，保护账号安全</p>
    </div>

    <!-- 密码修改表单 -->
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="password-form"
      >
        <el-form-item label="当前密码" prop="old_password">
          <el-input
            v-model="form.old_password"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="新密码" prop="new_password">
          <el-input
            v-model="form.new_password"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
          />
          <div class="password-strength">
            <div class="strength-bar">
              <div
                :class="['strength-fill', strengthClass]"
                :style="{ width: strengthPercentage + '%' }"
              />
            </div>
            <div class="strength-text">{{ strengthText }}</div>
          </div>
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirm_password">
          <el-input
            v-model="form.confirm_password"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>

        <!-- 密码强度提示 -->
        <div class="password-tips">
          <h4>密码安全要求：</h4>
          <ul>
            <li :class="{ 'tip-valid': passwordHasLength }">
              长度至少6位字符
            </li>
            <li :class="{ 'tip-valid': passwordHasUppercase }">
              包含大写字母
            </li>
            <li :class="{ 'tip-valid': passwordHasLowercase }">
              包含小写字母
            </li>
            <li :class="{ 'tip-valid': passwordHasNumber }">
              包含数字
            </li>
            <li :class="{ 'tip-valid': passwordHasSpecial }">
              包含特殊字符（可选）
            </li>
          </ul>
        </div>

        <!-- 提交按钮 -->
        <el-form-item>
          <div class="form-actions">
            <el-button
              type="primary"
              :loading="loading"
              :disabled="!isFormValid"
              @click="submitForm"
            >
              确认修改
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <!-- 安全提示 -->
    <div class="security-tips">
      <h3>安全提示</h3>
      <div class="tips-list">
        <div class="tip-item">
          <el-icon><Warning /></el-icon>
          <div class="tip-content">
            <h4>定期更换密码</h4>
            <p>建议每3-6个月修改一次密码，提高账号安全性</p>
          </div>
        </div>
        <div class="tip-item">
          <el-icon><Lock /></el-icon>
          <div class="tip-content">
            <h4>使用强密码</h4>
            <p>使用包含大小写字母、数字和特殊字符的组合密码</p>
          </div>
        </div>
        <div class="tip-item">
          <el-icon><User /></el-icon>
          <div class="tip-content">
            <h4>不要泄露密码</h4>
            <p>不要将密码告诉他人，包括系统管理员</p>
          </div>
        </div>
        <div class="tip-item">
          <el-icon><Key /></el-icon>
          <div class="tip-content">
            <h4>避免重复使用</h4>
            <p>不要在多个网站使用相同的密码</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { updatePassword } from '@/api/user'
import { ElMessage } from 'element-plus'
import { computed, reactive, ref } from 'vue'

const formRef = ref()
const loading = ref(false)

// 表单数据
const form = reactive({
  old_password: '',
  new_password: '',
  confirm_password: ''
})

// 密码强度计算
const passwordHasLength = computed(() => form.new_password.length >= 6)
const passwordHasUppercase = computed(() => /[A-Z]/.test(form.new_password))
const passwordHasLowercase = computed(() => /[a-z]/.test(form.new_password))
const passwordHasNumber = computed(() => /[0-9]/.test(form.new_password))
const passwordHasSpecial = computed(() => /[^A-Za-z0-9]/.test(form.new_password))

// 计算密码强度
const strengthScore = computed(() => {
  let score = 0
  if (passwordHasLength.value) score += 20
  if (passwordHasUppercase.value) score += 20
  if (passwordHasLowercase.value) score += 20
  if (passwordHasNumber.value) score += 20
  if (passwordHasSpecial.value) score += 20
  return Math.min(score, 100)
})

const strengthPercentage = computed(() => strengthScore.value)
const strengthClass = computed(() => {
  if (strengthScore.value < 40) return 'strength-weak'
  if (strengthScore.value < 70) return 'strength-medium'
  return 'strength-strong'
})

const strengthText = computed(() => {
  if (!form.new_password) return '请输入密码'
  if (strengthScore.value < 40) return '密码强度：弱'
  if (strengthScore.value < 70) return '密码强度：中'
  return '密码强度：强'
})

// 表单验证规则
const validateOldPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入当前密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度至少6位'))
  } else {
    callback()
  }
}

const validateNewPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度至少6位'))
  } else if (value === form.old_password) {
    callback(new Error('新密码不能与当前密码相同'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认新密码'))
  } else if (value !== form.new_password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  old_password: [
    { required: true, validator: validateOldPassword, trigger: 'blur' }
  ],
  new_password: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirm_password: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 表单是否有效
const isFormValid = computed(() => {
  return (
    form.old_password &&
    form.new_password &&
    form.confirm_password &&
    form.new_password === form.confirm_password &&
    form.new_password.length >= 6 &&
    form.new_password !== form.old_password
  )
})

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await updatePassword({
          old_password: form.old_password,
          new_password: form.new_password
        })
        
        ElMessage.success('密码修改成功')
        resetForm()
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error(error.response?.data?.message || '修改失败')
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
}
</script>

<style scoped>
.user-password-container {
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

.password-form {
  max-width: 500px;
  margin: 0 auto;
}

/* 密码强度条 */
.password-strength {
  margin-top: 10px;
}

.strength-bar {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 5px;
}

.strength-fill {
  height: 100%;
  border-radius: 3px;
  transition: all 0.3s;
}

.strength-weak {
  background: #f56c6c;
}

.strength-medium {
  background: #e6a23c;
}

.strength-strong {
  background: #67c23a;
}

.strength-text {
  font-size: 12px;
  color: #6c757d;
  text-align: right;
}

/* 密码提示 */
.password-tips {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  border-left: 4px solid #409eff;
}

.password-tips h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.password-tips ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.password-tips li {
  margin-bottom: 8px;
  color: #6c757d;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.password-tips li:before {
  content: '○';
  margin-right: 10px;
  font-size: 12px;
}

.password-tips li.tip-valid {
  color: #67c23a;
}

.password-tips li.tip-valid:before {
  content: '✓';
  color: #67c23a;
}

/* 表单操作 */
.form-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

/* 安全提示 */
.security-tips {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.security-tips h3 {
  margin: 0 0 25px 0;
  color: #2c3e50;
  font-size: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f2f5;
}

.tips-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.tip-item {
  display: flex;
  gap: 15px;
  align-items: flex-start;
}

.tip-item .el-icon {
  font-size: 24px;
  color: #409eff;
  flex-shrink: 0;
  margin-top: 5px;
}

.tip-content h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
}

.tip-content p {
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
  
  .password-form {
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .tips-list {
    grid-template-columns: 1fr;
  }
}
</style>