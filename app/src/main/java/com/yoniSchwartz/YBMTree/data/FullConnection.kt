package com.yoniSchwartz.YBMTree.data

/**
 * Represents a possible relationship between two family members.
 *
 * This class is used to suggest new connections that might be valid after adding a family member.
 * Each instance of `FullConnection` stores a pair of `FamilyMember` objects along with their
 * corresponding relationship type. It may also store an alternative connection in case the
 * primary connection is found to be incorrect.
 *
 * If the primary connection is determined to be incorrect, the alternative connection is guaranteed to be correct
 * and should be automatically applied without requiring further user input.
 *
 * @property memberOne The first member in the connection.
 * @property memberTwo The second member in the connection.
 * @property relationship The type of relationship between the two members.
 * @property alternativeConnection An optional alternative connection that is guaranteed to be correct if the primary connection is rejected.
 */
class FullConnection(
    val memberOne: FamilyMember,
    val memberTwo: FamilyMember,
    val relationship: Relations,
    val alternativeConnection: FullConnection? = null
) {
    /**
     * Creates a copy of this FullConnection with the ability to change specific properties.
     *
     * @param alternativeConnection The new alternative connection (optional).
     * @return A new instance of FullConnection with the specified changes.
     */
    fun copy(
        alternativeConnection: FullConnection? = this.alternativeConnection
    ): FullConnection {
        return FullConnection(memberOne, memberTwo, relationship, alternativeConnection)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FullConnection) return false
        return memberOne == other.memberOne &&
                memberTwo == other.memberTwo &&
                relationship == other.relationship &&
                alternativeConnection == other.alternativeConnection
    }

    override fun hashCode(): Int {
        return memberOne.hashCode() * 31 +
                memberTwo.hashCode() * 17 +
                relationship.hashCode() +
                (alternativeConnection?.hashCode() ?: 0)
    }
}
