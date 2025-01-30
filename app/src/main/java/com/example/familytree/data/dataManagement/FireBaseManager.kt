package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import com.example.familytree.data.exceptions.*

/**
 * FamilyTreeData is responsible for managing family tree data,
 * including interacting with Firebase to load and store family members
 * and relationships. It handles data using member map, and adjacency list.
 */
object FireBaseManager {

    // Firebase Firestore instance
    private val db by lazy { Firebase.firestore }

    // Relationship Gender Map
    internal val relationshipGenderMap: MutableMap<Relations, Boolean> = mutableMapOf()

    /**
     * Initializes the relationshipGenderMap with predefined relationships and their expected gender.
     * This map associates each relationship type with a boolean indicating the expected gender.
     *
     * The gender values are represented as:
     * - true for male
     * - false for female
     *
     * Example mappings:
     * - Relations.FATHER -> true (male)
     * - Relations.MOTHER -> false (female)
     * - Relations.SON -> true (male)
     * - Relations.DAUGHTER -> false (female)
     *
     * This setup ensures consistency when managing family member relationships
     * and checking gender-based connections.
     */
    init {
        relationshipGenderMap[Relations.FATHER] = true  // FATHER should be male
        relationshipGenderMap[Relations.MOTHER] = false  // MOTHER should be female
        relationshipGenderMap[Relations.SON] = true  // SON should be male
        relationshipGenderMap[Relations.DAUGHTER] = false  // DAUGHTER should be female
        relationshipGenderMap[Relations.GRANDMOTHER] = false  // GRANDMOTHER should be female
        relationshipGenderMap[Relations.GRANDFATHER] = true  // GRANDFATHER should be male
        relationshipGenderMap[Relations.GRANDSON] = true  // GRANDSON should be male
        relationshipGenderMap[Relations.GRANDDAUGHTER] = false  // GRANDDAUGHTER should be female
    }

    // functions

