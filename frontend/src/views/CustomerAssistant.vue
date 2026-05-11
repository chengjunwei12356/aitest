<template>
  <div class="page-container">
    <h2 class="page-title">客户经理助手</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="客户提醒" name="reminders">
        <el-card>
          <el-table :data="reminders" v-loading="reminderLoading" border>
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="reminderType" label="类型" width="120" />
            <el-table-column prop="priority" label="优先级" width="80">
              <template #default="{ row }">
                <el-tag :type="row.priority === 3 ? 'danger' : row.priority === 2 ? 'warning' : 'info'" size="small">
                  {{ row.priority === 3 ? '高' : row.priority === 2 ? '中' : '低' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'RESOLVED' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'RESOLVED' ? '已解决' : '待处理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="截止时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button v-if="row.status !== 'RESOLVED'" size="small" type="primary" @click="resolveReminder(row)">解决</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="知识库" name="knowledge">
        <el-card>
          <el-input v-model="searchKeyword" placeholder="搜索知识库..." prefix-icon="Search" clearable @keyup.enter="loadKnowledge" style="margin-bottom: 20px" />
          <el-table :data="knowledgeArticles" v-loading="knowledgeLoading" border>
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="viewCount" label="浏览次数" width="100" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" @click="viewArticle(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('reminders')
const reminderLoading = ref(false)
const knowledgeLoading = ref(false)
const reminders = ref([])
const knowledgeArticles = ref([])
const searchKeyword = ref('')

const loadReminders = async () => {
  reminderLoading.value = true
  try {
    // TODO: API集成
    reminders.value = []
  } catch (e) { console.error(e) } finally { reminderLoading.value = false }
}

const loadKnowledge = async () => {
  knowledgeLoading.value = true
  try {
    // TODO: API集成
    knowledgeArticles.value = []
  } catch (e) { console.error(e) } finally { knowledgeLoading.value = false }
}

const resolveReminder = (row) => {
  ElMessage.success('提醒已标记为已解决')
  loadReminders()
}

const viewArticle = (row) => {
  ElMessage.info(`查看文章: ${row.title}`)
}

onMounted(() => {
  loadReminders()
})
</script>

<style scoped>
.page-title { margin-bottom: 20px; color: #333; }
</style>
