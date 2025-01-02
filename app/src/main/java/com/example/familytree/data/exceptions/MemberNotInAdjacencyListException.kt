package com.example.familytree.data.exceptions

import com.example.familytree.data.FamilyMember

/**
 * Exception thrown when attempting to reference a family member
 * who is not present in the adjacency list.
 */
class MemberNotInAdjacencyListException : Exception {

    /**
     * Default constructor for the exception.
     */
    constructor() : super()

    /**
     * Helper method to generate a custom error message.
     *
     * <p>This method is marked as {@code static} in Java because it does not rely on any
     * instance variables or behavior from the exception class. Instead, it operates
     * solely based on the input parameter ({@code memberNotInList}). This allows it
     * to be called without requiring an instance of the {@code MemberNotInAdjacencyListException} class.</p>
     *
     * @param memberNotInList the family member who is not in the adjacency list.
     * @return a descriptive error message.
     */
    private companion object {
        fun createMessage(memberNotInList: FamilyMember): String {
            return "The member ${memberNotInList.getFullName()} is not in the adjacency list."
        }
    }

    /**
     * Constructor that accepts a family member and generates a specific error message.
     *
     * @param memberNotInList the family member who is not in the adjacency list.
     */
    constructor(memberNotInList: FamilyMember) : super(createMessage(memberNotInList))

    /**
     * Constructor that accepts a custom error message.
     *
     * @param message the custom error message for the exception.
     */
    constructor(message: String) : super(message)
}
