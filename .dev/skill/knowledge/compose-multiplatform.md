<!-- Last updated: 2026-03-10 -->
# Compose Multiplatform (CMP) UI知识库

## CMP vs 原生Compose的差异

| 方面 | Compose (Android) | Compose Multiplatform |
|------|------|------|
| 平台 | 仅Android | Android + iOS + Desktop + Web |
| 资源管理 | Android Resources | compose.resources |
| 导航 | Navigation Compose | Voyager / Decompose / 自定义 |
| 图片加载 | Coil (Android) | Coil 3 Multiplatform |
| 权限 | Accompanist | expect/actual |
| 状态栏 | SystemUI | 平台适配 |
| ViewModel | AndroidX ViewModel | **AndroidX ViewModel KMP版** (已官方KMP化) |

## 页面(Screen)结构标准

```kotlin
// ui/screen/SkinRecordScreen.kt
@Composable
fun SkinRecordScreen(
    viewModel: SkinRecordViewModel = koinInject(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (recordId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    SkinRecordContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onNavigateToDetail = onNavigateToDetail
    )
}

@Composable
private fun SkinRecordContent(
    uiState: SkinRecordUiState,
    onAction: (SkinRecordAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    // 纯UI，无副作用，易于Preview
    Scaffold(
        topBar = { /* ... */ }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingContent()
            uiState.error != null -> ErrorContent(uiState.error, onRetry = { onAction(SkinRecordAction.Retry) })
            else -> SuccessContent(uiState.records, onNavigateToDetail)
        }
    }
}
```

## 状态管理模式

### UiState + Action 模式
```kotlin
// 状态
data class SkinRecordUiState(
    val records: List<SkinRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 动作
sealed interface SkinRecordAction {
    data class DeleteRecord(val id: String) : SkinRecordAction
    data object Retry : SkinRecordAction
    data object Refresh : SkinRecordAction
}

// ViewModel
class SkinRecordViewModel(
    private val getRecords: GetSkinRecordsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SkinRecordUiState())
    val uiState: StateFlow<SkinRecordUiState> = _uiState.asStateFlow()

    init { loadRecords() }

    fun onAction(action: SkinRecordAction) {
        when (action) {
            is SkinRecordAction.DeleteRecord -> deleteRecord(action.id)
            SkinRecordAction.Retry, SkinRecordAction.Refresh -> loadRecords()
        }
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getRecords()
                .onSuccess { records -> _uiState.update { it.copy(records = records, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }
}
```

### ViewModel（AndroidX KMP版）
```kotlin
// 直接使用 AndroidX ViewModel KMP版，用法与Android一致
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class SkinRecordViewModel(
    private val getRecords: GetSkinRecordsUseCase
) : ViewModel() {
    // viewModelScope 已内置，无需自定义
    // 生命周期感知，自动清理
}

// Koin注册
val appModule = module {
    viewModelOf(::SkinRecordViewModel)
}

// Compose中使用
@Composable
fun SkinRecordScreen(
    viewModel: SkinRecordViewModel = koinViewModel()  // 使用koinViewModel()
) { ... }
```

## 导航方案

### 推荐：Voyager
```kotlin
// 定义Screen
class SkinRecordListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        SkinRecordScreen(
            onNavigateToDetail = { id -> navigator.push(SkinRecordDetailScreen(id)) },
            onNavigateBack = { navigator.pop() }
        )
    }
}

// App入口
@Composable
fun App() {
    MaterialTheme {
        Navigator(HomeScreen())
    }
}
```

### Tab导航
```kotlin
// 底部导航Tab
object HomeTab : Tab {
    override val options @Composable get() = TabOptions(
        index = 0u,
        title = "首页",
        icon = rememberVectorPainter(Icons.Default.Home)
    )
    @Composable override fun Content() { HomeScreen() }
}
```

## 主题系统（Material3）

```kotlin
// ui/theme/AppTheme.kt
@Composable
fun SkinCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme(
        primary = Color(0xFF8BC34A),      // 自然绿
        secondary = Color(0xFFFF9800),    // 活力橙
        background = Color(0xFF121212),
    ) else lightColorScheme(
        primary = Color(0xFF4CAF50),
        secondary = Color(0xFFFF9800),
        background = Color(0xFFF5F5F5),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
```

## 图片加载（Coil 3 Multiplatform）

```kotlin
// 加载网络图片
AsyncImage(
    model = imageUrl,
    contentDescription = "皮肤记录照片",
    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
    contentScale = ContentScale.Crop,
    placeholder = painterResource(Res.drawable.placeholder),
    error = painterResource(Res.drawable.error_image)
)
```

## 平台适配技巧

### iOS安全区域
```kotlin
// 处理iOS刘海屏/底部指示条
Scaffold(
    modifier = Modifier.windowInsetsPadding(WindowInsets.safeArea)
) { /* ... */ }
```

### 平台检测
```kotlin
// commonMain
expect val currentPlatform: Platform
enum class Platform { ANDROID, IOS }

// 按平台调整UI
if (currentPlatform == Platform.IOS) {
    // iOS特定UI调整
}
```

### 资源管理（compose.resources）
```kotlin
// 使用CMP资源系统
val text = stringResource(Res.string.app_name)
val image = painterResource(Res.drawable.logo)
```

## 常用组件模式

### 列表 + 下拉刷新
```kotlin
@Composable
fun RecordList(
    records: List<SkinRecord>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onItemClick: (String) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn {
            items(records, key = { it.id }) { record ->
                RecordItem(record = record, onClick = { onItemClick(record.id) })
            }
        }
    }
}
```

### 表单输入
```kotlin
@Composable
fun SkinCareFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage != null) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

## 常见陷阱
1. **AndroidX ViewModel KMP版已可用** — 直接使用 androidx.lifecycle:lifecycle-viewmodel
2. **不要使用 Android Resources (R.xxx)** — 用 compose.resources (Res.xxx)
3. **LazyColumn key 必须唯一** — 用实体ID
4. **Side effects 在 LaunchedEffect 中** — 不要在Composable直接调用suspend
5. **状态提升** — Screen拿ViewModel，Content是纯UI函数
6. **iOS手势冲突** — 注意与iOS原生手势（如返回滑动）的兼容
