package social.bigbone.api.entity

import social.bigbone.Parameters

/**
 * Profile fields that can be set in [social.bigbone.api.method.AccountMethods.updateCredentials].
 *
 * By default, at most four fields are allowed. Limit may be overridden by the instance.
 * Check [Instance.Configuration.Accounts.maxProfileFields] for limits.
 *
 * By default, each of them has a max key and value length of 255 characters.
 * See [ProfileFieldName] and [ProfileFieldValue] for further information.
 */
@JvmInline
value class ProfileFields(
    val fields: Map<ProfileFieldName, ProfileFieldValue>? = null
) {
    fun toParameters(parameters: Parameters = Parameters()): Parameters {
        if (fields.isNullOrEmpty()) return parameters

        fun appendField(index: Int, name: ProfileFieldName, value: ProfileFieldValue) {
            parameters.append("fields_attributes[$index][name]", name.name)
            parameters.append("fields_attributes[$index][value]", value.value)
        }

        return parameters.apply {
            fields.onEachIndexed { index, (name, value) ->
                appendField(index, name, value)
            }
        }
    }
}

/**
 * Value of a profile field used in [ProfileFields].
 *
 * By default, max length of 255 characters, but may be overridden by the instance.
 * Check [Instance.Configuration.Accounts.profileFieldValueLimit] for limits.
 */
@JvmInline
value class ProfileFieldValue(val value: String)

/**
 * Name of a profile field used in [ProfileFields].
 * Check [Instance.Configuration.Accounts.profileFieldNameLimit] for limits.
 */
@JvmInline
value class ProfileFieldName(val name: String)
