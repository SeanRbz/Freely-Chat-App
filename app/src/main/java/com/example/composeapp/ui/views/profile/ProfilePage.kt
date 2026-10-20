package com.example.composeapp.ui.views.profile

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.composeapp.repository.entities.SettingItem
import com.example.composeapp.repository.entities.SettingKind
import com.example.composeapp.repository.entities.UserProfile
import com.example.composeapp.repository.mock.MockFreelyData
import com.example.composeapp.ui.theme.Blush
import com.example.composeapp.ui.theme.CocoaMute
import com.example.composeapp.ui.theme.DeepEspresso
import com.example.composeapp.ui.theme.GoldenMarigold
import com.example.composeapp.ui.theme.LemonFizz
import com.example.composeapp.ui.theme.FreelyTheme
import com.example.composeapp.ui.theme.MintMist
import com.example.composeapp.ui.theme.PeachCreme
import com.example.composeapp.ui.theme.SunnyCoral
import com.example.composeapp.ui.theme.WarmSand
import com.example.composeapp.ui.theme.ZestyMint
import com.example.composeapp.ui.viewModel.ProfileViewModel
import com.example.composeapp.ui.views.base.Avatar
import com.example.composeapp.ui.views.base.IconCircle
import com.example.composeapp.ui.views.base.FreelyCard
import com.example.composeapp.ui.views.base.SoftPill
import com.example.composeapp.ui.views.base.sizedImageUrl

@Composable
fun ProfilePage(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ProfileContent(
        uiState = uiState,
        onShareInvite = {
            val send = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, "Come hang out with me on Freely! 🧡")
            context.startActivity(Intent.createChooser(send, "Invite friends to Freely"))
        },
        onLogout = onLogout,
        modifier = modifier
    )
}

