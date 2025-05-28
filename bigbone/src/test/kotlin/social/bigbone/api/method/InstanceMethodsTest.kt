package social.bigbone.api.method

import io.mockk.verify
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBe
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.Test
import social.bigbone.Dimension
import social.bigbone.PrecisionDateTime.ValidPrecisionDateTime.ExactTime
import social.bigbone.api.entity.DomainBlock
import social.bigbone.api.entity.ExtendedDescription
import social.bigbone.api.entity.InstanceActivity
import social.bigbone.api.entity.Rule
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.testtool.MockClient
import social.bigbone.testtool.TestUtil
import java.time.Instant

class InstanceMethodsTest {
    @Test
    fun getInstance() {
        val client = MockClient.mock("instance.json")
        val instanceMethods = InstanceMethods(client)

        val instance = instanceMethods.getInstance().execute()

        instance.domain shouldBeEqualTo "test.com"
        instance.title shouldBeEqualTo "test.com"
        instance.description shouldBeEqualTo "description"
        instance.version shouldBeEqualTo "1.3.2"

        verify {
            client.get(
                path = "api/v2/instance",
                query = null
            )
        }
    }

    @Test
    fun getInstanceExtended() {
        val client = MockClient.mock("instance_extended.json")
        val instanceMethods = InstanceMethods(client)

        val instance = instanceMethods.getInstance().execute()
        instance.domain shouldBeEqualTo "mastodon.social"
        instance.title shouldBeEqualTo "Mastodon"
        instance.version shouldBeEqualTo "4.4.0-alpha.3"
        instance.sourceUrl shouldBeEqualTo "https://github.com/mastodon/mastodon"
        instance.description shouldBeEqualTo "The original server operated by the Mastodon gGmbH non-profit"
        instance.usage.users.activeMonth shouldBeEqualTo 123_122

        with(instance.thumbnail) {
            url shouldBeEqualTo "https://files.mastodon.social/site_uploads/files/000/000/001/@1x/57c12f441d083cde.png"
            blurHash.shouldNotBeNull()
            with(versions) {
                this.shouldNotBeNull()
                resolution1x.shouldNotBeNull()
                resolution2x.shouldNotBeNull()
            }
        }

        with(instance.icon) {
            size shouldBeEqualTo 4
            get(0).src shouldBeEqualTo "https://files.mastodon.social/site_uploads/files/000/000/003/36/accf17b0104f18e5.png"
            get(0).size shouldBeEqualTo Dimension(width = 36, height = 36)
            get(1).size shouldBeEqualTo Dimension(width = 72, height = 72)
            get(2).size shouldBeEqualTo Dimension(width = 192, height = 192)
            get(3).size shouldBeEqualTo Dimension(width = 512, height = 512)
        }

        with(instance.languages) {
            size shouldBeEqualTo 1
            get(0) shouldBeEqualTo "en"
        }

        val config = instance.configuration
        with(config.urls) {
            streaming shouldBeEqualTo "wss://mastodon.social"
            about shouldBeEqualTo "https://mastodon.social/about"
            privacyPolicy shouldBeEqualTo "https://mastodon.social/privacy-policy"
            termsOfService shouldBeEqualTo "https://mastodon.social/terms-of-service"
        }
        config.vapid.publicKey shouldNotBe null
        with(config.accounts) {
            maxFeaturedTags shouldBeEqualTo 10
            maxPinnedStatuses shouldBeEqualTo 4
        }
        with(config.statuses) {
            maxCharacters shouldBeEqualTo 500
            maxMediaAttachments shouldBeEqualTo 4
            charactersReservedPerUrl shouldBeEqualTo 23
        }
        with(config.mediaAttachments) {
            supportedMimeTypes shouldHaveSize 27
            descriptionLimit shouldBeEqualTo 1500
            imageSizeLimit shouldBeEqualTo 10_485_760
            imageMatrixLimit shouldBeEqualTo 16_777_216
            videoSizeLimit shouldBeEqualTo 41_943_040
            videoFrameRateLimit shouldBeEqualTo 60
            videoMatrixLimit shouldBeEqualTo 2_304_000
        }
        with(config.polls) {
            maxOptions shouldBeEqualTo 4
            maxCharactersPerOption shouldBeEqualTo 50
            minExpiration shouldBeEqualTo 300
            maxExpiration shouldBeEqualTo 2_629_746
        }
        config.translation.enabled shouldBeEqualTo true

        with(instance.registrations) {
            enabled shouldBeEqualTo false
            approvalRequired shouldBeEqualTo false
            reasonRequired shouldBeEqualTo false
            minAge shouldBeEqualTo 16
            message.shouldBeNull()
        }
        instance.apiVersions.mastodon shouldBeEqualTo 1

        with(instance.contact) {
            email shouldBeEqualTo "staff@mastodon.social"
            with(account) {
                id shouldBeEqualTo "1"
                emojis.isEmpty() shouldBeEqualTo true
            }
        }

        with(instance.rules) {
            size shouldBeEqualTo 6
            get(0).id shouldBeEqualTo "1"
            get(0).text shouldBeEqualTo "Sexually explicit or violent media must be marked as sensitive when posting"
        }

        verify {
            client.get(
                path = "api/v2/instance",
                query = null
            )
        }
    }

