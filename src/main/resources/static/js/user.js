/**
 * 用户管理页面 JavaScript
 */

// 加载用户列表
function loadUsers() {
    fetch('/api/users')
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('userTableBody');
            if (data.code === 200 && data.data) {
                tbody.innerHTML = data.data.map(user => `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.username}</td>
                        <td>${user.email || '-'}</td>
                        <td>${user.phone || '-'}</td>
                        <td><span class="status-badge ${user.status === 1 ? 'status-active' : 'status-inactive'}">
                            ${user.status === 1 ? '启用' : '禁用'}
                        </span></td>
                        <td>
                            <button class="btn btn-primary" onclick="editUser(${user.id})">编辑</button>
                            <button class="btn btn-warning" onclick="resetPassword(${user.id})">重置密码</button>
                            <button class="btn btn-danger" onclick="deleteUser(${user.id})">删除</button>
                        </td>
                    </tr>
                `).join('');
            }
        });
}

// 加载角色选项
function loadRoles() {
    fetch('/api/users/roles')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('roles');
            if (data.code === 200 && data.data) {
                select.innerHTML = data.data.map(role =>
                    `<option value="${role.id}">${role.name}</option>`
                ).join('');
            }
        });
}

// 显示新增弹窗
function showAddModal() {
    document.getElementById('modalTitle').textContent = '新增用户';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('userModal').style.display = 'block';
}

// 编辑用户
function editUser(id) {
    fetch(`/api/users/${id}`)
        .then(res => res.json())
        .then(data => {
            if (data.code === 200) {
                document.getElementById('modalTitle').textContent = '编辑用户';
                document.getElementById('userId').value = data.data.user.id;
                document.getElementById('username').value = data.data.user.username;
                document.getElementById('password').value = '';
                document.getElementById('status').value = data.data.user.status;
                document.getElementById('userModal').style.display = 'block';
            }
        });
}

// 重置密码
function resetPassword(id) {
    const newPassword = prompt('请输入新密码：');
    if (newPassword) {
        fetch(`/api/users/${id}/password`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ password: newPassword })
        })
        .then(res => res.json())
        .then(data => {
            if (data.code === 200) {
                alert('密码重置成功');
                loadUsers();
            } else {
                alert('密码重置失败：' + data.message);
            }
        });
    }
}

// 删除用户
function deleteUser(id) {
    if (confirm('确定要删除该用户吗？')) {
        fetch(`/api/users/${id}`, { method: 'DELETE' })
            .then(res => res.json())
            .then(data => {
                if (data.code === 200) {
                    alert('删除成功');
                    loadUsers();
                } else {
                    alert('删除失败：' + data.message);
                }
            });
    }
}

// 关闭弹窗
function closeModal() {
    document.getElementById('userModal').style.display = 'none';
}

// 退出登录
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => window.location.href = '/login');
}

// 初始化
loadUsers();
loadRoles();
