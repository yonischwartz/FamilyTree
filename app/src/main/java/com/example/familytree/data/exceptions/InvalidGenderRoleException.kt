package com.example.familytree.data.exceptions

import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Realations

/**
 * Exception thrown when a family member is assigned a relationship role that contradicts their gender.
 *
 * <p>This exception is triggered when an invalid relationship role is attempted to be established, such as:
 * <ul>
 *     <li>Assigning a female as a "SON".</li>
 *     <li>Assigning a male as a "MOTHER".</li>
 *     <li>Other similar gender-role mismatches.</li>
 * </ul>
 * </p>
 */
class InvalidGenderRoleException(
    val member: FamilyMember,
    val invalidRole: Realations
) : Exception(
    "Invalid gender-role assignment: Member '${member.getFullName()}' (gender: ${
        if (member.gender) "Male" else "Female"
    }) cannot be assigned role '$invalidRole'."
)
