package com.example.familytree.data.dataManagement

import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FullConnection
import com.example.familytree.data.Relations
import com.example.familytree.data.exceptions.InvalidGenderRoleException
import com.example.familytree.data.exceptions.InvalidMoreThanOneConnection
import com.example.familytree.data.exceptions.SameMarriageException
import java.util.LinkedList
import java.util.Queue

/**
 * Object that manages a collection of FamilyMember instances locally.
 * Provides methods to add, retrieve, update, and delete family members.
 */
object MemberMap {

    // Stores family members using their unique ID as the key.
    private val members = mutableMapOf<String, FamilyMember>()

    // Set that holds the ids of the new added members and the updated members
    private val modifiedAndNewAddedMembersIds = mutableSetOf<String>()

    // Set that holds the ids of the members the user asked to remove
    private val deletedMembersIds = mutableSetOf<String>()

    // Set that holds suggested connection to offer the user to add to member map
    private val QueueOfSuggestedConnections: Queue<FullConnection> = LinkedList()

    // Functions

    /**
     * Adds a new family member to the map. If a member with the
     * same ID already exists, it will be overwritten.
     * @param member The FamilyMember object to be added or updated.
     */
    internal fun addMember(member: FamilyMember) {

        // Add member to memberMap
        members[member.getId()] = member

        // Add member's id to list of members who were modified
        modifiedAndNewAddedMembersIds.add(member.getId())
    }

    /**
     * Retrieves a family member by their unique ID.
     * @param memberId The ID of the family member.
     * @return The FamilyMember object if found, otherwise null.
     */
    internal fun getMember(memberId: String): FamilyMember? {
        return members[memberId]
    }

    /**
     * Deletes a member from the members map and updates related connections.
     *
     * This function:
     * 1. Removes the member from the `members` map.
     * 2. Adds the member's ID to `deletedMembersIds`.
     * 3. Iterates through all remaining members and removes any connections to the deleted member.
     * 4. If a member's connections are updated, their ID is added to `modifiedAndNewAddedMembersIds`.
     *
     * @param memberToBeRemovedId The ID of the member to be deleted.
     */
    internal fun deleteMember(memberToBeRemovedId: String) {

        // Remove the member from the map
        members.remove(memberToBeRemovedId)

        // Add member's ID to the list of deleted members
        deletedMembersIds.add(memberToBeRemovedId)

        // Iterate over all remaining members and remove the deleted member from their connections
        for (member in members.values) {
            // Check if the member has a connection to the deleted member
            val connectionToRemove = member.getConnections().filter { it.memberId == memberToBeRemovedId }

            if (connectionToRemove.isNotEmpty()) {
                // Remove the connections
                member.getConnections().removeAll { it.memberId == memberToBeRemovedId }

                // Add this member's ID to the list of modifiedAndNewAddedMembersIds
                modifiedAndNewAddedMembersIds.add(member.getId())
            }
        }
    }

    /**
     * Retrieves a list of all stored family members.
     * @return A list of all FamilyMember objects.
     */
    internal fun getAllMembers(): List<FamilyMember> {
        return members.values.toList()
    }

