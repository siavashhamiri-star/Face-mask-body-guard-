package com.example.ui.panels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun PrivacyStudioPanel(
    privacyConfig: PrivacyConfig,
    isPersian: Boolean,
    onUpdate: (PrivacyConfig) -> Unit,
    onClearManualZones: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "حفاظت و تار کردن چهره" else "Face Privacy & Blurring",
                style = MaterialTheme.typography.titleMedium,
                color = CyberTeal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isPersian) "پردازش ۱۰۰٪ محلی" else "100% On-Device",
                style = MaterialTheme.typography.labelSmall,
                color = SecurityGreen
            )
        }

        // Blur Type Selection
        Text(
            text = if (isPersian) "حالت تارکننده چهره:" else "Face Obfuscation Mode:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BlurType.values().forEach { type ->
                val selected = privacyConfig.blurType == type
                val label = when (type) {
                    BlurType.NONE -> if (isPersian) "غیرفعال" else "Off"
                    BlurType.PIXELATE -> if (isPersian) "موزاییکی (پیکسل)" else "Pixelate"
                    BlurType.GAUSSIAN -> if (isPersian) "مات و بلر" else "Gaussian"
                }
                FilterChip(
                    selected = selected,
                    onClick = { onUpdate(privacyConfig.copy(blurType = type)) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberTeal,
                        selectedLabelColor = Color.Black,
                        containerColor = CyberSurfaceVariant,
                        labelColor = TextPrimary
                    ),
                    modifier = Modifier.height(44.dp)
                )
            }
        }

        // Pixel Size Slider
        if (privacyConfig.blurType == BlurType.PIXELATE) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (isPersian) "اندازه بلوک‌های پیکسل:" else "Pixel Block Size:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        "${privacyConfig.pixelBlockSize} px",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTeal
                    )
                }
                Slider(
                    value = privacyConfig.pixelBlockSize.toFloat(),
                    onValueChange = { onUpdate(privacyConfig.copy(pixelBlockSize = it.toInt())) },
                    valueRange = 12f..56f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberTeal,
                        activeTrackColor = CyberTeal
                    )
                )
            }
        }

        // Blur Intensity Slider
        if (privacyConfig.blurType != BlurType.NONE) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (isPersian) "شدت پوشش حریم خصوصی:" else "Privacy Density:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        "${(privacyConfig.blurIntensity * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTeal
                    )
                }
                Slider(
                    value = privacyConfig.blurIntensity,
                    onValueChange = { onUpdate(privacyConfig.copy(blurIntensity = it)) },
                    valueRange = 0.2f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberTeal,
                        activeTrackColor = CyberTeal
                    )
                )
            }
        }

        // Privacy Masks
        Divider(color = CyberCardBorder)
        Text(
            text = if (isPersian) "ماسک‌های امنیتی چهره:" else "Security Privacy Masks:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(PrivacyMaskType.values()) { mask ->
                val selected = privacyConfig.maskType == mask
                val name = when (mask) {
                    PrivacyMaskType.NONE -> if (isPersian) "بدون ماسک" else "None"
                    PrivacyMaskType.EYES_VISOR -> if (isPersian) "نوار چشم (سنسور)" else "Eyes Visor"
                    PrivacyMaskType.FULL_SHIELD -> if (isPersian) "سپر کامل صورت" else "Full Shield"
                    PrivacyMaskType.MOUTH_GUARD -> if (isPersian) "ماسک دهان" else "Mouth Guard"
                    PrivacyMaskType.ANONYMOUS_HOOD -> if (isPersian) "سیلوئت ناشناس" else "Silhouette"
                    PrivacyMaskType.CYBER_NEON -> if (isPersian) "ماتریکس نئون" else "Cyber Grid"
                    PrivacyMaskType.VENETIAN_LINES -> if (isPersian) "کرکره امنیتی" else "Venetian"
                }
                Card(
                    modifier = Modifier
                        .clickable { onUpdate(privacyConfig.copy(maskType = mask)) }
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) CyberTeal else CyberCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) CyberSurfaceVariant else CyberSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = name,
                            color = if (selected) CyberTeal else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Safety Margin Multiplier
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isPersian) "حاشیه احتیاطی پوشش (جلوگیری از نشتی):" else "Safety Boundary Guard:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = "${String.format("%.2f", privacyConfig.safetyMarginMultiplier)}x",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecurityGreen
                )
            }
            Slider(
                value = privacyConfig.safetyMarginMultiplier,
                onValueChange = { onUpdate(privacyConfig.copy(safetyMarginMultiplier = it)) },
                valueRange = 1.0f..2.0f,
                colors = SliderDefaults.colors(
                    thumbColor = SecurityGreen,
                    activeTrackColor = SecurityGreen
                )
            )
            Text(
                text = if (isPersian)
                    "در صورت تکان سریع سر، این حاشیه به صورت خودکار چهره را کاملاً می‌پوشاند تا هویت افشا نشود."
                else
                    "Expands the protective mask zone dynamically to ensure no facial edges are revealed during motion.",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }

        // Multi-face tracking toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPersian) "پوشش همزمان چندین چهره" else "Multi-Face Protection",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = if (isPersian) "تار کردن تمامی چهره‌های موجود در کادر" else "Protect all detected faces in viewfinder",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = privacyConfig.multiFaceEnabled,
                onCheckedChange = { onUpdate(privacyConfig.copy(multiFaceEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberTeal,
                    checkedTrackColor = CyberSurfaceVariant
                )
            )
        }

        // Manual Zones count and clear
        if (privacyConfig.manualZones.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPersian) "نواحی دستی اضافه شده: ${privacyConfig.manualZones.size}" else "Manual Custom Zones: ${privacyConfig.manualZones.size}",
                    color = WarningAmber,
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = onClearManualZones) {
                    Text(if (isPersian) "حذف نواحی دستی" else "Clear Manual Zones", color = RecordingRed)
                }
            }
        }
    }
}

