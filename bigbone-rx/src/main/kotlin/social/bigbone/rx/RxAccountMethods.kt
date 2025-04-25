package social.bigbone.rx

import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.Account
import social.bigbone.api.entity.CredentialAccount
import social.bigbone.api.entity.FamiliarFollowers
import social.bigbone.api.entity.FeaturedTag
import social.bigbone.api.entity.Instance
import social.bigbone.api.entity.MastodonList
import social.bigbone.api.entity.Relationship
import social.bigbone.api.entity.Status
import social.bigbone.api.entity.Token
import social.bigbone.api.entity.data.Visibility
import social.bigbone.api.method.AccountMethods
import java.time.Duration
import java.time.LocalDate

/**
 * Reactive implementation of [AccountMethods].
 * Allows access to API methods with endpoints having an "api/vX/accounts" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/accounts/">Mastodon accounts API methods</a>
 */
class RxAccountMethods(client: MastodonClient) {

    private val accountMethods = AccountMethods(client)

    /**
     * Register an account.
     * @param username The desired username for the account
     * @param email The email address to be used for login
     * @param password The password to be used for login
     * @param agreement Whether the user agrees to the local rules, terms, and policies. These should be presented
     * to the user in order to allow them to consent before setting this parameter to TRUE.
     * @param locale The language of the confirmation email that will be sent
     * @param reason If registrations require manual approval, this text will be reviewed by moderators.
     * @param dateOfBirth If a minimum age is required (see [Instance.Registrations.minAge]), this date of birth must be provided and will be checked against.
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#create">Mastodon API documentation: methods/accounts/#create</a>
     */
    fun registerAccount(
        username: String,
        email: String,
        password: String,
        agreement: Boolean,
        locale: String,
        reason: String?,
        dateOfBirth: LocalDate?
    ): Single<Token> = Single.fromCallable {
        accountMethods.registerAccount(username, email, password, agreement, locale, reason, dateOfBirth).execute()
    }

    /**
     * View information about a profile.
     * @param accountId ID of the profile to look up
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#get">Mastodon API documentation: methods/accounts/#get</a>
     */
    fun getAccount(accountId: String): Single<Account> = Single.fromCallable {
        accountMethods.getAccount(accountId).execute()
    }

    /**
     * Test to make sure that the user token works.
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#verify_credentials">Mastodon API documentation: methods/accounts/#verify_credentials</a>
     */
    fun verifyCredentials(): Single<CredentialAccount> = Single.fromCallable {
        accountMethods.verifyCredentials().execute()
    }

    /**
     * Update the user’s display and preferences.
     *
     * You should use [verifyCredentials] to first obtain plaintext representations from within the source parameter,
     * then allow the user to edit these plaintext representations before submitting them through this API.
     * The server will generate the corresponding HTML.
     *
     * @param displayName The name to display in the user's profile
     * @param note A new biography for the user
     * @param avatar A String containing a base64-encoded image to display as the user's avatar
     *  (e.g. data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAUoAAADrCAYAAAA...)
     * @param header A String containing a base64-encoded image to display as the user's header image
     *  (e.g. data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAUoAAADrCAYAAAA...)
     * @param locked Whether manual approval of follow requests is required
     * @param bot Whether the account has a bot flag
     * @param discoverable Whether the account should be shown in the profile directory
     * @param hideCollections Whether to hide followers and followed accounts.
     * @param indexable Whether public posts should be searchable to anyone
     * @param profileFields The profile fields to be set
     * @param defaultPostVisibility Default post privacy for authored statuses
     * @param defaultSensitiveMark Whether to mark authored statuses as sensitive by default
     * @param defaultLanguage Default language to use for authored statuses (ISO 6391)
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#update_credentials">Mastodon API documentation: methods/accounts/#update_credentials</a>
     */
    fun updateCredentials(
        displayName: String?,
        note: String?,
        avatar: String?,
        header: String?,
        locked: Boolean?,
        bot: Boolean?,
        discoverable: Boolean?,
        hideCollections: Boolean?,
        indexable: Boolean?,
        profileFields: AccountMethods.ProfileFields?,
        defaultPostVisibility: Visibility?,
        defaultSensitiveMark: Boolean?,
        defaultLanguage: String?
    ): Single<CredentialAccount> = Single.fromCallable {
        accountMethods.updateCredentials(
            displayName = displayName,
            note = note,
            avatar = avatar,
            header = header,
            locked = locked,
            bot = bot,
            discoverable = discoverable,
            hideCollections = hideCollections,
            indexable = indexable,
            profileFields = profileFields,
            defaultPostVisibility = defaultPostVisibility,
            defaultSensitiveMark = defaultSensitiveMark,
            defaultLanguage = defaultLanguage
        ).execute()
    }

    /**
     * Accounts which follow the given account, if network is not hidden by the account owner.
     * @param accountId ID of the account to look up
     * @param range optional Range for the pageable return value
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#followers">Mastodon API documentation: methods/accounts/#followers</a>
     */
    @JvmOverloads
    fun getFollowers(accountId: String, range: Range = Range()): Single<Pageable<Account>> = Single.fromCallable {
        accountMethods.getFollowers(accountId, range).execute()
    }

