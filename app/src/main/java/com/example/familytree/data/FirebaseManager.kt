package com.example.familytree.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson

class FirebaseManager(
    private val memberMap: MemberMapByID,
    private val familyConnections: FamilyConnections
) {
    private val db = Firebase.firestore

    /**
     * Saves the given MemberMapByID to Firebase Firestore.
     */
    fun saveMemberMap() {
        val data = mutableMapOf<String, Any>()
        memberMap.idMap.forEach { (id, member) ->
            data[id.toString()] = member.toMap()
        }
        db.collection("memberMap").document("members").set(data)
    }

    /**
     * Loads the MemberMapByID from Firebase Firestore.
     */
    fun loadMemberMap() {
        db.collection("memberMap").document("members").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data
                    if (data != null) {
                        data.forEach { (id, memberData) ->
                            if (memberData is Map<*, *>) {
                                @Suppress("UNCHECKED_CAST")
                                val memberMapData = memberData as Map<String, Any>
                                val member = FamilyMember.fromMap(memberMapData)
                                memberMap.addNewMemberToIDMap(member)
                            } else {
                                println("Invalid data format for member: $id")
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error loading member map: ${exception.message}")
            }
    }

    /**
     * Saves the FamilyConnections to Firebase Firestore.
     */
    fun saveFamilyConnections() {
        familyConnections.getAdjacencyList().forEach { (memberId, connectionsForMember) ->
            val connectionData = connectionsForMember.map { connection ->
                mapOf(
                    "memberID" to connection.member.ID,
                    "relationship" to connection.relationship.name
                )
            }
            db.collection("familyConnections").document(memberId.toString())
                .set(connectionData)
        }
    }

    /**
     * Loads the FamilyConnections from Firebase Firestore.
     */
    fun loadFamilyConnections() {
        db.collection("familyConnections").get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    val memberId = document.id.toInt()

                    val connectionDataType = object : TypeToken<List<Map<String, Any>>>() {}.type
                    val connectionData: List<Map<String, Any>> = Gson().fromJson(document.data?.toString(), connectionDataType)

                    val connectionsForMember = connectionData.map { connectionMap ->
                        val memberID = connectionMap["memberID"] as Long
                        val relationship = Realations.valueOf(connectionMap["relationship"] as String)
                        val member = memberMap.idMap[memberID.toInt()]!!
                        Connection(member, relationship)
                    }

                    familyConnections.setConnectionsForMember(memberId, connectionsForMember)
                }
            }
            .addOnFailureListener { exception ->
                println("Error loading family connections: ${exception.message}")
            }
    }

    /**
     * Adds a new member to the family tree.
     *
     * @param familyMember The `FamilyMember` object to be added.
     */
    fun addNewMemberToTree(familyMember: FamilyMember) {
        // Add member to MemberMapByID
        memberMap.addNewMemberToIDMap(familyMember)

        // Add member to FamilyConnections
        familyConnections.addNewMemberToAdjacencyList(familyMember)

        // Save member to Firebase
        db.collection("memberMap").document(familyMember.ID.toString())
            .set(familyMember.toMap())
            .addOnSuccessListener { println("Member added to Firebase!") }
            .addOnFailureListener { e -> println("Error adding member: $e") }

        // Save updated connections to Firebase
        db.collection("familyConnections").document(familyMember.ID.toString())
            .set(mapOf("connections" to listOf<Map<String, Any>>()))
            .addOnSuccessListener { println("Connections added to Firebase!") }
            .addOnFailureListener { e -> println("Error adding connections: $e") }
    }

    fun addFirstMemberToTree() {}

}
