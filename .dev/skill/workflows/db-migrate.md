<!-- Last updated: 2026-03-10 -->
# 工作流：数据库变更

## 触发命令
`/app db [description]` — 如 `/app db add skin_records table`, `/app db add image_url to records`

## 执行流程

### 第1步：上下文加载
1. 加载知识：
   - `knowledge/database-patterns.md` — 数据库模式
   - `knowledge/spring-boot-patterns.md` — JPA Entity规范
2. 加载记忆：
   - `app-progress.md` — 已有表结构
   - `app-decisions.md` — 数据库相关决策

### 第2步：变更设计
确认以下信息：
```
变更类型: [新建表 / 修改表 / 添加索引 / 数据迁移]
表名: [snake_case]
字段列表:
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|

关联关系: [外键 / 索引]
```

### 第3步：创建/修改Entity
```
server/src/main/kotlin/{package}/entity/{Entity}.kt
```
- JPA Entity（非data class）
- 合适的注解（@Entity, @Table, @Column等）
- 关联关系（@ManyToOne, @OneToMany等）
- 审计字段（createdAt, updatedAt）

### 第4步：创建/修改Repository
```
server/src/main/kotlin/{package}/repository/{Entity}Repository.kt
```
- 继承 `JpaRepository`
- 添加业务需要的查询方法

### 第5步：迁移脚本
如果使用Flyway/Liquibase：
```
server/src/main/resources/db/migration/V{version}__{description}.sql
```

如果使用JPA自动DDL（开发阶段）：
- `application-dev.yml` 中 `spring.jpa.hibernate.ddl-auto: update`
- 记录DDL变更到 `app-decisions.md`

### 第6步：SQLDelight（如KMP本地需要）
```
shared/src/commonMain/sqldelight/{package}/{Entity}.sq
```
- 定义本地缓存表
- 查询语句

### 第7步：更新记忆
- `app-progress.md` — 记录数据库变更
- `app-decisions.md` — 如果是重要的数据模型决策

---

## 数据库设计规范
- [ ] 表名 `snake_case` 复数形式
- [ ] 主键统一使用 `id BIGSERIAL PRIMARY KEY`
- [ ] 包含 `created_at` 和 `updated_at` 审计字段
- [ ] 外键字段命名 `{table}_id`
- [ ] 索引覆盖常用查询条件
- [ ] 枚举使用 `VARCHAR` 存储（非整数）
- [ ] 文本长度限制合理（VARCHAR(255) vs TEXT）
- [ ] 敏感数据（密码）存储哈希值

---

## 输出格式

```
🗃️ 数据库变更: {description}

━━━ 变更内容 ━━━
[新建/修改的表 + 字段]

━━━ 创建的文件 ━━━
1. Entity
2. Repository
3. 迁移脚本（如有）
4. SQLDelight（如需要）

━━━ 关联关系 ━━━
[表之间的关系图]

下一步建议：
→ `/app service [name]` 创建Service操作此表
→ `/app api [endpoint]` 创建API
```