    /**
     * Accounts which the given account is following, if network is not hidden by the account owner.
     * @param accountId ID of the account to look up
     * @param range optional Range for the pageable return value
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#following">Mastodon API documentation: methods/accounts/#following</a>
     */
    @JvmOverloads
    fun getFollowing(accountId: String, range: Range = Range()): Single<Pageable<Account>> = Single.fromCallable {
        accountMethods.getFollowing(accountId, range).execute()
    }

    /**
     * Statuses posted to the given account.
     *
     * @param accountId ID of the account to look up
     * @param onlyMedia Filter out statuses without attachments.
     * @param excludeReplies Filter out statuses in reply to a different account.
     * @param excludeReblogs Filter out boosts from the response.
     * @param pinned Filter for pinned statuses only.
     * @param filterByTaggedWith Filter for statuses using a specific hashtag.
     * @param range optional Range for the pageable return value
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#statuses">Mastodon API documentation: methods/accounts/#statuses</a>
     */
    @JvmOverloads
    fun getStatuses(
        accountId: String,
        onlyMedia: Boolean = false,
        excludeReplies: Boolean = false,
        excludeReblogs: Boolean = false,
        pinned: Boolean = false,
        filterByTaggedWith: String? = null,
        range: Range = Range()
    ): Single<Pageable<Status>> = Single.fromCallable {
        accountMethods.getStatuses(
            accountId = accountId,
            onlyMedia = onlyMedia,
            excludeReplies = excludeReplies,
            excludeReblogs = excludeReblogs,
            pinned = pinned,
            filterByTaggedWith = filterByTaggedWith,
            range = range
        ).execute()
    }

    /**
     * Tags featured by this account.
     *
     * @param accountId The ID of the [Account] in the database.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#featured_tags">Mastodon API documentation: methods/accounts/#featured_tags</a>
     */
    fun getFeaturedTags(accountId: String): Single<List<FeaturedTag>> = Single.fromCallable {
        accountMethods.getFeaturedTags(accountId).execute()
    }

    /**
     * User lists that you have added this account to.
     *
     * @param accountId The ID of the [Account] in the database.
     * @return If the account is part of any lists, their [MastodonList] entities will be returned.
     * Otherwise, an empty list is returned.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#lists">Mastodon API documentation: methods/accounts/#lists</a>
     */
    fun getListsContainingAccount(accountId: String): Single<List<MastodonList>> = Single.fromCallable {
        accountMethods.getListsContainingAccount(accountId).execute()
    }

    /**
     * Follow the given account. Can also be used to update whether to show reblogs or enable notifications.
     *
     * @param accountId ID of the account to follow
     * @param includeReblogs Receive this account’s reblogs in home timeline? Defaults to true if null.
     * @param notifyOnStatus Receive notifications when this account posts a status? Defaults to false if null.
     * @param filterForLanguages Filter received statuses for these languages.
     * If not provided, you will receive this account’s posts in all languages.
     *
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#follow">Mastodon API documentation: methods/accounts/#follow</a>
     */
    @JvmOverloads
    fun followAccount(
        accountId: String,
        includeReblogs: Boolean? = null,
        notifyOnStatus: Boolean? = null,
        filterForLanguages: List<String>? = null
    ): Single<Relationship> = Single.fromCallable {
        accountMethods.followAccount(
            accountId = accountId,
            includeReblogs = includeReblogs,
            notifyOnStatus = notifyOnStatus,
            filterForLanguages = filterForLanguages
        ).execute()
    }

    /**
     * Unfollow the given account.
     * @param accountId ID of the account to unfollow
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#unfollow">Mastodon API documentation: methods/accounts/#unfollow</a>
     */
    fun unfollowAccount(accountId: String): Single<Relationship> = Single.fromCallable {
        accountMethods.unfollowAccount(accountId).execute()
    }

    /**
     * Remove the given account from your followers.
     *
     * @param accountId The ID of the [Account] in the database.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#remove_from_followers">
     *     Mastodon API documentation: methods/accounts/#remove_from_followers</a>
     */
    fun removeAccountFromFollowers(accountId: String): Single<Relationship> = Single.fromCallable {
        accountMethods.removeAccountFromFollowers(accountId).execute()
    }

    /**
     * Block the given account. Clients should filter statuses from this account if received (e.g. due to a boost in the Home timeline)
     * @param accountId ID of the account to block
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#block">Mastodon API documentation: methods/accounts/#block</a>
     */
    fun blockAccount(accountId: String): Single<Relationship> = Single.fromCallable {
        accountMethods.blockAccount(accountId).execute()
    }

    /**
     * Unblock the given account.
     * @param accountId ID of the account to unblock
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#unblock">Mastodon API documentation: methods/accounts/#unblock</a>
     */
    fun unblockAccount(accountId: String): Single<Relationship> = Single.fromCallable {
        accountMethods.unblockAccount(accountId).execute()
    }

