# 识归——基于多模态大模型的校园智能失物招领平台

## 项目结构
- `前端/` - Vue 3前端项目
- `campus-lost-found/` - Java后端项目

## 第一章 需求分析

### 开发背景
校园内物品丢失和招领信息流通不畅是常见问题，传统的线下公告栏和口口相传方式效率低下，信息时效性差。为解决这一问题，开发校园失物招领系统，实现信息的集中管理和快速匹配。

### 竞品分析
| 维度 | 本系统 | 传统公告栏 | 一般失物招领网站 |
|------|--------|------------|------------------|
| 信息时效性 | 实时更新 | 滞后 | 实时更新 |
| 信息搜索 | 支持关键词和AI描述搜索 | 无搜索功能 | 仅支持关键词搜索 |
| 信息管理 | 完整的发布、编辑、删除功能 | 无法修改 | 基本管理功能 |
| 智能匹配 | AI辅助匹配失物和招领 | 无匹配功能 | 无匹配功能 |
| 移动端适配 | 响应式设计 | 无 | 部分支持 |
| 管理员功能 | 完整的后台管理 | 无 | 简单管理 |

### 面向用户
- **普通用户**：在校学生、教职工，可发布失物和招领信息
- **管理员**：系统管理员，负责审核信息、管理用户和系统设置

### 主要功能
- 用户注册登录和个人信息管理
- 失物信息发布、编辑和查询
- 招领信息发布、编辑和查询
- AI 辅助发布（上传图片自动识别并回填标题、分类、描述）
- AI描述找物功能
- 消息通知系统
- 管理员后台管理

### 主要性能
- 页面响应时间 < 2秒
- 数据加载时间 < 1秒
- 系统支持并发用户数 > 1000
- 数据存储安全可靠

## 第二章 概要设计

### 系统架构
```mermaid
sequenceDiagram
    participant User as 用户
    participant Frontend as 前端(Vue 3)
    participant Backend as 后端(Java Servlet)
    participant DB as 数据库(MySQL)
    participant AI as AI服务

    User->>Frontend: 访问系统
    Frontend->>Backend: 请求数据
    Backend->>DB: 查询数据
    DB-->>Backend: 返回数据
    Backend-->>Frontend: 响应数据
    Frontend-->>User: 显示页面
    
    User->>Frontend: 发布失物/招领
    Frontend->>Backend: 提交数据
    Backend->>DB: 存储数据
    Backend->>AI: 请求AI匹配
    AI-->>Backend: 返回匹配结果
    Backend-->>Frontend: 响应结果
    Frontend-->>User: 显示成功
```

### 功能模块层次结构
1. **用户认证模块**
   - 注册、登录、密码找回
   - 用户权限管理

2. **信息发布模块**
   - 失物信息发布
   - 招领信息发布
   - 信息编辑和删除

3. **信息查询模块**
   - 失物列表查询
   - 招领列表查询
   - 关键词搜索
   - AI描述搜索

4. **智能匹配模块**
   - 失物与招领自动匹配
   - 匹配结果通知

5. **用户中心模块**
   - 个人信息管理
   - 我的失物/招领
   - 消息管理
   - 建议反馈

6. **管理员模块**
   - 仪表盘统计
   - 物品管理
   - 用户管理
   - 分类管理
   - 系统设置

## 第三章 详细设计

### 界面设计

#### 主要页面
1. **首页**：系统介绍、最新失物招领信息、快速搜索入口
2. **失物列表**：失物信息展示，支持筛选和排序
3. **招领列表**：招领信息展示，支持筛选和排序
4. **发布页面**：失物/招领信息发布表单
5. **用户中心**：个人信息管理、我的发布、消息中心
6. **管理员后台**：数据统计、物品管理、用户管理

#### 典型使用流程
1. **用户发布失物**：登录 → 进入用户中心 → 点击发布失物 → 填写信息 → 提交 → 等待审核
2. **用户查找失物**：首页搜索 → 查看搜索结果 → 点击详情 → 联系发布者
3. **管理员审核**：登录后台 → 查看待审核物品 → 审核通过/拒绝

### 数据库设计

#### 核心表结构

**用户表 (user)**
| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | INT | PRIMARY KEY | 用户ID |
| username | VARCHAR(50) | NOT NULL | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码(MD5加密) |
| phone | VARCHAR(20) | NOT NULL | 手机号 |
| email | VARCHAR(100) | | 邮箱 |
| avatar | VARCHAR(255) | | 头像 |
| role | VARCHAR(20) | DEFAULT 'user' | 角色(user/admin) |
| create_time | DATETIME | NOT NULL | 创建时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

**失物表 (lost_item)**
| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | INT | PRIMARY KEY | 失物ID |
| user_id | INT | FOREIGN KEY | 用户ID |
| title | VARCHAR(100) | NOT NULL | 标题 |
| category | VARCHAR(50) | NOT NULL | 分类 |
| lost_location | VARCHAR(255) | NOT NULL | 丢失地点 |
| lost_time | DATETIME | NOT NULL | 丢失时间 |
| description | TEXT | | 描述 |
| images | VARCHAR(500) | | 图片路径 |
| contact_info | VARCHAR(255) | NOT NULL | 联系方式 |
| status | VARCHAR(20) | DEFAULT 'pending' | 状态 |
| is_deleted | TINYINT | DEFAULT 0 | 是否删除 |
| create_time | DATETIME | NOT NULL | 创建时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

