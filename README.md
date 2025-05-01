# AI Resume System (智简未来)

一个基于 AI 的智能简历分析和职业规划系统，帮助求职者更好地规划职业发展路径。

## 🌟 系统特点

- 📝 智能简历分析 - 基于 AI 的简历解析和评估
- 💼 精准岗位匹配 - 智能推荐最适合的工作机会
- 🎯 职业规划指导 - AI 辅助的职业发展路径规划
- 👥 用户管理系统 - 完整的用户注册、登录和管理功能
- 🔐 权限控制系统 - 多角色的后台管理系统

## 🛠 技术栈

### 后端技术
- 核心框架：Spring Boot
- 安全框架：Spring Security + JWT
- 数据库：MySQL
- ORM 框架：MyBatis
- API 文档：Swagger
- 其他：Maven、Lombok

### 前端技术
- 核心框架：Vue 3
- UI 组件：Element Plus
- 状态管理：Pinia
- 路由管理：Vue Router
- HTTP 客户端：Axios
- 构建工具：Vite


## 🚀 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+

### 后端启动
```bash
# 克隆项目
git clone https://github.com/your-username/ai-resume-system.git

# 进入后端目录
cd ai-resume-system/backend

# 配置数据库
# 修改 src/main/resources/application.yml 中的数据库配置

# 启动项目
mvn spring-boot:run
```

### 前端启动
```bash
# 进入前端目录
cd ai-resume-system/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产环境
npm run build
```

## 📚 项目结构

### 后端结构
```
ai-resume-backend/
├── src/main/java/com/example/
│   ├── dto/         #数据传输对象
│   ├── controller/      # 控制器
│   ├── entity/         # 实体类
│   ├── repository/       #数据访问层
│   ├── security/         #数据安全层
│   ├── service/        # 服务层
```

### 前端结构
```
ai-resume-frontend/
├── src/
│   ├── api/           # API 接口
│   ├── components/    # 组件
│   ├── views/         # 页面
│   ├── router/        # 路由
│   ├── store/         # 状态管理
│   └── utils/         # 工具函数
```

## 🔒 安全性

- 使用 JWT 进行身份验证
- 实现 RBAC 权限控制
- 密码加密存储
- 防止 XSS 和 CSRF 攻击
- 接口访问限制

## 📋 功能列表

### 用户功能
- 用户注册与登录
- 简历上传与编辑
- 简历智能分析
- 职位匹配推荐
- 职业规划建议

### 管理员功能
- 用户管理
- 简历管理
- 职位管理
- 系统监控
- 权限配置

## 🤝 贡献指南

1. Fork 本仓库
2. 创建新的分支 `git checkout -b feature/your-feature`
3. 提交更改 `git commit -m 'Add some feature'`
4. 推送到分支 `git push origin feature/your-feature`
5. 提交 Pull Request

## 📄 开源协议

本项目使用 [MIT 许可证](LICENSE)

## 👥 联系我们

- 项目负责人：[sanmu]
- 邮箱：[3345166238@qq.com]
- 项目地址：[GitHub Repository URL]

