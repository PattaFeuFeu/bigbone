package social.bigbone.api.method

import io.mockk.slot
import io.mockk.verify
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.Test
import social.bigbone.Parameters
import social.bigbone.PrecisionDateTime.ValidPrecisionDateTime.ExactTime
import social.bigbone.api.entity.data.Visibility
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.testtool.MockClient
import social.bigbone.testtool.TestUtil.urlEncode
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class AccountMethodsTest {

    @Test
    fun `Given server returning success, when registering account, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_register_success.json")
        val accountMethods = AccountMethods(client)

        val token = accountMethods.registerAccount(
            username = "username",
            email = "email",
            password = "password",
            agreement = true,
            locale = "DE",
            reason = "Please let me in!",
            dateOfBirth = LocalDate.of(1990, 1, 1)
        ).execute()

        with(token) {
            accessToken shouldBeEqualTo "ZA-Yj3aBD8U8Cm7lKUp-lm9O9BmDgdhHzDeqsY8tlL0"
            tokenType shouldBeEqualTo "Bearer"
            scope shouldBeEqualTo "read write follow push"
            createdAt shouldBeEqualTo 1_573_979_017L
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.post(
                path = accountMethods.endpoint,
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "username=username" +
                "&email=email" +
                "&password=password" +
                "&agreement=true" +
                "&locale=DE" +
                "&reason=Please+let+me+in%21" +
                "&date_of_birth=1990-01-01"
        }
    }

    @Test
    fun `When registering account with no date of birth, then call endpoint without date_of_birth`() {
        val client = MockClient.mock("accounts_register_success.json")
        val accountMethods = AccountMethods(client)

        val token = accountMethods.registerAccount(
            username = "username",
            email = "email",
            password = "password",
            agreement = true,
            locale = "DE",
            reason = "Please let me in!",
            dateOfBirth = null
        ).execute()

        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.post(
                path = accountMethods.endpoint,
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "username=username" +
                "&email=email" +
                "&password=password" +
                "&agreement=true" +
                "&locale=DE" +
                "&reason=Please+let+me+in%21"
        }
    }

    @Test
    fun `Given account failing with error response, when registering account, then propagate BigBoneRequestException`() {
        val client = MockClient.failWithResponse(
            responseJsonAssetPath = "accounts_register_422.json",
            responseCode = 422,
            message = "Unprocessable entity"
        )
        val accountMethods = AccountMethods(client)

        invoking {
            accountMethods.registerAccount(
                username = "🤠--Buck",
                email = "email",
                password = "",
                agreement = false,
                locale = "DE",
                reason = "Please let me in!",
                dateOfBirth = LocalDate.of(1990, 1, 1)
            ).execute()
        }
            .shouldThrow(BigBoneRequestException::class)
            .withMessage("Unprocessable entity")
    }

    @Test
    fun getAccount() {
        val client = MockClient.mock("account.json")
        val accountMethods = AccountMethods(client)

        val account = accountMethods.getAccount("1").execute()

        with(account) {
            acct shouldBeEqualTo "test@test.com"
            displayName shouldBeEqualTo "test"
            username shouldBeEqualTo "test"
        }
        verify {
            client.get(
                path = "${accountMethods.endpoint}/1",
                query = null
            )
        }
    }

    @Test
    fun getAccountWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking {
            accountMethods.getAccount("1").execute()
        } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun verifyCredentials() {
        val client = MockClient.mock("accounts_verify_credentials_success.json")
        val accountMethods = AccountMethods(client)

        val account = accountMethods.verifyCredentials().execute()

        with(account) {
            acct shouldBeEqualTo "trwnh"
            displayName shouldBeEqualTo "infinite love ⴳ"
            username shouldBeEqualTo "trwnh"
        }
        verify {
            client.get(
                path = "${accountMethods.endpoint}/verify_credentials",
                query = null
            )
        }
    }

    @Test
    fun verifyCredentialsWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking {
            accountMethods.verifyCredentials().execute()
        } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun `Given client failing with 401, when verifying credentials, then throw BigBoneRequestException`() {
        val client = MockClient.failWithResponse(
            responseJsonAssetPath = "error_401_unauthorized.json",
            responseCode = 401,
            message = "Unauthorized"
        )
        val accountMethods = AccountMethods(client)

        invoking {
            accountMethods.verifyCredentials().execute()
        } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun `Given client returning success, when updating credentials with valid profile fields, then ensure correct endpoint parameters`() {
        val client = MockClient.mock("accounts_update_credentials_success.json")
        val accountMethods = AccountMethods(client)
        val profileFields = AccountMethods.ProfileFields(
            first = AccountMethods.ProfileFieldName("Location") to AccountMethods.ProfileFieldValue("Amsterdam, NL 🇳🇱"),
            second = AccountMethods.ProfileFieldName("Pronouns") to AccountMethods.ProfileFieldValue("he/they"),
            third = AccountMethods.ProfileFieldName("Website") to AccountMethods.ProfileFieldValue("https://example.com"),
            fourth = null
        )

        accountMethods.updateCredentials(
            displayName = null,
            note = null,
            avatar = null,
            header = null,
            locked = null,
            bot = null,
            discoverable = null,
            hideCollections = null,
            indexable = null,
            profileFields = profileFields,
            defaultPostVisibility = null,
            defaultSensitiveMark = null,
            defaultLanguage = null
        ).execute()

        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.patch(
                path = "${accountMethods.endpoint}/update_credentials",
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo
                "fields_attributes[0][name]=Location" +
                "&fields_attributes[0][value]=Amsterdam%2C+NL+%F0%9F%87%B3%F0%9F%87%B1" +
                "&fields_attributes[1][name]=Pronouns" +
                "&fields_attributes[1][value]=he%2Fthey" +
                "&fields_attributes[2][name]=Website" +
                "&fields_attributes[2][value]=https%3A%2F%2Fexample.com"
        }
    }

    @Test
    fun `Given string with more than 255 characters, when creating ProfileFieldName, then fail with exception`() {
        val tooLongProfileFieldName = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "01234567890123456789" +
            "01234567890" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "toomanycharacters"

        invoking { AccountMethods.ProfileFieldName(tooLongProfileFieldName) }
            .shouldThrow(IllegalArgumentException::class)
            .withMessage(
                "Name of profile field must not be longer than 255 characters but was: " +
                    "$tooLongProfileFieldName (${tooLongProfileFieldName.length} characters)."
            )
    }

    @Test
    fun `Given string with more than 255 characters, when creating ProfileFieldValue, then fail with exception`() {
        val tooLongProfileFieldValue = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "01234567890123456789" +
            "01234567890" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "toomanycharacters"

        invoking { AccountMethods.ProfileFieldValue(tooLongProfileFieldValue) }
            .shouldThrow(IllegalArgumentException::class)
            .withMessage(
                "Value of profile field must not be longer than 255 characters but was: " +
                    "$tooLongProfileFieldValue (${tooLongProfileFieldValue.length} characters)."
            )
    }

    @Test
    fun updateCredentials() {
        val client = MockClient.mock("accounts_update_credentials_success.json")
        val accountMethods = AccountMethods(client)

        val account = accountMethods.updateCredentials(
            displayName = "test",
            note = "test",
            avatar = "test",
            header = "test",
            locked = false,
            bot = false,
            discoverable = false,
            hideCollections = false,
            indexable = true,
            profileFields = null,
            defaultPostVisibility = Visibility.UNLISTED,
            defaultSensitiveMark = true,
            defaultLanguage = "EN"
        ).execute()

        with(account) {
            acct shouldBeEqualTo "trwnh"
            displayName shouldBeEqualTo "infinite love ⴳ"
            username shouldBeEqualTo "trwnh"
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.patch(
                path = "${accountMethods.endpoint}/update_credentials",
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "display_name=test" +
                "&note=test" +
                "&avatar=test" +
                "&header=test" +
                "&locked=false" +
                "&bot=false" +
                "&discoverable=false" +
                "&hide_collections=false" +
                "&indexable=true" +
                "&source[privacy]=unlisted" +
                "&source[sensitive]=true" +
                "&source[language]=EN"
        }
    }

    @Test
    fun updateCredentialsWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking {
            accountMethods.updateCredentials(
                displayName = "test",
                note = "test",
                avatar = "test",
                header = "test",
                locked = false,
                bot = false,
                discoverable = false,
                hideCollections = false,
                indexable = true,
                profileFields = null,
                defaultPostVisibility = null,
                defaultSensitiveMark = null,
                defaultLanguage = null
            ).execute()
        } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun getFollowers() {
        val client = MockClient.mock("accounts.json")
        val accountMethods = AccountMethods(client)

        val pageable = accountMethods.getFollowers("1").execute()

        with(pageable.part) {
            shouldHaveSize(1)

            with(first()) {
                acct shouldBeEqualTo "test@test.com"
                displayName shouldBeEqualTo "test"
                username shouldBeEqualTo "test"
            }
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.get(
                path = "${accountMethods.endpoint}/1/followers",
                query = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery().shouldBeEmpty()
        }
    }

    @Test
    fun getFollowersWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.getFollowers("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun getFollowing() {
        val client = MockClient.mock("accounts.json")
        val accountMethods = AccountMethods(client)

        val pageable = accountMethods.getFollowing("1").execute()

        with(pageable.part) {
            shouldHaveSize(1)

            with(first()) {
                acct shouldBeEqualTo "test@test.com"
                displayName shouldBeEqualTo "test"
                username shouldBeEqualTo "test"
            }
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.get(
                path = "${accountMethods.endpoint}/1/following",
                query = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery().shouldBeEmpty()
        }
    }

    @Test
    fun getFollowingWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.getFollowing("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun getStatuses() {
        val client = MockClient.mock("statuses.json")
        val accountMethods = AccountMethods(client)

        val pageable = accountMethods.getStatuses(
            accountId = "1",
            onlyMedia = false,
            excludeReplies = true,
            excludeReblogs = true,
            pinned = false,
            filterByTaggedWith = ""
        ).execute()

        with(pageable.part) {
            shouldHaveSize(1)

            with(first()) {
                id shouldBeEqualTo "11111"
            }
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.get(
                path = "${accountMethods.endpoint}/1/statuses",
                query = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "exclude_replies=true&exclude_reblogs=true"
        }
    }

    @Test
    fun getStatusesWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking {
            accountMethods.getStatuses("1", false).execute()
        } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun `Given server returning success, when getting featured tags, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_get_featured_tags_success.json")
        val accountMethods = AccountMethods(client)

        val featuredTags = accountMethods.getFeaturedTags(
            accountId = "1234"
        ).execute()

        with(featuredTags) {
            shouldHaveSize(1)

            with(first()) {
                id shouldBeEqualTo "627"
                name shouldBeEqualTo "nowplaying"
                statusesCount shouldBeEqualTo 36
                lastStatusAt shouldBeEqualTo ExactTime(Instant.parse("2019-11-15T07:14:43.524Z"))
            }
        }
        verify {
            client.get(
                path = "${accountMethods.endpoint}/1234/featured_tags",
                query = null
            )
        }
    }

    @Test
    fun `Given server returning success with entries, when getting lists containing account, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_get_lists_containing_account_some_success.json")
        val accountMethods = AccountMethods(client)

        val mastodonLists = accountMethods.getListsContainingAccount(
            accountId = "1234"
        ).execute()

        with(mastodonLists) {
            shouldHaveSize(1)

            with(first()) {
                id shouldBeEqualTo "13694"
                title shouldBeEqualTo "dev"
            }
        }
        verify {
            client.get(
                path = "${accountMethods.endpoint}/1234/lists",
                query = null
            )
        }
    }

    @Test
    fun `Given server returning success with no entries, when getting lists containing account, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_get_lists_containing_account_none_success.json")
        val accountMethods = AccountMethods(client)

        val mastodonLists = accountMethods.getListsContainingAccount(
            accountId = "1234"
        ).execute()

        mastodonLists.shouldBeEmpty()
        verify {
            client.get(
                path = "${accountMethods.endpoint}/1234/lists",
                query = null
            )
        }
    }

    @Test
    fun followAccount() {
        val client = MockClient.mock("relationship.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.followAccount(
            accountId = "1",
            includeReblogs = true,
            notifyOnStatus = false,
            filterForLanguages = listOf("de", "en", "uk")
        ).execute()

        with(relationship) {
            id shouldBeEqualTo "3361"
            isFollowing.shouldBeTrue()
            isFollowedBy.shouldBeFalse()
            isBlocking.shouldBeFalse()
            isMuting.shouldBeFalse()
            isRequested.shouldBeFalse()
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/follow",
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "reblogs=true" +
                "&notify=false" +
                "&languages[]=de" +
                "&languages[]=en" +
                "&languages[]=uk"
        }
    }

    @Test
    fun followAccountWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.followAccount("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun unfollowAccount() {
        val client = MockClient.mock("relationship.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.unfollowAccount("3361").execute()

        with(relationship) {
            id shouldBeEqualTo "3361"
            isFollowing shouldBeEqualTo true
            isFollowedBy shouldBeEqualTo false
            isBlocking shouldBeEqualTo false
            isMuting shouldBeEqualTo false
            isRequested shouldBeEqualTo false
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/3361/unfollow",
                body = null
            )
        }
    }

    @Test
    fun unfollowAccountWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.unfollowAccount("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun `Given server returning success, when removing account from followers, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_remove_from_followers_success.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.removeAccountFromFollowers("1").execute()

        with(relationship) {
            isFollowedBy.shouldBeFalse()
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/remove_from_followers",
                body = null
            )
        }
    }

    @Test
    fun blockAccount() {
        val client = MockClient.mock("relationship.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.blockAccount("1").execute()

        with(relationship) {
            id shouldBeEqualTo "3361"
            isFollowing shouldBeEqualTo true
            isFollowedBy shouldBeEqualTo false
            isBlocking shouldBeEqualTo false
            isMuting shouldBeEqualTo false
            isRequested shouldBeEqualTo false
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/block",
                body = null
            )
        }
    }

    @Test
    fun blockAccountWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.blockAccount("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun unblockAccount() {
        val client = MockClient.mock("relationship.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.unblockAccount("1").execute()

        with(relationship) {
            id shouldBeEqualTo "3361"
            isFollowing shouldBeEqualTo true
            isFollowedBy shouldBeEqualTo false
            isBlocking shouldBeEqualTo false
            isMuting shouldBeEqualTo false
            isRequested shouldBeEqualTo false
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/unblock",
                body = null
            )
        }
    }

    @Test
    fun unblockAccountWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.unblockAccount("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun muteAccount() {
        val client = MockClient.mock("relationship.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.muteAccount(
            accountId = "1",
            muteNotifications = false,
            muteDuration = Duration.ofDays(3)
        ).execute()

        with(relationship) {
            id shouldBeEqualTo "3361"
            isFollowing shouldBeEqualTo true
            isFollowedBy shouldBeEqualTo false
            isBlocking shouldBeEqualTo false
            isMuting shouldBeEqualTo false
            isRequested shouldBeEqualTo false
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/mute",
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "notifications=false&duration=259200"
        }
    }

    @Test
    fun muteAccountWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.muteAccount("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun unmuteAccount() {
        val client = MockClient.mock("relationship.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.unmuteAccount("1").execute()

        with(relationship) {
            id shouldBeEqualTo "3361"
            isFollowing shouldBeEqualTo true
            isFollowedBy shouldBeEqualTo false
            isBlocking shouldBeEqualTo false
            isMuting shouldBeEqualTo false
            isRequested shouldBeEqualTo false
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/unmute",
                body = null
            )
        }
    }

    @Test
    fun unmuteAccountWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.unmuteAccount("1").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun `Given server returning success, when pinning account, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_feature_account_on_profile_success.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.featureAccountOnProfile("1").execute()

        with(relationship) {
            endorsed.shouldBeTrue()
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/pin",
                body = null
            )
        }
    }

    @Test
    fun `Given server returning success, when unpinning account, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_unfeature_account_from_profile_success.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.unfeatureAccountFromProfile("1").execute()

        with(relationship) {
            endorsed.shouldBeFalse()
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/unpin",
                body = null
            )
        }
    }

    @Test
    fun `Given server returning success, when setting private note on profile, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_set_private_note_on_profile_success.json")
        val accountMethods = AccountMethods(client)
        val privateNote = "Met at 31C3. Slightly awkward, but in a good way!"

        val relationship = accountMethods.setPrivateNoteOnProfile(
            accountId = "1",
            privateNote = privateNote
        ).execute()

        with(relationship) {
            note shouldBeEqualTo privateNote
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/note",
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "comment=${privateNote.urlEncode()}"
        }
    }

    @Test
    fun `Given server returning success, when unsetting private note on profile, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_unset_private_note_on_profile_success.json")
        val accountMethods = AccountMethods(client)

        val relationship = accountMethods.setPrivateNoteOnProfile(
            "1",
            privateNote = null
        ).execute()

        with(relationship) {
            note.shouldBeEmpty()
        }
        verify {
            client.post(
                path = "${accountMethods.endpoint}/1/note",
                body = null
            )
        }
    }

    @Test
    fun getRelationships() {
        val client = MockClient.mock("relationships.json")
        val accountMethods = AccountMethods(client)

        val relationships = accountMethods.getRelationships(
            accountIds = listOf("1", "2", "23", "42"),
            includeSuspended = true
        ).execute()

        with(relationships) {
            shouldHaveSize(1)

            with(first()) {
                id shouldBeEqualTo "3361"
                isFollowing shouldBeEqualTo true
                isFollowedBy shouldBeEqualTo false
                isBlocking shouldBeEqualTo false
                isMuting shouldBeEqualTo false
                isRequested shouldBeEqualTo false
            }
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.get(
                path = "${accountMethods.endpoint}/relationships",
                query = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "id[]=1" +
                "&id[]=2" +
                "&id[]=23" +
                "&id[]=42" +
                "&with_suspended=true"
        }
    }

    @Test
    fun getRelationshipsWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.getRelationships(listOf("1")).execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun `Given server returning success, when finding familiar followers, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_find_familiar_followers_success.json")
        val accountMethods = AccountMethods(client)
        val accountIds = listOf("1", "2")

        val familiarFollowers = accountMethods.findFamiliarFollowers(
            accountIds = accountIds
        ).execute()

        with(familiarFollowers) {
            shouldHaveSize(2)

            with(get(0)) {
                id shouldBeEqualTo "1"

                with(accounts) {
                    shouldHaveSize(2)

                    with(get(0)) {
                        id shouldBeEqualTo "1087990"
                        username shouldBeEqualTo "moss"
                        acct shouldBeEqualTo "moss@goblin.camp"
                    }
                    with(get(1)) {
                        id shouldBeEqualTo "1092723"
                        username shouldBeEqualTo "vivianrose"
                        acct shouldBeEqualTo "vivianrose"
                    }
                }
            }

            with(get(1)) {
                id shouldBeEqualTo "2"
                accounts.shouldBeEmpty()
            }
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.get(
                path = "${accountMethods.endpoint}/familiar_followers",
                query = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "id[]=1&id[]=2"
        }
    }

    @Test
    fun searchAccounts() {
        val client = MockClient.mock("account_search.json")
        val accountMethods = AccountMethods(client)

        val accounts = accountMethods.searchAccounts("test").execute()

        with(accounts) {
            shouldHaveSize(40)

            with(first()) {
                acct shouldBeEqualTo "A"
                displayName shouldBeEqualTo ""
                username shouldBeEqualTo "A"
            }
        }
    }

    @Test
    fun searchAccountsWithException() {
        val client = MockClient.ioException()
        val accountMethods = AccountMethods(client)

        invoking { accountMethods.searchAccounts("test").execute() } shouldThrow BigBoneRequestException::class
    }

    @Test
    fun `Given server returning success, when getting account via WebFinger address, then call correct endpoint and parse response`() {
        val client = MockClient.mock("accounts_webfinger_success.json")
        val accountMethods = AccountMethods(client)

        val account = accountMethods.getAccountViaWebFingerAddress(
            usernameOrAddress = "trwnh"
        ).execute()

        with(account) {
            id shouldBeEqualTo "14715"
            username shouldBeEqualTo "trwnh"
            acct shouldBeEqualTo "trwnh"
            displayName shouldBeEqualTo "infinite love ⴳ"
            isLocked.shouldBeFalse()
        }
        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.post(
                path = "${accountMethods.endpoint}/lookup",
                body = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "acct=trwnh"
        }
    }
}
