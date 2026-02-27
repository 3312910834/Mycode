<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>校园失物招领系统</h2>
        <p>创建新账户</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（4-20位字母数字）"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <!-- 真实姓名 -->
        <el-form-item prop="real_name">
          <el-input
            v-model="registerForm.real_name"
            placeholder="请输入真实姓名"
            prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>
        
        <!-- 学号 -->
        <el-form-item prop="student_id">
          <el-input
            v-model="registerForm.student_id"
            placeholder="请输入学号"
            prefix-icon="Document"
            size="large"
          />
        </el-form-item>
        
        <!-- 手机号 -->
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
          />
        </el-form-item>
        
        <!-- 邮箱 -->
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        
        <!-- 协议 -->
        <el-form-item>
          <el-checkbox v-model="agreeTerms">
            我已阅读并同意
            <el-link type="primary" :underline="false">《用户协议》</el-link>
            和
            <el-link type="primary" :underline="false">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>
        
        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            :disabled="!agreeTerms"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        
        <!-- 底部链接 -->
        <div class="register-footer">
          <span>已有账户?</span>
          <el-link type="primary" :underline="false" @click="$router.push('/login')">
            立即登录
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)
const agreeTerms = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

// 自定义验证规则
const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9]{4,20}$/.test(value)) {
    callback(new Error('用户名必须为4-20位字母数字'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (value && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) {
    callback(new Error('邮箱格式不正确'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  real_name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  student_id: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{8,15}$/, message: '学号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  if (!agreeTerms.value) {
    ElMessage.warning('请阅读并同意用户协议和隐私政策')
    return
  }
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 准备注册数据，适配后端需要的字段
        // 后端需要的字段：username, password, phone
        // 额外字段：real_name, student_id, email（后端可能需要调整）
        const registerData = {
          username: registerForm.username,
          password: registerForm.password,
          phone: registerForm.phone,
          real_name: registerForm.real_name,
          student_id: registerForm.student_id,
          email: registerForm.email || `${registerForm.username}@example.com`
        }
        
        console.log('发送注册数据:', registerData)
        
        await userStore.register(registerData)
        ElMessage.success('注册成功！请登录')
        
        // 清空表单
        Object.keys(registerForm).forEach(key => {
          registerForm[key] = ''
        })
        
        // 跳转到登录页
        router.push('/login')
      } catch (error) {
        console.error('注册失败详情:', error)
        
        // 处理错误信息
        if (error.message) {
          try {
            // 尝试解析后端返回的JSON错误信息
            const errorMatch = error.message.match(/\{.*\}/)
            if (errorMatch) {
              const errorObj = JSON.parse(errorMatch[0])
              ElMessage.error(errorObj.error || '注册失败')
            } else {
              ElMessage.error('注册失败，用户名可能已存在')
            }
          } catch (e) {
            ElMessage.error('注册失败，请检查网络连接')
          }
        } else {
          ElMessage.error('注册失败，请重试')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 样式保持不变 */
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 450px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
  max-height: 90vh;
  overflow-y: auto;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-weight: 600;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.register-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  height: 50px;
  font-size: 16px;
  font-weight: 500;
  margin-top: 10px;
}

.register-btn:hover {
  opacity: 0.9;
}

.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.register-footer .el-link {
  margin-left: 10px;
}

/* 滚动条样式 */
.register-box::-webkit-scrollbar {
  width: 6px;
}

.register-box::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.register-box::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.register-box::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-box {
    padding: 30px 20px;
    max-height: 85vh;
  }
  
  .register-header h2 {
    font-size: 24px;
  }
}
</style>