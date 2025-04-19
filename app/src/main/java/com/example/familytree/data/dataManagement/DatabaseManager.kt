package com.example.familytree.data.dataManagement

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FullConnection
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * FamilyTreeData is responsible for managing family tree data,
 * including interacting with Firebase to load and store family members
 * and relationships. It handles data using member map, and adjacency list.
 */
object DatabaseManager {

    // Firebase Firestore instance
    private val firebase by lazy { Firebase.firestore }

    // Firebase Authentication instance
    private val auth = FirebaseAuth.getInstance()

    // Firebase Storage instance
    private val storage = Firebase.storage

    // MemberMap instance
    private val memberMap = MemberMap

    // A const that holds the name of the family tree graph file in Firebase Storage
    private const val FAMILY_TREE_GRAPH_FILE_NAME = "family_tree_graph.png"
    private const val PREFIX_GRAPH_FILE_NAME = "family_tree_graph"
    private const val SUFFIX_GRAPH_FILE_NAME = ".png"

    // functions

    /**
     * Attempts to sign in the user using Firebase Authentication with the provided email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param onResult Callback that returns true if sign-in was successful, false otherwise.
     */
    fun signIn(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    /**
     * Checks if there is a currently signed-in Firebase user.
     *
     * @return True if a user is signed in, false otherwise.
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Signs out the currently signed-in user from Firebase Authentication.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Downloads the family tree image from Firebase Storage and saves it locally.
     * If the download fails (e.g., due to lack of internet), this function falls back
     * to using the previously downloaded image from the app's local storage.
     *
     * @param context Context used to access internal file storage.
     * @param onFinished Callback invoked with the downloaded or cached [File] object, or null if unavailable.
     */
    fun downloadFamilyTreeImageToCache(
        context: Context,
        onFinished: (File?) -> Unit
    ) {
        val localDir = context.filesDir
        val cachedFile = File(localDir, FAMILY_TREE_GRAPH_FILE_NAME)

        val storageRef = FirebaseStorage.getInstance().reference.child(FAMILY_TREE_GRAPH_FILE_NAME)

        // Attempt to download the image from Firebase Storage
        storageRef.getFile(cachedFile)
            .addOnSuccessListener {

                // Download successful - return the cached file
                onFinished(cachedFile)
            }
            .addOnFailureListener {
                it.printStackTrace()

                // Fallback: if we have a cached file from a previous run, use it
                if (cachedFile.exists()) {
                    onFinished(cachedFile)
                } else {
                    // No file available - return null
                    onFinished(null)
                }
            }
    }

    fun downloadFamilyTreeImageWithProgress(
        onProgress: (Int) -> Unit,
        onFinished: (File?) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child(FAMILY_TREE_GRAPH_FILE_NAME)
        val localFile = File.createTempFile(PREFIX_GRAPH_FILE_NAME, SUFFIX_GRAPH_FILE_NAME)

        storageRef.getFile(localFile)
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                onProgress(progress)
            }
            .addOnSuccessListener {
                onFinished(localFile)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onFinished(null)
            }
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
     * Updates an existing family member in the local MemberMap.
     *
     * @param idOfMemberToBeUpdated The ID of the family member to be updated.
     * @param updatedMember The updated `FamilyMember` object.
     */
    fun updateMember(idOfMemberToBeUpdated: String, updatedMember: FamilyMember) {
        // Update member in local map
        memberMap.updateMember(idOfMemberToBeUpdated, updatedMember)
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
     * Retrieves a list of all family members who are Yeshiva members.
     *
     * @return A list of [FamilyMember] objects whose [MemberType] is [MemberType.Yeshiva].
     */
    fun getAllYeshivaMembers(): List<FamilyMember> {
        return memberMap.getAllYeshivaMembers()
    }

    /**
     * Retrieves a list of family members who belong to a specific machzor.
     *
     * @param machzor The machzor number to filter by. If null, members who didn't learn in the yeshiva will be returned.
     * @return A list of [FamilyMember] instances that match the given machzor.
     */
    fun getMembersByMachzor(machzor: Int?): List<FamilyMember> {
        return memberMap.getMembersByMachzor(machzor)
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
     * Retrieves the relationship between a family member and one of their connections.
     *
     * @param memberOne The family member whose connections are being checked.
     * @param memberTwo The family member to find within the connections of [memberOne].
     * @return The [Relations] enum representing the relationship between [memberOne] and [memberTwo],
     *         or null if no such connection exists.
     */
    internal fun getRelationBetweenMemberAndOneOfHisConnections(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ): Relations? {

        return memberMap.getRelationBetweenMemberAndOneOfHisConnections(memberOne, memberTwo)

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
     * 2. Adds the deleted member's ID to the `deletedMembersIds` list if it is not already included.
     * 3. Adds the IDs of the members whose connections were updated (from step 1) to the `modifiedAndNewAddedMembersIds` list,
     *    ensuring the list reflects all members affected by the deletion.
     *
     * @param memberToBeRemovedId The ID of the member to remove from the local member map and update connections for.
     * @return `true` if the member was successfully deleted, `false` otherwise.
     */
    fun removeMemberFromLocalMemberMap(memberToBeRemovedId: String) {

        // Remove member from local map. Throws exception in case of unsafe delete
        memberMap.deleteMember(memberToBeRemovedId)
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
}

