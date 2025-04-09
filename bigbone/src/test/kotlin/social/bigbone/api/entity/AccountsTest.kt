package social.bigbone.api.entity

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import social.bigbone.JSON_SERIALIZER
import social.bigbone.PrecisionDateTime
import social.bigbone.api.entity.data.Visibility
import social.bigbone.testtool.AssetsUtil

class AccountsTest {
    @Test
    fun deserializeAccounts() {
        // given a JSON string containing valid Account data
        val json = AssetsUtil.readFromAssets("accounts.json")

        // when parsing as a list of Account
        val list: List<Account> = JSON_SERIALIZER.decodeFromString(json)

        // then retrieving Account data should be possible
        list.first().id shouldBeEqualTo "11111"
    }

    @Test
    fun deserializeMutedAccounts() {
        // given a JSON string containing valid MutedAccount data
        val json = AssetsUtil.readFromAssets("mutes.json")

        // when parsing as a list of MutedAccount
        val list: List<MutedAccount> = JSON_SERIALIZER.decodeFromString(json)

        // then retrieving MutedAccount data should be possible
        list[0].id shouldBeEqualTo "11111"
        list[0].muteExpiresAt shouldBeInstanceOf PrecisionDateTime.ValidPrecisionDateTime.ExactTime::class
        list[1].id shouldBeEqualTo "22222"
        list[1].muteExpiresAt shouldBeInstanceOf PrecisionDateTime.InvalidPrecisionDateTime.Unavailable::class
        list[2].id shouldBeEqualTo "33333"
        list[2].muteExpiresAt shouldBeInstanceOf PrecisionDateTime.InvalidPrecisionDateTime.Unavailable::class
    }

    @Test
    fun deserializeCredentialAccount() {
        // given a JSON string containing valid CredentialAccount data
        val json = AssetsUtil.readFromAssets("credential_account.json")

        // when parsing as a CredentialAccount
        val credentialAccount: CredentialAccount = JSON_SERIALIZER.decodeFromString(json)

        // then retrieving CredentialAccount data should be possible
        credentialAccount.attributionDomains.size shouldBeEqualTo 2
        credentialAccount.attributionDomains.first() shouldBeEqualTo "example.com"
        credentialAccount.source.note.startsWith("Lorem ipsum dolor sit amet") shouldBeEqualTo true
        credentialAccount.source.privacy shouldBeEqualTo Visibility.PUBLIC
        credentialAccount.source.fields.first().name shouldBeEqualTo "Website"
    }

    @Test
    fun muteAccountsToAccounts() {
        // given a JSON string containing valid MutedAccount data
        val json = AssetsUtil.readFromAssets("mutes.json")

        // when parsing as a list of MutedAccount, and getting an Account from the first MutedAccount in the list
        val list: List<MutedAccount> = JSON_SERIALIZER.decodeFromString(json)
        val mutedAccount = list.first()
        val account = mutedAccount.toAccount()

        // then this Account should contain the same data as the original MutedAccount
        mutedAccount.id shouldBeEqualTo account.id
        mutedAccount.username shouldBeEqualTo account.username
        mutedAccount.acct shouldBeEqualTo account.acct
        mutedAccount.url shouldBeEqualTo account.url
        mutedAccount.displayName shouldBeEqualTo account.displayName
        mutedAccount.note shouldBeEqualTo account.note
        mutedAccount.avatar shouldBeEqualTo account.avatar
        mutedAccount.avatarStatic shouldBeEqualTo account.avatarStatic
        mutedAccount.header shouldBeEqualTo account.header
        mutedAccount.headerStatic shouldBeEqualTo account.headerStatic
        mutedAccount.isLocked shouldBeEqualTo account.isLocked
        mutedAccount.fields shouldBeEqualTo account.fields
        mutedAccount.emojis shouldBeEqualTo account.emojis
        mutedAccount.isBot shouldBeEqualTo account.isBot
        mutedAccount.isGroup shouldBeEqualTo account.isGroup
        mutedAccount.isDiscoverable shouldBeEqualTo account.isDiscoverable
        mutedAccount.isNotIndexed shouldBeEqualTo account.isNotIndexed
        mutedAccount.moved shouldBeEqualTo account.moved
        mutedAccount.isSuspended shouldBeEqualTo account.isSuspended
        mutedAccount.isLimited shouldBeEqualTo account.isLimited
        mutedAccount.createdAt shouldBeEqualTo account.createdAt
        mutedAccount.lastStatusAt shouldBeEqualTo account.lastStatusAt
        mutedAccount.statusesCount shouldBeEqualTo account.statusesCount
        mutedAccount.followersCount shouldBeEqualTo account.followersCount
        mutedAccount.followingCount shouldBeEqualTo account.followingCount
    }

    @Test
    fun credentialAccountToAccount() {
        // given a JSON string containing valid CredentialAccount data
        val json = AssetsUtil.readFromAssets("credential_account.json")

        // when parsing as a CredentialAccount, and getting an Account from it
        val credentialAccount: CredentialAccount = JSON_SERIALIZER.decodeFromString(json)
        val account = credentialAccount.toAccount()

        // then this Account should contain the same data as the original CredentialAccount
        credentialAccount.id shouldBeEqualTo account.id
        credentialAccount.username shouldBeEqualTo account.username
        credentialAccount.acct shouldBeEqualTo account.acct
        credentialAccount.url shouldBeEqualTo account.url
        credentialAccount.displayName shouldBeEqualTo account.displayName
        credentialAccount.note shouldBeEqualTo account.note
        credentialAccount.avatar shouldBeEqualTo account.avatar
        credentialAccount.avatarStatic shouldBeEqualTo account.avatarStatic
        credentialAccount.header shouldBeEqualTo account.header
        credentialAccount.headerStatic shouldBeEqualTo account.headerStatic
        credentialAccount.isLocked shouldBeEqualTo account.isLocked
        credentialAccount.fields shouldBeEqualTo account.fields
        credentialAccount.emojis shouldBeEqualTo account.emojis
        credentialAccount.isBot shouldBeEqualTo account.isBot
        credentialAccount.isGroup shouldBeEqualTo account.isGroup
        credentialAccount.isDiscoverable shouldBeEqualTo account.isDiscoverable
        credentialAccount.isNotIndexed shouldBeEqualTo account.isNotIndexed
        credentialAccount.moved shouldBeEqualTo account.moved
        credentialAccount.isSuspended shouldBeEqualTo account.isSuspended
        credentialAccount.isLimited shouldBeEqualTo account.isLimited
        credentialAccount.createdAt shouldBeEqualTo account.createdAt
        credentialAccount.lastStatusAt shouldBeEqualTo account.lastStatusAt
        credentialAccount.statusesCount shouldBeEqualTo account.statusesCount
        credentialAccount.followersCount shouldBeEqualTo account.followersCount
        credentialAccount.followingCount shouldBeEqualTo account.followingCount
    }
}
