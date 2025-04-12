package social.bigbone

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.Test
import social.bigbone.api.exception.InstanceVersionRetrievalException
import social.bigbone.api.exception.ServerInfoRetrievalException
import social.bigbone.nodeinfo.NodeInfoClient
import social.bigbone.nodeinfo.entity.Server
import java.net.UnknownHostException

class MastodonClientTest {

    @Test
    fun `Given response body without instance version, when building MastodonClient, then fail with InstanceVersionRetrievalException`() {
        val serverUrl = "foo.bar"
        val scheme = "http"
        val port = 443
        val clientBuilder = spyk(MastodonClient.Builder(serverUrl)) {
            // Mock internal NodeInfoClient so that we don't open the site in unit testing
            mockkObject(NodeInfoClient)
            every {
                NodeInfoClient.retrieveServerInfo(
                    host = serverUrl,
                    scheme = scheme,
                    port = port
                )
            } throws ServerInfoRetrievalException(
                "just for testing",
                null
            )

            val responseMock = mockk<Response> {
                val invalidResponseBody = "{ \"foo\": \"bar\" }"
                every { body } answers { invalidResponseBody.toResponseBody("application/json".toMediaType()) }
                every { isSuccessful } answers { true }
                every { close() } returns Unit
            }
            every { executeInstanceRequest(any()) } answers { responseMock }
        }
            .withHttpsDisabled()
            .withPort(port)

        invoking(clientBuilder::build)
            .shouldThrow(ServerInfoRetrievalException::class)
            .withMessage("just for testing")
    }

    @Test
    fun `Given response body without instance version, when building MastodonClient with defaults, then fail with InstanceVersionRetrievalException`() {
        val serverUrl = "foo.bar"
        val clientBuilder = spyk(MastodonClient.Builder(serverUrl)) {
            // Mock internal NodeInfoClient so that we don't open the site in unit testing
            mockkObject(NodeInfoClient)
            every { NodeInfoClient.retrieveServerInfo(host = serverUrl) } throws ServerInfoRetrievalException(
                "just for testing",
                null
            )

            val responseMock = mockk<Response> {
                val invalidResponseBody = "{ \"foo\": \"bar\" }"
                every { body } answers { invalidResponseBody.toResponseBody("application/json".toMediaType()) }
                every { isSuccessful } answers { true }
                every { close() } returns Unit
            }
            every { executeInstanceRequest(any()) } answers { responseMock }
        }

        invoking(clientBuilder::build)
            .shouldThrow(ServerInfoRetrievalException::class)
            .withMessage("just for testing")
    }

    @Test
    fun `Given a server that doesn't run Mastodon, when building MastodonClient, then fail with InstanceVersionRetrievalException`() {
        val serverUrl = "diasp.eu"
        val scheme = "http"
        val port = 443
        val clientBuilder = spyk(MastodonClient.Builder(serverUrl)) {
            // Mock internal NodeInfoClient so that we don't open the site in unit testing
            mockkObject(NodeInfoClient)
            every { NodeInfoClient.retrieveServerInfo(host = serverUrl, scheme = scheme, port = port) } returns Server(
                schemaVersion = "2.0",
                software = Server.Software(name = "diaspora", version = "0.7.18.2-p84e7e411")
            )

            val responseMock = mockk<Response> {
                every { code } answers { 404 }
                every { message } answers { "Not Found" }
                every { isSuccessful } answers { false }
                every { close() } returns Unit
            }
            every { executeInstanceRequest(any()) } answers { responseMock }
        }
            .withHttpsDisabled()
            .withPort(port)
        invoking(clientBuilder::build)
            .shouldThrow(InstanceVersionRetrievalException::class)
            .withMessage("Server $serverUrl doesn't appear to run Mastodon")
    }

    @Test
    fun `Given a server that doesn't run Mastodon, when building MastodonClient with defaults, then fail with InstanceVersionRetrievalException`() {
        val serverUrl = "diasp.eu"
        val clientBuilder = spyk(MastodonClient.Builder(serverUrl)) {
            // Mock internal NodeInfoClient so that we don't open the site in unit testing
            mockkObject(NodeInfoClient)
            every { NodeInfoClient.retrieveServerInfo(host = serverUrl) } returns Server(
                schemaVersion = "2.0",
                software = Server.Software(name = "diaspora", version = "0.7.18.2-p84e7e411")
            )

            val responseMock = mockk<Response> {
                every { code } answers { 404 }
                every { message } answers { "Not Found" }
                every { isSuccessful } answers { false }
                every { close() } returns Unit
            }
            every { executeInstanceRequest(any()) } answers { responseMock }
        }
        invoking(clientBuilder::build)
            .shouldThrow(InstanceVersionRetrievalException::class)
            .withMessage("Server $serverUrl doesn't appear to run Mastodon")
    }

