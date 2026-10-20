package com.example.composeapp.ui.views.signin

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composeapp.R
import com.example.composeapp.repository.entities.AuthProvider
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
import com.example.composeapp.ui.theme.WarmSand
import com.example.composeapp.ui.viewModel.SignInViewModel
import com.example.composeapp.ui.views.base.Avatar
import com.example.composeapp.ui.views.base.GradientButton
import com.example.composeapp.ui.views.base.FreelyCard
import com.example.composeapp.ui.views.base.FreelyTextInput
import com.example.composeapp.ui.views.base.SoftPill

// Decorative portraits for the "Welcome & Sign In" hero. The design's own left portrait URL
// returns HTTP 400, so it uses Sarah's photo from the Messages screen instead.
private const val HERO_LEFT_AVATAR = MockFreelyData.Images.SARAH
private const val HERO_RIGHT_AVATAR = "https://lh3.googleusercontent.com/aida-public/AB6AXuBPvE6hS1Cc4NmVwUReXJI5KJqIREtnJZ3NaPnFqOMQma9KxcnL-l053GPnFPs3BLUGjSbV9AC3evdUKY8W4u7QNPYvZyFdeDNWnQzdJP1FcmDL56DfvSyuZ7gwzy-L1vIr-ur3hoi1FxsRnIm06dBeGmk706j699ziplYbUMwqQnYUT-dwfNw-d6SopH-seTiR69UgMMXry3W_vFTEe_w37S7ZLacAvZl8Fti3F_AnJOxwM9cm-JPGAQ"

@Composable
fun SignInPage(viewModel: SignInViewModel, onSignedIn: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Leave once the session is saved. Keyed on the flag, so it runs once instead of on every recomposition.
    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn) onSignedIn()
    }
    SignInContent(
        uiState = uiState,
        onMethodChange = viewModel::setMethod,
        onPhoneChange = viewModel::setPhone,
        onEmailChange = viewModel::setEmail,
        onContinue = viewModel::continueWithForm,
        onSocialSignIn = viewModel::continueWith
    )
}

/** Stateless "Welcome & Sign In" screen from the Stitch design. */
@Composable
fun SignInContent(
    uiState: SignInUiState,
    onMethodChange: (SignInMethod) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
    onSocialSignIn: (AuthProvider) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WelcomeHero()
        Spacer(Modifier.height(28.dp))
        SoftPill(
            text = "YOUR HAPPY PLACE",
            containerColor = LemonFizz,
            borderColor = GoldenMarigold.copy(alpha = 0.5f),
            leading = {
                Icon(Icons.Outlined.Celebration, null, tint = SunnyCoral, modifier = Modifier.size(18.dp))
            }
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Stay close to your favorite people",
            style = MaterialTheme.typography.displaySmall,
            color = DeepEspresso,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Effortless messaging, calls, and shared moments in one cheerful place.",
            style = MaterialTheme.typography.bodyLarge,
            color = CocoaMute,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        SignInCard(
            uiState = uiState,
            onMethodChange = onMethodChange,
            onPhoneChange = onPhoneChange,
            onEmailChange = onEmailChange,
            onContinue = onContinue
        )
        Spacer(Modifier.height(24.dp))
        OrDivider()
        Spacer(Modifier.height(16.dp))
        SocialButton("Continue with Apple", R.drawable.ic_apple) { onSocialSignIn(AuthProvider.Apple) }
        Spacer(Modifier.height(12.dp))
        SocialButton("Continue with Google", R.drawable.ic_google) { onSocialSignIn(AuthProvider.Google) }
        Spacer(Modifier.height(24.dp))
        PrivacyFooter()
    }
}

@Composable
private fun WelcomeHero(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(FreelyGradients.PeachGlow)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            HeroPerson(caption = "☕ Coffee soon?", avatarUrl = HERO_LEFT_AVATAR)
            Column(
                modifier = Modifier.padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(FreelyGradients.Sunrise, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.FavoriteBorder, null, tint = Color.White)
                }
                Box(
                    Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(SunnyCoral.copy(alpha = 0.4f))
                )
                SoftPill("FREELY", containerColor = GoldenMarigold, contentColor = DeepEspresso)
            }
            HeroPerson(caption = "✨ Yes!", avatarUrl = HERO_RIGHT_AVATAR, captionColor = SunnyCoral)
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SoftPill(
                "5 unread chuckles",
                containerColor = Color.White,
                leading = { Icon(Icons.Outlined.Sms, null, tint = SunnyCoral, modifier = Modifier.size(18.dp)) }
            )
            SoftPill(
                "Encrypted",
                containerColor = Color.White,
                leading = {
                    Icon(
                        Icons.Outlined.Verified, null,
                        tint = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun HeroPerson(caption: String, avatarUrl: String, captionColor: Color = DeepEspresso) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SoftPill(caption, containerColor = Color.White, contentColor = captionColor, style = MaterialTheme.typography.labelMedium)
        Avatar(url = avatarUrl, size = 72.dp, presence = Presence.Online)
    }
}

@Composable
private fun SignInCard(
    uiState: SignInUiState,
    onMethodChange: (SignInMethod) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
) {
    FreelyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(20.dp)
    ) {
        MethodToggle(selected = uiState.method, onSelect = onMethodChange)
        Spacer(Modifier.height(20.dp))
        when (uiState.method) {
            SignInMethod.Phone -> {
                FieldLabel("Mobile number")
                InputPill {
                    Text(
                        "${uiState.countryFlag} ${uiState.countryCode}",
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepEspresso
                    )
                    Icon(
                        Icons.Outlined.ExpandMore,
                        contentDescription = "Change country code",
                        tint = CocoaMute,
                        modifier = Modifier.size(20.dp)
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(28.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    FreelyTextInput(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        placeholder = "(555) 000-0000",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = CocoaMute)
                }
            }

            SignInMethod.Email -> {
                FieldLabel("Email address")
                InputPill {
                    Icon(Icons.Outlined.Mail, contentDescription = null, tint = CocoaMute)
                    FreelyTextInput(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        placeholder = "you@example.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        GradientButton(
            text = uiState.method.continueLabel,
            onClick = onContinue,
            enabled = uiState.canContinue,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MethodToggle(selected: SignInMethod, onSelect: (SignInMethod) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(PeachCreme)
            .padding(4.dp)
            .selectableGroup()
    ) {
        SignInMethod.entries.forEach { method ->
            val isSelected = method == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(method) })
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    method.tabLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else CocoaMute
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = CocoaMute,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
private fun InputPill(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, WarmSand, CircleShape)
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = WarmSand)
        Text(
            "OR SIGN IN WITH",
            style = MaterialTheme.typography.labelMedium,
            color = CocoaMute,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = WarmSand)
    }
}

@Composable
private fun SocialButton(text: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, WarmSand, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(24.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, color = DeepEspresso)
    }
}

@Composable
private fun PrivacyFooter() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Shield, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
        Text(
            "We respect your privacy and never sell your data.",
            style = MaterialTheme.typography.bodySmall,
            color = CocoaMute
        )
    }
    Spacer(Modifier.height(8.dp))
    Text(
        "Terms of Service  •  Privacy Policy  •  Need Help?",
        style = MaterialTheme.typography.labelMedium,
        color = CocoaMute.copy(alpha = 0.8f)
    )
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
fun SignInPagePreview() {
    FreelyTheme {
        SignInContent(
            uiState = SignInUiState(phone = "(555) 010-2024"),
            onMethodChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onContinue = {},
            onSocialSignIn = {}
        )
    }
}
