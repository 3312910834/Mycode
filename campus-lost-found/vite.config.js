// vite.config.js
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      // 代理所有以 /api 开头的请求到后端
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 重写路径：将 /api 替换为 /campus-lost-found
        rewrite: (path) => path.replace(/^\/api/, '/campus-lost-found')
      }
    }
  }
})