@Composable
fun BackgroundStudioPanel(
    bgConfig: BackgroundConfig,
    isPersian: Boolean,
    onUpdate: (BackgroundConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "استودیو پس‌زمینه و پرده سبز" else "Virtual Background & Chroma Key",
                style = MaterialTheme.typography.titleMedium,
                color = CyberTeal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isPersian) "بدون نیاز به اینترنت" else "100% Offline",
                style = MaterialTheme.typography.labelSmall,
                color = SecurityGreen
            )
        }

        // Background Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackgroundMode.values().forEach { mode ->
                val selected = bgConfig.mode == mode
                val label = when (mode) {
                    BackgroundMode.ORIGINAL -> if (isPersian) "اصلی" else "Original"
                    BackgroundMode.BLUR -> if (isPersian) "بلر پس‌زمینه" else "Blur BG"
                    BackgroundMode.SOLID_COLOR -> if (isPersian) "تک‌رنگ استودیو" else "Solid Color"
                    BackgroundMode.CHROMA_KEY -> if (isPersian) "پرده سبز" else "Chroma Key"
                    BackgroundMode.VIRTUAL_IMAGE -> if (isPersian) "محیط‌های مجازی" else "Virtual Sets"
                }
                FilterChip(
                    selected = selected,
                    onClick = { onUpdate(bgConfig.copy(mode = mode)) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberTeal,
                        selectedLabelColor = Color.Black,
                        containerColor = CyberSurfaceVariant,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        // Virtual Environments Gallery
        if (bgConfig.mode == BackgroundMode.VIRTUAL_IMAGE || bgConfig.mode == BackgroundMode.CHROMA_KEY) {
            Text(
                text = if (isPersian) "محیط‌های پیش‌فرض همراه برنامه:" else "Bundled Virtual Environments:",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(BundledEnvironments.items) { env ->
                    val isSelected = bgConfig.selectedEnvId == env.id
                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .clickable { onUpdate(bgConfig.copy(selectedEnvId = env.id)) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) CyberTeal else CyberCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = env.drawableResId),
                                contentDescription = env.titleEn,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isPersian) env.titleFa else env.titleEn,
                                    color = if (isSelected) CyberTeal else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (isPersian) env.descriptionFa else env.descriptionEn,
                                    color = TextTertiary,
                                    fontSize = 9.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chroma Key Fine-tuning Sliders
        if (bgConfig.mode == BackgroundMode.CHROMA_KEY) {
            Divider(color = CyberCardBorder)
            Text(
                text = if (isPersian) "تنظیمات پرده سبز (کروماکی):" else "Chroma Key Calibration:",
                style = MaterialTheme.typography.bodyMedium,
                color = SecurityGreen
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isPersian) "میزان حساسیت رنگ سبز:" else "Color Tolerance:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("${(bgConfig.chromaTolerance * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = SecurityGreen)
                }
                Slider(
                    value = bgConfig.chromaTolerance,
                    onValueChange = { onUpdate(bgConfig.copy(chromaTolerance = it)) },
                    valueRange = 0.1f..0.8f,
                    colors = SliderDefaults.colors(thumbColor = SecurityGreen, activeTrackColor = SecurityGreen)
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isPersian) "نرمی حاشیه‌ها (حفظ مو و بدن):" else "Edge Softness:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("${(bgConfig.chromaSoftness * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = SecurityGreen)
                }
                Slider(
                    value = bgConfig.chromaSoftness,
                    onValueChange = { onUpdate(bgConfig.copy(chromaSoftness = it)) },
                    valueRange = 0.05f..0.4f,
                    colors = SliderDefaults.colors(thumbColor = SecurityGreen, activeTrackColor = SecurityGreen)
                )
            }
        }
    }
}

