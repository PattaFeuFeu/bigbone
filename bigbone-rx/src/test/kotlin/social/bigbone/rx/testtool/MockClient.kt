package social.bigbone.rx.testtool

import io.mockk.every
import io.mockk.mockk
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.WebSocket
import social.bigbone.MastodonClient
import social.bigbone.Parameters
import social.bigbone.api.entity.streaming.WebSocketCallback
import social.bigbone.api.entity.streaming.WebSocketEvent
import social.bigbone.api.exception.BigBoneRequestException
import java.net.SocketTimeoutException

object MockClient {

    /**
     * Mocks a [MastodonClient] for functions testing the websocket streaming APIs.
     *
     * @param events [WebSocketEvent]s that should be lined up to be returned by the [WebSocketCallback]
     */
    fun mockWebSocket(events: Collection<WebSocketEvent>): MastodonClient {
        val webSocket = mockk<WebSocket> {
            every { close(any<Int>(), any<String>()) } returns true
        }
        return mockk<MastodonClient> {
            every { stream(any<Parameters>(), any<WebSocketCallback>()) } answers {
                events.forEach { event: WebSocketEvent -> secondArg<WebSocketCallback>().onEvent(event) }
                webSocket
            }
        }
    }

    fun mockClearText(
        clearTextResponse: String,
        requestUrl: String = "https://example.com"
    ): MastodonClient {
        val response: Response = Response.Builder()
            .code(200)
            .message("OK")
            .request(Request(url = requestUrl.toHttpUrl()))
            .protocol(Protocol.HTTP_1_1)
            .body(clearTextResponse.toResponseBody())
            .build()

        return mockk {
            every { get(any<String>(), any()) } returns response
            every {
                this@mockk["performAction"](
                    any<String>(),
                    any<MastodonClient.Method>(),
                    any<Parameters>()
                )
            } returns response
        }
    }

    fun mock(
        jsonName: String,
        maxId: String? = null,
        sinceId: String? = null,
        requestUrl: String = "https://example.com",
        responseBaseUrl: String = "https://mstdn.jp/api/v1/timelines/public"
    ): MastodonClient {
        val client: MastodonClient = mockk()
        val response: Response = Response.Builder()
            .code(200)
            .message("OK")
            .request(Request(url = requestUrl.toHttpUrl()))
            .protocol(Protocol.HTTP_1_1)
            .body(
                AssetsUtil
                    .readFromAssets(jsonName)
                    .toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull())
            )
            .apply {
                val linkHeader = buildList {
                    maxId?.let {
                        add("""<$responseBaseUrl?limit=20&local=true&max_id=$it>; rel="next"""")
                    }
                    sinceId?.let {
                        add("""<$responseBaseUrl?limit=20&local=true&since_id=$it>; rel="prev"""")
                    }
                }
                if (linkHeader.isNotEmpty()) {
                    header(
                        name = "link",
                        value = linkHeader.joinToString(separator = ",")
                    )
                }
            }
            .build()
        every { client.get(any<String>(), any()) } returns response
        every {
            client["performAction"](
                any<String>(),
                any<MastodonClient.Method>(),
                any<Parameters>()
            )
        } returns response

        return client
    }

    fun failWithResponse(
        responseJsonAssetPath: String,
        responseCode: Int,
        message: String
    ): MastodonClient {
        val clientMock: MastodonClient = mockk()
        val responseBodyMock: ResponseBody = mockk()
        every { responseBodyMock.toString() } throws SocketTimeoutException()
        val response: Response = Response.Builder()
            .code(responseCode)
            .message(message)
            .request(Request(url = "https://test.com/".toHttpUrl()))
            .protocol(Protocol.HTTP_1_1)
            .body(
                AssetsUtil.readFromAssets(responseJsonAssetPath)
                    .toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull())
            )
            .build()

        every { clientMock.delete(any<String>(), any<Parameters>()) } throws BigBoneRequestException(response)
        every { clientMock.get(any<String>(), any<Parameters>()) } throws BigBoneRequestException(response)
        every { clientMock.patch(any<String>(), any<Parameters>()) } throws BigBoneRequestException(response)
        every { clientMock.post(any<String>(), any<Parameters>(), any<Boolean>()) } throws BigBoneRequestException(
            response
        )
        every { clientMock.postRequestBody(any<String>(), any<RequestBody>()) } throws BigBoneRequestException(response)
        every { clientMock.put(any<String>(), any<Parameters>()) } throws BigBoneRequestException(response)
        every {
            clientMock["performAction"](
                any<String>(),
                any<MastodonClient.Method>(),
                any<Parameters>()
            )
        } throws BigBoneRequestException(response)
        return clientMock
    }
}
