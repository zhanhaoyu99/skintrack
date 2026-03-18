package com.skintrack.app.snapshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.component.LockedFeatureCard
import com.skintrack.app.ui.component.MenuItem
import com.skintrack.app.ui.component.ScoreBar
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.TrendIndicator
import com.skintrack.app.ui.theme.extendedColors
import org.junit.Test

class ComponentSnapshotTest : SnapshotTestBase() {

    @Test
    fun scoreBar_light() = captureLight {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScoreBar(label = "痘痘", score = 78, color = MaterialTheme.extendedColors.skinMetric.acne)
            ScoreBar(label = "毛孔", score = 62, color = MaterialTheme.extendedColors.skinMetric.pore)
            ScoreBar(label = "泛红", score = 85, color = MaterialTheme.extendedColors.skinMetric.redness)
            ScoreBar(label = "均匀度", score = 70, color = MaterialTheme.extendedColors.skinMetric.evenness)
        }
    }

    @Test
    fun scoreBar_dark() = captureDark {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScoreBar(label = "痘痘", score = 78, color = MaterialTheme.extendedColors.skinMetric.acne)
            ScoreBar(label = "毛孔", score = 62, color = MaterialTheme.extendedColors.skinMetric.pore)
            ScoreBar(label = "泛红", score = 85, color = MaterialTheme.extendedColors.skinMetric.redness)
            ScoreBar(label = "均匀度", score = 70, color = MaterialTheme.extendedColors.skinMetric.evenness)
        }
    }

    @Test
    fun trendIndicator_light() = captureLight {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TrendIndicator(value = 5)
            TrendIndicator(value = -3)
            TrendIndicator(value = 0)
        }
    }

    @Test
    fun lockedFeatureCard_light() = captureLight {
        LockedFeatureCard(
            message = "升级会员解锁 AI 详细分析报告",
            onUpgrade = {},
            modifier = Modifier.padding(16.dp),
            title = "解锁完整分析报告",
            subtitle = "AI 智能分析 · 多维雷达图 · 个性化建议",
            tags = listOf("AI 深度分析", "雷达图", "趋势预测"),
        )
    }

    @Test
    fun sectionCard_light() = captureLight {
        SectionCard(modifier = Modifier.padding(16.dp)) {
            Text("标题", style = MaterialTheme.typography.titleMedium)
            Text("这是一段内容描述", style = MaterialTheme.typography.bodyMedium)
        }
    }

    @Test
    fun menuItem_light() = captureLight {
        Column(modifier = Modifier.fillMaxWidth()) {
            MenuItem(title = "会员中心", onClick = {})
            MenuItem(title = "打卡提醒", onClick = {}, trailing = { Text("已开启") })
            MenuItem(title = "退出登录", onClick = {}, textColor = MaterialTheme.colorScheme.error, showArrow = false)
        }
    }
}
