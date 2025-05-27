package social.bigbone

/**
 * Partial implementation of semantic versioning, enough to deal with version strings as returned
 * by Mastodon, e.g. as part of Instance information or via NodeInfo. This implementation interprets
 * the first three integer values as major, minor and patch, respectively, while ignoring additional
 * information such as pre-release and build.
 * @see <a href="https://semver.org/">https://semver.org/</a>
 * @property versionString string to parse as Semantic Version
 */
class SemanticVersion(val versionString: String) {
    /**
     * Major version number for this Semantic Version.
     */
    val major: Int

    /**
     * Minor version number for this Semantic Version.
     */
    val minor: Int

    /**
     * Patch version number for this Semantic Version.
     */
    val patch: Int

    init {
        val versionStringParts = versionString.split(".", "-", "+")
        println(versionStringParts.size)
        for (part in versionStringParts) {
            println(part)
        }
        major = versionStringParts.getOrNull(0)?.toIntOrNull() ?: -1
        minor = versionStringParts.getOrNull(1)?.toIntOrNull() ?: 0
        patch = versionStringParts.getOrNull(2)?.toIntOrNull() ?: 0
    }

    /**
     * Returns true if at least [major] is a valid value; false else.
     */
    val valid: Boolean
        get() = major > -1

    /**
     * Returns true if both versions are valid, and all of [major], [minor] and [patch] are equal; false else.
     * @param other another version to compare to
     */
    fun isEqualTo(other: SemanticVersion): Boolean =
        valid && other.valid && major == other.major && minor == other.minor && patch == other.patch

    /**
     * Returns true if this version is larger than [other]; false else.
     * @param other another version to compare to
     */
    fun isLargerThan(other: SemanticVersion): Boolean {
        if (!valid) return false
        if (!other.valid) return true
        if (major < other.major) return false
        if (major > other.major) return true
        if (minor < other.minor) return false
        if (minor > other.minor) return true
        return patch > other.patch
    }

    /**
     * Returns true if this version is at least as large as [other]; false else.
     * @param other another version to compare to
     */
    fun isAtLeast(other: SemanticVersion): Boolean = this.isEqualTo(other) || this.isLargerThan(other)

    /**
     * Returns this version as a string.
     */
    override fun toString(): String = "$major.$minor.$patch"
}
