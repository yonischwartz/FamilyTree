package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.data.exceptions.InvalidGenderRoleException
import com.example.familytree.data.exceptions.InvalidMoreThanOneConnection
import com.example.familytree.data.exceptions.SameMarriageException

/**
 * Object that manages a collection of FamilyMember instances locally.
 * Provides methods to add, retrieve, update, and delete family members.
 */
object MemberMap {

    // Stores family members using their unique ID as the key.
    private val members = mutableMapOf<String, FamilyMember>()

    // Functions

    /**
     * Adds a new family member to the map. If a member with the
     * same ID already exists, it will be overwritten.
     * @param member The FamilyMember object to be added or updated.
     */
    fun addMember(member: FamilyMember) {
        members[member.getId()] = member
    }

    /**
     * Retrieves a family member by their unique ID.
     * @param memberId The ID of the family member.
     * @return The FamilyMember object if found, otherwise null.
     */
    fun getMember(memberId: String): FamilyMember? {
        return members[memberId]
    }

    /**
     * Deletes a family member from the map and removes them from all other members' connections.
     *
     * This method removes the specified member from the `members` map and ensures that any connections
     * involving the deleted member are also removed from all other family members' connection lists.
     * The IDs of members whose connections were modified (i.e., the ones that had a connection to the deleted member)
     * are returned in a list.
     *
     * @param memberToBeRemovedId The ID of the member to remove from the map and the connections.
     * @return A list of member IDs whose connections were updated. If no connections were modified,
     *         this list will be empty. If the specified member does not exist in the map, the list will also be empty.
     */
    fun deleteMember(memberToBeRemovedId: String): List<String> {
        // Create a list to hold the IDs of members whose connections were updated
        val updatedMembers = mutableListOf<String>()

        // Remove the member from the map
        members.remove(memberToBeRemovedId)

        // Iterate over all remaining members and remove the deleted member from their connections
        for (member in members.values) {
            // Check if the member has a connection to the deleted member
            val connectionsToRemove = member.getConnections().filter { it.memberId == memberToBeRemovedId }

            if (connectionsToRemove.isNotEmpty()) {
                // Remove the connections
                member.getConnections().removeAll { it.memberId == memberToBeRemovedId }

                // Add this member's ID to the list of updated members
                updatedMembers.add(member.getId())
            }
        }

        // Return the list of member IDs whose connections were updated
        return updatedMembers
    }

    /**
     * Retrieves a list of all stored family members.
     * @return A list of all FamilyMember objects.
     */
    fun getAllMembers(): List<FamilyMember> {
        return members.values.toList()
    }

    /**
     * Adds a connection to a family member's connections list.
     *
     * @param memberId The ID of the family member to whom the connection will be added.
     * @param connectionToBeAdded The connection to add to the family member's connections list.
     */
    private fun addConnectionToSingleMember(memberId: String, connectionToBeAdded: Connection) {
        members[memberId]?.addConnection(connectionToBeAdded)
    }

    /**
     * Adds a connection between two family members based on the relationship type.
     *
     * @param memberOne The first family member.
     * @param memberTwo The second family member.
     * @param relationFromMemberOnePerspective The relationship type from memberOne's perspective.
     * @return A Boolean value indicating whether the operation was successful.
     */
    fun addConnectionToBothMembers(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations,
    ): Boolean {

        when (relationFromMemberOnePerspective) {

            Relations.MARRIAGE -> {

                addMutualConnection(memberOne, memberTwo, Relations.MARRIAGE)

                // If one of the members is a rabbi, his wife should be updated as a rabbi wife
                if (memberOne.getIsRabbi()) {
                    memberTwo.setIsRabbi(true)
                }
                if (memberTwo.getIsRabbi()) {
                    memberOne.setIsRabbi(true)
                }
            }

            Relations.SIBLINGS -> {

                addMutualConnection(memberOne, memberTwo, relationFromMemberOnePerspective)

                // Add mutual connections from one sibling to another
                addConnectionsFromOneSiblingToAnother(memberOne, memberTwo)
                addConnectionsFromOneSiblingToAnother(memberTwo, memberOne)
            }

            Relations.COUSINS ->
                addMutualConnection(memberOne, memberTwo, relationFromMemberOnePerspective)

            Relations.FATHER, Relations.MOTHER ->
                addChildParentConnection(memberOne, memberTwo)

            Relations.SON, Relations.DAUGHTER ->
                addChildParentConnection(memberTwo, memberOne)

            Relations.GRANDMOTHER, Relations.GRANDFATHER ->
                addGrandchildGrandparentConnection(memberOne, memberTwo)

            Relations.GRANDSON, Relations.GRANDDAUGHTER ->
                addGrandchildGrandparentConnection(memberTwo, memberOne)
        }

        return true
    }

