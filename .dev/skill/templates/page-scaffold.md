# CMP 页面脚手架模板

## 文件结构
```
composeApp/src/commonMain/kotlin/{package}/ui/screen/{feature}/
├── {Feature}Screen.kt        # Screen + Content Composable
├── {Feature}UiState.kt        # UiState + Action
└── component/                 # 页面专属组件（如需要）
    └── {Component}.kt

composeApp/src/commonMain/kotlin/{package}/viewmodel/
└── {Feature}ViewModel.kt     # ViewModel
```

## UiState 模板
```kotlin
data class {Feature}UiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    // 表单字段（如需要）
    // val fieldName: String = "",
)

sealed interface {Feature}Action {
    data object Refresh : {Feature}Action
    data object Retry : {Feature}Action
    // data class ItemClicked(val id: String) : {Feature}Action
    // data class FieldChanged(val value: String) : {Feature}Action
    // data object Submit : {Feature}Action
}
```

## ViewModel 模板
```kotlin
class {Feature}ViewModel(
    private val useCase: {UseCase}
) : ViewModel() {

    private val _uiState = MutableStateFlow({Feature}UiState())
    val uiState: StateFlow<{Feature}UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onAction(action: {Feature}Action) {
        when (action) {
            {Feature}Action.Refresh -> loadData()
            {Feature}Action.Retry -> loadData()
            // else -> { /* handle other actions */ }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            useCase()
                .onSuccess { data ->
                    _uiState.update { it.copy(items = data, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
}
```

## Screen 模板
```kotlin
@Composable
fun {Feature}Screen(
    viewModel: {Feature}ViewModel = koinInject(),
    onNavigateBack: () -> Unit = {},
    // onNavigateTo{Target}: (id: String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    {Feature}Content(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun {Feature}Content(
    uiState: {Feature}UiState,
    onAction: ({Feature}Action) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("{页面标题}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    ErrorContent(
                        message = uiState.error,
                        onRetry = { onAction({Feature}Action.Retry) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.items.isEmpty() -> {
                    EmptyContent(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    // 主要内容
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.items, key = { it.id }) { item ->
                            // ItemCard(item = item, onClick = { ... })
                        }
                    }
                }
            }
        }
    }
}
```

## Koin 注册
```kotlin
// di/FeatureModule.kt
val featureModule = module {
    factoryOf(::{Feature}ViewModel)
}
```

## 导航注册（Voyager）
```kotlin
class {Feature}VoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        {Feature}Screen(
            onNavigateBack = { navigator.pop() },
        )
    }
}
```
