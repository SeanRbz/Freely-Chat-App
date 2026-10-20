package com.example.composeapp.repository.mock

import com.example.composeapp.repository.entities.ActiveFreely
import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.Conversation
import com.example.composeapp.repository.entities.DeliveryStatus
import com.example.composeapp.repository.entities.DiscoverFeed
import com.example.composeapp.repository.entities.Icebreaker
import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.entities.Interest
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.entities.Presence
import com.example.composeapp.repository.entities.PreviewKind
import com.example.composeapp.repository.entities.ProfileStat
import com.example.composeapp.repository.entities.SettingItem
import com.example.composeapp.repository.entities.SettingKind
import com.example.composeapp.repository.entities.UserProfile

/**
 * Mock-up content taken from the "Social Friendly Chat App" Google Stitch project
 * (Discover People, Messages & Inbox, Active Chat Thread, User Profile & Settings).
 * Names, copy and photos match the design; details for people the design only shows as
 * avatars (Sofia, Marcus, ...) are written in the same voice.
 */
object MockFreelyData {

    /** Stitch-hosted photos. They accept a "=s<px>" suffix for resized copies. */
    object Images {
        /**
         * The signed-in user, styled after the "Giga Chad" meme. An Unsplash photo (Unsplash License);
         * its query string sizes it and turns it black and white.
         */
        const val SELF = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=512&h=512&fit=crop&crop=faces&sat=-100&con=20&auto=format&q=80"
        const val SOFIA = "https://lh3.googleusercontent.com/aida-public/AB6AXuBzCrwAeMTasasaLpSMnWo4mXmfHt28-QyJ-qfxQ6o1MuCp2ll0ssE5xJW1cYXGbc2E1mkvbObF-CZMiXFBrYw6humUoKrZE_kxFfi2aWzY-HUtf1USeybHoAcIhUOkCx4r0HBNACYII8uv6NCn0508FmKtAAFR1GcClMCblGJfSPJMOrMHId9AXnyKwuByoLvfEghe323cdWs5M6sIjMfxuOPDwU6MIOUx7O-8_mE3rPysHtiC3U88bw"
        const val MARCUS = "https://lh3.googleusercontent.com/aida-public/AB6AXuBBMkXoOEQKeLKbPdcWUDJwfHy3zAvATRRdSR325sjM4Q0FBmf32e6aTYUNiu51ZWhPvjmJEUedUlqie8KmVIJJS2soFXfaFyz8-6HH3zMl0fHvgYi4m5zsdBxthbxQBYMd8PddRgMvg6yTfO7ai_bCysKLTIfq5iT4a_0Msh04XWUqan-VMhM6QsK-OdgN6DyUPm3LElAyYdxI5T41Z7lZf7VzimQCVpzs5EGvDaoCawh-bnkEz9FmoQ"
        const val AALIYAH = "https://lh3.googleusercontent.com/aida-public/AB6AXuCCBGM6Hs52KZOD5N3liH9bOO2xW_KvDuqi1PKmN34qtau6BTl6lI5FJI39TAnJDrDf4n04RnNEVzwTMG9aX_5vLU6lX9WTEIuVFNGcxz2BmeKpnCjLecMHxQ-EiH66uvbfJcQhKTwFqdrTcJN3sUeWwS-nwt_MU_G7Q3-VVzzyoiNbdXr6p7yRsajyF2tYb8Nt5ry8PecvrhA2ehMolrWLxTb0vYJO1JJd8HL_sQv33IYXmgS1CvdsQg"
        const val JULIAN = "https://lh3.googleusercontent.com/aida-public/AB6AXuCxl5BoZWjPREwLM7jG7rlXzG6THmfiPFYJFawNTMqyt1yo_GSUyHYpK45nQVKGitTjP-jcMkPfNuxLdBmP7CUAlGcAqn9KiyvhA-N6-z1bCvlD-_MfGd9uEl5_0ZaDY2gFnTLrgzYQPdRnq14NfZrWkwCYo0yw7DG29__8B7Yk8WLJnYLg81Wy7pXVNaBchvjNAj_XYj5TYqi1FLntIIseTdozxgFjECJM5ChJVeMFFkllRWIAl7YJhQ"
        const val ELENA_GARDEN = "https://lh3.googleusercontent.com/aida-public/AB6AXuCeEmc5mRMaLTeV2f9pxVmjIkNI2rT2GyhvNeoZ77Hwlj2Ib39tn_j2SmMWcXSz4X4tfE5Rvt7tQvxxmqiDNi6bYhO_4o_r_ldSIej-T3ZM7eVvIVOiQLl5w2aZKhP7P_7GCLSFQeL_CmVZ1boUMYCL32gNfXNp2m64X3CRcOHDYlEggvwl80xbypC9KuWRTgILWrtVMvL25yjOgTZnTUcc24ouOMJwrf7KXCkdQAw2c6jE482CeCDSvg"
        const val CHLOE_FEATURED = "https://lh3.googleusercontent.com/aida-public/AB6AXuAAjJ8DvXKnnwQ1TDUgKTc5basiJLlHodz5IrAMh4sGnJtDvc2lYYrBCU8eqFkEgNya0R62CU4FUzB_bWGjpuJJu1NFFyK7tQmYmHJWV2t83ZXfIrkcUldw-FkWmcz-fU9A1zyZxkJtoYSerjXqxRlfKXXrQJL261lA7Vcs9cGBTJxpi-YzVuiBxYf0YxN5vIMz53XsQSq-cPgr4hf-RuxxU7ltYgaGnUIIIufwohyN0QHuwGjQg3uXvQ"
        const val MAYA_LOOP = "https://lh3.googleusercontent.com/aida-public/AB6AXuDpBAd6VuAYvBWjalVJmg_Kwe8ZhT6jDRFw1mAHG7UKCqTSvA887KRzbHUT28P-4iZ1ag0ZLtSyIePB5WHlyYY1V1nKA7D58fovmkH0uH5GpilPZeim9WlhebPURe9G7ZnxTT7RYOxuLQLuyn9j-hN12Wj_WydqiDv118x1cTyF3nDECsggmDHksuMFE048oV1LBJrtoIkuJeT3sY8QlOZdYHSbhtD7YQpxPe_LFjo1-W-KY7JfzT0_3w"
        const val LIAM_LOOP = "https://lh3.googleusercontent.com/aida-public/AB6AXuDlZThpw9yUtjI9gtiT6T_gU6lxCYSPPT8B6Lj6ZpSOtdLaX-1dGnVZLb_TWT31EGw18TuKXu0Y_uDKOrq7eJSQ6Bzm-vFmmP-Ao81I2to_zkG4pkOSjyNBWGXzOl2BBS8J18E8QxquNT7Tw7bO_IxkMtcaWzI07BfxBWs-MrxUUkxKUdgkudm-KxkTSB6KZ2r6_GnKajzAdmWb0idM9XRRUfZhXx_1USV4srjaU92zibNCfllmMLG8Qw"
        const val SARAH = "https://lh3.googleusercontent.com/aida-public/AB6AXuD62eBS47ia2ywthiu2LsXfl-zV5Mhd4uD-CzMHYf36ddyEfwGcz-XWuLO_bP_aGtOVBJjBRvaYeUGw8UvAvRNvr6fIljH-yy8Je1X0WFqXfQEdZLH-L3mLxqIi_bnGjX9H_nKjRLckR-t0UHwcOZWqp-d6C9lY67-EAzpD6UVBN6n5FC6A0QbquZVu530FW2f5d9JPQvXv0MtrT_vB1uJtx-A6YLKhLpo7ySaHjIsj_uCWHUpEJjOcEg"
        const val ALEX = "https://lh3.googleusercontent.com/aida-public/AB6AXuBZRblpFLWXCI8lUCMAp6JbeyfLn9QEbhn8iTcOLvNCOJvRsRF3UG49Nen-H4wY7ahY55AnCaBl9JBcT6EQXxcxMpFlvA33ZwxtmZ2nURfARLdQ0etbUj5XW7KvGvWKo1sYYNpA_cuXuBfYs5m1CELI3GKJBRJFGNIt_Fo7D7N2BizSeJlZg_6KNzljExZuPIF8sRu-7L4X7QEjhJy6QBM6LP0c0Umtt0XOwYJZPZ_ZKR5FPf1m_78zCg"
        const val CHLOE_LOOP = "https://lh3.googleusercontent.com/aida-public/AB6AXuAKPyjP9zIwvO1bJ6E8bKvvRLrcXbR0bGoZt4DXzlzCR6P0Gh_sZ9Y-FQ7FabPLghJHZ4pTdqjSxtMv334QqXS34gVIQFcrvbE6BqM3Woqmduv1RtcJJHKLL_KSgrUKtlUo8yeRRWVjIHMeqkmN_ehI7oKUGKRv2OAe5TLqm7XdHD7epOtPMzc6FFaLX_HXbNC90N21d0A6BaXBb8Gu7YogfNLnD5XUkkvjpbrikx7TOMqbhjqUT9ZufA"
        const val MAYA_CONVO = "https://lh3.googleusercontent.com/aida-public/AB6AXuCNF1SZv-WjZDpowfJ7ltU_idPkY5AoQsY9MLEC99NK6slwiYeP26tNPxC7m2Bo8b5nrj36uL-X509ZerHSRo3C16SrVTcNPBMcRC-qFa6-DiqOu0s8hgaVMenUuInCgkphXxajpbjgbaeEUVwGdEN3hQM8r5Wml5MBfC98xxPVM6JwVR3yHkU6WXtSr6sLQ4WUdBaFeSEMZzjhYSHXMi50eAQT2SH4ujq_ALhQiHmUITlKP8ujYTTyzg"
        const val HIKER_LIAM = "https://lh3.googleusercontent.com/aida-public/AB6AXuAgoV_EfON2FnXS9F0xA41TeCy-RwsUFHOkk6PdjZRa5puyV2ldiGA1KkvzDiM670ZCaYPXDSudL5RPiP2pru1Lh1utC9q23MrRiW7r9M1lm7tfJZUfsTNrtjnG4y7wWF3jHcy86IZ5-ztHUAKKfttFlT8-bn5rB2gmueNGtSVpkdtx_cUTl4qeCFBa0cO4e9YU62w5rm7A0qlDMAIaf32CtFwsSICEDGhlvocyODbhQlZ74MB_PqkDrQ"
        const val HIKER_WOMAN = "https://lh3.googleusercontent.com/aida-public/AB6AXuD5Imvyj77K1QSyzjjk9AKzA2ERf5cHNptn7fPfw75F-QNidylKlrHidkHtKxe43Lu3A_SNhdtRXQsqx5PwdoNRtqEgY6LlsHT-_deqwGucQf-ucHVZ775sejWve4Eb-xrOUDFblxdp01Qwrh-J-_vz2p1tavxSPaVHxGTBSCNDpag1H7BVFrG2CZ9Ypcx-g_v-DCu6JZAIzaueWqi0deDNHsMKHpMVfR-nQg1s-Tohff2wdJXfR9r_Vw"
        const val DAVID = "https://lh3.googleusercontent.com/aida-public/AB6AXuBODSbZN7FnJ6QPvSEcb_yDORNd9HqwwxUDruAOb69q8B2I_MEcS7eLqjfy60EaAACWjhthHBO-bWOWxIsixxc-gmh_JUJdnp8u4vfEg-NkZyAWgKcjjmNtWJIe5pgnj__Hl4Wz1cpm0e-6_v8VLJiJaAEQdyIemYypgKtKdcpnfyaTiFHxyUsiGl_y1-77Fzsezh9ZcJgWwv1z1z32OFKx3OrIqlSDA3qLzOZPsoP5UWYi64aBbghV2Q"
        const val CHLOE_CONVO = "https://lh3.googleusercontent.com/aida-public/AB6AXuB7sH6T7M9MkXDTmTN3pjP6gnaz_JP5Vv6fxoe19iDjJgzNynzkC6nAKhAv41zi-1Ml-r0iV9U3Cqps9OnLwxWvHbuQrtk16hJpLoaLGJ1mfMFVeP5R7OO_uyYTaIjpWpbJTuA0kuXp_7bRBlCRpoEMHr6EOSv52oy5ag_4R8Z_AJfgn448wYMYIflnCtHgNLPIbpjSomYB_Hj5EFI4DBkTVdwFpZXVU2x4kOtlC1h4mIIbOcu9ofz7RQ"
        const val MAYA_CHAT = "https://lh3.googleusercontent.com/aida-public/AB6AXuBpHQhc90WZxxyHFGBmao8433MF5glGrS_1EqPsO_cCWHuhKuSQ-z1TBhwbyjy2UZZBvp19_1mLsz5n8_IZOAkFPGGzP2DhGo4XhDBiDm92334gGQ99IxQjB52L22gXXRy0ZQTC70PRupeZi4A_CR_VLkhhprLbgnupuBDDdr7eYs-ucNE6TnADkdTR51ZxUuegrP-GInpFz5_L-g4ICpAdj7FJGoZrU6LrYdhl-nAyUXzHdum4vhPl9g"
        const val MEDIA_MUG = "https://lh3.googleusercontent.com/aida-public/AB6AXuDcsB4inlceEkNYywivin2nU6swobtWeAnTvzRWaLFYcX4O1vAx4mJREWazaVefiPBvYaK0NpQ2U_so6sy2yqbx-E7veEFSkRmyYK1NW6vs33rRp517Kh9NR3-g_S0-n45enjgDpf5IQQvIkFrDu5jxDrzWoUmdBN9D8JCWOmCySnsBGgC_i2UeSnNJLaRuvgVgeblKNKI7MVlNkp6r3a-sidLTJ_6_40C8o6Td1Wwiu5TOPPzBDeWgxQ"
        const val MEDIA_PLANTS = "https://lh3.googleusercontent.com/aida-public/AB6AXuC449Skmi3AdBR8x567PmWMjjc_s4iRJ3hDmon5UFjMy2nqqrtk79ImstqI4GYEQ1ZM2t0W4oz-CboUVqrEwbYKCVdi3U6dzo49ghrmYmAu8UiVPcnG3TONw0ymhyuGsIgANh2BIvymnJLdHXlzsig4guHC70pOQaAQZe1CGjPmOK90BKCivhWSVac4K3tNbAftiapQIkeHJptaLqwC7CzU4qBxlbhcOJ0NUBZZSrNDhduvcrifZG6DyA"
        const val MEDIA_MOODBOARD = "https://lh3.googleusercontent.com/aida-public/AB6AXuADfqRLFVEtbOpq96cmrVr_mbdhFLdN9Azt8fvQqubBJOfwhx5ZEXRYzGQonaD-N47NsJqigiwu49wjC3FzxexhhI8PEEEKr4KEy5loJWoqBJtY_ttzbmyuWlVDV4_LmC0FD_qs5m9x2aJX9w-XaRAXq1uqvdG4xWx9QtgNWlItjYMOZC7Miw1pqOrrBMpROSmpjDzKgiMpv_0998YQJjQHt3jcOS1p082sIeYc6B3tOMakHkQGUvXpng"
    }

