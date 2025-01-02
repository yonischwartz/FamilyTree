package com.example.familytree.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * A singleton object responsible for interacting with Firebase Firestore to manage
 * family tree data. This includes adding members, connecting members, and querying
 * family members by name.
 */
object FirebaseManager_2 {

    // FirebaseFirestore instance
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            // Configure Firestore settings if necessary
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .build() // Default settings
        }
    }

    /**
     * Adds a new family member to the Firestore database.
     *
     * @param familyMember The FamilyMember object to add to Firestore.
     * @param onSuccess Callback to be invoked upon successful addition of the family member.
     * @param onFailure Callback to be invoked if there is an error during the operation,
     *                  with an error message.
     */
    fun addNewMemberToTree(
        familyMember: FamilyMember,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("familyMembers")
            .document(familyMember.ID.toString())
            .set(familyMember)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Unknown error occurred") }
    }

    /**
     * Connects two family members in the tree by their IDs, establishing a relationship.
     *
     * @param memberOneId The ID of the first family member to connect.
     * @param memberTwoId The ID of the second family member to connect.
     * @param relation The type of relationship between the two family members (e.g., "parent", "sibling").
     * @param onSuccess Callback to be invoked upon successful connection of the family members.
     * @param onFailure Callback to be invoked if there is an error during the operation,
     *                  with an error message.
     */
    fun connectMemberToMember(
        memberOneId: Int,
        memberTwoId: Int,
        relation: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val connectionData = mapOf(
            "memberOneId" to memberOneId,
            "memberTwoId" to memberTwoId,
            "relation" to relation
        )

        firestore.collection("connections")
            .add(connectionData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Unknown error occurred") }
    }

    /**
     * Checks if a family member exists in the Firestore database by matching first and last names.
     *
     * @param firstName The first name of the family member to search for.
     * @param lastName The last name of the family member to search for.
     * @param onResult Callback to be invoked with the result of the check:
     *                 true if the member exists, false otherwise.
     * @param onFailure Callback to be invoked if there is an error during the operation,
     *                  with an error message.
     */
    fun checkIfNameExists(
        firstName: String,
        lastName: String,
        onResult: (Boolean) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("familyMembers")
            .whereEqualTo("firstName", firstName)
            .whereEqualTo("lastName", lastName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Unknown error occurred") }
    }

    /**
     * Retrieves a family member by name from the Firestore database.
     *
     * @param firstName The first name of the family member to search for.
     * @param lastName The last name of the family member to search for.
     * @param onResult Callback to be invoked with the FamilyMember object if the member is found,
     *                 or null if not found.
     * @param onFailure Callback to be invoked if there is an error during the operation,
     *                  with an error message.
     */
    fun getMemberByName(
        firstName: String,
        lastName: String,
        onResult: (FamilyMember?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("familyMembers")
            .whereEqualTo("firstName", firstName)
            .whereEqualTo("lastName", lastName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val member = querySnapshot.documents.firstOrNull()?.toObject(FamilyMember::class.java)
                onResult(member)
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Unknown error occurred") }
    }
}
