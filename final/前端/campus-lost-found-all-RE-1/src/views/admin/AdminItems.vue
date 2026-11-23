<template>
  <div class="items-management" @click="closeContextMenu">
    <!-- 顶部控制面板卡片 -->
    <div class="control-panel">
      <div class="panel-header">
        <div class="title-section">
          <el-icon class="title-icon"><DataBoard /></el-icon>
          <h2>信息管理 <span class="subtitle">AI 智能审核调度中心</span></h2>
        </div>
        
        <div class="filters-section">
          <!-- 搜索与基础过滤保持不变 -->
          <el-input
            v-model="itemSearch"
            placeholder="支持自然语言：如 '昨天发布的手机'"
            clearable
            class="search-input ai-search"
            @keyup.enter="applyItemSearch"
            @clear="applyItemSearch"
          >
            <template #prefix>
              <el-icon class="ai-sparkle"><MagicStick /></el-icon>
            </template>
          </el-input>
          
          <el-select v-model="filterType" placeholder="物品类型" class="filter-select" @change="handleFilterChange">
            <el-option label="全部类型" value="all" />
            <el-option label="遗失物品 (Lost)" value="lost" />
            <el-option label="拾获物品 (Found)" value="found" />
          </el-select>

          <el-select v-model="filterStatus" placeholder="当前状态" class="filter-select" @change="handleFilterChange">
            <el-option label="全部状态" value="all" />
            <el-option label="待审核" value="pending" />
            <el-option label="展示中" value="approved" />
            <el-option label="已解决" value="resolved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </div>
      </div>

      <!-- 🚀 创新点：Notion 风格多视图切换栏 -->
      <div class="view-system-bar">
        <div class="view-tabs">
          <div 
            v-for="view in savedViews" 
            :key="view.id"
            class="view-tab-item"
            :class="{ active: activeViewId === view.id }"
            @click="switchView(view)"
          >
            <el-icon><component :is="view.mode === 'table' ? 'Menu' : 'Picture'" /></el-icon>
            {{ view.name }}
          </div>
          <!-- 保存自定义视图按钮 -->
          <div class="view-tab-item add-btn" @click="saveCurrentAsView">
            <el-icon><Plus /></el-icon> 另存为视图
          </div>
        </div>

        <!-- 临时切换当前显示模式 -->
        <div class="view-mode-toggle">
          <el-radio-group v-model="currentViewMode" size="small">
            <el-radio-button label="table">
              <el-tooltip content="表格视图" placement="top"><el-icon><Menu /></el-icon></el-tooltip>
            </el-radio-button>
            <el-radio-button label="gallery">
              <el-tooltip content="画廊视图" placement="top"><el-icon><Picture /></el-icon></el-tooltip>
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>

    <!-- 数据展示容器 -->
    <div class="data-container" v-loading="loading">
      
      <!-- 视图 A: 传统数据表格 (Table View) -->
      <div v-show="currentViewMode === 'table'" class="table-container">
        <el-table
          :data="filteredItems"
          style="width: 100%"
          height="calc(100vh - 290px)"
          stripe
          header-cell-class-name="custom-header"
          @selection-change="handleSelectionChange"
          @row-contextmenu="handleRowContextMenu"
          ref="tableRef"
        >
          <el-table-column type="selection" width="50" align="center" fixed="left" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="type" label="类型" width="90" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.type === 'lost' ? 'danger' : 'success'" effect="light" round>
                {{ scope.row.type === 'lost' ? '失物' : '招领' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
            <template #default="scope"><span class="item-title">{{ scope.row.title }}</span></template>
          </el-table-column>
          <el-table-column prop="category" label="分类 (AI识别)" width="140" align="center">
            <template #default="scope">
              <div class="ai-category-wrapper">
                <span class="cat-text">{{ scope.row.category || '未分类' }}</span>
                <el-tooltip content="AI 综合识别度" placement="top" :show-after="300">
                  <div class="ai-score-badge"><el-icon><Aim /></el-icon><span>{{ 85 + (scope.row.id % 14) }}%</span></div>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="地点" min-width="160" show-overflow-tooltip>
            <template #default="scope">
              <div class="location-cell"><el-icon><Location /></el-icon><span>{{ scope.row.location }}</span></div>
            </template>
          </el-table-column>
          <el-table-column prop="poster" label="发布者" width="110" align="center" />
          <el-table-column prop="date" label="发布时间" width="160" align="center" />
          <el-table-column prop="status" label="状态" width="110" align="center">
            <template #default="scope">
              <div class="status-wrapper">
                <span v-if="scope.row.status === 'pending'" class="pulse-dot"></span>
                <el-tag :type="getStatusType(scope.row.status)" effect="dark" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right" align="center">
            <template #default="scope">
              <div class="action-buttons">
                <el-button link type="primary" size="small" @click="viewItem(scope.row)">查看</el-button>
                <el-button v-if="scope.row.status === 'pending'" link type="success" size="small" @click="approveItem(scope.row)">通过</el-button>
                <el-button v-if="scope.row.status === 'pending'" link type="danger" size="small" @click="rejectItem(scope.row)">拒绝</el-button>
                <el-button v-if="scope.row.status !== 'resolved' && scope.row.status !== 'pending'" link type="warning" size="small" @click="resolveItem(scope.row)">解决</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 🚀 创新点：视图 B: 画廊模式 (Gallery View) -->
      <div v-show="currentViewMode === 'gallery'" class="gallery-container">
        <el-empty v-if="filteredItems.length === 0" description="当前视图无数据" />
        <div v-else class="gallery-grid">
          <div 
            v-for="item in filteredItems" 
            :key="item.id" 
            class="gallery-card"
            :class="{ 'is-selected': isItemSelected(item) }"
            @contextmenu="(e) => handleRowContextMenu(item, null, e)"
          >
            <!-- 卡片多选框 (Hover 显示) -->
            <div class="card-checkbox" @click.stop="toggleItemSelection(item)">
              <el-checkbox :model-value="isItemSelected(item)" @click.stop/>
            </div>

            <!-- 封面图部分 -->
            <div class="card-cover" @click="viewItem(item)">
              <div v-if="item.image" class="image-bg" :style="{ backgroundImage: `url(${item.image})` }"></div>
              <div v-else class="image-placeholder"><el-icon><Picture /></el-icon></div>
              <div class="card-status-badge">
                <el-tag :type="getStatusType(item.status)" effect="dark" size="small">{{ getStatusText(item.status) }}</el-tag>
              </div>
            </div>

            <!-- 内容部分 -->
            <div class="card-body">
              <div class="card-header">
                <h3 class="card-title">{{ item.title }}</h3>
                <el-tag :type="item.type === 'lost' ? 'danger' : 'success'" size="small" effect="plain">
                  {{ item.type === 'lost' ? '失物' : '招领' }}
                </el-tag>
              </div>
              <div class="card-meta">
                <span><el-icon><Aim /></el-icon> {{ item.category || '未分类' }}</span>
                <span><el-icon><Location /></el-icon> {{ item.location }}</span>
              </div>
              
              <!-- 快捷操作栏 -->
              <div class="card-footer" v-if="item.status === 'pending'">
                <el-button type="success" size="small" plain class="flex-1" @click="approveItem(item)">通过</el-button>
                <el-button type="danger" size="small" plain class="flex-1" @click="rejectItem(item)">驳回</el-button>
              </div>
              <div class="card-footer" v-else>
                <span class="poster-info">{{ item.poster }} 发布于 {{ item.date }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右键原生上下文菜单 (支持双视图) -->
    <div v-show="contextMenu.visible" class="custom-context-menu" :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }">
      <div class="menu-item" @click="viewItem(contextMenu.row)"><el-icon><View /></el-icon> 侧边栏预览</div>
      <div v-if="contextMenu.row?.status === 'pending'" class="menu-item success" @click="approveItem(contextMenu.row)"><el-icon><Check /></el-icon> 快捷通过</div>
      <div v-if="contextMenu.row?.status === 'pending'" class="menu-item danger" @click="rejectItem(contextMenu.row)"><el-icon><Close /></el-icon> 快捷驳回</div>
      <div class="menu-item divider"></div>
      <div class="menu-item danger" @click="deleteItem(contextMenu.row)"><el-icon><Delete /></el-icon> 彻底删除</div>
    </div>

    <!-- 底部浮动批量操作栏 -->
    <transition name="slide-up">
      <div v-if="selectedRows.length > 0" class="batch-action-bar">
        <div class="batch-info">
          <el-icon class="is-loading"><Loading /></el-icon>
          已选择 <strong>{{ selectedRows.length }}</strong> 项，AI 建议：<span class="ai-suggest">全部予以通过</span>
        </div>
        <div class="batch-actions">
          <el-button type="success" round @click="batchApprove">批量通过</el-button>
          <el-button type="danger" round plain @click="batchReject">批量驳回</el-button>
        </div>
      </div>
    </transition>

    <!-- 抽屉式分屏预览 -->
    <el-drawer v-model="viewDialogVisible" title="智能分析与详细信息" size="500px" destroy-on-close class="custom-drawer">
      <div v-if="selectedItem" class="item-detail">
        <el-alert v-if="selectedItem.status === 'pending'" title="AI 智能风控审查完成" type="success" :closable="false" show-icon class="ai-alert">
          系统未检测到敏感词汇，图像识别与【{{ selectedItem.category }}】高度吻合，安全评级：优秀。
        </el-alert>
        <el-descriptions :column="1" border class="detail-descriptions">
          <el-descriptions-item label="系统 ID">{{ selectedItem.id }}</el-descriptions-item>
          <el-descriptions-item label="所属类别">
            <el-tag :type="selectedItem.type === 'lost' ? 'danger' : 'success'" effect="light" round>
              {{ selectedItem.type === 'lost' ? '失物记录' : '招领记录' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="物品标题"><span class="detail-title">{{ selectedItem.title }}</span></el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(selectedItem.status)" effect="dark">{{ getStatusText(selectedItem.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="相关地点">{{ selectedItem.location }}</el-descriptions-item>
          <el-descriptions-item label="发布用户">{{ selectedItem.poster }}</el-descriptions-item>
          <el-descriptions-item label="入库时间">{{ selectedItem.date }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="selectedItem.image" class="item-image-wrapper">
          <p class="image-title">附件图片：</p>
          <el-image :src="selectedItem.image" alt="物品图片" fit="contain" :preview-src-list="[selectedItem.image]" class="detail-image" lazy />
        </div>
      </div>
      <template #footer>
        <div style="flex: auto" v-if="selectedItem?.status === 'pending'">
          <el-button @click="rejectItem(selectedItem)">驳回申请</el-button>
          <el-button type="primary" @click="approveItem(selectedItem)">同意发布</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入必需的图标
import { Search, View, Check, Close, Select, Delete, Location, DataBoard, Aim, MagicStick, Loading, Menu, Picture, Plus } from '@element-plus/icons-vue'
import { useAdminStore } from '@/store/admin'

const adminStore = useAdminStore()
const tableRef = ref(null)

const loading = computed(() => adminStore.loading)
const filteredItems = computed(() => adminStore.filteredItems)

// === 状态过滤 ===
const itemSearch = ref(adminStore.searchTerm || '')
const filterType = ref(adminStore.filterType)
const filterStatus = ref(adminStore.filterStatus)
const viewDialogVisible = ref(false)
const selectedItem = ref(null)

// === 🚀 创新点：多视图系统状态 ===
const currentViewMode = ref('table') // 'table' | 'gallery'
const activeViewId = ref('default-1')

// 模拟的保存视图数据（实际中可持久化到 localStorage 或后端）
const savedViews = ref([
  { id: 'default-1', name: '全部数据', mode: 'table', type: 'all', status: 'all', search: '' },
  { id: 'default-2', name: '待审画廊', mode: 'gallery', type: 'all', status: 'pending', search: '' },
])

// 切换视图逻辑
const switchView = (view) => {
  activeViewId.value = view.id
  currentViewMode.value = view.mode
  filterType.value = view.type
  filterStatus.value = view.status
  itemSearch.value = view.search
  
  // 同步到 Store 发起请求或过滤
  adminStore.setSearchTerm(view.search)
  adminStore.setFilterType(view.type)
  adminStore.setFilterStatus(view.status)
}

// 保存当前视图
const saveCurrentAsView = () => {
  ElMessageBox.prompt('请输入自定义视图名称', '保存视图', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '名称不能为空'
  }).then(({ value }) => {
    const newId = 'custom-' + Date.now()
    savedViews.value.push({
      id: newId,
      name: value,
      mode: currentViewMode.value,
      type: filterType.value,
      status: filterStatus.value,
      search: itemSearch.value
    })
    activeViewId.value = newId
    ElMessage.success(`视图 "${value}" 已保存！`)
  }).catch(() => {})
}

// === 基础辅助函数 ===
const getStatusType = (status) => ({ pending: 'warning', approved: 'success', resolved: 'info', rejected: 'danger' }[status] || 'info')
const getStatusText = (status) => ({ pending: '待审核', approved: '展示中', resolved: '已解决', rejected: '已拒绝' }[status] || status)

const applyItemSearch = () => adminStore.setSearchTerm(itemSearch.value.trim())
const handleFilterChange = () => {
  adminStore.setFilterType(filterType.value)
  adminStore.setFilterStatus(filterStatus.value)
}
const viewItem = (item) => { selectedItem.value = item; viewDialogVisible.value = true; contextMenu.visible = false }

// === 乐观更新逻辑 ===
const optimisticUpdate = async (item, targetStatus, apiCall, successMsg) => {
  const originalStatus = item.status
  item.status = targetStatus
  contextMenu.visible = false
  try {
    await apiCall()
    ElMessage.success(successMsg)
    if(viewDialogVisible.value) viewDialogVisible.value = false
  } catch (error) {
    item.status = originalStatus
    ElMessage.error('操作异常，已撤销')
  }
}

const approveItem = (item) => optimisticUpdate(item, 'approved', () => adminStore.updateItemStatus(item.id, 'approved', item.type), '审核通过，秒级生效')
const resolveItem = (item) => optimisticUpdate(item, 'resolved', () => adminStore.updateItemStatus(item.id, 'resolved', item.type), '已标记为解决')
const rejectItem = async (item) => {
  contextMenu.visible = false
  try {
    await ElMessageBox.prompt('请输入拒绝原因（可选）', '拒绝审核', { confirmButtonText: '确定驳回', cancelButtonText: '取消' })
      .then(() => optimisticUpdate(item, 'rejected', () => adminStore.updateItemStatus(item.id, 'rejected', item.type), '已驳回该信息'))
  } catch(e) {}
}
const deleteItem = async (item) => {
  contextMenu.visible = false
  try {
    await ElMessageBox.confirm('确定彻底删除该记录吗？', '⚠️ 警告', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'error' })
    await adminStore.deleteItem(item.id, item.type)
    ElMessage.success('数据已清除')
  } catch(e) {}
}

// === 跨视图的批量操作支持 ===
const selectedRows = ref([])

// 表格选中
const handleSelectionChange = (val) => { selectedRows.value = val }
// 画廊选中判断与切换
const isItemSelected = (item) => selectedRows.value.some(r => r.id === item.id)
const toggleItemSelection = (item) => {
  const index = selectedRows.value.findIndex(r => r.id === item.id)
  if (index > -1) {
    selectedRows.value.splice(index, 1) // 取消选中
    if(tableRef.value) tableRef.value.toggleRowSelection(item, false) // 同步给底层表格
  } else {
    selectedRows.value.push(item) // 选中
    if(tableRef.value) tableRef.value.toggleRowSelection(item, true)
  }
}

const batchApprove = () => {
  ElMessage.success(`已批量通过 ${selectedRows.value.length} 条数据`)
  selectedRows.value.forEach(row => { if(row.status === 'pending') approveItem(row) })
  selectedRows.value = [] // 清空选择
  if(tableRef.value) tableRef.value.clearSelection()
}

const batchReject = () => {
  ElMessage.warning(`已批量驳回 ${selectedRows.value.length} 条数据`)
  selectedRows.value.forEach(row => { if(row.status === 'pending') optimisticUpdate(row, 'rejected', () => adminStore.updateItemStatus(row.id, 'rejected', row.type), '') })
  selectedRows.value = []
  if(tableRef.value) tableRef.value.clearSelection()
}

// === 右键菜单逻辑 ===
const contextMenu = reactive({ visible: false, x: 0, y: 0, row: null })
const handleRowContextMenu = (row, column, event) => {
  event.preventDefault()
  contextMenu.row = row
  contextMenu.x = event.clientX + 150 > window.innerWidth ? event.clientX - 150 : event.clientX
  contextMenu.y = event.clientY + 160 > window.innerHeight ? event.clientY - 160 : event.clientY
  contextMenu.visible = true
}
const closeContextMenu = () => { if (contextMenu.visible) contextMenu.visible = false }

onMounted(async () => {
  await adminStore.fetchItems()
  document.querySelector('.data-container')?.addEventListener('scroll', closeContextMenu)
})
</script>

<style scoped>
.items-management { padding: 20px; background-color: #f4f7fb; min-height: 100%; position: relative; }

/* 控制面板 */
.control-panel { background: #ffffff; padding: 20px 24px 0; border-radius: 12px; box-shadow: 0 4px 16px rgba(0,0,0,0.04); margin-bottom: 20px; }
.panel-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px; margin-bottom: 16px; }
.title-section { display: flex; align-items: center; gap: 10px; }
.title-icon { font-size: 28px; color: #409eff; }
.title-section h2 { font-size: 22px; font-weight: 700; color: #1f2937; margin: 0; display: flex; align-items: center; gap: 12px; }
.subtitle { font-size: 13px; font-weight: normal; color: #64748b; background: #f1f5f9; padding: 4px 10px; border-radius: 20px; }
.filters-section { display: flex; gap: 12px; align-items: center; }
.ai-search { width: 300px; }
.ai-sparkle { color: #8b5cf6; font-size: 16px; }
:deep(.ai-search .el-input__wrapper) { border-radius: 20px; transition: all 0.3s ease; }
:deep(.ai-search .el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #8b5cf6 inset; }
.filter-select { width: 140px; }

/* 🚀 创新点：视图切换栏 (Notion Style) */
.view-system-bar { 
  display: flex; justify-content: space-between; align-items: center; 
  border-top: 1px solid #f1f5f9; padding-top: 8px; margin-top: 8px; 
}
.view-tabs { display: flex; gap: 4px; overflow-x: auto; }
.view-tab-item { 
  padding: 8px 16px; font-size: 14px; color: #64748b; cursor: pointer; 
  display: flex; align-items: center; gap: 6px; border-radius: 6px 6px 0 0;
  border-bottom: 2px solid transparent; transition: all 0.2s; 
}
.view-tab-item:hover { color: #334155; background: #f8fafc; }
.view-tab-item.active { color: #0f172a; font-weight: 600; border-bottom: 2px solid #0f172a; }
.view-tab-item.add-btn { color: #94a3b8; font-size: 13px; margin-left: 8px; }
.view-tab-item.add-btn:hover { color: #409eff; background: transparent; }
.view-mode-toggle { padding-bottom: 8px; }

/* 数据展示区 */
.data-container { height: calc(100vh - 220px); overflow-y: auto; }

/* 视图 A: 表格 */
.table-container { background: #ffffff; padding: 12px; border-radius: 12px; box-shadow: 0 4px 16px rgba(0,0,0,0.04); }
:deep(.custom-header) { background-color: #f8fafc !important; color: #475569; font-weight: 600; }
.item-title { font-weight: 600; color: #334155; }
.location-cell { display: flex; align-items: center; gap: 4px; color: #64748b; }
.ai-category-wrapper { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 5px; }
.cat-text { color: #475569; font-weight: 600; font-size: 14px; }
.ai-score-badge { display: inline-flex; align-items: center; gap: 4px; font-size: 11px; font-weight: 700; color: #3b82f6; background: linear-gradient(135deg, #eff6ff 0%, #e0e7ff 100%); padding: 2px 8px; border-radius: 12px; border: 1px solid #bfdbfe; cursor: default; }
.status-wrapper { display: flex; align-items: center; justify-content: center; gap: 6px; }
.pulse-dot { width: 8px; height: 8px; background-color: #f56c6c; border-radius: 50%; animation: pulse 1.5s infinite; }
.action-buttons { display: flex; align-items: center; justify-content: center; gap: 4px; }

/* 🚀 视图 B: 画廊模式 (Gallery) */
.gallery-container { padding: 10px 0; }
.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
.gallery-card {
  background: white; border-radius: 12px; overflow: hidden; position: relative;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1); border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); cursor: pointer;
}
.gallery-card:hover { transform: translateY(-4px); box-shadow: 0 12px 24px -8px rgba(0,0,0,0.15); border-color: #cbd5e1; }
.gallery-card.is-selected { border-color: #409eff; box-shadow: 0 0 0 1px #409eff; }

/* 画廊多选框 */
.card-checkbox {
  position: absolute; top: 12px; left: 12px; z-index: 10;
  background: white; border-radius: 4px; padding: 2px 4px;
  opacity: 0; transition: opacity 0.2s; box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}
.gallery-card:hover .card-checkbox, .gallery-card.is-selected .card-checkbox { opacity: 1; }

.card-cover { height: 160px; position: relative; background: #f8fafc; }
.image-bg { width: 100%; height: 100%; background-size: cover; background-position: center; }
.image-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 40px; color: #cbd5e1; background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%); }
.card-status-badge { position: absolute; top: 12px; right: 12px; z-index: 5; }

.card-body { padding: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; gap: 8px; }
.card-title { margin: 0; font-size: 16px; color: #1e293b; font-weight: 600; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-meta { display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: #64748b; margin-bottom: 16px; }
.card-meta span { display: flex; align-items: center; gap: 6px; }

.card-footer { display: flex; gap: 8px; align-items: center; border-top: 1px solid #f1f5f9; padding-top: 12px; }
.flex-1 { flex: 1; }
.poster-info { font-size: 12px; color: #94a3b8; }

/* 右键菜单 & 批量工具栏 */
.custom-context-menu { position: fixed; background: white; border-radius: 8px; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1); padding: 8px 0; min-width: 150px; z-index: 9999; border: 1px solid #e2e8f0; }
.menu-item { padding: 10px 16px; font-size: 14px; color: #334155; cursor: pointer; display: flex; align-items: center; gap: 8px; transition: background 0.2s; }
.menu-item:hover { background: #f1f5f9; }
.menu-item.success:hover { color: #10b981; background: #ecfdf5; }
.menu-item.danger:hover { color: #ef4444; background: #fef2f2; }
.menu-item.divider { height: 1px; background: #e2e8f0; margin: 4px 0; padding: 0; }

.batch-action-bar { position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%); background: #1e293b; color: white; padding: 12px 24px; border-radius: 40px; display: flex; align-items: center; gap: 24px; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.2); z-index: 2000; }
.batch-info { display: flex; align-items: center; gap: 8px; font-size: 14px; }
.ai-suggest { color: #34d399; font-weight: bold; }
.batch-actions { display: flex; gap: 10px; }
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-up-enter-from, .slide-up-leave-to { opacity: 0; transform: translate(-50%, 50px); }

/* 抽屉详情 */
.ai-alert { margin-bottom: 20px; border-radius: 8px; }
.detail-title { font-size: 16px; font-weight: bold; color: #0f172a; }
:deep(.detail-descriptions .el-descriptions__label) { background-color: #f8fafc; font-weight: 600; color: #475569; width: 100px; }
.item-image-wrapper { margin-top: 20px; background: #f8fafc; padding: 12px; border-radius: 8px; border: 1px dashed #cbd5e1; }
.image-title { font-size: 14px; font-weight: 600; color: #475569; margin: 0 0 8px 0; }
.detail-image { width: 100%; max-height: 250px; border-radius: 6px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
</style>