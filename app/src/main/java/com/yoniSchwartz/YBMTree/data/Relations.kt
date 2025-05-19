package com.yoniSchwartz.YBMTree.data

import com.yoniSchwartz.YBMTree.ui.HebrewText

/**
 * Enum class representing different types of family relationships.
 *
 * Each relationship has a corresponding Hebrew translation, which is stored in [HebrewText].
 */
enum class Relations() {
    MARRIAGE,
//    SECOND_MARRIAGE,
//    THIRD_MARRIAGE,
//    FORTH_MARRIAGE,
    FATHER,
    MOTHER,
    SON,
    DAUGHTER,
    GRANDMOTHER,
    GRANDFATHER,
    GRANDSON,
    GRANDDAUGHTER,
    COUSINS,
    SIBLINGS,
    HALF_SIBLINGS,
    GREAT_GRANDMOTHER,
    GREAT_GRANDFATHER,
    GREAT_GRANDSON,
    GREAT_GRANDDAUGHTER,
    UNCLE,
    AUNT,
    NEPHEW,
    NIECE;

    /**
     * Companion object providing utility functions for converting Hebrew relation strings,
     * filtering valid relationships, and managing relationship-specific logic.
     *
     * The companion object allows these methods to be accessed directly via the [Relations] enum
     * without needing an instance, facilitating cleaner and more efficient code.
     */
    companion object {

        /**
         * Priority order used to sort relationships in a logical display sequence.
         * Lower numbers are shown first.
         */
        val relationPriority = mapOf(
            MARRIAGE to 1,

            SON to 2,
            DAUGHTER to 2,

            FATHER to 3,
            MOTHER to 3,

            SIBLINGS to 4,
            HALF_SIBLINGS to 4,

            GRANDSON to 5,
            GRANDDAUGHTER to 5,

            GRANDFATHER to 6,
            GRANDMOTHER to 6,

            GREAT_GRANDSON to 7,
            GREAT_GRANDDAUGHTER to 7,

            GREAT_GRANDFATHER to 8,
            GREAT_GRANDMOTHER to 8,

            COUSINS to 9,

            UNCLE to 10,
            AUNT to 10,

            NEPHEW to 11,
            NIECE to 11
        )

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
                HebrewText.WIFE -> Pair(MARRIAGE, false)
                HebrewText.HUSBAND -> Pair(MARRIAGE, true)
                HebrewText.FATHER -> Pair(FATHER, true)
                HebrewText.MOTHER -> Pair(MOTHER, false)
                HebrewText.SON -> Pair(SON, true)
                HebrewText.DAUGHTER -> Pair(DAUGHTER, false)
                HebrewText.GRANDMOTHER -> Pair(GRANDMOTHER, false)
                HebrewText.GRANDFATHER -> Pair(GRANDFATHER, true)
                HebrewText.GRANDSON -> Pair(GRANDSON, true)
                HebrewText.GRANDDAUGHTER -> Pair(GRANDDAUGHTER, false)
                HebrewText.MALE_COUSIN -> Pair(COUSINS, true)
                HebrewText.FEMALE_COUSIN -> Pair(COUSINS, false)
                HebrewText.BROTHER -> Pair(SIBLINGS, true)
                HebrewText.SISTER -> Pair(SIBLINGS, false)
                HebrewText.HALF_BROTHER -> Pair(HALF_SIBLINGS, true)
                HebrewText.HALF_SISTER -> Pair(HALF_SIBLINGS, false)
                else -> Pair(SIBLINGS, true) // Default case
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

            val spouse: String
//            val secondSpouse: String
//            val thirdSpouse: String
//            val forthSpouse: String

            if (member.getGender()) {
                spouse = HebrewText.WIFE
//                secondSpouse = HebrewText.SECOND_WIFE
//                thirdSpouse = HebrewText.THIRD_WIFE
//                forthSpouse = HebrewText.FORTH_WIFE
            } else {
                spouse = HebrewText.HUSBAND
//                secondSpouse = HebrewText.SECOND_HUSBAND
//                thirdSpouse = HebrewText.THIRD_HUSBAND
//                forthSpouse = HebrewText.FORTH_HUSBAND
            }

            return listOf(
                spouse,
//                secondSpouse,
//                thirdSpouse,
//                forthSpouse,
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
                HebrewText.SISTER,
                HebrewText.HALF_BROTHER,
                HebrewText.HALF_SISTER,
            )
        }

