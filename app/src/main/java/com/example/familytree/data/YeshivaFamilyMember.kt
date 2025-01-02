package com.example.familytree.data

/**
 * Represents a family member who is or was associated with the yeshiva,
 * including students, rabbis, and staff.
 *
 * @property machzor The year the family member joined the yeshiva.
 *                   Default is 0 if the member didn't join or isn't a student.
 */
open class YeshivaFamilyMember(
    firstName: String,
    lastName: String,
    gender: Boolean,     // Family member's gender (true = male, false = female)
    val machzor: Int
) : FamilyMember(firstName, lastName, gender) {
    /**
     * Secondary constructor for cases where the member doesn't have a machzor,
     * such as a rabbi who wasn't a student in the yeshiva.
     */
    constructor(firstName: String, lastName: String, gender: Boolean) : this(firstName, lastName, gender, 0)
}
