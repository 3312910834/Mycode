<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// 这里可以添加全局的script逻辑
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  font-family: 'Microsoft YaHei', 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

body {
  background-color: #f5f7fa;
  line-height: 1.6;
}

#app {
  min-height: 100vh;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 全局链接样式 */
a {
  text-decoration: none;
  color: inherit;
}

/* 全局图片样式 */
img {
  max-width: 100%;
  height: auto;
  vertical-align: middle;
}

/* 全局工具类 */
.text-center {
  text-align: center;
}

.text-left {
  text-align: left;
}

.text-right {
  text-align: right;
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.mt-10 { margin-top: 10px; }
.mt-20 { margin-top: 20px; }
.mt-30 { margin-top: 30px; }
.mb-10 { margin-bottom: 10px; }
.mb-20 { margin-bottom: 20px; }
.mb-30 { margin-bottom: 30px; }
.ml-10 { margin-left: 10px; }
.mr-10 { margin-right: 10px; }
.pt-10 { padding-top: 10px; }
.pb-10 { padding-bottom: 10px; }
.pl-10 { padding-left: 10px; }
.pr-10 { padding-right: 10px; }

/* 全局动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Element Plus 全局样式覆盖 */
.el-button {
  font-weight: 500;
}

.el-input__inner {
  border-radius: 8px;
}

.el-select {
  width: 100%;
}

/* 响应式设计基础 */
@media (max-width: 768px) {
  .hidden-xs {
    display: none !important;
  }
}

@media (min-width: 768px) {
  .hidden-sm {
    display: none !important;
  }
}
</style>