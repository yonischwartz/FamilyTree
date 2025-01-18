package com.example.familytree.data.dataManagement

import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * FamilyTreeData is responsible for managing family tree data,
 * including interacting with Firebase to load and store family members
 * and relationships. It handles data using member map, and adjacency list.
 */
object FamilyTreeData {

    // Member Map
    internal val idMap: MutableMap<String, FamilyMember> = mutableMapOf()

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

    // Firebase Firestore instance
    internal val db by lazy { Firebase.firestore }

    // functions

    /**
     * Loads family member data and from Firebase Firestore.
     * - Fetches family member information from the "memberMap" collection.
     */
    fun loadDataFromFirebase() {
        fetchFamilyMemberData(db, idMap)
    }

    /**
     * Adds a new family member to the tree (including both yeshiva and non-yeshiva members)
     * and saves it in Firebase.
     * The member is added to the "memberMap" collection in Firestore,
     * and a corresponding document for family connections is created.
     *
     * @param familyMember The family member object to be added.
     */
    fun addNewFamilyMemberToTree(familyMember: FamilyMember) {

        // Save member to Firebase
        db.collection("memberMap").add(familyMember)
            .addOnSuccessListener { documentReference ->
                // Set the document ID in the FamilyMember object
                familyMember.documentId = documentReference.id
                println("DocumentSnapshot added with ID: ${documentReference.id}")

                // Update the firebase documentId field
                db.collection("memberMap").document(documentReference.id)
                    .update("documentId", documentReference.id)
                    .addOnSuccessListener {
                        println("Document ID updated successfully!")
                    }
                    .addOnFailureListener { e ->
                        println("Error updating document ID: $e")
                    }

                // Add member to IDMap
                idMap[familyMember.documentId] = familyMember
            }
    }


    /**
     * Retrieves all family members from the ID map.
     *
     * @return A list of all FamilyMember objects.
     */
    fun getAllMembers(): List<FamilyMember> {
        return idMap.values.toList()
    }

    /**
     * Adds a connection between two family members in the adjacency list.
     * This function validates the existence of both members before establishing the connection.
     *
     * @param memberOne The first FamilyMember involved in the connection.
     * @param memberTwo The second FamilyMember involved in the connection.
     * @param relation The type of relation between the two members.
     */
    fun addConnectionToAdjacencyList(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relation: Relations
    ) {
        validateMembersExist(memberOne, memberTwo)
        when (relation) {
            Relations.MARRIAGE -> addMarriageConnection(memberOne, memberTwo)
            Relations.FATHER -> addParentChildConnection(memberOne, memberTwo, Relations.FATHER)
            Relations.MOTHER -> addParentChildConnection(memberOne, memberTwo, Relations.MOTHER)
            Relations.SON -> addChildParentConnection(memberOne, memberTwo, Relations.SON)
            Relations.DAUGHTER -> addChildParentConnection(
                memberOne, memberTwo,
                Relations.DAUGHTER
            )

            Relations.GRANDMOTHER -> addGrandparentGrandchildConnection(
                memberOne, memberTwo,
                Relations.GRANDMOTHER
            )

            Relations.GRANDFATHER -> addGrandparentGrandchildConnection(
                memberOne, memberTwo,
                Relations.GRANDFATHER
            )

            Relations.GRANDDAUGHTER -> addGrandchildGrandparentConnection(
                memberOne, memberTwo,
                Relations.GRANDDAUGHTER
            )

            Relations.GRANDSON -> addGrandchildGrandparentConnection(
                memberOne, memberTwo,
                Relations.GRANDSON
            )

            Relations.COUSINS -> addCousinsConnection(memberOne, memberTwo)
            Relations.SIBLINGS -> addSiblingConnection(memberOne, memberTwo)
        }
    }

    /**
     * Searches for family members whose full name matches or contains the provided search term.
     *
     * @param searchTerm The string representing either a full name or a substring of a name to search for.
     * @return A list of FamilyMember objects whose names match or contain the search term.
     */
    fun searchForMember(searchTerm: String): List<FamilyMember> {
        val searchLower = searchTerm.lowercase()
        return idMap.values.filter { it.getFullName().lowercase().contains(searchLower) }
    }

    /**
     * Deletes a family member from the family tree, including all their connections.
     * The member is deleted from the ID map, adjacency list, and Firebase.
     *
     * @param memberToBeRemovedId The unique identifier of the family member to be deleted.
     * @return True if the member was successfully deleted, false if the member was not found.
     */
    fun deleteFamilyMember(memberToBeRemovedId: String) {

        // Delete the member from other members connection lists
        idMap.forEach { (_, familyMember) ->
            familyMember.removeConnectionFromAdjacencyList(memberToBeRemovedId)
        }

        // Delete the member from the ID map
        idMap.remove(memberToBeRemovedId)

        // Delete member and connections from Firebase
        db.collection("memberMap").document(memberToBeRemovedId)
            .delete()
            .addOnSuccessListener { println("Member deleted from Firebase successfully!") }
            .addOnFailureListener { e -> println("Error deleting member from Firebase: $e") }
    }
}

