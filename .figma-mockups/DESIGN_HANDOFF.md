# SkinTrack 设计稿交接文档

## 项目概况

### Figma 文件
- **个人账号文件**: https://www.figma.com/design/Fs4LqbOvElAVtlC1EoJD5b
- **文件所有者**: zhanhaoyu99@gmail.com（个人账号）
- **MCP 权限**: 通过公司账号 zane@xmind.org 的 can edit 权限写入
- **注意**: MCP Figma 工具绑定的是公司 Figma 账号，需要个人账号 share 给公司账号才能写入

### 本地文件
- **HTML 模拟稿目录**: `/Users/zane/Projects/skintrack/.figma-mockups/`
- **本地预览服务器**: `cd .figma-mockups && python3 -m http.server 8767 --bind 127.0.0.1`
- **预览地址**: `http://127.0.0.1:8767/`

## 设计文件清单（20 个文件，约 50+ 屏幕状态）

### Light Mode（15 文件）
| # | 文件名 | 屏幕数 | 内容 |
|---|--------|--------|------|
| 01 | 01-onboarding.html | 4 | 引导页（记录/护肤品/AI分析/肤质选择），含浮动数据徽章插画 |
| 02 | 02-auth.html | 3 | 登录 + 注册 + 找回密码 |
| 03 | 03-dashboard.html | 2 | 首页仪表盘（内容态 + 空状态），布局已收紧 |
| 04 | 04-timeline.html | 2 | 肌肤记录列表（内容态 + 空状态含 3 步引导） |
| 05 | 05-camera.html | 3 | 拍照（取景器含人脸引导 + ScoreRing 结果 + 权限请求） |
| 06 | 06-record-detail.html | 1 | 记录详情（雷达图 + AI 高亮框） |
| 07 | 07-profile.html | 1 | 个人中心（渐变头部 + VIP 徽章 + 毛玻璃统计） |
| 08 | 08-product.html | 2 | 护肤品列表 + 添加产品 Bottom Sheet |
| 09 | 09-attribution.html | 1 | 归因分析报告（含前后对比分数） |
| 10 | 10-paywall.html | 1 | 会员订阅（含社会证明 + 信任标识） |
| 11 | 11-settings.html | 2 | 设置 + 删除账号弹窗 |
| 12 | 12-share.html | 1 | 分享对比卡片（含模板选择器） |
| 18 | 18-loading-states.html | 3 | 加载骨架屏（Dashboard / Detail / Timeline） |
| 19 | 19-toast-states.html | 3 | 通知状态（成功/警告/错误 Toast + 空状态） |
| 20 | 20-edit-profile.html | 2 | 编辑资料 + 修改密码（含密码强度指示器） |

### VIP 锁定态（1 文件）
| 13 | 13-locked-states.html | 4 | 详情锁定/拍照限额/归因锁定/分享锁定 |

### Dark Mode（4 文件）
| 14 | 14-dark-core.html | 3 | 暗色 Dashboard（已同步新结构）+ Timeline + Profile |
| 15 | 15-dark-screens.html | 4 | 暗色 Auth + RecordDetail + Product + Attribution（更新中） |
| 16 | 16-dark-utility.html | 4 | 暗色 Onboarding + Paywall + Settings + Share（更新中） |
| 17 | 17-dark-locked.html | 2 | 暗色锁定态（详情+拍照）（更新中） |

## 设计体系

### 色彩
- **Primary**: Mint #2D9F7F（科学/信任）
- **Secondary**: Apricot #F4A261（温暖/活力）
- **Rose**: #FB7185（女性化/温暖）— 用于提醒卡、打卡、产品高亮
- **Lavender**: #A78BFA（治愈/高级）— 用于归因分析、护肤贴士
- **暗色模式 Primary**: #58CAA5（提亮版 mint）

### 用户画像
- **核心用户**: 22-32岁女性，护肤成分党
- **设计调性**: 温暖 · 治愈 · 清新 · 有温度的数据
- **语气**: 鼓励式（"皮肤状态不错哦~"）而非冰冷报告
- **详细画像**: 见 `USER_PERSONA.md`

