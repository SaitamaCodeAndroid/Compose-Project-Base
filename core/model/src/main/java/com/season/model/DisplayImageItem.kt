package com.season.model

import java.io.File

data class DisplayImageItem(
    val name: String,
    val url: String
)

fun File.asExternalModel() = DisplayImageItem(
    name = this.name,
    url = this.absolutePath,
)
