package social.bigbone.api

import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test

class ScopeTest {

    @Test
    fun buildScope() {
        // when creating a scope
        val scope = Scope(Scope.READ.ALL, Scope.WRITE.ACCOUNTS, Scope.PROFILE.ALL, Scope.PUSH.ALL, Scope.ADMIN.READ.ACCOUNTS)

        // after splitting the resulting space-delimited scope string
        val scopeNames = scope.toString().split(" ")

        // the list of individual scope strings should contain those corresponding to the original scopes
        scopeNames.contains(Scope.READ.ALL.name()) shouldBeEqualTo true
        scopeNames.contains(Scope.WRITE.ACCOUNTS.name()) shouldBeEqualTo true
        scopeNames.contains(Scope.PROFILE.ALL.name()) shouldBeEqualTo true
        scopeNames.contains(Scope.PUSH.ALL.name()) shouldBeEqualTo true
        scopeNames.contains(Scope.ADMIN.READ.ACCOUNTS.name()) shouldBeEqualTo true
    }

    @Test
    fun hasScopes() {
        // given a scope
        val scope = Scope(Scope.READ.ALL, Scope.WRITE.MEDIA)

        // checking for those scopes or individual child scopes returns true
        scope.contains(Scope.READ.ALL) shouldBeEqualTo true
        scope.contains(Scope.WRITE.MEDIA) shouldBeEqualTo true
        scope.contains(Scope.READ.ACCOUNTS) shouldBeEqualTo true
        scope.contains(Scope.READ.FAVOURITES) shouldBeEqualTo true

        // and checking for other scopes returns false
        scope.contains(Scope.WRITE.ALL) shouldBeEqualTo false
        scope.contains(Scope.ADMIN.WRITE.IP_BLOCKS) shouldBeEqualTo false
    }

    @Test
    fun subScopes() {
        // given various scopes
        val scopeSuper = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.ADMIN.WRITE.REPORTS)
        val scopeSub1 = Scope(Scope.READ.ALL)
        val scopeSub2 = Scope(Scope.READ.ACCOUNTS, Scope.WRITE.ALL)
        val scopeSub3 = Scope(Scope.READ.BOOKMARKS, Scope.WRITE.BOOKMARKS, Scope.ADMIN.WRITE.REPORTS)
        val scopeIncompatible = Scope(Scope.READ.ALL, Scope.ADMIN.READ.ALL)

        // checking actual sub scopes returns true
        scopeSub1.isSubsetOf(scopeSuper) shouldBeEqualTo true
        scopeSub2.isSubsetOf(scopeSuper) shouldBeEqualTo true
        scopeSub3.isSubsetOf(scopeSuper) shouldBeEqualTo true

        // and comparing a scope to itself returns true
        scopeSuper.isSubsetOf(scopeSuper) shouldBeEqualTo true

        // while checking the other way around returns false
        scopeSuper.isSubsetOf(scopeSub1) shouldBeEqualTo false
        scopeSuper.isSubsetOf(scopeSub2) shouldBeEqualTo false
        scopeSuper.isSubsetOf(scopeSub3) shouldBeEqualTo false

        // and checking incompatible scopes returns false
        scopeIncompatible.isSubsetOf(scopeSuper) shouldBeEqualTo false
    }

    @Test
    fun scopeMapEntriesMatching() {
        // check if all pairs in our map of all scopes match
        for (pair in Scope.scopesByName) {
            pair.key shouldBeEqualTo pair.value.name()
        }
    }

    @Test
    fun scopesFromString() {
        // given various scopes
        val scope1 = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.ADMIN.WRITE.REPORTS)
        val scope2 = Scope(Scope.READ.ALL)
        val scope3 = Scope(Scope.READ.ACCOUNTS, Scope.WRITE.ALL)
        val scope4 = Scope(Scope.READ.BOOKMARKS, Scope.WRITE.BOOKMARKS, Scope.ADMIN.WRITE.REPORTS)
        val scope5 = Scope(Scope.READ.ALL, Scope.ADMIN.READ.ALL)

        // when creating new scopes from the original scope's string representation
        val newScope1 = Scope.fromString(scope1.toString())
        val newScope2 = Scope.fromString(scope2.toString())
        val newScope3 = Scope.fromString(scope3.toString())
        val newScope4 = Scope.fromString(scope4.toString())
        val newScope5 = Scope.fromString(scope5.toString())

        // then these new scopes match the original scopes in terms of their permission
        scope1.isSubsetOf(newScope1) shouldBeEqualTo true
        newScope1.isSubsetOf(scope1) shouldBeEqualTo true

        scope2.isSubsetOf(newScope2) shouldBeEqualTo true
        newScope2.isSubsetOf(scope2) shouldBeEqualTo true

        scope3.isSubsetOf(newScope3) shouldBeEqualTo true
        newScope3.isSubsetOf(scope3) shouldBeEqualTo true

        scope4.isSubsetOf(newScope4) shouldBeEqualTo true
        newScope4.isSubsetOf(scope4) shouldBeEqualTo true

        scope5.isSubsetOf(newScope5) shouldBeEqualTo true
        newScope5.isSubsetOf(scope5) shouldBeEqualTo true
    }

    @Test
    fun handlingUnsupportedValuesInScopeString() {
        // given a scope string containing known and unknown permissions
        val scope = Scope(Scope.READ.ALL, Scope.WRITE.ALL, Scope.ADMIN.WRITE.REPORTS)
        val scopeString = "$scope admin:read:tolstoy"

        // this string should be considered invalid
        Scope.scopeStringIsValid(scopeString) shouldBeEqualTo false

        // and trying to create a Scope should throw an exception
        invoking { Scope.fromString(scopeString) } shouldThrow IllegalArgumentException::class

        // but creating a Scope ignoring unknown values should succeed and result in
        // the set of known permissions
        val newScope = Scope.fromString(scopeString, failOnUnknownValues = false)
        scope.isSubsetOf(newScope) shouldBeEqualTo true
        newScope.isSubsetOf(scope) shouldBeEqualTo true
    }
}
