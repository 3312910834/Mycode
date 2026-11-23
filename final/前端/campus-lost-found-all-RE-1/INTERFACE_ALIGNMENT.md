# 前后端接口对齐清单（v2）

## 认证与用户

- `GET /userapi/login`：登录，成功后写入 Session（`currentUser`）并返回用户信息。
- `GET /userapi/register`：注册。
- `GET /userapi/info?id=`：获取用户信息。
- `POST /userapi/update`：更新用户资料。
- `POST /userapi/updatePassword`：修改密码（`userId/id + old_password + new_password`）。
- `POST /userapi/logout`：退出登录，销毁 Session。

## 失物模块

- `GET /lostapi/list`：失物列表（`page,size`）。
- `GET /lostapi/detail?id=`：失物详情。
- `GET /lostapi/my?userId=`：我的失物。
- `GET /lostapi/search?keyword=`：搜索失物。
- `POST /lostapi/publish`：发布失物。
- `POST /lostapi/update`：更新失物。
- `POST /lostapi/delete`：删除失物。
- `POST /lostapi/smartMatch`：AI 文本匹配。

## 招领模块

- `GET /foundapi/list`：招领列表（`page,size`）。
- `GET /foundapi/detail?id=`：招领详情。
- `GET /foundapi/my?userId=`：我的招领。
- `GET /foundapi/search?keyword=`：搜索招领。
- `POST /foundapi/publish`：发布招领。
- `POST /foundapi/update`：更新招领。
- `POST /foundapi/delete`：删除招领。
- `POST /foundapi/autoFillByImage`：AI 图像识别填充。
- `POST /foundapi/{id}/claim`：认领申请（已兼容前端路径格式）。

## 管理端模块

受 `AdminAuthFilter` 保护：`/admin/*` 与 `/adminapi/*`，支持 Session 或 Bearer Token 识别管理员。

- `GET /admin/user?action=list&page&limit&username&role&status`：用户分页列表。
- `POST /admin/user?action=updateStatus&id&status`：更新用户状态。
- `POST /admin/user?action=updateRole&id&role`：更新用户角色。

- `GET /adminapi/items?page&size&type&status&keyword`：信息管理分页与筛选。
  - 返回 `data.items / data.total / data.page / data.size`。
- `POST /adminapi/item/status`：更新信息状态（`id,type,status`）。
- `POST /adminapi/item/delete`：删除信息（`id,type`）。
- `GET /adminapi/stats`：管理看板统计。
- `GET /adminapi/categories`、`POST /adminapi/categories`：分类配置。
- `GET /adminapi/settings`、`POST /adminapi/settings`：系统设置。

## 首页模块

- `GET /homeapi/stats`：首页统计（`lostCount/foundCount/successCount/userCount`）。

## 消息模块（联系消息 `contact_message`）

基路径：`/messageapi/*`（`MessageApiServlet`）。前端封装见 `src/api/message.js`。

### GET

| 路径 | 参数 | 成功响应 `data` 要点 |
|------|------|------------------------|
| `/messageapi/received` | `userId`（必填） | `messages[]`：`id, fromUserId, fromUserName, itemId, itemType, itemTitle, content, status, createTime` |
| `/messageapi/sent` | `userId`（必填） | `messages[]`：含 `toUserId, toUserName` 等 |
| `/messageapi/detail` | `id`（必填） | 单条：`fromUserId, toUserId, itemId, itemType, itemTitle, content, status, ...` |
| `/messageapi/unread` | `userId`（必填） | `unreadCount` |
| `/messageapi/conversation` | `user1Id`, `user2Id` | 双方会话 `messages[]` |
| `/messageapi/item` | `itemId`, `itemType`（`lost`/`found`） | 该物品相关消息 |

### POST（`Content-Type: application/x-www-form-urlencoded`，参数与 Servlet `getParameter` 一致）

| 路径 | 参数 | 响应 |
|------|------|------|
| `/messageapi/send` | `fromUserId`, `toUserId`, `itemId`, `itemType`, `content` | `{ success, messageId }` 或 `{ error }` |
| `/messageapi/read` | `id` | `{ success: true/false }` |
| `/messageapi/delete` | `id` | `{ success: true/false }` |

### 说明

- `status` 枚举与库表一致：`unread` / `read`。
- 错误多为 `{ error: "..." }`，无 `code` 字段；前端 axios 拦截器会提示 `error` 文案。
- 根路径 `GET /messageapi/` 返回可用接口列表（文档里未列出的 `GET /messageapi/detail` 已实现）。

## 建议模块（`suggestion`）

基路径：`/suggestionapi/*`（`SuggestionApiServlet`）。前端封装见 `src/api/suggestion.js`。用户身份以登录后 `utils/auth` 的 `userInfo.id` 为准（与 Pinia 写入的 `userInfo` 一致）。

### GET

| 路径 | 参数 | 成功响应 `data` 要点 |
|------|------|------------------------|
| `/suggestionapi/list` | `page`, `size` | 全站分页：`suggestions[]`（含 `userId, username, content, status, hasReply, createTime`） |
| `/suggestionapi/my` | `userId`（必填） | **不分页**，`count` + `suggestions[]`（项内含 `reply`） |
| `/suggestionapi/detail` | `id` | 单条详情 |
| `/suggestionapi/stats` | 无 | `total, pending, read, processed` |
| `/suggestionapi/byStatus` | `status`（必填） | `pending` / `read` / `processed` 等与库表枚举一致 |

### POST（表单 `application/x-www-form-urlencoded`）

| 路径 | 参数 | 响应 |
|------|------|------|
| `/suggestionapi/submit` | `userId`, `content` | `{ success:true, suggestionId }` 或 `{ error }` |
| `/suggestionapi/reply` | `id`, `reply` | `{ success }` |
| `/suggestionapi/updateStatus` | `id`, `status` | `{ success }` |
| `/suggestionapi/delete` | `id` | `{ success }` |

### 说明

- 前端 `getSuggestions` / `getUserSuggestions` 仅调用 `/my`，分页在页面本地对数组切片即可；**勿把 `page/size` 当作后端分页参数**。
- 删除、更新状态、回复等接口与 `submit` 一样走 **POST + 表单字段**，不是 JSON body。

## 当前已完成的关键对齐

- 管理端 `adminapi` 缺失接口已补齐。
- 用户改密、退出登录接口已补齐。
- 招领认领接口已补齐并与前端对齐。
- 首页统计已从 mock 切到真实后端接口。
- 详情页/首页请求层已收敛为统一 API 调用，减少环境耦合。
- 消息/建议接口与 `message.js`、`suggestion.js` 及 Servlet 行为已对齐文档说明；建议提交统一使用 `userInfo` 解析用户 ID。
