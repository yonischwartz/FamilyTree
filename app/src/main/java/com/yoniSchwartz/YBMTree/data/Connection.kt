package com.yoniSchwartz.YBMTree.data

/**
 * Represents a connection between a member and their relationship in the family tree.
 *
 * @param memberId The ID of the family member.
 * @param relationship The relationship type between this member and the current one.
 */
data class Connection(
    val memberId: String,
    val relationship: Relations
) {

    /**
     * Converts the Connection object to a map for Firebase storage.
     *
     * @return A map representing the Connection object.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "memberId" to memberId,
            "relationship" to relationship.name,
        )
    }
}

