package com.yoniSchwartz.YBMTree.data.exceptions

import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.Relations

/**
 * Exception thrown when a family member is assigned a relationship
 * that would result in an invalid or contradictory connection.
 *
 * <p>Examples of invalid connections include:</p>
 * <ul>
 *     <li>Assigning a second father or mother to the same member.</li>
 *     <li>Adding a second spouse when one already exists.</li>
 * </ul>
 */
class InvalidRelationshipException : Exception {

    /**
     * Constructs an {@code InvalidRelationshipException} with a default message.
     */
    constructor() : super("Invalid relationship: A family member cannot have conflicting or duplicate relationships.")

    /**
     * Constructs an {@code InvalidRelationshipException} with a specific message.
     *
     * @param message the specific error message.
     */
    constructor(message: String) : super(message)

    /**
     * Constructs an {@code InvalidRelationshipException} with a message that includes details
     * about the member and the attempted relationship.
     *
     * @param familyMember the family member for whom the invalid relationship was attempted.
     * @param relation     the conflicting or duplicate relationship that caused the error.
     */
    constructor(familyMember: FamilyMember, relation: Relations) : super(
        "Invalid relationship for member ${familyMember.getFullName()}: Cannot add relationship '$relation' because it conflicts with an existing relationship."
    )
}
