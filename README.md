# 识归——基于多模态大模型的校园智能失物招领平台

> 中国大学生计算机设计大赛 · 软件应用与开发类参赛作品
> Vue 3 + Java Servlet + MySQL｜2 人小组（余恩慧、李宇欣）

用一句话描述你丢了什么，系统在招领库里做语义匹配，把可能是你的东西推到你面前。

## 功能

- **账号与个人中心**：注册 / 登录 / 找回密码，资料与密码维护，我的发布、消息通知、建议反馈
- **失物 / 招领**：发布、编辑、删除、图片上传，列表筛选与关键词搜索，发布后走审核流转
- **AI 辅助发布**：招领发布页上传照片，视觉模型识别后回填标题、分类与描述；失物发布输入标题即可自动补全分类、地点、时间
- **AI 智能匹配**：失物详情页自动列出"可能匹配到的招领信息"，由大模型给出相似度与匹配理由并排序
- **AI 对话助手**：`/ai-search` 页为聊天式工作台，可询问平台用法，也可直接描述要找的东西
- **管理后台**：数据看板、物品审核、分类管理、用户管理、系统设置

## 技术栈

| 层 | 技术 |
| --- | --- |
| 前端 | Vue 3.3、Vite 4、Element Plus、Pinia、Vue Router、Axios、Tailwind CSS |
| 后端 | Java 21、Jakarta Servlet 6、MyBatis 3.5、MySQL 8、Jackson、CORS 过滤器、MD5 密码加密、Maven（WAR） |
| AI | 通义千问 qwen-plus（语义匹配 / 字段补全）、qwen-vl-plus（图像理解 / 识图回填）、百度图像搜索 |

## 目录结构

```
final/
├── 前端/campus-lost-found-all-RE-1/   Vue 3 前端（内含 sql/campus_lost_found.sql 建库脚本）
├── campus-lost-found/                 Java 后端（Maven WAR 工程）
├── README.md                          完整设计文档：需求 / 架构 / 表结构 / 测试 / 部署
├── 01-3 软件应用与开发类作品设计和开发文档模板（2025版）.docx   参赛正式文档
├── 01-2 作品信息摘要模板（2025版）V2.docx
├── 01-1 软件应用与开发类作品提交要求（2025版）V2-1.docx
├── UML.md                             用例图、类图、时序图
├── 功能详细说明.md
├── 概要介绍.docx
├── 项目详细方案.docx
└── 政产学研用-AI动态交互扶贫平台.pptx   参赛答辩 PPT
archive/                               早期版本与过程文件（截图、旧版表导出）
```

## 快速开始

```bash
# 1. 建库
mysql -u root -p < "final/前端/campus-lost-found-all-RE-1/sql/campus_lost_found.sql"

# 2. 填配置
#    final/campus-lost-found/src/main/resources/db.properties   → 数据库账号密码
#    final/campus-lost-found/src/main/resources/api.properties  → 通义千问 / 百度图像搜索 Key

# 3. 后端：打包后丢进 Tomcat 的 webapps
cd final/campus-lost-found && mvn clean package

# 4. 前端
cd "final/前端/campus-lost-found-all-RE-1" && npm install && npm run dev
```

环境要求：JDK 17+（pom 按 21 编译）、Tomcat 10+（Jakarta Servlet 6）、MySQL 8、Node 18+。

> 配置里的 Key 均为占位符，需要自己到[阿里云百炼](https://bailian.console.aliyun.com/)申请；不填也能跑基础功能，AI 相关接口会返回"未配置"。

## 说明

- 两人小组作品：一人负责前端与前后端联调，一人负责后端与数据库设计。
- 开发中借助 Cursor 生成脚手架与重复 CRUD，需求拆解、表结构、接口协议与联调排错由组员完成。
- 参赛文档 01-3 第五章写的"JDK 8+ / Tomcat 8+"是早期版本遗留：当前后端已迁到 `jakarta.*`（Servlet 6），实际需要 JDK 17+ 与 Tomcat 10.1+。
- 以图搜物（`/lostapi/imageSearch`，百度相似图 + 视觉模型描述）后端接口已实现，界面暂未接入；上面"功能"里未列此项。
- 更完整的设计说明见 [`final/README.md`](final/README.md)。
