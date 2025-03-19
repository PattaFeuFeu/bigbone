package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.api.Scope
import social.bigbone.api.exception.BigBoneRequestException
import java.util.Scanner

object OAuthGetAccessToken {
    @Throws(BigBoneRequestException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val instanceName = args[0]
        val clientId = args[1]
        val clientSecret = args[2]
        val redirectUri = args[3]
        val client = MastodonClient.Builder(instanceName).build()
        val fullScope = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL)
        val state = "example_state"

        // example values generated via: https://example-app.com/pkce,
        // should be generated according to: https://oauth.net/2/pkce/
        val codeVerifier = "4d165eefac426b3e61ef652b7b44d63aad113b0540ad25f5f10bc935"
        val codeChallenge = "LYcAAS1MRj9aDDd1cgPxku0YgFtJFH6_3_RGCWZGvOc"

        val url = client.oauth.getOAuthUrl(
            clientId = clientId,
            redirectUri = redirectUri,
            scope = fullScope,
            state = state,
            codeChallenge = codeChallenge,
            // currently no other method supported
            codeChallengeMethod = "S256"
        )
        println("Open authorization page and copy code:")
        println(url)
        println("Paste code:")
        var authCode: String
        Scanner(System.`in`).use { s -> authCode = s.nextLine() }
        val token = client.oauth.getUserAccessTokenWithAuthorizationCodeGrant(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            code = authCode,
            codeVerifier = codeVerifier,
            scope = fullScope
        )
        println("Access Token:")
        println(token.execute().accessToken)
    }
}
