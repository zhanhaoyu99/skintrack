<!-- Last updated: 2026-03-10 -->
# API 设计规范知识库

## RESTful 规范

### URL设计
```
基础路径: /api/v1

资源命名（名词复数）:
GET    /api/v1/skin-records          # 列表（分页）
POST   /api/v1/skin-records          # 创建
GET    /api/v1/skin-records/{id}     # 详情
PUT    /api/v1/skin-records/{id}     # 全量更新
PATCH  /api/v1/skin-records/{id}     # 部分更新
DELETE /api/v1/skin-records/{id}     # 删除

嵌套资源:
GET    /api/v1/skin-records/{id}/issues     # 记录下的问题
POST   /api/v1/skin-records/{id}/issues     # 在记录下创建问题

动作（非CRUD操作）:
POST   /api/v1/skin-records/{id}/analyze    # 触发AI分析
POST   /api/v1/auth/login                   # 登录
POST   /api/v1/auth/register                # 注册
POST   /api/v1/auth/refresh                 # 刷新Token
```

### 命名规范
- URL: `kebab-case`（小写+连字符）
- JSON字段: `camelCase`
- 查询参数: `camelCase`

## 统一响应格式

```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorInfo? = null,
    val timestamp: String = Instant.now().toString()
) {
    companion object {
        fun <T> success(data: T? = null) = ApiResponse(success = true, data = data)
        fun error(code: String, message: String) = ApiResponse<Nothing>(
            success = false,
            error = ErrorInfo(code, message)
        )
    }
}

data class ErrorInfo(
    val code: String,
    val message: String
)
```

### 成功响应示例
```json
{
  "success": true,
  "data": {
    "id": 1,
    "skinType": "OILY",
    "notes": "T区出油明显",
    "createdAt": "2026-03-10T10:00:00Z"
  },
  "timestamp": "2026-03-10T10:00:00Z"
}
```

### 错误响应示例
```json
{
  "success": false,
  "error": {
    "code": "RECORD_001",
    "message": "记录不存在"
  },
  "timestamp": "2026-03-10T10:00:00Z"
}
```

## 分页

### 请求参数
```
GET /api/v1/skin-records?page=0&size=20&sort=createdAt,desc
```

### 分页响应
```kotlin
data class PagedResult<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean
) {
    companion object {
        fun <T, R> from(page: Page<T>, mapper: (T) -> R) = PagedResult(
            content = page.content.map(mapper),
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }
}
```

```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "hasNext": true
  }
}
```

## DTO 设计

### 请求DTO（创建）
```kotlin
data class CreateSkinRecordRequest(
    @field:NotBlank(message = "皮肤类型不能为空")
    val skinType: String,

    @field:Size(max = 1000, message = "备注不超过1000字")
    val notes: String? = null,

    val imageUrl: String? = null,

    val issues: List<String>? = null
)
```

### 请求DTO（更新）
```kotlin
data class UpdateSkinRecordRequest(
    val skinType: String? = null,
    val notes: String? = null,
    val imageUrl: String? = null
)
```

### 响应DTO
```kotlin
data class SkinRecordResponse(
    val id: Long,
    val skinType: String,
    val notes: String?,
    val imageUrl: String?,
    val issues: List<SkinIssueResponse>?,
    val createdAt: String,
    val updatedAt: String
)
```

### DTO规范
- 请求DTO：创建和更新分开（Create vs Update）
- 响应DTO：不暴露内部字段（如user引用）
- 日期统一用ISO-8601字符串（前端易解析）
- 嵌套对象也用DTO，不暴露Entity

## 错误码设计

### 分模块编码
```
AUTH_001 ~ AUTH_099     认证相关
USER_001 ~ USER_099     用户相关
RECORD_001 ~ RECORD_099 皮肤记录相关
PRODUCT_001 ~ PRODUCT_099 护肤品相关
ANALYSIS_001 ~ ANALYSIS_099 AI分析相关
SYSTEM_001 ~ SYSTEM_099  系统错误
```

### HTTP状态码使用
| 状态码 | 含义 | 场景 |
|--------|------|------|
| 200 | OK | 成功（GET/PUT/PATCH/DELETE） |
| 201 | Created | 创建成功（POST） |
| 400 | Bad Request | 参数验证失败 |
| 401 | Unauthorized | 未登录/Token过期 |
| 403 | Forbidden | 无权限 |
| 404 | Not Found | 资源不存在 |
| 409 | Conflict | 资源冲突（如邮箱重复） |
| 500 | Internal Server Error | 服务器错误 |

## 认证API设计

```
POST /api/v1/auth/register
Request: { "email", "password", "nickname" }
Response: { "token", "refreshToken", "user": {...} }

POST /api/v1/auth/login
Request: { "email", "password" }
Response: { "token", "refreshToken", "user": {...} }

POST /api/v1/auth/refresh
Request: { "refreshToken" }
Response: { "token", "refreshToken" }

GET /api/v1/auth/me
Header: Authorization: Bearer {token}
Response: { "user": {...} }
```

## API版本管理
- URL路径版本化：`/api/v1/`, `/api/v2/`
- MVP阶段只维护 v1
- 破坏性变更才升级版本号

## 文件上传
```
POST /api/v1/upload/image
Content-Type: multipart/form-data
Response: { "url": "https://...", "key": "..." }
```
- 图片上传单独接口，返回URL
- 创建记录时引用URL
- 限制文件大小（如10MB）
- 限制文件类型（JPEG/PNG/HEIC）
