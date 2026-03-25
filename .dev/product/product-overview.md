# SkinTrack 产品概览

> 最后更新：2026-03-22
> 设计系统版本：v6 "Morning Garden"

## 一句话定位

**每天拍一张脸，AI 追踪皮肤变化，告诉你护肤品到底有没有用。**

## 核心价值主张

- 不再只能"感觉"护肤品有没有效，而是用**数据看得见**
- 长期追踪 + 护肤品归因分析（竞品仅做单次检测）
- 科学决策，避免在无效产品上浪费钱

## 目标用户

### 核心用户：「成分党」Lisa
- **年龄**：22-32 岁女性
- **特征**：有护肤习惯，月护肤品消费 ¥300-800，iPhone 用户，关注小红书/抖音美妆内容
- **痛点**：买了很多护肤品但不确定效果，想用数据说话
- **动机**：希望找到真正适合自己的护肤方案

### 次要用户：「护肤小白」Mia
- **年龄**：18-24 岁
- **特征**：想建立科学护肤习惯，预算有限
- **动机**：想知道从哪里开始，避免踩坑

## 设计调性

**Calm Tech + Warm Data** — 像 Linear 的克制遇上 Headspace 的温暖

- **色彩**：薄荷绿 Sage Mint（科学/信任）+ 杏橘 Warm Apricot（温暖/活力）+ 玫瑰 Petal Rose（女性化）+ 薰衣草 Soft Lavender（疗愈）
- **语言**：鼓励式表达（"皮肤状态不错哦~" "继续保持~"），非冰冷医疗报告
- **视觉**：大圆角（`shapes.extraLarge` 24dp）、充足留白（`spacing.xxl` 24dp）、渐变色彩（`gradients.hero/warm/roseWarm`）、微质感动画
- **字体**：SF Pro Display 系统字体，数字使用加粗 weight 增强数据感（`num-xl` 38sp/800w）
- **暖灰底色**：中性色带绿色调（`neutral-50` #F8FAF9），远离冷灰

## 皮肤评分系统

### 五大维度（0-100 分）
| 维度 | 英文 | 色值 token | 评估内容 |
|------|------|-----------|---------|
| 痘痘 | Acne | `extendedColors.skinMetric.acne` (#F27A8E) | 痘痘数量与严重程度 |
| 毛孔 | Pore | `extendedColors.skinMetric.pore` (#4ECDC4) | 毛孔大小与可见度 |
| 均匀度 | Evenness | `extendedColors.skinMetric.evenness` (#F5C542) | 肤色均匀程度 |
| 泛红 | Redness | `extendedColors.skinMetric.redness` (#E87878) | 皮肤泛红程度 |
| 水润 | Hydration | `extendedColors.skinMetric.hydration` (#45B7D1) | 皮肤水润程度 |

### 综合评分（加权平均）
| 等级 | 分数范围 | 显示文案 | 色调 |
|------|---------|---------|------|
| EXCELLENT | 85-100 | "皮肤状态超棒!" | `content-success` |
| GOOD | 70-84 | "皮肤状态不错哦~" | `content-brand` |
| MODERATE | 55-69 | "还有提升空间" | `content-warning` |
| CONCERN | 0-54 | "需要多关注下哦" | `content-error` |

## 产品类目颜色
| 品类 | 中文 | Token | 色值 |
|------|------|-------|------|
| Cleanser | 洁面 | `--cat-clean` | #3B82F6 (Info Blue) |
| Serum | 精华 | `--cat-serum` | #9B8ADB (Lavender) |
| Moisturizer | 面霜 | `--cat-cream` | #2A9D7C (Primary) |
| Sunscreen | 防晒 | `--cat-sun` | #E8925B (Secondary) |
| Toner | 化妆水 | `--cat-toner` | #00838F (Teal) |
| Mask | 面膜 | `--cat-mask` | #9B8ADB (Lavender) |

## 核心用户旅程

### 新用户首次使用
```
Onboarding(4页引导+肤质选择) → Auth(邮箱注册) → Dashboard(空态3步引导)
→ Camera(拍首张自拍) → AI 分析 → RecordDetail(查看分析结果)
```

### 日常记录
```
Dashboard(查看评分+提醒) → Camera(拍照) → AI 分析 → RecordDetail
→ ProductScreen(记录今日护肤品) → Timeline(查看趋势)
```

### 深度使用
```
Dashboard → Attribution(归因分析报告) → 了解产品效果排名
→ Share(生成分享对比卡) → 系统 ShareSheet
```

### 付费转化
```
功能锁定触发(拍照≤3次/AI详情/归因报告/分享卡) → Paywall
→ 14天免费试用 → 订阅 → 解锁全部功能
```

## 商业模式

### 订阅方案 [V1]
| 方案 | 价格 | 备注 |
|------|------|------|
| 月度 | ¥19.9/月 | 标准方案 |
| 年度 | ¥168/年 | 推荐，省 ¥70.8 |

### 免费 vs PRO
| 功能 | 免费版 | PRO 版 |
|------|--------|--------|
| 肌肤拍照分析 | ≤3 次 | 无限次 |
| 基础评分（5 维度数值） | ✅ | ✅ |
| AI 深度分析文本 | ❌ | ✅ |
| 雷达图多维分析 | ❌ | ✅ |
| 归因分析报告 | ❌ | ✅ |
| 精美分享卡片 | ❌ | ✅ |
| 云端备份 | ❌ | ✅ |

### 付费转化关键
- 前 14 天完全免费（新用户试用期）
- 功能门控 4 处入口：Camera(拍照限额) / RecordDetail(AI 深度分析) / Attribution(AI 归因) / Share(分享卡)
- 盈亏平衡：~150 个付费会员
- 单用户月成本：~1-3 元（AI API + 存储），毛利率 >85%

## V1 vs V2 功能边界

| V1（首发版） | V2（后续迭代） |
|------------|-------------|
| 邮箱注册/登录 | 微信/Apple/手机号登录 |
| 本地贴士轮换 | 天气 API 动态推荐 |
| 系统 ShareSheet 分享 | 微信/微博 SDK 直接分享 |
| 默认分享模板（1 种） | 3 种分享模板可选 |
| 首字母头像 | 头像上传 |
| 本地通知打卡提醒 | FCM 服务端推送 |
| Mock 支付流程 | 微信支付 + StoreKit 2 |
| 手动输入/搜索产品 | 扫码添加产品 |
| AI 分析（LLM API） | 自训练皮肤分析模型 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 客户端框架 | Compose Multiplatform (KMP) |
| 平台 | Android 首发 + iOS 跟进 |
| 后端 | Ktor Server + Exposed + PostgreSQL（国内云部署） |
| 本地数据库 | Room KMP |
| KV 存储 | DataStore KMP |
| 状态管理 | AndroidX ViewModel KMP |
| DI | Koin |
| 导航 | Voyager |
| 图片加载 | Coil 3 KMP |
| AI 分析 | 多模态 LLM API (GPT-4o/Gemini/Claude) via 服务端代理 |
| 图表 | Compose Canvas 自绘（TrendChart / RadarChart / ScoreRing / CompareCard） |

## 增长策略

- **核心渠道**：小红书前后对比图种草（天然传播内容）
- **裂变机制**：分享对比图自带 App 链接、邀请奖励、闺蜜护肤 PK
- **内容营销**：护肤品实测数据评测、换季护肤指南