### CSS 架构
- `base.css` — Light 主题 + 全局组件样式 + 色彩变量
- `dark.css` — Dark 主题覆盖（通过 `.dark` class 激活）
- 每个 HTML 内有页面特定样式（`<style>` 标签内）

### 关键 CSS 变量
```css
--primary, --on-primary, --primary-container
--rose-50~500, --lavender-50~400  /* 女性化配色 */
--surface, --surface-variant, --on-surface
--r-sm/md/lg/xl/full  /* 圆角 */
--shadow-xs/sm/md/lg/xl  /* 投影层次 */
```

## HTML → Figma 捕获流程

### 前置条件
1. HTML 文件中需包含: `<script src="https://mcp.figma.com/mcp/html-to-design/capture.js" async></script>`
2. 本地 HTTP 服务器运行在 `127.0.0.1:8767`
3. Figma MCP 认证为公司账号（whoami → zane@xmind.org）

### 捕获单页流程
```
1. 调用 mcp__figma__generate_figma_design(outputMode="existingFile", fileKey="...")
   → 获得 captureId

2. 用 Bash 打开浏览器:
   open "http://127.0.0.1:8767/{file}.html#figmacapture={captureId}&figmaendpoint=https%3A%2F%2Fmcp.figma.com%2Fmcp%2Fcapture%2F{captureId}%2Fsubmit&figmadelay=3000"

3. 等待 10 秒

4. 轮询: mcp__figma__generate_figma_design(captureId="{captureId}")
   - pending → 等 8 秒重试
   - processing → 等 5 秒重试
   - completed → 成功
```

### 关键经验
- **每次只开 1 个浏览器标签**，否则后台标签页会被冻结导致 pending
- **figmadelay=3000** 给页面足够渲染时间
- **渐变背景上的白色文字会丢失**，所以按钮用纯色 `var(--primary)` 而非 gradient
- 捕获后所有屏幕在同一个 Figma Page 下作为独立 Frame
- Figma REST API **不支持**删除/重命名/排序 Frame，需手动操作

### 批量捕获
- 用后台 Agent 自动化，逐页串行执行
- 18 页约需 5-10 分钟

## 已知限制

1. **按钮文字**: 渐变背景按钮的白色文字在 HTML-to-Figma 捕获中可能丢失，已改用纯色背景
2. **Figma MCP 限额**: 免费 seat 有每日工具调用次数限制，可用 REST API + 个人 token 替代截图功能
3. **MCP 认证**: MCP 绑定 Figma 桌面端账号，切换桌面端不影响 MCP 认证
4. **照片占位**: 使用 skin-tone 渐变 + 半透明 SVG 人脸轮廓模拟，非真实照片
5. **暗色模式**: 通过 `.dark` CSS class + `dark.css` 覆盖实现，非独立设计系统

## v2 优化记录

以下是 v2 迭代中对设计稿的主要改进：

### 新增页面
- **02-auth**: 新增 Forgot Password 找回密码页面（第 3 屏）
- **04-timeline**: 新增空状态页面，含 3 步操作引导（拍照 → 记录 → 趋势）
- **08-product**: 新增添加产品 Bottom Sheet（第 2 屏）
- **18-loading-states**: 全新文件，包含 Dashboard / Detail / Timeline 三个骨架屏加载态

### 视觉优化
- **01-onboarding**: 引导页插画增加浮动数据徽章，强化产品价值感知
- **03-dashboard**: 布局收紧，信息密度优化
- **05-camera**: 取景器增加人脸引导线，结果页使用 ScoreRing 环形分数展示
- **06-record-detail**: 增加 AI 高亮框，突出 AI 分析洞察
- **07-profile**: 改用渐变头部 + VIP 徽章 + 毛玻璃统计卡片
- **09-attribution**: 增加前后对比分数展示
- **10-paywall**: 增加社会证明（用户数）和信任标识（隐私/取消保障）
- **11-settings**: 注销弹窗改为删除账号确认弹窗
- **12-share**: 增加模板选择器

### 暗色模式
- **14-dark-core**: Dashboard 已同步至新布局结构
- 15/16/17 暗色页面正在同步更新中

