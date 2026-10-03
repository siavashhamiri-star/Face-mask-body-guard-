package com.example.model

enum class PitchProfile(val titleEn: String, val titleFa: String, val pitchFactor: Float) {
    NATURAL("Natural Audio", "صدای طبیعی", 1.0f),
    DEEP_GUARD("Deep Guard (Privacy)", "صدای بم حفاظتی", 0.82f),
    WARM_STUDIO("Warm Studio Radio", "استودیو رادیویی گرم", 0.92f),
    SOFT_VOICE("Soft Air Profile", "صدای ملایم و نرم", 1.15f),
    CHARACTER_CYBER("Cyber Anonymous", "ناشناس دیجیتال", 0.75f)
}

data class VoiceConfig(
    val pitchProfile: PitchProfile = PitchProfile.NATURAL,
    val micGain: Float = 1.0f,
    val noiseReduction: Boolean = true,
    val syncOffsetMs: Int = 0, // Calibration for audio/video sync
    val privacySealEn: String = "Zero Cloud Audio: Microphone stream never uploaded",
    val privacySealFa: String = "صدای آفلاین: صدای میکروفون هرگز ذخیره ابری یا ارسال نمی‌شود"
)
