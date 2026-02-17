package social.bigbone.api

/**
 * Represents the access permissions that can be requested when registering an app or when requesting access tokens.
 * Existing scopes are available via members of this class, arranged hierarchically. It is recommended that you request
 * as little as possible for your application.
 * @param scopes individual scopes that should be requested.
 * @see <a href="https://docs.joinmastodon.org/api/oauth-scopes/">Mastodon OAuth scopes</a>
 */
@Suppress("UseDataClass") // data class can not be used with primary constructor vararg parameters
class Scope(private vararg val scopes: Name) {

    /**
     * Interface to define scopes. While existing Mastodon scopes are generally available as objects of the [Scope]
     * class, potential new scopes defined by Mastodon but not yet implemented here can be added by using this interface.
     */
    fun interface Name {
        fun name(): String
    }

    /**
     * Check if this Scope contains a specific, individual scope.
     * @param scopeName the name of the individual scope to check for
     * @return true if the scope is contained, either directly or by its parent scope; false if not
     */
    fun contains(scopeName: Name): Boolean {
        if (scopes.contains(scopeName)) {
            return true
        }
        if (!scopeName.name().contains(':')) {
            return false
        }

        /*
        If the scope name indicates a child scope (i.e. contains a colon),
        check if we contain the parent scope (currently, checking one level up is sufficient).
         */
        val superScopeName = scopeName.name().substringBeforeLast(':')
        return scopes.any { it.name() == superScopeName }
    }

    /**
     * Check if this Scope is a subset of another Scope. A Scope is a subset of another, if the other
     * Scope contains at least all the permissions of the original Scope, and potentially more.
     * @param otherScope another Scope to compare to
     * @return true if this Scope is a subset of the other; false if not
     */
    fun isSubsetOf(otherScope: Scope): Boolean = scopes.all { scopeName -> otherScope.contains(scopeName) }

    /**
     * Scopes granting access to read data are grouped here. Use [READ.ALL] to request full read access, or preferably
     * individual child scopes to limit access to a minimum.
     */
    object READ {

        /**
         * Grants access to read all data. Requesting this scope is equivalent to requesting all other [READ] child scopes.
         */
        @JvmField
        val ALL = Name { "read" }

        @JvmField
        val ACCOUNTS = Name { "read:accounts" }

        @JvmField
        val BLOCKS = Name { "read:blocks" }

        @JvmField
        val BOOKMARKS = Name { "read:bookmarks" }

        @JvmField
        val FAVOURITES = Name { "read:favourites" }

        @JvmField
        val FILTERS = Name { "read:filters" }

        @JvmField
        val FOLLOWS = Name { "read:follows" }

        @JvmField
        val LISTS = Name { "read:lists" }

        @JvmField
        val MUTES = Name { "read:mutes" }

        @JvmField
        val NOTIFICATIONS = Name { "read:notifications" }

        @JvmField
        val SEARCH = Name { "read:search" }

        @JvmField
        val STATUSES = Name { "read:statuses" }
    }

    /**
     * Scopes granting access to write data are grouped here. Use [WRITE.ALL] to request full write access, or preferably
     * individual child scopes to limit access to a minimum.
     */
    object WRITE {

        /**
         * Grants access to write all data. Requesting this scope is equivalent to requesting all other child scopes.
         */
        @JvmField
        val ALL = Name { "write" }

        @JvmField
        val ACCOUNTS = Name { "write:accounts" }

        @JvmField
        val BLOCKS = Name { "write:blocks" }

        @JvmField
        val BOOKMARKS = Name { "write:bookmarks" }

        @JvmField
        val CONVERSATIONS = Name { "write:conversations" }

        @JvmField
        val FAVOURITES = Name { "write:favourites" }

        @JvmField
        val FILTERS = Name { "write:filters" }

        @JvmField
        val FOLLOWS = Name { "write:follows" }

        @JvmField
        val LISTS = Name { "write:lists" }

        @JvmField
        val MEDIA = Name { "write:media" }

        @JvmField
        val MUTES = Name { "write:mutes" }

        @JvmField
        val NOTIFICATIONS = Name { "write:notifications" }

        @JvmField
        val REPORTS = Name { "write:reports" }

        @JvmField
        val STATUSES = Name { "write:statuses" }
    }

    /**
     * Scopes granting access to profile information about the currently authenticated user.
     * Currently, only [PROFILE.ALL] exists.
     */
    object PROFILE {

