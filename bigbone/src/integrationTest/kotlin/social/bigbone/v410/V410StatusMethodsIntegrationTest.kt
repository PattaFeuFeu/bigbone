package social.bigbone.v410

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
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
import social.bigbone.api.entity.Token
import social.bigbone.api.entity.data.PollData
import social.bigbone.api.entity.data.Visibility
import social.bigbone.api.exception.BigBoneRequestException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

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
            val postedStatus =
                user1Client.statuses.postStatus(status = "This is my status", spoilerText = "Test").execute()

            // then
            val retrievedStatus = user1Client.statuses.getStatus(postedStatus.id).execute()
            assertEquals("<p>This is my status</p>", retrievedStatus.content)
            assertEquals("Test", retrievedStatus.spoilerText)
        }

        @Test
        fun `should throw BigBoneRequestException when called with an invalid id`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)

            // when / then
            val exception = assertThrows(BigBoneRequestException::class.java) {
                user1Client.statuses.getStatus("non-existing status id").execute()
            }
            assertEquals(404, exception.httpStatusCode)
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
            val postedStatus = user1Client.statuses.postStatus(status = "Status to be posted").execute()

            // then
            assertEquals("<p>Status to be posted</p>", postedStatus.content)
            assertEquals(Visibility.PUBLIC, postedStatus.visibility)
            assertNull(postedStatus.inReplyToId)
            assertEquals(0, postedStatus.mediaAttachments.size)
            assertFalse(postedStatus.isSensitive)
            assertEquals("", postedStatus.spoilerText)
            assertEquals("en", postedStatus.language)
        }

        @Test
        fun `should post status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "Status to be posted").execute().id
            val uploadedMediaId1 =
                TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id
            val uploadedMediaId2 =
                TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id

            // when
            val postedStatus = user1Client.statuses.postStatus(
                status = "This is a reply to the previous status",
                visibility = Visibility.PRIVATE,
                inReplyToId = statusId,
                mediaIds = listOf(uploadedMediaId1, uploadedMediaId2),
                sensitive = true,
                spoilerText = "<p>This is a spoiler text</p>",
                language = "en"
            ).execute()

            // then
            assertEquals("<p>This is a reply to the previous status</p>", postedStatus.content)
            assertEquals(Visibility.PRIVATE, postedStatus.visibility)
            assertEquals(statusId, postedStatus.inReplyToId)
            assertEquals(2, postedStatus.mediaAttachments.size)
            assertTrue(postedStatus.isSensitive)
            assertEquals("<p>This is a spoiler text</p>", postedStatus.spoilerText)
            assertEquals("en", postedStatus.language)
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
            val postedPoll = user1Client.statuses.postPoll(
                status = "Do you think this test will pass?",
                pollData = PollData(
                    options = listOf("Yes", "No"),
                    expiresIn = "300"
                )
            ).execute()

            // then
            assertEquals("<p>Do you think this test will pass?</p>", postedPoll.content)
            assertEquals(Visibility.PUBLIC, postedPoll.visibility)
            assertNull(postedPoll.inReplyToId)
            assertEquals(0, postedPoll.mediaAttachments.size)
            assertFalse(postedPoll.isSensitive)
            assertEquals("", postedPoll.spoilerText)
            assertEquals("en", postedPoll.language)
        }

        @Test
        fun `should post poll when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "Poll status test").execute().id

            // when
            val postedPoll = user1Client.statuses.postPoll(
                status = "Wird dieser Test erfolgreich sein?",
                pollData = PollData(
                    options = listOf("Ja", "Nein"),
                    expiresIn = "300",
                    multiple = true,
                    hideTotals = true
                ),
                visibility = Visibility.PRIVATE,
                inReplyToId = statusId,
                sensitive = true,
                spoilerText = "Das ist der Spoilertext zur Umfrage",
                language = "de"
            ).execute()

            // then
            assertEquals("<p>Wird dieser Test erfolgreich sein?</p>", postedPoll.content)
            assertEquals(Visibility.PRIVATE, postedPoll.visibility)
            assertEquals(statusId, postedPoll.inReplyToId)
            assertEquals(0, postedPoll.mediaAttachments.size)
            assertTrue(postedPoll.isSensitive)
            assertEquals("Das ist der Spoilertext zur Umfrage", postedPoll.spoilerText)
            assertEquals("de", postedPoll.language)
        }
    }

    @Nested
    @DisplayName("scheduleStatus tests")
    internal inner class ScheduleStatusTests {
        @Test
        fun `should schedule status when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES)

            // when
            val scheduledStatus = user1Client.statuses.scheduleStatus(
                status = "This status is scheduled for $inSixMinutes",
                scheduledAt = inSixMinutes
            ).execute()

            // then
            assertEquals("This status is scheduled for $inSixMinutes", scheduledStatus.params.text)
            assertEquals(Visibility.PUBLIC, scheduledStatus.params.visibility)
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
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES)
            val uploadedMediaId1 =
                TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id
            val uploadedMediaId2 =
                TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id

            // when
            val scheduledStatus = user1Client.statuses.scheduleStatus(
                status = "Dieser Status wird um $inSixMinutes gepostet",
                scheduledAt = inSixMinutes,
                visibility = Visibility.PRIVATE,
                inReplyToId = statusId,
                mediaIds = listOf(uploadedMediaId1, uploadedMediaId2),
                sensitive = true,
                spoilerText = "Das ist ein Spoilertext",
                language = "de"
            ).execute()

            // then
            assertEquals("Dieser Status wird um $inSixMinutes gepostet", scheduledStatus.params.text)
            assertEquals(Visibility.PRIVATE, scheduledStatus.params.visibility)
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
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES)

            // when
            val scheduledPoll = user1Client.statuses.schedulePoll(
                status = "Will this poll be posted at $inSixMinutes?",
                scheduledAt = inSixMinutes,
                pollData = PollData(
                    options = listOf("Yes", "No"),
                    expiresIn = "300"
                )
            ).execute()

            // then
            assertEquals("Will this poll be posted at $inSixMinutes?", scheduledPoll.params.text)
            assertEquals(Visibility.PUBLIC, scheduledPoll.params.visibility)
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
            val inSixMinutes = Instant.now().plus(6, ChronoUnit.MINUTES)

            // when
            val scheduledPoll = user1Client.statuses.schedulePoll(
                status = "Wird diese Umfrage um $inSixMinutes gepostet?",
                scheduledAt = inSixMinutes,
                pollData = PollData(
                    options = listOf("Yes", "No"),
                    expiresIn = "300",
                    multiple = true,
                    hideTotals = true
                ),
                visibility = Visibility.PRIVATE,
                inReplyToId = statusId,
                sensitive = true,
                spoilerText = "Das ist ein Spoilertext",
                language = "de"
            ).execute()

            // then
            assertEquals("Wird diese Umfrage um $inSixMinutes gepostet?", scheduledPoll.params.text)
            assertEquals(inSixMinutes, scheduledPoll.scheduledAt)
            assertIterableEquals(listOf("Yes", "No"), scheduledPoll.params.poll!!.options)
            assertEquals("300", scheduledPoll.params.poll!!.expiresIn)
            assertEquals(Visibility.PRIVATE, scheduledPoll.params.visibility)
            assertEquals(true, scheduledPoll.params.poll!!.multiple)
            assertEquals(statusId, scheduledPoll.params.inReplyToId)
            assertTrue(scheduledPoll.params.sensitive!!)
            assertEquals("Das ist ein Spoilertext", scheduledPoll.params.spoilerText)
            assertEquals("de", scheduledPoll.params.language)
            assertEquals(true, scheduledPoll.params.poll!!.hideTotals)
        }
    }

    @Nested
    @DisplayName("reblogStatus tests")
    internal inner class ReblogStatusTests {
        @Test
        fun `should reblog status when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be reblogged soon").execute().id

            // when
            val rebloggedStatus = user2Client.statuses.reblogStatus(statusId).execute()

            // then
            assertEquals("<p>This post will be reblogged soon</p>", rebloggedStatus.reblog!!.content)
            assertEquals(Visibility.PUBLIC, rebloggedStatus.visibility)
            assertNull(rebloggedStatus.inReplyToId)
            assertEquals(0, rebloggedStatus.mediaAttachments.size)
            assertFalse(rebloggedStatus.isSensitive)
            assertEquals("", rebloggedStatus.spoilerText)
            assertNull(rebloggedStatus.language)
        }

        @Test
        fun `should reblog status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be reblogged soon (all params set)")
                .execute().id

            // when
            val rebloggedStatus = user2Client.statuses.reblogStatus(
                statusId = statusId,
                visibility = Visibility.PRIVATE
            ).execute()

            // then
            assertEquals("<p>This post will be reblogged soon (all params set)</p>", rebloggedStatus.reblog!!.content)
            assertEquals(Visibility.PRIVATE, rebloggedStatus.visibility)
            assertNull(rebloggedStatus.inReplyToId)
            assertEquals(0, rebloggedStatus.mediaAttachments.size)
            assertFalse(rebloggedStatus.isSensitive)
            assertEquals("", rebloggedStatus.spoilerText)
            assertNull(rebloggedStatus.language)
        }
    }

    @Nested
    @DisplayName("unreblogStatus tests")
    internal inner class UnreblogStatusTests {
        @Test
        fun `should unreblog status when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be reblogged soon").execute().id
            user2Client.statuses.reblogStatus(statusId).execute()

            // when
            val unrebloggedStatus = user2Client.statuses.unreblogStatus(statusId).execute()

            // then
            assertFalse(unrebloggedStatus.isReblogged)
        }
    }

    @Nested
    @DisplayName("bookmarkStatus tests")
    internal inner class BookmarkStatusTests {
        @Test
        fun `should bookmark status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be bookmarked soon").execute().id

            // when
            val bookmarkedStatus = user2Client.statuses.bookmarkStatus(statusId).execute()

            // then
            assertTrue(bookmarkedStatus.isBookmarked)
        }
    }

    @Nested
    @DisplayName("unbookmarkStatus tests")
    internal inner class UnbookmarkStatusTests {
        @Test
        fun `should unbookmark status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId =
                user1Client.statuses.postStatus(status = "This post will be bookmarked soon, then unbookmarked")
                    .execute().id
            user2Client.statuses.bookmarkStatus(statusId).execute()

            // when
            val unbookmarkedStatus = user2Client.statuses.unbookmarkStatus(statusId).execute()

            // then
            assertFalse(unbookmarkedStatus.isBookmarked)
        }
    }

    @Nested
    @DisplayName("favouriteStatus tests")
    internal inner class FavouriteStatusTests {
        @Test
        fun `should favourite status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be favorized soon").execute().id

            // when
            val favourizedStatus = user2Client.statuses.favouriteStatus(statusId).execute()

            // then
            assertTrue(favourizedStatus.isFavourited)
        }
    }

    @Nested
    @DisplayName("unfavouriteStatus tests")
    internal inner class UnfavouriteStatusTests {
        @Test
        fun `should unfavourite status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId =
                user1Client.statuses.postStatus(status = "This post will be favourited soon, then unfavourited again")
                    .execute().id
            user1Client.statuses.favouriteStatus(statusId).execute()

            // when
            val unfavouritedStatus = user2Client.statuses.unfavouriteStatus(statusId).execute()

            // then
            assertFalse(unfavouritedStatus.isFavourited)
        }
    }

    @Nested
    @DisplayName("pinStatus tests")
    internal inner class PinStatusTests {
        @Test
        fun `should pin status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be pinned soon").execute().id
            TestHelpers.unpinAllPinnedStatuses(user1Client)

            // when
            val pinnedStatus = user1Client.statuses.pinStatus(statusId).execute()

            // then
            assertTrue(pinnedStatus.isPinned)
        }
    }

    @Nested
    @DisplayName("unpinStatus tests")
    internal inner class UnpinStatusTests {
        @Test
        fun `should unpin status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId =
                user1Client.statuses.postStatus(status = "This post will be pinned soon, then unpinned again")
                    .execute().id
            TestHelpers.unpinAllPinnedStatuses(user1Client)
            user1Client.statuses.pinStatus(statusId).execute()

            // when
            val unpinnedStatus = user1Client.statuses.unpinStatus(statusId).execute()

            // then
            assertFalse(unpinnedStatus.isPinned)
        }
    }

    @Nested
    @DisplayName("muteConversation tests")
    internal inner class MuteConversationTests {
        @Test
        fun `should mute conversation when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This conversation will be muted soon").execute().id

            // when
            val mutedConversation = user2Client.statuses.muteConversation(statusId).execute()

            // then
            assertTrue(mutedConversation.isMuted)
        }
    }

    @Nested
    @DisplayName("unmuteConversation tests")
    internal inner class UnmuteConversationTests {
        @Test
        fun `should unmute conversation when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val statusId =
                user1Client.statuses.postStatus(status = "This conversation will be muted soon, then unmuted again")
                    .execute().id
            user2Client.statuses.muteConversation(statusId).execute()

            // when
            val unmutedConversation = user2Client.statuses.unmuteConversation(statusId).execute()

            // then
            assertFalse(unmutedConversation.isMuted)
        }
    }

    @Nested
    @DisplayName("deleteStatus tests")
    internal inner class DeleteStatusTests {
        @Test
        fun `should delete status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be deleted soon").execute().id

            // when
            user1Client.statuses.deleteStatus(statusId).execute()

            // then
            val exception = assertThrows(BigBoneRequestException::class.java) {
                user1Client.statuses.getStatus(statusId).execute()
            }
            assertEquals(404, exception.httpStatusCode)
        }
    }

    @Nested
    @DisplayName("editStatus tests")
    internal inner class EditStatusTests {
        @Test
        fun `should edit status when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be edited soon").execute().id

            // when
            val editedStatus = user1Client.statuses.editStatus(statusId, "This is the edited status text").execute()

            // then
            assertEquals("<p>This is the edited status text</p>", editedStatus.content)
            assertEquals(Status.Visibility.Public.value, editedStatus.visibility)
            assertNull(editedStatus.inReplyToId)
            assertEquals(0, editedStatus.mediaAttachments.size)
            assertFalse(editedStatus.isSensitive)
            assertEquals("", editedStatus.spoilerText)
            assertEquals("en", editedStatus.language)
        }

        @Test
        fun `should edit status when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postStatus(status = "This post will be edited soon").execute().id
            val uploadedMediaId1 = TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id
            val uploadedMediaId2 = TestHelpers.uploadMediaFromResourcesFolder("castle-1280x853.jpg", "image/jpg", user1Client).id

            // when
            val editedStatus = user1Client.statuses.editStatus(
                statusId = statusId,
                status = "Das ist der bearbeitete Statustext",
                mediaIds = listOf(uploadedMediaId1, uploadedMediaId2),
                sensitive = true,
                spoilerText = "Das ist der Spoilertext",
                language = "de").execute()

            // then
            assertEquals("<p>Das ist der bearbeitete Statustext</p>", editedStatus.content)
            assertEquals(Status.Visibility.Public.value, editedStatus.visibility)
            assertEquals(2, editedStatus.mediaAttachments.size)
            assertTrue(editedStatus.isSensitive)
            assertEquals("Das ist der Spoilertext", editedStatus.spoilerText)
            assertEquals("de", editedStatus.language)
        }

        @Test
        fun `should not reset poll if status is edited`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postPoll(
                status = "Do you think this test will pass?",
                pollOptions = listOf("Yes", "No"),
                pollExpiresIn = 300
            ).execute().id

            // when
            val editedPoll = user1Client.statuses.editStatus(
                statusId = statusId,
                status = "Do you really think this test will pass?",
            ).execute()

            val editedPoll2 = user1Client.statuses.getStatus(statusId).execute()

            // then
            assertEquals("<p>Do you really think this test will pass?</p>", editedPoll2.content)
            assertIterableEquals(listOf("Yes", "No"), editedPoll2.poll!!.options.stream().map { it.title }.collect(Collectors.toList()))
            assertEquals(Status.Visibility.Public.value, editedPoll2.visibility)
            assertEquals(0, editedPoll2.mediaAttachments.size)
            assertFalse(editedPoll2.isSensitive)
            assertEquals("", editedPoll2.spoilerText)
            assertEquals("en", editedPoll2.language)
        }
    }

    @Nested
    @DisplayName("editPoll tests")
    internal inner class EditPollTests {
        @Test
        fun `should edit poll when mandatory params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postPoll(
                status = "Do you think this test will pass?",
                pollOptions = listOf("Yes", "No"),
                pollExpiresIn = 300
            ).execute().id

            // when
            val editedPoll = user1Client.statuses.editPoll(
                statusId = statusId,
                status = "Do you really think this test will pass?",
                pollOptions = listOf("Yes", "No", "Eventually"),
                pollExpiresIn = 500).execute()

            // then
            assertEquals("<p>Do you really think this test will pass?</p>", editedPoll.content)
            assertIterableEquals(listOf("Yes", "No", "Eventually"), editedPoll.poll!!.options.stream().map { it.title }.collect(Collectors.toList()))
            assertEquals(Status.Visibility.Public.value, editedPoll.visibility)
            assertEquals(0, editedPoll.mediaAttachments.size)
            assertFalse(editedPoll.isSensitive)
            assertEquals("", editedPoll.spoilerText)
            assertEquals("en", editedPoll.language)
        }

        @Test
        fun `should edit poll when all params set`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val statusId = user1Client.statuses.postPoll(
                status = "Do you think this test will pass?",
                pollOptions = listOf("Yes", "No"),
                pollExpiresIn = 300
            ).execute().id

            // when
            val editedPoll = user1Client.statuses.editPoll(
                statusId = statusId,
                status = "Denkst Du wirklich dass dieser Test erfolgreich sein wird?",
                pollOptions = listOf("Ja", "Nein", "Vielleicht"),
                pollExpiresIn = 500,
                pollMultiple = true,
                pollHideTotals = true,
                sensitive = true,
                spoilerText = "Das ist der Spoilertext",
                language = "de").execute()

            // then
            assertEquals("<p>Denkst Du wirklich dass dieser Test erfolgreich sein wird?</p>", editedPoll.content)
            assertIterableEquals(listOf("Ja", "Nein", "Vielleicht"), editedPoll.poll!!.options.stream().map { it.title }.collect(Collectors.toList()))
            assertEquals(Status.Visibility.Public.value, editedPoll.visibility)
            assertEquals(0, editedPoll.mediaAttachments.size)
            assertTrue(editedPoll.poll!!.multiple)
            assertTrue(editedPoll.isSensitive)
            assertEquals("Das ist der Spoilertext", editedPoll.spoilerText)
            assertEquals("de", editedPoll.language)
        }
    }

    @Nested
    @DisplayName("getContext tests")
    internal inner class GetContextTests {
        @Test
        fun `should retrieve context when status id provided`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val firstStatus =
                user1Client.statuses.postStatus(status = "Hello, this is the first post in this thread!").execute()
            val secondStatus = user2Client.statuses.postStatus(
                status = "This is the second status in this thread",
                inReplyToId = firstStatus.id
            ).execute()
            val thirdStatus = user1Client.statuses.postStatus(
                status = "This is the third status in this thread",
                inReplyToId = secondStatus.id
            ).execute()
            val fourthStatus = user2Client.statuses.postStatus(
                status = "This is the fourth status in this thread",
                inReplyToId = thirdStatus.id
            ).execute()
            user1Client.statuses.postStatus(
                status = "This is the fifth status in this thread",
                inReplyToId = fourthStatus.id
            ).execute()

            // when
            val context = user1Client.statuses.getContext(fourthStatus.id).execute()

            // then
            assertEquals(3, context.ancestors.size)
            assertEquals(1, context.descendants.size)
        }
    }

    @Nested
    @DisplayName("getRebloggedBy tests")
    internal inner class GetRebloggedByTests {
        @Test
        fun `should retrieve reblogged count when status id provided`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val status =
                user1Client.statuses.postStatus(status = "Hello, this is something that gets reblogged").execute()
            user1Client.statuses.reblogStatus(status.id).execute()
            user2Client.statuses.reblogStatus(status.id).execute()

            // when
            val accounts = user1Client.statuses.getRebloggedBy(status.id).execute()

            // then
            assertEquals(2, accounts.part.size)
        }
    }

    @Disabled("This test currently fails as no translation service in Mastodon is configured")
    @Nested
    @DisplayName("translateStatus tests")
    internal inner class TranslateStatusTests {
        @Test
        fun `should translate status when status id provided`() {
            // given
            val user1Client = TestHelpers.getTrustAllClient(user1UserToken.accessToken)
            val user2Client = TestHelpers.getTrustAllClient(user2UserToken.accessToken)
            val status = user1Client.statuses.postStatus(status = "Hello, how are you?").execute()

            // when
            val translation = user2Client.statuses.translateStatus(status.id, "de").execute()

            // then
            assertEquals("Hallo, wie geht es dir?", translation.content)
        }
    }
}