    /**
     * Adds a connection between two family members, ensuring the relationship
     * is bidirectional where applicable. After adding the connection to
     * both members, their id's are added to modifiedAndNewAddedMembersIds list
     *
     * @param memberOne The first family member.
     * @param memberTwo The second family member.
     * @param relationFromMemberOnePerspective The relationship from the perspective of memberOne.
     */
    internal fun addConnectionToBothMembers(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations,
    ) {

        when (relationFromMemberOnePerspective) {

            Relations.MARRIAGE ->
                addMarriageConnection(memberOne, memberTwo)

            Relations.SIBLINGS ->
                addSiblingsConnection(memberOne, memberTwo)

            Relations.COUSINS ->
                addCousinsConnection(memberOne, memberTwo)

            Relations.FATHER, Relations.MOTHER ->
                addParentConnection(memberOne, memberTwo, relationFromMemberOnePerspective)

            Relations.SON, Relations.DAUGHTER ->
                addChildConnection(memberOne, memberTwo)

            Relations.GRANDMOTHER, Relations.GRANDFATHER ->
                addGrandparentConnection(memberOne, memberTwo, relationFromMemberOnePerspective)

            Relations.GRANDSON, Relations.GRANDDAUGHTER -> {
                addGrandchildConnection(memberOne, memberTwo)
            }
        }
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
    internal fun validateConnection(
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
    internal fun searchForMember(searchTerm: String): List<FamilyMember> {
        val normalizedSearchTerm = searchTerm.lowercase()
        return members.values.filter {
            it.getFullName().lowercase().contains(normalizedSearchTerm)
        }
    }

    /**
     * Retrieves the set of IDs of newly added or modified members.
     *
     * @return A set of strings representing the member IDs.
     */
    internal fun getModifiedAndNewAddedMembersIds(): Set<String> {
        return modifiedAndNewAddedMembersIds
    }

    /**
     * Retrieves the set of IDs of members that the user has requested to remove.
     *
     * @return A set of strings representing the deleted member IDs.
     */
    internal fun getDeletedMembersIds(): Set<String> {
        return deletedMembersIds
    }

    /**
     * Clears the lists of newly added, modified, and deleted member IDs.
     * This function should be called after the Firebase database is successfully updated,
     * as there is no longer a need to keep track of these changes.
     */
    internal fun clearAllTrackedChanges() {
        modifiedAndNewAddedMembersIds.clear()
        deletedMembersIds.clear()
    }

    /**
     * Retrieves and removes the next suggested connection from the queue.
     *
     * @return The next `FullConnection` from the queue, or `null` if the queue is empty.
     */
    internal fun popNextSuggestedConnection(): FullConnection? {
        return QueueOfSuggestedConnections.poll()
    }

    /**
     * Retrieves without removing the next suggested connection from the queue.
     *
     * @return The next `FullConnection` from the queue, or `null` if the queue is empty.
     */
    internal fun getNextSuggestedConnection(): FullConnection? {
        return QueueOfSuggestedConnections.peek()
    }

    /**
     * Checks if the queue of suggested connections is empty.
     *
     * @return `false` if the queue is empty, `true` otherwise.
     */
    internal fun isQueueOfSuggestedConnectionsNotEmpty(): Boolean {
        return QueueOfSuggestedConnections.isNotEmpty()
    }

    /**
     * Clears all family members from the local storage.
     */
    internal fun clearMemberMap() {
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

    // Private functions for adding connection

    /**
     * Adds a connection to a family member's connections list,
     *
     * @param memberId The ID of the family member to whom the connection will be added.
     * @param connectionToBeAdded The connection to add to the family member's connections list.
     */
    private fun addConnectionToSingleMember(memberId: String, connectionToBeAdded: Connection) {
        members[memberId]?.addConnection(connectionToBeAdded)
    }

    /**
     * Adds a mutual connection between two members to their respective connection lists,
     * and updates the modifiedAndNewAddedMembersIds list.
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

        // Add both members id's to list of members who were modified
        modifiedAndNewAddedMembersIds.add(memberOne.getId())
        modifiedAndNewAddedMembersIds.add(memberTwo.getId())
    }

    /**
     * Adds a child-parent connection between two members,
     * and updates the modifiedAndNewAddedMembersIds list.
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

        // Add both members id's to list of members who were modified
        modifiedAndNewAddedMembersIds.add(child.getId())
        modifiedAndNewAddedMembersIds.add(parent.getId())
    }

    /**
     * Adds a grandchild-grandparent connection between two members,
     * and updates the modifiedAndNewAddedMembersIds list.
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

        // Add both members id's to list of members who were modified
        modifiedAndNewAddedMembersIds.add(grandchild.getId())
        modifiedAndNewAddedMembersIds.add(grandparent.getId())
    }

    /**
     * Establishes a marriage connection between two family members.
     * If one member is a rabbi, the spouse is marked accordingly.
     *
     * @param memberOne The first spouse.
     * @param memberTwo The second spouse.
     */
    private fun addMarriageConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        addMutualConnection(memberOne, memberTwo, Relations.MARRIAGE)

        // If one of the members is a rabbi, his wife should be updated as a rabbi wife
        if (memberOne.getIsRabbi()) {
            memberTwo.setIsRabbi(true)
        }
        if (memberTwo.getIsRabbi()) {
            memberOne.setIsRabbi(true)
        }

        // Add to QueueOfSuggestedConnections optional connections
        addSuggestedConnectionsForMarriageFromOneToTwo(memberOne, memberTwo)
    }

    /**
     * Establishes a sibling connection between two family members and ensures
     * their existing mutual connections are updated accordingly.
     *
     * @param memberOne The first sibling.
     * @param memberTwo The second sibling.
     */
    private fun addSiblingsConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        // Add the members themself to each others connection
        addMutualConnection(memberOne, memberTwo, Relations.SIBLINGS)

        // Add their mutual connections to each other
        addConnectionsFromOneSiblingToAnother(memberOne, memberTwo)
        addConnectionsFromOneSiblingToAnother(memberTwo, memberOne)
    }

    /**
     * Establishes a cousin connection between two family members and ensures
     * their siblings are also connected as cousins.
     *
     * @param memberOne The first cousin.
     * @param memberTwo The second cousin.
     */
    private fun addCousinsConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        // Add the members themself to each others connection
        addMutualConnection(memberOne, memberTwo, Relations.COUSINS)


        // Look for siblings in both members and add to their connection the cousin
        addRelationToMembersSibling(memberOne, memberTwo, Relations.COUSINS)
        addRelationToMembersSibling(memberTwo, memberOne, Relations.COUSINS)
    }

