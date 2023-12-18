package com.season.filestorage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment.*
import android.os.FileObserver
import androidx.core.content.FileProvider
import com.season.common.util.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named


private const val DIRECTORY_CAMERA_SESSION = "camera_session"

class MondayFileStorageDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("CacheDir") private val cacheDir: File
) {

    private val cameraDirectory = File(cacheDir.absolutePath, DIRECTORY_CAMERA_SESSION)

    init {
        if (!cameraDirectory.exists()) {
            val isDirectoryCreated = cameraDirectory.mkdirs()
            if (!isDirectoryCreated) {
                // Directory creation failed
                throw IllegalArgumentException("Failed to create camera directory")
            }
        }
    }

    fun cleanCacheCameraFolder() {
        if (cameraDirectory.exists().not()) {
            return
        }
        val files = cameraDirectory.listFiles()
        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }

    fun cacheCapturedImage(cameraSessionId: String, bitmap: Bitmap, rotation: Int) {
        val fileName = cameraSessionId + "-" + System.currentTimeMillis() + ".jpg"
        val cacheFile = File(cameraDirectory, fileName)
        val outputStream = FileOutputStream(cacheFile)
        val rotationExif = ImageUtils.degreesToExifOrientation(rotation)
        val rotatedBitmap = ImageUtils.rotateBitmapIfNeeded(bitmap, rotationExif)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        bitmap.recycle()
        rotatedBitmap.recycle()
        outputStream.close()
    }

    fun queryImageByName(targetFileName: String): File? {
        val imageFiles = cameraDirectory.listFiles { file ->
            // Filter files with names starting with the specified prefix
            file.isFile && file.name == targetFileName
        }
        return imageFiles?.firstOrNull()
    }

    fun queryImagesBySessionId(sessionId: String): List<File> {
        val imageFiles = cameraDirectory.listFiles { file ->
            // Filter files with names starting with the specified prefix
            file.isFile
                    && file.name.startsWith(sessionId, ignoreCase = true)
                    && file.name.endsWith(".jpg")
        }
        return imageFiles?.toList() ?: emptyList()
    }

    @Suppress("DEPRECATION")
    fun registerFileChangesListener(events: List<Int>, onChange: () -> Unit) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            object : FileObserver(cameraDirectory, ALL_EVENTS) {
                override fun onEvent(event: Int, path: String?) {
                    if (events.contains(event) && path != null) {
                        onChange()
                    }
                }
            }
        } else {
            object : FileObserver(cameraDirectory.absolutePath, ALL_EVENTS) {
                override fun onEvent(event: Int, path: String?) {
                    if (events.contains(event) && path != null) {
                        onChange()
                    }
                }
            }
        }

    fun overrideCroppedImageFile(fileName: String, bitmap: Bitmap) {
        val file = queryImageByName(fileName)
        file?.let {
            try {
                val fos = FileOutputStream(it)
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    fos
                ); // Adjust the format and quality as needed
                fos.flush();
                fos.close();
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle the exception
            }
        }
    }

    fun overrideEditedImageFile(sourceUri: Uri, destinationUri: Uri) {
        // Step 1: Convert the URIs to File objects
        val fileA = File(sourceUri.path) // Replace fileAUri with the URI of file A
        val fileB = File(destinationUri.path) // Replace fileBUri with the URI of file B

        // Step 2: Read the contents of file B and write them to file A
        try {
            val fis = FileInputStream(fileB)
            val fos = FileOutputStream(fileA)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                fos.write(buffer, 0, bytesRead)
            }

            // Close the streams after copying the data
            fis.close()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception
        }

        // Step 3 (Optional): Delete file B if no longer needed
        val deleted = fileB.delete()
        if (!deleted) {
            // Handle the case where the file couldn't be deleted (optional)
        }
    }

    fun createPdfFromImages(sessionId: String, isMarginOn: Boolean): File? {
        val pdfDocument = PdfDocument()

        try {
            for (imageFile in queryImagesBySessionId(sessionId)) {
                val bitmap: Bitmap = BitmapFactory.decodeFile(imageFile.path)

                val targetWidth = if (isMarginOn) 535 else 585
                val targetHeight = if (isMarginOn) 792 else 842

                val resizedBitmap =
                    Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

                println("===== resized bitmap : ${resizedBitmap.width} - ${resizedBitmap.height}")

                // A4 size in points (72 points/inch)
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                println("===== page size : ${pageInfo.pageWidth} - ${pageInfo.pageHeight}")

                // Calculate the center coordinates to draw the bitmap
                val centerX = (pageInfo.pageWidth / 2).toFloat()
                val centerY = (pageInfo.pageHeight / 2).toFloat()

                // Calculate the left-top coordinates of the bitmap to center it on the page
                val left = centerX - resizedBitmap.width / 2f
                val top = centerY - resizedBitmap.height / 2f

                canvas.drawBitmap(resizedBitmap, left, top, null)
                pdfDocument.finishPage(page)
            }

            val fileName = "monday_${System.currentTimeMillis()}-output.pdf"
            val publicDir = getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)
            val destinationFile = File(publicDir, fileName)

            val outputStream = FileOutputStream(destinationFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()

            return destinationFile

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }

        return null
    }

    fun getSharePdfUri(filePath: String): Uri = FileProvider.getUriForFile(
        /* context = */ context,
        /* authority = */ "com.season.imagetofile.fileprovider",
        /* file = */ File(filePath)
    )
}