@Composable
fun FaceStyleStudioPanel(
    styleConfig: FaceStyleConfig,
    isPersian: Boolean,
    onUpdate: (FaceStyleConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "استایل چهره و فیلترهای بصری" else "Face Style & Visual Filters",
                style = MaterialTheme.typography.titleMedium,
                color = CyberTeal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isPersian) "غیرمخرب • قابل بازگشت" else "Non-destructive",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        // Filter Presets
        Text(
            text = if (isPersian) "فیلتر رنگی و تم هنری:" else "Artistic Look / Filter:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(FilterPreset.values()) { preset ->
                val selected = styleConfig.preset == preset
                Card(
                    modifier = Modifier
                        .clickable { onUpdate(styleConfig.copy(preset = preset)) }
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) CyberTeal else CyberCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) CyberSurfaceVariant else CyberSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isPersian) preset.titleFa else preset.titleEn,
                            color = if (selected) CyberTeal else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Skin Smoothing Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (isPersian) "لطافت پوست (Skin Smoothing):" else "Skin Smoothing:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("${(styleConfig.skinSmoothing * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = CyberTeal)
            }
            Slider(
                value = styleConfig.skinSmoothing,
                onValueChange = { onUpdate(styleConfig.copy(skinSmoothing = it)) },
                valueRange = 0f..1.0f,
                colors = SliderDefaults.colors(thumbColor = CyberTeal, activeTrackColor = CyberTeal)
            )
        }

        // Warmth / Coolness Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (isPersian) "تن گرم/سرد (Warmth):" else "Warmth / Coolness:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(String.format("%.1f", styleConfig.warmth), style = MaterialTheme.typography.bodySmall, color = WarningAmber)
            }
            Slider(
                value = styleConfig.warmth,
                onValueChange = { onUpdate(styleConfig.copy(warmth = it)) },
                valueRange = -1.0f..1.0f,
                colors = SliderDefaults.colors(thumbColor = WarningAmber, activeTrackColor = WarningAmber)
            )
        }

        // Alteration Label Watermark Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPersian) "نشانگر شفافیت تغییر دیجیتالی" else "Digitally Altered Badge",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = if (isPersian) "نمایش برچسب اخلاقی هوش مصنوعی و حفظ حریم خصوصی" else "Transparently labels digital transformation",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = styleConfig.showAlterationBadge,
                onCheckedChange = { onUpdate(styleConfig.copy(showAlterationBadge = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberTeal,
                    checkedTrackColor = CyberSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun BodySilhouetteStudioPanel(
    silhouetteConfig: BodySilhouetteConfig,
    isPersian: Boolean,
    onUpdate: (BodySilhouetteConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "استودیو فرم اندام (سیلوئت)" else "Body Silhouette Studio",
                style = MaterialTheme.typography.titleMedium,
                color = WarningAmber,
                fontWeight = FontWeight.Bold
            )
            // Explicit Experimental Badge
            Surface(
                color = WarningAmber.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber)
            ) {
                Text(
                    text = if (isPersian) "آفلاین • تجربی" else "Offline • Experimental",
                    color = WarningAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Redmi Note 8 Engineering Limitation Notice
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = WarningAmber)
                Text(
                    text = if (isPersian) silhouetteConfig.limitationNoteFa else silhouetteConfig.limitationNoteEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "فعال‌سازی افکت فرم اندام" else "Enable Silhouette Effect",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Switch(
                checked = silhouetteConfig.enabled,
                onCheckedChange = { onUpdate(silhouetteConfig.copy(enabled = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = WarningAmber)
            )
        }

        if (silhouetteConfig.enabled) {
            // Waist Contour
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isPersian) "تنظیم کانتور دور کمر:" else "Waist Contour:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("${(silhouetteConfig.waistContour * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = WarningAmber)
                }
                Slider(
                    value = silhouetteConfig.waistContour,
                    onValueChange = { onUpdate(silhouetteConfig.copy(waistContour = it)) },
                    valueRange = -0.5f..0.5f,
                    colors = SliderDefaults.colors(thumbColor = WarningAmber, activeTrackColor = WarningAmber)
                )
            }

            // Shoulder Contour
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isPersian) "تنظیم کانتور شانه‌ها:" else "Shoulder Contour:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("${(silhouetteConfig.shoulderContour * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = WarningAmber)
                }
                Slider(
                    value = silhouetteConfig.shoulderContour,
                    onValueChange = { onUpdate(silhouetteConfig.copy(shoulderContour = it)) },
                    valueRange = -0.5f..0.5f,
                    colors = SliderDefaults.colors(thumbColor = WarningAmber, activeTrackColor = WarningAmber)
                )
            }

            Button(
                onClick = { onUpdate(silhouetteConfig.copy(waistContour = 0f, shoulderContour = 0f, overallScale = 0f)) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isPersian) "بازنشانی تنظیمات فرم اندام" else "Reset Silhouette Parameters", color = TextPrimary)
            }
        }
    }
}