    // ---- People ----------------------------------------------------------------------------

    private val chloe = Person(
        id = "chloe",
        name = "Chloe Bennett",
        age = 24,
        avatarUrl = Images.CHLOE_CONVO,
        photoUrl = Images.CHLOE_FEATURED,
        presence = Presence.Online,
        headline = "Graphic Designer",
        mutuals = "3 mutuals in Design Freely",
        interests = listOf(
            Interest("☕", "Coffee Hopping"),
            Interest("🎨", "Pottery"),
            Interest("🪴", "Indoor Jungle"),
        ),
        askMeAbout = "My favorite secret corner bookshops & warm matcha spots in the city!",
        isVerified = true,
        hasNewMoment = true,
    )

    private val sofia = Person(
        id = "sofia",
        name = "Sofia",
        age = 23,
        avatarUrl = Images.SOFIA,
        presence = Presence.Online,
        headline = "Pastry Chef",
        mutuals = "5 mutuals in Brunch Club",
        interests = listOf(
            Interest("🥐", "Weekend Baking"),
            Interest("💃", "Salsa Nights"),
            Interest("📚", "Book Swaps"),
        ),
        askMeAbout = "The flakiest croissant I've ever made, and the three I burned first.",
        hasNewMoment = true,
    )

    private val marcus = Person(
        id = "marcus",
        name = "Marcus",
        age = 26,
        avatarUrl = Images.MARCUS,
        presence = Presence.Online,
        headline = "Street Photographer",
        mutuals = "2 mutuals in Photo Walks",
        interests = listOf(
            Interest("📷", "Film Cameras"),
            Interest("🚲", "City Rides"),
            Interest("🎧", "Lo-fi Beats"),
        ),
        askMeAbout = "Hidden rooftops with the best golden-hour light.",
        isVerified = true,
        hasNewMoment = true,
    )

