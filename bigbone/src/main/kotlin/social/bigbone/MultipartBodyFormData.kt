package social.bigbone

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import social.bigbone.api.entity.FileAsMediaAttachment

/**
 * Helper method to add a [FileAsMediaAttachment] to a passed [MultipartBody.Builder].
 */
internal fun addFileToFormBody(
    file: FileAsMediaAttachment?,
    formDataName: String,
    builder: MultipartBody.Builder
): MultipartBody.Builder {
    if (file == null) return builder

    val (file, mediaType) = file
    val body = file.asRequestBody(contentType = mediaType.toMediaTypeOrNull())
    val part = MultipartBody.Part.createFormData(name = formDataName, filename = file.name, body = body)

    return builder.addPart(part)
}
