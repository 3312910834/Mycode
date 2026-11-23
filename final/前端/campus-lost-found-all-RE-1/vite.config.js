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
      },
      // 管理端/用户端直连 context-path 时走同源代理
      '/campus-lost-found': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 👇👇👇 【新增的重点：代理本地磁盘的图片路径】 👇👇👇
      '/uploads': {
        target: 'http://localhost:8080', // 指向你的 Tomcat
        changeOrigin: true
      }
      // 👆👆👆 ========================================= 👆👆👆
    }
  }
})