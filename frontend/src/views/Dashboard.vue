<template>
  <div class="dashboard">
    <h2 class="page-title">工作台</h2>
    
    <!-- 关键指标卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6" v-for="item in metrics" :key="item.label">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待办任务 -->
    <el-card class="mb-20">
      <template #header><span>待办任务</span></template>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="待审批申请">{{ todoStats.application || 0 }}</el-descriptions-item>
        <el-descriptions-item label="待签署合同">{{ todoStats.contract || 0 }}</el-descriptions-item>
        <el-descriptions-item label="待放款">{{ todoStats.loan || 0 }}</el-descriptions-item>
        <el-descriptions-item label="待处理还款">{{ todoStats.repayment || 0 }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 状态分布图表 -->
    <el-card>
      <template #header><span>贷款状态分布</span></template>
      <div ref="chartRef" style="height: 300px"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMetrics, getTodoStats, getStatusDistribution } from '@/api/dashboard'
import * as echarts from 'echarts'

const metrics = ref([])
const todoStats = ref({})
const chartRef = ref(null)

const loadMetrics = async () => {
  try {
    const data = await getMetrics()
    metrics.value = [
      { label: '总申请数', value: data.totalApplications },
      { label: '总客户数', value: data.totalCustomers },
      { label: '待审批', value: data.pendingApproval },
      { label: '通过率', value: data.approvalRate }
    ]
  } catch (e) {
    console.error('加载指标失败', e)
  }
}

const loadTodoStats = async () => {
  try {
    todoStats.value = await getTodoStats()
  } catch (e) {
    console.error('加载待办统计失败', e)
  }
}

const loadChart = async () => {
  try {
    const data = await getStatusDistribution()
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '5%', left: 'center' },
      series: [{
        name: '贷款状态',
        type: 'pie',
        radius: ['40%', '70%'],
        data: Object.entries(data).map(([key, value]) => ({ name: key, value })),
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
        }
      }]
    })
  } catch (e) {
    console.error('加载图表失败', e)
  }
}

onMounted(() => {
  loadMetrics()
  loadTodoStats()
  loadChart()
})
</script>

<style scoped>
.page-title { margin-bottom: 20px; color: #333; }
.mb-20 { margin-bottom: 20px; }
.metric-card { text-align: center; }
.metric-value { font-size: 28px; font-weight: bold; color: #409eff; }
.metric-label { font-size: 14px; color: #909399; margin-top: 8px; }
</style>