    private val aaliyah = Person(
        id = "aaliyah",
        name = "Aaliyah",
        age = 25,
        avatarUrl = Images.AALIYAH,
        presence = Presence.Online,
        headline = "Illustrator",
        mutuals = "4 mutuals in Design Freely",
        interests = listOf(
            Interest("✏️", "Sketchbooks"),
            Interest("🌿", "Plant Swaps"),
            Interest("🎬", "Indie Films"),
        ),
        askMeAbout = "Which cafés still let you doodle on the napkins (I keep a list).",
        hasNewMoment = true,
    )

    private val julian = Person(
        id = "julian",
        name = "Julian",
        age = 27,
        avatarUrl = Images.JULIAN,
        presence = Presence.Away,
        headline = "Bookshop Owner",
        mutuals = "1 mutual in Book Club",
        interests = listOf(
            Interest("📖", "Mystery Novels"),
            Interest("♟️", "Chess"),
            Interest("☕", "Pour-over"),
        ),
        askMeAbout = "The rare first editions hiding on my top shelf.",
    )

    private val elena = Person(
        id = "elena",
        name = "Elena",
        age = 24,
        avatarUrl = Images.ELENA_GARDEN,
        presence = Presence.Online,
        headline = "Botanist",
        mutuals = "6 mutuals in Green Thumbs",
        interests = listOf(
            Interest("🌸", "Botanical Gardens"),
            Interest("🧺", "Picnics"),
            Interest("🎻", "Cello"),
        ),
        askMeAbout = "Which houseplants survive even the busiest week.",
        hasNewMoment = true,
    )

