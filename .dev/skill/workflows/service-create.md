<!-- Last updated: 2026-03-10 -->
# 工作流：创建后端Service

## 触发命令
`/app service [name]` — 如 `/app service skin-analysis`, `/app service image-upload`, `/app service notification`

## 执行流程

### 第1步：上下文加载
1. 加载知识：
   - `knowledge/spring-boot-patterns.md` — Service模式
   - `knowledge/code-style.md` — 命名规范
2. 加载记忆：
   - `app-progress.md` — 已有Service
   - `app-decisions.md` — 相关决策

### 第2步：Service设计
确认以下信息：
```
Service名称: {Feature}Service
职责: [一句话描述]
依赖: [需要注入的Repository/其他Service]
事务需求: [哪些方法需要 @Transactional]
对外接口: [public方法列表]
```

### 第3步：创建Service文件
```
server/src/main/kotlin/{package}/service/{Feature}Service.kt
```

标准结构：
```kotlin
@Service
class FeatureService(
    private val featureRepository: FeatureRepository,
    // 其他依赖注入
) {
    // 查询方法 — @Transactional(readOnly = true)
    @Transactional(readOnly = true)
    fun getById(id: Long, userId: Long): FeatureResponse { ... }

    // 写入方法 — @Transactional
    @Transactional
    fun create(userId: Long, request: CreateFeatureRequest): FeatureResponse { ... }

    @Transactional
    fun update(userId: Long, id: Long, request: UpdateFeatureRequest): FeatureResponse { ... }

    @Transactional
    fun delete(userId: Long, id: Long) { ... }

    // 私有辅助方法
    private fun findByIdAndUser(id: Long, userId: Long): Feature {
        return featureRepository.findByIdAndUserId(id, userId)
            ?: throw BusinessException(ErrorCode.FEATURE_NOT_FOUND)
    }
}
```

### 第4步：检查Repository是否存在
- 如果需要的Repository不存在，创建它
- 确认必要的查询方法已定义

### 第5步：更新Controller
- 确保Controller已注入此Service
- 确认API端点调用了正确的Service方法

---

## Service质量门控
- [ ] 单一职责（一个Service管一个领域）
- [ ] 查询方法用 `@Transactional(readOnly = true)`
- [ ] 写入方法用 `@Transactional`
- [ ] 权限检查（用户只能操作自己的数据）
- [ ] 业务异常使用 `BusinessException(ErrorCode.XXX)`
- [ ] Entity → DTO 转换使用扩展函数
- [ ] 不在Service中处理HTTP相关逻辑（那是Controller的事）

---

## 输出格式

```
⚙️ 新Service: {Feature}Service

━━━ 创建的文件 ━━━
1. {path}/service/{Feature}Service.kt

━━━ 公共方法 ━━━
- getById(id, userId): FeatureResponse
- create(userId, request): FeatureResponse
- update(userId, id, request): FeatureResponse
- delete(userId, id)

━━━ 依赖 ━━━
[注入的Repository/Service列表]

下一步建议：
→ `/app api [endpoint]` 为Service创建API端点
→ `/app test [service]` 为Service写单元测试
```
