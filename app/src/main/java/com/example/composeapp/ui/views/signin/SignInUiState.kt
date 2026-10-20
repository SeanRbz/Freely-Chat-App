package com.example.composeapp.ui.views.signin

enum class SignInMethod(val tabLabel: String, val continueLabel: String) {
    Phone("Phone Number", "Continue with Phone"),
    Email("Email Address", "Continue with Email"),
}

data class SignInUiState(
    val method: SignInMethod = SignInMethod.Phone,
    val countryFlag: String = "🇺🇸",
    val countryCode: String = "+1",
    val phone: String = "",
    val email: String = "",
    val isSigningIn: Boolean = false,
    /** Set once the session is saved; SignInPage then leaves for the app. */
    val isSignedIn: Boolean = false,
) {
    val canContinue: Boolean
        get() = when (method) {
            SignInMethod.Phone -> phone.count(Char::isDigit) >= 7
            SignInMethod.Email -> '@' in email && '.' in email.substringAfter('@')
        }
}
