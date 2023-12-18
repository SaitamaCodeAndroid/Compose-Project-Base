package com.season.contentprovider.model

import com.season.model.ImageData
import kotlinx.datetime.LocalDateTime

data class ImageDataEntity(
    val id: Long,
    val name: String,
    val path: String,
    val dateModified: LocalDateTime,
)

fun ImageDataEntity.asExternalModel() = ImageData(
    id = this.id,
    name = this.name,
    path = this.path,
    dateModified = dateModified,
)