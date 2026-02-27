<template>
  <div class="message-list-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>我的消息</h2>
      <p>查看和管理您的站内消息</p>
    </div>

    <!-- 消息标签页 -->
    <div class="message-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="全部消息" name="all">
          <template #label>
            <span>全部消息</span>
            <el-badge v-if="unreadCount > 0" :value="unreadCount" />
          </template>
        </el-tab-pane>
        <el-tab-pane label="未读消息" name="unread">
          <template #label>
            <span>未读消息</span>
            <el-badge v-if="unreadCount > 0" :value="unreadCount" />
          </template>
        </el-tab-pane>
        <el-tab-pane label="已读消息" name="read" />
      </el-tabs>
    </div>

    <!-- 消息列表 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="filteredMessages.length === 0" class="empty-container">
      <el-empty :description="emptyDescription" />
    </div>

    <div v-else class="messages-list">
      <div v-for="message in paginatedMessages" :key="message.id" class="message-item">
        <div class="message-header" @click="viewMessage(message)">
          <div class="message-sender">
            <el-avatar :src="message.from_user?.avatar || '/default-avatar.png'" :size="40" />
            <div class="sender-info">
              <div class="sender-name">
                {{ message.fromUserName || message.from_user?.real_name || message.from_user?.username || '系统' }}
                <el-tag v-if="message.status === 'unread'" type="danger" size="small">新</el-tag>
              </div>
              <div class="send-time">{{ formatDate(message.createTime || message.create_time) }}</div>
            </div>
          </div>
          
          <div class="message-item-info">
            <span class="item-title" @click.stop="viewRelatedItem(message)">
              相关：{{ message.itemTitle || message.item_title || '未知物品' }}
            </span>
            <el-tag size="small" :type="(message.itemType || message.item_type) === 'lost' ? 'warning' : 'success'">
              {{ (message.itemType || message.item_type) === 'lost' ? '失物' : '招领' }}
            </el-tag>
          </div>
        </div>
        
        <div class="message-content">
          <p>{{ truncateText(message.content, 100) }}</p>
        </div>
        
        <div class="message-actions">
          <el-button type="text" @click="viewMessage(message)">
            查看详情
          </el-button>
          <el-button v-if="message.status === 'unread'" type="text" @click="markAsRead(message)">
            标记为已读
          </el-button>
          <el-button type="text" class="delete-btn" @click="deleteMessage(message)">
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredMessages.length > 0" class="pagination-container">
      <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="filteredMessages.length"
        :page-sizes="[10, 20, 30]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { deleteMessage as deleteMessageApi, getMessages, markMessageAsRead, getUnreadCount } from '@/api/message'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 当前标签页
const activeTab = ref('all')

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const loading = ref(false)
const allMessages = ref([]) // 存储所有消息
const unreadCount = ref(0)

// 计算属性：根据标签页过滤消息
const filteredMessages = computed(() => {
  if (!allMessages.value.length) return []
  
  switch (activeTab.value) {
    case 'unread':
      return allMessages.value.filter(msg => msg.status === 'unread')
    case 'read':
      return allMessages.value.filter(msg => msg.status === 'read')
    default:
      return allMessages.value
  }
})

// 计算属性：当前页的消息
const paginatedMessages = computed(() => {
  const start = (pagination.current - 1) * pagination.size
  const end = start + pagination.size
  return filteredMessages.value.slice(start, end)
})

// 空状态描述
const emptyDescription = computed(() => {
  switch (activeTab.value) {
    case 'unread':
      return '暂无未读消息'
    case 'read':
      return '暂无已读消息'
    default:
      return '暂无消息'
  }
})

onMounted(() => {
  fetchMessages()
  fetchUnreadCount()
})

// 获取消息列表
const fetchMessages = async () => {
  loading.value = true
  try {
    // 获取当前用户ID
    const userId = userStore.userInfo?.id
    
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用后端接口
    const response = await getMessages({ userId })
    
    // 根据后端响应格式处理
    if (response.code === 200) {
      // 映射字段，统一数据结构
      allMessages.value = (response.data.messages || []).map(msg => ({
        id: msg.id,
        fromUserId: msg.fromUserId,
        fromUserName: msg.fromUserName,
        toUserId: msg.toUserId,
        toUserName: msg.toUserName,
        item_id: msg.itemId,
        itemId: msg.itemId,
        item_type: msg.itemType,
        itemType: msg.itemType,
        item_title: msg.itemTitle,
        itemTitle: msg.itemTitle,
        content: msg.content,
        status: msg.status,
        create_time: msg.createTime,
        createTime: msg.createTime
      }))
    } else {
      allMessages.value = []
      ElMessage.error(response.message || '获取消息失败')
    }
    
  } catch (error) {
    console.error('获取消息失败:', error)
    ElMessage.error('加载失败: ' + (error.message || '请检查网络连接'))
    allMessages.value = []
  } finally {
    loading.value = false
  }
}