/** Stateless "User Profile & Settings" screen from the Stitch design. */
@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onShareInvite: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val profile = uiState.profile ?: return
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(key = "header") { ProfileHeaderCard(profile) }
        item(key = "stats") { StatsRow(profile) }
        item(key = "invite") { InviteCard(onShareInvite) }
        item(key = "settings-title") {
            Text(
                "ACCOUNT & PREFERENCES",
                style = MaterialTheme.typography.labelLarge,
                color = CocoaMute,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        item(key = "settings") { SettingsCard(profile) }
        item(key = "logout") { LogoutButton(onLogout) }
        item(key = "footer") {
            Text(
                "${profile.appVersion}  •  Encrypted & Community-First",
                style = MaterialTheme.typography.labelMedium,
                color = CocoaMute,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(profile: UserProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Brush.verticalGradient(listOf(Color.White, Color(0xFFFFF1DC))))
            .border(1.dp, WarmSand, MaterialTheme.shapes.large)
    ) {
        IconCircle(
            icon = Icons.Outlined.Edit,
            containerColor = Color.White,
            contentColor = DeepEspresso,
            size = 44.dp,
            iconSize = 22.dp,
            contentDescription = "Edit profile",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box {
                Avatar(
                    url = profile.avatarUrl,
                    size = 120.dp,
                    ring = SolidColor(Blush),
                    ringWidth = 4.dp,
                    contentDescription = profile.name
                )
                if (profile.isVerified) {
                    IconCircle(
                        icon = Icons.Outlined.Verified,
                        containerColor = GoldenMarigold,
                        contentColor = DeepEspresso,
                        size = 36.dp,
                        iconSize = 20.dp,
                        contentDescription = "Verified",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .border(3.dp, Color.White, CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(profile.name, style = MaterialTheme.typography.headlineLarge, color = DeepEspresso)
            Text(profile.handle, style = MaterialTheme.typography.bodyLarge, color = SunnyCoral)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LemonFizz)
                    .border(1.dp, GoldenMarigold.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${profile.statusEmoji} ${profile.statusText}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CocoaMute
                )
                Box(
                    Modifier
                        .size(5.dp)
                        .background(GoldenMarigold, CircleShape)
                )
                Box(
                    Modifier
                        .size(8.dp)
                        .background(ZestyMint, CircleShape)
                )
                Text(profile.moodLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun StatsRow(profile: UserProfile) {
    val accents = listOf(SunnyCoral, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiary)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        profile.stats.forEachIndexed { index, stat ->
            FreelyCard(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stat.value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = accents[index % accents.size]
                )
                Text(stat.label, style = MaterialTheme.typography.labelMedium, color = DeepEspresso, maxLines = 1)
            }
        }
    }
}

@Composable
private fun InviteCard(onShareInvite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Brush.linearGradient(listOf(Color(0xFFFFE7DD), Color(0xFFFFF1D0))))
            .border(1.dp, GoldenMarigold.copy(alpha = 0.35f), MaterialTheme.shapes.large)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCircle(Icons.Outlined.Redeem, SunnyCoral, Color.White, size = 56.dp, iconSize = 28.dp)
        Column(Modifier.weight(1f)) {
            Text("Invite Friends to Freely", style = MaterialTheme.typography.titleMedium, color = DeepEspresso)
            Text("Bring your close circle together", style = MaterialTheme.typography.bodySmall, color = CocoaMute)
        }
        Text(
            "Share Link",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(role = Role.Button, onClick = onShareInvite)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun SettingsCard(profile: UserProfile) {
    FreelyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        profile.settings.forEachIndexed { index, item ->
            SettingRow(item, profile)
            if (index < profile.settings.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)
            }
        }
    }
}

private data class SettingVisuals(val icon: ImageVector, val container: Color, val content: Color)

@Composable
private fun SettingKind.visuals(): SettingVisuals = when (this) {
    SettingKind.StatusMood -> SettingVisuals(Icons.Outlined.SentimentSatisfied, LemonFizz, MaterialTheme.colorScheme.secondary)
    SettingKind.Notifications -> SettingVisuals(Icons.Outlined.Notifications, PeachCreme, SunnyCoral)
    SettingKind.PrivacySafety -> SettingVisuals(Icons.Outlined.Lock, MintMist, MaterialTheme.colorScheme.tertiary)
    SettingKind.MediaLinksDocs -> SettingVisuals(Icons.Outlined.PhotoLibrary, WarmSand, MaterialTheme.colorScheme.secondary)
    SettingKind.ChatTheme -> SettingVisuals(Icons.Outlined.Palette, PeachCreme, SunnyCoral)
}

@Composable
private fun SettingRow(item: SettingItem, profile: UserProfile) {
    val visuals = item.kind.visuals()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = {})
            .padding(vertical = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCircle(visuals.icon, visuals.container, visuals.content, size = 48.dp)
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = DeepEspresso)
                Text(
                    item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CocoaMute,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            when (item.kind) {
                SettingKind.StatusMood -> item.badge?.let {
                    SoftPill(it, containerColor = MintMist, contentColor = MaterialTheme.colorScheme.tertiary)
                }

                SettingKind.MediaLinksDocs -> Text(
                    "View all",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                SettingKind.ChatTheme -> ThemeSwatch()
                else -> Unit
            }
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = CocoaMute)
        }
        if (item.kind == SettingKind.MediaLinksDocs) {
            MediaStrip(urls = profile.recentMedia, moreCount = profile.moreMediaCount)
        }
    }
}

@Composable
private fun ThemeSwatch() {
    Box {
        Box(
            Modifier
                .size(20.dp)
                .background(SunnyCoral, CircleShape)
        )
        Box(
            Modifier
                .offset(x = 10.dp)
                .size(20.dp)
                .background(GoldenMarigold, CircleShape)
        )
    }
    Spacer(Modifier.size(10.dp))
}

@Composable
private fun MediaStrip(urls: List<String>, moreCount: Int) {
    val thumbPx = with(LocalDensity.current) { 96.dp.roundToPx() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        urls.forEach { url ->
            AsyncImage(
                model = sizedImageUrl(url, thumbPx),
                contentDescription = "Shared photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small)
                .background(PeachCreme),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.MoreHoriz, contentDescription = null, tint = SunnyCoral)
            Text("+$moreCount", style = MaterialTheme.typography.labelMedium, color = SunnyCoral)
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Blush, CircleShape)
            .clickable(role = Role.Button, onClick = onLogout),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text("Log Out", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true, heightDp = 1300)
@Composable
fun ProfilePagePreview() {
    FreelyTheme {
        ProfileContent(
            uiState = ProfileUiState(isLoading = false, profile = MockFreelyData.currentUser),
            onShareInvite = {},
            onLogout = {}
        )
    }
}
