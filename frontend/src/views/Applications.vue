<template>
  <div class="page-container">
    <h2 class="page-title">申请管理</h2>
    
    <!-- 筛选栏 -->
    <el-card class="mb-20">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="待审批" value="PENDING_APPROVAL" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleCreate">新建申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="customerName" label="客户姓名" />
        <el-table-column prop="customerIdNo" label="身份证号" />
        <el-table-column prop="customerPhone" label="手机号" />
        <el-table-column label="贷款金额">
          <template #default="{ row }">{{ (row.loanAmount / 10000).toFixed(2) }} 万元</template>
        </el-table-column>
        <el-table-column prop="loanTerm" label="期限(月)" width="100" />
        <el-table-column prop="loanPurpose" label="用途" />
        <el-table-column label="担保方式" width="100">
          <template #default="{ row }">{{ translateGuaranteeType(row.guaranteeType) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ translateStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="客户姓名" prop="customerName">
          <el-input v-model="form.customerName" />
        </el-form-item>
        <el-form-item label="证件号码" prop="customerIdNo">
          <el-input v-model="form.customerIdNo" />
        </el-form-item>
        <el-form-item label="手机号" prop="customerPhone">
          <el-input v-model="form.customerPhone" />
        </el-form-item>
        <el-form-item label="贷款金额(元)" prop="loanAmount">
          <el-input-number v-model="form.loanAmount" :min="0" :step="10000" style="width:100%" />
        </el-form-item>
        <el-form-item label="贷款期限(月)" prop="loanTerm">
          <el-input-number v-model="form.loanTerm" :min="1" :max="360" style="width:100%" />
        </el-form-item>
        <el-form-item label="贷款用途" prop="loanPurpose">
          <el-select v-model="form.loanPurpose" style="width:100%">
            <el-option label="消费" value="消费" />
            <el-option label="经营" value="经营" />
            <el-option label="购房" value="购房" />
            <el-option label="装修" value="装修" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="担保方式" prop="guaranteeType">
          <el-select v-model="form.guaranteeType" style="width:100%">
            <el-option label="信用" value="CREDIT" />
            <el-option label="保证" value="GUARANTEE" />
            <el-option label="抵押" value="MORTGAGE" />
            <el-option label="质押" value="PLEDGE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApplications, createApplication, updateApplication, deleteApplication } from '@/api/application'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建申请')
const formRef = ref(null)

const searchForm = reactive({ keyword: '', status: '' })

const form = reactive({
  id: null,
  customerName: '',
  customerIdNo: '',
  customerPhone: '',
  loanAmount: 0,
  loanTerm: 12,
  loanPurpose: '',
  guaranteeType: ''
})

const rules = {
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  customerIdNo: [{ required: true, message: '请输入证件号码', trigger: 'blur' }],
  customerPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  loanAmount: [{ required: true, message: '请输入贷款金额', trigger: 'blur' }],
  loanTerm: [{ required: true, message: '请输入贷款期限', trigger: 'blur' }],
  loanPurpose: [{ required: true, message: '请选择贷款用途', trigger: 'change' }],
  guaranteeType: [{ required: true, message: '请选择担保方式', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    tableData.value = await getApplications(searchForm)
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  dialogTitle.value = '新建申请'
  Object.assign(form, { id: null, customerName: '', customerIdNo: '', customerPhone: '', loanAmount: 0, loanTerm: 12, loanPurpose: '', guaranteeType: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑申请'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (form.id) {
        await updateApplication(form.id, form)
      } else {
        await createApplication(form)
      }
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadData()
    } catch (e) {
      console.error('保存失败', e)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条申请吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deleteApplication(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      console.error('删除失败', e)
    }
  })
}

const translateStatus = (status) => {
  const map = { DRAFT: '草稿', PENDING_APPROVAL: '待审批', APPROVED: '已通过', REJECTED: '已拒绝', DISBURSED: '已放款' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { DRAFT: 'info', PENDING_APPROVAL: 'warning', APPROVED: 'success', REJECTED: 'danger', DISBURSED: '' }
  return map[status] || ''
}

const translateGuaranteeType = (type) => {
  const map = { CREDIT: '信用', GUARANTEE: '保证', MORTGAGE: '抵押', PLEDGE: '质押' }
  return map[type] || type
}

onMounted(() => loadData())
</script>

<style scoped>
.page-title { margin-bottom: 20px; color: #333; }
.mb-20 { margin-bottom: 20px; }
</style>
