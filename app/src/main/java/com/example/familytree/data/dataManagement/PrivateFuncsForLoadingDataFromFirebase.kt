package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

/**
 * Utility functions for managing family tree data, extracted from FamilyTreeData.
 */

/**
 * Processes a collection of DocumentSnapshot objects and populates the provided idMap
 * with FamilyMember instances. Each document ID serves as the key, and the FamilyMember
 * object is the corresponding value. Skips documents that do not map to valid FamilyMember objects.
 *
 * @param documents The Iterable collection of DocumentSnapshot objects from Firebase Firestore.
 * @param idMap The mutable map to store FamilyMember objects indexed by their document IDs.
 */
internal fun loadFamilyMembersFromDocuments(
    documents: Iterable<DocumentSnapshot>,
    idMap: MutableMap<String, FamilyMember>
) {
    for (document in documents) {
        val familyMember = document.toObject(FamilyMember::class.java)
        if (familyMember != null) {
            idMap[document.id] = familyMember
        } else {
            println("Skipped null FamilyMember for document ID: \${document.id}")
        }
    }
    println("Members loaded from Firebase")
}

/**
 * Parses family connection data from Firebase Firestore and updates the adjacency list.
 * This function converts the raw data into a structured form suitable for managing
 * relationships between family members, mapping each member ID to a list of connections.
 *
 * @param data The raw map containing connection data from Firestore.
 * @param idMap The map of FamilyMember objects indexed by their IDs.
 * @param adjacencyList The mutable adjacency list to populate with Connection objects.
 */
internal fun loadFamilyConnectionsFromData(
    data: Map<String, Any>,
    idMap: MutableMap<String, FamilyMember>,
    adjacencyList: MutableMap<String, MutableList<Connection>>
) {
    val connections = data["connections"] as? Map<*, *> ?: return
    val castedConnections = castConnections(connections)
    val adjacencyListData = buildAdjacencyList(castedConnections, idMap)
    adjacencyList.clear()
    adjacencyList.putAll(adjacencyListData)
    println("Adjacency list loaded from Firebase")
}

/**
 * Fetches family member data from the "memberMap" collection in Firebase Firestore.
 * Upon successful retrieval, loads the data into the provided idMap.
 *
 * @param db The FirebaseFirestore instance for accessing Firestore.
 * @param idMap The map to populate with FamilyMember objects.
 */
internal fun fetchFamilyMemberData(
    db: FirebaseFirestore,
    idMap: MutableMap<String, FamilyMember>
) {
    db.collection("memberMap").get()
        .addOnSuccessListener { documents ->
            loadFamilyMembersFromDocuments(documents, idMap)
        }
        .addOnFailureListener { e ->
            println("Error loading members: \$e")
        }
}

/**
 * Fetches family connection data from the "familyConnections" document in Firebase Firestore.
 * Processes and updates the adjacency list using the fetched data.
 *
 * @param db The FirebaseFirestore instance for accessing Firestore.
 * @param idMap The map of FamilyMember objects indexed by their IDs.
 * @param adjacencyList The mutable adjacency list to populate with Connection objects.
 */
internal fun fetchFamilyConnections(
    db: FirebaseFirestore,
    idMap: MutableMap<String, FamilyMember>,
    adjacencyList: MutableMap<String, MutableList<Connection>>
) {
    db.collection("familyConnections").document("connections").get()
        .addOnSuccessListener { document ->
            document.data?.let { data ->
                loadFamilyConnectionsFromData(data, idMap, adjacencyList)
            }
        }
        .addOnFailureListener { e ->
            println("Error loading adjacency list: \$e")
        }
}

/**
 * Converts a raw map of connection data into a type-safe map of connection lists.
 * This function ensures type safety by filtering and transforming the input map
 * into a format suitable for further processing in the family tree data structure.
 *
 * @param connections The raw map representing family connections.
 * @return A type-safe map with string keys and lists of maps as values.
 */
internal fun castConnections(connections: Map<*, *>): Map<String, List<Map<String, Any>>> {
    return connections.mapNotNull { (key, value) ->
        if (key is String && value is List<*>) {
            val castedValue = value.filterIsInstance<Map<String, Any>>()
            key to castedValue
        } else null
    }.toMap()
}

/**
 * Builds an adjacency list from the provided casted connections and idMap.
 * Each connection is represented as a list of Connection objects for each member ID.
 * This function uses the relation string to construct Connection objects that store
 * references to FamilyMember instances and their respective Relations enum values.
 *
 * @param castedConnections The type-safe map of connections.
 * @param idMap The map of FamilyMember objects indexed by their IDs.
 * @return A map representing the adjacency list for family member connections.
 */
internal fun buildAdjacencyList(
    castedConnections: Map<String, List<Map<String, Any>>>,
    idMap: MutableMap<String, FamilyMember>
): Map<String, MutableList<Connection>> {
    return castedConnections.mapValues { entry ->
        entry.value.mapNotNull { connectionMap ->
            val familyMemberId = connectionMap["id"] as? String ?: ""
            val familyMember = idMap[familyMemberId] ?: return@mapNotNull null
            val relationString = connectionMap["relation"] as? String ?: ""
            val relation = parseRelation(relationString) ?: return@mapNotNull null
            Connection(familyMember, relation)
        }.toMutableList()
    }
}

/**
 * Parses a string representation of a relation into a Relations enum value.
 * Handles invalid input by returning null and logging a message.
 *
 * @param relationString The string representation of a relation.
 * @return The corresponding Relations enum value or null if invalid.
 */
internal fun parseRelation(relationString: String): Relations? {
    return try {
        Relations.valueOf(relationString.uppercase(Locale.ROOT))
    } catch (e: IllegalArgumentException) {
        println("Invalid relation: $relationString. Skipping connection.")
        null
    }
}
