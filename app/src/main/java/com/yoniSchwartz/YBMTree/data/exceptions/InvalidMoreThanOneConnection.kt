package com.yoniSchwartz.YBMTree.data.exceptions

import com.yoniSchwartz.YBMTree.data.Relations

/**
 * Exception thrown when a family member is found to have more than one instance of a specific relationship
 * that should be unique, such as marriage or parent-child relationships.
 *
 * This ensures the integrity of the family tree by preventing invalid relationships like multiple marriages
 * or having more than one father.
 */
class InvalidMoreThanOneConnection : Exception {

    /**
     * Constructs an {@code InvalidRelationshipException} with a default message.
     */
    constructor() : super("A family member can not have more than one of this connection.")

    /**
     * Constructs an {@code InvalidMoreThanOneConnection} with a specific relation in the message.
     *
     * @param relation The specific relationship type that caused the exception, such as "MARRIAGE" or "FATHER."
     */
    constructor(relation: Relations) :
            super("A family member cannot have more than one '${relation.name.lowercase()}' connection.")
}


