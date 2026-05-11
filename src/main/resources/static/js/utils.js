/**
 * 通用工具函数库
 * 提供Toast提示、确认对话框、加载状态等UX增强功能
 */

// ==================== Toast 提示 ====================

/**
 * 显示Toast提示
 * @param {string} message - 提示消息
 * @param {string} type - 类型: success, error, warning, info
 * @param {number} duration - 显示时长(毫秒)
 */
function showToast(message, type = 'info', duration = 3000) {
    // 移除已存在的toast
    const existing = document.querySelector('.toast-container');
    if (existing) existing.remove();

    const container = document.createElement('div');
    container.className = 'toast-container';
    container.innerHTML = `
        <div class="toast toast-${type}">
            <span class="toast-icon">${getToastIcon(type)}</span>
            <span class="toast-message">${message}</span>
            <button class="toast-close" onclick="this.parentElement.parentElement.remove()">&times;</button>
        </div>
    `;

    document.body.appendChild(container);

    // 自动消失
    setTimeout(() => {
        if (container.parentElement) {
            container.classList.add('toast-hide');
            setTimeout(() => container.remove(), 300);
        }
    }, duration);
}

function getToastIcon(type) {
    const icons = {
        success: '&#10004;',
        error: '&#10008;',
        warning: '&#9888;',
        info: '&#8505;'
    };
    return icons[type] || icons.info;
}

// Toast样式
const toastStyle = document.createElement('style');
toastStyle.textContent = `
    .toast-container {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        animation: slideIn 0.3s ease;
    }
    .toast-container.toast-hide {
        animation: slideOut 0.3s ease;
    }
    .toast {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px 16px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        background: #fff;
        min-width: 280px;
        max-width: 400px;
    }
    .toast-success {
        border-left: 4px solid #52c41a;
    }
    .toast-error {
        border-left: 4px solid #ff4d4f;
    }
    .toast-warning {
        border-left: 4px solid #faad14;
    }
    .toast-info {
        border-left: 4px solid #1890ff;
    }
    .toast-icon {
        font-size: 18px;
    }
    .toast-success .toast-icon { color: #52c41a; }
    .toast-error .toast-icon { color: #ff4d4f; }
    .toast-warning .toast-icon { color: #faad14; }
    .toast-info .toast-icon { color: #1890ff; }
    .toast-message {
        flex: 1;
        font-size: 14px;
    }
    .toast-close {
        background: none;
        border: none;
        font-size: 18px;
        cursor: pointer;
        color: #999;
        padding: 0;
        line-height: 1;
    }
    .toast-close:hover {
        color: #333;
    }
    @keyframes slideIn {
        from { transform: translateX(400px); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(400px); opacity: 0; }
    }
`;
document.head.appendChild(toastStyle);

// ==================== 确认对话框 ====================

/**
 * 显示确认对话框
 * @param {string} message - 确认消息
 * @param {Function} onConfirm - 确认回调
 * @param {Function} onCancel - 取消回调
 */
function showConfirm(message, onConfirm, onCancel) {
    const overlay = document.createElement('div');
    overlay.className = 'confirm-overlay';
    overlay.innerHTML = `
        <div class="confirm-dialog">
            <div class="confirm-icon">&#9888;</div>
            <div class="confirm-message">${message}</div>
            <div class="confirm-buttons">
                <button class="btn btn-cancel" id="confirmCancel">取消</button>
                <button class="btn btn-danger" id="confirmOk">确定</button>
            </div>
        </div>
    `;

    document.body.appendChild(overlay);

    document.getElementById('confirmCancel').onclick = () => {
        overlay.remove();
        if (onCancel) onCancel();
    };

    document.getElementById('confirmOk').onclick = () => {
        overlay.remove();
        if (onConfirm) onConfirm();
    };

    // 点击背景关闭
    overlay.onclick = (e) => {
        if (e.target === overlay) {
            overlay.remove();
            if (onCancel) onCancel();
        }
    };
}

