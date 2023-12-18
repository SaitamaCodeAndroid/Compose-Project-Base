package com.season.model

sealed class FileFolder {

    abstract val files: List<DisplayFileItem>

    data class All(
        override val files: List<DisplayFileItem> = emptyList()
    ) : FileFolder()

    data class Common(
        val name: String,
        val path: String,
        override val files: List<DisplayFileItem> = emptyList()
    ) : FileFolder()

}
