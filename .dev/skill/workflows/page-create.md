<!-- Last updated: 2026-03-10 -->
# 工作流：创建CMP页面

## 触发命令
`/app page [name]` — 如 `/app page login`, `/app page skin-record-list`, `/app page record-detail`

## 执行流程

### 第1步：上下文加载
1. 加载记忆文件：
   - `app-progress.md` — 已有页面清单，避免重复
   - `app-decisions.md` — UI相关决策
2. 加载知识：
   - `knowledge/compose-multiplatform.md` — CMP模式
   - `knowledge/code-style.md` — 命名规范
3. 加载模板：
   - `templates/page-scaffold.md` — 页面脚手架

### 第2步：页面设计
确认以下信息：
```
页面名称: [PascalCase]Screen
所属模块: [feature模块]
功能描述: [一句话]
数据来源: [哪个API / 本地数据]
导航关系: [从哪来，到哪去]
需要的组件: [列表]
```

### 第3步：文件创建
按照CMP标准创建以下文件：

**1. UiState**
```
composeApp/src/commonMain/kotlin/{package}/ui/screen/{feature}/{Feature}UiState.kt
```
- data class 包含页面所有状态
- sealed interface Action 包含所有用户操作

**2. ViewModel**
```
composeApp/src/commonMain/kotlin/{package}/viewmodel/{Feature}ViewModel.kt
```
- 注入 UseCase（通过 Koin）
- StateFlow<UiState>
- onAction 分发函数
- 初始化加载逻辑

**3. Screen**
```
composeApp/src/commonMain/kotlin/{package}/ui/screen/{feature}/{Feature}Screen.kt
```
- Screen Composable（持有ViewModel）
- Content Composable（纯UI，可Preview）
- 处理 Loading/Error/Success 三态

**4. 页面专属组件（如需要）**
```
composeApp/src/commonMain/kotlin/{package}/ui/screen/{feature}/component/
```

### 第4步：导航接入
1. 在导航图中注册新页面
2. 添加导航入口（按钮/Tab/菜单项）
3. 处理导航参数传递

### 第5步：Koin注册
```kotlin
// 在DI模块中注册ViewModel
val featureModule = module {
    factoryOf(::FeatureViewModel)
}
```

### 第6步：更新记忆
更新 `app-progress.md`：
- 页面完成度表格：添加/更新该页面状态
- 里程碑进度：更新百分比
- 当前状态：更新当前工作描述

---

## 页面质量门控
- [ ] 遵循 UiState + Action 模式
- [ ] Screen 和 Content 分离
- [ ] 处理 Loading/Error/Empty 三态
- [ ] Modifier 从外部传入
- [ ] 导航回调从参数传入（不在Composable内部处理）
- [ ] 无硬编码字符串（用资源或常量）
- [ ] Koin注入正确注册

---

## 输出格式

```
📱 新页面: {Feature}Screen

━━━ 创建的文件 ━━━
1. {path}/UiState.kt
2. {path}/ViewModel.kt
3. {path}/Screen.kt
4. {path}/component/... (如有)

━━━ 导航 ━━━
[导航入口 + 参数]

━━━ DI注册 ━━━
[Koin模块更新]

━━━ 进度更新 ━━━
[app-progress.md 已更新]

下一步建议：
→ `/app page [next-page]` 创建下一页
→ `/app test [page]` 为页面写测试
→ `/app component [name]` 抽取可复用组件
```

---

## 注意事项
- 一次只创建一个页面
- 如果依赖的API还未创建，先写假数据（TODO标注）
- 优先实现核心功能，样式后续优化
- 参考已有页面保持风格一致
