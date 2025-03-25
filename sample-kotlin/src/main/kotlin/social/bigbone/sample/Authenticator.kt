package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.api.Scope
import social.bigbone.api.entity.Application
import social.bigbone.api.entity.Token
import social.bigbone.api.method.OAuthMethods
import java.io.File
import java.util.Properties

object Authenticator {
    private const val CLIENT_ID = "client_id"
    private const val CLIENT_SECRET = "client_secret"
    private const val ACCESS_TOKEN = "access_token"
    private const val REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"

    // Request all non-admin scopes during app registration and while getting the user access token,
    // because individual samples might require any or all of them. Generally, an app should request
    // the minimum necessary for its intended functionality.
    private val fullScope = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.PUSH.ALL)

    fun appRegistrationIfNeeded(instanceName: String, credentialFilePath: String): MastodonClient {
        val file = File(credentialFilePath)
        if (!file.exists()) {
            println("create $credentialFilePath.")
            file.createNewFile()
        }
        val properties = Properties()
        println("load $credentialFilePath.")
        properties.load(file.inputStream())
        if (properties[CLIENT_ID] == null) {
            println("try app registration...")
            val appRegistration = appRegistration(instanceName)
            properties[CLIENT_ID] = appRegistration.clientId
            properties[CLIENT_SECRET] = appRegistration.clientSecret
            properties.store(file.outputStream(), "app registration")
        } else {
            println("app registration found...")
        }
        val clientId = properties[CLIENT_ID]?.toString() ?: error("client id not found")
        val clientSecret = properties[CLIENT_SECRET]?.toString() ?: error("client secret not found")

        if (properties[ACCESS_TOKEN] == null) {
            println("get access token for $instanceName...")
            println("please input your email...")
            val email = System.`in`.bufferedReader().readLine()
            println("please input your password...")
            val pass = System.`in`.bufferedReader().readLine()
            val accessToken = getUserAccessToken(
                instanceName,
                clientId,
                clientSecret,
                email,
                pass
            )
            properties[ACCESS_TOKEN] = accessToken.accessToken
            properties.store(file.outputStream(), "app registration")
        } else {
            println("access token found...")
        }
        return MastodonClient.Builder(instanceName)
            .accessToken(properties[ACCESS_TOKEN].toString())
            .build()
    }

    private fun getUserAccessToken(
        instanceName: String,
        clientId: String,
        clientSecret: String,
        email: String,
        password: String
    ): Token {
        val client = MastodonClient.Builder(instanceName).build()
        val oAuthMethods = OAuthMethods(client)
        return oAuthMethods.getUserAccessTokenWithPasswordGrant(clientId, clientSecret, REDIRECT_URI, email, password, fullScope).execute()
    }

    private fun appRegistration(instanceName: String): Application {
        val client = MastodonClient.Builder(instanceName).build()
        return client.apps.createApp(
            clientName = "bigbone-sample-app",
            redirectUris = REDIRECT_URI,
            scope = fullScope
        ).execute()
    }
}
