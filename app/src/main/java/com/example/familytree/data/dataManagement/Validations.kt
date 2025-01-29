package com.example.familytree.data.dataManagement

import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.data.exceptions.InvalidGenderRoleException
import com.example.familytree.data.exceptions.InvalidMoreThanOneConnection
import com.example.familytree.data.exceptions.SameMarriageException

/**
 * Validates that the member's gender matches the expected gender for the specified relationship.
 *
 * @param member The family member whose gender is being validated.
 * @param relationship The relationship type to check against the member's gender.
 * @throws InvalidGenderRoleException If the member's gender does not match the expected gender for the relationship.
 */
internal fun FireBaseManager.validateGenderRole(member: FamilyMember, relationship: Relations) {
    val expectedGender = relationshipGenderMap[relationship]
    if (expectedGender != null && member.getGender() != expectedGender) {
        throw InvalidGenderRoleException(member, relationship)
    }
}

/**
 * Validates the marriage relationship between two family members.
 * A marriage cannot exist if either member is already married, and the genders must be different.
 *
 * @param memberOne The first family member.
 * @param memberTwo The second family member.
 * @throws SameMarriageException If the genders of the two members are the same.
 * @throws InvalidMoreThanOneConnection If the member already has an existing marriage connection.
 */
internal fun validateMarriage(memberOne: FamilyMember, memberTwo: FamilyMember) {
    validateNotMoreThanOneMemberConnection(memberOne, Relations.MARRIAGE)
    validateNotMoreThanOneMemberConnection(memberTwo, Relations.MARRIAGE)
    if (memberOne.getGender() == memberTwo.getGender()) {
        throw SameMarriageException()
    }
}

/**
 * Validates that a family member does not already have an existing connection of a specific relationship type.
 *
 * @param member The family member to check for existing connections.
 * @param relationship The type of relationship to check for.
 * @throws InvalidMoreThanOneConnection If the member already has an existing connection of the specified relationship.
 */
internal fun validateNotMoreThanOneMemberConnection(member: FamilyMember, relationship: Relations) {
    val connections = member.getConnections()
    for (connection in connections) {
        if (relationship == connection.relationship) {
            throw InvalidMoreThanOneConnection(relationship)
        }
    }
}

/**
 * Validates the parent-child connection in the family tree.
 *
 * This function ensures the following:
 * 1. The parent's gender matches the expected role based on the specified relationship (e.g., FATHER or MOTHER).
 * 2. The child does not have more than one parent of the specified relationship type (e.g., no more than one FATHER or MOTHER).
 *
 * @param child The child family member whose relationship to the parent is being validated.
 * @param parent The parent family member being validated.
 * @param parentRelation The relationship type between the parent and child (must be either FATHER or MOTHER).
 *
 * @throws InvalidGenderRoleException If the parent's gender does not match the specified relationship type.
 * @throws InvalidMoreThanOneConnection If the child already has a parent of the specified relationship type.
 */
internal fun FireBaseManager.validateParentChildConnection(
    child: FamilyMember,
    parent: FamilyMember,
    parentRelation: Relations // FATHER or MOTHER
) {
    validateGenderRole(parent, parentRelation)
    validateNotMoreThanOneMemberConnection(child, parentRelation)
}

/**
 * Validates the child-parent connection in the family tree.
 *
 * This function ensures the following:
 * 1. The child's gender matches the expected role based on the specified relationship (e.g., SON or DAUGHTER).
 * 2. The child does not have more than one parent of the determined relationship type (e.g., no duplicate FATHER or MOTHER).
 *
 * The parent's role (FATHER or MOTHER) is determined dynamically based on the parent's gender.
 *
 * @param child The child family member being validated.
 * @param parent The parent family member whose relationship to the child is being validated.
 * @param childRelation The relationship type between the child and parent (must be either SON or DAUGHTER).
 *
 * @throws InvalidGenderRoleException If the child's gender does not match the specified relationship type.
 * @throws InvalidMoreThanOneConnection If the child already has a parent of the determined relationship type (FATHER or MOTHER).
 */
internal fun FireBaseManager.validateChildParentConnection(
    parent: FamilyMember,
    child: FamilyMember,
    childRelation: Relations // SON or DAUGHTER
) {
    val parentRelation = if (parent.getGender()) Relations.FATHER else Relations.MOTHER
    validateGenderRole(child, childRelation)
    validateNotMoreThanOneMemberConnection(child, parentRelation)
}

/**
 * Validates the grandparent-grandchild connection in the family tree.
 *
 * This function ensures the following:
 * 1. The grandparent's gender matches the expected role based on the specified relationship
 *    (e.g., GRANDFATHER or GRANDMOTHER).
 *
 * @param grandparent The grandparent family member being validated.
 * @param grandparentRelation The relationship type of the grandparent (must be either GRANDFATHER or GRANDMOTHER).
 *
 * @throws InvalidGenderRoleException If the grandparent's gender does not match the specified relationship type.
 */
internal fun FireBaseManager.validateGrandparentGrandchildConnection(
    grandparent: FamilyMember,
    grandparentRelation: Relations
) {
    validateGenderRole(grandparent, grandparentRelation)
}

/**
 * Validates the grandchild-grandparent connection in the family tree.
 *
 * @param grandchild The grandchild family member being validated.
 * @param grandchildRelation The relationship type of the grandchild (must be either GRANDSON or GRANDDAUGHTER).
 *
 * @throws InvalidGenderRoleException If the grandchild's gender does not match the specified relationship type.
 */
internal fun FireBaseManager.validateGrandchildGrandparentConnection(
    grandchild: FamilyMember,
    grandchildRelation: Relations
) {
    validateGenderRole(grandchild, grandchildRelation)
}
