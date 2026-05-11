<template>
  <div class="page-container">
    <h2 class="page-title">客户管理</h2>
    
    <el-card class="mb-20">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="keyword" placeholder="姓名/手机号/身份证号" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleCreate">新建客户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="idNo" label="身份证号" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="身份证号" prop="idNo"><el-input v-model="form.idNo" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱" prop="email"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="地址" prop="address"><el-input v-model="form.address" type="textarea" /></el-form-item>
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
import { getCustomers, searchCustomers, createCustomer, updateCustomer, deleteCustomer } from '@/api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const keyword = ref('')
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建客户')
const formRef = ref(null)

const form = reactive({ id: null, name: '', idNo: '', phone: '', email: '', address: '' })

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idNo: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    tableData.value = keyword.value ? await searchCustomers(keyword.value) : await getCustomers()
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const handleCreate = () => {
  dialogTitle.value = '新建客户'
  Object.assign(form, { id: null, name: '', idNo: '', phone: '', email: '', address: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑客户'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (form.id) await updateCustomer(form.id, form)
      else await createCustomer(form)
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadData()
    } catch (e) { console.error(e) } finally { submitLoading.value = false }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该客户吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deleteCustomer(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) { console.error(e) }
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.page-title { margin-bottom: 20px; color: #333; }
.mb-20 { margin-bottom: 20px; }
</style>
