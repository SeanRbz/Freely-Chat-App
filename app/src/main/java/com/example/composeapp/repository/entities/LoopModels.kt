package com.example.composeapp.repository.entities

// Domain models behind the Freely screens. MockFreelyRepository serves them until a real API exists.

enum class Presence { Online, Away, Offline }

data class Interest(val emoji: String, val label: String)

data class Person(
    val id: String,
    val name: String,
    val age: Int,
    val avatarUrl: String,
    val presence: Presence,
    val headline: String,
    val mutuals: String = "",
    val interests: List<Interest> = emptyList(),
    val askMeAbout: String = "",
    val isVerified: Boolean = false,
    /** Draws the story-style gradient ring when the person has something new to see. */
    val hasNewMoment: Boolean = false,
    /** Full-bleed photo for the Discover card. */
    val photoUrl: String = avatarUrl,
) {
    val firstName: String get() = name.substringBefore(' ')
}

data class Icebreaker(val title: String, val body: String)

data class DiscoverFeed(
    val radiusLabel: String,
    val featured: Person,
    val circles: List<Person>,
    val icebreaker: Icebreaker,
)

data class ActiveFreely(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val presence: Presence,
    val hasNewMoment: Boolean = false,
    val isSelf: Boolean = false,
)

enum class PreviewKind { Text, Photo, VoiceNote }

enum class DeliveryStatus { Sent, Read }

data class Conversation(
    val id: String,
    val title: String,
    val titleEmoji: String? = null,
    /** One URL for a direct chat, two for a group (drawn stacked), none for an icon-only group. */
    val avatarUrls: List<String>,
    val presence: Presence = Presence.Offline,
    val hasNewMoment: Boolean = false,
    val sender: String? = null,
    val preview: String,
    val previewKind: PreviewKind = PreviewKind.Text,
    val timeLabel: String,
    val unreadCount: Int = 0,
    val deliveryStatus: DeliveryStatus? = null,
)

data class Inbox(
    val activeFreelys: List<ActiveFreely>,
    val conversations: List<Conversation>,
    val dailyPing: String,
)

sealed interface ChatMessage {
    val id: String
    val fromMe: Boolean
    val time: String

    data class Text(
        override val id: String,
        override val fromMe: Boolean,
        override val time: String,
        val text: String,
        val reaction: String? = null,
        val deliveryStatus: DeliveryStatus? = null,
    ) : ChatMessage

    data class VoiceMemo(
        override val id: String,
        override val fromMe: Boolean,
        override val time: String,
        val duration: String,
    ) : ChatMessage

    data class Place(
        override val id: String,
        override val fromMe: Boolean,
        override val time: String,
        val name: String,
        val details: String,
    ) : ChatMessage
}

data class ChatThread(
    val conversationId: String,
    val contactName: String,
    /** Null for icon-only group chats. */
    val contactAvatarUrl: String?,
    val presence: Presence,
    val statusLabel: String,
    val isFavorite: Boolean,
    val dayLabel: String,
    val messages: List<ChatMessage>,
    val isContactTyping: Boolean = false,
)

data class ProfileStat(val value: String, val label: String)

enum class SettingKind { StatusMood, Notifications, PrivacySafety, MediaLinksDocs, ChatTheme }

data class SettingItem(
    val kind: SettingKind,
    val title: String,
    val subtitle: String,
    val badge: String? = null,
)

data class UserProfile(
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val isVerified: Boolean,
    val statusEmoji: String,
    val statusText: String,
    val moodLabel: String,
    val stats: List<ProfileStat>,
    val settings: List<SettingItem>,
    val recentMedia: List<String>,
    val moreMediaCount: Int,
    val appVersion: String,
)

enum class AuthProvider { Phone, Email, Apple, Google }

/** The signed-in account. Sign-in is mocked, so this only records how the user signed in. */
data class Session(
    val provider: AuthProvider,
    /** Phone number or email; null for Apple and Google. */
    val identifier: String?,
    val signedInAt: Long,
)
