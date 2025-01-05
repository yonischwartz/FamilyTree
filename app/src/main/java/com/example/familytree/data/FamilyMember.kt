package com.example.familytree.data

/**
 * Represents a family member with their personal details and a unique ID.
 *
 * @property firstName The first name of the family member.
 * @property lastName The last name of the family member.
 * @property gender The gender of the family member, represented as a Boolean.
 *                  `true` for male, `false` for female.
 * @property documentId
 */
open class FamilyMember(
    private val firstName: String = "",
    private val lastName: String = "",
    private val gender: Boolean = true
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
     * @return The gender.
     */
    fun getGender(): Boolean {
        return gender;
    }

    /**
     * Retrieves the last name of the family member.
     *
     * @return The last name as a string.
     */
    fun getLastName(): String {
        return lastName
    }

}
