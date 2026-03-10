<!-- Last updated: 2026-03-10 -->
# 工作流：创建后端API

## 触发命令
`/app api [endpoint]` — 如 `/app api skin-records`, `/app api auth/login`, `/app api products`

## 执行流程

### 第1步：上下文加载
1. 加载记忆文件：
   - `app-progress.md` — 已有API清单
   - `app-decisions.md` — 相关决策
2. 加载知识：
   - `knowledge/api-design.md` — API规范
   - `knowledge/spring-boot-patterns.md` — 后端模式
   - `knowledge/code-style.md` — 命名规范
3. 加载模板：
   - `templates/api-doc.md` — API文档模板

### 第2步：API设计
确认以下信息：
```
资源名称: [复数名词]
基础路径: /api/v1/[resource]
CRUD操作: [需要哪些 — GET列表/GET详情/POST/PUT/PATCH/DELETE]
认证: [需要JWT认证？]
特殊操作: [非CRUD操作，如 POST /analyze]
分页: [列表是否需要分页？]
```

### 第3步：创建文件

**1. Controller**
```
server/src/main/kotlin/{package}/controller/{Feature}Controller.kt
```
- `@RestController` + `@RequestMapping`
- 注入Service
- 参数验证 `@Valid`
- 返回 `ApiResponse<T>`

**2. DTO（Request + Response）**
```
server/src/main/kotlin/{package}/dto/request/{Action}{Feature}Request.kt
server/src/main/kotlin/{package}/dto/response/{Feature}Response.kt
```
- Request: 验证注解（@NotBlank, @Size等）
- Response: 不暴露内部字段

**3. Service（如果还没有）**
```
server/src/main/kotlin/{package}/service/{Feature}Service.kt
```
- 业务逻辑
- `@Transactional` 注解
- 调用Repository

**4. Entity（如果还没有）**
```
server/src/main/kotlin/{package}/entity/{Feature}.kt
```
- JPA Entity（非data class）
- 扩展函数 `.toResponse()`

**5. Repository（如果还没有）**
```
server/src/main/kotlin/{package}/repository/{Feature}Repository.kt
```
- 继承 `JpaRepository`
- 自定义查询方法

### 第4步：客户端API Service（KMP shared）
同步创建Ktor Client端：
```
shared/src/commonMain/kotlin/{package}/data/api/{Feature}ApiService.kt
```
```kotlin
class SkinRecordApiService(private val client: HttpClient) {
    suspend fun getRecords(page: Int, size: Int): ApiResponse<PagedResult<SkinRecordResponse>> {
        return client.get("/api/v1/skin-records") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }
    // ...
}
```

### 第5步：错误码注册
在 `ErrorCode` 枚举中添加新的错误码：
```kotlin
// 新模块错误码
FEATURE_NOT_FOUND("FEATURE_001", "xxx不存在", 404),
```

### 第6步：更新记忆
更新 `app-progress.md`：
- API完成度表格：添加新API
- 里程碑进度：更新百分比

---

## API质量门控
- [ ] URL遵循RESTful规范（名词复数、kebab-case）
- [ ] 统一使用 `ApiResponse<T>` 包装
- [ ] 请求DTO有验证注解
- [ ] 响应DTO不暴露Entity内部字段
- [ ] Service层有 `@Transactional`
- [ ] 错误码已注册到 `ErrorCode`
- [ ] 认证端点需JWT（公开端点除外）
- [ ] 列表接口支持分页
- [ ] 客户端API Service已同步创建

---

## 输出格式

```
🔌 新API: /api/v1/{resource}

━━━ 端点清单 ━━━
| Method | Endpoint | 说明 |
|--------|----------|------|

━━━ 创建的文件 ━━━
后端:
1. Controller
2. DTO (Request + Response)
3. Service (如新建)
4. Entity (如新建)
5. Repository (如新建)

客户端:
6. ApiService (Ktor Client)

━━━ 错误码 ━━━
[新增的错误码]

━━━ 进度更新 ━━━
[app-progress.md 已更新]

下一步建议：
→ `/app page [name]` 创建前端页面消费此API
→ `/app test [endpoint]` 为API写测试
```

---

## 注意事项
- API设计先于实现，先确认端点设计再写代码
- 每个API都要同步创建客户端Ktor Service
- 不要遗漏错误处理和边界情况
- 敏感数据（密码等）不要出现在Response中
