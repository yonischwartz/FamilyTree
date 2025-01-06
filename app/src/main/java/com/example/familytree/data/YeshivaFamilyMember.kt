package com.example.familytree.data

/**
 * Represents a family member associated with a yeshiva,
 * which can include students, rabbis, and staff members.
 *
 * @param firstName The first name of the family member.
 * @param lastName The last name of the family member.
 * @param gender The gender of the family member (true for male, false for female).
 * @param machzor The machzor (academic cycle) the student belongs to, or 0 for non-students.
 * @param rabbi A boolean indicating whether the person is a rabbi.
 */
class YeshivaFamilyMember(
    firstName: String,
    lastName: String,
    gender: Boolean,
    private val machzor: Int = 0,  // Default to 0 for non-students
    private val rabbi: Boolean
) : FamilyMember(firstName, lastName, gender) {

    /**
     * Secondary constructor for creating a YeshivaFamilyMember with no machzor,
     * useful for rabbis or staff who did not study at the yeshiva.
     */
    constructor(firstName: String, lastName: String, gender: Boolean, rabbi: Boolean) : this(
        firstName, lastName, gender, 0, rabbi
    )
}
