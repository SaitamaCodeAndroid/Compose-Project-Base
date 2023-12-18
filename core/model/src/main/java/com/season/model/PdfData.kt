package com.season.model

data class PdfData(
    val name: String,
    val size: String,
    val pageSize: String,
    val pageMargin: String,
    val numberOfPages: Int,
    val fileLocation: String,
)