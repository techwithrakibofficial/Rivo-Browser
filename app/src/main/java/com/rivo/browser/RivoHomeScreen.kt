package com.rivo.browser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rivo.browser.data.SavedPage
import com.rivo.browser.designsystem.RivoShapes
import com.rivo.browser.designsystem.RivoSpacing

@Composable
internal fun RivoHomeScreen(
    bookmarks: List<SavedPage>,
    history: List<SavedPage>,
    privateMode: Boolean,
    onOpenUrl: (String) -> Unit,
    onNewTab: () -> Unit,
    onNewPrivateTab: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenTabs: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = RivoSpacing.large, vertical = RivoSpacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(RivoSpacing.large)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(RivoSpacing.compact)) {
            Text(
                "RIVO",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "A calmer way to browse.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                if (privateMode) "Private session · Your browsing stays out of local history"
                else "Your web, your space. Pick up where you left off.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(RivoShapes.panel),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(RivoSpacing.large),
                verticalArrangement = Arrangement.spacedBy(RivoSpacing.small)
            ) {
                Text("Quick access", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(RivoSpacing.small)
                ) {
                    HomeAction("New tab", "Start fresh", onNewTab, Modifier.weight(1f))
                    HomeAction("Private", "No local history", onNewPrivateTab, Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(RivoSpacing.small)
                ) {
                    HomeAction("All tabs", "Switch pages", onOpenTabs, Modifier.weight(1f))
                    HomeAction("Bookmarks", "Saved pages", onOpenBookmarks, Modifier.weight(1f))
                }
            }
        }

        HomeSectionHeader("Bookmarks", "See all", onOpenBookmarks)
        if (bookmarks.isEmpty()) {
            HomeEmptyState("Your saved pages will appear here.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(RivoSpacing.small)) {
                bookmarks.take(5).forEach { page ->
                    HomePageRow(page = page, onClick = { onOpenUrl(page.url) })
                }
            }
        }

        if (!privateMode) {
            HomeSectionHeader("Recently visited", "History", onOpenHistory)
            if (history.isEmpty()) {
                HomeEmptyState("Pages you visit will show up here.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(RivoSpacing.small)) {
                    history.take(5).forEach { page ->
                        HomePageRow(page = page, onClick = { onOpenUrl(page.url) })
                    }
                }
            }
        }

        Spacer(Modifier.height(RivoSpacing.small))
        Text(
            if (privateMode) "Private session · History is not shown or saved"
            else "Local bookmarks and history · No Rivo account required",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun HomeAction(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(RivoShapes.card),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(RivoSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HomeSectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f).semantics { heading() })
        TextButton(onClick = onAction) { Text(action) }
    }
}

@Composable
private fun HomePageRow(page: SavedPage, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(RivoShapes.card),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = RivoSpacing.medium, vertical = RivoSpacing.small),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                page.title.ifBlank { page.url },
                style = MaterialTheme.typography.bodyLarge,
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

@Composable
private fun HomeEmptyState(message: String) {
    Text(
        message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = RivoSpacing.small)
    )
}
