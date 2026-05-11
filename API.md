# 登录 API 接口文档

## 接口列表

### 1. 获取验证码
**请求：**
```
GET /api/auth/captcha
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "image": "iVBORw0KGgoAAAANSUhEUgAA...",
    "sessionId": "abc123def456..."
  }
}
```

### 2. 用户登录
**请求：**
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456",
  "captcha": "A3B4",
  "sessionId": "abc123def456..."
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "token": "token_admin_1234567890",
    "success": true
  }
}
```

### 3. 用户登出
**请求：**
```
POST /api/auth/logout
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 500 | 服务器内部错误 |
| 1001 | 用户名或密码错误 |
| 1002 | 用户已被禁用 |
| 1003 | 验证码错误 |
| 1004 | 验证码已过期 |
| 1005 | 用户不存在 |

## 测试账号

- 用户名：`admin`
- 密码：`123456`

## 使用流程

1. 调用 `GET /api/auth/captcha` 获取验证码图片和 sessionId
2. 将验证码图片展示给用户
3. 用户输入用户名、密码和验证码
4. 调用 `POST /api/auth/login` 进行登录
5. 登录成功后保存返回的 token 用于后续请求