    /**
     * Validates the connection between two family members
     *
     * This function checks various types of relationships (e.g., marriage, parent-child, etc.) and
     * ensures that the connection adheres to specific rules based on gender and the number of connections.
     *
     * @param memberOne The first family member involved in the relationship.
     * @param memberTwo The second family member involved in the relationship.
     * @param relationFromMemberOnePerspective The relationship type as seen from member one's perspective.
     * @return True if the connection is valid; if the connection fails validation, exceptions are thrown.
     */
    fun validateConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations,
    ): Boolean {

        when (relationFromMemberOnePerspective) {

            Relations.MARRIAGE -> validateMarriage(memberOne, memberTwo)
            Relations.FATHER -> validateParentChildConnection(memberOne, memberTwo, Relations.FATHER)
            Relations.MOTHER -> validateParentChildConnection(memberOne, memberTwo, Relations.MOTHER)
            Relations.SON -> validateChildParentConnection(memberTwo, memberOne, Relations.SON)
            Relations.DAUGHTER -> validateChildParentConnection(memberTwo, memberOne, Relations.DAUGHTER)
            Relations.GRANDMOTHER -> validateGrandparentGrandchildConnection(memberTwo, Relations.GRANDMOTHER)
            Relations.GRANDFATHER -> validateGrandparentGrandchildConnection(memberTwo, Relations.GRANDFATHER)
            Relations.GRANDDAUGHTER -> validateGrandchildGrandparentConnection(memberTwo, Relations.GRANDDAUGHTER)
            Relations.GRANDSON -> validateGrandchildGrandparentConnection(memberTwo, Relations.GRANDSON)
            Relations.SIBLINGS -> Unit // No validation required for siblings
            Relations.COUSINS -> Unit // No validation required for cousins

        }
        return true
    }

    /**
     * Searches for family members whose full name matches or contains the provided search term.
     *
     * This function filters the list of all members retrieved from Firestore based on the search term.
     * The search is case-insensitive and matches full names or substrings of names.
     *
     * @param searchTerm The string representing either a full name or a substring of a name to search for.
     * @return A list of FamilyMember objects whose names match or contain the search term.
     */
    fun searchForMember(searchTerm: String): List<FamilyMember> {
        val normalizedSearchTerm = searchTerm.lowercase()
        return members.values.filter {
            it.getFullName().lowercase().contains(normalizedSearchTerm)
        }
    }

    /**
     * Clears all family members from the local storage.
     */
    fun clearMemberMap() {
        members.clear()
    }

    private fun findPossibleConnections(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations
    ) {

        when (relationFromMemberOnePerspective) {

            Relations.MARRIAGE -> Unit
            Relations.FATHER -> Unit
            Relations.MOTHER -> Unit
            Relations.SON -> Unit
            Relations.DAUGHTER -> Unit
            Relations.GRANDMOTHER -> Unit
            Relations.GRANDFATHER -> Unit
            Relations.GRANDDAUGHTER -> Unit
            Relations.GRANDSON -> Unit
            Relations.SIBLINGS -> Unit
            Relations.COUSINS -> Unit
        }
    }

    // private functions for validation

    /**
     * Validates the gender role of a family member based on the expected gender for the specified relationship.
     *
     * This function checks if the gender of the member matches the expected gender for the given relationship.
     * If there is a mismatch, it throws an `InvalidGenderRoleException`.
     *
     * @param member The family member whose gender is being validated.
     * @param relationship The type of relationship that dictates the expected gender.
     * @throws InvalidGenderRoleException If the gender of the member does not match the expected gender for the relationship.
     */
    private fun validateGenderRole(member: FamilyMember, relationship: Relations) {
        val expectedGender = relationship.expectedGender()
        if (expectedGender != null && member.getGender() != expectedGender) {
            throw InvalidGenderRoleException(member, relationship)
        }
    }

    /**
     * Validates that a family member does not have more than one connection for a specific relationship.
     *
     * This function checks if the member already has an existing connection of the same type (e.g., one marriage).
     * If the member has more than one connection for the same relationship, an exception is thrown.
     *
     * @param member The family member whose connections are being validated.
     * @param relationship The type of relationship to check for duplicate connections.
     * @throws InvalidMoreThanOneConnection If the member already has a connection of the specified relationship type.
     */
    private fun validateNotMoreThanOneMemberConnection(member: FamilyMember, relationship: Relations) {
        val connections = member.getConnections()
        for (connection in connections) {
            if (relationship == connection.relationship) {
                throw InvalidMoreThanOneConnection(relationship)
            }
        }
    }

    /**
     * Validates a marriage connection between two family members.
     *
     * This function ensures that neither member already has an existing marriage connection,
     * and it also checks that the members are not of the same gender.
     *
     * @param memberOne The first family member in the marriage.
     * @param memberTwo The second family member in the marriage.
     * @throws SameMarriageException If the two members are of the same gender.
     */
    private fun validateMarriage(memberOne: FamilyMember, memberTwo: FamilyMember) {
        validateNotMoreThanOneMemberConnection(memberOne, Relations.MARRIAGE)
        validateNotMoreThanOneMemberConnection(memberTwo, Relations.MARRIAGE)
        if (memberOne.getGender() == memberTwo.getGender()) {
            throw SameMarriageException()
        }
    }

    /**
     * Validates a parent-child connection between two family members (either father-child or mother-child).
     *
     * This function checks that the parent has the correct gender for the relationship type and that the child
     * does not have more than one connection of the same relationship type.
     *
     * @param child The child family member.
     * @param parent The parent family member.
     * @param parentRelation The type of relationship for the parent (either FATHER or MOTHER).
     */
    private fun validateParentChildConnection(
        child: FamilyMember,
        parent: FamilyMember,
        parentRelation: Relations // FATHER or MOTHER
    ) {
        validateGenderRole(parent, parentRelation)
        validateNotMoreThanOneMemberConnection(child, parentRelation)
    }

    /**
     * Validates a child-parent connection between two family members (either son-father or daughter-mother).
     *
     * This function ensures that the child has the correct gender for the relationship and that the parent does not
     * have more than one connection of the same relationship type.
     *
     * @param child The child family member.
     * @param parent The parent family member.
     * @param childRelation The type of relationship for the child (either SON or DAUGHTER).
     */
    private fun validateChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember,
        childRelation: Relations // SON or DAUGHTER
    ) {
        val parentRelation = if (parent.getGender()) Relations.FATHER else Relations.MOTHER
        validateGenderRole(child, childRelation)
        validateNotMoreThanOneMemberConnection(child, parentRelation)
    }

    /**
     * Validates a grandparent-grandchild connection.
     *
     * This function ensures that the grandparent has the correct gender for the relationship (grandmother or grandfather).
     *
     * @param grandparent The grandparent family member.
     * @param grandparentRelation The relationship type for the grandparent (either GRANDMOTHER or GRANDFATHER).
     */
    private fun validateGrandparentGrandchildConnection(
        grandparent: FamilyMember,
        grandparentRelation: Relations
    ) {
        validateGenderRole(grandparent, grandparentRelation)
    }

    /**
     * Validates a grandchild-grandparent connection.
     *
     * This function ensures that the grandchild has the correct gender for the relationship (granddaughter or grandson).
     *
     * @param grandchild The grandchild family member.
     * @param grandchildRelation The relationship type for the grandchild (either GRANDDAUGHTER or GRANDSON).
     */
    private fun validateGrandchildGrandparentConnection(
        grandchild: FamilyMember,
        grandchildRelation: Relations
    ) {
        validateGenderRole(grandchild, grandchildRelation)
    }

    // Private functions for adding member

    /**
     * Adds a mutual connection between two family members to their respective connection lists.
     * This function is specifically used for relationships such as **Marriage**, **Cousins**, and **Siblings**.
     *
     * @param memberOne The first family member.
     * @param memberTwo The second family member.
     * @param relation The relationship type between the two members (Marriage, Cousins, or Siblings).
     */
    private fun addMutualConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relation: Relations
    ) {

        // Add mutual connection to member one
        addConnectionToSingleMember(memberOne.getId(), Connection(memberTwo.getId(), relation))

        // Add mutual connection to member two
        addConnectionToSingleMember(memberTwo.getId(), Connection(memberOne.getId(), relation))
    }

    /**
     * Adds a child-parent connection between two family members.
     *
     * @param child The child family member.
     * @param parent The parent family member.
     */
    private fun addChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember,
    ) {

        // Get child connection (SON or DAUGHTER) based on child's gender
        val childConnection =
            if (child.getGender()) Relations.SON else
                Relations.DAUGHTER

        // Get parent connection (FATHER or MOTHER) based on parent's gender
        val parentConnection =
            if (parent.getGender()) Relations.FATHER
            else Relations.MOTHER

        // Add parent connection to child
        addConnectionToSingleMember(child.getId(), Connection(parent.getId(), parentConnection))

        // Add child connection to parent
        addConnectionToSingleMember(parent.getId(), Connection(child.getId(), childConnection))
    }

    /**
     * Adds a grandchild-grandparent connection between two family members.
     *
     * @param grandchild The grandchild family member.
     * @param grandparent The grandparent family member.
     */
    private fun addGrandchildGrandparentConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember
    ) {

        // Get grandchild connection (SON or DAUGHTER) based on grandchild's gender
        val childConnection =
            if (grandchild.getGender()) Relations.GRANDSON
            else Relations.GRANDDAUGHTER

        // Get grandparent connection (FATHER or MOTHER) based on grandparent's gender
        val parentConnection =
            if (grandparent.getGender()) Relations.GRANDFATHER
            else Relations.GRANDMOTHER

        // Add grandparent connection to child
        addConnectionToSingleMember(grandchild.getId(), Connection(grandparent.getId(), parentConnection))

        // Add grandchild connection to parent
        addConnectionToSingleMember(grandparent.getId(), Connection(grandchild.getId(), childConnection))
    }

    // Private functions for finding possible connections

    private fun findPossibleConnectionsForMarriageConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
    ): List<Connection> {

        val possibleConnectionsTwo = mutableListOf<Connection>()

        for (connection in memberOne.getConnections()) {

            // If memberOne and memberTwo are married, they might have mutual children
            if (connection.relationship == Relations.SON ||
                connection.relationship == Relations.DAUGHTER) {

                // Check if memberTwo already has this connection
                if (!memberTwo.getConnections().contains(connection)) {
                    possibleConnectionsTwo.add(connection)
                }
            }

            // If memberOne and memberTwo are married, they might have mutual grandchildren
            if (connection.relationship == Relations.GRANDSON ||
                connection.relationship == Relations.GRANDDAUGHTER) {

                // Check if memberTwo already has this connection
                if (!memberTwo.getConnections().contains(connection)) {
                    possibleConnectionsTwo.add(connection)
                }
            }
        }

        return possibleConnectionsTwo
    }

    // Private functions for adding connections based on the connection the user added

    /**
     * Adds connections from one sibling to another, ensuring mutual relationships are established.
     *
     * This function iterates through all connections of `memberOne` and adds them to `memberTwo`,
     * except for non-mutual relationships like marriage, parent-child, and grandparent-grandchild.
     * It ensures that duplicate connections are not added.
     *
     * @param memberOne The source family member whose connections are to be shared.
     * @param memberTwo The target family member who will receive the shared connections.
     */
    private fun addConnectionsFromOneSiblingToAnother(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        for (connection in memberOne.getConnections()) {

            // These relations are not mutual for siblings
            if (connection.relationship == Relations.MARRIAGE ||
                connection.relationship == Relations.SON ||
                connection.relationship == Relations.DAUGHTER ||
                connection.relationship == Relations.GRANDSON ||
                connection.relationship == Relations.GRANDDAUGHTER
            ) {
                continue
            }

            // Skip the sibling connection to member two
            if (connection.memberId == memberTwo.getId()) {
                continue
            }

            // Check if memberTwo already has this connection
            val alreadyConnected = memberTwo.getConnections().any {
                it.memberId == connection.memberId && it.relationship == connection.relationship
            }

            // Add the connection to memberTwo
            if (!alreadyConnected) {
                val connectionToBeAdded = Connection(connection.memberId, connection.relationship)
                memberTwo.addConnection(connectionToBeAdded)
            }
        }
    }





}
