package com.example.familytree.data.exceptions

/**
 * Exception thrown when an attempt is made to validate or create a marriage
 * relationship between two family members of the same gender.
 *
 * This exception ensures that a valid marriage relationship must have two members
 * of different genders as per the app's constraints.
 */
class SameSexMarriageException : Exception("Marriage between two members of the same gender is not allowed.")
