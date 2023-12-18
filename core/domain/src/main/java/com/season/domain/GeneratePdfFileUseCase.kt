package com.season.domain

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import com.season.common.network.AppDispatchers.*
import com.season.common.network.Dispatcher
import com.season.common.result.Result
import com.season.common.result.asResult
import com.season.data.repository.FileRepository
import com.season.model.PdfData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow


class GeneratePdfFileUseCase @Inject constructor(
    private val fileRepository: FileRepository,
    @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(cameraSessionId: String, isMarginOn: Boolean): Flow<Result<PdfData>> =
        flow {
            val pdfFile = fileRepository.generatePdfFile(cameraSessionId, isMarginOn)
            val pdfRenderer: PdfRenderer
            val parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            pdfFile?.let {
                emit(
                    PdfData(
                        name = it.name,
                        size = it.length().formatFileSize(),
                        pageSize = "A4",
                        pageMargin = "0.25in",
                        numberOfPages = pdfRenderer.pageCount,
                        fileLocation = it.absolutePath,
                    )
                )
            } ?: run {
                throw IOException("Failed to create pdf file")
            }

            pdfRenderer.close()
        }
            .flowOn(ioDispatcher)
            .asResult()

}

private fun Long.formatFileSize(): String {
    if (this <= 0) return "0 B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()

    return String.format("%.2f %s", this / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}
