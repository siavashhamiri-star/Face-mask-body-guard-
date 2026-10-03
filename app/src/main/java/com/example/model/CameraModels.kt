package com.example.model

enum class PerformancePreset(
    val labelEn: String,
    val labelFa: String,
    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val fps: Int,
    val recommendedForRedmi: Boolean
) {
    LOW_REDMI("Redmi Guard (720p/24fps)", "حالت بهینه ردمی (720p/24fps)", 1280, 720, 24, true),
    BALANCED("Balanced (720p/30fps)", "متعادل (720p/30fps)", 1280, 720, 30, false),
    HIGH("High Quality (1080p/30fps)", "کیفیت بالا (1080p/30fps)", 1920, 1080, 30, false)
}

enum class ActiveStudioTab {
    NONE,
    PRIVACY,
    BACKGROUND,
    STYLE,
    BODY,
    VOICE,
    SETTINGS
}

data class VideoItem(
    val id: String,
    val uri: String,
    val absolutePath: String,
    val name: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val timestamp: Long,
    val appliedPrivacySummary: String,
    val isProcessed: Boolean = false
)
