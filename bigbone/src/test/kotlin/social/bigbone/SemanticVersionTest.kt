package social.bigbone

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class SemanticVersionTest {
    @Test
    fun parseValidVersionStrings() {
        // given valid version strings, when parsing them as SemanticVersion
        val semVerStandardRelease = SemanticVersion("4.3.8")
        val semVerNightly = SemanticVersion("4.4.0-nightly.2025-05-24")
        val semVerQoto = SemanticVersion("3.5.19-qoto")

        // then these should have the major, minor, patch values expected
        semVerStandardRelease.valid shouldBeEqualTo true
        semVerStandardRelease.major shouldBeEqualTo 4
        semVerStandardRelease.minor shouldBeEqualTo 3
        semVerStandardRelease.patch shouldBeEqualTo 8

        semVerNightly.valid shouldBeEqualTo true
        semVerNightly.major shouldBeEqualTo 4
        semVerNightly.minor shouldBeEqualTo 4
        semVerNightly.patch shouldBeEqualTo 0

        semVerQoto.valid shouldBeEqualTo true
        semVerQoto.major shouldBeEqualTo 3
        semVerQoto.minor shouldBeEqualTo 5
        semVerQoto.patch shouldBeEqualTo 19
    }

    @Test
    fun parseInvalidVersionString() {
        // given invalid version strings, when parsing them as SemanticVersion
        val leadingV = SemanticVersion("v1.2.3")
        val nonVersion = SemanticVersion("foobar")

        // then these should not be valid either
        leadingV.valid shouldBeEqualTo false
        nonVersion.valid shouldBeEqualTo false
    }

    @Test
    fun checkEquality() {
        // given valid version strings that are either the same or different
        // according to our partial implementation of semantic versioning,
        // when parsing them as SemanticVersion
        val semVer1 = SemanticVersion("1.2.3")
        val semVer2 = SemanticVersion("1.2.3-foo")
        val semVer3 = SemanticVersion("1.2.3+bar")
        val semVerDifferent = SemanticVersion("2.3.4")

        // then the ones that are the same should return true when comparing
        semVer1.isEqualTo(semVer2) shouldBeEqualTo true
        semVer2.isEqualTo(semVer3) shouldBeEqualTo true
        semVer3.isEqualTo(semVer1) shouldBeEqualTo true

        // and the one that is different should return false when comparing
        semVer1.isEqualTo(semVerDifferent) shouldBeEqualTo false
    }

    @Test
    fun checkComparison() {
        // given valid version strings that are different, when parsing them as SemanticVersion
        val semVer123 = SemanticVersion("1.2.3")
        val semVer124 = SemanticVersion("1.2.4")
        val semVer130 = SemanticVersion("1.3.0")
        val semVer400 = SemanticVersion("4.0.0")

        // then larger ones should return true when compared to smaller ones
        semVer400.isLargerThan(semVer130) shouldBeEqualTo true
        semVer130.isLargerThan(semVer124) shouldBeEqualTo true
        semVer124.isLargerThan(semVer123) shouldBeEqualTo true

        // and smaller ones should return false when compared to larger ones
        semVer123.isLargerThan(semVer124) shouldBeEqualTo false
        semVer124.isLargerThan(semVer130) shouldBeEqualTo false
        semVer130.isLargerThan(semVer400) shouldBeEqualTo false

        // and one compared to itself should return false for "isLarger"
        semVer123.isLargerThan(semVer123) shouldBeEqualTo false

        // but true for "isAtLeast"
        semVer123.isAtLeast(semVer123) shouldBeEqualTo true
    }
}
