package com.example.familytree.data

/**
 * The {@code Connection} class represents a relationship between a family member
 * and another member in the family tree. This class encapsulates both the family
 * member involved in the connection and the type of relationship from the perspective
 * of the owner of the connection.
 *
 * <p>In a family tree, connections are not merely links between nodes; they also
 * carry specific semantics about the type of relationship (e.g., FATHER, SON, etc.).
 * This class allows precise and organized representation of such relationships,
 * ensuring that the connections are both meaningful and easy to traverse.</p>
 *
 * <p>The {@code Connection} class is particularly useful in adjacency list
 * implementations, where each family member's connections need to be stored
 * alongside the type of relationship. By using this class, we can efficiently
 * manage and query relationships within the family tree.</p>
 *
 * <p>For example, if a family member has multiple connections (e.g., children or siblings),
 * each connection can be stored as a {@code Connection} object, making it clear
 * how the members are related without ambiguity.</p>
 */
data class Connection(
    val member: FamilyMember,
    val relationship: Realations
)
