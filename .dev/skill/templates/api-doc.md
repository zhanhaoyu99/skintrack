# API文档模板

## [资源名称] API

基础路径: `/api/v1/[resource]`
认证: JWT Bearer Token（除特殊标注外）

---

### 列表查询
```
GET /api/v1/[resource]?page=0&size=20&sort=createdAt,desc
```

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码（从0开始） |
| size | int | 否 | 20 | 每页数量 |
| sort | string | 否 | createdAt,desc | 排序 |

**响应:**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "hasNext": true
  }
}
```

---

### 详情查询
```
GET /api/v1/[resource]/{id}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    ...
  }
}
```

**错误:**
| 状态码 | 错误码 | 说明 |
|--------|--------|------|
| 404 | XXX_001 | 资源不存在 |

---

### 创建
```
POST /api/v1/[resource]
Content-Type: application/json
```

**请求体:**
```json
{
  "field1": "value1",
  "field2": "value2"
}
```

**字段说明:**
| 字段 | 类型 | 必填 | 约束 | 说明 |
|------|------|------|------|------|

**响应:** 201 Created
```json
{
  "success": true,
  "data": { ... }
}
```

---

### 更新
```
PUT /api/v1/[resource]/{id}
Content-Type: application/json
```

**请求体:**
```json
{
  "field1": "newValue"
}
```

**响应:** 200 OK

---

### 删除
```
DELETE /api/v1/[resource]/{id}
```

**响应:** 200 OK
```json
{
  "success": true,
  "data": null
}
```

---

## 错误码汇总
| 错误码 | HTTP状态 | 说明 |
|--------|----------|------|
