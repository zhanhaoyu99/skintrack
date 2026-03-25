package com.skintrack.app.ui.screen.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.auth.AuthScreen
import com.skintrack.app.ui.theme.Secondary50
import com.skintrack.app.ui.theme.Secondary300
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Lavender100
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Lavender400
import com.skintrack.app.ui.theme.Primary300
import com.skintrack.app.ui.theme.Primary400
import com.skintrack.app.ui.theme.Primary50
import com.skintrack.app.ui.theme.Primary600
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.Rose100
import com.skintrack.app.ui.theme.Rose300
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.MetricHydrationLight
import com.skintrack.app.ui.theme.MetricPoreLight
import com.skintrack.app.ui.theme.MetricRednessLight
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val bgGradientColors: List<Color>,
    val glowColor: Color,
    val accentColor: Color,
    val iconTint: Color,
)

@Composable
private fun carouselPages(): List<OnboardingPage> {
    val isDark = isSystemInDarkTheme()
    return listOf(
        OnboardingPage(
            icon = Icons.Default.Face,
            title = "记录你的\n肌肤变化",
            subtitle = "每天一张自拍，AI 帮你追踪痘痘、毛孔、肤色等指标，见证皮肤一天天变好",
            bgGradientColors = if (isDark) listOf(Primary300.copy(alpha = 0.15f), Primary400.copy(alpha = 0.1f))
            else listOf(Color(0xFFB8E8D9), Color(0xFFD1F0E4), Primary50, Rose50),
            glowColor = Primary400,
            accentColor = Rose300,
            iconTint = if (isDark) Primary300 else Primary600,
        ),
        OnboardingPage(
            icon = Icons.Default.Favorite,
            title = "管理你的\n护肤方案",
            subtitle = "记录每天用了哪些护肤品，让数据告诉你什么组合最适合你的肌肤",
            bgGradientColors = if (isDark) listOf(Rose300.copy(alpha = 0.15f), Rose400.copy(alpha = 0.1f))
            else listOf(Rose100, Rose50, Lavender50, Secondary50),
            glowColor = Rose300,
            accentColor = Primary300,
            iconTint = if (isDark) Rose300 else Rose400,
        ),
        OnboardingPage(
            icon = Icons.Default.Star,
            title = "AI 帮你找到\n最佳护肤方案",
            subtitle = "哪个精华让你皮肤变好了？AI 为你分析每个产品的真实效果，科学护肤不踩坑",
            bgGradientColors = if (isDark) listOf(Lavender300.copy(alpha = 0.15f), Lavender400.copy(alpha = 0.1f))
            else listOf(Lavender100, Lavender50, Primary50, Rose50),
            glowColor = Lavender300,
            accentColor = Rose300,
            iconTint = if (isDark) Lavender300 else Lavender400,
        ),
    )
}

data class SkinTypeOption(
    val id: String,
    val label: String,
    val description: String,
    val color: Color,
)

private val skinTypeOptions = listOf(
    SkinTypeOption("oily", "油性肌肤", "T区容易出油，毛孔偏大", Secondary300),
    SkinTypeOption("dry", "干性肌肤", "容易干燥紧绷，需要保湿", MetricHydrationLight),
    SkinTypeOption("combination", "混合肌肤", "T区偏油，两颊偏干", MetricPoreLight),
    SkinTypeOption("sensitive", "敏感肌肤", "容易泛红、刺痛，屏障脆弱", MetricRednessLight),
    SkinTypeOption("normal", "中性肌肤", "水油平衡，状态稳定", Lavender400),
)

class OnboardingScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: OnboardingViewModel = koinViewModel()
        val navigator = LocalNavigator.currentOrThrow
        val pages = carouselPages()
        val totalPages = pages.size + 1 // 3 carousel + 1 skin type
        val pagerState = rememberPagerState(pageCount = { totalPages })
        val coroutineScope = rememberCoroutineScope()
        val isSkinTypePage = pagerState.currentPage == pages.size
        val isLastCarousel = pagerState.currentPage == pages.size - 1

        var selectedSkinType by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                if (page < pages.size) {
                    OnboardingPageContent(
                        page = pages[page],
                        index = page,
                    )
                } else {
                    SkinTypeSelectionPage(
                        selectedSkinType = selectedSkinType,
                        onSkinTypeSelected = { selectedSkinType = it },
                    )
                }
            }

            // Footer area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.xl)
                    .padding(bottom = MaterialTheme.spacing.section),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Page indicator — active = wide bar, inactive = dot
                Row(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.sm,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    repeat(totalPages) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            animationSpec = tween(Motion.MEDIUM),
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant,
                            animationSpec = tween(Motion.MEDIUM),
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color),
                        )
                    }
                }

                // Buttons
                AnimatedContent(
                    targetState = isSkinTypePage,
                    modifier = Modifier.fillMaxWidth(),
                    transitionSpec = {
                        (fadeIn() + slideInVertically { it / 2 }) togetherWith
                                (fadeOut() + slideOutVertically { -it / 2 })
                    },
                    label = "onboarding_buttons",
                ) { skinTypePage ->
                    if (skinTypePage) {
                        // Skin type page — "Start" button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MaterialTheme.dimens.buttonHeight)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    brush = MaterialTheme.gradients.primary,
                                    alpha = if (selectedSkinType != null) 1f else 0.38f,
                                )
                                .then(
                                    if (selectedSkinType != null) Modifier.clickable {
                                        viewModel.completeOnboarding(selectedSkinType)
                                        navigator.replaceAll(AuthScreen())
                                    } else Modifier,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "开始使用",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // Full-width primary button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(MaterialTheme.dimens.buttonHeight)
                                    .clip(RoundedCornerShape(50))
                                    .background(brush = MaterialTheme.gradients.primary)
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "下一步",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            // Skip text below
                            TextButton(
                                onClick = {
                                    viewModel.completeOnboarding(null)
                                    navigator.replaceAll(AuthScreen())
                                },
                            ) {
                                Text(
                                    text = "跳过",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    index: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Illustration container (260dp) with layered circles
        Box(
            modifier = Modifier
                .size(260.dp)
                .animateListItem(index),
            contentAlignment = Alignment.Center,
        ) {
            // Glow layer (240dp, blurred)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(page.glowColor.copy(alpha = 0.15f)),
            )
            // Main circle background (210dp) with radial gradient
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(page.bgGradientColors),
                    ),
            )
            // Accent circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp)
                    .clip(CircleShape)
                    .background(page.accentColor.copy(alpha = 0.3f)),
            )
            // Icon
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = page.iconTint,
            )
        }

        // Title — 28sp Bold
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp,
            lineHeight = 35.sp,
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.xl)
                .animateListItem(index + 1),
        )

        // Subtitle — 15sp, onSurfaceVariant
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp,
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.compact)
                .animateListItem(index + 2),
        )
    }
}

@Composable
private fun SkinTypeSelectionPage(
    selectedSkinType: String?,
    onSkinTypeSelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.lg),
    ) {
        Text(
            text = "你的肤质是？",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 26.sp),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.cardInner)
                .animateListItem(0),
        )

        Text(
            text = "选择肤质帮助 AI 更准确地分析",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.xs, bottom = MaterialTheme.spacing.listGap)
                .animateListItem(1),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            skinTypeOptions.forEachIndexed { index, option ->
                val isSelected = selectedSkinType == option.id
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    animationSpec = tween(Motion.SHORT),
                )
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) Primary50
                    else MaterialTheme.colorScheme.surface,
                    animationSpec = tween(Motion.SHORT),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateListItem(index + 2)
                        .clip(MaterialTheme.shapes.large)
                        .border(
                            width = if (isSelected) 2.dp else 1.5.dp,
                            color = borderColor,
                            shape = MaterialTheme.shapes.large,
                        )
                        .background(bgColor)
                        .clickable { onSkinTypeSelected(option.id) }
                        .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.listGap),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.listGap),
                ) {
                    // Color icon container (40dp rounded square, medium radius)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(option.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Simple colored circle as icon placeholder
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(option.color.copy(alpha = 0.6f)),
                        )
                    }

                    // Text
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = option.description,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 1.dp),
                        )
                    }

                    // Radio circle (22dp)
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .then(
                                if (isSelected) {
                                    Modifier.background(MaterialTheme.colorScheme.primary)
                                } else {
                                    Modifier.border(
                                        2.dp,
                                        MaterialTheme.colorScheme.outlineVariant,
                                        CircleShape,
                                    )
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
