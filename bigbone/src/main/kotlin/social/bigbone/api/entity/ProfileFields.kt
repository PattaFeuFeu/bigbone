package social.bigbone.api.entity

import social.bigbone.Parameters

/**
 * Profile fields that can be set in [social.bigbone.api.method.AccountMethods.updateCredentials].
 *
 * At most four fields are allowed.
 *
 * By default, each of them has a max key and value length of 255 characters.
 * See [ProfileFieldName] and [ProfileFieldValue] for further information.
 */
data class ProfileFields(
    val first: Pair<ProfileFieldName, ProfileFieldValue>? = null,
    val second: Pair<ProfileFieldName, ProfileFieldValue>? = null,
    val third: Pair<ProfileFieldName, ProfileFieldValue>? = null,
    val fourth: Pair<ProfileFieldName, ProfileFieldValue>? = null
) {
    fun toParameters(parameters: Parameters = Parameters()): Parameters {
        fun appendField(index: Int, name: ProfileFieldName, value: ProfileFieldValue) {
            parameters.append("fields_attributes[$index][name]", name.name)
            parameters.append("fields_attributes[$index][value]", value.value)
        }

        return parameters.apply {
            first?.let { (name, value) -> appendField(0, name, value) }
            second?.let { (name, value) -> appendField(1, name, value) }
            third?.let { (name, value) -> appendField(2, name, value) }
            fourth?.let { (name, value) -> appendField(3, name, value) }
        }
    }
}

/**
 * Value of a profile field used in [ProfileFields].
 *
 * By default, max length of 255 characters, but may be overridden by the instance.
 * todo: Link to profile_field_name_limit once available
 * Check [Instance.Configuration.accounts] for limits.
 */
@JvmInline
value class ProfileFieldValue(val value: String)

/**
 * Name of a profile field used in [ProfileFields].
 * todo: Link to profile_field_value_limit once available
 * Check [Instance.Configuration.accounts] for limits.
 */
@JvmInline
value class ProfileFieldName(val name: String)
