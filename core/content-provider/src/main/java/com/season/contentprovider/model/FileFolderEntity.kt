package com.season.contentprovider.model

import com.season.model.FileFolder

/**
 * Represent a folder data queried from content-provider, it can be an image or document folder.
 * @param name name of folder on device.
 * @param path path of folder on device.
 */
data class FileFolderEntity(
    val name: String,
    val path: String,
)


fun FileFolderEntity.asExternalModel() = FileFolder.Common(
    name = this.name,
    path = this.path,
)