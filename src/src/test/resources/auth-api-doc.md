# 认证 API 接口文档

本文档详细说明了认证系统的 API 接口，包括注册和登录功能。

## 1. 用户注册

### 接口信息

- **URL**: `/api/auth/register`
- **方法**: `POST`
- **描述**: 注册新用户

### 请求参数

请求体为 JSON 格式，包含以下字段：

| 字段名    | 类型   | 必填 | 描述     |
|---------|--------|-----|----------|
| username | String | 是   | 用户名，长度为 3-20 个字符，只能包含字母、数字和下划线 |
| password | String | 是   | 密码，长度至少为 6 个字符 |
| email    | String | 是   | 有效的电子邮件地址 |
| phone    | String | 否   | 手机号码 |

示例：
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "phone": "13800138000"
}
```

### 响应参数

#### 成功响应

- **状态码**: `200 OK`
- **内容类型**: `application/json`

响应体：

| 字段名    | 类型   | 描述     |
|---------|--------|----------|
| message | String | 成功消息 |
| user    | Object | 用户信息对象 |
| user.id | Long   | 用户ID |
| user.username | String | 用户名 |
| user.email    | String | 电子邮件地址 |
| user.status   | Integer| 用户状态（1:正常, 0:禁用） |
| user.createTime | String | 账号创建时间 |
| user.updateTime | String | 账号最后更新时间 |

示例：
```json
{
  "message": "Registration successful",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "status": 1,
    "createTime": "2025-03-26T16:30:00",
    "updateTime": "2025-03-26T16:30:00"
  }
}
```

#### 失败响应

- **状态码**: `400 Bad Request`
- **内容类型**: `application/json`

响应体：

| 字段名 | 类型   | 描述     |
|-------|--------|----------|
| error | String | 错误消息 |

可能的错误消息：
- "Username already exists" - 用户名已被占用
- "Email already exists" - 电子邮件已被占用
- "Invalid username format" - 用户名格式无效
- "Password must be at least 6 characters" - 密码长度不足

示例：
```json
{
  "error": "Username already exists"
}
```

## 2. 用户登录

### 接口信息

- **URL**: `/api/auth/login`
- **方法**: `POST`
- **描述**: 用户登录并获取认证信息

### 请求参数

请求体为 JSON 格式，包含以下字段：

| 字段名    | 类型   | 必填 | 描述     |
|---------|--------|-----|----------|
| username | String | 是   | 用户名 |
| password | String | 是   | 密码 |

示例：
```json
{
  "username": "testuser",
  "password": "password123"
}
```

### 响应参数

#### 成功响应

- **状态码**: `200 OK`
- **内容类型**: `application/json`

响应体：

| 字段名    | 类型   | 描述     |
|---------|--------|----------|
| message | String | 成功消息 |
| user    | Object | 用户信息对象 |
| user.id | Long   | 用户ID |
| user.username | String | 用户名 |
| user.email    | String | 电子邮件地址 |
| user.status   | Integer| 用户状态（1:正常, 0:禁用） |
| user.createTime | String | 账号创建时间 |
| user.updateTime | String | 账号最后更新时间 |

示例：
```json
{
  "message": "Login successful",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "status": 1,
    "createTime": "2025-03-26T16:30:00",
    "updateTime": "2025-03-26T16:30:00"
  }
}
```

#### 失败响应

- **状态码**: `400 Bad Request`
- **内容类型**: `application/json`

响应体：

| 字段名 | 类型   | 描述     |
|-------|--------|----------|
| error | String | 错误消息 |

可能的错误消息：
- "Invalid username or password" - 用户名或密码错误
- "Account is disabled" - 账户被禁用
- "Empty credentials" - 空凭据

示例：
```json
{
  "error": "Invalid username or password"
}
```

## 测试用例

### 注册接口测试

1. **成功注册新用户**
   - 输入有效的用户名、密码和电子邮件
   - 预期结果：返回 200 状态码，包含新用户信息

2. **尝试注册已存在的用户名**
   - 使用已存在的用户名注册
   - 预期结果：返回 400 状态码，错误消息 "Username already exists"

3. **使用无效的电子邮件格式**
   - 提供格式错误的电子邮件
   - 预期结果：返回 400 状态码，错误消息指示电子邮件格式无效

### 登录接口测试

1. **成功登录**
   - 使用正确的用户名和密码
   - 预期结果：返回 200 状态码，包含用户信息

2. **错误的凭据**
   - 使用不存在的用户名或错误的密码
   - 预期结果：返回 400 状态码，错误消息 "Invalid username or password"

3. **空凭据**
   - 使用空用户名或空密码
   - 预期结果：返回 400 状态码 