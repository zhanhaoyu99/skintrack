<!-- Last updated: 2026-03-10 -->
# 皮肤管理App 问题追踪

## 🔴 BLOCKING（阻塞中）
<!-- 格式：
### #XXX [严重度🔴🟡🟢] 标题
- **日期**: YYYY-MM-DD
- **模块**: [Frontend/Backend/Shared/DevOps]
- **描述**:
- **已尝试**:
- **根因**:
- **解决方案**:
- **教训**:
-->

暂无

## 🟡 OPEN（待解决）

暂无

## 🟢 RESOLVED（已解决）

暂无

---

## 技术债务

| # | 描述 | 模块 | 优先级 | 登记日期 | 状态 |
|---|------|------|--------|---------|------|
| D1 | TrendChart Canvas 内 4 处小 dp 值 (6-8dp 轴标签定位) 未走 token | UI | 低 | 2026-03-12 | 可接受 |
| D2 | MenuItem/ScoreBar/ErrorContent 仅 1 处使用，复用度不足 2+ | UI | 低 | 2026-03-12 | 随功能增长自然解决 |
| D3 | gradients.scoreRing 未使用，预留给 RadarChart | UI | 低 | 2026-03-12 | 待 RadarChart 开发 |
| D4 | Supabase 项目未创建，需填入 credentials 激活后端 | Backend | 中 | 2026-03-10 | 代码就绪，待创建项目 |
| D5 | iOS expect/actual 全部为空占位 (Camera/ImageCompressor/ImageStorage) | Platform | 中 | 2026-03-10 | M3 iOS 适配时处理 |
| D6 | ~~AuthScreen 暗色模式背景未生效（缺 Surface 包裹）~~ | UI | 低 | 2026-03-12 | ✅ 已修复 |
| D7 | ~~离线同步策略未实现~~ | Backend | 中 | 2026-03-12 | ✅ SyncManager 已实现 |
| D8 | ShareCardViewModel/AttributionReportViewModel 仍用 hardcoded "local-user" | Backend | 低 | 2026-03-12 | 功能可用，待统一 |
