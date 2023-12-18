package com.season.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed class DisplayFileItem {

    data class GroupDate(val date: LocalDate) : DisplayFileItem()

    data class File(
        val id: Long,
        val name: String,
        val path: String,
        val date: LocalDateTime
    ) : DisplayFileItem()

}


fun List<ImageData>.asDisplayGroupDateFileItems(): List<DisplayFileItem> {
    val rs = mutableListOf<DisplayFileItem>()

    val groupItems = groupBy { it.dateModified.date }
    val sortedGroups = groupItems.entries.sortedByDescending { it.key }

    sortedGroups.forEach { group ->
        rs.add(DisplayFileItem.GroupDate(group.key))
        rs.addAll(
            group.value.map { imageData -> imageData.asDisplayFile() }
                .sortedByDescending { it.date }
        )
    }

    return rs
}

fun ImageData.asDisplayFile(): DisplayFileItem.File {
    return DisplayFileItem.File(
        id = this.id,
        name = this.name,
        path = this.path,
        date = this.dateModified
    )
}
