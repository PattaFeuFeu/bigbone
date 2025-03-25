package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.api.entity.data.Visibility
import social.bigbone.api.method.FileAsMediaAttachment
import java.io.File

object PostStatusWithMediaAttached {
    @JvmStatic
    fun main(args: Array<String>) {
        val instance = args[0]

        // access token with at least Scope.WRITE.MEDIA and Scope.WRITE.STATUSES
        val accessToken = args[1]

        // Instantiate client
        val client = MastodonClient.Builder(instance)
            .accessToken(accessToken)
            .build()

        // Read file from resources folder
        val classLoader = Thread.currentThread().contextClassLoader
        val uploadFile = File(classLoader.getResource("castle.jpg")!!.file)

        // Upload image to Mastodon
        val uploadedFile = client.media.uploadMediaAsync(
            mediaAttachment = FileAsMediaAttachment(file = uploadFile, mediaType = "image/jpg")
        ).execute()
        val mediaId = uploadedFile.id

        // Post status with media attached
        val statusText = "Status posting test"
        val mediaIds = listOf(mediaId)
        val visibility = Visibility.PRIVATE
        val inReplyToId: String? = null
        val sensitive = false
        val spoilerText = "A castle"
        val language = "en"
        client.statuses.postStatus(
            status = statusText,
            mediaIds = mediaIds,
            visibility = visibility,
            inReplyToId = inReplyToId,
            sensitive = sensitive,
            spoilerText = spoilerText,
            language = language
        ).execute()
    }
}
