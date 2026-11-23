# 校园失物招领系统 UML图表

## 功能模块图

```mermaid
graph TD
    A[校园失物招领系统] --> B[用户认证模块]
    A --> C[信息发布模块]
    A --> D[信息查询模块]
    A --> E[智能匹配模块]
    A --> F[用户中心模块]
    A --> G[管理员模块]
    
    B --> B1[注册登录]
    B --> B2[密码找回]
    B --> B3[权限管理]
    
    C --> C1[失物发布]
    C --> C2[招领发布]
    C --> C3[信息编辑]
    C --> C4[信息删除]
    
    D --> D1[失物列表查询]
    D --> D2[招领列表查询]
    D --> D3[关键词搜索]
    D --> D4[AI描述搜索]
    
    E --> E1[自动匹配]
    E --> E2[匹配通知]
    
    F --> F1[个人信息管理]
    F --> F2[我的失物/招领]
    F --> F3[消息管理]
    F --> F4[建议反馈]
    
    G --> G1[仪表盘统计]
    G --> G2[物品管理]
    G --> G3[用户管理]
    G --> G4[分类管理]
    G --> G5[系统设置]
```

## 普通用户用例图

```mermaid
useCaseDiagram
    actor User as "普通用户"
    
    usecase UC1 as "注册登录"
    usecase UC2 as "发布失物"
    usecase UC3 as "发布招领"
    usecase UC4 as "查询失物"
    usecase UC5 as "查询招领"
    usecase UC6 as "AI描述搜索"
    usecase UC7 as "管理个人信息"
    usecase UC8 as "查看我的发布"
    usecase UC9 as "接收消息通知"
    usecase UC10 as "提交建议"
    
    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC9
    User --> UC10
```

## 管理员用例图

```mermaid
useCaseDiagram
    actor Admin as "管理员"
    
    usecase UC1 as "登录系统"
    usecase UC2 as "审核物品信息"
    usecase UC3 as "管理用户"
    usecase UC4 as "管理物品分类"
    usecase UC5 as "查看系统统计"
    usecase UC6 as "系统设置"
    usecase UC7 as "处理用户建议"
    
    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC7
```

## 类图

```mermaid
classDiagram
    class User {
        +id: Integer
        +username: String
        +password: String
        +phone: String
        +email: String
        +avatar: String
        +role: String
        +createTime: LocalDateTime
        +updateTime: LocalDateTime
        +register()
        +login()
        +updateProfile()
    }
    
    class LostItem {
        +id: Integer
        +userId: Integer
        +title: String
        +category: String
        +lostLocation: String
        +lostTime: LocalDateTime
        +description: String
        +images: String
        +contactInfo: String
        +status: String
        +isDeleted: Integer
        +createTime: LocalDateTime
        +updateTime: LocalDateTime
        +create()
        +update()
        +delete()
    }
    
    class FoundItem {
        +id: Integer
        +userId: Integer
        +title: String
        +category: String
        +foundLocation: String
        +foundTime: LocalDateTime
        +description: String
        +images: String
        +contactInfo: String
        +status: String
        +isDeleted: Integer
        +createTime: LocalDateTime
        +updateTime: LocalDateTime
        +create()
        +update()
        +delete()
    }
    
    class Message {
        +id: Integer
        +senderId: Integer
        +receiverId: Integer
        +content: String
        +type: String
        +status: String
        +createTime: LocalDateTime
        +send()
        +markAsRead()
    }
    
    class Suggestion {
        +id: Integer
        +userId: Integer
        +content: String
        +status: String
        +createTime: LocalDateTime
        +updateTime: LocalDateTime
        +submit()
        +process()
    }
    
    class Announcement {
        +id: Integer
        +title: String
        +content: String
        +createTime: LocalDateTime
        +updateTime: LocalDateTime
        +create()
        +update()
        +delete()
    }
    
    User "1" -- "*" LostItem: 发布
    User "1" -- "*" FoundItem: 发布
    User "1" -- "*" Message: 发送/接收
    User "1" -- "*" Suggestion: 提交
```

## E-R图

```mermaid
graph TD
    User["用户(User)"]
    LostItem["失物(LostItem)"]
    FoundItem["招领(FoundItem)"]
    Message["消息(Message)"]
    Suggestion["建议(Suggestion)"]
    Announcement["公告(Announcement)"]
    
    User -- "发布" --> LostItem
    User -- "发布" --> FoundItem
    User -- "发送" --> Message
    User -- "接收" --> Message
    User -- "提交" --> Suggestion
    
    LostItem -- "可能匹配" --> FoundItem
```

## 招领部分流程图

```mermaid
flowchart TD
    A[用户登录] --> B[进入用户中心]
    B --> C[点击发布招领]
    C --> D[填写招领信息]
    D --> E[上传图片]
    E --> F[提交审核]
    F --> G[管理员审核]
    G --> H{审核通过?}
    H -->|是| I[招领信息发布成功]
    H -->|否| J[审核失败，返回修改]
    I --> K[系统自动匹配失物]
    K --> L[发送匹配通知]
    J --> D
```

## 失物部分流程图

```mermaid
flowchart TD
    A[用户登录] --> B[进入用户中心]
    B --> C[点击发布失物]
    C --> D[填写失物信息]
    D --> E[上传图片]
    E --> F[提交审核]
    F --> G[管理员审核]
    G --> H{审核通过?}
    H -->|是| I[失物信息发布成功]
    H -->|否| J[审核失败，返回修改]
    I --> K[系统自动匹配招领]
    K --> L[发送匹配通知]
    J --> D
```