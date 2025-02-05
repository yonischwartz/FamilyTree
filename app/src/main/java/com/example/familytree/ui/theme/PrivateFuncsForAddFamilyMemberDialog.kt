package com.example.familytree.ui.theme

import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations





/**
 * Finds a matching family member in the provided list based on the first name, last name, and machzor.
 *
 * @param firstName The first name of the family member to search for.
 * @param lastName The last name of the family member to search for.
 * @param machzor The machzor of the family member to search for, or null if machzor is not applicable.
 * @param members A list of existing family members to search within.
 * @return The first matching family member if found, or null if no match is found.
 */
private fun findMatchingMember(
    firstName: String, lastName: String, machzor: Int?, members: List<FamilyMember>
): FamilyMember? {
    return members.find {
        it.getFirstName() == firstName && it.getLastName() == lastName && it.getMachzor() == machzor
    }
}


/**
 * Returns the inverse relationship based on the given relation and the genders of both members.
 *
 * @param relation The relationship to invert.
 * @param memberOneIsMale True if the first member is male, false if female.
 * @param memberTwoIsMale True if the second member is male, false if female.
 * @return The inverse relationship.
 */
private fun getInverseRelation(relation: Relations, memberOneIsMale: Boolean, memberTwoIsMale: Boolean): Relations {
    return when (relation) {
        Relations.FATHER -> if (memberOneIsMale) Relations.SON else Relations.DAUGHTER
        Relations.MOTHER -> if (memberOneIsMale) Relations.SON else Relations.DAUGHTER
        Relations.SON -> if (memberTwoIsMale) Relations.FATHER else Relations.MOTHER
        Relations.DAUGHTER -> if (memberTwoIsMale) Relations.FATHER else Relations.MOTHER
        Relations.GRANDMOTHER -> if (memberOneIsMale) Relations.GRANDSON else Relations.GRANDDAUGHTER
        Relations.GRANDFATHER -> if (memberOneIsMale) Relations.GRANDSON else Relations.GRANDDAUGHTER
        Relations.GRANDSON -> if (memberTwoIsMale) Relations.GRANDFATHER else Relations.GRANDMOTHER
        Relations.GRANDDAUGHTER -> if (memberTwoIsMale) Relations.GRANDFATHER else Relations.GRANDMOTHER
        Relations.SIBLINGS -> Relations.SIBLINGS
        Relations.COUSINS -> Relations.COUSINS
        Relations.MARRIAGE -> Relations.MARRIAGE
    }
}
