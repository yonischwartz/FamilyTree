package com.example.familytree.data.dataManagement

import android.util.Log
import android.widget.Toast
import com.example.familytree.UniqueQueue
import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FullConnection
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import com.example.familytree.data.exceptions.InvalidGenderRoleException
import com.example.familytree.data.exceptions.InvalidMoreThanOneConnection
import com.example.familytree.data.exceptions.SameSexMarriageException
import com.example.familytree.data.exceptions.UnsafeDeleteException

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

    // Queue that holds suggested connections to offer the user to add to member map
    private val QueueOfSuggestedConnections = UniqueQueue<FullConnection>()

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
     * 1. Checks if the deletion is safe; if not, throws an UnsafeDeleteException.
     * 2. Removes the member from the `members` map.
     * 3. Adds the member's ID to `deletedMembersIds`.
     * 4. Iterates through all remaining members and removes any connections to the deleted member.
     * 5. If a member's connections are updated, their ID is added to `modifiedAndNewAddedMembersIds`.
     *
     * @param memberToBeRemovedId The ID of the member to be deleted.
     * @throws UnsafeDeleteException if deleting the member would split the graph.
     */
    internal fun deleteMember(memberToBeRemovedId: String) {

        // Check if memberToBeRemovedId can be removed safely
        if (isDeleteSafe(memberToBeRemovedId).not()) {
            throw UnsafeDeleteException(memberToBeRemovedId)
        }

        // Remove the member from the map
        members.remove(memberToBeRemovedId)

        // Add member's ID to the list of deleted members
        deletedMembersIds.add(memberToBeRemovedId)

        // Iterate over all remaining members and remove the deleted member from their connections
        for (member in members.values) {
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
     * Retrieves a list of all family members who are Yeshiva members.
     *
     * @return A list of [FamilyMember] objects whose [MemberType] is [MemberType.Yeshiva].
     */
    internal fun getAllYeshivaMembers(): List<FamilyMember> {
        return members.values.filter { it.getMemberType() == MemberType.Yeshiva }
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
                addChildParentConnection(memberOne, memberTwo)

            Relations.SON, Relations.DAUGHTER ->
                addChildParentConnection(memberTwo, memberOne)

            Relations.GRANDMOTHER, Relations.GRANDFATHER ->
                addGrandchildGrandparentConnection(memberOne, memberTwo)

            Relations.GRANDSON, Relations.GRANDDAUGHTER ->
                addGrandchildGrandparentConnection(memberTwo, memberOne)
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
     * Retrieves the relationship between a family member and one of their connections.
     *
     * @param memberOne The family member whose connections are being checked.
     * @param memberTwo The family member to find within the connections of [memberOne].
     * @return The [Relations] enum representing the relationship between [memberOne] and [memberTwo],
     *         or null if no such connection exists.
     */
    internal fun getRelationBetweenMemberAndOneOfHisConnections(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ): Relations? {

        for (connection in memberOne.getConnections()) {
            if (connection.memberId == memberTwo.getId()) {
                return connection.relationship
            }
        }
        // If memberTwo isn't in memberOne's connections, return null
        return null
    }

    /**
     * Finds the shortest path between two family members using a BFS algorithm.
     *
     * @param memberOne The starting family member.
     * @param memberTwo The target family member.
     * @return A list of FamilyMember objects representing the shortest path from memberOne to memberTwo.
     *         If no path is found, the list will be empty.
     */
    internal fun getShortestPathBetweenTwoMembers(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ): List<FamilyMember> {

        // Set to keep track of visited members by their ID
        val visited = mutableSetOf<String>()

        // Queue to handle the BFS traversal
        val queue = UniqueQueue<FamilyMember>()

        // Map to track the parent of each visited member for path reconstruction
        val parentMap = mutableMapOf<String, FamilyMember?>()

        // Initialize BFS with the starting member
        queue.add(memberOne)
        parentMap[memberOne.getId()] = null

        while (queue.isNotEmpty()) {
            val currentMember = queue.poll() ?: continue

            // Check if the target member is reached
            if (currentMember.getId() == memberTwo.getId()) {
                // Reconstruct the path by backtracking using the parent map
                val path = mutableListOf<FamilyMember>()
                var member: FamilyMember? = currentMember
                while (member != null) {
                    path.add(member)
                    member = parentMap[member.getId()]
                }
                return path.reversed() // The path is constructed in reverse order
            }

            // Mark as visited when dequeued, not when enqueued
            visited.add(currentMember.getId())

            // Enqueue all unvisited connections of the current member
            for (connection in currentMember.getConnections()) {
                val member = members[connection.memberId]
                if (member != null && member.getId() !in visited && member.getId() !in parentMap) {
                    queue.add(member)
                    parentMap[member.getId()] = currentMember
                }
            }
        }

        // Return an empty list if no path is found
        return emptyList()
    }

    /**
     * Clears all family members from the local storage.
     */
    internal fun clearMemberMap() {
        members.clear()
    }

    // Private functions for deleting member

    /**
     * Checks if removing a member from the family tree would split the graph into multiple components.
     *
     * @param memberToBeRemovedId The ID of the member to be removed.
     * @return true if removing the member does not split the graph, false otherwise.
     *
     * Time Complexity: O(V + E), where V is the number of members and E is the number of relationships.
     * Space Complexity: O(V) due to the visited set.
     */
    private fun isDeleteSafe(memberToBeRemovedId: String): Boolean {
        val visited = mutableSetOf<String>()
        val membersWithoutMemberToBeRemoved = members.keys - memberToBeRemovedId

        // If no members remain, it's safe
        if (membersWithoutMemberToBeRemoved.isEmpty()) return true

        // Find a starting point different from the one being removed
        val startMember = membersWithoutMemberToBeRemoved.first()

        // Perform DFS to check connectivity
        fun dfs(memberId: String) {
            if (memberId in visited) return
            visited.add(memberId)
            members[memberId]?.getConnections()?.forEach { connection ->
                if (connection.memberId != memberToBeRemovedId) {
                    dfs(connection.memberId)
                }
            }
        }

        dfs(startMember)

        // Check for any unexpected members in visited
        if (visited.size != membersWithoutMemberToBeRemoved.size) {
            val unexpectedMembers = visited - membersWithoutMemberToBeRemoved
            unexpectedMembers.forEach { memberId ->
                val memberName = members[memberId]?.getFullName() ?: "MemberId: $memberId"
                Log.e("FamilyTree", "Unexpected member in graph: $memberName")
            }
        }


        return visited.size == membersWithoutMemberToBeRemoved.size
    }

    // Private functions for validation

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
     * @throws SameSexMarriageException If the two members are of the same gender.
     */
    private fun validateMarriage(memberOne: FamilyMember, memberTwo: FamilyMember) {
        validateNotMoreThanOneMemberConnection(memberOne, Relations.MARRIAGE)
        validateNotMoreThanOneMemberConnection(memberTwo, Relations.MARRIAGE)
        if (memberOne.getGender() == memberTwo.getGender()) {
            throw SameSexMarriageException()
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
    private fun addMutualConnectionToMap(
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
    private fun addChildParentConnectionToMap(
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
    private fun addGrandchildGrandparentConnectionToMap(
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
        addMutualConnectionToMap(memberOne, memberTwo, Relations.MARRIAGE)

        // If one of the members is a rabbi, his wife should be updated as a rabbi wife
        if (memberOne.getIsRabbi()) {
            memberTwo.setIsRabbi(true)
        }
        if (memberTwo.getIsRabbi()) {
            memberOne.setIsRabbi(true)
        }

        // Add to QueueOfSuggestedConnections optional connections
        addSuggestionConnectionsAfterAddingMarriageConnection(memberOne, memberTwo)
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
        addMutualConnectionToMap(memberOne, memberTwo, Relations.SIBLINGS)

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
        addMutualConnectionToMap(memberOne, memberTwo, Relations.COUSINS)

        // Look for siblings in both members and add to their connection the cousin
        addRelationToMembersSibling(memberOne, memberTwo, Relations.COUSINS)
        addRelationToMembersSibling(memberTwo, memberOne, Relations.COUSINS)

        // Add to QueueOfSuggestedConnections optional connections
        addSuggestionConnectionsAfterAddingCousinConnection(memberOne, memberTwo)
    }

    /**
     * Adds a parent-child connection between two family members.
     * Ensures the relationship is established in both directions.
     * If the parent has a parent, they will be added as a grandparent to the child.
     * If the child has a child, they will be added as a grandchild to the parent.
     *
     * @param child The parent family member.
     * @param parent The child family member.
     */
    private fun addChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember,
    ) {
        // Add the members themself to each others connection
        addChildParentConnectionToMap(child, parent)

        // Add (if found) grandparents connections to child
        addConnectionsFromParentToChild(child, parent)

        // Add (if found) grandchildren connections to parent
        addConnectionsFromChildToParent(child, parent)

        // Determine if parent is a FATHER or a MOTHER
        val parentRelation = if (parent.getGender()) {Relations.FATHER} else (Relations.MOTHER)

        // Look for siblings in child connections and add to their connections the parent
        addRelationToMembersSibling(child, parent, parentRelation)

        // Look for children in parent connections and add to their connections the child as sibling
        addSiblingConnectionToMembersChild(child, parent)

        // Add to QueueOfSuggestedConnections optional connections
        addSuggestionConnectionsAfterAddingChildParentConnection(child, parent)
    }

    /**
     * Establishes a grandparent-grandchild relationship and updates sibling connections.
     *
     * @param grandchild The grandchild.
     * @param grandparent The grandparent.
     * @param relationFromMemberOnePerspective Either GRANDFATHER or GRANDMOTHER.
     */
    private fun addGrandchildGrandparentConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember,
    ) {
        // Add the members themself to each others connection
        addGrandchildGrandparentConnectionToMap(grandchild, grandparent)

        // Determine if grandparent is a GRANDFATHER or a GRANDMOTHER
        val grandparentRelation =
            if (grandparent.getGender()) {Relations.GRANDFATHER} else (Relations.GRANDMOTHER)

        // Look for siblings in grandchild connections and add to their connection the grandparent
        addRelationToMembersSibling(grandchild, grandparent, grandparentRelation)

        // Add to QueueOfSuggestedConnections optional connections
        addSuggestionConnectionsAfterAddingGrandchildGrandparentConnection(grandchild, grandparent)
    }

    // Private functions for finding possible connections

    /**
     * Suggests connections after adding a marriage connection.
     *
     * @param memberOne The first spouse in the marriage.
     * @param memberTwo The second spouse in the marriage.
     */
    private fun addSuggestionConnectionsAfterAddingMarriageConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        // Add suggested children connections
        addSuggestionOfChildrenConnectionsAfterAddingMarriageConnection(memberOne, memberTwo)
        addSuggestionOfChildrenConnectionsAfterAddingMarriageConnection(memberTwo, memberOne)

        // Add suggested grandchildren connections
        addSuggestionOfGrandchildrenConnectionsAfterAddingMarriageConnection(memberOne, memberTwo)
        addSuggestionOfGrandchildrenConnectionsAfterAddingMarriageConnection(memberTwo, memberOne)
    }

    /**
     * Suggests parent-child connections when a marriage relationship is established.
     *
     * This function checks if `memberOne` has any children and, if so, suggests adding
     * corresponding child connections to `memberTwo` (the spouse). The function ensures
     * that `memberTwo` does not already have the same child connection before adding it.
     *
     * @param memberOne The first member in the marriage relationship.
     * @param memberTwo The second member in the marriage relationship.
     */
    private fun addSuggestionOfChildrenConnectionsAfterAddingMarriageConnection(
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
                    val fullConnection = FullConnection(child!!, memberTwo, parentRelation)

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
     * corresponding grandchild connections to `memberTwo` (the spouse). The function ensures
     * that `memberTwo` does not already have the same grandchild connection before adding it.
     *
     * @param memberOne The first member in the marriage relationship.
     * @param memberTwo The second member in the marriage relationship.
     */
    private fun addSuggestionOfGrandchildrenConnectionsAfterAddingMarriageConnection(
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
                    val fullConnection = FullConnection(grandchild!!, memberTwo, grandparentRelation)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }
            }
        }
    }

    /**
     * Suggests connections after adding a cousin connection.
     *
     * @param memberOne The first cousin.
     * @param memberTwo The second cousin.
     */
    private fun addSuggestionConnectionsAfterAddingCousinConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {

        // Add suggested grandparent connections to both cousins
        addSuggestionOfGrandparentsConnectionsAfterAddingCousinConnection(memberOne, memberTwo)
        addSuggestionOfGrandparentsConnectionsAfterAddingCousinConnection(memberTwo, memberOne)

        // Add suggested siblings and other cousins connections
        addSuggestionOfCousinsAndSiblingsConnectionsAfterAddingCousinConnection(memberOne, memberTwo)
        addSuggestionOfCousinsAndSiblingsConnectionsAfterAddingCousinConnection(memberTwo, memberOne)
    }

    /**
     * Suggests grandparent-grandchild connections when a cousin relationship is established.
     *
     * This function checks if `memberOne` has any grandparents and, if so, suggests adding
     * corresponding grandparent connections to `memberTwo` (the cousin). The function ensures
     * that `memberTwo` does not already have the same grandparent connection before adding it.
     *
     * @param memberOne The first member in the cousin relationship.
     * @param memberTwo The second member in the cousin relationship.
     */
    private fun addSuggestionOfGrandparentsConnectionsAfterAddingCousinConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        for (connection in memberOne.getConnections()) {

            // Check for possible grandparent connections
            if (connection.relationship == Relations.GRANDFATHER ||
                connection.relationship == Relations.GRANDMOTHER) {

                // Make sure memberTwo doesn't have this grandparent connection already
                if (connection.memberId !in memberTwo.getConnections().map { it.memberId }) {

                    // Get grandparent FamilyMember object
                    val grandparent = members[connection.memberId]

                    // Determine if grandparent is a GRANDFATHER or GRANDMOTHER
                    val grandparentRelation =
                        if (grandparent!!.getGender()) Relations.GRANDFATHER else Relations.GRANDMOTHER

                    // Create the full connection to add to queue
                    val fullConnection = FullConnection(memberTwo, grandparent, grandparentRelation)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }
            }
        }
    }

    /**
     * Suggests cousin and sibling connections when a cousin relationship is established.
     *
     * This function checks if the grandparent has any other grandchildren and, if so,
     * suggests adding corresponding cousin and sibling connections to the grandchild.
     * The function ensures that the grandchild does not already have the connection
     * before adding it.
     *
     * @param memberOne The first cousin.
     * @param memberTwo The second cousin.
     */
    private fun addSuggestionOfCousinsAndSiblingsConnectionsAfterAddingCousinConnection(
        memberOne: FamilyMember,
        memberTwo: FamilyMember
    ) {
        for (connection in memberOne.getConnections()) {
            // Check if memberOne has other cousins
            if (connection.relationship == Relations.COUSINS &&
                // Ensure this cousin isn't memberTwo himself
                connection.memberId != memberTwo.getId()
            ) {

                val anotherCousinOfMemberOne = members[connection.memberId]

                // Ensure the memberTwo does not already have this connection
                if (anotherCousinOfMemberOne!!.getId() !in memberTwo.getConnections().map { it.memberId }) {

                    // Create suggested COUSIN connection
                    val fullCousinConnection =
                        FullConnection(memberTwo, anotherCousinOfMemberOne, Relations.COUSINS)

                    // Create suggested SIBLING connection
                    val fullSiblingConnection =
                        FullConnection(memberTwo, anotherCousinOfMemberOne, Relations.SIBLINGS)

                    // Add the suggested connection to queue
                    QueueOfSuggestedConnections.add(fullCousinConnection)
                    QueueOfSuggestedConnections.add(fullSiblingConnection)
                }

            }
        }
    }

    /**
     * Suggests connections after adding a grandparent connection.
     *
     * @param grandchild The grandchild.
     * @param grandparent The grandparent.
     */
    private fun addSuggestionConnectionsAfterAddingGrandchildGrandparentConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember
    ) {
        // Add suggested grandparents connections
        addSuggestionOfGrandparentConnectionsAfterAddingGrandparentConnection(grandchild, grandparent)

        // Add suggested children connections
        addSuggestionOfChildrenConnectionsAfterAddingGrandchildGrandparentConnection(grandchild, grandparent)

        // Add suggested marriage connections
        addSuggestionOfMarriageConnectionAfterAddingGrandparentConnection(grandchild, grandparent)

        // Add suggested cousins and children connections
        addSuggestionOfCousinsAndSiblingsConnectionsAfterAddingGrandchildConnection(grandchild, grandparent)
    }

    /**
     * Suggests cousin and sibling connections when a grandchild relationship is established.
     *
     * This function checks if the grandparent has any other grandchildren and, if so,
     * suggests adding corresponding cousin and sibling connections to the grandchild.
     * The function ensures that the grandchild does not already have the connection
     * before adding it.
     *
     * @param grandchild The grandchild.
     * @param grandparent The grandparent.
     */
    private fun addSuggestionOfCousinsAndSiblingsConnectionsAfterAddingGrandchildConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember
    ) {
        for (connection in grandparent.getConnections()) {
            // Check if the grandparent has other grandchildren
            if (connection.relationship == Relations.GRANDSON ||
                connection.relationship == Relations.GRANDDAUGHTER
            ) {
                val otherGrandchild = members[connection.memberId]

                if (otherGrandchild!!.getId() != grandchild.getId()) {
                    // Ensure the grandchild does not already have this connection
                    if (otherGrandchild.getId() !in grandchild.getConnections().map { it.memberId }) {

                        // Create suggested COUSIN connection
                        val fullCousinConnection =
                            FullConnection(grandchild, otherGrandchild, Relations.COUSINS)

                        // Create suggested SIBLING connection
                        val fullSiblingConnection =
                            FullConnection(grandchild, otherGrandchild, Relations.SIBLINGS)

                        // Add the suggested connection to queue
                        QueueOfSuggestedConnections.add(fullCousinConnection)
                        QueueOfSuggestedConnections.add(fullSiblingConnection)
                    }
                }
            }
        }
    }

    /**
     * Suggests grandparent-grandchild connections when a grandparent relationship is established.
     *
     * This function goes over the grandchild's cousins and suggests adding
     * corresponding grandparent connection to them.
     * The function ensures that the cousin does not already have the connection.
     *
     * @param grandchild The grandchild.
     * @param grandparent The grandparent.
     */
    private fun addSuggestionOfGrandparentConnectionsAfterAddingGrandparentConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember
    ) {
        for (connection in grandchild.getConnections()) {
            // Check if the grandchild has cousins
            if (connection.relationship == Relations.COUSINS) {
                val cousin = members[connection.memberId]

                // Check if the cousin already has grandparent as a connection
                if (grandparent.getId() !in cousin!!.getConnections().map { it.memberId }) {

                    // Determine if grandparent is a GRANDFATHER or a GRANDMOTHER
                    val grandparentRelation =
                        if(grandparent.getGender()) {Relations.GRANDFATHER}
                        else {Relations.GRANDMOTHER}

                    // Create the full connection to add to queue
                    val fullConnection =
                        FullConnection(cousin, grandparent, grandparentRelation)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }
            }
        }
    }

    /**
     * Suggests children connections when a grandparent relationship is established.
     *
     * This function goes over the grandchild's parents and suggests adding
     * corresponding child-parent connection between them and the grandparent.
     * The function ensures that the cousin does not already have the connection.
     *
     * @param grandchild The grandchild.
     * @param grandparent The grandparent.
     */
    private fun addSuggestionOfChildrenConnectionsAfterAddingGrandchildGrandparentConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember
    ) {
        for (connection in grandchild.getConnections()) {
            // Check if the grandchild has parents
            if (connection.relationship == Relations.FATHER ||
                connection.relationship == Relations.MOTHER) {

                val parent = members[connection.memberId]

                // Determine weather grandparent is FATHER or MOTHER of parent
                val parentRelation =
                    if (grandparent.getGender()) Relations.FATHER else Relations.MOTHER

                // Check if the parent already has grandparent as a connection
                if (grandparent.getId() !in parent!!.getConnections().map { it.memberId }) {

                    // Create the full connection to add to queue
                    val fullConnection =
                        FullConnection(parent, grandparent, parentRelation)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }
            }
        }

        for (connection in grandparent.getConnections()) {
            // Check if the grandparent has children
            if (connection.relationship == Relations.SON ||
                connection.relationship == Relations.DAUGHTER) {

                val child = members[connection.memberId]

                // Determine weather grandchild is SON or DAUGHTER of child
                val childRelation =
                    if (grandchild.getGender()) Relations.SON else Relations.DAUGHTER

                // Check if the child already has grandchild as a connection
                if (grandchild.getId() !in child!!.getConnections().map { it.memberId }) {

                    // Create the full connection to add to queue
                    val fullConnection =
                        FullConnection(child, grandchild, childRelation)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }
            }
        }
    }

    /**
     * Suggests marriage connections when a grandparent-grandchild relationship is established.
     *
     * This function checks if the child has another grandparent with an opposite gender.
     * If he does, the function adds the other grandparent as a optional marriage connection
     * to the given grandparent.
     * The function ensures that the two grandparents don't already have a marriage connection.
     *
     * @param grandchild The child.
     * @param grandparent The parent.
     */
    private fun addSuggestionOfMarriageConnectionAfterAddingGrandparentConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember
    ) {
        for (connection in grandchild.getConnections()) {
            if ((connection.relationship == Relations.GRANDFATHER ||
                connection.relationship == Relations.GRANDMOTHER)
                // Make sure it's a different grandparent
                && connection.memberId != grandparent.getId()
            ) {
                val otherGrandparent = members[connection.memberId]

                // Ensure the two grandparents have opposite genders.
                if (otherGrandparent!!.getGender() != grandparent.getGender()) {

                    // Ensure that neither of them has an existing marriage
                    val grandparentHasMarriage =
                        grandparent.getConnections().any { it.relationship == Relations.MARRIAGE }
                    val otherGrandparentHasMarriage =
                        otherGrandparent.getConnections().any { it.relationship == Relations.MARRIAGE }

                    if (!grandparentHasMarriage && !otherGrandparentHasMarriage) {
                        // Create the full connection to add to queue
                        val fullConnection =
                            FullConnection(grandparent, otherGrandparent, Relations.MARRIAGE)

                        // Add the full connection to queue
                        QueueOfSuggestedConnections.add(fullConnection)
                    }
                }

                // Only one other grandparent possible, so stop after finding one
                break
            }
        }
    }

    /**
     * Suggests connections after adding a parent-child connection.
     *
     * @param child The child.
     * @param parent The parent.
     */
    private fun addSuggestionConnectionsAfterAddingChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember
    ) {
        // Add suggested cousin connections
        addSuggestionOfCousinConnectionsAfterAddingChildParentConnection(child, parent)

        // Add suggested marriage connections
        addSuggestionOfMarriageConnectionAfterAddingChildParentConnection(child, parent)

        // Add suggested child-parent connections
        addSuggestionOfChildParentConnectionAfterAddingChildParentConnection(child, parent)
    }

    /**
     * Suggests cousin connections when a child-parent relationship is established.
     *
     * This function goes over the parent's siblings. Every sibling is an uncle or an aunt.
     * For every uncle or aunt, the function goes over their connections, and looks for a child.
     * If it finds a child, it adds him as an optional cousin to the given child.
     * After finding a child of an uncle / aunt, the loop stops, because if one of the
     * children is a cousin, they all are.
     * The function ensures that the cousin does not already have the connection.
     *
     * @param child The child.
     * @param parent The parent.
     */
    private fun addSuggestionOfCousinConnectionsAfterAddingChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember
    ) {
        for (connection in parent.getConnections()) {
            // Check if the parent has siblings (i.e., uncles/aunts of the child)
            if (connection.relationship == Relations.SIBLINGS) {

                // Parent's sibling is child's uncle or aunt.
                val uncleOrAunt = members[connection.memberId]

                // Go over uncle's or aunt's connections
                for (uncleOrAuntConnection in uncleOrAunt!!.getConnections()) {

                    // Check if the uncle/aunt has children (i.e., cousins of the child)
                    if (uncleOrAuntConnection.relationship == Relations.SON ||
                        uncleOrAuntConnection.relationship == Relations.DAUGHTER) {

                        val cousin = members[uncleOrAuntConnection.memberId]

                        // Ensure the cousin doesn't already have this connection
                        if (child.getId() !in cousin!!.getConnections().map { it.memberId }) {

                            // Create the full connection to add to queue
                            val fullConnection =
                                FullConnection(child, cousin, Relations.COUSINS)

                            // Add the full connection to queue
                            QueueOfSuggestedConnections.add(fullConnection)
                        }

                        // Break after finding the first cousin
                        break
                    }
                }
            }
        }
    }

    /**
     * Suggests marriage connections when a child-parent relationship is established.
     *
     * This function checks if the child has another parent. If he does, the function
     * adds the other parent as a optional marriage connection to the given parent.
     * The function ensures that the two parents don't already have a marriage connection.
     *
     * @param child The child.
     * @param parent The parent.
     */
    private fun addSuggestionOfMarriageConnectionAfterAddingChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember
    ) {
        for (connection in child.getConnections()) {
            // Check if the child has another parent
            if ((connection.relationship == Relations.FATHER ||
                connection.relationship == Relations.MOTHER)
                // Make sure it's a different parent
                && connection.memberId != parent.getId()
            ) {
                val otherParent = members[connection.memberId]

                // Ensure neither parent has any existing marriage connections
                val parentHasMarriage =
                    parent.getConnections().any { it.relationship == Relations.MARRIAGE }
                val otherParentHasMarriage =
                    otherParent!!.getConnections().any { it.relationship == Relations.MARRIAGE }

                if (!parentHasMarriage && !otherParentHasMarriage) {
                    // Create the full connection to add to queue
                    val fullConnection = FullConnection(parent, otherParent, Relations.MARRIAGE)

                    // Add the full connection to queue
                    QueueOfSuggestedConnections.add(fullConnection)
                }

                // Only one other parent possible, so we stop after finding one
                break
            }
        }
    }

    /**
     * Suggests child-parent connections when a child-parent relationship is established.
     *
     * This function checks if the parent has grandchildren. If he does, the function
     * adds them as an optional child of the child.
     * The function ensures there is not a connection already.
     *
     * @param child The child.
     * @param parent The parent.
     */
    private fun addSuggestionOfChildParentConnectionAfterAddingChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember
    ) {
        for (connection in parent.getConnections()) {

            // Look for grandchildren
            if (connection.relationship == Relations.GRANDSON ||
                connection.relationship == Relations.GRANDDAUGHTER) {

                val grandchild = members[connection.memberId]

                // Check if grandchild already has child as a connection
                if (grandchild!!.getConnections().any { it.memberId == child.getId() }) {
                    continue
                }

                // Determine whether child is grandchild FATHER or MOTHER
                val parentRelation =
                    if(child.getGender()) { Relations.FATHER } else { Relations.MOTHER }

                // Create suggestion
                val suggestedConnection = FullConnection(grandchild, child, parentRelation)

                // Add the suggested connection to queue
                QueueOfSuggestedConnections.add(suggestedConnection)
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
     * Adds a specified relationship to member's siblings.
     *
     * This function iterates through `memberOne`'s connections to find siblings.
     * If `memberTwo` is not already connected to the sibling, it establishes the relationship
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


    /**
     * Adds a sibling connection between the given child and all other children of the specified parent.
     *
     * This function iterates through the parent's connections and identifies all connections
     * that are either sons or daughters. For each of these connections, it checks if the child
     * is already connected as a sibling. If not, it establishes a sibling relationship between
     * the child and the other children of the parent.
     *
     * @param child The FamilyMember representing the child to connect as a sibling.
     * @param parent The FamilyMember whose children will be checked for sibling connections.
     */
    private fun addSiblingConnectionToMembersChild(
        child: FamilyMember,
        parent: FamilyMember,
    ) {

        for (connection in parent.getConnections()) {

            if (connection.relationship == Relations.SON ||
                connection.relationship == Relations.DAUGHTER) {

                // Skip the sibling connection to member two
                if (connection.memberId == child.getId()) {
                    continue
                }

                val otherChild = members[connection.memberId]

                // Check if child already has otherChild as a connection
                if (otherChild!!.getConnections().any { it.memberId == child.getId() }) {
                    continue
                }

                // Connect the sibling with memberTwo if not already connected, and add to modifiedAndNewAddedMembersIds
                addConnectionToBothMembers(child, otherChild, Relations.SIBLINGS)
            }
        }
    }
}






