package social.bigbone

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import social.bigbone.api.Pageable
import social.bigbone.api.entity.streaming.MastodonApiEvent.GenericMessage
import social.bigbone.api.entity.streaming.MastodonApiEvent.StreamEvent
import social.bigbone.api.entity.streaming.ParsedStreamEvent.Companion.toStreamEvent
import social.bigbone.api.entity.streaming.RawStreamEvent
import social.bigbone.api.entity.streaming.TechnicalEvent.Closed
import social.bigbone.api.entity.streaming.TechnicalEvent.Closing
import social.bigbone.api.entity.streaming.TechnicalEvent.Failure
import social.bigbone.api.entity.streaming.TechnicalEvent.Open
import social.bigbone.api.entity.streaming.WebSocketCallback
import social.bigbone.api.exception.BigBoneClientInstantiationException
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.api.exception.InstanceVersionRetrievalException
import social.bigbone.api.method.AccountMethods
import social.bigbone.api.method.AnnouncementMethods
import social.bigbone.api.method.AppMethods
import social.bigbone.api.method.BlockMethods
import social.bigbone.api.method.BookmarkMethods
import social.bigbone.api.method.ConversationMethods
import social.bigbone.api.method.CustomEmojiMethods
import social.bigbone.api.method.DirectoryMethods
import social.bigbone.api.method.DomainBlockMethods
import social.bigbone.api.method.EmailMethods
import social.bigbone.api.method.EndorsementMethods
import social.bigbone.api.method.FavouriteMethods
import social.bigbone.api.method.FeaturedTagMethods
import social.bigbone.api.method.FilterMethods
import social.bigbone.api.method.FollowRequestMethods
import social.bigbone.api.method.FollowedTagMethods
import social.bigbone.api.method.GroupedNotificationMethods
import social.bigbone.api.method.InstanceMethods
import social.bigbone.api.method.ListMethods
import social.bigbone.api.method.MarkerMethods
import social.bigbone.api.method.MediaMethods
import social.bigbone.api.method.MuteMethods
import social.bigbone.api.method.NotificationMethods
import social.bigbone.api.method.OAuthMethods
import social.bigbone.api.method.OEmbedMethods
import social.bigbone.api.method.PollMethods
import social.bigbone.api.method.PreferenceMethods
import social.bigbone.api.method.PushNotificationMethods
import social.bigbone.api.method.ReportMethods
import social.bigbone.api.method.ScheduledStatusMethods
import social.bigbone.api.method.SearchMethods
import social.bigbone.api.method.StatusMethods
import social.bigbone.api.method.StreamingMethods
import social.bigbone.api.method.SuggestionMethods
import social.bigbone.api.method.TagMethods
import social.bigbone.api.method.TimelineMethods
import social.bigbone.api.method.TrendMethods
import social.bigbone.api.method.admin.AdminAccountMethods
import social.bigbone.api.method.admin.AdminCanonicalEmailBlockMethods
import social.bigbone.api.method.admin.AdminDimensionMethods
import social.bigbone.api.method.admin.AdminDomainAllowMethods
import social.bigbone.api.method.admin.AdminDomainBlockMethods
import social.bigbone.api.method.admin.AdminEmailDomainBlockMethods
import social.bigbone.api.method.admin.AdminIpBlockMethods
import social.bigbone.api.method.admin.AdminMeasureMethods
import social.bigbone.api.method.admin.AdminReportMethods
import social.bigbone.api.method.admin.AdminRetentionMethods
import social.bigbone.api.method.admin.AdminTrendMethods
import social.bigbone.extension.emptyRequestBody
import social.bigbone.nodeinfo.NodeInfoClient
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

/**
 * This class is used by method classes (e.g. AccountMethods, RxAcccountMethods, ...) and performs HTTP calls
 * towards the Mastodon instance specified. Request/response data is serialized/deserialized accordingly.
 */
