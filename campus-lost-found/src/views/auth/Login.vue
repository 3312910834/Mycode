<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>校园失物招领系统</h2>
        <p>登录您的账户</p>
      </div>
      
      <!-- 测试连接按钮 -->
      <div class="test-connection" v-if="showTest">
        <el-button size="small" @click="testBackendConnection">
          测试后端连接
        </el-button>
        <span v-if="testResult" :class="['test-result', testResult.success ? 'success' : 'error']">
          {{ testResult.message }}
        </span>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-link type="primary" :underline="false" class="forgot-password">
            忘记密码?
          </el-link>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <span>还没有账户?</span>
          <el-link type="primary" :underline="false" @click="$router.push('/register')">
            立即注册
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

const loginFormRef = ref()
const loading = ref(false)
const rememberMe = ref(false)
const showTest = ref(true) // 开发环境显示测试按钮
const testResult = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 测试后端连接
const testBackendConnection = async () => {
  try {
    testResult.value = { success: false, message: '测试中...' }
    const result = await userStore.testConnection()
    testResult.value = { success: true, message: '后端连接成功' }
    ElMessage.success('后端服务正常')
  } catch (error) {
    testResult.value = { 
      success: false, 
      message: '后端连接失败，请检查服务是否启动' 
    }
    ElMessage.error('后端服务异常，请检查控制台')
    console.error('后端连接失败详情:', error)
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用store的登录方法
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        
        // 登录成功后获取完整的用户信息
        await userStore.getUserInfo()
        
        // 跳转到首页
        router.push('/')
      } catch (error) {
        console.error('登录失败详情:', error)
        
        // 处理后端返回的错误格式
        if (error.message) {
          try {
            // 尝试解析后端返回的JSON错误信息
            const errorMatch = error.message.match(/\{.*\}/)
            if (errorMatch) {
              const errorObj = JSON.parse(errorMatch[0])
              ElMessage.error(errorObj.error || '登录失败')
            } else if (error.message.includes('error')) {
              // 直接显示错误信息
              ElMessage.error('用户名或密码错误')
            } else {
              ElMessage.error('登录失败，请检查网络连接')
            }
          } catch (e) {
            // 解析失败，显示通用错误
            ElMessage.error('用户名或密码错误')
          }
        } else {
          ElMessage.error('登录失败，请检查网络连接')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

// 页面加载时自动测试连接（可选）
// onMounted(() => {
//   testBackendConnection()
// })
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-weight: 600;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.test-connection {
  text-align: center;
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 5px;
}

.test-result {
  margin-left: 10px;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 3px;
}

.test-result.success {
  background-color: #e7f7ef;
  color: #13ce66;
}

.test-result.error {
  background-color: #fef0f0;
  color: #ff4949;
}

.login-form {
  margin-top: 10px;
}

.forgot-password {
  float: right;
}

.login-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  height: 50px;
  font-size: 16px;
  font-weight: 500;
}

.login-btn:hover {
  opacity: 0.9;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.login-footer .el-link {
  margin-left: 10px;
}
</style>