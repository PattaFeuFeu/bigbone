package social.bigbone.api.entity

import java.io.File

/**
 * Wrapper that can be used to upload e.g. a [MediaAttachment] or avatar for [Profile]/[Account].
 *
 * @property file [java.io.File] representation of the media attachment that should be used when uploading.
 * @property mediaType [String] representation of [file]’s media type. Defaults to image/jpeg.
 */
data class FileAsMediaAttachment(
    val file: File,
    val mediaType: String = "image/jpeg"
)
