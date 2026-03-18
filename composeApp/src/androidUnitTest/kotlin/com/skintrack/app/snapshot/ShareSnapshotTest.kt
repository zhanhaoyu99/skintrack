package com.skintrack.app.snapshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkinType
import com.skintrack.app.ui.screen.share.ShareCardContent
import com.skintrack.app.ui.screen.share.ShareTargets
import com.skintrack.app.ui.screen.share.TemplateSelector
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Instant
import org.junit.Test

class ShareSnapshotTest : SnapshotTestBase() {

    @Test
    fun shareCard_light() = captureLight {
        ShareCardPreview()
    }

    @Test
    fun shareCard_dark() = captureDark {
        ShareCardPreview()
    }

    @Test
    fun shareControls_light() = captureLight {
        ShareControlsPreview()
    }
}

private val sampleBefore = SkinRecord(
    id = "before-1",
    userId = "user-1",
    skinType = SkinType.COMBINATION,
    overallScore = 72,
    recordedAt = Instant.parse("2026-03-01T08:00:00Z"),
    createdAt = Instant.parse("2026-03-01T08:00:00Z"),
)

private val sampleAfter = SkinRecord(
    id = "after-1",
    userId = "user-1",
    skinType = SkinType.COMBINATION,
    overallScore = 82,
    recordedAt = Instant.parse("2026-03-14T08:00:00Z"),
    createdAt = Instant.parse("2026-03-14T08:00:00Z"),
)

@Composable
private fun ShareCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        ShareCardContent(
            before = sampleBefore,
            after = sampleAfter,
        )
    }
}

@Composable
private fun ShareControlsPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        TemplateSelector()
        ShareTargets(onTargetClick = {})
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
            ) {
                Text("保存图片")
            }
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
            ) {
                Text("分享")
            }
        }
    }
}
