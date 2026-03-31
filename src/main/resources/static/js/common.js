/**
 * 公共 JavaScript 工具函数
 */

/**
 * 格式化日期（支持多种格式）
 */
function formatDate(dateStr) {
    if (!dateStr) return '';
    // 尝试解析日期
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * 退出登录（带错误处理）
 */
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/login';
        })
        .catch(err => {
            console.error('退出登录失败:', err);
            window.location.href = '/login';
        });
}

/**
 * 显示提示信息
 */
function showMessage(message, type) {
    const msgEl = document.getElementById('message');
    if (msgEl) {
        msgEl.textContent = message;
        msgEl.className = 'message ' + (type || 'error');
        setTimeout(() => {
            msgEl.textContent = '';
            msgEl.className = 'message';
        }, 3000);
    }
}
