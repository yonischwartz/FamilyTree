package com.example.familytree.data

class FullConnection(
    val memberOne: FamilyMember,
    val memberTwo: FamilyMember,
    val relationship: Relations
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FullConnection) return false
        return memberOne == other.memberOne &&
                memberTwo == other.memberTwo &&
                relationship == other.relationship
    }

    override fun hashCode(): Int {
        return memberOne.hashCode() * 31 + memberTwo.hashCode() * 17 + relationship.hashCode()
    }
}
