<template>
  <div class="categories-management">
    <!-- 顶部控制面板 -->
    <div class="control-panel">
      <div class="panel-header">
        <div class="title-section">
          <el-icon class="title-icon"><FolderOpened /></el-icon>
          <h2>分类配置 <span class="subtitle">AI 视觉识别标签库</span></h2>
        </div>
        
        <div class="actions-section">
          <el-button type="primary" class="add-btn" @click="addCategory" size="large">
            <el-icon><Plus /></el-icon> 新增分类节点
          </el-button>
        </div>
      </div>
    </div>

    <!-- 骨架屏 / 空状态 -->
    <div v-if="categories.length === 0" class="empty-state">
      <el-empty description="当前模型未配置任何识别分类">
        <el-button type="primary" plain @click="addCategory">初始化分类库</el-button>
      </el-empty>
    </div>

    <!-- 创新点 1：高级数据卡片阵列 -->
    <div v-else class="categories-grid">
      <div 
        v-for="(category, index) in categories" 
        :key="category.id" 
        class="category-card group"
      >
        <div class="card-bg-glow" :class="getColorClass(index)"></div>
        <div class="card-content">
          <div class="card-top">
            <div class="icon-box" :class="getColorClass(index)">
              <el-icon><Opportunity /></el-icon>
            </div>
            <!-- 悬浮操作台 -->
            <div class="card-actions">
              <el-button circle size="small" type="primary" plain @click="editCategory(category)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button circle size="small" type="danger" plain @click="removeCategory(category)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
          
          <div class="card-body">
            <h3 class="cat-name">{{ category.name }}</h3>
            <div class="cat-id">标签节点 ID: {{ category.id }}</div>
            
            <div class="cat-desc-box">
              <p class="desc-label">提取特征集 / 包含元素：</p>
              <p class="desc-text line-clamp-2" :title="category.description">
                {{ category.description || '暂无详细特征描述，可能影响 AI 识别精度' }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '修正分类节点' : '构建新分类节点'"
      width="480px"
      destroy-on-close
      class="custom-dialog"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="90px"
        label-position="top"
      >
        <el-form-item label="主分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="例如: 电子产品、文具、证件..."
            size="large"
          />
        </el-form-item>

        <!-- 创新点 3：AI 魔法生成特征词 -->
        <el-form-item label="AI 识别特征描述 (训练集说明)" prop="description">
          <div class="desc-input-wrapper">
            <el-input
              v-model="categoryForm.description"
              type="textarea"
              :rows="3"
              placeholder="请描述该分类下通常包含哪些具体物品，以便 AI 模型更准确地进行聚类。如：手机、耳机、充电宝等。"
            />
            <el-button 
              class="magic-btn" 
              type="primary" 
              link 
              @click="simulateAIGenerate"
              :loading="isGenerating"
            >
              <el-icon><MagicStick /></el-icon> {{ isGenerating ? 'AI 提取中...' : 'AI 智能提取特征' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消构建</el-button>
          <el-button type="primary" @click="submitCategory">
            {{ isEditing ? '保存修改' : '确认写入知识库' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, FolderOpened, Edit, Delete, Opportunity, MagicStick } from '@element-plus/icons-vue'
import { useAdminStore } from '@/store/admin'

const adminStore = useAdminStore()
const categories = ref([])

const dialogVisible = ref(false)
const isEditing = ref(false)
const isGenerating = ref(false)
const categoryFormRef = ref()
const categoryForm = ref({
  id: null,
  name: '',
  description: ''
})

const categoryRules = {
  name: [
    { required: true, message: '必须提供分类名称以建立索引', trigger: 'blur' },
    { min: 2, max: 20, message: '分类名称长度限制为 2-20 个字符', trigger: 'blur' }
  ]
}

// 颜色生成器，为了让卡片五颜六色
const getColorClass = (index) => {
  const colors = ['theme-blue', 'theme-purple', 'theme-green', 'theme-orange', 'theme-pink']
  return colors[index % colors.length]
}

const addCategory = () => {
  isEditing.value = false
  categoryForm.value = { id: null, name: '', description: '' }
  dialogVisible.value = true
}

// 新增：编辑方法
const editCategory = (category) => {
  isEditing.value = true
  categoryForm.value = { 
    id: category.id, 
    name: category.name, 
    description: category.description || '' 
  }
  dialogVisible.value = true
}

const removeCategory = async (category) => {
  try {
    await ElMessageBox.confirm(`删除分类【${category.name}】可能会导致历史物品失去归类，确定继续吗？`, '⚠️ 破坏性操作', {
      confirmButtonText: '强制删除',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    })

    categories.value = categories.value.filter(c => c.id !== category.id)
    await adminStore.updateCategories(categories.value)
    ElMessage.success('分类节点已从系统中移除')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除操作失败')
  }
}

const submitCategory = async () => {
  if (!categoryFormRef.value) return

  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEditing.value) {
          const index = categories.value.findIndex(c => c.id === categoryForm.value.id)
          if (index !== -1) {
            categories.value[index] = { ...categoryForm.value }
          }
        } else {
          // 添加
          const newId = categories.value.length > 0 ? Math.max(...categories.value.map((c) => c.id)) + 1 : 1
          categories.value.push({
            id: newId,
            name: categoryForm.value.name,
            description: categoryForm.value.description
          })
        }

        await adminStore.updateCategories(categories.value)
        ElMessage.success(isEditing.value ? '分类节点已更新' : '分类节点成功写入')
        dialogVisible.value = false
      } catch (error) {
        ElMessage.error(isEditing.value ? '更新失败' : '写入失败')
      }
    }
  })
}

