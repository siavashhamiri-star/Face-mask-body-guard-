package com.example.ui.gallery

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.engine.StorageManager
import com.example.model.VideoItem
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineVideoLabScreen(
    storageManager: StorageManager,
    isPersian: Boolean,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val coroutineScope = rememberCoroutineScope()
    var savedVideos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var videoViewInstance by remember { mutableStateOf<VideoView?>(null) }

    // Lab tools state
    var showExportDialog by remember { mutableStateOf(false) }
    var exportName by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf<VideoItem?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Trim state
    var trimStartPercent by remember { mutableStateOf(0f) }
    var trimEndPercent by remember { mutableStateOf(1f) }
    var isSplitCompareMode by remember { mutableStateOf(false) }

    // Load videos
    fun reloadVideos() {
        coroutineScope.launch {
            savedVideos = storageManager.getSavedVideos()
            if (selectedVideo == null && savedVideos.isNotEmpty()) {
                selectedVideo = savedVideos.first()
            }
        }
    }

    LaunchedEffect(Unit) {
        reloadVideos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isPersian) "آزمایشگاه ویدیو آفلاین" else "Offline Video Lab",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (isPersian) "مدیریت و تدوین محلی ویدیوهای ذخیره شده" else "Local processing • Zero cloud storage",
                            color = SecurityGreen,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    Surface(
                        color = SecurityGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SecurityGreen),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SecurityGreen, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isPersian) "آفلاین" else "OFFLINE",
                                color = SecurityGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberSurface)
            )
        },
        containerColor = CyberBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Status banner if any
            if (statusMessage != null) {
                Surface(
                    color = CyberTeal.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(statusMessage ?: "", color = CyberTeal, fontSize = 12.sp)
                        IconButton(onClick = { statusMessage = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (selectedVideo != null) {
                // Video Player Area
                val currentVideo = selectedVideo!!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Color.Black)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val mc = MediaController(ctx)
                                mc.setAnchorView(this)
                                setMediaController(mc)
                                setVideoURI(Uri.parse(currentVideo.uri))
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                    isPlaying = true
                                }
                                videoViewInstance = this
                            }
                        },
                        update = { view ->
                            view.setVideoURI(Uri.parse(currentVideo.uri))
                            view.start()
                            isPlaying = true
                        }
                    )

                    // Video Lab Overlay Badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = currentVideo.name,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (isSplitCompareMode) {
                        Surface(
                            color = CyberTeal.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = if (isPersian) "حالت مقایسه فعال" else "Compare Mode Active",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Video Lab Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Export Button
                    IconButton(onClick = {
                        exportName = currentVideo.name.substringBeforeLast(".") + "_protected"
                        showExportDialog = true
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = CyberTeal)
                        }
                    }

                    // Compare Mode Toggle
                    IconButton(onClick = { isSplitCompareMode = !isSplitCompareMode }) {
                        Icon(
                            Icons.Default.Compare,
                            contentDescription = "Compare",
                            tint = if (isSplitCompareMode) CyberTeal else TextSecondary
                        )
                    }

                    // Delete Video Button (Always asks confirmation)
                    IconButton(onClick = { showDeleteConfirmDialog = currentVideo }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RecordingRed)
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(CyberSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideocamOff, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (isPersian) "هیچ ویدیویی ذخیره نشده است" else "No recordings saved yet",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isPersian) "برای ثبت اولین ویدیوی خصوصی دکمه ضبط را بزنید" else "Tap record on camera screen to create your first private video",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // List of Saved Videos
            Text(
                text = if (isPersian) "ویدیوهای ضبط شده در حافظه داخلی (${savedVideos.size}):" else "Local Device Recordings (${savedVideos.size}):",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(savedVideos) { item ->
                    val isSelected = item.id == selectedVideo?.id
                    val dateFormatted = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(Date(item.timestamp))
                    val sizeMb = String.format("%.1f MB", item.sizeBytes / (1024f * 1024f))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVideo = item }
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(CyberBackground, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isSelected) CyberTeal else TextSecondary
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "$dateFormatted • $sizeMb",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(onClick = { showDeleteConfirmDialog = item }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextTertiary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog && selectedVideo != null) {
        val target = selectedVideo!!
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    if (isPersian) "خروجی محلی ویدیو (MP4)" else "Local MP4 Export",
                    color = CyberTeal,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isPersian) "این فایل به صورت محلی در حافظه دستگاه ذخیره می‌شود و هیچ اتصالی به اینترنت برقرار نخواهد شد."
                        else "Video will be exported locally to your device storage without any internet or cloud connection.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = exportName,
                        onValueChange = { exportName = it },
                        label = { Text(if (isPersian) "نام فایل خروجی" else "Export File Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = CyberCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val exported = storageManager.exportVideo(target, exportName)
                            showExportDialog = false
                            statusMessage = if (exported != null) {
                                if (isPersian) "ویدیو با موفقیت ذخیره شد: ${exported.name}" else "Saved locally: ${exported.name}"
                            } else {
                                if (isPersian) "خطا در استخراج ویدیو" else "Export failed"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal)
                ) {
                    Text(if (isPersian) "ذخیره در حافظه" else "Export Locally", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text(if (isPersian) "انصراف" else "Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurface
        )
    }

    // Delete Confirmation Dialog (Never delete without confirmation)
    if (showDeleteConfirmDialog != null) {
        val toDelete = showDeleteConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = RecordingRed) },
            title = {
                Text(
                    if (isPersian) "تأیید حذف ویدیو" else "Confirm Permanent Deletion",
                    color = RecordingRed,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isPersian)
                        "آیا از حذف ویدیوی '${toDelete.name}' اطمینان دارید؟ به دلیل ماهیت آفلاین برنامه، این فایل از حافظه محلی حذف خواهد شد."
                    else
                        "Are you sure you want to delete '${toDelete.name}'? Since this app operates 100% offline, this file will be permanently removed from your device.",
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = storageManager.deleteVideo(toDelete)
                            showDeleteConfirmDialog = null
                            if (selectedVideo?.id == toDelete.id) {
                                selectedVideo = null
                            }
                            reloadVideos()
                            statusMessage = if (success) {
                                if (isPersian) "ویدیو حذف شد" else "Video deleted"
                            } else {
                                if (isPersian) "خطا در حذف ویدیو" else "Delete error"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RecordingRed)
                ) {
                    Text(if (isPersian) "حذف قطعی" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text(if (isPersian) "لغو" else "Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurface
        )
    }
}
