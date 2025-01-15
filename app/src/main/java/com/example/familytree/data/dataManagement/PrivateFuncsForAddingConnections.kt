package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.FamilyTreeData.db
import com.example.familytree.data.exceptions.*
import com.google.firebase.firestore.SetOptions

/**
 * Helper functions for managing family tree relationships.
 * These functions are used for validating and adding relationships between family members in the tree.
 */

/**
 * Validates that the member's gender matches the expected gender for the specified relationship.
 *
 * @param member The family member whose gender is being validated.
 * @param relationship The relationship type to check against the member's gender.
 * @throws InvalidGenderRoleException If the member's gender does not match the expected gender for the relationship.
 */
internal fun FamilyTreeData.validateGenderRole(member: FamilyMember, relationship: Relations) {
    val expectedGender = relationshipGenderMap[relationship]
    if (expectedGender != null && member.getGender() != expectedGender) {
        throw InvalidGenderRoleException(member, relationship)
    }
}

/**
 * Validates that both family members exist in the adjacency list.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 * @throws MemberNotInAdjacencyListException If either member is not present in the adjacency list.
 */
internal fun FamilyTreeData.validateMembersExist(memberOne: FamilyMember, memberTwo: FamilyMember) {
    if (!idMap.containsKey(memberOne.documentId)) {
        throw MemberNotInAdjacencyListException(memberOne)
    }
    if (!idMap.containsKey(memberTwo.documentId)) {
        throw MemberNotInAdjacencyListException(memberTwo)
    }
}

/**
 * Validates the marriage relationship between two family members.
 * A marriage cannot exist if either member is already married, and the genders must be different.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 * @throws InvalidRelationshipException If the relationship is invalid (e.g., both members are the same gender, or one or both are already married).
 */
internal fun FamilyTreeData.validateMarriage(memberOne: FamilyMember, memberTwo: FamilyMember) {
    validateNotMoreThanOneMemberConnection(memberOne, Relations.MARRIAGE)
    validateNotMoreThanOneMemberConnection(memberTwo, Relations.MARRIAGE)
    if (memberOne.getGender() == memberTwo.getGender()) {
        throw InvalidRelationshipException("Same-gender marriages are not allowed.")
    }
}

/**
 * Validates that a family member does not already have an existing connection of a specific relationship type.
 *
 * @param member The family member to check for existing connections.
 * @param relationship The type of relationship to check for.
 * @throws InvalidRelationshipException If the member already has an existing connection of the specified relationship.
 */
internal fun FamilyTreeData.validateNotMoreThanOneMemberConnection(member: FamilyMember, relationship: Relations) {
    val connections = idMap[member.documentId]?.getConnections()
    if (connections != null) {
        for (connection in connections) {
            if (relationship == connection.relationship) {
                throw InvalidRelationshipException(member, relationship)
            }
        }
    }
}

/**
 * Adds a marriage connection between two family members.
 * This function validates that the relationship is valid before adding the connection.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 * @throws InvalidRelationshipException If the marriage relationship is invalid.
 */
internal fun FamilyTreeData.addMarriageConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
    validateMarriage(memberOne, memberTwo)
    memberOne.addConnectionToAdjacencyList(Connection(memberTwo, Relations.MARRIAGE))
    memberTwo.addConnectionToAdjacencyList(Connection(memberOne, Relations.MARRIAGE))
    updateMemberConnections(memberOne)
    updateMemberConnections(memberTwo)
}

/**
 * Adds a parent-child connection, validating the parent-child relationship.
 * The parent-child relationship is validated by gender role and uniqueness of the relationship.
 *
 * @param parent The parent family member.
 * @param child The child family member.
 * @param parentRelation The relationship type for the parent (either FATHER or MOTHER).
 * @throws InvalidGenderRoleException If the parent's gender does not match the expected gender for the relationship.
 * @throws InvalidRelationshipException If the child already has a parent-child connection with the same relationship.
 */
internal fun FamilyTreeData.addParentChildConnection(
    parent: FamilyMember,
    child: FamilyMember,
    parentRelation: Relations
) {
    val childRelation = if (child.getGender()) Relations.SON else Relations.DAUGHTER
    validateGenderRole(parent, parentRelation)
    validateNotMoreThanOneMemberConnection(child, parentRelation)
    child.addConnectionToAdjacencyList(Connection(parent, parentRelation))
    parent.addConnectionToAdjacencyList(Connection(child, childRelation))
    updateMemberConnections(parent)
    updateMemberConnections(child)
}

/**
 * Adds a child-parent connection, validating the child-parent relationship.
 * The child-parent relationship is validated by gender role and uniqueness of the relationship.
 *
 * @param child The child family member.
 * @param parent The parent family member.
 * @param childRelation The relationship type for the child (either SON or DAUGHTER).
 * @throws InvalidGenderRoleException If the child's gender does not match the expected gender for the relationship.
 * @throws InvalidRelationshipException If the parent already has a child-parent connection with the same relationship.
 */
