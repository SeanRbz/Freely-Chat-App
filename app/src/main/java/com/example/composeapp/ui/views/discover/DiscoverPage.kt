package com.example.composeapp.ui.views.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.entities.Presence
import com.example.composeapp.repository.mock.MockFreelyData
import com.example.composeapp.ui.theme.CocoaMute
import com.example.composeapp.ui.theme.DeepEspresso
import com.example.composeapp.ui.theme.GoldenMarigold
import com.example.composeapp.ui.theme.LemonFizz
import com.example.composeapp.ui.theme.FreelyGradients
import com.example.composeapp.ui.theme.FreelyTheme
import com.example.composeapp.ui.theme.PeachCreme
import com.example.composeapp.ui.theme.SunnyCoral
import com.example.composeapp.ui.theme.ZestyMint
import com.example.composeapp.ui.viewModel.DiscoverViewModel
import com.example.composeapp.ui.views.base.Avatar
import com.example.composeapp.ui.views.base.GradientButton
import com.example.composeapp.ui.views.base.IconCircle
import com.example.composeapp.ui.views.base.SectionHeader
import com.example.composeapp.ui.views.base.SoftPill
import com.example.composeapp.ui.views.base.sizedImageUrl

@Composable
fun DiscoverPage(
    viewModel: DiscoverViewModel,
    onSayHello: (Person) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DiscoverContent(
        uiState = uiState,
        onSelectPerson = viewModel::selectPerson,
        onPass = viewModel::pass,
        onWave = viewModel::wave,
        onToggleSaved = viewModel::toggleSaved,
        onSayHello = onSayHello,
        modifier = modifier
    )
}

