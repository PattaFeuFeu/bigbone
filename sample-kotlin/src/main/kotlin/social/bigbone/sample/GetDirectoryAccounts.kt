package social.bigbone.sample

import social.bigbone.MastodonClient
import social.bigbone.PrecisionDateTime
import social.bigbone.api.method.DirectoryMethods

object GetDirectoryAccounts {
    @JvmStatic
    fun main(args: Array<String>) {
        val instance = args[0]

        // instantiate client
        val client = MastodonClient.Builder(instance)
            .build()

        // get 40 local accounts that were recently active, skipping the first ten
        val accounts = client.directories.listAccounts(
            local = true,
            order = DirectoryMethods.AccountOrder.ACTIVE,
            offset = 10,
            limit = 40
        ).execute()

        // do something with the result; here, we find the oldest account still active and output information about it
        val oldestAccount = accounts
            .filter { it.createdAt.isValid() }
            .minBy { (it.createdAt as PrecisionDateTime.ValidPrecisionDateTime).instant }
        oldestAccount.let {
            println("@${it.acct}@$instance posted ${it.statusesCount} times since ${it.createdAt}")
        }
    }
}
