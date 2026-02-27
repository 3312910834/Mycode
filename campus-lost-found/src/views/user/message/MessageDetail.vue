<template>
  <div class="message-detail-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>消息详情</h2>
      <p>查看和回复消息</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 消息详情 -->
    <div v-else-if="message.id" class="message-detail">
      <!-- 消息头部 -->
      <div class="message-header">
        <el-button type="text" @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回列表
        </el-button>
      </div>

      <!-- 消息内容 -->
      <div class="message-content">
        <div class="message-info">
          <div class="sender-info">
            <el-avatar :src="message.from_user?.avatar || '/default-avatar.png'" :size="60" />
            <div class="sender-details">
              <h3>{{ message.fromUserName || message.from_user?.real_name || message.from_user?.username || '系统' }}</h3>
              <p>{{ formatDateTime(message.createTime || message.create_time) }}</p>
            </div>
          </div>
          
          <div class="related-item">
            <el-tag :type="(message.itemType || message.item_type) === 'lost' ? 'warning' : 'success'" size="large">
              {{ (message.itemType || message.item_type) === 'lost' ? '失物' : '招领' }}
            </el-tag>
            <span class="item-title" @click="viewRelatedItem">
              {{ message.itemTitle || message.item_title || '未知物品' }}
            </span>
          </div>
        </div>

        <!-- 消息正文 -->
        <div class="message-body">
          <div class="message-text">
            {{ message.content }}
          </div>
        </div>

        <!-- 回复表单 -->
        <div v-if="message.fromUserId !== currentUserId" class="reply-form">
          <h3>回复消息</h3>
          <el-form :model="replyForm" class="reply-form-content">
            <el-form-item prop="content">
              <el-input
                v-model="replyForm.content"
                type="textarea"
                :rows="4"
                placeholder="请输入回复内容..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item>
              <div class="reply-actions">
                <el-button type="primary" :loading="replying" @click="sendReply">
                  发送回复
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>

        <!-- 历史消息 -->
        <div v-if="messageHistory.length > 0" class="message-history">
          <h3>历史对话</h3>
          <div class="history-list">
            <div
              v-for="(history, index) in messageHistory"
              :key="index"
              :class="['history-item', history.fromUserId === currentUserId ? 'sent' : 'received']"
            >
              <div class="history-content">
                <p>{{ history.content }}</p>
                <span class="history-time">{{ formatTime(history.createTime || history.create_time) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 消息不存在 -->
    <div v-else class="empty-container">
      <el-empty description="消息不存在或已被删除" />
      <div style="text-align: center; margin-top: 20px;">
        <el-button type="primary" @click="goBack">返回消息列表</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getMessageDetail, sendMessage, getConversation } from '@/api/message'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const replying = ref(false)
const currentUserId = userStore.userInfo?.id || 0

// 消息数据
const message = ref({})
const messageHistory = ref([])

// 回复表单
const replyForm = reactive({
  content: ''
})

onMounted(() => {
  fetchMessageDetail()
})

// 获取消息详情和历史对话
const fetchMessageDetail = async () => {
  const messageId = route.params.id
  if (!messageId) {
    ElMessage.error('消息ID不存在')
    router.push('/user/messages')
    return
  }

  loading.value = true
  try {
    // 获取消息详情
    const detailResponse = await getMessageDetail(messageId)
    
    if (detailResponse.code === 200) {
      message.value = detailResponse.data
      
      // 获取对话历史
      const fromUserId = message.value.fromUserId
      const toUserId = message.value.toUserId
      
      if (fromUserId && toUserId) {
        const historyResponse = await getConversation(fromUserId, toUserId)
        if (historyResponse.code === 200) {
          messageHistory.value = historyResponse.data.messages || []
        }
      }
    } else {
      ElMessage.error(detailResponse.error || '消息不存在')
      router.push('/user/messages')
    }
  } catch (error) {
    console.error('获取消息详情失败:', error)
    ElMessage.error('加载失败')
    router.push('/user/messages')
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

// 返回列表
const goBack = () => {
  router.push('/user/messages')
}

// 查看相关物品
const viewRelatedItem = () => {
  const itemType = message.value.itemType || message.value.item_type
  const itemId = message.value.itemId || message.value.item_id
  
  if (itemType === 'lost') {
    router.push(`/lost/${itemId}`)
  } else {
    router.push(`/found/${itemId}`)
  }
}

// 发送回复
const sendReply = async () => {
  if (!replyForm.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  replying.value = true
  try {
    const replyData = {
      fromUserId: currentUserId,
      toUserId: message.value.fromUserId,
      itemId: message.value.itemId || message.value.item_id,
      itemType: message.value.itemType || message.value.item_type,
      content: replyForm.content
    }
    
    const response = await sendMessage(replyData)
    
    if (response.success) {
      ElMessage.success('回复发送成功')
      
      // 添加回复到历史记录
      messageHistory.value.push({
        id: response.messageId,
        fromUserId: currentUserId,
        content: replyForm.content,
        createTime: new Date().toISOString()
      })
      
      // 清空回复框
      replyForm.content = ''
    } else {
      ElMessage.error(response.error || '发送失败')
    }
  } catch (error) {
    console.error('发送回复失败:', error)
    ElMessage.error('发送失败')
  } finally {
    replying.value = false
  }
}
</script>

<style scoped>
.message-detail-container {
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

/* 加载状态 */
.loading-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 20px;
}

/* 消息详情 */
.message-detail {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 消息头部 */
.message-header {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

/* 消息内容 */
.message-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.sender-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sender-details h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 18px;
}

.sender-details p {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-title {
  color: #409eff;
  cursor: pointer;
  transition: color 0.3s;
}

.item-title:hover {
  color: #1677ff;
  text-decoration: underline;
}

/* 消息正文 */
.message-body {
  margin-bottom: 40px;
}

.message-text {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 25px;
  line-height: 1.6;
  color: #495057;
  font-size: 15px;
  white-space: pre-wrap;
}

/* 回复表单 */
.reply-form {
  margin-bottom: 40px;
  padding-top: 30px;
  border-top: 2px solid #f0f2f5;
}

.reply-form h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.reply-form-content {
  max-width: 600px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
}

/* 历史消息 */
.message-history {
  padding-top: 30px;
  border-top: 2px solid #f0f2f5;
}

.message-history h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.history-item {
  max-width: 70%;
  position: relative;
}

.history-item.sent {
  align-self: flex-end;
}

.history-item.received {
  align-self: flex-start;
}

.history-item.sent .history-content {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.history-item.received .history-content {
  background: #f0f2f5;
  color: #495057;
  border-bottom-left-radius: 4px;
}

.history-content {
  padding: 12px 16px;
  border-radius: 10px;
  border-bottom-right-radius: 0;
}

.history-item.sent .history-content {
  border-bottom-left-radius: 10px;
}

.history-item.received .history-content {
  border-bottom-right-radius: 10px;
}

.history-content p {
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.history-time {
  font-size: 12px;
  opacity: 0.8;
  display: block;
  text-align: right;
}

/* 空状态 */
.empty-container {
  background: white;
  border-radius: 10px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-detail {
    padding: 20px;
  }
  
  .message-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .history-item {
    max-width: 85%;
  }
  
  .reply-actions .el-button {
    width: 100%;
  }
}
</style>