    private val maya = Person(
        id = "maya",
        name = "Maya Lin",
        age = 25,
        avatarUrl = Images.MAYA_CONVO,
        presence = Presence.Online,
        headline = "Product Designer",
        hasNewMoment = true,
    )

    private val liam = Person(
        id = "liam",
        name = "Liam",
        age = 27,
        avatarUrl = Images.LIAM_LOOP,
        presence = Presence.Online,
        headline = "Trail Runner",
        hasNewMoment = true,
    )

    private val sarah = Person(
        id = "sarah",
        name = "Sarah",
        age = 24,
        avatarUrl = Images.SARAH,
        presence = Presence.Online,
        headline = "Ceramics Teacher",
    )

    private val alex = Person(
        id = "alex",
        name = "Alex",
        age = 28,
        avatarUrl = Images.ALEX,
        presence = Presence.Online,
        headline = "UX Writer",
    )

    private val david = Person(
        id = "david",
        name = "David Chen",
        age = 29,
        avatarUrl = Images.DAVID,
        presence = Presence.Offline,
        headline = "Barista & Home Cook",
    )

    // ---- Screens -----------------------------------------------------------------------------

    val discoverFeed = DiscoverFeed(
        radiusLabel = "Within 5 km",
        featured = chloe,
        circles = listOf(sofia, marcus, aaliyah, julian, elena),
        icebreaker = Icebreaker(
            title = "Freely Icebreaker",
            body = "92% of chats that start with a question lead to friendship.",
        ),
    )

