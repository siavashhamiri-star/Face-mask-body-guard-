package com.example.ui.camera

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ActiveStudioTab
import com.example.ui.dialogs.PrivacyAuditDialog
import com.example.ui.overlay.PrivacyCameraOverlay
import com.example.ui.panels.*
import com.example.ui.theme.*
import com.example.viewmodel.FaceGuardViewModel

@Composable
fun CameraHomeScreen(
    viewModel: FaceGuardViewModel,
    onNavigateToGallery: () -> Unit
) {
    val privacyConfig by viewModel.privacyConfig.collectAsStateWithLifecycle()
    val bgConfig by viewModel.backgroundConfig.collectAsStateWithLifecycle()
    val faceStyleConfig by viewModel.faceStyleConfig.collectAsStateWithLifecycle()
    val silhouetteConfig by viewModel.silhouetteConfig.collectAsStateWithLifecycle()
    val voiceConfig by viewModel.voiceConfig.collectAsStateWithLifecycle()
    val performancePreset by viewModel.performancePreset.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val trackedFaces by viewModel.trackedFaces.collectAsStateWithLifecycle()
    val cameraSelector by viewModel.cameraSelector.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val durationSec by viewModel.recordingDurationSec.collectAsStateWithLifecycle()
    val isPersian by viewModel.isPersian.collectAsStateWithLifecycle()
    val showAudit by viewModel.showPrivacyAudit.collectAsStateWithLifecycle()
    val statusMsg by viewModel.statusMessage.collectAsStateWithLifecycle()

    var overlayWidth by remember { mutableStateOf(1080f) }
    var overlayHeight by remember { mutableStateOf(1920f) }

    // Pulsating animation for recording indicator
    val infiniteTransition = rememberInfiniteTransition(label = "rec_pulse")
    val recPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rec_scale"
    )

    // Build CameraX VideoCapture once
    val videoCapture = remember(performancePreset) {
        viewModel.recordingEngine.buildVideoCapture(performancePreset)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .onGloballyPositioned { coords ->
                overlayWidth = coords.size.width.toFloat()
                overlayHeight = coords.size.height.toFloat()
            }
    ) {
        // 1. CameraX Preview Layer
        CameraPreviewView(
            cameraSelector = cameraSelector,
            videoCapture = videoCapture,
            faceDetectionEngine = viewModel.faceDetectionEngine,
            onFacesUpdated = { viewModel.onFacesDetected(it) },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Privacy & Visual Overlay Layer (Face blur, Privacy masks, Chroma key)
        PrivacyCameraOverlay(
            trackedFaces = trackedFaces,
            privacyConfig = privacyConfig,
            backgroundConfig = bgConfig,
            faceStyleConfig = faceStyleConfig,
            isPersian = isPersian,
            onTapAddManualZone = { offset ->
                viewModel.addManualPrivacyZone(offset, overlayWidth, overlayHeight)
            },
            modifier = Modifier.fillMaxSize()
        )

        // 3. Top HUD Bar (Privacy Badge, Faces count, Perf profile, Language toggle)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(CyberBackground.copy(alpha = 0.90f), Color.Transparent)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 100% OFFLINE Seal Badge
                Surface(
                    color = CyberSurface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SecurityGreen),
                    modifier = Modifier.clickable { viewModel.setShowPrivacyAudit(true) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SecurityGreen)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isPersian) "۱۰۰٪ آفلاین • امنیت محض" else "100% OFFLINE • AIR-GAPPED",
                            color = SecurityGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Language Switcher (FA / EN) & Camera Flip
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Language Switcher Button
                    Surface(
                        color = CyberSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                        modifier = Modifier.clickable { viewModel.toggleLanguage() }
                    ) {
                        Text(
                            text = if (isPersian) "EN" else "فارسی",
                            color = CyberTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    // Flip Camera Button
                    IconButton(
                        onClick = { viewModel.toggleCameraFacing() },
                        modifier = Modifier
                            .size(38.dp)
                            .background(CyberSurface.copy(alpha = 0.85f), CircleShape)
                            .border(1.dp, CyberCardBorder, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.FlipCameraAndroid,
                            contentDescription = "Switch Camera",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Secondary Info Strip (Faces protected count + Recording Status timer)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Faces protected indicator
                Surface(
                    color = CyberSurfaceVariant.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isPersian)
                                "چهره‌های تحت حفاظت: ${if (privacyConfig.autoFaceTracking) trackedFaces.size else 0}"
                            else
                                "Protected Faces: ${if (privacyConfig.autoFaceTracking) trackedFaces.size else 0}",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Recording timer readout
                if (isRecording) {
                    val minutes = durationSec / 60
                    val seconds = durationSec % 60
                    val timerStr = String.format("%02d:%02d", minutes, seconds)
                    Surface(
                        color = RecordingRed.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(recPulseScale)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "REC $timerStr",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Notification Snackbar if any
        if (statusMsg != null) {
            Surface(
                color = CyberSurface.copy(alpha = 0.95f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberTeal),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 110.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(statusMsg ?: "", color = TextPrimary, fontSize = 12.sp)
                }
            }
        }

        // 4. Studio Control Panel Drawer (Collapsible bottom panel)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Expandable Studio Tab Content
            AnimatedVisibility(
                visible = activeTab != ActiveStudioTab.NONE,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = CyberSurface.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                ) {
                    when (activeTab) {
                        ActiveStudioTab.PRIVACY -> PrivacyStudioPanel(
                            privacyConfig = privacyConfig,
                            isPersian = isPersian,
                            onUpdate = { viewModel.updatePrivacyConfig(it) },
                            onClearManualZones = { viewModel.clearManualZones() }
                        )
                        ActiveStudioTab.BACKGROUND -> BackgroundStudioPanel(
                            bgConfig = bgConfig,
                            isPersian = isPersian,
                            onUpdate = { viewModel.updateBackgroundConfig(it) }
                        )
                        ActiveStudioTab.STYLE -> FaceStyleStudioPanel(
                            styleConfig = faceStyleConfig,
                            isPersian = isPersian,
                            onUpdate = { viewModel.updateFaceStyleConfig(it) }
                        )
                        ActiveStudioTab.BODY -> BodySilhouetteStudioPanel(
                            silhouetteConfig = silhouetteConfig,
                            isPersian = isPersian,
                            onUpdate = { viewModel.updateSilhouetteConfig(it) }
                        )
                        ActiveStudioTab.VOICE -> VoiceStudioPanel(
                            voiceConfig = voiceConfig,
                            isPersian = isPersian,
                            onUpdate = { viewModel.updateVoiceConfig(it) }
                        )
                        ActiveStudioTab.SETTINGS -> SettingsPerformancePanel(
                            preset = performancePreset,
                            isPersian = isPersian,
                            onPresetChange = { viewModel.setPerformancePreset(it) },
                            onShowPrivacyAudit = { viewModel.setShowPrivacyAudit(true) }
                        )
                        ActiveStudioTab.NONE -> {}
                    }
                }
            }

            // Bottom Bar: Studio Tab Selectors + Giant Shutter Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, CyberBackground.copy(alpha = 0.95f))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Studio Tab Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        Triple(ActiveStudioTab.PRIVACY, if (isPersian) "حریم خصوصی" else "Privacy", Icons.Default.Shield),
                        Triple(ActiveStudioTab.BACKGROUND, if (isPersian) "پس‌زمینه" else "Background", Icons.Default.Landscape),
                        Triple(ActiveStudioTab.STYLE, if (isPersian) "استایل" else "Style", Icons.Default.AutoAwesome),
                        Triple(ActiveStudioTab.BODY, if (isPersian) "اندام" else "Body", Icons.Default.AccessibilityNew),
                        Triple(ActiveStudioTab.VOICE, if (isPersian) "صدا" else "Voice", Icons.Default.Mic),
                        Triple(ActiveStudioTab.SETTINGS, if (isPersian) "تنظیمات" else "Settings", Icons.Default.Tune)
                    )

                    tabs.forEach { (tab, title, icon) ->
                        val isSelected = activeTab == tab
                        Surface(
                            color = if (isSelected) CyberTeal else CyberSurface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyberTeal else CyberCardBorder
                            ),
                            modifier = Modifier
                                .clickable { viewModel.setActiveTab(tab) }
                                .padding(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = if (isSelected) Color.Black else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Shutter / Record Row (Giant center button, Gallery shortcut, Stop/Rec)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery Shortcut Button
                    IconButton(
                        onClick = onNavigateToGallery,
                        modifier = Modifier
                            .size(52.dp)
                            .background(CyberSurface.copy(alpha = 0.9f), CircleShape)
                            .border(1.5.dp, CyberCardBorder, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = "Gallery",
                            tint = CyberTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Giant Shutter Record Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clickable { viewModel.toggleRecording() },
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer Pulsating Border Ring
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(
                                    width = 4.dp,
                                    color = if (isRecording) RecordingRed else CyberTeal,
                                    shape = CircleShape
                                )
                        )
                        // Inner Core
                        Box(
                            modifier = Modifier
                                .size(if (isRecording) 28.dp else 56.dp)
                                .clip(if (isRecording) RoundedCornerShape(6.dp) else CircleShape)
                                .background(if (isRecording) RecordingRed else Color.White)
                        )
                    }

                    // Quick Privacy Mode Toggle (Full Face Shield vs Normal)
                    IconButton(
                        onClick = {
                            val nextMask = if (privacyConfig.maskType == com.example.model.PrivacyMaskType.NONE) {
                                com.example.model.PrivacyMaskType.EYES_VISOR
                            } else {
                                com.example.model.PrivacyMaskType.NONE
                            }
                            viewModel.updatePrivacyConfig(privacyConfig.copy(maskType = nextMask))
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .background(CyberSurface.copy(alpha = 0.9f), CircleShape)
                            .border(
                                1.5.dp,
                                if (privacyConfig.maskType != com.example.model.PrivacyMaskType.NONE) SecurityGreen else CyberCardBorder,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (privacyConfig.maskType != com.example.model.PrivacyMaskType.NONE) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Mask",
                            tint = if (privacyConfig.maskType != com.example.model.PrivacyMaskType.NONE) SecurityGreen else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    // Privacy Audit Dialog
    if (showAudit) {
        PrivacyAuditDialog(
            isPersian = isPersian,
            onDismiss = { viewModel.setShowPrivacyAudit(false) }
        )
    }
}