        /**
         * Grants access to only minimal information about the currently authenticated user.
         */
        @JvmField
        val ALL = Name { "profile" }
    }

    /**
     * Scopes granting access to Web Push API subscriptions are grouped here. Currently, only [PUSH.ALL] exists.
     */
    object PUSH {

        /**
         * Grants access to Web Push API subscriptions.
         */
        @JvmField
        val ALL = Name { "push" }
    }

    /**
     * Scopes granting access to the moderation API are grouped here, split between [ADMIN.READ] and [ADMIN.WRITE] for
     * read and write access, respectively.
     */
    object ADMIN {

        /**
         * Scopes granting read access to the moderation API are grouped here. Use [ADMIN.READ.ALL] to request full
         * read access, or preferably individual child scopes to limit access to a minimum.
         */
        object READ {

            /**
             * Grants read access to the moderation API. Requesting this scope is equivalent to requesting all other
             * child scopes.
             */
            @JvmField
            val ALL = Name { "admin:read" }

            @JvmField
            val ACCOUNTS = Name { "admin:read:accounts" }

            @JvmField
            val REPORTS = Name { "admin:read:reports" }

            @JvmField
            val DOMAIN_ALLOWS = Name { "admin:read:domain_allows" }

            @JvmField
            val DOMAIN_BLOCKS = Name { "admin:read:domain_blocks" }

            @JvmField
            val IP_BLOCKS = Name { "admin:read:ip_blocks" }

            @JvmField
            val EMAIL_DOMAIN_BLOCKS = Name { "admin:read:email_domain_blocks" }

            @JvmField
            val CANONICAL_EMAIL_BLOCKS = Name { "admin:read:canonical_email_blocks" }
        }

        /**
         * Scopes granting write access to the moderation API are grouped here. Use [ADMIN.WRITE.ALL] to request full
         * write access, or preferably individual child scopes to limit access to a minimum.
         */
        object WRITE {

            /**
             * Grants write access to the moderation API. Requesting this scope is equivalent to requesting all other
             * child scopes.
             */
            @JvmField
            val ALL = Name { "admin:write" }

            @JvmField
            val ACCOUNTS = Name { "admin:write:accounts" }

            @JvmField
            val REPORTS = Name { "admin:write:reports" }

            @JvmField
            val DOMAIN_ALLOWS = Name { "admin:write:domain_allows" }

            @JvmField
            val DOMAIN_BLOCKS = Name { "admin:write:domain_blocks" }

            @JvmField
            val IP_BLOCKS = Name { "admin:write:ip_blocks" }

            @JvmField
            val EMAIL_DOMAIN_BLOCKS = Name { "admin:write:email_domain_blocks" }

            @JvmField
            val CANONICAL_EMAIL_BLOCKS = Name { "admin:write:canonical_email_blocks" }
        }
    }

    override fun toString(): String = scopes.distinct().joinToString(separator = SCOPE_DELIMITER, transform = { it.name() })

