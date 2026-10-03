package com.example.model

import android.graphics.RectF

enum class BlurType {
    NONE,
    PIXELATE,
    GAUSSIAN
}

enum class PrivacyMaskType {
    NONE,
    FULL_SHIELD,       // Full face dark protective shield
    EYES_VISOR,        // Sleek black sensor / privacy bar across eyes
    MOUTH_GUARD,       // Anonymizing mouth and chin bar
    ANONYMOUS_HOOD,    // Dark silhouette shroud covering head and neck
    CYBER_NEON,        // Glowing cyber-grid matrix mask
    VENETIAN_LINES     // Slotted horizontal privacy blinds
}

data class TrackedFace(
    val id: Int,
    val bounds: RectF, // Normalized 0f..1f (left, top, right, bottom)
    val confidence: Float = 0.95f,
    val eyeCenterY: Float = 0.35f,
    val mouthCenterY: Float = 0.72f,
    val smoothedBounds: RectF = bounds
)

data class ManualPrivacyZone(
    val id: String = java.util.UUID.randomUUID().toString(),
    val bounds: RectF,
    val blurType: BlurType = BlurType.PIXELATE
)

data class PrivacyConfig(
    val blurType: BlurType = BlurType.PIXELATE,
    val blurIntensity: Float = 0.8f,
    val pixelBlockSize: Int = 24, // in pixels
    val maskType: PrivacyMaskType = PrivacyMaskType.EYES_VISOR,
    val autoFaceTracking: Boolean = true,
    val multiFaceEnabled: Boolean = true,
    val safetyMarginMultiplier: Float = 1.35f, // Dynamic safety expansion if confidence dips
    val manualZones: List<ManualPrivacyZone> = emptyList()
)
