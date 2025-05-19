package com.yoniSchwartz.YBMTree.data

import com.yoniSchwartz.YBMTree.ui.HebrewText
import java.util.UUID

/**
 * Represents a family member in the Yeshiva family tree.
 *
 * @property memberType The type of member (e.g., student, rabbi, non-yeshiva member).
 * @property firstName The first name of the family member.
 * @property lastName The last name of the family member.
 * @property gender The gender of the family member (true for male, false for female).
 * @property machzor The machzor (graduation cycle) the member belongs to. Null for non yeshiva members.
 * @property isRabbi Indicates whether the member is a rabbi.
 * @property isYeshivaRabbi Indicates whether the rabbi is specifically affiliated with the Yeshiva.
 * @property id A unique identifier for the member, auto-generated using UUID.
 * @property connections A list of relationships the member has with other family members.
 */
class FamilyMember(
    private var memberType: MemberType = MemberType.NonYeshiva,
    private var firstName: String = "",
    private var lastName: String = "",
    private val gender: Boolean = true,
    private var machzor: Int? = null,  // machzor is 0 for non-student yeshiva members
    private var isRabbi: Boolean = false,
    private var isYeshivaRabbi: Boolean = false,
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
     * Returns a full name formatted to better fit limited-width UI elements (like cubes).
     *
     * If the full name (including any title like "Rabbi") contains more than four words,
     * and the `firstName` field includes at least two words, the second word in the first name
     * will be abbreviated to its initial followed by a dot.
     *
     * If the full name is short enough (3 words or fewer), it returns the regular full name.
     *
     * @return A possibly shortened full name suitable for compact display.
     */
    fun getFullNameThatFitsTheCube(): String {
        val prefix = when {
            isRabbi && !gender -> HebrewText.RABBI_WIFE
            isRabbi -> HebrewText.RABBI
            else -> ""
        }

        val fullName = "$prefix$firstName $lastName".trim()
        val fullNameWords = fullName.split(" ")

        // Only shorten if there are more than 4 words in the full name
        if (fullNameWords.size >= 4) {
            val firstNames = firstName.trim().split(" ")
            if (firstNames.size >= 2) {
                val shortenedFirstName = "${firstNames[0]} ${firstNames[1].first()}."
                return "$prefix$shortenedFirstName $lastName".trim()
            }
        }

        // Otherwise return the normal full name
        return fullName
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

    /**
     * Creates a duplicate instance of this `FamilyMember` with the `machzor` set to 0.
     * This is specifically used for displaying Yeshiva rabbis in both their original
     * `machzor` group and in the "machzor 0" group in the UI.
     *
     * The duplicate retains all other properties, including the same unique ID and connections.
     *
     * @return A new `FamilyMember` instance with `machzor` set to 0.
     */
    fun getDuplicateRabbiWithNoMachzor(): FamilyMember {

        return FamilyMember(
            memberType = this.memberType,
            firstName = this.firstName,
            lastName = this.lastName,
            gender = this.gender,
            machzor = 0,
            isRabbi = this.isRabbi,
            isYeshivaRabbi = this.isYeshivaRabbi,
            id = this.id, // Keeping the same ID, but if a new ID is needed, use UUID.randomUUID().toString()
            connections = this.connections.toMutableList()
        )
    }

    /**
     * Updates the editable properties of this FamilyMember based on another instance,
     * excluding immutable fields and connection data.
     *
     * This function copies over the following fields:
     * - firstName
     * - lastName
     * - machzor
     * - isRabbi
     * - isYeshivaRabbi
     * - memberType
     *
     * @param updated A FamilyMember instance whose editable values will overwrite this member’s corresponding fields.
     */
    fun updateMember(updated: FamilyMember) {
        firstName = updated.firstName
        lastName = updated.lastName
        machzor = updated.machzor
        isRabbi = updated.isRabbi
        isYeshivaRabbi = updated.isYeshivaRabbi
        memberType = updated.memberType
    }
}