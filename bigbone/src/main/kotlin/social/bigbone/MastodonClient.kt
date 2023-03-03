package social.bigbone

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import social.bigbone.api.Pageable
import social.bigbone.api.entity.InstanceVersion
import social.bigbone.api.exception.BigBoneRequestException
import social.bigbone.api.method.AccountMethods
import social.bigbone.api.method.AppMethods
import social.bigbone.api.method.BlockMethods
import social.bigbone.api.method.FavouriteMethods
import social.bigbone.api.method.FollowRequestMethods
import social.bigbone.api.method.InstanceMethods
import social.bigbone.api.method.ListMethods
import social.bigbone.api.method.MediaMethods
import social.bigbone.api.method.MuteMethods
import social.bigbone.api.method.NotificationMethods
import social.bigbone.api.method.OAuthMethods
import social.bigbone.api.method.ReportMethods
import social.bigbone.api.method.SearchMethods
import social.bigbone.api.method.StatusMethods
import social.bigbone.api.method.StreamingMethods
import social.bigbone.api.method.TimelineMethods
import social.bigbone.extension.emptyRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * This class is used by method classes (e.g. AccountMethods, RxAcccountMethods, ...) and performs HTTP calls
 * towards the Mastodon instance specified. Request/response data is serialized/deserialized accordingly.
 */
