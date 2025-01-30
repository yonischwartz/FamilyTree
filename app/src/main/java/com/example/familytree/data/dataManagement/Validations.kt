package com.example.familytree.data.dataManagement

import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.FireBaseManager.getConnectionsOfFamilyMemberById
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
 * Validates that a family member does not already have an existing connection of a specific relationship type.
 *
 * @param memberId The ID of the family member to check for existing connections.
 * @param relationship The type of relationship to check for.
 * @throws InvalidMoreThanOneConnection If the member already has an existing connection of the specified relationship.
 */
internal suspend fun validateNotMoreThanOneMemberConnection(memberId: String, relationship: Relations) {
    val connections = getConnectionsOfFamilyMemberById(memberId)
    for (connection in connections) {
        if (relationship == connection.relationship) {
            throw InvalidMoreThanOneConnection(relationship)
        }
    }
}

/**
 * Validates the marriage relationship between two family members.
 * The validation checks that:
 * 1. Neither member is already married.
 * 2. The genders of the two members are different.
 *
 * @param memberOneId The ID of the first family member.
 * @param memberOneGender The gender of the first family member (true for male, false for female).
 * @param memberTwoId The ID of the second family member.
 * @param memberTwoGender The gender of the second family member (true for male, false for female).
 * @throws SameMarriageException If the genders of the two members are the same.
 * @throws InvalidMoreThanOneConnection If either member already has an existing marriage connection.
 */
internal suspend fun validateMarriage(
    memberOneId: String,
    memberOneGender: Boolean,
    memberTwoId: String,
    memberTwoGender: Boolean
) {
    validateNotMoreThanOneMemberConnection(memberOneId, Relations.MARRIAGE)
    validateNotMoreThanOneMemberConnection(memberTwoId, Relations.MARRIAGE)
    if (memberOneGender == memberTwoGender) {
        throw SameMarriageException()
    }
}

/**
 * Validates the parent-child connection in the family tree.
 *
 * This function ensures the following:
 * 1. The parent's role matches the expected relationship type (e.g., FATHER).
 * 2. The child does not already have more than one parent of the specified relationship type.
 *
 * @param childId The ID of the child family member whose relationship to the parent is being validated.
 * @param parent The parent family member being validated.
 * @param parentRelation The relationship type between the parent and child (must be FATHER).
 *
 * @throws InvalidMoreThanOneConnection If the child already has a FATHER in the family tree.
 */
internal suspend fun FireBaseManager.validateParentChildConnection(
    childId: String,
    parent: FamilyMember,
    parentRelation: Relations // FATHER or MOTHER
) {
    validateGenderRole(parent, parentRelation)
    validateNotMoreThanOneMemberConnection(childId, parentRelation)
}

/**
 * Validates the child-parent connection in the family tree.
 *
 * This function ensures the following:
 * 1. The child's assigned relationship (SON or DAUGHTER) aligns with their expected gender.
 * 2. The child does not already have more than one parent of the determined type (FATHER or MOTHER).
 *
 * The parent's role (FATHER or MOTHER) is determined based on the provided `parentGender` flag.
 *
 * @param parentGender Boolean indicating whether the parent is male (`true` for FATHER, `false` for MOTHER).
 * @param child The child family member whose relationship is being validated.
 * @param childId The unique ID of the child in the database.
 * @param childRelation The relationship type between the child and parent (must be either SON or DAUGHTER).
 *
 * @throws InvalidGenderRoleException If the child's gender does not match the specified relationship type (SON or DAUGHTER).
 * @throws InvalidMoreThanOneConnection If the child already has a parent of the determined relationship type (FATHER or MOTHER).
 */
internal suspend fun FireBaseManager.validateChildParentConnection(
    parentGender: Boolean,
    child: FamilyMember,
    childId: String,
    childRelation: Relations // SON or DAUGHTER
) {
    val parentRelation = if (parentGender) Relations.FATHER else Relations.MOTHER
    validateGenderRole(child, childRelation)
    validateNotMoreThanOneMemberConnection(childId, parentRelation)
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
