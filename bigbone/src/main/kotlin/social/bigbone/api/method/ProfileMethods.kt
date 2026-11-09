package social.bigbone.api.method

import okhttp3.MultipartBody
import social.bigbone.JSON_SERIALIZER
import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameters
import social.bigbone.addFileToFormBody
import social.bigbone.api.entity.CredentialAccount
import social.bigbone.api.entity.FileAsMediaAttachment
import social.bigbone.api.entity.Profile
import social.bigbone.api.entity.ProfileFields

/**
 * Allows access to API methods with endpoints having an "api/vX/profile" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/profile/">Mastodon profile API methods</a>
 */
class ProfileMethods(private val client: MastodonClient) {

    private val endpoint = "api/v1/profile"

    /**
     * Returns the current user’s profile.
     */
    fun getUserProfile(): MastodonRequest<Profile> {
        return client.getMastodonRequest(
            endpoint = endpoint,
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Deletes the avatar associated with the user’s profile.
     */
    fun deleteProfileAvatar(): MastodonRequest<CredentialAccount> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/avatar",
            method = MastodonClient.Method.DELETE
        )
    }

    /**
     * Deletes the header image associated with the user’s profile.
     */
    fun deleteProfileHeader(): MastodonRequest<CredentialAccount> {
        return client.getMastodonRequest(
            endpoint = "$endpoint/header",
            method = MastodonClient.Method.DELETE
        )
    }

    /**
     * Updates the current user’s profile.
     * To keep the method simpler, this method doesn’t allow you to change media (header, avatar) of a profile.
     * Use [updateProfileMedia] for that instead.
     *
     * @param displayName The display name to use for the profile.
     * @param note The account bio.
     * @param locked Whether manual approval of follow requests is required.
     * @param bot Whether the account has a bot flag.
     * @param discoverable Whether the account should be shown in the profile directory and be available for other discovery features.
     * @param hideCollections Whether to hide followers and followed accounts.
     * @param indexable Whether public posts should be searchable to anyone.
     * @param showMedia Whether a “Media” tab with media attachments should be shown on this profile.
     * @param showMediaReplies Whether media attachments in replies should be shown in the “Media” tab of this profile.
     * @param showFeatured Whether a “Featured” tab should be shown on this profile.
     * @param attributionDomains Domains of websites allowed to credit the account. Maximum of 10 domains.
     * @param fieldsAttributes The profile fields to be set. Each hash includes name and value.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    fun updateProfile(
        displayName: String? = null,
        note: String? = null,
        locked: Boolean? = null,
        bot: Boolean? = null,
        discoverable: Boolean? = null,
        hideCollections: Boolean? = null,
        indexable: Boolean? = null,
        showMedia: Boolean? = null,
        showMediaReplies: Boolean? = null,
        showFeatured: Boolean? = null,
        attributionDomains: List<String>? = null,
        fieldsAttributes: ProfileFields? = null
    ): MastodonRequest<Profile> {
        return client.getMastodonRequest(
            endpoint = endpoint,
            method = MastodonClient.Method.PATCH,
            parameters = Parameters().apply {
                displayName?.let { append("display_name", it) }
                note?.let { append("note", it) }

                locked?.let { append("locked", it) }
                bot?.let { append("bot", it) }
                discoverable?.let { append("discoverable", it) }
                hideCollections?.let { append("hide_collections", it) }
                indexable?.let { append("indexable", it) }

                showMedia?.let { append("show_media", it) }
                showMediaReplies?.let { append("show_media_replies", it) }
                showFeatured?.let { append("show_featured", it) }

                attributionDomains?.let { append("attribution_domains", it) }

                fieldsAttributes?.toParameters(this)
            }
        )
    }

    /**
     * Updates the current user’s profile media (avatar and header).
     * If you want to update other properties, use [updateProfile].
     *
     * @param avatar Avatar image encoded using multipart/form-data.
     * @param avatarDescription A plain-text description of the avatar, for accessibility purposes.
     * @param header Header image encoded using multipart/form-data.
     * @param headerDescription A plain-text description of the header, for accessibility purposes.
     *
     * @since Mastodon 4.6.0, API version 8
     */
    fun updateProfileMedia(
        avatar: FileAsMediaAttachment? = null,
        avatarDescription: String? = null,
        header: FileAsMediaAttachment? = null,
        headerDescription: String? = null
    ): MastodonRequest<Profile> {
        val multipartBodyBuilder: MultipartBody.Builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .apply {
                addFileToFormBody(file = avatar, formDataName = "avatar", builder = this)
                avatarDescription?.let { addFormDataPart("avatar_description", avatarDescription) }

                addFileToFormBody(file = header, formDataName = "header", builder = this)
                headerDescription?.let { addFormDataPart("header_description", headerDescription) }
            }

        return MastodonRequest(
            executor = { client.patchMultipart(path = endpoint, body = multipartBodyBuilder.build()) },
            mapper = { JSON_SERIALIZER.decodeFromString<Profile>(it) }
        )
    }
}