### v3 商业化品质提升
- **02-auth**: 注册页增加密码强度指示器（4段进度条）+ 密码要求清单
- **19-toast-states**: 全新文件，包含成功/信息/警告/错误四类通知状态
- **20-edit-profile**: 全新文件，编辑资料表单 + 修改密码（含密码强度+校验清单）
- **index.html**: 升级为专业设计系统展示页，含分类导航和统计数据

### v4 商业级品质全面提升（2026.03.18）

#### 设计系统（base.css / dark.css）
- 新增品牌三色温暖渐变 token: `--grad-rose-warm`, `--grad-lavender-soft`, `--grad-tri-warm`
- 新增卡片精致边框 token: `--card-border`, `--card-border-accent`
- `.btn-primary` 升级为微妙渐变按钮 + 半透明顶部高光边框，增加悬浮质感
- `.section-card` 统一添加精致 `border: var(--card-border)`
- `dark.css` 同步新增暗色版本 token 和按钮样式

#### 文案温度全面升级（22-32 岁女性护肤成分党调性）
- **01-onboarding**: 标题改为"记录你的美丽变化"/"打造你的专属方案"，副标题更温暖鼓励
- **02-auth**: 品牌标语改为"开启你的美丽旅程"，注册按钮改为"开始护肤之旅"，密码强度用品牌色
- **03-dashboard**: 提醒文案"每天一拍，见证蜕变的美好"，里程碑"你的坚持正在改变肌肤~"，空状态"只需一张素颜自拍，AI 帮你解读肌肤密码"
- **04-timeline**: 记录摘要全部改为温暖鼓励语气，空状态改为"你的第一次记录将从这里开始"
- **05-camera**: 结果页"今天的皮肤状态超棒!"，权限页"来记录你的美丽吧"
- **06-record-detail**: AI 分析"整体状态很不错呢~"，建议标签改为"贴心建议"
- **09-attribution**: AI 洞察文案全面重写，更具感染力
- **10-paywall**: "开启你的变美之旅"/"科学护肤，让每一天的努力都被看见"
- **12-share**: "14 天变美见证~"/"见证每一天的美好变化"
- **13-locked-states**: "解锁完整肌肤分析"/"AI 深度解读 · 多维雷达图 · 专属护肤建议"

#### 视觉精致度提升
- **03-dashboard**: Hero 卡片增加 inset 高光, Tip 卡片增加闪光 SVG, 趋势图卡片增加底部渐变
- **07-profile**: 统计卡片边框改用品牌色半透明, Logo 增加发光晕
- **08-product**: AM/PM 分区间距优化
- **09-attribution**: 摘要卡片添加品牌色边框
- **10-paywall**: 选中方案卡片渐变细化

#### Index 页面图标系统升级
- 全部 17 个 emoji 图标替换为 inline SVG（Feather 风格, 20x20）
- 每个 SVG 使用对应页面主题色，暗色模式卡片使用月亮图标

#### 暗色模式同步
- **14-dark-core**: 文案同步至 v4 版本（Dashboard + Timeline + Profile）
- **15/16/17**: 文案同步至 v4 版本
- `dark.css`: 新增暗色渐变 token + 按钮渐变样式

## 后续优化方向

- [x] 补充 Forgot Password 页面（已在 02-auth 中实现）
- [x] 增加 Loading 状态页面（18-loading-states）
- [x] 更多空状态变体（Timeline 空状态、Dashboard 空状态含 3 步引导）
- [x] 新增通知/Toast 状态页面（19-toast-states）
- [x] 新增编辑资料 + 修改密码页面（20-edit-profile，含密码强度指示器）
- [x] 注册页增加密码强度指示器和校验提示（02-auth 优化）
- [x] 索引页升级为专业设计系统展示页
- [x] 全部文案温度升级至商业级（v4）
- [x] index.html emoji 替换为 SVG 图标
- [x] base.css/dark.css 新增品牌渐变和卡片边框 token
- [x] 暗色模式 14-17 文案同步
- [ ] 暗色模式 15/16/17 布局同步至最新 Light 结构
- [ ] 添加真实照片或高质量占位图替换 SVG 轮廓
- [ ] Figma 内整理命名和分页结构
- [ ] 新增暗色模式 19/20 的 dark variant