    suspend fun checkFirestoreAccess(): Boolean {
        return try {
            // Attempt to fetch a small piece of data or perform any Firestore operation
            db.collection("members").limit(1).get().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Adds a new family member to the family tree and saves it to the Firebase Firestore database atomically.
     *
     * @param familyMember The `FamilyMember` object containing the details of the new family member to be added.
     * @throws Exception if the transaction fails.
     */
    suspend fun addNewFamilyMemberToFirebase(familyMember: FamilyMember) {
        try {
            // Run the transaction and return the document ID
            val documentId = db.runTransaction { transaction ->
                // Create a new document reference
                val documentReference = db.collection("memberMap").document()

                // Add the family member to Firestore
                transaction.set(documentReference, familyMember.toMap())

                // Update the document with its own ID
                transaction.update(documentReference, "documentId", documentReference.id)

                // Return the document ID
                documentReference.id
            }.await()

            // Update the `documentId` field of the family member locally
            familyMember.documentId = documentId

            println("Family member added and document ID updated successfully!")
        } catch (e: Exception) {
            println("Transaction failed: $e")
            throw e
        }
    }

    /**
     * Adds a new connection to a specified member's list of connections in Firebase Firestore.
     *
     * @param memberId The unique identifier of the member to whom the connection should be added.
     * @param connectionToAddToMembersList The connection object that needs to be added.
     *
     * This function retrieves the current connections list of the given member from Firestore,
     * appends the new connection, and updates Firestore with the modified list.
     * If the update is successful, it logs a success message; otherwise, it logs an error.
     */
    @Suppress("UNCHECKED_CAST")
    fun addConnectionToFirebase(
        memberId: String,
        connectionToAddToMembersList: Connection
    ) {
        db.collection("memberMap").document(memberId).get()
            .addOnSuccessListener { document ->
                val existingConnections = (document.get("connections") as? List<Map<String, Any?>>) ?: emptyList()
                val updatedConnections = existingConnections + connectionToAddToMembersList

                db.collection("memberMap").document(memberId)
                    .update("connections", updatedConnections)
                    .addOnSuccessListener {
                        println("connection added for member")
                    }
                    .addOnFailureListener { e ->
                        println("Error adding connection for member: $e")
                    }
            }
    }

    /**
     * Updates the connections field of a specific member document in Firestore.
     *
     * This function updates the `connections` field for the member identified by the
     * given `memberId` in the Firestore `memberMap` collection. The `connections` field
     * is set to the provided list of `Connection` objects.
     *
     * @param memberId The unique identifier of the member document to be updated in Firestore.
     * @param connections The list of `Connection` objects to be set as the new value for the `connections` field.
     *
     * @throws Exception If the update operation fails, an exception is thrown with an error message.
     */
    fun updateMemberConnectionsInFirebase(
        memberId: String,
        connections: MutableList<Connection>
    ) {

        try {
            // Update the 'connections' field for the specified member document
            db
                .collection("memberMap")
                .document(memberId)
                .update("connections", connections)
        } catch (e: Exception) {
            // Throw a new exception if the update fails
            throw Exception("Failed to update member connections in Firebase: ${e.message}")
        }
    }

    /**
     * Retrieves all family members from the Firebase Firestore collection "memberMap".
     *
     * This function uses a coroutine to perform the Firestore query asynchronously.
     * It returns a list of FamilyMember objects if successfully fetched, or an empty list if an error occurs.
     *
     * @return A list of FamilyMember objects retrieved from the "memberMap" collection.
     */
    suspend fun getAllMembers(): List<FamilyMember> {

        val memberCollection = db.collection("memberMap")
        val memberList = mutableListOf<FamilyMember>()

        try {
            val snapshot = memberCollection.get().await() // Awaiting the result of the query
            for (document in snapshot.documents) {

                document.toObject<FamilyMember>()?.let { memberList.add(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return memberList
    }

    /**
     * Validates the connection between two family members based on the specified relationship type.
     *
     * This function enforces specific rules for certain relationships to ensure the integrity of the family tree.
     * For some relationships (e.g., COUSINS and SIBLINGS), no validation is required as they don't have constraints
     * that need to be programmatically enforced.
     *
     * Supported validations:
     * - **MARRIAGE**: Ensures the rules for marriage relationships are followed.
     * - **FATHER/MOTHER**: Validates parent-child relationships, ensuring gender roles match and no duplicate parent roles exist.
     * - **SON/DAUGHTER**: Validates child-parent relationships, ensuring gender roles match and no duplicate child roles exist.
     * - **GRANDMOTHER/GRANDFATHER**: Validates grandparent-grandchild relationships, ensuring gender roles match.
     * - **GRANDSON/GRANDDAUGHTER**: Validates grandchild-grandparent relationships, ensuring gender roles match.
     * - **COUSINS/SIBLINGS**: No validation is required as these relationships don't have strict constraints.
     *
     * @param existingMember The first family member involved in the relationship.
     * @param newMember The second family member involved in the relationship.
     * @param relationFromExistingMemberPerspective The type of relationship being validated (e.g., MARRIAGE, FATHER, COUSINS).
     *
     * @throws InvalidGenderRoleException If the gender of a family member does not match the expected role for the specified relationship.
     * @throws InvalidMoreThanOneConnection If a relationship constraint (e.g., no duplicate parent or grandparent roles) is violated.
     * @throws SameMarriageException If the genders of the two members are the same.
     */
    suspend fun validateConnection(
        existingMember: FamilyMember,
        newMember: FamilyMember,
        relationFromExistingMemberPerspective: Relations,
        onValidation: (Boolean) -> Unit
    ) {

        // Get members' IDs
        val existingMemberId: String = existingMember.documentId.toString()
        val newMemberId: String = newMember.documentId.toString()

        // Get members' genders
        val existingMemberGender: Boolean = existingMember.getGender()
        val newMemberGender: Boolean = newMember.getGender()

        when (relationFromExistingMemberPerspective) {

            Relations.MARRIAGE ->
                validateMarriage(
                    existingMemberId,
                    existingMemberGender,
                    newMemberId,
                    newMemberGender
                )

            Relations.FATHER ->
                validateParentChildConnection(
                    existingMemberId,
                    newMember,
                    Relations.FATHER
                )

            Relations.MOTHER ->
                validateParentChildConnection(
                    existingMemberId,
                    newMember,
                    Relations.MOTHER
                )

            Relations.SON ->
                validateChildParentConnection(
                    existingMemberGender,
                    newMember,
                    newMemberId,
                    Relations.SON
                )

            Relations.DAUGHTER ->
                validateChildParentConnection(
                    existingMemberGender,
                    newMember,
                    newMemberId,
                    Relations.DAUGHTER
                )

            Relations.GRANDMOTHER ->
                validateGrandparentGrandchildConnection(
                    newMember,
                    Relations.GRANDMOTHER
                )

            Relations.GRANDFATHER ->
                validateGrandparentGrandchildConnection(
                    newMember,
                    Relations.GRANDFATHER
                )

            Relations.GRANDDAUGHTER ->
                validateGrandchildGrandparentConnection(
                    newMember,
                    Relations.GRANDDAUGHTER
                )

            Relations.GRANDSON ->
                validateGrandchildGrandparentConnection(
                    newMember,
                    Relations.GRANDSON
                )

            Relations.COUSINS -> {} // No validation required for cousins

            Relations.SIBLINGS -> {} // No validation required for siblings
        }

        // Connection is valid
        onValidation(true)
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
    fun searchForMember(searchTerm: String): List<FamilyMember> {

        // Retrieve members using a coroutine
        val members = runBlocking { getAllMembers() }
        return members.filter {
            it.getFullName().contains(searchTerm, ignoreCase = true)
        }
    }

    /**
     * Deletes a family member from the Firebase Firestore collection "memberMap" and removes this member
     * from the connections of other family members.
     *
     * @param memberToBeRemovedId The ID of the member to be deleted.
     */
    fun deleteFamilyMember(memberToBeRemovedId: String) {
        // Step 1: Delete the member from Firebase
        deleteMemberFromFirebase(memberToBeRemovedId) { success ->
            if (success) {
                // Step 2: Remove the deleted member from other members' connections
                removeMemberFromConnections(memberToBeRemovedId)
            }
        }
    }

    /**
     * Deletes a member from the "memberMap" collection in Firebase.
     *
     * @param memberId The ID of the member to be deleted.
     * @param onComplete Callback that is invoked with `true` if the deletion is successful, or `false` if it fails.
     */
    private fun deleteMemberFromFirebase(memberId: String, onComplete: (Boolean) -> Unit) {
        db.collection("memberMap").document(memberId)
            .delete()
            .addOnSuccessListener {
                println("Member deleted from Firebase successfully!")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                println("Error deleting member from Firebase: $e")
                onComplete(false)
            }
    }

    /**
     * Removes the deleted member from other members' connections in the "memberMap" collection.
     *
     * @param memberToBeRemovedId The ID of the member to be deleted.
     */
    private fun removeMemberFromConnections(memberToBeRemovedId: String) {
        // Launch a coroutine to call suspend functions
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("memberMap").get().await() // Use await() to get the result asynchronously

                for (document in result) {
                    val memberId = document.id

                    // Skip the member that was deleted
                    if (memberId != memberToBeRemovedId) {
                        val connections = document.get("connections") as? Map<*, *> ?: continue

                        // Remove the deleted member from the connections if present
                        if (connections.containsKey(memberToBeRemovedId)) {
                            val updatedConnections = connections.toMutableMap()
                            updatedConnections.remove(memberToBeRemovedId)

                            // Update the member's document with the new connections
                            updateMemberConnections(memberId, updatedConnections)
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error retrieving member documents: $e")
            }
        }
    }

    /**
     * Updates the connections of a member in the "memberMap" collection.
     *
     * @param memberId The ID of the member whose connections need to be updated.
     * @param updatedConnections The updated list of connections.
     */
    private fun updateMemberConnections(memberId: String, updatedConnections: MutableMap<Any?, Any?>) {
        db.collection("memberMap").document(memberId)
            .update("connections", updatedConnections)
            .addOnSuccessListener {
                println("Removed deleted member from $memberId's connections")
            }
            .addOnFailureListener { e ->
                println("Error updating $memberId's connections: $e")
            }
    }

    /**
     * Retrieves the connections of a family member from Firestore.
     *
     * @param id The ID of the family member whose connections need to be fetched.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun getConnectionsOfFamilyMemberById(id: String): List<Connection> {

        // Get FamilyMember document from firebase
        val document = db.collection("memberMap").document(id).get().await()

        // If document doesn't exist return an empty list
        if (!document.exists()) return emptyList()

        // Get the connections field from the document
        val connectionsList = document.get("connections") as? List<Map<String, Any>>

        // Objects are saved if the firebase as maps, so we need to converts them back to an object
        return connectionsList?.mapNotNull { map ->
            val memberId = map["memberId"] as? String
            val relationship = (map["relationship"] as? String)?.let { Relations.valueOf(it) }
            if (memberId != null && relationship != null) Connection(memberId, relationship) else null
        } ?: emptyList()
    }
}

