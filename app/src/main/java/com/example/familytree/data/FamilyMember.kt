package com.example.familytree.data

import com.example.familytree.ui.theme.HebrewText
import java.util.UUID

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
    private val memberType: MemberType = MemberType.NonYeshiva,
    private val firstName: String = "",
    private val lastName: String = "",
    private val gender: Boolean = true,
    private val machzor: Int? = null,  // machzor is 0 for non-student yeshiva members
    private var isRabbi: Boolean = false,
    private val isYeshivaRabbi: Boolean = false,
    private val id: String = UUID.randomUUID().toString(), // Auto-generate unique ID
    private val connections: MutableList<Connection> = mutableListOf()
) {

    /**
     * Converts the FamilyMember object to a map for Firebase storage.
     *
     * @return A map representing the FamilyMember object with its fields.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "documentId" to id,
            "memberType" to memberType,
            "firstName" to firstName,
            "lastName" to lastName,
            "gender" to gender,
            "machzor" to machzor,
            "rabbi" to isRabbi,
            "yeshivaRabbi" to isYeshivaRabbi,
            "connections" to connections.map { it.toMap() }
        )
    }

    /**
     * Retrieves a list of connections for a given family member.
     */
    fun getConnections(): MutableList<Connection> {
        return connections
    }

    /**
     * Adds a connection to the member's connections.
     *
     * @param connection The connection to be added.
     */
    fun addConnection(connection: Connection) {
        connections.add(connection)
    }

    /**
     * Returns the full name of the family member with an appropriate title if applicable.
     *
     * @return The full name, including a title if applicable, formatted as "Title FirstName LastName".
     */
    fun getFullName(): String {
        val prefix = when {
            isRabbi && !gender -> HebrewText.RABBI_WIFE
            isRabbi -> HebrewText.RABBI
            else -> ""
        }
        return "$prefix$firstName $lastName".trim()
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
    fun getIsRabbi(): Boolean {
        return isRabbi
    }

    /**
     * Sets the value of the `isRabbi` property for the member.
     *
     * @param rabbi A Boolean value that sets the `isRabbi` property.
     *              `true` if the member is a rabbi, `false` otherwise.
     */
    fun setIsRabbi(rabbi: Boolean) {
        isRabbi = rabbi
    }

    /**
     * Checks if the family member is a rabbi in the yeshiva.
     *
     */
    fun getIsYeshivaRabbi(): Boolean {
        return isYeshivaRabbi
    }

    /**
     * Retrieves the member type of the family member.
     *
     * @return The Family member type
     */
    fun getMemberType(): MemberType {
        return memberType
    }

    /**
     * Retrieves the id of the family member.
     *
     * @return The Family member id
     */
    fun getId(): String {
        return id
    }
}