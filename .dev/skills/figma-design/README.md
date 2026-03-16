# Figma Design Skill — HTML-to-Figma 设计稿工作流

## 概述
通过编写高保真 HTML/CSS 模拟稿 → 本地预览 → 捕获到 Figma 的方式，实现 AI 驱动的 UI 设计稿生成。

## 工作流程

### 1. 准备阶段
```bash
# 创建模拟稿目录
mkdir -p .figma-mockups

# 创建 base.css（设计系统 token）
# 创建各页面 HTML（每个文件含 1-4 个 phone-frame）
# 每个 HTML 需包含 Figma 捕获脚本:
# <script src="https://mcp.figma.com/mcp/html-to-design/capture.js" async></script>

# 启动本地服务器（必须绑定 127.0.0.1 避免 proxy 问题）
cd .figma-mockups && python3 -m http.server 8767 --bind 127.0.0.1
```

### 2. HTML 模拟稿规范

#### Phone Frame 结构
```html
<div class="phone-frame">  <!-- 390x844, border-radius: 50px -->
  <div class="status-bar">...</div>  <!-- iOS 状态栏 -->
  <div class="top-app-bar">...</div>  <!-- 顶部导航 -->
  <div class="content">...</div>      <!-- 内容区 -->
  <div class="bottom-nav">...</div>   <!-- 底部导航 -->
</div>
```

#### iOS 状态栏 SVG 模板
```html
<!-- Light background -->
<svg viewBox="0 0 18 12" width="18"><path d="M1 8.5h2v3H1zM5 6h2v5.5H5zM9 3.5h2v8H9zM13 1h2v10.5h-2z" fill="#111827" stroke="none"/></svg>
<!-- + WiFi SVG + Battery SVG -->

<!-- Dark background -->
<!-- 同上但 fill="#E4E3E8" -->
```

#### 图标使用
- **不使用 emoji** — 全部用 inline SVG
- SVG 默认: `fill: none; stroke: currentColor; stroke-width: 2; stroke-linecap: round`
- 图标大小: 20-24px，通过 `width/height` 属性控制

#### 暗色模式
```html
<!-- Light -->
<div class="phone-frame">...</div>

<!-- Dark: 加 .dark class + 引入 dark.css -->
<link rel="stylesheet" href="dark.css">
<div class="phone-frame dark">...</div>
```

### 3. 捕获到 Figma

#### 首页创建新文件
```
1. mcp__figma__generate_figma_design(outputMode="newFile", fileName="...", planKey="team::1587727830005729646")
   → 返回 captureId
2. open "http://127.0.0.1:8767/{file}#figmacapture={id}&figmaendpoint=https%3A%2F%2Fmcp.figma.com%2Fmcp%2Fcapture%2F{id}%2Fsubmit&figmadelay=3000"
3. sleep 8
4. Poll: mcp__figma__generate_figma_design(captureId="{id}")
   → 返回 fileKey（如 IWwTvhVfvBDa1zV2ol1GN5）
```

#### 后续页面添加到已有文件（逐一串行）
```
每个页面重复:
1. mcp__figma__generate_figma_design(outputMode="existingFile", fileKey="{fileKey}")
   → 返回新 captureId
2. open "http://127.0.0.1:8767/{file}#figmacapture={newId}&figmaendpoint=...&figmadelay=3000"
3. sleep 8
4. Poll: mcp__figma__generate_figma_design(captureId="{newId}")
```

#### 批量捕获注意事项
- **必须串行处理**：每次只开 1 个标签页，等完成后再开下一个
- 并行打开多个标签页会导致后台标签被浏览器冻结（pending 永不完成）
- 每页约 12-15 秒完成（open + 8s delay + poll）
- figmadelay=3000 给予页面足够加载时间

### 4. Figma 文件信息

| 项目 | fileKey |
|------|---------|
| SkinTrack App Design | IWwTvhVfvBDa1zV2ol1GN5 |

Figma 账户: zane@xmind.org (Zane Zhan's team, View seat, starter tier)

### 5. 关键经验

| 经验 | 说明 |
|------|------|
| 按钮文字 | 渐变背景上的白字会在捕获中丢失，改用纯色背景 |
| 标签页冻结 | 同时打开多个标签页，先打开的会被浏览器冻结，导致 pending |
| MCP 认证 | 绑定 Figma 桌面端，跨账号需 share can edit 权限 |
| proxy 问题 | 用 `--bind 127.0.0.1` 启动服务器，避免 socks proxy 干扰 |
| REST API | 可用个人 token 替代 MCP 做截图：`/v1/images/{fileKey}?ids=...` |
| Figma 限制 | REST API 不支持删除/重命名/排序 Frame |
| curl 代理 | macOS 上 curl 默认走 ALL_PROXY，用 `--noproxy localhost` 绕过 |

## 设计质量检查清单

- [ ] 所有 emoji 替换为 SVG 图标
- [ ] iOS 状态栏使用真实 SVG（信号/WiFi/电池）
- [ ] 按钮文字可见（纯色背景 + 白色文字）
- [ ] 照片占位用 skin-tone 渐变 + SVG 面部轮廓
- [ ] 卡片有层次化投影（shadow-xs 到 shadow-xl）
- [ ] 用户画像驱动的文案语气
- [ ] Light + Dark 双版本同步
- [ ] 4 Tab + 中间 FAB 对称导航
