package com.season.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.season.model.DisplayFileItem
import com.season.model.DisplayImageItem
import com.season.model.FileFolder
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FileRepository {

    suspend fun getImageFolders(): List<FileFolder>

    suspend fun getImagesInFolder(path: String?): List<DisplayFileItem>

    suspend fun startCameraSession()

    suspend fun cacheCapturedImage(cameraSessionId: String, bitmap: Bitmap, rotation: Int)

    suspend fun getImagesBySessionId(sessionId: String): List<Uri>

    suspend fun getImageByName(fileName: String): File?

    suspend fun getImageBitmapByName(fileName: String): Bitmap?

    fun observeAllImagesInSession(
        sessionId: String,
        events: List<Int>
    ): Flow<List<DisplayImageItem>>

    suspend fun overrideCroppedImageFile(fileName: String, bitmap: Bitmap)

    suspend fun overrideEditedImageFile(sourceUri: Uri, destinationUri: Uri)

    suspend fun generatePdfFile(sessionId: String, isMarginOn: Boolean): File?

    suspend fun generatePdfRendererPages(pdfFile: File): List<Bitmap>

    suspend fun deleteCacheImage(fileName: String)

    suspend fun getSharePdfUri(filePath: String) : Uri

}