class MastodonClient
private constructor(
    private val instanceName: String,
    private val client: OkHttpClient,
    private val gson: Gson
) {
    private var debug = false
    private var instanceVersion: String? = null

    /**
     * Access API methods under "api/vX/accounts" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("accounts")
    val accounts: AccountMethods by lazy { AccountMethods(this) }

    /**
     * Access API methods under "api/vX/apps" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("apps")
    val apps: AppMethods by lazy { AppMethods(this) }

    /**
     * Access API methods under "api/vX/blocks" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("blocks")
    val blocks: BlockMethods by lazy { BlockMethods(this) }

    /**
     * Access API methods under "api/vX/favourites" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("favourites")
    val favourites: FavouriteMethods by lazy { FavouriteMethods(this) }

    /**
     * Access API methods under "api/vX/follow_requests" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("followRequests")
    val followRequests: FollowRequestMethods by lazy { FollowRequestMethods(this) }

    /**
     * Access API methods under "api/vX/instance" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("instances")
    val instances: InstanceMethods by lazy { InstanceMethods(this) }

    /**
     * Access API methods under "api/vX/lists" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("lists")
    val lists: ListMethods by lazy { ListMethods(this) }

    /**
     * Access API methods under "api/vX/media" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("media")
    val media: MediaMethods by lazy { MediaMethods(this) }

    /**
     * Access API methods under "api/vX/mutes" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("mutes")
    val mutes: MuteMethods by lazy { MuteMethods(this) }

    /**
     * Access API methods under "api/vX/notification[s]" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("notifications")
    val notifications: NotificationMethods by lazy { NotificationMethods(this) }

    /**
     * Access API methods under "oauth" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("oauth")
    val oauth: OAuthMethods by lazy { OAuthMethods(this) }

    /**
     * Access API methods under "api/vX/reports" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("reports")
    val reports: ReportMethods by lazy { ReportMethods(this) }

    /**
     * Access API methods under "api/vX/search" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("search")
    val search: SearchMethods by lazy { SearchMethods(this) }

    /**
     * Access API methods under "api/vX/statuses" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("statuses")
    val statuses: StatusMethods by lazy { StatusMethods(this) }

    /**
     * Access API methods under "api/vX/streaming" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("streaming")
    val streaming: StreamingMethods by lazy { StreamingMethods(this) }

    /**
     * Access API methods under "api/vX/timelines" endpoint.
     */
    @Suppress("unused") // public API
    @get:JvmName("timelines")
    val timelines: TimelineMethods by lazy { TimelineMethods(this) }

    /**
     * Specifies the HTTP methods / HTTP verb that can be used by this class.
     */
    enum class Method {
        DELETE,
        GET,
        POST,
        PATCH
    }

    @PublishedApi
    internal fun getSerializer() = gson

    fun getInstanceName() = instanceName

    fun getInstanceVersion() = instanceVersion

    /**
     * Returns a MastodonRequest for the defined action, allowing to retrieve returned data.
     * @param T
     * @param endpoint the Mastodon API endpoint to call
     * @param method the HTTP method to use
     * @param parameters parameters to use in the action; can be null
     */
    internal inline fun <reified T : Any> getMastodonRequest(
        endpoint: String,
        method: Method,
        parameters: Parameters? = null
    ): MastodonRequest<T> {
        return MastodonRequest(
            {
                when (method) {
                    Method.DELETE -> delete(endpoint)
                    Method.GET -> get(endpoint, parameters)
                    Method.PATCH -> patch(endpoint, parameters)
                    Method.POST -> post(endpoint, parameters)
                }
            },
            { getSerializer().fromJson(it, T::class.java) }
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
            {
                when (method) {
                    Method.DELETE -> delete(endpoint)
                    Method.GET -> get(endpoint, parameters)
                    Method.PATCH -> patch(endpoint, parameters)
                    Method.POST -> post(endpoint, parameters)
                }
            },
            { getSerializer().fromJson(it, T::class.java) }
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
            {
                when (method) {
                    Method.DELETE -> delete(endpoint)
                    Method.GET -> get(endpoint, parameters)
                    Method.PATCH -> patch(endpoint, parameters)
                    Method.POST -> post(endpoint, parameters)
                }
            },
            { getSerializer().fromJson(it, T::class.java) }
        )
    }

    /**
     * Performs the defined action and throws an exception if unsuccessful, without returning any data.
     * @param endpoint the Mastodon API endpoint to call
     * @param method the HTTP method to use
     */
    @Throws(BigBoneRequestException::class)
    internal fun performAction(endpoint: String, method: Method) {
        val response = when (method) {
            Method.DELETE -> delete(endpoint)
            Method.GET -> get(endpoint)
            Method.PATCH -> patch(endpoint, null)
            Method.POST -> post(endpoint)
        }
        response.close()
        if (!response.isSuccessful) {
            throw BigBoneRequestException(response)
        }
    }

    /**
     * Get a response from the Mastodon instance defined for this client using the DELETE method.
     * @param path an absolute path to the API endpoint to call
     */
    fun delete(path: String): Response {
        try {
            val url = fullUrl(instanceName, path)
            debugPrintUrl(url)
            val call = client.newCall(
                Request.Builder()
                    .url(url)
                    .delete()
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
            val url = fullUrl(instanceName, path, query)
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

    /**
     * Get a response from the Mastodon instance defined for this client using the PATCH method.
     * @param path an absolute path to the API endpoint to call
     * @param body the parameters to use in the request body for this request
     */
    fun patch(path: String, body: Parameters?): Response {
        if (body == null) {
            throw BigBoneRequestException("Patch request not possible with null body")
        }

        try {
            val url = fullUrl(instanceName, path)
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
     */
    fun post(path: String, body: Parameters? = null): Response =
        postRequestBody(path, parameterBody(body))

    /**
     * Get a response from the Mastodon instance defined for this client using the POST method. Use this method if
     * you need to build your own RequestBody; see post() otherwise.
     * @param path an absolute path to the API endpoint to call
     * @param body the RequestBody to use for this request
     *
     * @see post
     */
    fun postRequestBody(path: String, body: RequestBody): Response {
        try {
            val url = fullUrl(instanceName, path)
            debugPrintUrl(url)
            val call = client.newCall(
                Request.Builder()
                    .url(url)
                    .post(body)
                    .build()
            )
            return call.execute()
        } catch (e: IllegalArgumentException) {
            throw BigBoneRequestException(e)
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
         * @param instanceName host of a Mastodon instance
         * @param path Mastodon API endpoint to be called
         * @param query query part of the URL to build; may be null
         */
        fun fullUrl(instanceName: String, path: String, query: Parameters? = null): HttpUrl {
            val urlBuilder = HttpUrl.Builder()
                .scheme("https")
                .host(instanceName)
                .addEncodedPathSegments(path)
            query?.let {
                urlBuilder.encodedQuery(it.toQuery())
            }
            return urlBuilder.build()
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
        private val gson = Gson()
        private var accessToken: String? = null
        private var debug = false

        fun accessToken(accessToken: String) = apply {
            this.accessToken = accessToken
        }

        fun useStreamingApi() = apply {
            okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS)
        }

        fun debug() = apply {
            this.debug = true
        }

        private fun getInstanceVersion(): String {
            try {
                return listOf(v2InstanceRequest(), v1InstanceRequest()).stream()
                    .filter { response -> response.isSuccessful }
                    .map { response -> gson.fromJson(response.body?.string(), InstanceVersion::class.java) }
                    .map { json -> json.version }
                    .findFirst()
                    .get()
            } catch (e: Exception) {
                throw BigBoneRequestException("Unable to fetch instance version", e)
            }
        }

        /**
         * Returns the server response for an instance request of version 2.
         * @return server response for this request; if the response is not successful, its body will be closed
         */
        internal fun v2InstanceRequest(): Response = versionedInstanceRequest(2)

        /**
         * Returns the server response for an instance request of version 1.
         * @return server response for this request; if the response is not successful, its body will be closed
         */
        internal fun v1InstanceRequest(): Response = versionedInstanceRequest(1)

        /**
         * Returns the server response for an instance request of a specific version.
         * @param version value corresponding to the version that should be returned; falls
         *  back to returning version 1 for illegal values.
         * @return server response for this request; if the response is not successful, its body will be closed
         */
        private fun versionedInstanceRequest(version: Int): Response {
            val versionString = if (version == 2) {
                "v2"
            } else {
                "v1"
            }
            val client = OkHttpClient.Builder().build()
            val response = client.newCall(
                Request.Builder().url(
                    fullUrl(
                        instanceName,
                        "api/$versionString/instance"
                    )
                ).get().build()).execute()
            if (!response.isSuccessful) {
                response.close()
            }
            return response
        }

        fun build(): MastodonClient {
            return MastodonClient(
                instanceName,
                okHttpClientBuilder.addNetworkInterceptor(AuthorizationInterceptor(accessToken)).build(),
                gson
            ).also {
                it.debug = debug
                it.instanceVersion = getInstanceVersion()
            }
        }
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
