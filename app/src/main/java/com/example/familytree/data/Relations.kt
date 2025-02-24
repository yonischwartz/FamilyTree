package com.example.familytree.data

import com.example.familytree.ui.theme.HebrewText

/**
 * Enum class representing different types of family relationships.
 *
 * Each relationship has a corresponding Hebrew translation, which is stored in [HebrewText].
 */
enum class Relations() {
    MARRIAGE,
    FATHER,
    MOTHER,
    SON,
    DAUGHTER,
    GRANDMOTHER,
    GRANDFATHER,
    GRANDSON,
    GRANDDAUGHTER,
    COUSINS,
    SIBLINGS;

    /**
     * Companion object providing utility functions for converting Hebrew relation strings,
     * filtering valid relationships, and managing relationship-specific logic.
     *
     * The companion object allows these methods to be accessed directly via the [Relations] enum
     * without needing an instance, facilitating cleaner and more efficient code.
     */
    companion object {

        /**
         * Converts a Hebrew relation string to a pair of [Relations] enum and a boolean indicating gender.
         *
         * @param relationString The Hebrew relation string from the [HebrewText] object.
         * @return A [Pair] where the first element is a [Relations] enum and the second element is a boolean.
         *         The boolean is true for male relations and false for female relations.
         *         The default return value is (Relations.SIBLINGS, true) if the input is not recognized.
         */
        fun relationStringToRelation(relationString: String): Pair<Relations, Boolean> {
            return when (relationString) {
                HebrewText.WIFE -> Pair(Relations.MARRIAGE, false)
                HebrewText.HUSBAND -> Pair(Relations.MARRIAGE, true)
                HebrewText.FATHER -> Pair(Relations.FATHER, true)
                HebrewText.MOTHER -> Pair(Relations.MOTHER, false)
                HebrewText.SON -> Pair(Relations.SON, true)
                HebrewText.DAUGHTER -> Pair(Relations.DAUGHTER, false)
                HebrewText.GRANDMOTHER -> Pair(Relations.GRANDMOTHER, false)
                HebrewText.GRANDFATHER -> Pair(Relations.GRANDFATHER, true)
                HebrewText.GRANDSON -> Pair(Relations.GRANDSON, true)
                HebrewText.GRANDDAUGHTER -> Pair(Relations.GRANDDAUGHTER, false)
                HebrewText.MALE_COUSIN -> Pair(Relations.COUSINS, true)
                HebrewText.FEMALE_COUSIN -> Pair(Relations.COUSINS, false)
                HebrewText.BROTHER -> Pair(Relations.SIBLINGS, true)
                HebrewText.SISTER -> Pair(Relations.SIBLINGS, false)
                else -> Pair(Relations.SIBLINGS, true) // Default case
            }
        }

        /**
         * Filters out invalid relationship options for a given family member.
         *
         * @param member The [FamilyMember] whose valid relationship options are being determined.
         * @return A list of valid relationship options in Hebrew.
         */
        fun getValidRelationOptions(member: FamilyMember): List<String> {
            val relationOptions = relationOptions(member)
            val invalidRelations = getInvalidRelationsOfMember(member)

            return relationOptions.filter { relation ->
                val (relationEnum, _) = relationStringToRelation(relation)
                !invalidRelations.contains(relationEnum)
            }
        }

        /**
         * Generates a list of relationship options in Hebrew based on the gender of the provided family member.
         *
         * The function evaluates the gender of the [FamilyMember] to determine the appropriate term
         * for "spouse" (either "wife" or "husband" in Hebrew). It then returns a list of all possible
         * family relationships, including both male and female-specific terms where applicable.
         *
         * @param member The [FamilyMember] whose gender determines specific relationship options.
         * @return A list of relationship options in Hebrew as [String] values.
         */
        private fun relationOptions(member: FamilyMember): List<String> {
            val spouse = if (member.getGender()) {
                HebrewText.WIFE
            } else {
                HebrewText.HUSBAND
            }

            return listOf(
                spouse,
                HebrewText.FATHER,
                HebrewText.MOTHER,
                HebrewText.SON,
                HebrewText.DAUGHTER,
                HebrewText.GRANDMOTHER,
                HebrewText.GRANDFATHER,
                HebrewText.GRANDSON,
                HebrewText.GRANDDAUGHTER,
                HebrewText.MALE_COUSIN,
                HebrewText.FEMALE_COUSIN,
                HebrewText.BROTHER,
                HebrewText.SISTER
            )
        }

        /**
         * Checks which relationship types are not valid for a given family member.
         *
         * A family member can have only one connection of the following types:
         * - MARRIAGE: A member cannot be married to more than one person.
         * - FATHER: A member cannot have more than one father.
         * - MOTHER: A member cannot have more than one mother.
         *
         * @param member The [FamilyMember] whose connections are being validated.
         * @return A set of invalid [Relations] types for the given member.
         *
         */
        private fun getInvalidRelationsOfMember(member: FamilyMember): Set<Relations> {
            val setOfInvalidRelations = mutableSetOf<Relations>()

            for (connection in member.getConnections()) {
                if (connection.relationship in setOf(Relations.MARRIAGE, Relations.FATHER, Relations.MOTHER)) {
                    setOfInvalidRelations.add(connection.relationship)
                }
            }

            return setOfInvalidRelations
        }
    }

    /**
     * Returns the Hebrew text representing how this relationship should be displayed when
     * describing a connection between family members.
     *
     * @return A formatted Hebrew string for displaying family connections.
     */
    fun displayAsConnections(gender: Boolean = true): String {
        return when (this) {
            MARRIAGE -> if (gender) HebrewText.HUSBAND_OF else HebrewText.WIFE_OF
            FATHER -> HebrewText.FATHER_OF
            MOTHER -> HebrewText.MOTHER_OF
            SON -> HebrewText.SON_OF
            DAUGHTER -> HebrewText.DAUGHTER_OF
            GRANDMOTHER -> HebrewText.GRANDMOTHER_OF
            GRANDFATHER -> HebrewText.GRANDFATHER_OF
            GRANDSON -> HebrewText.GRANDSON_OF
            GRANDDAUGHTER -> HebrewText.GRANDDAUGHTER_OF
            COUSINS -> if (gender) HebrewText.MALE_COUSIN_OF else HebrewText.FEMALE_COUSIN_OF
            SIBLINGS -> if (gender) HebrewText.BROTHER_OF else HebrewText.SISTER_OF
        }
    }

    /**
     * Returns the expected gender for the relationship.
     * - true for male
     * - false for female
     * - null for relationships that do not have a specific gender expectation
     */
    fun expectedGender(gender: Boolean? = null): Boolean? {
        return when (this) {
            FATHER, SON, GRANDFATHER, GRANDSON -> true
            MOTHER, DAUGHTER, GRANDMOTHER, GRANDDAUGHTER -> false
            COUSINS, SIBLINGS -> null
            MARRIAGE -> if (gender != null) !gender else null
        }
    }
}
