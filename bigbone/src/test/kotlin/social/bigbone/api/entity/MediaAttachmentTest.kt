package social.bigbone.api.entity

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.jupiter.api.Test
import social.bigbone.JSON_SERIALIZER
import social.bigbone.testtool.AssetsUtil

class MediaAttachmentTest {
    @Test
    fun deserialize() {
        val json = AssetsUtil.readFromAssets("attachment.json")
        val mediaAttachment: MediaAttachment = JSON_SERIALIZER.decodeFromString(json)
        mediaAttachment.id shouldBeEqualTo "10"
        mediaAttachment.url shouldBeEqualTo "youtube"
        mediaAttachment.remoteUrl shouldNotBe null
        mediaAttachment.previewUrl shouldBeEqualTo "preview"
        mediaAttachment.type shouldBeEqualTo MediaAttachment.MediaType.VIDEO
    }

    @Test
    fun deserializeAudio() {
        val json = AssetsUtil.readFromAssets("media_attachment_audio.json")
        val mediaAttachment: MediaAttachment = JSON_SERIALIZER.decodeFromString(json)
        mediaAttachment.id shouldBeEqualTo "21165404"
        mediaAttachment.url shouldBeEqualTo "https://files.mastodon.social/media_attachments/files/021/165/404/original/a31a4a46cd713cd2.mp3"
        mediaAttachment.previewUrl shouldBeEqualTo "https://files.mastodon.social/media_attachments/files/021/165/404/small/a31a4a46cd713cd2.mp3"
        mediaAttachment.type shouldBeEqualTo MediaAttachment.MediaType.AUDIO
        mediaAttachment.meta?.length shouldBeEqualTo "0:06:42.86"
        mediaAttachment.meta?.original?.width shouldBeEqualTo null
        mediaAttachment.meta?.original?.height shouldBeEqualTo null
    }
}