    val inbox = Inbox(
        activeFreelys = listOf(
            ActiveFreely("me", "Your ping", Images.SELF, Presence.Online, isSelf = true),
            ActiveFreely("maya", "Maya", Images.MAYA_LOOP, Presence.Online, hasNewMoment = true),
            ActiveFreely("liam", "Liam", Images.LIAM_LOOP, Presence.Online, hasNewMoment = true),
            ActiveFreely("sarah", "Sarah", Images.SARAH, Presence.Online),
            ActiveFreely("alex", "Alex", Images.ALEX, Presence.Online),
            ActiveFreely("chloe", "Chloe", Images.CHLOE_LOOP, Presence.Online),
        ),
        conversations = listOf(
            Conversation(
                id = "maya",
                title = "Maya Lin",
                avatarUrls = listOf(Images.MAYA_CONVO),
                presence = Presence.Online,
                hasNewMoment = true,
                preview = "Haha that sounds amazing! See you at 5 ☕",
                timeLabel = "Just now",
                unreadCount = 2,
                deliveryStatus = DeliveryStatus.Read,
            ),
            Conversation(
                id = "weekend-hikers",
                title = "Weekend Hikers",
                titleEmoji = "🌲",
                avatarUrls = listOf(Images.HIKER_LIAM, Images.HIKER_WOMAN),
                sender = "Liam",
                preview = "Don't forget water bottles!",
                timeLabel = "10:42 AM",
                deliveryStatus = DeliveryStatus.Sent,
            ),
            Conversation(
                id = "david",
                title = "David Chen",
                avatarUrls = listOf(Images.DAVID),
                preview = "Sent a photo 📷",
                previewKind = PreviewKind.Photo,
                timeLabel = "Yesterday",
                deliveryStatus = DeliveryStatus.Read,
            ),
            Conversation(
                id = "chloe",
                title = "Chloe Bennett",
                avatarUrls = listOf(Images.CHLOE_CONVO),
                presence = Presence.Online,
                preview = "Voice note (0:24) 🎙️",
                previewKind = PreviewKind.VoiceNote,
                timeLabel = "Yesterday",
                unreadCount = 1,
            ),
            Conversation(
                id = "family",
                title = "Family Chat",
                titleEmoji = "❤️",
                avatarUrls = emptyList(),
                sender = "Mom",
                preview = "Have a great day sweetie!",
                timeLabel = "Oct 18",
                deliveryStatus = DeliveryStatus.Read,
            ),
        ),
        dailyPing = "Share what made you laugh today with 3 friends!",
    )

