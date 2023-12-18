package com.season.data.repository

import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.ParcelFileDescriptor
import com.season.common.network.AppDispatchers.IO
import com.season.common.network.Dispatcher
import com.season.contentprovider.MondayProviderDataSource
import com.season.contentprovider.model.asExternalModel
import com.season.filestorage.MondayFileStorageDataSource
import com.season.model.DisplayFileItem
import com.season.model.FileFolder
import com.season.model.asDisplayGroupDateFileItems
import com.season.model.asExternalModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val mondayProviderDataSource: MondayProviderDataSource,
    private val mondayFileStorageDataSource: MondayFileStorageDataSource,
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
) : FileRepository {

    override suspend fun getImageFolders(): List<FileFolder> = withContext(ioDispatcher) {
        val folders = mondayProviderDataSource.queryFoldersWithImages()
            .map { it.asExternalModel() }
            .sortedBy { it.name.uppercase() }

        if (folders.isEmpty()) {
            emptyList()
        } else {
            mutableListOf<FileFolder>().apply {
                add(FileFolder.All())
                addAll(folders)
            }
        }
    }

    override suspend fun getImagesInFolder(path: String?): List<DisplayFileItem> =
        withContext(ioDispatcher) {
            mondayProviderDataSource.queryImagesInFolder(path)
                .map { it.asExternalModel() }
                .asDisplayGroupDateFileItems()
        }

    override suspend fun startCameraSession() =
        withContext(ioDispatcher) {
            mondayFileStorageDataSource.cleanCacheCameraFolder()
        }

    override suspend fun cacheCapturedImage(
        cameraSessionId: String,
        bitmap: Bitmap,
        rotation: Int
    ) = withContext(ioDispatcher) {
        mondayFileStorageDataSource.cacheCapturedImage(cameraSessionId, bitmap, rotation)
    }

    override suspend fun getImagesBySessionId(sessionId: String): List<Uri> =
        withContext(ioDispatcher) {
            mondayProviderDataSource.getImagesBySessionId(sessionId)
        }

    override suspend fun getImageByName(fileName: String): File? =
        withContext(ioDispatcher) {
            mondayFileStorageDataSource.queryImageByName(fileName)
        }

    override suspend fun getImageBitmapByName(fileName: String): Bitmap? =
        withContext(ioDispatcher) {
            val file = mondayFileStorageDataSource.queryImageByName(fileName)
                ?: return@withContext null
            try {
                BitmapFactory.decodeFile(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    override fun observeAllImagesInSession(sessionId: String, events: List<Int>) = callbackFlow {
        trySend(
            mondayFileStorageDataSource.queryImagesBySessionId(sessionId)
                .map { it.asExternalModel() }
        )

        val observer = mondayFileStorageDataSource.registerFileChangesListener(events) {
            trySend(
                mondayFileStorageDataSource.queryImagesBySessionId(sessionId)
                    .map { it.asExternalModel() }
            )
        }

        observer.startWatching()

        awaitClose {
            observer.stopWatching()
        }
    }
        .conflate()
        .flowOn(ioDispatcher)

    override suspend fun overrideCroppedImageFile(fileName: String, bitmap: Bitmap) =
        withContext(ioDispatcher) {
            mondayFileStorageDataSource.overrideCroppedImageFile(fileName, bitmap)
        }

    override suspend fun overrideEditedImageFile(sourceUri: Uri, destinationUri: Uri) {
        withContext(ioDispatcher) {
            mondayFileStorageDataSource.overrideEditedImageFile(sourceUri, destinationUri)
        }
    }

    override suspend fun generatePdfFile(sessionId: String, isMarginOn: Boolean): File? =
        withContext(ioDispatcher) {
            mondayFileStorageDataSource.createPdfFromImages(sessionId, isMarginOn)
        }

    override suspend fun generatePdfRendererPages(pdfFile: File): List<Bitmap> =
        withContext(ioDispatcher) {
            val pdfRender = PdfRenderer(
                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            )

            val renderedBitmaps = mutableListOf<Bitmap>()

            for (i in 0 until pdfRender.pageCount) {
                val page = pdfRender.openPage(i)

                val bitmap = Bitmap.createBitmap(
                    page?.width ?: 1,
                    page?.height ?: 1,
                    Bitmap.Config.ARGB_8888
                )

                // say we render for showing on the screen
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                // do stuff with the bitmap

                // close the page
                page.close()

                renderedBitmaps.add(bitmap)

            }

            // close the renderer
            pdfRender.close()

            renderedBitmaps
        }

    override suspend fun deleteCacheImage(fileName: String): Unit =
        withContext(ioDispatcher) {
            val file = mondayFileStorageDataSource.queryImageByName(fileName)
            file?.delete()
        }

    override suspend fun getSharePdfUri(filePath: String): Uri =
        withContext(ioDispatcher) {
            mondayFileStorageDataSource.getSharePdfUri(filePath)
        }

    /**
     * Observe images change in shared storage using content provider, un-used for now.
     */
    fun observeAllImagesInSession(sessionId: String) = callbackFlow {

        trySend(mondayProviderDataSource.getImagesBySessionId(sessionId))

        val handlerThread = HandlerThread("MyBackgroundThread")
        // Start the thread (don't forget this step)
        handlerThread.start()

        val observer = object : ContentObserver(Handler(handlerThread.looper)) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                trySend(mondayProviderDataSource.getImagesBySessionId(sessionId))
            }
        }

        mondayProviderDataSource.registerObserver(observer)

        awaitClose {
            mondayProviderDataSource.unregisterObserver(observer)
            handlerThread.quit()
        }

    }
        .conflate()
        .flowOn(ioDispatcher)
}
