package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions

class FamilyTreeData {

    // Member Map
    private val idMap: MutableMap<String, FamilyMember> = mutableMapOf()

    // Adjacency List
    internal val adjacencyList: MutableMap<String, MutableList<Connection>> = mutableMapOf()

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

    // Firebase Instance
    private val db = Firebase.firestore

    // functions

    /**
     * Loads family member data and family connections from Firebase Firestore.
     * - Fetches family member information from the "memberMap" collection.
     * - Fetches family connection data from the "familyConnections" document.
     */
    fun loadDataFromFirebase() {
        fetchFamilyMemberData(db, idMap)
        fetchFamilyConnections(db, idMap, adjacencyList)
    }

    fun addNewMemberToTree(familyMember: FamilyMember) {

        // Save member to Firebase
        db.collection("memberMap").add(familyMember)
            .addOnSuccessListener { documentReference ->
                // Set the document ID in the FamilyMember object
                familyMember.documentId = documentReference.id

                // Update the document with the documentId field
                db.collection("memberMap").document(documentReference.id)
                    .update("documentId", documentReference.id)
                    .addOnSuccessListener {
                        println("Document ID updated successfully!")
                    }
                    .addOnFailureListener { e ->
                        println("Error updating document ID: $e")
                    }

                // Save updated connections to Firebase using the newly generated documentId
                db.collection("familyConnections").document(documentReference.id)
                    .set(mapOf("connections" to listOf<Map<String, Any>>()))
                    .addOnSuccessListener { println("Connections added to Firebase!") }
                    .addOnFailureListener { e -> println("Error adding connections: $e") }

            }
            .addOnFailureListener { e -> println("Error adding member: $e") }

        // Add member to IDMap
        idMap[familyMember.documentId] = familyMember

        // Add member to AdjacencyList
        adjacencyList[familyMember.documentId] = mutableListOf()

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
     * Retrieves all connections associated with a given family member.
     *
     * @param memberId The unique identifier of the family member.
     * @return A list of Connection objects representing relationships of the member, or null if no connections exist.
     */
    fun getConnectionsForMember(memberId: String): List<Connection>? {
        return adjacencyList[memberId]
    }
}
