package com.yoniSchwartz.YBMTree.data.exceptions

import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.Relations

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
    invalidRole: Relations
) : Exception(
    "Invalid gender-role assignment: Member '${member.getFullName()}' (gender: ${
        if (member.getGender()) "Male" else "Female"
    }) cannot be assigned role '$invalidRole'."
)