class MastodonClient private constructor(
    private val instanceName: String,
    private val streamingUrl: String,
    private val client: OkHttpClient,
    private val accessToken: String? = null,
    private val debug: Boolean = false,
    private val instanceVersion: String? = null,
    private val scheme: String = "https",
    private val port: Int = 443
) {

    //region API methods

    /**
     * Access API methods under the "accounts" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("accounts")
    val accounts: AccountMethods by lazy { AccountMethods(this) }

    /**
     * Access API methods under the "admin/accounts" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminAccounts")
    val adminAccounts: AdminAccountMethods by lazy { AdminAccountMethods(this) }

    /**
     * Access API methods under the "admin/canonical_email_blocks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminCanonicalEmailBlocks")
    val adminCanonicalEmailBlocks: AdminCanonicalEmailBlockMethods by lazy { AdminCanonicalEmailBlockMethods(this) }

    /**
     * Access API methods under the "admin/dimensions" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminDimensions")
    val adminDimensions: AdminDimensionMethods by lazy { AdminDimensionMethods(this) }

    /**
     * Access API methods under the "admin/domain_allows" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminDomainAllow")
    val adminDomainAllow: AdminDomainAllowMethods by lazy { AdminDomainAllowMethods(this) }

    /**
     * Access API methods under the "admin/domain_blocks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminDomainBlocks")
    val adminDomainBlocks: AdminDomainBlockMethods by lazy { AdminDomainBlockMethods(this) }

    /**
     * Access API methods under the "admin/email_domain_blocks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminEmailDomainBlocks")
    val adminEmailDomainBlocks: AdminEmailDomainBlockMethods by lazy { AdminEmailDomainBlockMethods(this) }

    /**
     * Access API methods under the "admin/ip_blocks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminIpBlock")
    val adminIpBlock: AdminIpBlockMethods by lazy { AdminIpBlockMethods(this) }

    /**
     * Access API methods under the "admin/measures" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminMeasures")
    val adminMeasures: AdminMeasureMethods by lazy { AdminMeasureMethods(this) }

    /**
     * Access API methods under the "admin/reports" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminReports")
    val adminReports: AdminReportMethods by lazy { AdminReportMethods(this) }

    /**
     * Access API methods under the "admin/retention" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminRetention")
    val adminRetention: AdminRetentionMethods by lazy { AdminRetentionMethods(this) }

    /**
     * Access API methods under the "admin/trends" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("adminTrends")
    val adminTrends: AdminTrendMethods by lazy { AdminTrendMethods(this) }

    /**
     * Access API methods under the "announcements" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("announcements")
    val announcements: AnnouncementMethods by lazy { AnnouncementMethods(this) }

    /**
     * Access API methods under the "apps" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("apps")
    val apps: AppMethods by lazy { AppMethods(this) }

    /**
     * Access API methods under the "blocks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("blocks")
    val blocks: BlockMethods by lazy { BlockMethods(this) }

    /**
     * Access API methods under the "bookmarks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("bookmarks")
    val bookmarks: BookmarkMethods by lazy { BookmarkMethods(this) }

    /**
     * Access API methods under the "conversations" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("conversations")
    val conversations: ConversationMethods by lazy { ConversationMethods(this) }

    /**
     * Access API methods under the "custom_emojis" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("customEmojis")
    val customEmojis: CustomEmojiMethods by lazy { CustomEmojiMethods(this) }

    /**
     * Access API methods under the "directory" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("directories")
    val directories: DirectoryMethods by lazy { DirectoryMethods(this) }

    /**
     * Access API methods under the "domain_blocks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("domainBlocks")
    val domainBlocks: DomainBlockMethods by lazy { DomainBlockMethods(this) }

    /**
     * Access API methods under the "emails" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("emails")
    val emails: EmailMethods by lazy { EmailMethods(this) }

    /**
     * Access API methods under the "endorsements" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("endorsements")
    val endorsements: EndorsementMethods by lazy { EndorsementMethods(this) }

    /**
     * Access API methods under the "favourites" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("favourites")
    val favourites: FavouriteMethods by lazy { FavouriteMethods(this) }

    /**
     * Access API methods under the "featured_tags" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("featuredTags")
    val featuredTags: FeaturedTagMethods by lazy { FeaturedTagMethods(this) }

    /**
     * Access API methods under the "filters" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("filters")
    val filters: FilterMethods by lazy { FilterMethods(this) }

    /**
     * Access API methods under the "follow_requests" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("followRequests")
    val followRequests: FollowRequestMethods by lazy { FollowRequestMethods(this) }

    /**
     * Access API methods under the "followed_tags" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("followedTags")
    val followedTags: FollowedTagMethods by lazy { FollowedTagMethods(this) }

    /**
     * Access API methods under the "notification" endpoint that are specific to grouped notifications.
     * See also: [notifications]
     */
    @Suppress("unused") // public API
    @get:JvmName("groupedNotifications")
    val groupedNotifications: GroupedNotificationMethods by lazy { GroupedNotificationMethods(this) }

    /**
     * Access API methods under the "instance" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("instances")
    val instances: InstanceMethods by lazy { InstanceMethods(this) }

    /**
     * Access API methods under the "lists" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("lists")
    val lists: ListMethods by lazy { ListMethods(this) }

    /**
     * Access API methods under the "markers" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("markers")
    val markers: MarkerMethods by lazy { MarkerMethods(this) }

    /**
     * Access API methods under the "media" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("media")
    val media: MediaMethods by lazy { MediaMethods(this) }

    /**
     * Access API methods under the "mutes" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("mutes")
    val mutes: MuteMethods by lazy { MuteMethods(this) }

    /**
     * Access API methods under the "notification" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("notifications")
    val notifications: NotificationMethods by lazy { NotificationMethods(this) }

    /**
     * Access API methods under the "oauth" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("oauth")
    val oauth: OAuthMethods by lazy { OAuthMethods(this) }

    /**
     * Access API methods under the "oembed" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("oembed")
    val oembed: OEmbedMethods by lazy { OEmbedMethods(this) }

    /**
     * Access API methods under the "polls" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("polls")
    val polls: PollMethods by lazy { PollMethods(this) }

    /**
     * Access API methods under the "preferences" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("preferences")
    val preferences: PreferenceMethods by lazy { PreferenceMethods(this) }

    /**
     * Access API methods under "push" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("pushNotifications")
    val pushNotifications: PushNotificationMethods by lazy { PushNotificationMethods(this) }

    /**
     * Access API methods under the "reports" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("reports")
    val reports: ReportMethods by lazy { ReportMethods(this) }

    /**
     * Access API methods under the "scheduled_statuses" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("scheduledStatuses")
    val scheduledStatuses: ScheduledStatusMethods by lazy { ScheduledStatusMethods(this) }

    /**
     * Access API methods under the "search" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("search")
    val search: SearchMethods by lazy { SearchMethods(this) }

    /**
     * Access API methods under the "statuses" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("statuses")
    val statuses: StatusMethods by lazy { StatusMethods(this) }

    /**
     * Access API methods under the "streaming" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("streaming")
    val streaming: StreamingMethods by lazy { StreamingMethods(this) }

    /**
     * Access API methods under the "suggestions" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("suggestions")
    val suggestions: SuggestionMethods by lazy { SuggestionMethods(this) }

    /**
     * Access API methods under the "tags" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("tags")
    val tags: TagMethods by lazy { TagMethods(this) }

    /**
     * Access API methods under the "timelines" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("timelines")
    val timelines: TimelineMethods by lazy { TimelineMethods(this) }

    /**
     * Access API methods under the "trends" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("trends")
    val trends: TrendMethods by lazy { TrendMethods(this) }

    //endregion API methods

    /**
     * Specifies the HTTP methods / HTTP verb that can be used by this class.
     */
    enum class Method {
        DELETE,
        GET,
        PATCH,
        POST,
        PUT
    }

    fun getInstanceName() = instanceName

    fun getInstanceVersion() = instanceVersion

    fun getScheme() = scheme

    fun getPort() = port

    /**
     * Returns a MastodonRequest for the defined action, allowing to retrieve returned data.
     * @param T
     * @param endpoint the Mastodon API endpoint to call
     * @param method the HTTP method to use
     * @param parameters parameters to use in the action; can be null
     * @param addIdempotencyKey if true, adds idempotency key to header to avoid duplicate POST requests
     */
    internal inline fun <reified T : Any> getMastodonRequest(
        endpoint: String,
        method: Method,
        parameters: Parameters? = null,
        addIdempotencyKey: Boolean = false
    ): MastodonRequest<T> {
        return MastodonRequest(
            executor = {
                when (method) {
                    Method.DELETE -> delete(endpoint, parameters)
                    Method.GET -> get(endpoint, parameters)
                    Method.PATCH -> patch(endpoint, parameters)
                    Method.POST -> post(endpoint, parameters, addIdempotencyKey)
                    Method.PUT -> put(endpoint, parameters)
                }
            },
            mapper = { JSON_SERIALIZER.decodeFromString<T>(it) }
        )
    }

    /**
     * Returns a MastodonRequest for the defined action, allowing to retrieve returned data as a Pageable.
     * @param T
     * @param endpoint the Mastodon API endpoint to call
     * @param method the HTTP method to use
     * @param parameters parameters to use in the action; can be null
     */
    internal inline fun <reified T : Any> getPageableMastodonRequest(
        endpoint: String,
        method: Method,
        parameters: Parameters? = null
    ): MastodonRequest<Pageable<T>> {
        return MastodonRequest<Pageable<T>>(
            executor = {
                when (method) {
                    Method.DELETE -> delete(endpoint, parameters)
                    Method.GET -> get(endpoint, parameters)
                    Method.PATCH -> patch(endpoint, parameters)
                    Method.POST -> post(endpoint, parameters)
                    Method.PUT -> put(endpoint, parameters)
                }
            },
            mapper = { JSON_SERIALIZER.decodeFromString<T>(it) }
        ).toPageable()
    }

    /**
     * Returns a MastodonRequest for the defined action, allowing to retrieve returned data as a List.
     * @param T
     * @param endpoint the Mastodon API endpoint to call
     * @param method the HTTP method to use
     * @param parameters parameters to use in the action; can be null
     */
    internal inline fun <reified T : Any> getMastodonRequestForList(
        endpoint: String,
        method: Method,
        parameters: Parameters? = null
    ): MastodonRequest<List<T>> {
        return MastodonRequest(
            executor = {
                when (method) {
                    Method.DELETE -> delete(endpoint, parameters)
                    Method.GET -> get(endpoint, parameters)
                    Method.PATCH -> patch(endpoint, parameters)
                    Method.POST -> post(endpoint, parameters)
                    Method.PUT -> put(endpoint, parameters)
                }
            },
            mapper = { JSON_SERIALIZER.decodeFromString<T>(it) }
        )
    }

    /**
     * Performs the defined action and throws an exception if unsuccessful, without returning any data.
     * @param endpoint the Mastodon API endpoint to call
     * @param method the HTTP method to use
     * @param parameters parameters to use in the action; can be null
     * @throws BigBoneRequestException in case the action to be performed yielded an unsuccessful response
     */
    @Throws(BigBoneRequestException::class)
    internal fun performAction(endpoint: String, method: Method, parameters: Parameters? = null) {
        when (method) {
            Method.DELETE -> delete(endpoint, parameters)
            Method.GET -> get(endpoint, parameters)
            Method.PATCH -> patch(endpoint, parameters)
            Method.POST -> post(endpoint, parameters)
            Method.PUT -> put(endpoint, parameters)
        }.use { response: Response ->
            if (!response.isSuccessful) {
                throw BigBoneRequestException(response)
            }
        }
    }

    /**
     * Get a response from the Mastodon instance defined for this client using the DELETE method.
     * @param path an absolute path to the API endpoint to call
     * @param body the parameters to use in the request body for this request
     */
    fun delete(path: String, body: Parameters?): Response {
        try {
            val url = fullUrl(scheme, instanceName, port, path)
            debugPrintUrl(url)
            val call = client.newCall(
                Request.Builder()
                    .url(url)
                    .delete(parameterBody(body))
                    .build()
            )
            return call.execute()
        } catch (e: IOException) {
            throw BigBoneRequestException("Request not executed due to network IO issue", e)
        }
    }

    /**
     * Get a response from the Mastodon instance defined for this client using the GET method.
     * @param path an absolute path to the API endpoint to call
     * @param query the parameters to use as query string for this request; may be null
     */
    fun get(path: String, query: Parameters? = null): Response {
        try {
            val url = fullUrl(scheme, instanceName, port, path, query)
            debugPrintUrl(url)
            val call = client.newCall(
                Request.Builder()
                    .url(url)
                    .get()
                    .build()
            )
            return call.execute()
        } catch (e: IOException) {
            throw BigBoneRequestException("Request not executed due to network IO issue", e)
        }
    }

    fun stream(parameters: Parameters?, callback: WebSocketCallback): WebSocket {
        val webSocketClient: OkHttpClient = client
            .newBuilder()
            .pingInterval(60L, TimeUnit.SECONDS)
            .build()

        return webSocketClient.newWebSocket(
            request = Request.Builder()
                /*
                OKHTTP doesn't currently (at least when checked in 4.12.0) use the [AuthorizationInterceptor] for
                WebSocket connections, so we need to set it in the header ourselves again here.
                See also: https://github.com/square/okhttp/issues/6454
                 */
                .header("Authorization", "Bearer $accessToken")
                .url(
                    fullUrl(
                        existingUrl = streamingUrl.toHttpUrl(),
                        path = "api/v1/streaming",
                        queryParameters = parameters
                    )
                )
                .build(),
            listener = object : WebSocketListener() {
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    callback.onEvent(Closed(code, reason))
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    callback.onEvent(Closing(code, reason))
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    callback.onEvent(Failure(t))

                    /*
                    onFailure represents a terminal event and no further events would be received by this web socket,
                    so we close it.
                    1002 indicates that an endpoint is terminating the connection due to a protocol error.
                    see: https://datatracker.ietf.org/doc/html/rfc6455#section-7.4
                     */
                    webSocket.close(
                        code = 1002,
                        reason = null
                    )
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    // We should usually be able to decode WebSocket messages as an [Event] type but if that fails,
                    // we return the text received in this message verbatim via the [GenericMessage] type.
                    try {
                        val rawEvent: RawStreamEvent = JSON_SERIALIZER.decodeFromString<RawStreamEvent>(text)
                        val streamEvent = rawEvent.toStreamEvent()
                        callback.onEvent(StreamEvent(event = streamEvent, streamTypes = rawEvent.stream))
                    } catch (e: IllegalArgumentException) {
                        callback.onEvent(GenericMessage(text))
                    }
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    // We should usually be able to decode WebSocket messages as an [Event] type but if that fails,
                    // we return the text received in this message verbatim via the [GenericMessage] type.
                    val bytesAsString: String = bytes.utf8()
                    try {
                        val rawEvent = JSON_SERIALIZER.decodeFromString<RawStreamEvent>(bytesAsString)
                        val streamEvent = rawEvent.toStreamEvent()
                        callback.onEvent(StreamEvent(event = streamEvent, streamTypes = rawEvent.stream))
                    } catch (e: IllegalArgumentException) {
                        callback.onEvent(GenericMessage(bytesAsString))
                    }
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    callback.onEvent(Open)
                }
            }
        )
    }

    /**
     * Get a response from the Mastodon instance defined for this client using the PATCH method.
     * @param path an absolute path to the API endpoint to call
     * @param body the parameters to use in the request body for this request
     */
    fun patch(path: String, body: Parameters?): Response {
        try {
            val url = fullUrl(scheme, instanceName, port, path)
            debugPrintUrl(url)
            val call = client.newCall(
                Request.Builder()
                    .url(url)
                    .patch(parameterBody(body))
                    .build()
            )
            return call.execute()
        } catch (e: IOException) {
            throw BigBoneRequestException("Request not executed due to network IO issue", e)
        }
    }

    /**
     * Get a response from the Mastodon instance defined for this client using the POST method.
     * @param path an absolute path to the API endpoint to call
     * @param body the parameters to use in the request body for this request; may be null
     * @param addIdempotencyKey if true, generate idempotency key for this request
     */
    fun post(path: String, body: Parameters? = null, addIdempotencyKey: Boolean = false): Response {
        val idempotencyKey = if (addIdempotencyKey) {
            body?.uuid()
        } else {
            null
        }
        return postRequestBody(path, parameterBody(body), idempotencyKey)
    }

    /**
     * Get a response from the Mastodon instance defined for this client using the POST method. Use this method if
     * you need to build your own RequestBody; see post() otherwise.
     * @param path an absolute path to the API endpoint to call
     * @param body the RequestBody to use for this request
     * @param idempotencyKey optional idempotency value to avoid duplicate calls
     *
     * @see post
     */
    fun postRequestBody(path: String, body: RequestBody, idempotencyKey: String? = null): Response {
        try {
            val url = fullUrl(scheme, instanceName, port, path)
            debugPrintUrl(url)
            val call = client.newCall(
                Request.Builder().apply {
                    url(url)
                    post(body)
                    idempotencyKey?.let {
                        header("Idempotency-Key", it)
                    }
                }.build()
            )
            return call.execute()
        } catch (e: IllegalArgumentException) {
            throw BigBoneRequestException(e)
        } catch (e: IOException) {
            throw BigBoneRequestException("Request not executed due to network IO issue", e)
        }
    }

    /**
     * Get a response from the Mastodon instance defined for this client using the [Method.PUT] method.
     *
     * @param path an absolute path to the API endpoint to call
     * @param body the parameters to use in the request body for this request
     */
    fun put(path: String, body: Parameters?): Response = putRequestBody(path, parameterBody(body))

    /**
     * Get a response from the Mastodon instance defined for this client using the [Method.PUT] method.
     * Use this method if you need to build your own RequestBody, otherwise [put] is probably the better option for you.
     *
     * @param path an absolute path to the API endpoint to call
     * @param body the RequestBody to use for this request
     */
    fun putRequestBody(path: String, body: RequestBody): Response {
        try {
            val url = fullUrl(scheme, instanceName, port, path)
            debugPrintUrl(url)
            val call = client.newCall(
                Request.Builder()
                    .url(url)
                    .put(body)
                    .build()
            )
            return call.execute()
        } catch (e: IOException) {
            throw BigBoneRequestException("Request not executed due to network IO issue", e)
        }
    }

    /**
     * In debug mode, print out any accessed URL. Debug mode can be activated via MastodonClient.Builder.debug().
     */
    private fun debugPrintUrl(url: HttpUrl) {
        if (debug) {
            println(url.toUrl().toString())
        }
    }

    companion object {
        /**
         * Returns a HttpUrl.
         * @param scheme the schema to be used, either "http" or "https"
         * @param instanceName the Mastodon instance hostname
         * @param port the TCP/IP port to connect to, usually 443
         * @param path Mastodon API endpoint to be called
         * @param query query part of the URL to build; may be null
         */
        @JvmOverloads
        fun fullUrl(scheme: String, instanceName: String, port: Int, path: String, query: Parameters? = null): HttpUrl {
            val urlBuilder = HttpUrl.Builder()
                .scheme(scheme)
                .host(instanceName)
                .port(port)
                .addEncodedPathSegments(path)
            query?.let {
                urlBuilder.encodedQuery(it.toQuery())
            }
            return urlBuilder.build()
        }

        /**
         * Adds [path] and optional [queryParameters] parameters to the [existingUrl] to create a new [HttpUrl].
         *
         * @param existingUrl HttpUrl to add [path] and [queryParameters] to
         * @param path Mastodon API endpoint to be called
         * @param queryParameters query part of the URL to build; may be null
         */
        @JvmOverloads
        fun fullUrl(
            existingUrl: HttpUrl,
            path: String,
            queryParameters: Parameters? = null
        ): HttpUrl {
            with(existingUrl.newBuilder()) {
                addEncodedPathSegments(path)
                queryParameters?.let { encodedQuery(queryParameters.toQuery()) }

                return build()
            }
        }

        /**
         * Returns a RequestBody matching the given parameters.
         * @param parameters list of parameters to use; may be null, in which case the returned RequestBody will be empty
         */
        fun parameterBody(parameters: Parameters?): RequestBody {
            return parameters
                ?.toQuery()
                ?.toRequestBody("application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull())
                ?: emptyRequestBody()
        }
    }

    /**
     * The builder used to create a new instance of [MastodonClient]. New instances of [MastodonClient] should
     * be created using this builder only.
     */
    class Builder(
        private val instanceName: String
    ) {
        private val okHttpClientBuilder = OkHttpClient.Builder()
        private var accessToken: String? = null
        private var debug = false
        private var scheme = "https"
        private var port = 443
        private var trustAllCerts = false
        private var readTimeoutSeconds = 10L
        private var writeTimeoutSeconds = 10L
        private var connectTimeoutSeconds = 10L

        /**
         * Sets the access token required for calling authenticated endpoints.
         * @param accessToken the access token to be used
         */
        fun accessToken(accessToken: String) = apply {
            this.accessToken = accessToken
        }

        /**
         * Makes the client use an unsecured HTTP connection to the Mastodon server.
         *
         * IMPORTANT: Please do not use this on production environments, as it is considered
         * bad practice. Use it solely for testing purposes.
         */
        fun withHttpsDisabled() = apply {
            scheme = "http"
        }

        /**
         * Disables certificate validation and hostname verification.
         *
         * IMPORTANT: Please do not use this on production environments, as it is considered
         * bad practice. Use it solely for testing purposes.
         */
        fun withTrustAllCerts() = apply {
            trustAllCerts = true
            configureForTrustAll(okHttpClientBuilder)
        }

        /**
         * Makes the client use a different port than 443 for connecting to a Mastodon server.
         *
         * IMPORTANT: It is best practice to use the default port 443 when connecting to a
         * Mastodon server.
         */
        fun withPort(port: Int) = apply {
            this.port = port
        }

        /**
         * Sets the read timeout for connections of this client.
         * @param timeoutSeconds the new timeout value in seconds; default value is 10 seconds, setting this to 0
         *  disables the timeout completely
         */
        fun setReadTimeoutSeconds(timeoutSeconds: Long) = apply {
            readTimeoutSeconds = timeoutSeconds
        }

        /**
         * Sets the write timeout for connections of this client.
         * @param timeoutSeconds the new timeout value in seconds; default value is 10 seconds, setting this to 0
         *  disables the timeout completely
         */
        fun setWriteTimeoutSeconds(timeoutSeconds: Long) = apply {
            writeTimeoutSeconds = timeoutSeconds
        }

        /**
         * Sets the connect timeout for connections of this client.
         * @param timeoutSeconds the new timeout value in seconds; default value is 10 seconds, setting this to 0
         *  disables the timeout completely
         */
        fun setConnectTimeoutSeconds(timeoutSeconds: Long) = apply {
            connectTimeoutSeconds = timeoutSeconds
        }

        fun debug() = apply {
            this.debug = true
        }

        private fun getStreamingApiUrl(fallbackUrl: String): String {
            executeInstanceRequest().use { response: Response ->
                if (!response.isSuccessful) return fallbackUrl

                val streamingUrl: String? = response.body.string().let { responseBody: String ->
                    val rawJsonObject = JSON_SERIALIZER
                        .parseToJsonElement(responseBody)
                        .jsonObject

                    (rawJsonObject["configuration"]?.jsonObject?.get("urls")?.jsonObject?.get("streaming") as? JsonPrimitive)
                        ?.contentOrNull
                        // okhttp’s HttpUrl which is used later to parse this result only allows http(s)
                        // so we need to replace ws(s) first
                        ?.replace("ws:", "http:")
                        ?.replace("wss:", "https:")
                }

                return streamingUrl ?: fallbackUrl
            }
        }

        /**
         * Get the version string for this Mastodon instance.
         * @return a string corresponding to the version of this Mastodon instance
         * @throws BigBoneClientInstantiationException if instance version cannot be retrieved
         */
        @Throws(BigBoneClientInstantiationException::class)
        private fun getInstanceVersion(): String {
            val serverVersion: String? = NodeInfoClient
                .retrieveServerInfo(host = instanceName, scheme = scheme, port = port)
                ?.software
                ?.takeIf { it.name == "mastodon" }
                ?.version

            if (serverVersion == null) throw InstanceVersionRetrievalException(message = "Server $instanceName doesn't appear to run Mastodon")

            return serverVersion
        }

        private fun configureForTrustAll(clientBuilder: OkHttpClient.Builder) {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(TrustAllX509TrustManager), SecureRandom())
            clientBuilder
                .sslSocketFactory(sslContext.socketFactory, TrustAllX509TrustManager)
                .hostnameVerifier(AcceptAllHostnameVerifier)
        }

        /**
         * Executes a call to the instance API endpoint.
         *
         * Can be used to gather information about this instance, e.g. to find out the streaming URL to be used.
         *
         * The [Response] returned by this method needs to be closed by the caller, either by reading from it
         * via [ResponseBody.string], or by calling [Response.close].
         * @return Server response as [Response] for this request
         */
        internal fun executeInstanceRequest(): Response {
            val clientBuilder = OkHttpClient.Builder()
            if (trustAllCerts) configureForTrustAll(clientBuilder)

            return clientBuilder
                .build()
                .newCall(
                    Request.Builder()
                        .url(
                            fullUrl(
                                scheme = scheme,
                                instanceName = instanceName,
                                port = port,
                                path = "api/v2/instance"
                            )
                        )
                        .get()
                        .build()
                )
                .execute()
        }

        /**
         * Builds this MastodonClient.
         *
         * @throws BigBoneClientInstantiationException if the client could not be instantiated, likely due to an issue
         * when getting the instance version of the server in [instanceName]. Other exceptions, e.g. due to no Internet
         * connection are _not_ caught by this library.
         */
        fun build(): MastodonClient {
            return MastodonClient(
                instanceName = instanceName,
                client = okHttpClientBuilder
                    .addNetworkInterceptor(AuthorizationInterceptor(accessToken))
                    .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
                    .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                    .build(),
                accessToken = accessToken,
                debug = debug,
                instanceVersion = getInstanceVersion(),
                scheme = scheme,
                port = port,
                streamingUrl = getStreamingApiUrl(fallbackUrl = scheme + instanceName)
            )
        }
    }

    private object TrustAllX509TrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    private object AcceptAllHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String?, session: SSLSession?): Boolean = true
    }

    private class AuthorizationInterceptor(val accessToken: String? = null) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val compressedRequest = originalRequest.newBuilder()
                .headers(originalRequest.headers)
                .method(originalRequest.method, originalRequest.body)
                .apply {
                    accessToken?.let {
                        header("Authorization", String.format("Bearer %s", it))
                    }
                }
                .build()
            return chain.proceed(compressedRequest)
        }
    }
}
