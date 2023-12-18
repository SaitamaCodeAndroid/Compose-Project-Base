package com.season.contentprovider

import android.content.ContentResolver
import android.content.ContentValues
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME
import android.provider.MediaStore.Images.Media.DATA
import android.provider.MediaStore.Images.Media.DATE_ADDED
import android.provider.MediaStore.Images.Media.DATE_MODIFIED
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Images.Media._ID
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.MIME_TYPE
import android.provider.MediaStore.MediaColumns.RELATIVE_PATH
import androidx.exifinterface.media.ExifInterface
import com.season.common.util.ImageUtils
import com.season.contentprovider.model.FileFolderEntity
import com.season.contentprovider.model.ImageDataEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject


class MondayProviderDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    fun queryFoldersWithImages(): List<FileFolderEntity> {
        val uri = EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            DATA,
            BUCKET_DISPLAY_NAME
        )

        val selection = "$MIME_TYPE LIKE ?"
        val selectionArgs = arrayOf("%image%") // Query for any file with 'image' in its MIME type.

//        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val folders = mutableSetOf<FileFolderEntity>()

        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            val dataColumnIndex = cursor.getColumnIndex(DATA)
            val folderNameColumnIndex =
                cursor.getColumnIndex(BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val folderName = cursor.getString(folderNameColumnIndex)
                val filePath = cursor.getString(dataColumnIndex)
                val folderPath = filePath.substringBeforeLast("/")

                folders.add(FileFolderEntity(folderName, folderPath))
            }

            // Process the list of folders as needed
//            for (folder in folders) {
//                println("==== Folder Path: $folder")
//            }
        }

        return folders.toList()
    }

    fun queryImagesInFolder(folderPath: String?): List<ImageDataEntity> {
        val uri = EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            _ID,
            DATA,
            DISPLAY_NAME,
            DATE_MODIFIED
        )

        val selection = folderPath?.let { "$DATA LIKE ?" }
        // Query for images with paths starting with the specified folder path.
        val selectionArgs = folderPath?.let { arrayOf("$folderPath%") }

        val images = mutableSetOf<ImageDataEntity>()

        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            val idColumnIndex = cursor.getColumnIndex(_ID)
            val dataColumnIndex = cursor.getColumnIndex(DATA)
            val nameColumnIndex = cursor.getColumnIndex(DISPLAY_NAME)
            val dateModifiedColumnIndex =
                cursor.getColumnIndex(DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val filePath = cursor.getString(dataColumnIndex)
                val imageId = cursor.getLong(idColumnIndex)
                val imageName = cursor.getString(nameColumnIndex)
                val dateModified = cursor.getLong(dateModifiedColumnIndex)
//                val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                    .format(Date(dateModified * 1000L)) // Convert seconds to milliseconds

                // Process the image file as needed
//                println("Image File Path: $filePath")
                images.add(
                    ImageDataEntity(
                        id = imageId,
                        name = imageName,
                        path = filePath,
                        dateModified = Instant.fromEpochSeconds(dateModified)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                )
            }
        }

        return images.toList()
    }

    fun saveMediaToStorage(bitmap: Bitmap): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(DISPLAY_NAME, filename)
            put(MIME_TYPE, "image/jpg")
            put(RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        val imageUri: Uri? = contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues)

        val fos: OutputStream? = imageUri?.let { contentResolver.openOutputStream(it) }

        val saveSuccess = fos?.let {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        } ?: false

        return if (saveSuccess) imageUri else null
    }

    fun uriToBitmap(imageUri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri) ?: return null

            // Read the data into a byte array
            val byteArray = inputStream.readBytes()
            inputStream.close()

            // Get the orientation of the image from its EXIF metadata
            val exifInterface = ExifInterface(ByteArrayInputStream(byteArray))
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            // Rotate the image to the correct orientation
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            return ImageUtils.rotateBitmapIfNeeded(bitmap, orientation)

        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun overrideImageFile(imageUri: Uri, bitmap: Bitmap): Boolean {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            contentResolver.openOutputStream(imageUri)?.let { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                return true
            }
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun getImagesBySessionId(sessionId: String): List<Uri> {
        val projection = arrayOf(_ID)
        val selection = "$DISPLAY_NAME LIKE '$sessionId-%'"
        val sortOrder = "$DATE_ADDED DESC"

        println("======= + $selection")

        val imagesList = mutableListOf<Uri>()
        val cursor = contentResolver.query(
            /* uri = */ EXTERNAL_CONTENT_URI,
            /* projection = */ projection,
            /* selection = */ selection,
            /* selectionArgs = */ null,
            /* sortOrder = */ sortOrder
        )

        cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(_ID)
            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(columnIndex)
                val contentUri = Uri.withAppendedPath(EXTERNAL_CONTENT_URI, imageId.toString())
                imagesList.add(contentUri)
            }
        }

        println("======= image list $imagesList")

        return imagesList
    }

    fun registerObserver(observer: ContentObserver) {
        contentResolver.registerContentObserver(EXTERNAL_CONTENT_URI, true, observer)
    }

    fun unregisterObserver(observer: ContentObserver) {
        contentResolver.unregisterContentObserver(observer)
    }

}
