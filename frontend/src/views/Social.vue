<template>
  <div class="social-container">
    <el-card class="social-card">
      <template #header>
        <div class="card-header">
          <h2>社交互动</h2>
          <el-button type="primary" @click="createPost">
            <el-icon><Edit /></el-icon> 发布动态
          </el-button>
        </div>
      </template>
      
      <!-- 发布动态表单 -->
      <el-dialog v-model="postDialogVisible" title="发布动态">
        <el-form :model="postForm" label-width="80px">
          <el-form-item label="内容">
            <el-input
              v-model="postForm.content"
              type="textarea"
              :rows="4"
              placeholder="分享你的音乐心情..."
            ></el-input>
          </el-form-item>
          
          <el-form-item label="关联音乐">
            <el-input v-model="postForm.song" placeholder="输入歌曲名称"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="postDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitPost">发布</el-button>
          </span>
        </template>
      </el-dialog>
      
      <!-- 动态列表 -->
      <div class="posts-list">
        <el-card v-for="post in posts" :key="post.id" shadow="hover" class="post-card">
          <div class="post-header">
            <el-avatar :size="48" class="user-avatar">
              {{ post.user.charAt(0) }}
            </el-avatar>
            <div class="user-info">
              <h4>{{ post.user }}</h4>
              <p>{{ formatDate(post.createdAt) }}</p>
            </div>
          </div>
          
          <div class="post-content">
            <p>{{ post.content }}</p>
            <div v-if="post.song" class="post-song">
              <el-button size="small" type="info">{{ post.song }}</el-button>
            </div>
          </div>
          
          <div class="post-actions">
            <el-button size="small" @click="likePost(post)">
              <el-icon><Star /></el-icon> {{ post.likes }} 点赞
            </el-button>
            <el-button size="small" @click="commentPost(post)">
              <el-icon><ChatDotRound /></el-icon> {{ post.commentsCount }} 评论
            </el-button>
            <el-button size="small">
              <el-icon><Share /></el-icon> 分享
            </el-button>
          </div>
          
          <!-- 评论列表 -->
          <div v-if="post.comments.length > 0" class="comments-list">
            <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
              <el-avatar :size="32" class="comment-avatar">
                {{ comment.user.charAt(0) }}
              </el-avatar>
              <div class="comment-content">
                <h5>{{ comment.user }}</h5>
                <p>{{ comment.content }}</p>
                <p class="comment-time">{{ formatDate(comment.createdAt) }}</p>
              </div>
            </div>
          </div>
          
          <!-- 评论输入 -->
          <div class="comment-input">
            <el-input
              v-model="commentContent"
              placeholder="写下你的评论..."
              @keyup.enter="submitComment(post.id)"
            ></el-input>
            <el-button type="primary" @click="submitComment(post.id)">发送</el-button>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Edit, Star, ChatDotRound, Share } from '@element-plus/icons-vue'

const postDialogVisible = ref(false)
const postForm = reactive({
  content: '',
  song: ''
})

const commentContent = ref('')

// 模拟数据
const posts = ref([
  {
    id: 1,
    user: '用户1',
    content: '今天听了一首很好听的歌，分享给大家！',
    song: '孤勇者 - 陈奕迅',
    likes: 12,
    commentsCount: 3,
    createdAt: new Date(),
    comments: [
      { id: 1, user: '用户2', content: '确实很好听！', createdAt: new Date() },
      { id: 2, user: '用户3', content: '我也喜欢这首歌', createdAt: new Date() },
      { id: 3, user: '用户4', content: '感谢分享', createdAt: new Date() }
    ]
  },
  {
    id: 2,
    user: '用户5',
    content: '推荐一个很棒的歌单，都是我喜欢的类型',
    song: '流行热歌',
    likes: 8,
    commentsCount: 2,
    createdAt: new Date(),
    comments: [
      { id: 4, user: '用户6', content: '看起来不错', createdAt: new Date() },
      { id: 5, user: '用户7', content: '收藏了', createdAt: new Date() }
    ]
  }
])

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

const createPost = () => {
  postDialogVisible.value = true
}

const submitPost = () => {
  // 发布动态逻辑
  console.log('Post content:', postForm.content)
  // 模拟发布
  posts.value.unshift({
    id: posts.value.length + 1,
    user: '当前用户',
    content: postForm.content,
    song: postForm.song,
    likes: 0,
    commentsCount: 0,
    createdAt: new Date(),
    comments: []
  })
  postDialogVisible.value = false
  postForm.content = ''
  postForm.song = ''
}

const likePost = (post) => {
  post.likes++
}

const commentPost = (post) => {
  // 评论逻辑
  console.log('Commenting on post:', post.id)
}

const submitComment = (postId) => {
  if (!commentContent.value) return
  
  // 提交评论逻辑
  const post = posts.value.find(p => p.id === postId)
  if (post) {
    post.comments.push({
      id: post.comments.length + 1,
      user: '当前用户',
      content: commentContent.value,
      createdAt: new Date()
    })
    post.commentsCount++
    commentContent.value = ''
  }
}
</script>

<style scoped>
.social-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.social-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.posts-list {
  margin-top: 20px;
}

.post-card {
  margin-bottom: 20px;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.user-avatar {
  background-color: #667eea;
  font-weight: bold;
}

.user-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
}

.user-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.post-content {
  margin-bottom: 15px;
}

.post-content p {
  margin: 0 0 10px 0;
  line-height: 1.5;
}

.post-song {
  margin-top: 10px;
}

.post-actions {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.comments-list {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.comment-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.comment-avatar {
  background-color: #764ba2;
  font-weight: bold;
}

.comment-content h5 {
  margin: 0 0 5px 0;
  font-size: 14px;
  font-weight: bold;
}

.comment-content p {
  margin: 0 0 5px 0;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #999 !important;
}

.comment-input {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.comment-input .el-input {
  flex: 1;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
