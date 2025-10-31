<template>
  <div class="dict-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据字典</span>
          <el-button type="primary" @click="showAddDialog">添加字典</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="字典类型、标签或值" clearable />
        </el-form-item>
        <el-form-item label="字典类型">
          <el-select v-model="searchForm.dictType" placeholder="选择字典类型" clearable style="width: 150px;">
            <el-option 
              v-for="type in dictTypes" 
              :key="type" 
              :label="type" 
              :value="type" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadDictItems">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="dictItems" v-loading="loading">
        <el-table-column prop="dictType" label="字典类型" />
        <el-table-column prop="dictLabel" label="字典标签" />
        <el-table-column prop="dictValue" label="字典值" />
        <el-table-column prop="sortOrder" label="排序" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editDict(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteDict(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadDictItems"
        @current-change="loadDictItems"
        class="pagination"
      />
    </el-card>

    <!-- 添加/编辑字典对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字典' : '添加字典'" width="500px">
      <el-form :model="dictForm" :rules="rules" ref="dictFormRef" label-width="100px">
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="dictForm.dictType" />
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="dictForm.dictLabel" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="dictForm.dictValue" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="dictForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="dictForm.status" style="width: 100%;">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dictForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDict" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDictList, addDict, updateDict, deleteDict as deleteDictApi, getAllDictTypes } from '@/api/dict'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const dictFormRef = ref()

// 字典列表
const dictItems = ref([])

// 字典类型列表
const dictTypes = ref([])

// 搜索表单
const searchForm = ref({
  keyword: '',
  dictType: ''
})

// 分页
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 字典表单
const dictForm = ref({
  dictType: '',
  dictLabel: '',
  dictValue: '',
  sortOrder: 0,
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

// 加载字典列表
const loadDictItems = async () => {
  loading.value = true
  try {
    const response = await getDictList({
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm.value
    })
    
    dictItems.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('加载字典列表失败')
  } finally {
    loading.value = false
  }
}

// 加载字典类型列表
const loadDictTypes = async () => {
  try {
    const response = await getAllDictTypes()
    dictTypes.value = response.data
  } catch (error) {
    ElMessage.error('加载字典类型失败')
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    dictType: ''
  }
  pagination.value.current = 1
  loadDictItems()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  dictForm.value = {
    dictType: '',
    dictLabel: '',
    dictValue: '',
    sortOrder: 0,
    status: 1,
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑字典
const editDict = (dict) => {
  isEdit.value = true
  dictForm.value = { ...dict }
  dialogVisible.value = true
}

// 保存字典
const saveDict = async () => {
  if (!dictFormRef.value) return
  
  await dictFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateDict(dictForm.value.id, dictForm.value)
          ElMessage.success('更新字典成功')
        } else {
          await addDict(dictForm.value)
          ElMessage.success('添加字典成功')
        }
        
        dialogVisible.value = false
        loadDictItems()
        loadDictTypes() // 重新加载字典类型
      } catch (error) {
        ElMessage.error(error.message || '保存字典失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 删除字典
const deleteDict = async (dictId) => {
  try {
    await ElMessageBox.confirm('确定要删除该字典吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteDictApi(dictId)
    ElMessage.success('删除字典成功')
    loadDictItems()
    loadDictTypes() // 重新加载字典类型
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除字典失败')
    }
  }
}

onMounted(() => {
  loadDictItems()
  loadDictTypes()
})
</script>

<style scoped>
.dict-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
