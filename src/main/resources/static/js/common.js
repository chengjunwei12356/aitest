/**
 * 公共 JavaScript 工具函数
 */

/**
 * 格式化日期
 */
function formatDate(dateStr) {
    if (!dateStr) return '';
    return dateStr.substring(0, 10);
}

/**
 * 退出登录
 */
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => window.location.href = '/login');
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
