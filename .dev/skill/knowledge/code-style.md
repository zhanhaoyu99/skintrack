<!-- Last updated: 2026-03-10 -->
# Kotlin 代码规范知识库

## 命名规范

### 通用规则
| 类型 | 风格 | 示例 |
|------|------|------|
| 包名 | 小写.分隔 | `com.skincare.feature.record` |
| 类/接口 | PascalCase | `SkinRecordViewModel` |
| 函数/属性 | camelCase | `getSkinRecords()` |
| 常量 | SCREAMING_SNAKE | `MAX_IMAGE_SIZE` |
| 枚举值 | SCREAMING_SNAKE | `SKIN_TYPE_OILY` |
| 文件名 | PascalCase.kt | `SkinRecordScreen.kt` |
| 资源文件 | snake_case | `ic_camera.xml` |

### 项目特定命名
| 组件 | 命名模式 | 示例 |
|------|---------|------|
| Screen(Composable) | `{Feature}Screen` | `SkinRecordScreen` |
| ViewModel | `{Feature}ViewModel` | `SkinRecordViewModel` |
| UiState | `{Feature}UiState` | `SkinRecordUiState` |
| Action | `{Feature}Action` | `SkinRecordAction` |
| Controller | `{Feature}Controller` | `SkinRecordController` |
| Service | `{Feature}Service` | `SkinRecordService` |
| Repository | `{Feature}Repository` | `SkinRecordRepository` |
| Entity | 单数名词 | `SkinRecord` |
| DTO(Request) | `{Action}{Feature}Request` | `CreateSkinRecordRequest` |
| DTO(Response) | `{Feature}Response` | `SkinRecordResponse` |
| UseCase | `{动词}{Feature}UseCase` | `GetSkinRecordsUseCase` |
| Mapper扩展 | `{Entity}.to{Target}()` | `skinRecord.toResponse()` |

## Kotlin 惯用法

### 使用 data class 做DTO
```kotlin
// ✅ Good
data class SkinRecordResponse(
    val id: Long,
    val skinType: String,
    val notes: String?
)

// ❌ Bad - 不用 data class 做 JPA Entity
data class SkinRecord(...)  // JPA代理问题
```

### 使用 sealed interface 做状态/动作
```kotlin
// ✅ Good
sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: List<Item>) : UiState
    data class Error(val message: String) : UiState
}
```

### 使用扩展函数做转换
```kotlin
// ✅ Good
fun SkinRecord.toResponse() = SkinRecordResponse(
    id = id,
    skinType = skinType.displayName,
    notes = notes
)

// ❌ Bad - 在Service里手动映射每个字段
```

### 空安全
```kotlin
// ✅ Good - 用 ?: 和 let
val record = repository.findByIdOrNull(id) ?: throw NotFoundException()
user.email?.let { sendEmail(it) }

// ❌ Bad - 用 !! 强制解包
val record = repository.findById(id).get()!!
```

### 作用域函数
```kotlin
// apply - 配置对象
val record = SkinRecord().apply {
    skinType = request.skinType
    notes = request.notes
}

// let - 空安全转换
val response = record?.let { it.toResponse() }

// also - 附加操作（如日志）
repository.save(record).also { logger.info("Created record: ${it.id}") }

// run/with - 对象上执行多步操作
```

## 包结构

### 按功能划分（推荐）
```
com.skincare.feature.record/
├── SkinRecordScreen.kt
├── SkinRecordViewModel.kt
├── SkinRecordUiState.kt
└── component/
    └── RecordCard.kt
```

### 按层划分（后端）
```
com.skincare/
├── controller/SkinRecordController.kt
├── service/SkinRecordService.kt
├── repository/SkinRecordRepository.kt
├── entity/SkinRecord.kt
└── dto/SkinRecordDTO.kt
```

## Compose 代码规范

### Composable函数命名
```kotlin
// ✅ 顶层Screen: PascalCase + Screen后缀
@Composable fun SkinRecordScreen() {}

// ✅ 内部Content: PascalCase + Content后缀
@Composable private fun SkinRecordContent() {}

// ✅ 组件: PascalCase
@Composable fun RecordCard() {}
@Composable fun SkinTypeSelector() {}
```

### 参数顺序
```kotlin
@Composable
fun RecordCard(
    // 1. 必要数据
    record: SkinRecord,
    // 2. 可选数据
    showActions: Boolean = true,
    // 3. Modifier（总是有默认值）
    modifier: Modifier = Modifier,
    // 4. 回调（事件处理）
    onClick: () -> Unit,
    onDelete: () -> Unit
)
```

### Modifier使用
```kotlin
// ✅ Good - 外部传Modifier，内部链式调用
@Composable
fun RecordCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(8.dp)) {}
}

// ❌ Bad - 硬编码Modifier
@Composable
fun RecordCard() {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {}
}
```

## 注释规范
- 不写显而易见的注释
- 复杂业务逻辑写注释解释"为什么"
- API接口用 KDoc 注释
- TODO格式：`// TODO: [description] — [context]`

## 格式化
- 使用 ktlint 或 IDE 默认格式化
- 缩进：4空格
- 行宽：120字符
- 函数参数超过3个时换行

## 导入规范
- 不使用通配符导入（`import xxx.*`）
- IDE自动组织导入
- 删除未使用的导入
