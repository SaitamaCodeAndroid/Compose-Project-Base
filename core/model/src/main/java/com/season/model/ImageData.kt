package com.season.model

import kotlinx.datetime.LocalDateTime

data class ImageData(
    val id: Long,
    val name: String,
    val path: String,
    val dateModified: LocalDateTime,
)