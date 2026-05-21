package social.bigbone.api.entity

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import social.bigbone.JSON_SERIALIZER
import social.bigbone.testtool.AssetsUtil

class ProfileTest {

    @Test
    fun deserialize() {
        val json = AssetsUtil.readFromAssets("profile.json")
        val profile: Profile = JSON_SERIALIZER.decodeFromString(json)
        profile.id shouldBeEqualTo "116222600881276277"
        profile.displayName shouldBeEqualTo "Documentation user"
        profile.note shouldBeEqualTo "I'm only here as an example for documentation"
        profile.fields.size shouldBeEqualTo 1
        profile.fields[0].name shouldBeEqualTo "pronouns"
        profile.fields[0].value shouldBeEqualTo "it/its"
        profile.avatar shouldBeEqualTo null
        profile.avatarStatic shouldBeEqualTo null
        profile.avatarDescription shouldBeEqualTo ""
        profile.header shouldBeEqualTo null
        profile.headerStatic shouldBeEqualTo null
        profile.headerDescription shouldBeEqualTo ""
        profile.locked shouldBeEqualTo false
        profile.bot shouldBeEqualTo false
        profile.hideCollections shouldBeEqualTo null
        profile.discoverable shouldBeEqualTo true
        profile.indexable shouldBeEqualTo true
        profile.showMedia shouldBeEqualTo true
        profile.showMediaReplies shouldBeEqualTo true
        profile.showFeatured shouldBeEqualTo true
        profile.attributionDomains.size shouldBeEqualTo 1
        profile.attributionDomains[0] shouldBeEqualTo "articles.example.com"
    }
}
