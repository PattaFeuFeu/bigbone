package social.bigbone.api.method

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import social.bigbone.MastodonClient
import social.bigbone.Parameters
import social.bigbone.TestConstants
import social.bigbone.api.Scope
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.testtool.MockClient

@Suppress("FunctionMaxLength")
class OAuthMethodsTest {

    @Test
    fun getOAuthUrl() {
        val client: MastodonClient = mockk()
        every { client.getInstance().domain } returns "mastodon.cloud"
        every { client.getScheme() } returns "https"
        every { client.getPort() } returns 443

        val url = OAuthMethods(client).getOAuthUrl(
            clientId = "client_id",
            redirectUri = TestConstants.REDIRECT_URI,
            scope = Scope(Scope.READ.ALL, Scope.WRITE.ACCOUNTS, Scope.ADMIN.READ.REPORTS)
        )

        url shouldBeEqualTo "https://mastodon.cloud/oauth/authorize?client_id=client_id" +
            "&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&response_type=code&scope=read+write%3Aaccounts+admin%3Aread%3Areports"
    }

    @Test
    fun getOAuthUrlWithoutScopeWithLanguageWithForceLogin() {
        val client: MastodonClient = mockk()
        every { client.getInstance().domain } returns "mastodon.cloud"
        every { client.getScheme() } returns "https"
        every { client.getPort() } returns 443

        val url = OAuthMethods(client).getOAuthUrl(
            clientId = "client_id",
            redirectUri = TestConstants.REDIRECT_URI,
            forceLogin = true,
            languageCode = "de"
        )

        url shouldBeEqualTo "https://mastodon.cloud/oauth/authorize?client_id=client_id" +
            "&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&response_type=code&force_login=true&lang=de"
    }

    @Test
    fun getUserAccessTokenWithAuthorizationCodeGrant() {
        val client: MastodonClient = MockClient.mock("access_token.json")
        every { client.getInstance().domain } returns "mastodon.cloud"
        val oauth = OAuthMethods(client)
        val accessToken = oauth.getUserAccessTokenWithAuthorizationCodeGrant(
            clientId = "test",
            clientSecret = "test",
            redirectUri = TestConstants.REDIRECT_URI,
            code = "test"
        ).execute()
        accessToken.accessToken shouldBeEqualTo "test"
        accessToken.scope shouldBeEqualTo "read write"
        accessToken.tokenType shouldBeEqualTo "bearer"
        accessToken.createdAt shouldBeEqualTo 1_493_188_835
    }

    @Test
    fun getAccessTokenWithAuthorizationCodeGrantWithException() {
        Assertions.assertThrows(BigBoneRequestException::class.java) {
            val client: MastodonClient = MockClient.ioException()
            every { client.getInstance().domain } returns "mastodon.cloud"
            val oauth = OAuthMethods(client)
            oauth.getUserAccessTokenWithAuthorizationCodeGrant(
                clientId = "test",
                clientSecret = "test",
                redirectUri = TestConstants.REDIRECT_URI,
                code = "test"
            ).execute()
        }
    }

    @Test
    fun getAccessTokenWithClientCredentialsGrant() {
        val client: MastodonClient = MockClient.mock("access_token.json")
        every { client.getInstance().domain } returns "mastodon.cloud"
        val oauth = OAuthMethods(client)
        val accessToken = oauth.getAccessTokenWithClientCredentialsGrant(
            clientId = "test",
            clientSecret = "test",
            redirectUri = TestConstants.REDIRECT_URI
        ).execute()
        accessToken.accessToken shouldBeEqualTo "test"
        accessToken.scope shouldBeEqualTo "read write"
        accessToken.tokenType shouldBeEqualTo "bearer"
        accessToken.createdAt shouldBeEqualTo 1_493_188_835
    }

    @Test
    fun getAccessTokenWithClientCredentialsGrantWithException() {
        Assertions.assertThrows(BigBoneRequestException::class.java) {
            val client: MastodonClient = MockClient.ioException()
            every { client.getInstance().domain } returns "mastodon.cloud"
            val oauth = OAuthMethods(client)
            oauth.getAccessTokenWithClientCredentialsGrant(
                clientId = "test",
                clientSecret = "test",
                redirectUri = TestConstants.REDIRECT_URI
            ).execute()
        }
    }

    @Test
    fun getAccessTokenWithPasswordGrant() {
        val client: MastodonClient = MockClient.mock("access_token.json")
        every { client.getInstance().domain } returns "mastodon.cloud"
        val oauth = OAuthMethods(client)
        val accessToken = oauth.getUserAccessTokenWithPasswordGrant(
            "test",
            "test",
            TestConstants.REDIRECT_URI,
            "test",
            "test"
        ).execute()
        accessToken.accessToken shouldBeEqualTo "test"
        accessToken.scope shouldBeEqualTo "read write"
        accessToken.tokenType shouldBeEqualTo "bearer"
        accessToken.createdAt shouldBeEqualTo 1_493_188_835
    }

    @Test
    fun getAccessTokenWithPasswordGrantWithException() {
        Assertions.assertThrows(BigBoneRequestException::class.java) {
            val client: MastodonClient = MockClient.ioException()
            every { client.getInstance().domain } returns "mastodon.cloud"
            val oauth = OAuthMethods(client)
            oauth.getUserAccessTokenWithPasswordGrant(
                clientId = "test",
                clientSecret = "test",
                redirectUri = TestConstants.REDIRECT_URI,
                username = "test",
                password = "test"
            ).execute()
        }
    }

    @Test
    fun `Given client returning success, when revoking token, then ensure correct endpoint and parameters are used`() {
        val client: MastodonClient = MockClient.mock("oauth_revoke_token_success.json")
        val oAuthMethods = OAuthMethods(client)
        val clientId = "clientId"
        val clientSecret = "clientSecret"
        val token = "token"

        oAuthMethods.revokeToken(clientId, clientSecret, token)

        val parametersCapturingSlot = slot<Parameters>()
        verify {
            client.performAction(
                endpoint = "oauth/revoke",
                method = MastodonClient.Method.POST,
                parameters = capture(parametersCapturingSlot)
            )
        }
        with(parametersCapturingSlot.captured) {
            toQuery() shouldBeEqualTo "client_id=$clientId&client_secret=$clientSecret&token=$token"
        }
    }
}
