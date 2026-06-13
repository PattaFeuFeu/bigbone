package social.bigbone.api.entity

import social.bigbone.Parameters

/**
 * Profile fields that can be set in [social.bigbone.api.method.AccountMethods.updateCredentials].
 *
 * At most four fields are allowed. Each of them has a max key and value length of 255 characters.
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
 * Must not be longer than 255 characters.
 */
@JvmInline
value class ProfileFieldValue(val value: String) {
    init {
        require(value.length <= 255) {
            "Value of profile field must not be longer than 255 characters but was: $value (${value.length} characters)."
        }
    }
}

/**
 * Name of a profile field used in [ProfileFields].
 * Must not be longer than 255 characters.
 */
@JvmInline
value class ProfileFieldName(val name: String) {
    init {
        require(name.length <= 255) {
            "Name of profile field must not be longer than 255 characters but was: $name (${name.length} characters)."
        }
    }
}
