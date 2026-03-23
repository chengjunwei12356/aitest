let sessionId = '';

// 获取验证码
async function fetchCaptcha() {
    try {
        const response = await fetch('/api/auth/captcha');
        const result = await response.json();

        if (result.code === 200) {
            sessionId = result.data.sessionId;
            document.getElementById('captchaImage').src = 'data:image/png;base64,' + result.data.image;
        }
    } catch (error) {
        console.error('获取验证码失败:', error);
    }
}

// 显示消息
function showMessage(message, type) {
    const messageEl = document.getElementById('message');
    messageEl.textContent = message;
    messageEl.className = 'message ' + type;

    if (type === 'success') {
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 1500);
    }
}

// 处理登录
async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const captcha = document.getElementById('captcha').value;
    const loginBtn = document.querySelector('.login-btn');

    loginBtn.disabled = true;
    loginBtn.textContent = '登录中...';

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password,
                captcha: captcha,
                sessionId: sessionId
            })
        });

        const result = await response.json();

        if (result.code === 200) {
            showMessage('登录成功！正在跳转...', 'success');
        } else {
            showMessage(result.message || '登录失败，请重试', 'error');
            // 刷新验证码
            fetchCaptcha();
            document.getElementById('captcha').value = '';
        }
    } catch (error) {
        console.error('登录失败:', error);
        showMessage('网络错误，请重试', 'error');
        fetchCaptcha();
        document.getElementById('captcha').value = '';
    } finally {
        loginBtn.disabled = false;
        loginBtn.textContent = '登录';
    }
}

// 页面加载时获取验证码
document.addEventListener('DOMContentLoaded', function() {
    fetchCaptcha();

    // 绑定表单提交事件
    document.getElementById('loginForm').addEventListener('submit', handleLogin);

    // 点击验证码图片刷新
    document.getElementById('captchaImage').addEventListener('click', function() {
        fetchCaptcha();
    });
});
