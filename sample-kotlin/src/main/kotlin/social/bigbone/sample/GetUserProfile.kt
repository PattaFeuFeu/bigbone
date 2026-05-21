package social.bigbone.sample

import social.bigbone.MastodonClient

object GetUserProfile {
    @JvmStatic
    fun main(args: Array<String>) {
        val instance = args[0]

        // access token with at least Scope.READ.ACCOUNTS or Scope.PROFILE.ALL
        val accessToken = args[1]

        // instantiate client
        val client = MastodonClient.Builder(instance)
            .accessToken(accessToken)
            .build()

        // Get and display user profile
        val profile = client.profile.getUserProfile().execute()
        profile.apply {
            print("$displayName (ID: $id)\n")
            print("--------------------\n")
            print("$note\n")
            print("--------------------\n")
            for (field in fields) {
                print("${field.name}: ${field.value}\n")
            }
        }
    }
}
