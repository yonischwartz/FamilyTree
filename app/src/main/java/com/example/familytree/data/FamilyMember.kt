package com.example.familytree.data

/**
 * Represents a family member with their personal details and a unique ID.
 *
 * @property firstName The first name of the family member.
 * @property lastName The last name of the family member.
 * @property gender The gender of the family member, represented as a Boolean.
 *                  `true` for male, `false` for female.
 * @property ID The unique identifier for the family member, automatically assigned
 *               using the ID generator from the `MemberDataBase` singleton.
 */
open class FamilyMember(
    private val firstName: String, // Family member's first name
    private val lastName: String,  // Family member's last name
    val gender: Boolean    // Family member's gender (true = male, false = female)
) {
    // Unique identifier for the family member
    val ID: Int = MemberMapByID.getInstance().getNewID()

    /**
     * Returns the full name of the family member, combining the first and last name.
     *
     * @return The full name as a string, formatted as "firstName lastName".
     */
    fun getFullName(): String {
        return "$firstName $lastName"
    }

    /**
     * Converts the FamilyMember object to a Map<String, Any> for storage in Firestore.
     *
     * @return A Map representation of the FamilyMember object.
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "gender" to gender,
            "ID" to ID
        )
    }

    /**
     * Creates a FamilyMember object from a Map<String, Any> retrieved from Firestore.
     *
     * @param map The Map representation of the FamilyMember object.
     * @return A new FamilyMember object created from the provided Map.
     */
    companion object {
        fun fromMap(map: Map<String, Any>): FamilyMember {
            return FamilyMember(
                map["firstName"] as String,
                map["lastName"] as String,
                map["gender"] as Boolean
            )
        }
    }
}