        /**
         * Determines which relationship types are not valid for a given family member.
         *
         * **Validation Rules:**
         * - **MARRIAGE** → Invalid **only if** no `MARRIAGE` connection exists.
         * - **SECOND_MARRIAGE** → Invalid in either of these cases:
         *   1. No `MARRIAGE` connection exists.
         *   2. A `SECOND_MARRIAGE` connection already exists.
         * - **THIRD_MARRIAGE** → Invalid in either of these cases:
         *   1. No `SECOND_MARRIAGE` connection exists.
         *   2. A `THIRD_MARRIAGE` connection already exists.
         * - **FORTH_MARRIAGE** → Invalid **only if**:
         *   1. A `THIRD_MARRIAGE` connection exists.
         *   2. A `FORTH_MARRIAGE` connection already exists.
         * - **FATHER & MOTHER** → Only one of each is allowed.
         *
         * @param member The [FamilyMember] whose connections are being validated.
         * @return A set of invalid [Relations] types for the given member.
         */
        private fun getInvalidRelationsOfMember(member: FamilyMember): Set<Relations> {
            val setOfInvalidRelations = mutableSetOf<Relations>()

            // Track which marriage relations exist
            var hasMarriage = false
            var hasSecondMarriage = false
            var hasThirdMarriage = false
            var hasForthMarriage = false

            for (connection in member.getConnections()) {
                when (connection.relationship) {
                    MARRIAGE -> hasMarriage = true
//                    SECOND_MARRIAGE -> hasSecondMarriage = true
//                    THIRD_MARRIAGE -> hasThirdMarriage = true
//                    FORTH_MARRIAGE -> hasForthMarriage = true
                    FATHER, MOTHER -> setOfInvalidRelations.add(connection.relationship) // Only one allowed
                    else -> {} // Other relationships are allowed
                }
            }

            // Apply validation rules
            if (hasMarriage) setOfInvalidRelations.add(MARRIAGE)
//            if (hasSecondMarriage) setOfInvalidRelations.add(SECOND_MARRIAGE)
//            if (hasThirdMarriage) setOfInvalidRelations.add(THIRD_MARRIAGE)
//            if (hasForthMarriage) setOfInvalidRelations.add(FORTH_MARRIAGE)

            return setOfInvalidRelations
        }
    }

    /**
     * Returns the Hebrew text representing how this relationship should be displayed when
     * describing a connection between family members.
     *
     * @return A formatted Hebrew string for displaying family connections.
     */
    fun displayAsRelation(gender: Boolean = true): String {
        return when (this) {
            MARRIAGE,
//            SECOND_MARRIAGE, THIRD_MARRIAGE, FORTH_MARRIAGE
                 -> if (gender) HebrewText.HUSBAND_OF else HebrewText.WIFE_OF
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
            HALF_SIBLINGS -> if (gender) HebrewText.HALF_BROTHER_OF else HebrewText.HALF_SISTER_OF
            GREAT_GRANDMOTHER -> HebrewText.GREAT_GRANDMOTHER_OF
            GREAT_GRANDFATHER -> HebrewText.GREAT_GRANDFATHER_OF
            GREAT_GRANDSON -> HebrewText.GREAT_GRANDSON_OF
            GREAT_GRANDDAUGHTER -> HebrewText.GREAT_GRANDDAUGHTER_OF
            UNCLE -> HebrewText.UNCLE_OF
            AUNT -> HebrewText.AUNT_OF
            NEPHEW -> HebrewText.NEPHEW_OF
            NIECE -> HebrewText.NIECE_OF
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
            MARRIAGE,
//            SECOND_MARRIAGE, THIRD_MARRIAGE, FORTH_MARRIAGE
                ->
                if (gender != null) !gender else null
            else -> null
        }
    }

    /**
     * Returns the Hebrew term for this relationship, adjusted for gender when relevant.
     *
     * @param gender true = male, false = female
     * @return The Hebrew string representing this relationship.
     */
    fun toHebrew(gender: Boolean): String {
        return when (this) {
            MARRIAGE -> if (gender) HebrewText.A_HUSBAND else HebrewText.A_WIFE
            FATHER -> HebrewText.FATHER
            MOTHER -> HebrewText.MOTHER
            SON -> HebrewText.SON
            DAUGHTER -> HebrewText.DAUGHTER
            GRANDMOTHER -> HebrewText.GRANDMOTHER
            GRANDFATHER -> HebrewText.GRANDFATHER
            GRANDSON -> HebrewText.GRANDSON
            GRANDDAUGHTER -> HebrewText.GRANDDAUGHTER
            COUSINS -> if (gender) HebrewText.MALE_COUSIN else HebrewText.FEMALE_COUSIN
            SIBLINGS -> if (gender) HebrewText.BROTHER else HebrewText.SISTER
            HALF_SIBLINGS -> if (gender) HebrewText.HALF_BROTHER else HebrewText.HALF_SISTER
            GREAT_GRANDMOTHER -> HebrewText.GREAT_GRANDMOTHER
            GREAT_GRANDFATHER -> HebrewText.GREAT_GRANDFATHER
            GREAT_GRANDSON -> HebrewText.GREAT_GRANDSON
            GREAT_GRANDDAUGHTER -> HebrewText.GREAT_GRANDDAUGHTER
            UNCLE -> HebrewText.UNCLE
            AUNT -> HebrewText.AUNT
            NEPHEW -> HebrewText.NEPHEW
            NIECE -> HebrewText.NIECE
        }
    }


}
