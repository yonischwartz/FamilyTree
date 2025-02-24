package com.example.familytree.data.dataManagement

import android.util.Log
import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FullConnection
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
     * Checks if the family tree is empty by verifying if the member map is empty.
     * @return true if the family tree is empty, false otherwise.
     */
    fun isTreeEmpty(): Boolean {
        return memberMap.getAllMembers().isEmpty()
    }

    /**
     * Retrieves a family member by their unique ID.
     * @param memberId The ID of the family member.
     * @return The FamilyMember object if found, otherwise null.
     */
    fun getMemberById(memberId: String): FamilyMember? {
        return memberMap.getMember(memberId)
    }

    /**
     * Saves the locally modified members and deletions to Firebase Firestore.
     *
     * This function batches all new, updated, and deleted members into a single Firestore batch operation.
     * It clears the local modified and deleted member lists upon successful completion.
     *
     * @param onComplete A callback function that receives `true` if the operation succeeds, `false` otherwise.
     */
    fun saveLocalMapToFirebase(onComplete: (Boolean) -> Unit) {

        // Batch is a way to make sure all the operations in the batch are executed together
        // either they all succeed or they all fail.
        val batch = firebase.batch()
        val membersCollection = firebase.collection("memberMap")

        // Add or update modified members
        for (memberId in memberMap.getModifiedAndNewAddedMembersIds()) {
            val memberRef = membersCollection.document(memberId)
            memberMap.getMember(memberId)?.let { batch.set(memberRef, it.toMap()) }
        }

        // Remove deleted members
        for (memberId in memberMap.getDeletedMembersIds()) {
            val memberRef = membersCollection.document(memberId)
            batch.delete(memberRef)
        }

        // Commit the batch operation
        batch.commit().addOnSuccessListener {
            // Clear local tracking after successful save
            memberMap.clearAllTrackedChanges()
            onComplete(true)
        }.addOnFailureListener {
            // Handle failure
            onComplete(false)
        }
    }

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
            val isRabbi = document.getBoolean("rabbi")
            val isYeshivaRabbi = document.getBoolean("yeshivaRabbi")
            val machzor = document.getLong("machzor")?.toInt()
            val memberTypeString = document.getString("memberType")
            val connectionsAsMaps = document.get("connections") as? MutableList<Map<String, Any>>


            // Ensure all required fields are present
            if (
                firstName == null ||
                lastName == null ||
                gender == null ||
                isRabbi == null ||
                isYeshivaRabbi == null ||
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
                isYeshivaRabbi = isYeshivaRabbi,
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
     * Adds a new family member to the local MemberMap and tracks it in
     * modifiedAndNewAddedMembersIds list for future updates to Firebase.
     *
     * @param member The `FamilyMember` object to be added to the local `MemberMap`.
     * @return `true` if the member was added successfully, `false` otherwise.
     */
    fun addNewMemberToLocalMemberMap(member: FamilyMember): Boolean {

        // Add member to local memberMap
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

        // Add connection to local map
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
     * Deletes a family member from the local member map and updates the lists of modified members.
     *
     * This function performs the following steps:
     *
     * 1. Deletes the specified member from the `memberMap` and removes them from all other members' connections.
     *
     * 2. Adds the deleted member's ID to the `deletedMembersIds` list if it is not already included.
     *
     * 3. Adds the IDs of the members whose connections were updated (from step 1) to the `modifiedAndNewAddedMembersIds` list,
     *    ensuring the list reflects all members affected by the deletion.
     *
     * @param memberToBeRemovedId The ID of the member to remove from the local member map and update connections for.
     * @return `true` if the member was successfully deleted, `false` otherwise.
     */
    fun deleteMemberFromLocalMemberMap(memberToBeRemovedId: String): Boolean {
        return memberMap.deleteMember(memberToBeRemovedId)
    }

    /**
     * Retrieves and removes the next suggested connection from the queue.
     *
     * @return The next `FullConnection` from the queue, or `null` if the queue is empty.
     */
    fun popNextSuggestedConnection(): FullConnection? {
        return memberMap.popNextSuggestedConnection()
    }

    /**
     * Finds the shortest path between two family members using a BFS algorithm.
     *
     * @param memberOne The starting family member.
     * @param memberTwo The target family member.
     * @return A list of FamilyMember objects representing the shortest path from memberOne to memberTwo.
     *         If no path is found, the list will be empty.
     */
    internal fun getShortestPathBetweenTwoMembers(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ): List<FamilyMember> {

        return memberMap.getShortestPathBetweenTwoMembers(memberOne, memberTwo)
    }

    /**
     * Retrieves without removing the next suggested connection from the queue.
     *
     * @return The next `FullConnection` from the queue, or `null` if the queue is empty.
     */
    fun getNextSuggestedConnection(): FullConnection? {
        return memberMap.popNextSuggestedConnection()
    }

    /**
     * Checks if the queue of suggested connections is empty.
     *
     * @return `false` if the queue is empty, `true` otherwise.
     */
    fun isQueueOfSuggestedConnectionsNotEmpty(): Boolean {
        return memberMap.isQueueOfSuggestedConnectionsNotEmpty()
    }
}