    private val mayaThread = ChatThread(
        conversationId = "maya",
        contactName = "Maya Lin",
        contactAvatarUrl = Images.MAYA_CHAT,
        presence = Presence.Online,
        statusLabel = "Online · Active now",
        isFavorite = true,
        dayLabel = "Today",
        messages = listOf(
            ChatMessage.Text("maya-1", fromMe = false, time = "2:15 PM", text = "Hey! Are we still on for coffee this afternoon? ☕"),
            ChatMessage.Text(
                "maya-2", fromMe = true, time = "2:16 PM",
                text = "Yes definitely! How about that cozy spot on 4th Street?",
                reaction = "❤️ 1",
                deliveryStatus = DeliveryStatus.Read,
            ),
            ChatMessage.VoiceMemo("maya-3", fromMe = false, time = "2:18 PM", duration = "0:18"),
            ChatMessage.Text(
                "maya-4", fromMe = true, time = "2:19 PM",
                text = "Sounds perfect, see you at 5:00! 🙌",
                deliveryStatus = DeliveryStatus.Read,
            ),
            ChatMessage.Place(
                "maya-5", fromMe = false, time = "2:20 PM",
                name = "The Roost Coffeehouse",
                details = "4th Street & Pine · 0.4 mi away",
            ),
        ),
        isContactTyping = true,
    )