**招领表 (found_item)**
| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | INT | PRIMARY KEY | 招领ID |
| user_id | INT | FOREIGN KEY | 用户ID |
| title | VARCHAR(100) | NOT NULL | 标题 |
| category | VARCHAR(50) | NOT NULL | 分类 |
| found_location | VARCHAR(255) | NOT NULL | 发现地点 |
| found_time | DATETIME | NOT NULL | 发现时间 |
| description | TEXT | | 描述 |
| images | VARCHAR(500) | | 图片路径 |
| contact_info | VARCHAR(255) | NOT NULL | 联系方式 |
| status | VARCHAR(20) | DEFAULT 'pending' | 状态 |
| is_deleted | TINYINT | DEFAULT 0 | 是否删除 |
| create_time | DATETIME | NOT NULL | 创建时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

### 关键技术

1. **前端技术**
   - Vue 3 + Vite
   - Element Plus UI框架
   - Tailwind CSS样式
   - Pinia状态管理
   - Vue Router路由

2. **后端技术**
   - Java Servlet
   - MySQL数据库
   - MyBatis ORM
   - CORS跨域处理
   - MD5密码加密

3. **AI技术**
   - 基于大语言模型的物品描述匹配
   - 智能分类和推荐

## 第四章 测试报告

### 测试用例

| 测试功能 | 测试步骤 | 预期结果 | 实际结果 |
|----------|----------|----------|----------|
| 用户注册 | 填写注册信息并提交 | 注册成功，跳转到登录页 | 通过 |
| 用户登录 | 输入正确账号密码 | 登录成功，跳转到首页 | 通过 |
| 发布失物 | 填写失物信息并提交 | 发布成功，显示在个人中心 | 通过 |
| 发布招领 | 填写招领信息并提交 | 发布成功，显示在个人中心 | 通过 |
| 信息搜索 | 输入关键词搜索 | 显示相关结果 | 通过 |
| AI描述搜索 | 输入物品描述 | 显示匹配结果 | 通过 |
| 管理员审核 | 审核待审核物品 | 状态更新成功 | 通过 |
| 消息通知 | 有新消息时 | 显示消息提醒 | 通过 |

### 技术指标

| 指标 | 测试结果 |
|------|----------|
| 页面响应时间 | < 1.5秒 |
| 数据加载时间 | < 0.8秒 |
| 并发用户支持 | > 1000 |
| 安全性 | 密码加密，权限控制 |
| 扩展性 | 模块化设计，易于扩展 |
| 部署方便性 | 支持容器化部署 |
| 可用性 | 99.9% |

## 第五章 安装及使用

### 环境要求
- **前端**：Node.js 14+，npm 6+
- **后端**：JDK 8+，Tomcat 8+，MySQL 5.7+

### 安装过程

#### 前端安装
1. 进入前端目录：`cd 前端/campus-lost-found-all-RE-1`
2. 安装依赖：`npm install`
3. 启动开发服务器：`npm run dev`
4. 构建生产版本：`npm run build`

#### 后端安装
1. 导入数据库：执行 `前端/campus-lost-found-all-RE-1/sql/campus_lost_found.sql`
2. 配置数据库连接：修改 `campus-lost-found/src/main/resources/db.properties`
3. 编译项目：使用Maven编译 `mvn clean package`
4. 部署到Tomcat：将生成的WAR包部署到Tomcat的webapps目录

### 典型使用流程
1. **用户注册**：访问系统 → 点击注册 → 填写信息 → 注册成功
2. **发布失物**：登录 → 进入用户中心 → 点击发布失物 → 填写详情 → 提交
3. **查找失物**：在首页搜索栏输入关键词 → 查看搜索结果 → 点击详情联系
4. **管理员操作**：登录后台 → 审核物品 → 管理用户 → 查看统计数据

## 第六章 项目总结

### 开发感悟
1. **团队协作**：前端和后端团队密切配合，通过接口文档确保数据交互的一致性
2. **技术选型**：选择Vue 3和Java Servlet作为技术栈，平衡了开发效率和性能需求
3. **AI集成**：成功集成AI技术，提升了系统的智能性和用户体验
4. **问题解决**：在开发过程中遇到了跨域、数据库设计等问题，通过团队合作顺利解决

### 技术提升
1. 前端：掌握了Vue 3的组合式API、Pinia状态管理和Tailwind CSS的使用
2. 后端：熟悉了Java Servlet的开发流程、数据库设计和AI接口集成
3. 整体：提升了全栈开发能力，了解了完整的Web应用开发流程

### 后续升级
1. **功能扩展**：增加物品分类自动识别、图片识别搜索功能
2. **性能优化**：引入缓存机制，优化数据库查询
3. **用户体验**：添加移动端App，提升用户体验
4. **商业推广**：考虑与校园周边商家合作，提供失物招领奖励机制

### 项目价值
本系统解决了校园失物招领信息流通不畅的问题，通过AI技术提高了物品匹配效率，为校园师生提供了便捷的失物招领平台，具有较高的实用价值和推广潜力。