    /**
     * Adds a parent-child connection between two family members.
     * Ensures the relationship is established in both directions.
     * If the parent has a parent, they will be added as a grandparent to the child.
     * If the child has a child, they will be added as a grandchild to the parent.
     *
     * @param memberOne The parent family member.
     * @param memberTwo The child family member.
     * @param relationFromMemberOnePerspective The relationship from the perspective of memberOne.
     */
    private fun addParentConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations,
    ) {
        // Add the members themself to each others connection
        addChildParentConnection(memberOne, memberTwo)

        // Add (if found) grandparents connections to child
        addConnectionsFromParentToChild(memberOne, memberTwo)

        // Add (if found) grandchildren connections to parent
        addConnectionsFromChildToParent(memberOne, memberTwo)

        // Look for siblings in child connections and add to their connection the parent
        addRelationToMembersSibling(memberOne, memberTwo, relationFromMemberOnePerspective)
    }

    /**
     * Adds a child-parent connection between two family members.
     * Ensures the relationship is established in both directions.
     * If the parent has a parent, they will be added as a grandparent to the child.
     * If the child has a child, they will be added as a grandchild to the parent.
     *
     * @param memberOne The child family member.
     * @param memberTwo The parent family member.
     */
    private fun addChildConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
    ) {
        // Add the members themself to each others connection
        addChildParentConnection(memberTwo, memberOne)

        // Add (if found) grandparents connections to child
        addConnectionsFromParentToChild(memberTwo, memberOne)

        // Add (if found) grandchildren connections to parent
        addConnectionsFromChildToParent(memberTwo, memberOne)

        // Determine if parent is a FATHER or a MOTHER
        val parent = if (memberOne.getGender()) {Relations.FATHER} else (Relations.MOTHER)

        // Look for siblings in child connections and add to their connection the parent
        addRelationToMembersSibling(memberTwo, memberOne, parent)
    }

    /**
     * Establishes a grandparent-grandchild relationship and updates sibling connections.
     *
     * @param memberOne The grandparent.
     * @param memberTwo The grandchild.
     * @param relationFromMemberOnePerspective Either GRANDFATHER or GRANDMOTHER.
     */
    private fun addGrandparentConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relationFromMemberOnePerspective: Relations,
    ) {
        // Add the members themself to each others connection
        addGrandchildGrandparentConnection(memberOne, memberTwo)

        // Look for siblings in grandchild connections and add to their connection the grandparent
        addRelationToMembersSibling(memberOne, memberTwo, relationFromMemberOnePerspective)
    }

    /**
     * Establishes a grandchild-grandparent relationship and updates sibling connections.
     *
     * @param memberOne The grandchild.
     * @param memberTwo The grandparent.
     */
    private fun addGrandchildConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
    ) {
        // Add the members themself to each others connection
        addGrandchildGrandparentConnection(memberTwo, memberOne)

        // Determine if grandparent is a GRANDFATHER or a GRANDMOTHER
        val grandparent =
            if (memberOne.getGender()) {Relations.GRANDFATHER} else (Relations.GRANDMOTHER)

        // Look for siblings in grandchild connections and add to their connection the grandparent
        addRelationToMembersSibling(memberTwo, memberOne, grandparent)
    }

    // Private functions for finding possible connections

    /**
     * Adds suggested connections between two family members who are married.
     *
     * @param memberOne The first spouse in the marriage.
     * @param memberTwo The second spouse in the marriage.
     */
    private fun addSuggestedConnectionsForMarriageFromOneToTwo(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        // Add suggested children connections
        addSuggestedChildrenConnectionsForMarriageFromOneToTwo(memberOne, memberTwo)
        addSuggestedChildrenConnectionsForMarriageFromOneToTwo(memberTwo, memberOne)

        // Add suggested grandchildren connections
        addSuggestedGrandchildrenConnectionsForMarriageFromOneToTwo(memberOne, memberTwo)
        addSuggestedGrandchildrenConnectionsForMarriageFromOneToTwo(memberTwo, memberOne)
    }

    /**
     * Suggests parent-child connections when a marriage relationship is established.
     *
     * This function checks if `memberOne` has any children and, if so, suggests adding
     * corresponding parental connections to `memberTwo` (the spouse). The function ensures
     * that `memberTwo` does not already have the same child connection before adding it.
     *
     * @param memberOne The first member in the marriage relationship.
     * @param memberTwo The second member in the marriage relationship.
     */
    private fun addSuggestedChildrenConnectionsForMarriageFromOneToTwo(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        for (connection in memberOne.getConnections()) {

            // Check for possible children connections
            if (connection.relationship == Relations.SON ||
                connection.relationship == Relations.DAUGHTER) {

                // Make sure memberTwo doesn't have this connection already
                if (connection.memberId !in memberTwo.getConnections().map { it.memberId }) {

                    // Get child FamilyMember object
                    val child = members[connection.memberId]

                    // Determine if parent is a FATHER or a MOTHER
                    val parentRelation =
                        if(memberTwo.getGender()) {Relations.FATHER} else {Relations.MOTHER}

                    // Create the full connection to add to queue
                    val fullConnection = FullConnection(memberTwo, child!!, parentRelation)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }
            }
        }
    }

    /**
     * Suggests grandparent-grandchild connections when a marriage relationship is established.
     *
     * This function checks if `memberOne` has any grandchildren and, if so, suggests adding
     * corresponding grandparent connections to `memberTwo` (the spouse). The function ensures
     * that `memberTwo` does not already have the same grandchild connection before adding it.
     *
     * @param memberOne The first member in the marriage relationship.
     * @param memberTwo The second member in the marriage relationship.
     */
    private fun addSuggestedGrandchildrenConnectionsForMarriageFromOneToTwo(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        for (connection in memberOne.getConnections()) {

            // Check for possible grandchildren connections
            if (connection.relationship == Relations.GRANDSON ||
                connection.relationship == Relations.GRANDDAUGHTER) {

                // Make sure memberTwo doesn't have this connection already
                if (connection.memberId !in memberTwo.getConnections().map { it.memberId }) {

                    // Get grandchild FamilyMember object
                    val grandchild = members[connection.memberId]

                    // Determine if parent is a GRANDFATHER or a GRANDMOTHER
                    val grandparentRelation =
                        if(memberTwo.getGender()) {Relations.GRANDFATHER} else {Relations.GRANDMOTHER}

                    // Create the full connection to add to queue
                    val fullConnection = FullConnection(memberTwo, grandchild!!, grandparentRelation)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }
            }
        }
    }


    // Private functions for adding connections based on the connection the user added

    /**
     * Adds mutual connections from one sibling to another based on existing relationships.
     *
     * This function iterates through the connections of `memberOne` and adds applicable
     * relationships to `memberTwo`, ensuring that certain non-mutual relationships
     * (e.g., marriage, son, daughter, grandson, granddaughter) are excluded.
     *
     * @param memberOne The first sibling whose connections will be copied.
     * @param memberTwo The second sibling who will receive applicable connections.
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

                addConnectionToBothMembers(
                    memberTwo,
                    members[connection.memberId]!!,
                    connection.relationship
                )

                // Add the member that was connected to modified member id list
                modifiedAndNewAddedMembersIds.add(connection.memberId)
            }
        }
    }

    /**
     * Adds parent connections of the parent as grandparents connections to the child.
     *
     * @param child The child who will receive grandparent connections.
     * @param parent The parent whose parents will be added as the child's grandparents.
     */
    private fun addConnectionsFromParentToChild(
        child: FamilyMember,
        parent: FamilyMember
    ) {
        for (connection in parent.getConnections()) {
            // Only consider parents of the parent
            if (connection.relationship != Relations.FATHER &&
                connection.relationship != Relations.MOTHER
            ) {
                continue
            }

            // Check if the child already has this grandparent connection
            val alreadyConnected = child.getConnections().any {
                it.memberId == connection.memberId &&
                        (it.relationship == Relations.GRANDFATHER ||
                        it.relationship == Relations.GRANDMOTHER)
            }

            // Add the grandparent connection if it doesn't already exist
            if (!alreadyConnected) {

                // Determine if it's a GRANDFATHER or a GRANDMOTHER
                val grandparentRelation =
                    if (connection.relationship == Relations.FATHER) Relations.GRANDFATHER
                    else Relations.GRANDMOTHER


                addConnectionToBothMembers(
                    child,
                    members[connection.memberId]!!,
                    grandparentRelation
                )

                // Add the member that was connected to modified member id list
                modifiedAndNewAddedMembersIds.add(connection.memberId)
            }
        }
    }

    /**
     * Adds child connections of the child as grandchildren connections to the parent.
     *
     * @param child The child whose children will be added as the parent's grandchildren.
     * @param parent The parent who will receive grandchild connections.
     */
    private fun addConnectionsFromChildToParent(
        child: FamilyMember,
        parent: FamilyMember
    ) {
        for (connection in child.getConnections()) {

            // Only consider children of the child
            if (connection.relationship != Relations.SON &&
                connection.relationship != Relations.DAUGHTER
            ) {
                continue
            }

            // Check if the parent already has this grandchild connection
            val alreadyConnected = parent.getConnections().any {
                it.memberId == connection.memberId &&
                        (it.relationship == Relations.GRANDSON ||
                                it.relationship == Relations.GRANDDAUGHTER)
            }

            // Add the grandchild connection if it doesn't already exist
            if (!alreadyConnected) {

                // Determine if it's a GRANDSON or a GRANDDAUGHTER
                val grandchildRelation =
                    if (connection.relationship == Relations.SON) Relations.GRANDSON
                    else Relations.GRANDDAUGHTER

                addConnectionToBothMembers(
                    parent,
                    members[connection.memberId]!!,
                    grandchildRelation
                )

                // Add the member that was connected to modified member id list
                modifiedAndNewAddedMembersIds.add(connection.memberId)
            }
        }
    }

    /**
     * Adds a specified relationship between a member and all of their siblings.
     *
     * This function iterates through `memberOne`'s connections to find siblings.
     * If `memberTwo` is not already connected to a sibling, it establishes the relationship
     * between them and marks the sibling as modified.
     *
     * @param memberOne The first family member whose siblings will be considered.
     * @param memberTwo The second family member who will be connected to the siblings.
     * @param relation The type of relationship to establish between memberTwo and the siblings.
     */
    private fun addRelationToMembersSibling(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relation: Relations
    ) {

        for (connection in memberOne.getConnections()) {
            if (connection.relationship == Relations.SIBLINGS) {
                val sibling = members[connection.memberId]!!

                // Check if memberTwo is already connected to this sibling
                if (sibling.getConnections().any { it.memberId == memberTwo.getId() && it.relationship == relation }) {
                    continue // Skip if the relation already exists
                }

                // Connect the sibling with memberTwo if not already connected, and add to modifiedAndNewAddedMembersIds
                addConnectionToBothMembers(sibling, memberTwo, relation)
                modifiedAndNewAddedMembersIds.add(connection.memberId)
            }
        }
    }
}




