package com.example.familytree.data

/**
 * Represents a family member with personal details and identifiers.
 * This class is designed to handle both yeshiva family members and non-yeshiva family members.
 *
 * @property firstName The first name of the family member (default is an empty string).
 * @property lastName The last name of the family member (default is an empty string).
 * @property gender The gender of the family member, where true typically represents male and false female (default is true).
 * @property machzor The machzor of the family member
 *                   For yeshiva members who did not study in the yeshiva (e.g., staff), machzor is set to 0.
 *                   For non-yeshiva members, machzor is null.
 * @property isRabbi Indicates if the family member is a rabbi (nullable, default is null).
 *                 For yeshiva members who are rabbis, this is set to true, while for non-yeshiva members, rabbi is null.
 */

class FamilyMember(
    private val firstName: String = "",
    private val lastName: String = "",
    private val gender: Boolean = true,
    private val machzor: Int? = null,  // machzor is 0 for non-student yeshiva members
    private val isRabbi: Boolean? = null
) {
    // A unique identifier for the family member. It is initialized as an empty string by default.
    // When the object is added to Firebase, Firestore automatically assigns it a unique ID.
    var documentId: String = ""

    /**
     * Returns the full name of the family member, combining the first and last name.
     *
     * @return The full name as a string, formatted as "firstName lastName".
     */
    fun getFullName(): String {
        return "$firstName $lastName"
    }

    /**
     * Retrieves the first name of the family member.
     *
     * @return The first name as a string.
     */
    fun getFirstName(): String {
        return firstName
    }

    /**
     * Retrieves the gender of the family member.
     *
     * @return The gender as a boolean.
     */
    fun getGender(): Boolean {
        return gender
    }

    /**
     * Retrieves the last name of the family member.
     *
     * @return The last name as a string.
     */
    fun getLastName(): String {
        return lastName
    }

    /**
     * Retrieves the machzor (class year) of the family member.
     * Note: machzor is 0 for non-student yeshiva members (e.g., staff or rabbis).
     *
     * @return The machzor as an Int, or null if not set.
     */
    fun getMachzor(): Int? {
        return machzor
    }

    /**
     * Checks if the family member is a rabbi.
     *
     * @return True if the rabbi field is true, false if it is false, or null if not set.
     */
    fun getIsRabbi(): Boolean? {
        return isRabbi
    }
}