    companion object {
        /**
         * The character used as delimiter between individual scopes in a scope string.
         */
        private const val SCOPE_DELIMITER = " "

        /**
         * A map of all individual scopes, with their string representations as keys.
         */
        internal val scopesByName = mapOf(
            Pair(READ.ALL.name(), READ.ALL),
            Pair(READ.ACCOUNTS.name(), READ.ACCOUNTS),
            Pair(READ.BLOCKS.name(), READ.BLOCKS),
            Pair(READ.BOOKMARKS.name(), READ.BOOKMARKS),
            Pair(READ.FAVOURITES.name(), READ.FAVOURITES),
            Pair(READ.FILTERS.name(), READ.FILTERS),
            Pair(READ.FOLLOWS.name(), READ.FOLLOWS),
            Pair(READ.LISTS.name(), READ.LISTS),
            Pair(READ.MUTES.name(), READ.MUTES),
            Pair(READ.NOTIFICATIONS.name(), READ.NOTIFICATIONS),
            Pair(READ.SEARCH.name(), READ.SEARCH),
            Pair(READ.STATUSES.name(), READ.STATUSES),

            Pair(WRITE.ALL.name(), WRITE.ALL),
            Pair(WRITE.ACCOUNTS.name(), WRITE.ACCOUNTS),
            Pair(WRITE.BLOCKS.name(), WRITE.BLOCKS),
            Pair(WRITE.BOOKMARKS.name(), WRITE.BOOKMARKS),
            Pair(WRITE.CONVERSATIONS.name(), WRITE.CONVERSATIONS),
            Pair(WRITE.FAVOURITES.name(), WRITE.FAVOURITES),
            Pair(WRITE.FILTERS.name(), WRITE.FILTERS),
            Pair(WRITE.FOLLOWS.name(), WRITE.FOLLOWS),
            Pair(WRITE.LISTS.name(), WRITE.LISTS),
            Pair(WRITE.MEDIA.name(), WRITE.MEDIA),
            Pair(WRITE.MUTES.name(), WRITE.MUTES),
            Pair(WRITE.NOTIFICATIONS.name(), WRITE.NOTIFICATIONS),
            Pair(WRITE.REPORTS.name(), WRITE.REPORTS),
            Pair(WRITE.STATUSES.name(), WRITE.STATUSES),

            Pair(PROFILE.ALL.name(), PROFILE.ALL),

            Pair(PUSH.ALL.name(), PUSH.ALL),

            Pair(ADMIN.READ.ALL.name(), ADMIN.READ.ALL),
            Pair(ADMIN.READ.ACCOUNTS.name(), ADMIN.READ.ACCOUNTS),
            Pair(ADMIN.READ.REPORTS.name(), ADMIN.READ.REPORTS),
            Pair(ADMIN.READ.DOMAIN_ALLOWS.name(), ADMIN.READ.DOMAIN_ALLOWS),
            Pair(ADMIN.READ.DOMAIN_BLOCKS.name(), ADMIN.READ.DOMAIN_BLOCKS),
            Pair(ADMIN.READ.IP_BLOCKS.name(), ADMIN.READ.IP_BLOCKS),
            Pair(ADMIN.READ.EMAIL_DOMAIN_BLOCKS.name(), ADMIN.READ.EMAIL_DOMAIN_BLOCKS),
            Pair(ADMIN.READ.CANONICAL_EMAIL_BLOCKS.name(), ADMIN.READ.CANONICAL_EMAIL_BLOCKS),

            Pair(ADMIN.WRITE.ALL.name(), ADMIN.WRITE.ALL),
            Pair(ADMIN.WRITE.ACCOUNTS.name(), ADMIN.WRITE.ACCOUNTS),
            Pair(ADMIN.WRITE.REPORTS.name(), ADMIN.WRITE.REPORTS),
            Pair(ADMIN.WRITE.DOMAIN_ALLOWS.name(), ADMIN.WRITE.DOMAIN_ALLOWS),
            Pair(ADMIN.WRITE.DOMAIN_BLOCKS.name(), ADMIN.WRITE.DOMAIN_BLOCKS),
            Pair(ADMIN.WRITE.IP_BLOCKS.name(), ADMIN.WRITE.IP_BLOCKS),
            Pair(ADMIN.WRITE.EMAIL_DOMAIN_BLOCKS.name(), ADMIN.WRITE.EMAIL_DOMAIN_BLOCKS),
            Pair(ADMIN.WRITE.CANONICAL_EMAIL_BLOCKS.name(), ADMIN.WRITE.CANONICAL_EMAIL_BLOCKS),
        )

        /**
         * Check if a string is a valid representation of a scope.
         * @param scopeString the string to check
         * @return true if all substrings of the space-delimited string are valid scopes; false if not
         */
        fun scopeStringIsValid(scopeString: String): Boolean {
            return scopeString
                .split(SCOPE_DELIMITER)
                .none { scopeName -> scopesByName[scopeName] == null }
        }

        /**
         * Create a [Scope] from its string representation.
         * @param scopeString string representation of a Scope
         * @param failOnUnknownValues if true (default), will fail when encountering unknown values;
         *  otherwise, will ignore them and return a potentially smaller Scope
         * @return the Scope for the given string.
         * @throws IllegalArgumentException if the scope string contains unknown values (see [scopeStringIsValid]);
         *  to force parsing an invalid string, call with `failOnUnknownValues = false`
         */
        @JvmOverloads
        fun fromString(scopeString: String, failOnUnknownValues: Boolean = true): Scope {
            if (failOnUnknownValues && !scopeStringIsValid(scopeString)) {
                throw IllegalArgumentException("Scope string contains unknown values")
            }

            val knownScopes: List<Name> = scopeString
                .split(SCOPE_DELIMITER)
                .mapNotNull { scopeName -> scopesByName[scopeName] }
            return Scope(*knownScopes.toTypedArray())
        }
    }
}
