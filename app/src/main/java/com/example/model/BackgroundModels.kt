package com.example.model

import com.example.R

enum class BackgroundMode {
    ORIGINAL,
    BLUR,
    SOLID_COLOR,
    CHROMA_KEY,
    VIRTUAL_IMAGE
}

data class VirtualEnvironment(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val drawableResId: Int,
    val descriptionEn: String,
    val descriptionFa: String
)

object BundledEnvironments {
    val items = listOf(
        VirtualEnvironment(
            id = "garden",
            titleEn = "Flower Garden",
            titleFa = "باغ گل",
            drawableResId = R.drawable.bg_flower_garden,
            descriptionEn = "Vibrant outdoor garden with soft sunlight",
            descriptionFa = "باغ باطراوت با گل‌های رنگارنگ و نور ملایم"
        ),
        VirtualEnvironment(
            id = "living",
            titleEn = "Modern Living Room",
            titleFa = "اتاق نشیمن مدرن",
            drawableResId = R.drawable.bg_modern_living,
            descriptionEn = "Warm interior space with comfortable ambient light",
            descriptionFa = "فضای داخلی مدرن و دنج با نورپردازی آرامش‌بخش"
        ),
        VirtualEnvironment(
            id = "palace",
            titleEn = "Palace Interior",
            titleFa = "قصر کلاسیک",
            drawableResId = R.drawable.bg_palace_interior,
            descriptionEn = "Grand palace hall with marble pillars and chandeliers",
            descriptionFa = "تالار باشکوه با ستون‌های مرمر و شکوه کلاسیک"
        ),
        VirtualEnvironment(
            id = "office",
            titleEn = "Executive Office",
            titleFa = "دفتر کار اداری",
            drawableResId = R.drawable.bg_pro_office,
            descriptionEn = "Modern corporate executive suite with glass facade",
            descriptionFa = "محیط اداری رسمی و حرفه‌ای با نمای مدرن شهری"
        ),
        VirtualEnvironment(
            id = "studio",
            titleEn = "Neutral Studio",
            titleFa = "استودیو خاکستری",
            drawableResId = R.drawable.bg_neutral_studio,
            descriptionEn = "Minimalist photography studio backdrop with soft light",
            descriptionFa = "پس‌زمینه عکاسی مینیمال با نور موضعی ملایم"
        ),
        VirtualEnvironment(
            id = "nature",
            titleEn = "Nature Landscape",
            titleFa = "طبیعت و کوهستان",
            drawableResId = R.drawable.bg_nature_lake,
            descriptionEn = "Tranquil mountain lake surrounded by pine forest",
            descriptionFa = "منظره دریاچه کوهستانی آرام و جنگل کاج"
        )
    )
}

data class BackgroundConfig(
    val mode: BackgroundMode = BackgroundMode.ORIGINAL,
    val solidColor: Long = 0xFF0F172A,
    val selectedEnvId: String = "studio",
    val customImageUri: String? = null,
    val blurRadius: Float = 16f,
    // Chroma key settings
    val chromaKeyHue: Float = 120f, // 120 is green, 240 is blue
    val chromaTolerance: Float = 0.35f,
    val chromaSoftness: Float = 0.15f
)
