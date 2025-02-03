package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.QuerySnapshot

/**
 * FamilyTreeData is responsible for managing family tree data,
 * including interacting with Firebase to load and store family members
 * and relationships. It handles data using member map, and adjacency list.
 */
object DatabaseManager {

    // Firebase Firestore instance
    private val firebase by lazy { Firebase.firestore }

    // MemberMap instance
    private val memberMap = MemberMap

    // functions

    /**
     * Fetches all family members from Firestore and populates the MemberMap.
     * @param onComplete Callback that is invoked once the operation is finished.
     */
    fun loadMembersFromFirebaseIntoLocalMap(onComplete: (Boolean) -> Unit) {
        firebase.collection("memberMap").get()
            .addOnSuccessListener { snapshot ->
                populateMemberMap(snapshot) {
                    onComplete(true)
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Parses Firestore documents and populates the MemberMap.
     * @param snapshot The Firestore query snapshot containing all members.
     * @param onComplete Callback that is invoked once the operation is finished.
     */
    @Suppress("UNCHECKED_CAST") // Suppress unchecked cast warning
    private fun populateMemberMap(snapshot: QuerySnapshot, onComplete: () -> Unit) {

        snapshot.documents.forEach { document ->

            val id = document.id
            val firstName = document.getString("firstName")
            val lastName = document.getString("lastName")
            val gender = document.getBoolean("gender")
            val isRabbi = document.getBoolean("isRabbi")
            val machzor = document.getLong("machzor")?.toInt()
            val memberTypeString = document.getString("memberType")
            val connectionsAsMaps = document.get("connections") as? MutableList<Map<String, Any>>


            // Ensure all required fields are present
            if (
                firstName == null ||
                lastName == null ||
                gender == null ||
                isRabbi == null ||
                memberTypeString == null ||
                connectionsAsMaps == null
            ) {
                return@forEach  // Skips this document if any field is missing
            }

            // Parse connections (since connections are required, we assume it's never null here)
            val connections = connectionsAsMaps.mapNotNull { map ->
                val memberId = map["memberId"] as? String
                val relationshipStr = map["relationship"] as? String
                val relationship = relationshipStr?.let { runCatching { Relations.valueOf(it) }.getOrNull() }

                if (memberId != null && relationship != null) {
                    Connection(memberId, relationship)
                } else {
                    null // Skip invalid connection entries
                }
            }.toMutableList()

            val member = FamilyMember(
                id = id,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                isRabbi = isRabbi,
                machzor = machzor,
                memberType = MemberType.valueOf(memberTypeString),
                connections = connections
            )

            MemberMap.addMember(member)
        }

        // Invoke callback after all members are added
        onComplete()
    }

    /**
     * Adds a new family member to the local MemberMap.
     *
     * @param member The `FamilyMember` object to be added to the local `MemberMap`.
     */
    fun addNewMemberToLocalMemberMap(member: FamilyMember): Boolean {
        memberMap.addMember(member)
        return true
    }

    /**
     * Adds a connection between two family members based on the relationship type.
     *
     * @param memberOne The first family member.
     * @param memberTwo The second family member.
     * @param relationFromMemberOnePerspective The relationship type from memberOne's perspective.
     * @return A Boolean value indicating whether the operation was successful.
     */
    fun addConnectionToBothMembersInLocalMap(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations,
    ): Boolean {

        memberMap.addConnectionToBothMembers(memberOne, memberTwo, relationFromMemberOnePerspective)
        return true
    }

    /**
     * Retrieves all family members stored in the local MemberMap.
     *
     * @return A list of all `FamilyMember` objects stored in the `MemberMap`.
     */
    fun getAllMembers(): List<FamilyMember> {
        return memberMap.getAllMembers()
    }

    /**
     * Validates the connection between two family members
     *
     * This function checks various types of relationships (e.g., marriage, parent-child, etc.) and
     * ensures that the connection adheres to specific rules based on gender and the number of connections.
     *
     * @param memberOne The first family member involved in the relationship.
     * @param memberTwo The second family member involved in the relationship.
     * @param relationFromMemberOnePerspective The relationship type as seen from member one's perspective.
     * @return True if the connection is valid; if the connection fails validation, exceptions are thrown.
     */
    fun validateConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations,
    ): Boolean {
        memberMap.validateConnection(memberOne, memberTwo, relationFromMemberOnePerspective)
        return true
    }

    /**
     * Searches for family members whose full name matches or contains the provided search term.
     *
     * This function filters the list of all members retrieved from Firestore based on the search term.
     * The search is case-insensitive and matches full names or substrings of names.
     *
     * @param searchTerm The string representing either a full name or a substring of a name to search for.
     * @return A list of FamilyMember objects whose names match or contain the search term.
     */
    fun searchForMemberInLocalMap(searchTerm: String): List<FamilyMember> {

        return memberMap.searchForMember(searchTerm)
    }

    /**
     * Deletes a family member from the local MemberMap.
     *
     * This function removes the specified family member, identified by their unique ID,
     * from the local `MemberMap`. It ensures that the member is no longer stored locally.
     *
     * @param memberToBeRemovedId The ID of the family member to be removed from the `MemberMap`.
     */
    fun deleteMemberFromLocalMemberMap(memberToBeRemovedId: String) {
        memberMap.deleteMember(memberToBeRemovedId)
    }
}

