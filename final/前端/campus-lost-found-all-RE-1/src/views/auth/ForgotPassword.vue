<template>
  <div class="min-h-screen bg-surface flex items-center justify-center p-4">
    <div class="w-full max-w-[440px] bg-surface-container-low rounded-[2rem] shadow-2xl border border-surface-container-high p-8 md:p-10">
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-primary-container text-primary rounded-2xl mb-6">
          <span class="material-symbols-outlined text-4xl">lock_reset</span>
        </div>
        <h2 class="text-3xl font-black text-on-surface font-headline mb-2">忘记密码</h2>
        <p class="text-on-surface-variant font-medium">
          当前暂不支持在线重置密码，请联系管理员协助处理。
        </p>
      </div>

      <div class="space-y-4">
        <div class="bg-surface-container-high rounded-2xl p-4 text-on-surface-variant text-sm leading-7">
          <p>你可以联系管理员并提供以下信息：</p>
          <p>1. 用户名</p>
          <p>2. 注册手机号 / 邮箱</p>
          <p>3. 学号（如已绑定）</p>
        </div>

        <button
          type="button"
          @click="$router.push('/login')"
          class="w-full py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg transition-all"
        >
          返回登录
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-primary { background-color: #6750A4; }
.bg-primary-container { background-color: #EADDFF; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }

.border-surface-container-high { border-color: #ECE6F0; }
</style>