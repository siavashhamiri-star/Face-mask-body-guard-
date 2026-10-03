package com.example.engine

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StorageManager(private val context: Context) {

    private val videosDir: File
        get() {
            val dir = File(context.getExternalFilesDir(null), "faceguard_videos")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

    fun createNewVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(videosDir, "FaceGuard_$timeStamp.mp4")
    }

    suspend fun getSavedVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val files = videosDir.listFiles { file -> file.extension.equals("mp4", ignoreCase = true) }
            ?: return@withContext emptyList()

        files.sortedByDescending { it.lastModified() }.map { file ->
            var duration = 0L
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(file.absolutePath)
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                duration = durationStr?.toLongOrNull() ?: 0L
                retriever.release()
            } catch (_: Exception) {
            }

            VideoItem(
                id = file.name,
                uri = Uri.fromFile(file).toString(),
                absolutePath = file.absolutePath,
                name = file.name,
                durationMs = duration,
                sizeBytes = file.length(),
                timestamp = file.lastModified(),
                appliedPrivacySummary = "Encrypted Local • Offline Guard",
                isProcessed = false
            )
        }
    }

    suspend fun deleteVideo(videoItem: VideoItem): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(videoItem.absolutePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun exportVideo(videoItem: VideoItem, targetName: String): File? = withContext(Dispatchers.IO) {
        try {
            val source = File(videoItem.absolutePath)
            if (!source.exists()) return@withContext null

            val exportDir = File(context.getExternalFilesDir(null), "faceguard_exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val cleanName = if (targetName.endsWith(".mp4", ignoreCase = true)) targetName else "$targetName.mp4"
            val dest = File(exportDir, cleanName)
            source.copyTo(dest, overwrite = true)
            dest
        } catch (e: Exception) {
            null
        }
    }

    fun getTotalStorageUsedBytes(): Long {
        return videosDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
}
