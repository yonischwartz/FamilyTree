package com.example.familytree.data

import com.example.familytree.ui.theme.HebrewText

/**
 * Enum class representing different types of family relationships.
 *
 * Each relationship has a corresponding Hebrew translation, which is stored in [HebrewText].
 */
enum class Relations(val hebrew: String) {
    MARRIAGE(HebrewText.MARRIAGE),
    FATHER(HebrewText.FATHER),
    MOTHER(HebrewText.MOTHER),
    SON(HebrewText.SON),
    DAUGHTER(HebrewText.DAUGHTER),
    GRANDMOTHER(HebrewText.GRANDMOTHER),
    GRANDFATHER(HebrewText.GRANDFATHER),
    GRANDSON(HebrewText.GRANDSON),
    GRANDDAUGHTER(HebrewText.GRANDDAUGHTER),
    COUSINS(HebrewText.COUSIN),
    SIBLINGS(HebrewText.SIBLING_OF);

    /**
     * Returns the Hebrew text representing how this relationship should be displayed when
     * describing a connection between family members.
     *
     * @return A formatted Hebrew string for displaying family connections.
     */
    fun displayAsConnections(gender: Boolean = true): String {
        return if (this == MARRIAGE) {
            displayMarriageAsConnection(gender)
        } else {
            "$hebrew ${HebrewText.OF} "
        }
    }

    /**
     * Returns the Hebrew text representing how the MARRIAGE relationship should be displayed
     * when describing a connection between family members, based on gender.
     *
     * @param gender A Boolean indicating the gender of the person being described.
     *               `true` for male, `false` for female.
     * @return A formatted Hebrew string for displaying the marriage connection.
     */
    private fun displayMarriageAsConnection(gender: Boolean): String {
        return if (gender) HebrewText.HUSBAND_OF else HebrewText.WIFE_OF
    }
}