    /**
     * Mute the given account.
     * Clients should filter statuses and notifications from this account, if received (e.g. due to a boost in the Home timeline).
     *
     * @param accountId ID of the account to mute
     * @param muteNotifications Mute notifications in addition to statuses? Defaults to true.
     * @param muteDuration How long the mute should last, in seconds. Defaults to null (mute indefinitely)
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#mute">Mastodon API documentation: methods/accounts/#mute</a>
     */
    @JvmOverloads
    fun muteAccount(
        accountId: String,
        muteNotifications: Boolean? = null,
        muteDuration: Duration? = null
    ): Single<Relationship> = Single.fromCallable {
        accountMethods.muteAccount(
            accountId = accountId,
            muteNotifications = muteNotifications,
            muteDuration = muteDuration
        ).execute()
    }

    /**
     * Unmute the given account.
     * @param accountId ID of the account to mute
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#unmute">Mastodon API documentation: methods/accounts/#unmute</a>
     */
    fun unmuteAccount(accountId: String): Single<Relationship> = Single.fromCallable {
        accountMethods.unmuteAccount(accountId).execute()
    }

    /**
     * Add the given account to the user’s featured profiles.
     * (Featured profiles are currently shown on the user’s own public profile.)
     *
     * @param accountId The ID of the [Account] in the database.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#pin">Mastodon API documentation: methods/accounts/#pin</a>
     */
    fun featureAccountOnProfile(accountId: String): Single<Relationship> = Single.fromCallable {
        accountMethods.featureAccountOnProfile(accountId).execute()
    }

    /**
     * Remove the given account from the user’s featured profiles.
     * This reverts [featureAccountOnProfile].
     *
     * @param accountId The ID of the [Account] in the database.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#unpin">Mastodon API documentation: methods/accounts/#unpin</a>
     */
    fun unfeatureAccountFromProfile(accountId: String): Single<Relationship> = Single.fromCallable {
        accountMethods.unfeatureAccountFromProfile(accountId).execute()
    }

    /**
     * Sets a private note on a user.
     *
     * @param accountId The ID of the [Account] in the database.
     * @param privateNote The comment to be set on that user. Provide empty string or null to clear currently set note.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#note">Mastodon API documentation: methods/accounts/#note</a>
     */
    fun setPrivateNoteOnProfile(
        accountId: String,
        privateNote: String?
    ): Single<Relationship> = Single.fromCallable {
        accountMethods.setPrivateNoteOnProfile(accountId, privateNote).execute()
    }

    /**
     * Find out whether a given account is followed, blocked, muted, etc.
     *
     * @param accountIds List of IDs of the accounts to check
     * @param includeSuspended Whether relationships should be returned for suspended users. Defaults to false if not supplied.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#relationships">Mastodon API documentation: methods/accounts/#relationships</a>
     */
    @JvmOverloads
    fun getRelationships(
        accountIds: List<String>,
        includeSuspended: Boolean? = null
    ): Single<List<Relationship>> = Single.fromCallable {
        accountMethods.getRelationships(accountIds, includeSuspended).execute()
    }

    /**
     * Obtain a list of all accounts that follow the specified accounts, filtered for accounts you follow.
     *
     * @param accountIds Account IDs to find familiar followers for.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#familiar_followers">Mastodon API documentation: methods/accounts/#familiar_followers</a>
     */
    fun findFamiliarFollowers(accountIds: List<String>): Single<List<FamiliarFollowers>> = Single.fromCallable {
        accountMethods.findFamiliarFollowers(accountIds).execute()
    }

    /**
     * Search for matching accounts by username or display name.
     *
     * @param query the search query
     * @param limit the maximum number of matching accounts to return (default: 40. max: 80)
     * @param offset skips this amount of results
     * @param limitToFollowing Limit the search to users you are following. Defaults to false if not supplied.
     * @param attemptWebFingerLookup Use this when [query] is an exact address. Defaults to false if not supplied.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#search">Mastodon API documentation: methods/accounts/#search</a>
     */
    @JvmOverloads
    fun searchAccounts(
        query: String,
        limit: Int? = null,
        offset: Int? = null,
        limitToFollowing: Boolean? = null,
        attemptWebFingerLookup: Boolean? = null
    ): Single<List<Account>> = Single.fromCallable {
        accountMethods.searchAccounts(
            query = query,
            limit = limit,
            offset = offset,
            limitToFollowing = limitToFollowing,
            attemptWebFingerLookup = attemptWebFingerLookup
        ).execute()
    }

    /**
     * Quickly lookup a username to see if it is available, skipping WebFinger resolution.
     *
     * @param usernameOrAddress The username or WebFinger address to lookup.
     *
     * @see <a href="https://docs.joinmastodon.org/methods/accounts/#unpin">Mastodon API documentation: methods/accounts/#unpin</a>
     * @see <a href="https://docs.joinmastodon.org/spec/webfinger/">Mastodon API documentation: WebFinger information</a>
     */
    fun getAccountViaWebFingerAddress(usernameOrAddress: String): Single<Account> = Single.fromCallable {
        accountMethods.getAccountViaWebFingerAddress(usernameOrAddress).execute()
    }
}
