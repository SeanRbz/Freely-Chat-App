package com.example.composeapp.ui.views.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composeapp.repository.entities.Presence
import com.example.composeapp.repository.mock.MockFreelyData
import com.example.composeapp.ui.theme.Blush
import com.example.composeapp.ui.theme.CocoaMute
import com.example.composeapp.ui.theme.DeepEspresso
import com.example.composeapp.ui.theme.GoldenMarigold
import com.example.composeapp.ui.theme.FreelyGradients
import com.example.composeapp.ui.theme.FreelyTheme
import com.example.composeapp.ui.theme.PeachCreme
import com.example.composeapp.ui.theme.SunnyCoral
import com.example.composeapp.ui.theme.ZestyMint

/** Stitch-hosted (googleusercontent) images accept "=s<px>" and return a resized copy. */
fun sizedImageUrl(url: String, px: Int): String =
    if ("googleusercontent.com" in url && '=' !in url.substringAfterLast('/')) "$url=s$px" else url

/** Circular photo with an optional ring and presence dot. A null [url] shows a group icon. */
@Composable
fun Avatar(
    url: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    ring: Brush? = null,
    ringWidth: Dp = 3.dp,
    presence: Presence? = null,
    contentDescription: String? = null,
) {
    val px = with(LocalDensity.current) { size.roundToPx() }
    Box(modifier = modifier.size(size)) {
        val imageModifier = if (ring != null) {
            Modifier
                .fillMaxSize()
                .border(ringWidth, ring, CircleShape)
                .padding(ringWidth + 2.dp)
        } else {
            Modifier.fillMaxSize()
        }
        if (url == null) {
            Box(
                modifier = imageModifier.background(PeachCreme, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Groups, contentDescription, tint = SunnyCoral, modifier = Modifier.size(size / 2))
            }
        } else {
            AsyncImage(
                model = sizedImageUrl(url, px),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
        }
        if (presence != null && presence != Presence.Offline) {
            PresenceDot(
                presence = presence,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.26f)
            )
        }
    }
}

@Composable
fun PresenceDot(presence: Presence, modifier: Modifier = Modifier) {
    val color = when (presence) {
        Presence.Online -> ZestyMint
        Presence.Away -> GoldenMarigold
        Presence.Offline -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = modifier
            .background(Color.White, CircleShape)
            .padding(2.dp)
            .background(color, CircleShape)
    )
}

/** The Freely app mark: a sunrise-gradient tile with a chat bubble. */
@Composable
fun FreelyLogoMark(modifier: Modifier = Modifier, size: Dp = 44.dp) {
    val shape = MaterialTheme.shapes.small
    Box(
        modifier = modifier
            .size(size)
            .shadow(6.dp, shape, spotColor = SunnyCoral)
            .background(FreelyGradients.Sunrise, shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

/** Header shared by the bottom-bar tabs: logo, search and the signed-in user's avatar. */
@Composable
fun FreelyTopBar(
    avatarUrl: String?,
    onSearchClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FreelyLogoMark(size = 40.dp)
            Spacer(Modifier.width(12.dp))
            Text("Freely", style = MaterialTheme.typography.headlineMedium, color = DeepEspresso)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Search", tint = DeepEspresso)
            }
            if (avatarUrl != null) {
                Avatar(
                    url = avatarUrl,
                    size = 44.dp,
                    ring = SolidColor(GoldenMarigold),
                    ringWidth = 2.dp,
                    contentDescription = "Your profile",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onAvatarClick)
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

/** Pill-shaped primary action filled with a gradient. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = FreelyGradients.Ember,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    height: Dp = 56.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
) {
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .height(height)
            .shadow(if (enabled) 8.dp else 0.dp, CircleShape, spotColor = SunnyCoral)
            .clip(CircleShape)
            .background(brush)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let { Icon(it, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp)) }
        Text(text, style = textStyle, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        trailingIcon?.let { Icon(it, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp)) }
    }
}

/** Small rounded label, optionally with a leading icon or dot. */
@Composable
fun SoftPill(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = PeachCreme,
    contentColor: Color = DeepEspresso,
    borderColor: Color? = null,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    leading: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, CircleShape) else Modifier)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading?.invoke()
        Text(text, style = style, color = contentColor, maxLines = 1)
    }
}

/** Round icon tile, clickable when [onClick] is set. */
@Composable
fun IconCircle(
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = contentColor, modifier = Modifier.size(iconSize))
    }
}

/** White rounded card with the hairline border used throughout the design. */
@Composable
fun FreelyCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainer, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(contentPadding),
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

/** Section title row: optional leading mark, title, a mark right after the title, trailing action. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    afterTitle: (@Composable () -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading?.invoke()
        Text(title, style = MaterialTheme.typography.titleLarge, color = DeepEspresso)
        afterTitle?.invoke()
        Spacer(Modifier.weight(1f))
        trailing?.invoke(this)
    }
}

/** Single-line text input without Material chrome, for the pill-shaped fields in the design. */
@Composable
fun FreelyTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = textStyle.copy(color = DeepEspresso),
        cursorBrush = SolidColor(SunnyCoral),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        style = textStyle,
                        color = CocoaMute.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                innerTextField()
            }
        }
    )
}

/** Peach pill search field from the Messages screen. */
@Composable
fun FreelySearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(PeachCreme)
            .border(1.dp, Blush, CircleShape)
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = CocoaMute)
        FreelyTextInput(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

@Composable
fun UnreadBadge(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(FreelyGradients.Sunrise, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("$count", style = MaterialTheme.typography.labelMedium, color = Color.White)
    }
}

/** Centered helper text for empty lists. */
@Composable
fun EmptyHint(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = CocoaMute,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp),
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun FreelyTopBarPreview() {
    FreelyTheme {
        FreelyTopBar(avatarUrl = MockFreelyData.currentUser.avatarUrl, onSearchClick = {}, onAvatarClick = {})
    }
}