// 创新点 3：前端模拟 AI 自动提取特征词
const simulateAIGenerate = () => {
  if (!categoryForm.value.name) {
    ElMessage.warning('请先输入分类名称，AI需要上下文参考')
    return
  }
  isGenerating.value = true
  
  // 假装在调用大模型网络请求
  setTimeout(() => {
    const name = categoryForm.value.name
    let result = ''
    if (name.includes('电子') || name.includes('数码')) {
      result = '包含：智能手机、蓝牙耳机、笔记本电脑、平板、充电宝、智能手表、U盘等带有电子芯片的设备。'
    } else if (name.includes('证件') || name.includes('卡')) {
      result = '包含：学生证、身份证、银行卡、公交卡、校园一卡通、图书借阅证等身份凭证。'
    } else if (name.includes('书') || name.includes('文具')) {
      result = '包含：课本、教材、笔记本、试卷、签字笔、铅笔盒、文件夹等学习办公用品。'
    } else if (name.includes('衣') || name.includes('服')) {
      result = '包含：外套、校服、帽子、手套、围巾、运动鞋等纺织穿戴物品。'
    } else {
      result = `包含：一切符合“${name}”特征的实体物品。系统将提取其主要视觉边缘特征和文本语义进行综合判断。`
    }
    
    categoryForm.value.description = result
    isGenerating.value = false
    ElMessage.success('特征提取完毕')
  }, 1200)
}

onMounted(async () => {
  await adminStore.fetchCategories()
  categories.value = Array.isArray(adminStore.categories) ? [...adminStore.categories] : []
})
</script>

<style scoped>
.categories-management {
  padding: 20px;
  background-color: #f4f7fb;
  min-height: 100%;
}

/* 控制面板样式 */
.control-panel {
  background: #ffffff;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 28px;
  color: #409eff;
}

.title-section h2 {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.subtitle {
  font-size: 13px;
  font-weight: normal;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 20px;
  letter-spacing: 1px;
}

.add-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  transition: all 0.3s;
}
.add-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
}

/* 卡片网格布局 */
.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

/* 类别卡片核心样式 */
.category-card {
  position: relative;
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f1f5f9;
}
.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  border-color: transparent;
}

.card-bg-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  opacity: 0.8;
}

.card-content {
  padding: 20px;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.card-actions {
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
  display: flex;
  gap: 8px;
}
.category-card:hover .card-actions {
  opacity: 1;
  transform: translateX(0);
}

.card-body {
  position: relative;
}

.cat-name {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.cat-id {
  font-size: 12px;
  font-family: monospace;
  color: #94a3b8;
  margin-bottom: 16px;
}

.cat-desc-box {
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
}

.desc-label {
  margin: 0 0 4px 0;
  font-size: 12px;
  color: #64748b;
  font-weight: 600;
}

.desc-text {
  margin: 0;
  font-size: 13px;
  color: #475569;
  line-height: 1.5;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;  
  overflow: hidden;
}

/* 颜色主题 */
.theme-blue { background: #eff6ff; color: #3b82f6; }
.card-bg-glow.theme-blue { background: linear-gradient(90deg, #3b82f6, #93c5fd); }

.theme-purple { background: #faf5ff; color: #8b5cf6; }
.card-bg-glow.theme-purple { background: linear-gradient(90deg, #8b5cf6, #c4b5fd); }

.theme-green { background: #f0fdf4; color: #10b981; }
.card-bg-glow.theme-green { background: linear-gradient(90deg, #10b981, #6ee7b7); }

.theme-orange { background: #fff7ed; color: #f97316; }
.card-bg-glow.theme-orange { background: linear-gradient(90deg, #f97316, #fdba74); }

.theme-pink { background: #fdf2f8; color: #ec4899; }
.card-bg-glow.theme-pink { background: linear-gradient(90deg, #ec4899, #f9a8d4); }

/* 弹窗样式 */
.desc-input-wrapper {
  position: relative;
  width: 100%;
}
.magic-btn {
  position: absolute;
  right: 8px;
  bottom: 8px;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 4px;
}

.empty-state {
  background: #ffffff;
  border-radius: 12px;
  padding: 60px 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}
</style>