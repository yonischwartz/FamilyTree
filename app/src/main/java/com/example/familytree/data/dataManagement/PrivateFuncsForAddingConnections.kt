package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.data.exceptions.*

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
    if (!adjacencyList.containsKey(memberOne.documentId)) {
        throw MemberNotInAdjacencyListException(memberOne)
    }
    if (!adjacencyList.containsKey(memberTwo.documentId)) {
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
    val connections = adjacencyList[member.documentId] ?: return
    for (connection in connections) {
        if (relationship == connection.relationship) {
            throw InvalidRelationshipException(member, relationship)
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
    adjacencyList[memberTwo.documentId]?.add(Connection(memberOne, Relations.MARRIAGE))
    adjacencyList[memberOne.documentId]?.add(Connection(memberTwo, Relations.MARRIAGE))
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
    adjacencyList[child.documentId]?.add(Connection(parent, parentRelation))
    adjacencyList[parent.documentId]?.add(Connection(child, childRelation))
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
    adjacencyList[child.documentId]?.add(Connection(parent, parentRelation))
    adjacencyList[parent.documentId]?.add(Connection(child, childRelation))
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
    adjacencyList[grandchild.documentId]?.add(Connection(grandparent, grandparentRelation))
    adjacencyList[grandparent.documentId]?.add(Connection(grandchild, grandchildRelation))
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
    adjacencyList[grandchild.documentId]?.add(Connection(grandparent, grandparentRelation))
    adjacencyList[grandparent.documentId]?.add(Connection(grandchild, grandchildRelation))
}

/**
 * Adds a cousins connection between two family members.
 * This is a symmetric relationship where both members are connected as cousins.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 */
internal fun FamilyTreeData.addCousinsConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
    adjacencyList[memberOne.documentId]?.add(Connection(memberTwo, Relations.COUSINS))
    adjacencyList[memberTwo.documentId]?.add(Connection(memberOne, Relations.COUSINS))
}

/**
 * Adds a sibling connection between two family members.
 * This is a symmetric relationship where both members are connected as siblings.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 */
internal fun FamilyTreeData.addSiblingConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
    adjacencyList[memberOne.documentId]?.add(Connection(memberTwo, Relations.SIBLINGS))
    adjacencyList[memberTwo.documentId]?.add(Connection(memberOne, Relations.SIBLINGS))
}
