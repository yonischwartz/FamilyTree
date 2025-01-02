package com.example.familytree.data.exceptions

/**
 * Exception thrown when a family member is not found in the family tree.
 *
 * <p>This exception is triggered when an operation tries to reference a family member
 * who is not present in the family tree. This could happen when attempting to access
 * or modify the details of a member who has not been added to the tree structure.</p>
 */
class MemberNotInTreeException : Exception {

    /**
     * Default constructor for the exception.
     */
    constructor() : super()

    /**
     * Constructor that accepts a custom error message.
     *
     * @param message the custom error message for the exception.
     */
    constructor(message: String) : super(message)
}
