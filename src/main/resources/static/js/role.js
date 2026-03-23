/**
 * 角色管理页面 JavaScript
 */

// 加载角色列表
function loadRoles() {
    fetch('/api/roles')
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('roleTableBody');
            if (data.code === 200 && data.data) {
                tbody.innerHTML = data.data.map(role => `
                    <tr>
                        <td>${role.id}</td>
                        <td>${role.name}</td>
                        <td>${role.code}</td>
                        <td>${role.description || '-'}</td>
                        <td><span class="status-badge ${role.status === 1 ? 'status-active' : 'status-inactive'}">
                            ${role.status === 1 ? '启用' : '禁用'}
                        </span></td>
                        <td>
                            <button class="btn btn-primary" onclick="editRole(${role.id})">编辑</button>
                            <button class="btn btn-warning" onclick="assignMenus(${role.id})">分配菜单</button>
                            <button class="btn btn-danger" onclick="deleteRole(${role.id})">删除</button>
                        </td>
                    </tr>
                `).join('');
            }
        });
}

// 加载菜单树
function loadMenus() {
    fetch('/api/roles/menus')
        .then(res => res.json())
        .then(data => {
            const tree = document.getElementById('menuTree');
            if (data.code === 200 && data.data) {
                tree.innerHTML = data.data.map(menu => `
                    <div class="menu-item">
                        <input type="checkbox" name="menuIds" value="${menu.id}" id="menu_${menu.id}">
                        <label for="menu_${menu.id}">${menu.name}</label>
                    </div>
                `).join('');
            }
        });
}

// 显示新增弹窗
function showAddModal() {
    document.getElementById('modalTitle').textContent = '新增角色';
    document.getElementById('roleForm').reset();
    document.getElementById('roleId').value = '';
    document.getElementById('roleModal').style.display = 'block';
    loadMenus();
}

// 编辑角色
function editRole(id) {
    fetch(`/api/roles/${id}`)
        .then(res => res.json())
        .then(data => {
            if (data.code === 200) {
                document.getElementById('modalTitle').textContent = '编辑角色';
                document.getElementById('roleId').value = data.data.role.id;
                document.getElementById('roleName').value = data.data.role.name;
                document.getElementById('roleCode').value = data.data.role.code;
                document.getElementById('roleDesc').value = data.data.role.description || '';
                document.getElementById('roleModal').style.display = 'block';
                loadMenus();
            }
        });
}

// 分配菜单
function assignMenus(id) {
    // 简化实现：打开弹窗让用户选择菜单
    showAddModal();
}

// 删除角色
function deleteRole(id) {
    if (confirm('确定要删除该角色吗？')) {
        fetch(`/api/roles/${id}`, { method: 'DELETE' })
            .then(res => res.json())
            .then(data => {
                if (data.code === 200) {
                    alert('删除成功');
                    loadRoles();
                } else {
                    alert('删除失败：' + data.message);
                }
            });
    }
}

// 关闭弹窗
function closeModal() {
    document.getElementById('roleModal').style.display = 'none';
}

// 退出登录
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => window.location.href = '/login');
}

// 初始化
loadRoles();
loadMenus();