    @Test
    fun getInstanceWithJson() {
        val client = MockClient.mock("instance.json")
        val instanceMethods = InstanceMethods(client)
        instanceMethods.getInstance()
            .doOnJson {
                val expected = """{
  "domain": "test.com",
  "title": "test.com",
  "version": "1.3.2",
  "source_url": "https://github.com/mastodon/mastodon",
  "description": "description",
  "contact": {
    "email": "owner@test.com"
  },
  "usage": {
    "users": {
      "active_month": 123456
    }
  },
  "thumbnail": {
    "url": "https://www.server.com/testimage.svg"
  },
  "registrations": {
    "enabled": true,
    "approval_required": false
  }
}
"""
                TestUtil.normalizeLineBreaks(it) shouldBeEqualTo TestUtil.normalizeLineBreaks(expected)
            }
            .execute()

        verify {
            client.get(
                path = "api/v2/instance",
                query = null
            )
        }
    }

    @Test
    fun getInstanceWithException() {
        val client = MockClient.ioException()
        val instanceMethods = InstanceMethods(client)

        invoking {
            instanceMethods.getInstance().execute()
        } shouldThrow BigBoneRequestException::class

        verify {
            client.get(
                path = "api/v2/instance",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning success, when getting peers, then call correct endpoint and expect parsed values`() {
        val client = MockClient.mock("instance_peers_success.json")

        val instanceMethods = InstanceMethods(client)
        val peers: List<String> = instanceMethods.getPeers().execute()

        peers shouldHaveSize 3
        peers[0] shouldBeEqualTo "tilde.zone"
        peers[1] shouldBeEqualTo "mspsocial.net"
        peers[2] shouldBeEqualTo "conf.tube"

        verify {
            client.get(
                path = "api/v1/instance/peers",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning unauthorized, when getting peers, then propagate error`() {
        val client = MockClient.failWithResponse(
            responseJsonAssetPath = "error_401_unauthorized.json",
            responseCode = 401,
            message = "Unauthorized"
        )

        invoking {
            InstanceMethods(client).getPeers().execute()
        } shouldThrow BigBoneRequestException::class withMessage "Unauthorized"

        verify {
            client.get(
                path = "api/v1/instance/peers",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning success, when getting activity, then call correct endpoint and expect parsed values`() {
        val client = MockClient.mock(jsonName = "instance_activity_success.json")
        val instanceMethods = InstanceMethods(client)

        val activity: List<InstanceActivity> = instanceMethods.getActivity().execute()
        activity shouldHaveSize 12
        with(activity[0]) {
            week shouldBeEqualTo "1574640000"
            statuses shouldBeEqualTo "37125"
            logins shouldBeEqualTo "14239"
            registrations shouldBeEqualTo "542"
        }

        verify {
            client.get(
                path = "api/v1/instance/activity",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning unauthorized, when getting activity, then propagate error`() {
        val client = MockClient.failWithResponse(
            responseJsonAssetPath = "error_401_unauthorized.json",
            responseCode = 401,
            message = "Unauthorized"
        )

        invoking {
            InstanceMethods(client).getActivity().execute()
        } shouldThrow BigBoneRequestException::class withMessage "Unauthorized"

        verify {
            client.get(
                path = "api/v1/instance/activity",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning success, when getting rules, then call correct endpoint and expect parsed values`() {
        val client = MockClient.mock(jsonName = "instance_rules_success.json")
        val instanceMethods = InstanceMethods(client)

        val rules: List<Rule> = instanceMethods.getRules().execute()
        rules shouldHaveSize 6
        with(rules[0]) {
            id shouldBeEqualTo "1"
            text shouldBeEqualTo "Sexually explicit or violent media must be marked as sensitive when posting"
        }

        verify {
            client.get(
                path = "api/v1/instance/rules",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning success, when getting blocked servers, then call correct endpoint and expect parsed values`() {
        val client = MockClient.mock(jsonName = "instance_domain_blocks_success.json")
        val instanceMethods = InstanceMethods(client)

        val domainBlocks: List<DomainBlock> = instanceMethods.getBlockedDomains().execute()
        domainBlocks shouldHaveSize 2
        with(domainBlocks[0]) {
            domain shouldBeEqualTo "birb.elfenban.de"
            digest shouldBeEqualTo "5d2c6e02a0cced8fb05f32626437e3d23096480b47efbba659b6d9e80c85d280"
            severity shouldBeEqualTo DomainBlock.Severity.SUSPEND
            comment shouldBeEqualTo "Third-party bots"
        }

        verify {
            client.get(
                path = "api/v1/instance/domain_blocks",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning unauthorized, when getting blocked servers, then propagate error`() {
        val client = MockClient.failWithResponse(
            responseJsonAssetPath = "error_401_unauthorized.json",
            responseCode = 401,
            message = "Unauthorized"
        )

        invoking {
            InstanceMethods(client).getBlockedDomains().execute()
        } shouldThrow BigBoneRequestException::class withMessage "Unauthorized"

        verify {
            client.get(
                path = "api/v1/instance/domain_blocks",
                query = null
            )
        }
    }

    @Test
    fun `Given a client returning success, when getting extended description, then call correct endpoint and expect parsed values`() {
        val client = MockClient.mock(jsonName = "instance_extended_description_success.json")
        val instanceMethods = InstanceMethods(client)

        val extendedDescription: ExtendedDescription = instanceMethods.getExtendedDescription().execute()
        with(extendedDescription) {
            updatedAt shouldBeEqualTo ExactTime(Instant.parse("2022-11-03T04:09:07Z"))
            content.shouldNotBeEmpty()
        }

        verify {
            client.get(
                path = "api/v1/instance/extended_description",
                query = null
            )
        }
    }
}