// 获取未读消息数量
const fetchUnreadCount = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) return
    
    const response = await getUnreadCount(userId)
    if (response.code === 200) {
      unreadCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    console.error('获取未读消息数失败:', error)
  }
}

// 标签页切换
const handleTabChange = () => {
  pagination.current = 1
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
}

const handleCurrentChange = (page) => {
  pagination.current = page
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  // 如果是今天，显示时间
  if (diff < 24 * 60 * 60 * 1000) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }
  // 如果是昨天
  if (diff < 2 * 24 * 60 * 60 * 1000) {
    return '昨天'
  }
  // 其他显示日期
  return date.toLocaleDateString()
}

// 截断文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 查看消息详情
const viewMessage = (message) => {
  // 如果是未读消息，先标记为已读
  if (message.status === 'unread') {
    markAsRead(message)
  }
  router.push(`/user/messages/${message.id}`)
}

// 查看相关物品
const viewRelatedItem = (message) => {
  const itemType = message.itemType || message.item_type
  const itemId = message.itemId || message.item_id
  
  if (itemType === 'lost') {
    router.push(`/lost/${itemId}`)
  } else {
    router.push(`/found/${itemId}`)
  }
}

// 标记为已读
const markAsRead = async (message) => {
  try {
    const response = await markMessageAsRead(message.id)
    if (response.success) {
      // 更新本地状态
      const msg = allMessages.value.find(m => m.id === message.id)
      if (msg) {
        msg.status = 'read'
      }
      // 更新未读计数
      unreadCount.value = Math.max(0, unreadCount.value - 1)
      ElMessage.success('标记为已读')
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除消息
const deleteMessage = async (message) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条消息吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteMessageApi(message.id)
    if (response.success) {
      ElMessage.success('删除成功')
      // 从列表中移除
      allMessages.value = allMessages.value.filter(m => m.id !== message.id)
      // 重新获取未读计数
      fetchUnreadCount()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.message-list-container {
  padding: 20px;
}

/* 页面标题 */
.page-title {
  margin-bottom: 30px;
}

.page-title h2 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 24px;
}

.page-title p {
  margin: 0;
  color: #7f8c8d;
}

/* 标签页 */
.message-tabs {
  background: white;
  border-radius: 10px 10px 0 0;
  padding: 20px 20px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 加载状态 */
.loading-container {
  background: white;
  border-radius: 0 0 10px 10px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 空状态 */
.empty-container {
  background: white;
  border-radius: 0 0 10px 10px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 消息列表 */
.messages-list {
  background: white;
  border-radius: 0 0 10px 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 消息项 */
.message-item {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  transition: background 0.3s;
}

.message-item:hover {
  background: #f8f9fa;
}

.message-item:last-child {
  border-bottom: none;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  cursor: pointer;
}

.message-sender {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sender-info {
  flex: 1;
}

.sender-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
}

.send-time {
  font-size: 12px;
  color: #6c757d;
}

.message-item-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.item-title {
  color: #3498db;
  cursor: pointer;
  transition: color 0.3s;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-title:hover {
  color: #2980b9;
  text-decoration: underline;
}

/* 消息内容 */
.message-content {
  margin-bottom: 15px;
}

.message-content p {
  margin: 0;
  color: #495057;
  line-height: 1.6;
}

/* 消息操作 */
.message-actions {
  display: flex;
  gap: 15px;
}

.message-actions .el-button {
  padding: 4px 8px;
  font-size: 12px;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  color: #f78989;
}

/* 分页 */
.pagination-container {
  margin-top: 20px;
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .message-item-info {
    align-items: flex-start;
    width: 100%;
  }
  
  .item-title {
    max-width: 100%;
  }
  
  .message-actions {
    flex-wrap: wrap;
  }
}
</style>