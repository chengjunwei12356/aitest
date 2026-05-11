<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">银行信贷管理系统</h2>
      <el-form :model="form" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="captcha">
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="验证码" prefix-icon="Key" size="large" style="flex:1" />
            <img v-if="captchaImage" :src="captchaImage" @click="refreshCaptcha" class="captcha-img" alt="验证码" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCaptcha, login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const captchaImage = ref('')
const sessionId = ref('')

const form = reactive({
  username: 'admin',
  password: '123456',
  captcha: '',
  sessionId: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = async () => {
  try {
    const data = await getCaptcha()
    captchaImage.value = data.imageBase64
    sessionId.value = data.sessionId
    form.sessionId = data.sessionId
  } catch (e) {
    console.error('获取验证码失败', e)
  }
}

const handleLogin = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const data = await login(form)
      userStore.setToken(data.token)
      userStore.setUserInfo({ userId: data.userId, username: data.username })
      ElMessage.success('登录成功')
      router.push('/')
    } catch (e) {
      refreshCaptcha()
      form.captcha = ''
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 20px;
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.login-form {
  margin-top: 20px;
}
.captcha-row {
  display: flex;
  gap: 10px;
}
.captcha-img {
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
}
</style>