internal fun FamilyTreeData.addChildParentConnection(
    child: FamilyMember,
    parent: FamilyMember,
    childRelation: Relations
) {
    val parentRelation = if (parent.getGender()) Relations.FATHER else Relations.MOTHER
    validateGenderRole(child, childRelation)
    validateNotMoreThanOneMemberConnection(child, parentRelation)
    child.addConnectionToAdjacencyList(Connection(parent, parentRelation))
    parent.addConnectionToAdjacencyList(Connection(child, childRelation))
    updateMemberConnections(parent)
    updateMemberConnections(child)
}

/**
 * Adds a grandparent-grandchild connection, validating the grandparent-grandchild relationship.
 *
 * @param grandparent The grandparent family member.
 * @param grandchild The grandchild family member.
 * @param grandparentRelation The relationship type for the grandparent (either GRANDMOTHER or GRANDFATHER).
 * @throws InvalidGenderRoleException If the grandparent's gender does not match the expected gender for the relationship.
 */
internal fun FamilyTreeData.addGrandparentGrandchildConnection(
    grandparent: FamilyMember,
    grandchild: FamilyMember,
    grandparentRelation: Relations
) {
    val grandchildRelation = if (grandchild.getGender()) Relations.GRANDSON else Relations.GRANDDAUGHTER
    validateGenderRole(grandparent, grandparentRelation)
    grandchild.addConnectionToAdjacencyList(Connection(grandparent, grandparentRelation))
    grandparent.addConnectionToAdjacencyList(Connection(grandchild, grandchildRelation))
    updateMemberConnections(grandparent)
    updateMemberConnections(grandchild)
}

/**
 * Adds a grandchild-grandparent connection, validating the grandchild-grandparent relationship.
 *
 * @param grandchild The grandchild family member.
 * @param grandparent The grandparent family member.
 * @param grandchildRelation The relationship type for the grandchild (either GRANDDAUGHTER or GRANDSON).
 * @throws InvalidGenderRoleException If the grandchild's gender does not match the expected gender for the relationship.
 */
internal fun FamilyTreeData.addGrandchildGrandparentConnection(
    grandchild: FamilyMember,
    grandparent: FamilyMember,
    grandchildRelation: Relations
) {
    val grandparentRelation = if (grandparent.getGender()) Relations.GRANDFATHER else Relations.GRANDMOTHER
    validateGenderRole(grandchild, grandchildRelation)
    grandchild.addConnectionToAdjacencyList(Connection(grandparent, grandparentRelation))
    grandparent.addConnectionToAdjacencyList(Connection(grandchild, grandchildRelation))
    updateMemberConnections(grandparent)
    updateMemberConnections(grandchild)
}

/**
 * Adds a cousins connection between two family members.
 * This is a symmetric relationship where both members are connected as cousins.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 */
internal fun FamilyTreeData.addCousinsConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
    memberOne.addConnectionToAdjacencyList(Connection(memberTwo, Relations.COUSINS))
    memberTwo.addConnectionToAdjacencyList(Connection(memberOne, Relations.COUSINS))
    updateMemberConnections(memberOne)
    updateMemberConnections(memberTwo)
}

/**
 * Adds a sibling connection between two family members.
 * This is a symmetric relationship where both members are connected as siblings.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 */
internal fun FamilyTreeData.addSiblingConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
    memberOne.addConnectionToAdjacencyList(Connection(memberTwo, Relations.SIBLINGS))
    memberTwo.addConnectionToAdjacencyList(Connection(memberOne, Relations.SIBLINGS))
    updateMemberConnections(memberOne)
    updateMemberConnections(memberTwo)
}

/**
 * Updates the adjacency list (connections) of a family member in Firestore.
 * This method retrieves the connections from the FamilyMember object, converts them
 * to a format suitable for Firestore storage, and updates the Firestore document
 * representing the member. It uses a map structure to store the related member's ID
 * and the relationship type.
 *
 * @param member The family member whose connections need to be updated.
 *
 * This function performs a merge operation, updating only the connections field
 * without overwriting other fields in the document.
 *
 * Success and failure of the operation are logged to the console.
 */
fun updateMemberConnections(member: FamilyMember) {
    val memberDocumentId = member.documentId

    // Transform connections into a list of maps for Firestore compatibility.
    val connectionsData = member.getConnections().map { connection ->
        mapOf(
            "relatedMemberId" to connection.member.documentId,
            "fullName" to connection.member.getFullName(),
            "relationship" to connection.relationship.name
        )
    }

    // Create a map for updating only the connections field in Firestore.
    val memberData = mapOf("connections" to connectionsData)

    // Update Firestore with the new connections data, using a merge to preserve other fields.
    db.collection("memberMap")
        .document(memberDocumentId)
        .set(memberData, SetOptions.merge())
        .addOnSuccessListener {
            println("Successfully updated connections for member: $memberDocumentId")
        }
        .addOnFailureListener { e ->
            println("Failed to update connections for member: $memberDocumentId. Error: ${e.message}")
        }
}