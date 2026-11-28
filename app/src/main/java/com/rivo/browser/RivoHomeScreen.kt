package com.rivo.browser

import android.animation.ValueAnimator
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rivo.browser.data.SavedPage
import com.rivo.browser.designsystem.RivoShapes
import com.rivo.browser.designsystem.RivoSpacing

@Composable
internal fun RivoHomeScreen(
    bookmarks: List<SavedPage>,
    history: List<SavedPage>,
    privateMode: Boolean,
    onSearch: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    onNewPrivateTab: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenTabs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    val motionAlpha = rememberHomeMotionAlpha(ValueAnimator.areAnimatorsEnabled())
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = RivoSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(54.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFF4285F4))) { append("G") }
                withStyle(SpanStyle(color = Color(0xFFEA4335))) { append("o") }
                withStyle(SpanStyle(color = Color(0xFFFBBC05))) { append("o") }
                withStyle(SpanStyle(color = Color(0xFF4285F4))) { append("g") }
                withStyle(SpanStyle(color = Color(0xFF34A853))) { append("l") }
                withStyle(SpanStyle(color = Color(0xFFEA4335))) { append("e") }
            },
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .graphicsLayer { alpha = motionAlpha }
                .semantics { contentDescription = "Google search" }
        )
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .size(width = 84.dp, height = 3.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f),
                            Color.Transparent
                        )
                    )
                )
                .graphicsLayer { alpha = motionAlpha }
        )
        Spacer(Modifier.height(26.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = if (MaterialTheme.colorScheme.surface.alpha < 1f) 0.78f else 1f
            ),
            border = BorderStroke(1.dp, borderColor),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⌕", fontSize = 25.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Box(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    if (query.isEmpty()) {
                        Text(
                            "Search Google or type a URL",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth().semantics {
                            contentDescription = "Search Google or enter a website"
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                val submitted = query.trim()
                                if (submitted.isNotEmpty()) onSearch(submitted)
                                keyboard?.hide()
                            }
                        )
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Text("×", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(RivoSpacing.small)
        ) {
            HomeShortcut("Bookmarks", "☆", onOpenBookmarks, Modifier.weight(1f))
            HomeShortcut("History", "◷", onOpenHistory, Modifier.weight(1f))
            HomeShortcut("Private", "◉", onNewPrivateTab, Modifier.weight(1f))
            HomeShortcut("Tabs", "▢", onOpenTabs, Modifier.weight(1f))
        }

        if (bookmarks.isNotEmpty()) {
            Spacer(Modifier.height(30.dp))
            HomeSectionHeader("Bookmarks", "See all", onOpenBookmarks)
            Column(verticalArrangement = Arrangement.spacedBy(RivoSpacing.small)) {
                bookmarks.take(4).forEach { page ->
                    HomePageRow(page = page, onClick = { onOpenUrl(page.url) })
                }
            }
        }

        if (!privateMode && history.isNotEmpty()) {
            Spacer(Modifier.height(22.dp))
            HomeSectionHeader("Recently visited", "History", onOpenHistory)
            Column(verticalArrangement = Arrangement.spacedBy(RivoSpacing.small)) {
                history.take(3).forEach { page ->
                    HomePageRow(page = page, onClick = { onOpenUrl(page.url) })
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        Text(
            if (privateMode) "Private session · Browsing is not saved to local history"
            else "Google is your default search engine",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(22.dp))
    }
}

@Composable
private fun rememberHomeMotionAlpha(enabled: Boolean): Float {
    if (!enabled) return 0.84f
    val transition = rememberInfiniteTransition(label = "home-silver-motion")
    val alpha by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home-silver-sheen"
    )
    return alpha
}

@Composable
private fun HomeShortcut(
    title: String,
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(RivoShapes.card))
            .clickable(onClick = onClick)
            .padding(vertical = RivoSpacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RivoSpacing.compact)
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(symbol, fontSize = 23.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
private fun HomeSectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f).semantics { heading() }
        )
        androidx.compose.material3.TextButton(onClick = onAction) { Text(action) }
    }
}

@Composable
private fun HomePageRow(page: SavedPage, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(RivoShapes.card),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = RivoSpacing.medium, vertical = RivoSpacing.small),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                page.title.ifBlank { page.url },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                page.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