    @Test
    fun `Given a server that cannot be reached, when building MastodonClient, then propagate UnknownHostException`() {
        val clientBuilder: MastodonClient.Builder = spyk(MastodonClient.Builder("unreachabledomain"))

        invoking(clientBuilder::build) shouldThrow UnknownHostException::class
    }

    @Test
    fun `Given streaming URL in instance response, when building MastodonClient, then use that streaming URL`() {
        val serverUrl = "mastodon.example"
        val expectedStreamingUrl = "wss://streaming.example.com"
        val clientBuilder = spyk(MastodonClient.Builder(serverUrl)) {
            // Mock internal NodeInfoClient so that we don't open the site in unit testing
            mockkObject(NodeInfoClient)
            every { NodeInfoClient.retrieveServerInfo(host = serverUrl) } returns Server(
                schemaVersion = "2.0",
                software = Server.Software(name = "mastodon", version = "4.0.0")
            )

            val jsonResponse = """
                {
                  "domain": "mastodon.example",
                  "title": "Mastodon Example",
                  "version": "4.3.5",
                  "source_url": "https://github.com/mastodon/mastodon",
                  "description": "A Mastodon instance for testing",
                  "configuration": {
                    "urls": {
                      "streaming": "$expectedStreamingUrl"
                    }
                  }
                }
            """.trimIndent()

            val responseMock = mockk<Response> {
                every { body } answers { jsonResponse.toResponseBody("application/json".toMediaType()) }
                every { isSuccessful } answers { true }
                every { close() } returns Unit
            }
            every { executeInstanceRequest(any()) } answers { responseMock }
        }

        val mastodonClient = clientBuilder.build()
        mastodonClient.streamingUrl shouldBeEqualTo expectedStreamingUrl.replace("wss", "https")
    }

    @Test
    fun `Given no streaming URL in instance response, when building MastodonClient, then use fallback streaming url`() {
        val serverUrl = "mastodon.example"
        val clientBuilder = spyk(MastodonClient.Builder(serverUrl)) {
            // Mock internal NodeInfoClient so that we don't open the site in unit testing
            mockkObject(NodeInfoClient)
            every { NodeInfoClient.retrieveServerInfo(host = serverUrl) } returns Server(
                schemaVersion = "2.0",
                software = Server.Software(name = "mastodon", version = "4.0.0")
            )

            val jsonResponse = """
                {
                  "domain": "mastodon.example",
                  "title": "Mastodon Example",
                  "version": "4.3.5",
                  "source_url": "https://github.com/mastodon/mastodon",
                  "description": "A Mastodon instance for testing",
                  "configuration": {
                    "urls": { }
                  }
                }
            """.trimIndent()

            val responseMock = mockk<Response> {
                every { body } answers { jsonResponse.toResponseBody("application/json".toMediaType()) }
                every { isSuccessful } answers { true }
                every { close() } returns Unit
            }
            every { executeInstanceRequest(any()) } answers { responseMock }
        }

        val mastodonClient = clientBuilder.build()
        mastodonClient.streamingUrl shouldBeEqualTo "https://$serverUrl/"
    }

    @Test
    fun `Given valid server response and no response to instance request, when building server, then use fallback streaming url`() {
        val serverUrl = "mastodon.example"
        val clientBuilder = spyk(MastodonClient.Builder(serverUrl)) {
            // Mock internal NodeInfoClient so that we don't open the site in unit testing
            mockkObject(NodeInfoClient)
            every { NodeInfoClient.retrieveServerInfo(host = serverUrl) } returns Server(
                schemaVersion = "2.0",
                software = Server.Software(name = "mastodon", version = "4.3.5")
            )

            val responseMock = mockk<Response> {
                every { body } answers {
                    "{\"error\": \"Instance info not found\"}".toResponseBody("application/json".toMediaType())
                }
                every { isSuccessful } answers { false }
                every { close() } returns Unit
            }
            every { executeInstanceRequest(any()) } answers { responseMock }
        }

        val mastodonClient = clientBuilder.build()
        mastodonClient.streamingUrl shouldBeEqualTo "https://$serverUrl/"
    }
}