// 确认对话框样式
const confirmStyle = document.createElement('style');
confirmStyle.textContent = `
    .confirm-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0,0,0,0.45);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
    }
    .confirm-dialog {
        background: #fff;
        border-radius: 8px;
        padding: 24px;
        min-width: 320px;
        max-width: 400px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    }
    .confirm-icon {
        font-size: 48px;
        color: #faad14;
        text-align: center;
        margin-bottom: 16px;
    }
    .confirm-message {
        font-size: 14px;
        color: #333;
        text-align: center;
        margin-bottom: 24px;
        line-height: 1.6;
    }
    .confirm-buttons {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
    }
    .confirm-buttons .btn {
        padding: 8px 20px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        border: 1px solid #d9d9d9;
        background: #fff;
    }
    .confirm-buttons .btn-cancel:hover {
        border-color: #1890ff;
        color: #1890ff;
    }
    .confirm-buttons .btn-danger {
        background: #ff4d4f;
        color: #fff;
        border-color: #ff4d4f;
    }
    .confirm-buttons .btn-danger:hover {
        background: #ff7875;
    }
`;
document.head.appendChild(confirmStyle);

// ==================== 加载状态 ====================

/**
 * 显示按钮加载状态
 * @param {HTMLElement} button - 按钮元素
 * @param {string} loadingText - 加载时文本
 */
function showButtonLoading(button, loadingText = '加载中...') {
    button.dataset.originalText = button.textContent;
    button.disabled = true;
    button.innerHTML = `<span class="loading-spinner"></span> ${loadingText}`;
}

/**
 * 隐藏按钮加载状态
 * @param {HTMLElement} button - 按钮元素
 */
function hideButtonLoading(button) {
    button.disabled = false;
    button.textContent = button.dataset.originalText || '提交';
}

// 加载动画样式
const loadingStyle = document.createElement('style');
loadingStyle.textContent = `
    .loading-spinner {
        display: inline-block;
        width: 14px;
        height: 14px;
        border: 2px solid #fff;
        border-top-color: transparent;
        border-radius: 50%;
        animation: spin 0.6s linear infinite;
        vertical-align: middle;
        margin-right: 6px;
    }
    @keyframes spin {
        to { transform: rotate(360deg); }
    }
    .btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
`;
document.head.appendChild(loadingStyle);

/**
 * 显示页面加载遮罩
 * @param {string} message - 加载提示文本
 */
function showPageLoading(message = '加载中...') {
    const overlay = document.createElement('div');
    overlay.id = 'page-loading';
    overlay.className = 'page-loading-overlay';
    overlay.innerHTML = `
        <div class="page-loading-content">
            <div class="page-loading-spinner"></div>
            <div class="page-loading-text">${message}</div>
        </div>
    `;
    document.body.appendChild(overlay);
}

/**
 * 隐藏页面加载遮罩
 */
function hidePageLoading() {
    const overlay = document.getElementById('page-loading');
    if (overlay) overlay.remove();
}

const pageLoadingStyle = document.createElement('style');
pageLoadingStyle.textContent = `
    .page-loading-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(255,255,255,0.8);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9998;
    }
    .page-loading-content {
        text-align: center;
    }
    .page-loading-spinner {
        width: 40px;
        height: 40px;
        border: 4px solid #f0f0f0;
        border-top-color: #1890ff;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
        margin: 0 auto 16px;
    }
    .page-loading-text {
        font-size: 14px;
        color: #666;
    }
`;
document.head.appendChild(pageLoadingStyle);

// ==================== API请求封装 ====================

/**
 * 封装fetch请求，自动处理错误和加载状态
 * @param {string} url - 请求URL
 * @param {Object} options - fetch选项
 * @returns {Promise} 响应数据
 */
async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        const data = await response.json();

        if (data.code === 200) {
            return data.data;
        } else {
            showToast(data.message || '请求失败', 'error');
            throw new Error(data.message);
        }
    } catch (error) {
        console.error('API请求失败:', error);
        showToast(error.message || '网络请求失败', 'error');
        throw error;
    }
}

/**
 * GET请求
 */
async function apiGet(url) {
    return apiRequest(url, { method: 'GET' });
}

/**
 * POST请求
 */
async function apiPost(url, data) {
    return apiRequest(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

/**
 * PUT请求
 */
async function apiPut(url, data) {
    return apiRequest(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

/**
 * DELETE请求
 */
async function apiDelete(url) {
    return apiRequest(url, { method: 'DELETE' });
}
