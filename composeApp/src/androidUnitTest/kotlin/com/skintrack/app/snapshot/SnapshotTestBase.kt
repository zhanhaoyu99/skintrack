package com.skintrack.app.snapshot

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.skintrack.app.ui.theme.SkinTrackTheme
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [34],
    qualifiers = "w400dp-h800dp-xxhdpi",
    application = TestApplication::class,
)
abstract class SnapshotTestBase {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            outputDirectoryPath = "build/outputs/roborazzi",
        ),
    )

    fun captureLight(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            SkinTrackTheme(darkTheme = false) {
                content()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    fun captureDark(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            SkinTrackTheme(darkTheme = true) {
                content()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
