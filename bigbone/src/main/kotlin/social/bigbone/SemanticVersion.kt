package social.bigbone

/**
 * Partial implementation of semantic versioning, enough to deal with version strings as returned
 * by Mastodon, e.g. as part of Instance information or via NodeInfo. This implementation interprets
 * the first three integer values as major, minor and patch, respectively, while ignoring additional
 * information such as pre-release and build.
 * @see <a href="https://semver.org/">https://semver.org/</a>
 * @property versionString string to parse as Semantic Version
 */
class SemanticVersion(val versionString: String) : Comparable<SemanticVersion> {
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
     * Compares this SemanticVersion with another for order. Returns zero if this object is equal to the specified other
     * object, a negative number if it's less than other, or a positive number if it's greater than other.
     * @param other SemanticVersion to compare to
     */
    override fun compareTo(other: SemanticVersion): Int =
        compareBy(
            SemanticVersion::major,
            SemanticVersion::minor,
            SemanticVersion::patch
        ).compare(this, other)

    /**
     * Returns this version as a string.
     */
    override fun toString(): String = "$major.$minor.$patch"
}