    val contacts: List<Person> =
        listOf(chloe, sofia, marcus, aaliyah, julian, elena, maya, liam, sarah, alex, david)
            .sortedBy { it.name }

    val currentUser = UserProfile(
        name = "Handsome User",
        handle = "@handsome_user",
        avatarUrl = Images.SELF,
        isVerified = true,
        statusEmoji = "☕",
        statusText = "Coffee & creative work",
        moodLabel = "In a flow",
        stats = listOf(
            ProfileStat("142", "Friends"),
            ProfileStat("18", "Groups"),
            ProfileStat("342", "Media Shared"),
        ),
        settings = listOf(
            SettingItem(SettingKind.StatusMood, "Status & Mood", "Custom status, away modes, busy sync", badge = "Active"),
            SettingItem(SettingKind.Notifications, "Notifications & Sounds", "Muted chats, mellow chime tones"),
            SettingItem(SettingKind.PrivacySafety, "Privacy & Safety", "Read receipts, last seen, blocked contacts"),
            SettingItem(SettingKind.MediaLinksDocs, "Media, Links & Docs", "Recent memories with friends"),
            SettingItem(SettingKind.ChatTheme, "Chat Wallpaper & Theme", "Coral warm glow, bubble colors"),
        ),
        recentMedia = listOf(Images.MEDIA_MUG, Images.MEDIA_PLANTS, Images.MEDIA_MOODBOARD),
        moreMediaCount = 339,
        appVersion = "Freely v2.4.1",
    )

    /**
     * Maya's thread is the one designed in Stitch. Other inbox conversations open with their
     * last message, and people without a conversation yet open an empty thread.
     */
    fun chatThread(conversationId: String): ChatThread? {
        if (conversationId == mayaThread.conversationId) return mayaThread

        inbox.conversations.firstOrNull { it.id == conversationId }?.let { conversation ->
            val isGroup = conversation.avatarUrls.size != 1
            return ChatThread(
                conversationId = conversation.id,
                contactName = conversation.title,
                contactAvatarUrl = conversation.avatarUrls.firstOrNull(),
                presence = conversation.presence,
                statusLabel = if (isGroup) "Group chat" else statusLabel(conversation.presence),
                isFavorite = false,
                dayLabel = conversation.timeLabel.takeIf { "AM" !in it && "PM" !in it && it != "Just now" } ?: "Today",
                messages = listOf(conversation.toMessage()),
            )
        }

        contacts.firstOrNull { it.id == conversationId }?.let { person ->
            return ChatThread(
                conversationId = person.id,
                contactName = person.name,
                contactAvatarUrl = person.avatarUrl,
                presence = person.presence,
                statusLabel = statusLabel(person.presence),
                isFavorite = false,
                dayLabel = "Today",
                messages = emptyList(),
            )
        }
        return null
    }

    private fun statusLabel(presence: Presence) = when (presence) {
        Presence.Online -> "Online · Active now"
        Presence.Away -> "Away · Back soon"
        Presence.Offline -> "Offline"
    }

    private fun Conversation.toMessage(): ChatMessage {
        val time = if (timeLabel == "Just now" || "AM" in timeLabel || "PM" in timeLabel) timeLabel else "9:30 AM"
        return when (previewKind) {
            PreviewKind.VoiceNote -> ChatMessage.VoiceMemo(
                id = "$id-last", fromMe = false, time = time,
                duration = Regex("""\d+:\d{2}""").find(preview)?.value ?: "0:00",
            )
            else -> ChatMessage.Text(
                id = "$id-last", fromMe = false, time = time,
                text = sender?.let { "$it: $preview" } ?: preview,
            )
        }
    }
}
