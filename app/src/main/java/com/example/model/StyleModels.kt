package com.example.model

enum class FilterPreset(val titleEn: String, val titleFa: String) {
    NATURAL("Natural Clean", "طبیعی و شفاف"),
    SMOOTH_WARM("Warm Smoothing", "پوست لطیف و گرم"),
    COOL_CYBER("Cool Cyber Teal", "سایبر نئون"),
    NOIR_BW("Film Noir B&W", "سیاه و سفید سینمایی"),
    VINTAGE_SEPIA("Vintage Sepia", "سپیا کلاسیک"),
    VIVID_CONTRAST("Vivid Privacy", "کنتراست بالا"),
    AVATAR_GLOW("Stylized Avatar", "آواتار دیجیتال")
}

data class FaceStyleConfig(
    val preset: FilterPreset = FilterPreset.NATURAL,
    val skinSmoothing: Float = 0.4f,
    val warmth: Float = 0.0f,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f,
    val showAlterationBadge: Boolean = true // Ethical AI/Privacy watermark
)

data class BodySilhouetteConfig(
    val enabled: Boolean = false,
    val waistContour: Float = 0.0f,      // -0.5f (slender) to +0.5f (wider)
    val shoulderContour: Float = 0.0f,   // -0.5f to +0.5f
    val overallScale: Float = 0.0f,      // -0.2f to +0.2f
    val intensity: Float = 0.5f,
    val isExperimental: Boolean = true,
    val limitationNoteEn: String = "Experimental local post-processing - low memory profile for Redmi Note 8",
    val limitationNoteFa: String = "قابلیت تجربی پردازش آفلاین محلی - بهینه‌سازی شده برای حافظه ردمی نوت ۸"
)
