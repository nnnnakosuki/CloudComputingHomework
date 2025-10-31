<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// 主应用组件
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: #f5f5f5;
}

/* 修复所有下拉框显示问题 */
.el-select {
  width: 100%;
}

.el-select .el-input__wrapper {
  min-width: 200px;
}

/* 确保表单中的下拉框有合适的宽度 */
.el-form-item .el-select {
  width: 100%;
}

/* 搜索表单中的下拉框使用弹性宽度 */
.inline .el-form-item .el-select,
.search-form .el-form-item .el-select {
  width: auto;
  min-width: 150px;
}
</style>
