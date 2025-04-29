# 智简未来

这是一个基于 SpringBoot + Vue3 + MySQL 构建的智能客服系统，具备以下功能：

- 用户管理（注册 / 登录 / 修改 / 删除）
- 智能推荐（简历分析、岗位匹配）
- 职业路径规划（AI辅助）
- 后台管理系统（权限控制）

## 技术栈

- 后端：Spring Boot、MyBatis、JWT、MySQL
- 前端：Vue3、Element Plus、Axios
- 数据分析：Python（Pandas / scikit-learn / Flask）

## 快速开始

```bash
# 克隆项目
git clone https://github.com/你的用户名/你的项目名.git

# 进入项目目录
cd ai-customer

# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
npm install
npm run dev

```

## 后端端项目结构

```
ai-resume-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── resume/
│   │   │               ├── config/              # 配置类
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── JwtFilter.java
│   │   │               │   ├── JwtUtils.java
│   │   │               │   └── CorsConfig.java
│   │   │               ├── controller/          # 控制器
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── ResumeController.java
│   │   │               │   ├── JobController.java
│   │   │               │   └── AdminController.java
│   │   │               ├── entity/              # 实体类
│   │   │               │   ├── User.java
│   │   │               │   ├── Resume.java
│   │   │               │   ├── Job.java
│   │   │               │   └── MatchRecord.java
│   │   │               ├── mapper/              # Mapper接口
│   │   │               │   ├── UserMapper.java
│   │   │               │   ├── ResumeMapper.java
│   │   │               │   ├── JobMapper.java
│   │   │               │   └── MatchRecordMapper.java
│   │   │               ├── service/             # 服务接口
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── ResumeService.java
│   │   │               │   ├── JobService.java
│   │   │               │   └── AdminService.java
│   │   │               ├── service/impl/        # 服务实现类
│   │   │               │   ├── AuthServiceImpl.java
│   │   │               │   ├── ResumeServiceImpl.java
│   │   │               │   ├── JobServiceImpl.java
│   │   │               │   └── AdminServiceImpl.java
│   │   │               ├── common/              # 通用返回类
│   │   │               │   ├── Result.java
│   │   │               │   ├── ResultCode.java
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               └── ResumeApplication.java  # 主程序入口
│   │   ├── resources/
│   │   │   ├── application.yml    # 配置文件
│   │   │   ├── mapper/             # MyBatis Plus的xml文件
│   │   │   │   ├── UserMapper.xml
│   │   │   │   ├── ResumeMapper.xml
│   │   │   │   ├── JobMapper.xml
│   │   │   │   └── MatchRecordMapper.xml
│   │   │   └── static/             # 静态资源（如果有）
│   │   │   └── templates/          # 页面模板（如邮件模板）
│   ├── test/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               └── resume/
│   │                   └── ResumeApplicationTests.java  # 测试类
├── Dockerfile
├── README.md
├── pom.xml                      # Maven依赖文件
└── .gitignore

```

## 前端项目结构

```
ai-resume-frontend/
├── public/                  # 静态资源
│   └── favicon.ico
├── src/
│   ├── api/                  # 所有API请求
│   │   ├── auth.js           # 登录注册接口
│   │   ├── resume.js         # 简历相关接口
│   │   ├── job.js            # 职位推荐接口
│   │   └── ai.js             # AI分析接口
│   ├── assets/               # 图片、样式资源
│   ├── components/           # 公共组件
│   │   ├── ResumeForm.vue    # 简历编辑表单
│   │   └── ...
│   ├── layouts/              # 页面布局组件
│   │   └── DefaultLayout.vue
│   ├── router/               # Vue Router配置
│   │   └── index.js
│   ├── store/                # Pinia状态管理
│   │   ├── user.js           # 用户状态
│   │   └── resume.js         # 简历状态
│   ├── views/                # 页面组件
│   │   ├── Login.vue         # 登录页
│   │   ├── Register.vue      # 注册页
│   │   ├── Dashboard.vue     # 用户主页
│   │   ├── ResumeEditor.vue  # 简历编辑页
│   │   ├── JobRecommend.vue  # 岗位推荐页
│   │   ├── AdminPanel.vue    # 管理员面板
│   │   └── NotFound.vue      # 404页面
│   ├── utils/                # 工具函数
│   │   ├── request.js        # 封装axios
│   │   └── auth.js           # Token处理
│   ├── App.vue               # 根组件
│   └── main.js               # 项目入口
├── .env                      # 环境变量
├── vite.config.js            # Vite配置文件
├── package.json              # 项目依赖
└── README.md

```