/** Stateless "Discover People" screen from the Stitch design. */
@Composable
fun DiscoverContent(
    uiState: DiscoverUiState,
    onSelectPerson: (Person) -> Unit,
    onPass: () -> Unit,
    onWave: () -> Unit,
    onToggleSaved: () -> Unit,
    onSayHello: (Person) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(key = "circles-header") {
            SectionHeader(
                title = "New In Your Circles",
                modifier = Modifier.padding(horizontal = 16.dp),
                leading = {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(SunnyCoral, CircleShape)
                    )
                },
                trailing = {
                    if (uiState.radiusLabel.isNotEmpty()) {
                        SoftPill(
                            uiState.radiusLabel,
                            containerColor = PeachCreme,
                            contentColor = SunnyCoral,
                            leading = {
                                Icon(Icons.Outlined.Radar, null, tint = SunnyCoral, modifier = Modifier.size(18.dp))
                            }
                        )
                    }
                }
            )
        }
        item(key = "circles") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.circles, key = { it.id }) { person ->
                    CircleAvatar(
                        person = person,
                        selected = person.id == uiState.featured?.id,
                        onClick = { onSelectPerson(person) }
                    )
                }
            }
        }
        uiState.featured?.let { person ->
            item(key = "featured") {
                FeaturedPersonCard(
                    person = person,
                    isSaved = uiState.isFeaturedSaved,
                    hasWaved = uiState.hasWavedAtFeatured,
                    onPass = onPass,
                    onWave = onWave,
                    onToggleSaved = onToggleSaved,
                    onSayHello = { onSayHello(person) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        uiState.icebreaker?.let { icebreaker ->
            item(key = "icebreaker") {
                IcebreakerCard(
                    title = icebreaker.title,
                    body = icebreaker.body,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun CircleAvatar(person: Person, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(84.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClickLabel = "Show ${person.firstName}", role = Role.Button, onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Avatar(
            url = person.avatarUrl,
            size = 72.dp,
            ring = if (person.hasNewMoment) FreelyGradients.Ring else SolidColor(MaterialTheme.colorScheme.surfaceVariant),
            presence = person.presence,
            contentDescription = person.name
        )
        Text(
            "${person.name}, ${person.age}",
            style = MaterialTheme.typography.labelLarge,
            color = when {
                selected -> SunnyCoral
                person.presence == Presence.Away -> CocoaMute
                else -> DeepEspresso
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FeaturedPersonCard(
    person: Person,
    isSaved: Boolean,
    hasWaved: Boolean,
    onPass: () -> Unit,
    onWave: () -> Unit,
    onToggleSaved: () -> Unit,
    onSayHello: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val photoPx = with(LocalDensity.current) { 520.dp.roundToPx() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(560.dp)
            .clip(MaterialTheme.shapes.large)
            .background(DeepEspresso)
    ) {
        AsyncImage(
            model = sizedImageUrl(person.photoUrl, photoPx),
            contentDescription = "Photo of ${person.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            Modifier
                .matchParentSize()
                .background(FreelyGradients.PhotoScrim)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isOnline = person.presence == Presence.Online
            SoftPill(
                if (isOnline) "Active on Freely" else "Away for now",
                containerColor = Color.White.copy(alpha = 0.88f),
                leading = {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(if (isOnline) ZestyMint else GoldenMarigold, CircleShape)
                    )
                }
            )
            Spacer(Modifier.weight(1f))
            IconCircle(
                icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                containerColor = Color.White.copy(alpha = 0.88f),
                contentColor = DeepEspresso,
                contentDescription = if (isSaved) "Remove ${person.firstName} from saved" else "Save ${person.firstName}",
                onClick = onToggleSaved
            )
            IconCircle(
                icon = Icons.Outlined.Share,
                containerColor = Color.White.copy(alpha = 0.88f),
                contentColor = DeepEspresso,
                contentDescription = "Share profile"
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${person.name}, ${person.age}",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (person.isVerified) {
                    IconCircle(
                        icon = Icons.Outlined.Verified,
                        containerColor = GoldenMarigold,
                        contentColor = DeepEspresso,
                        size = 30.dp,
                        iconSize = 18.dp,
                        contentDescription = "Verified"
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Palette, null, tint = GoldenMarigold, modifier = Modifier.size(20.dp))
                Text(
                    listOf(person.headline, person.mutuals).filter { it.isNotBlank() }.joinToString(" • "),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            if (person.interests.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    person.interests.forEach { interest ->
                        SoftPill(
                            "${interest.emoji}  ${interest.label}",
                            containerColor = Color.White.copy(alpha = 0.18f),
                            contentColor = Color.White
                        )
                    }
                }
            }
            if (person.askMeAbout.isNotBlank()) {
                AskMeAboutCard(person.askMeAbout)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCircle(
                    icon = Icons.Outlined.Close,
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White,
                    size = 52.dp,
                    contentDescription = "Pass",
                    onClick = onPass
                )
                Row(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(CircleShape)
                        .background(LemonFizz)
                        .clickable(enabled = !hasWaved, role = Role.Button, onClick = onWave)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (hasWaved) "✅" else "👋", fontSize = 20.sp)
                    Text(
                        if (hasWaved) "Waved" else "Wave",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeepEspresso
                    )
                }
                GradientButton(
                    text = "Say Hello",
                    onClick = onSayHello,
                    brush = FreelyGradients.Sunrise,
                    leadingIcon = Icons.Outlined.ChatBubbleOutline,
                    height = 52.dp,
                    textStyle = MaterialTheme.typography.labelLarge,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AskMeAboutCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White.copy(alpha = 0.93f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconCircle(
            icon = Icons.Outlined.WbSunny,
            containerColor = PeachCreme,
            contentColor = SunnyCoral,
            size = 40.dp,
            iconSize = 22.dp
        )
        Column {
            Text("ASK ME ABOUT", style = MaterialTheme.typography.labelMedium, color = SunnyCoral)
            Text(text, style = MaterialTheme.typography.bodyLarge, color = DeepEspresso, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun IcebreakerCard(title: String, body: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCircle(
            icon = Icons.Outlined.TipsAndUpdates,
            containerColor = LemonFizz,
            contentColor = GoldenMarigold,
            size = 52.dp
        )
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = DeepEspresso)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = CocoaMute)
        }
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = CocoaMute)
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
fun DiscoverPagePreview() {
    val feed = MockFreelyData.discoverFeed
    FreelyTheme {
        DiscoverContent(
            uiState = DiscoverUiState(
                isLoading = false,
                radiusLabel = feed.radiusLabel,
                circles = feed.circles,
                deck = listOf(feed.featured) + feed.circles,
                featured = feed.featured,
                icebreaker = feed.icebreaker,
            ),
            onSelectPerson = {},
            onPass = {},
            onWave = {},
            onToggleSaved = {},
            onSayHello = {}
        )
    }
}