@Composable
fun VoiceStudioPanel(
    voiceConfig: VoiceConfig,
    isPersian: Boolean,
    onUpdate: (VoiceConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "استودیو صدای خصوصی" else "Private Voice Studio",
                style = MaterialTheme.typography.titleMedium,
                color = SecurityGreen,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = SecurityGreen.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SecurityGreen)
            ) {
                Text(
                    text = if (isPersian) "بدون ارسال صوت به سرور" else "Zero Audio Upload",
                    color = SecurityGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Voice Profile
        Text(
            text = if (isPersian) "پروفایل تغییر تن صدا:" else "Voice Persona Profile:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(PitchProfile.values()) { profile ->
                val selected = voiceConfig.pitchProfile == profile
                Card(
                    modifier = Modifier
                        .clickable { onUpdate(voiceConfig.copy(pitchProfile = profile)) }
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) SecurityGreen else CyberCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) CyberSurfaceVariant else CyberSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isPersian) profile.titleFa else profile.titleEn,
                            color = if (selected) SecurityGreen else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Mic Gain Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (isPersian) "تقویت صدای میکروفون (Gain):" else "Microphone Gain:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("${String.format("%.1f", voiceConfig.micGain)}x", style = MaterialTheme.typography.bodySmall, color = SecurityGreen)
            }
            Slider(
                value = voiceConfig.micGain,
                onValueChange = { onUpdate(voiceConfig.copy(micGain = it)) },
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(thumbColor = SecurityGreen, activeTrackColor = SecurityGreen)
            )
        }

        // Noise Reduction Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPersian) "حذف نویز محیطی (Noise Gate)" else "Offline Noise Suppression",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = if (isPersian) "فیلتر نویز ایزوله بدون ارسال هیچ سیگنالی به اینترنت" else "Clean vocal clarity without sending audio to cloud",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = voiceConfig.noiseReduction,
                onCheckedChange = { onUpdate(voiceConfig.copy(noiseReduction = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = SecurityGreen)
            )
        }
    }
}

@Composable
fun SettingsPerformancePanel(
    preset: PerformancePreset,
    isPersian: Boolean,
    onPresetChange: (PerformancePreset) -> Unit,
    onShowPrivacyAudit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isPersian) "تنظیمات عملکرد و سخت‌افزار (Redmi Note 8)" else "Performance & Hardware Profile",
            style = MaterialTheme.typography.titleMedium,
            color = CyberTeal,
            fontWeight = FontWeight.Bold
        )

        // Presets List
        PerformancePreset.values().forEach { item ->
            val isSelected = preset == item
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPresetChange(item) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) CyberTeal else CyberCardBorder,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CyberSurfaceVariant else CyberSurface
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isPersian) item.labelFa else item.labelEn,
                                color = if (isSelected) CyberTeal else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (item.recommendedForRedmi) {
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    color = SecurityGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isPersian) "توصیه ردمی نوت ۸" else "Redmi Optimal",
                                        color = SecurityGreen,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${item.resolutionWidth}x${item.resolutionHeight} @ ${item.fps} FPS",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { onPresetChange(item) },
                        colors = RadioButtonDefaults.colors(selectedColor = CyberTeal)
                    )
                }
            }
        }

        // Privacy Audit Button
        OutlinedButton(
            onClick = onShowPrivacyAudit,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, SecurityGreen)
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SecurityGreen)
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isPersian) "ممیزی حریم خصوصی و عدم دسترسی به اینترنت" else "Inspect Offline Privacy Seal",
                color = SecurityGreen
            )
        }
    }
}
