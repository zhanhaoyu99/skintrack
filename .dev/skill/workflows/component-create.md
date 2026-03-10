<!-- Last updated: 2026-03-10 -->
# 工作流：创建可复用UI组件

## 触发命令
`/app component [name]` — 如 `/app component record-card`, `/app component skin-type-selector`, `/app component image-picker`

## 执行流程

### 第1步：上下文加载
1. 加载知识：
   - `knowledge/compose-multiplatform.md` — CMP组件模式
   - `knowledge/code-style.md` — 命名规范
2. 检查已有组件，避免重复

### 第2步：组件设计
```
组件名称: [PascalCase]
位置: composeApp/src/commonMain/kotlin/{package}/ui/component/
用途: [一句话描述]
使用场景: [哪些页面会用到]
参数:
  - 必要数据参数
  - 可选配置参数（有默认值）
  - modifier: Modifier = Modifier
  - 事件回调
```

### 第3步：创建组件文件
```
composeApp/src/commonMain/kotlin/{package}/ui/component/{ComponentName}.kt
```

遵循规范：
- Modifier 从外部传入，有默认值
- 参数顺序：数据 → 配置 → modifier → 回调
- 组件内部不持有状态（无状态组件）
- 可Preview

### 第4步：如果需要平台特定组件
- 使用 expect/actual
- 或在组件内部用 `currentPlatform` 条件分支

---

## 组件质量门控
- [ ] 无状态（状态由外部传入）
- [ ] Modifier参数有默认值
- [ ] 参数顺序正确
- [ ] 可在多个页面复用
- [ ] 命名清晰反映用途

---

## 输出格式

```
🧩 新组件: {ComponentName}

━━━ 创建的文件 ━━━
{file path}

━━━ 使用示例 ━━━
```kotlin
ComponentName(
    data = ...,
    modifier = Modifier.fillMaxWidth(),
    onClick = { /* ... */ }
)
```

━━━ 使用场景 ━━━
- [页面1]
- [页面2]
```
