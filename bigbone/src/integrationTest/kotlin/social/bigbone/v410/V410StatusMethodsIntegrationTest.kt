package social.bigbone.v410

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import social.bigbone.TestConstants.Companion.USER1_APP_NAME
import social.bigbone.TestConstants.Companion.USER1_EMAIL
import social.bigbone.TestConstants.Companion.USER1_PASSWORD
import social.bigbone.TestConstants.Companion.USER2_APP_NAME
import social.bigbone.TestConstants.Companion.USER2_EMAIL
import social.bigbone.TestConstants.Companion.USER2_PASSWORD
import social.bigbone.TestHelpers
import social.bigbone.TestHelpers.toISO8601DateTime
import social.bigbone.api.entity.Status
import social.bigbone.api.entity.Token
import social.bigbone.api.exception.BigBoneRequestException
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Integration tests for StatusMethods running on Mastodon 4.1.0.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class V410StatusMethodsIntegrationTest {
    private lateinit var user1AppToken: Token
    private lateinit var user2AppToken: Token
    private lateinit var user1UserToken: Token
    private lateinit var user2UserToken: Token

    @BeforeAll
    fun beforeAll() {
        val user1Application = TestHelpers.createApp(USER1_APP_NAME)
        user1AppToken = TestHelpers.getAppToken(user1Application)
        user1UserToken = TestHelpers.getUserToken(user1Application, USER1_EMAIL, USER1_PASSWORD)

        val user2Application = TestHelpers.createApp(USER2_APP_NAME)
        user2AppToken = TestHelpers.getAppToken(user2Application)
        user2UserToken = TestHelpers.getUserToken(user2Application, USER2_EMAIL, USER2_PASSWORD)
    }

    @Nested
    @DisplayName("getStatus tests")
    internal inner class GetStatusTests {
        @Test
        fun `should return status when retrieved by id`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)

            // when
            val statusId = user1Client.statuses.postStatus(status = "This is my status", spoilerText = "Test").execute().id

            // then
            val retrievedStatus = user1Client.statuses.getStatus(statusId).execute()
            assertEquals("<p>This is my status</p>", retrievedStatus.content)
            assertEquals("Test", retrievedStatus.spoilerText)
        }

        @Test
        fun `should throw BigBoneRequestException when called with an invalid id`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)

            // when / then
            assertThrows(BigBoneRequestException::class.java) {
                user1Client.statuses.getStatus("non-existing status id").execute()
            }
        }
    }

    @Nested
    @DisplayName("postStatus tests")
    internal inner class PostStatusTests {
        @Test
        fun `should post status when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)

            // when
            val statusId = user1Client.statuses.postStatus(status = "Status to be posted").execute().id

            // then
            val status = user1Client.statuses.getStatus(statusId).execute()
            assertEquals(statusId, status.id)
            assertEquals("<p>Status to be posted</p>", status.content)
            assertEquals(Status.Visibility.Public.value, status.visibility)
            assertNull(status.inReplyToId)
            assertEquals(0, status.mediaAttachments.size)
            assertFalse(status.isSensitive)
            assertEquals("", status.spoilerText)
            assertEquals("en", status.language)
        }

        @Test
        fun `should post status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "Status to be posted").execute().id
            val uploadedMediaId1 = TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id
            val uploadedMediaId2 = TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id

            // when
            val replyStatusId = user1Client.statuses.postStatus(
                status = "This is a reply to the previous status",
                visibility = Status.Visibility.Private,
                inReplyToId = statusId,
                mediaIds = listOf(uploadedMediaId1, uploadedMediaId2),
                sensitive = true,
                spoilerText = "<p>This is a spoiler text</p>",
                language = "en"
            ).execute().id

            // then
            val replyStatus = user1Client.statuses.getStatus(replyStatusId).execute()
            assertEquals(replyStatusId, replyStatus.id)
            assertEquals("<p>This is a reply to the previous status</p>", replyStatus.content)
            assertEquals(Status.Visibility.Private.value, replyStatus.visibility)
            assertEquals(statusId, replyStatus.inReplyToId)
            assertEquals(2, replyStatus.mediaAttachments.size)
            assertTrue(replyStatus.isSensitive)
            assertEquals("<p>This is a spoiler text</p>", replyStatus.spoilerText)
            assertEquals("en", replyStatus.language)
        }
    }

    @Nested
    @DisplayName("postPoll tests")
    internal inner class PostPollTests {
        @Test
        fun `should post poll when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)

            // when
            val statusId = user1Client.statuses.postPoll(
                status = "Do you think this test will pass?",
                pollOptions = listOf("Yes", "No"),
                pollExpiresIn = 300
            ).execute().id

            // then
            val retrievedStatus = user1Client.statuses.getStatus(statusId).execute()
            assertEquals(statusId, retrievedStatus.id)
            assertEquals("<p>Do you think this test will pass?</p>", retrievedStatus.content)
            assertEquals(Status.Visibility.Public.value, retrievedStatus.visibility)
            assertNull(retrievedStatus.inReplyToId)
            assertEquals(0, retrievedStatus.mediaAttachments.size)
            assertFalse(retrievedStatus.isSensitive)
            assertEquals("", retrievedStatus.spoilerText)
            assertEquals("en", retrievedStatus.language)
        }

        @Test
        fun `should post poll when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "Poll status test").execute().id

            // when
            val pollStatusId = user1Client.statuses.postPoll(
                status = "Wird dieser Test erfolgreich sein?",
                pollOptions = listOf("Ja", "Nein"),
                pollExpiresIn = 300,
                visibility = Status.Visibility.Private,
                pollMultiple = true,
                pollHideTotals = true,
                inReplyToId = statusId,
                sensitive = true,
                spoilerText = "Das ist der Spoilertext zur Umfrage",
                language = "de"
            ).execute().id

            // then
            val retrievedPollStatus = user1Client.statuses.getStatus(pollStatusId).execute()
            assertEquals(pollStatusId, retrievedPollStatus.id)
            assertEquals("<p>Wird dieser Test erfolgreich sein?</p>", retrievedPollStatus.content)
            assertEquals(Status.Visibility.Private.value, retrievedPollStatus.visibility)
            assertEquals(statusId, retrievedPollStatus.inReplyToId)
            assertEquals(0, retrievedPollStatus.mediaAttachments.size)
            assertTrue(retrievedPollStatus.isSensitive)
            assertEquals("Das ist der Spoilertext zur Umfrage", retrievedPollStatus.spoilerText)
            assertEquals("de", retrievedPollStatus.language)
        }
    }

    @Nested
    @DisplayName("scheduleStatus tests")
    internal inner class ScheduleStatusTests {
        @Test
        fun `should schedule status when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES).toISO8601DateTime(ZoneId.systemDefault())

            // when
            val scheduledStatus = user1Client.statuses.scheduleStatus(
                status = "This status is scheduled for $inSixMinutes",
                scheduledAt = inSixMinutes
            ).execute()

            // then
            assertEquals("This status is scheduled for $inSixMinutes", scheduledStatus.params.text)
            assertEquals(Status.Visibility.Public.value, scheduledStatus.params.visibility)
            assertNull(scheduledStatus.params.inReplyToId)
            assertEquals(0, scheduledStatus.mediaAttachments.size)
            assertFalse(scheduledStatus.params.sensitive!!)
            assertNull(scheduledStatus.params.spoilerText)
            assertNull(scheduledStatus.params.language)
        }

        @Test
        fun `should schedule status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "Test eines geplanten Posts").execute().id
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES).toISO8601DateTime(ZoneId.systemDefault())
            val uploadedMediaId1 = TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id
            val uploadedMediaId2 = TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id

            // when
            val scheduledStatus = user1Client.statuses.scheduleStatus(
                status = "Dieser Status wird um $inSixMinutes gepostet",
                scheduledAt = inSixMinutes,
                visibility = Status.Visibility.Private,
                inReplyToId = statusId,
                mediaIds = listOf(uploadedMediaId1, uploadedMediaId2),
                sensitive = true,
                spoilerText = "Das ist ein Spoilertext",
                language = "de"
            ).execute()

            // then
            assertEquals("Dieser Status wird um $inSixMinutes gepostet", scheduledStatus.params.text)
            assertEquals(Status.Visibility.Private.value, scheduledStatus.params.visibility)
            assertEquals(statusId, scheduledStatus.params.inReplyToId)
            assertEquals(2, scheduledStatus.mediaAttachments.size)
            assertTrue(scheduledStatus.params.sensitive!!)
            assertEquals("Das ist ein Spoilertext", scheduledStatus.params.spoilerText)
            assertEquals("de", scheduledStatus.params.language)
        }
    }

    @Nested
    @DisplayName("schedulePoll tests")
    internal inner class SchedulePollTests {
        @Test
        fun `should schedule poll when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES).toISO8601DateTime(ZoneId.systemDefault())

            // when
            val scheduledPoll = user1Client.statuses.schedulePoll(
                status = "Will this poll be posted at $inSixMinutes?",
                scheduledAt = inSixMinutes,
                pollOptions = listOf("Yes", "No"),
                pollExpiresIn = 300
            ).execute()

            // then
            assertEquals("Will this poll be posted at $inSixMinutes?", scheduledPoll.params.text)
            assertEquals(Status.Visibility.Public.value, scheduledPoll.params.visibility)
            assertNull(scheduledPoll.params.inReplyToId)
            assertEquals(0, scheduledPoll.mediaAttachments.size)
            assertFalse(scheduledPoll.params.sensitive!!)
            assertNull(scheduledPoll.params.spoilerText)
            assertNull(scheduledPoll.params.language)
        }

        @Test
        fun `should schedule poll when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "Test eines geplanten Posts").execute().id
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES).toISO8601DateTime(ZoneId.systemDefault())

            // when
            val scheduledPoll = user1Client.statuses.schedulePoll(
                status = "Wird diese Umfrage um $inSixMinutes gepostet?",
                scheduledAt = inSixMinutes,
                pollOptions = listOf("Yes", "No"),
                pollExpiresIn = 300,
                visibility = Status.Visibility.Private,
                pollMultiple = true,
                inReplyToId = statusId,
                sensitive = true,
                spoilerText = "Das ist ein Spoilertext",
                language = "de",
                pollHideTotals = true
            ).execute()

            // then
            assertEquals("Wird diese Umfrage um $inSixMinutes gepostet?", scheduledPoll.params.text)
            assertEquals(inSixMinutes, scheduledPoll.scheduledAt)
            assertIterableEquals(listOf("Yes", "No"), scheduledPoll.params.poll!!.options)
            assertEquals("300", scheduledPoll.params.poll!!.expiresIn)
            assertEquals(Status.Visibility.Private.value, scheduledPoll.params.visibility)
            assertEquals(true, scheduledPoll.params.poll!!.multiple)
            assertEquals(statusId, scheduledPoll.params.inReplyToId)
            assertTrue(scheduledPoll.params.sensitive!!)
            assertEquals("Das ist ein Spoilertext", scheduledPoll.params.spoilerText)
            assertEquals("de", scheduledPoll.params.language)
            assertEquals(true, scheduledPoll.params.poll!!.hideTotals)
        }
    }
}
