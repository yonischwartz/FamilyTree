package com.example.familytree.data

import com.example.familytree.data.exceptions.InvalidGenderRoleException
import com.example.familytree.data.exceptions.InvalidRelationshipException
import com.example.familytree.data.exceptions.MemberNotInAdjacencyListException

/**
 * The [FamilyConnections] class manages the relationships between family members
 * using an adjacency list representation implemented as a map.
 * This class allows users to find and manage the connections associated with
 * a specific family member.
 *
 * This class follows the Singleton design pattern to ensure that there is only one
 * instance managing all family connections across the application.
 */
class FamilyConnections private constructor() {

    // Map to store family connections (adjacency list)
    private val adjacencyList: MutableMap<Int, MutableList<Connection>> = mutableMapOf()

    /**
     * A map storing the expected gender for each relationship type.
     * The key is the relationship, and the value is the expected gender:
     * true means male, false means female.
     */
    private val relationshipGenderMap: MutableMap<Realations, Boolean> = mutableMapOf()

    init {
        // Initialize the map once with predefined relationships
        relationshipGenderMap[Realations.FATHER] = true  // FATHER should be male
        relationshipGenderMap[Realations.MOTHER] = false  // MOTHER should be female
        relationshipGenderMap[Realations.SON] = true  // SON should be male
        relationshipGenderMap[Realations.DAUGHTER] = false  // DAUGHTER should be female
        relationshipGenderMap[Realations.GRANDMOTHER] = false  // GRANDMOTHER should be female
        relationshipGenderMap[Realations.GRANDFATHER] = true  // GRANDFATHER should be male
        relationshipGenderMap[Realations.GRANDSON] = true  // GRANDSON should be male
        relationshipGenderMap[Realations.GRANDDAUGHTER] = false  // GRANDDAUGHTER should be female
    }

    // Singleton instance
    companion object {
        @Volatile
        private var instance: FamilyConnections? = null

        fun getInstance(): FamilyConnections {
            return instance ?: synchronized(this) {
                instance ?: FamilyConnections().also { instance = it }
            }
        }
    }

    /**
     * Adds a new member to the adjacency list.
     */
    fun addNewMemberToAdjacencyList(familyMember: FamilyMember) {
        adjacencyList[familyMember.ID] = mutableListOf()
    }

    /**
     * Validates that the gender of a family member matches the expected
     * gender for a given relationship.
     *
     * <p>This method checks that the family member's gender matches the
     * expected role for the relationship type. If the gender does not
     * match the expected role, an exception is thrown.</p>
     *
     * <p>The method uses a predefined map of relationships to expected
     * genders. If the member's gender does not align with the specified
     * relationship, an {@link InvalidGenderRoleException} is thrown.</p>
     *
     * @param member the family member whose gender is being validated.
     * @param relationship the relationship being validated (e.g., FATHER,
     *                     MOTHER, SON, DAUGHTER, GRANDMOTHER, etc.).
     * @throws InvalidGenderRoleException if the member's gender does not
     *                                     align with the specified relationship.
     */
    @Throws(InvalidGenderRoleException::class)
    private fun validateGenderRole(member: FamilyMember, relationship: Realations) {
        // Check if the relationship exists in the map and get the expected gender
        val expectedGender = relationshipGenderMap[relationship]

        // If the relationship exists and the gender doesn't match, throw an exception
        if (expectedGender != null && member.gender != expectedGender) {
            throw InvalidGenderRoleException(member, relationship)
        }
    }


    /**
     * Validates that both specified family members exist in the adjacency list.
     *
     * This method checks if the IDs of [memberOne] and [memberTwo]
     * are present in the [adjacencyList]. If either member is not found,
     * a [MemberNotInAdjacencyListException] is thrown, identifying the
     * member that is missing.
     *
     * @param memberOne the first family member to validate.
     * @param memberTwo the second family member to validate.
     * @throws MemberNotInAdjacencyListException if either [memberOne] or
     *         [memberTwo] is not found in the adjacency list.
     */
    @Throws(MemberNotInAdjacencyListException::class)
    private fun validateMembersExist(memberOne: FamilyMember, memberTwo: FamilyMember) {
        if (!adjacencyList.containsKey(memberOne.ID)) {
            throw MemberNotInAdjacencyListException(memberOne)
        }
        if (!adjacencyList.containsKey(memberTwo.ID)) {
            throw MemberNotInAdjacencyListException(memberTwo)
        }
    }

    /**
     * Validates whether the specified family members can be connected by a marriage relationship.
     *
     * This method ensures that neither of the provided family members already has an existing
     * marriage relationship before a new marriage connection is established. Specifically:
     * - Checks that [memberOne] does not already have a [MARRIAGE] connection.
     * - Checks that [memberTwo] does not already have a [MARRIAGE] connection.
     * - Ensures that both [memberOne] and [memberTwo] are of opposite genders.
     *   A same-gender marriage is not allowed.
     *
     * If either family member already has a spouse, or if both members are of the same gender, an
     * [InvalidRelationshipException] is thrown.
     *
     * @param memberOne the first family member in the marriage relationship.
     * @param memberTwo the second family member in the marriage relationship.
     * @throws InvalidRelationshipException if either [memberOne] or [memberTwo] is already married,
     *                                      or if both members are of the same gender (same-gender marriages are not allowed).
     */
    @Throws(InvalidRelationshipException::class)
    private fun validateMarriage(memberOne: FamilyMember, memberTwo: FamilyMember) {
        validateNotMoreThanOneMemberConnection(memberOne, Realations.MARRIAGE)
        validateNotMoreThanOneMemberConnection(memberTwo, Realations.MARRIAGE)

        // Check if both members are the same gender for marriage
        if (memberOne.gender == memberTwo.gender) {
            throw InvalidRelationshipException("Same-gender marriages are not allowed.")
        }
    }

    /**
     * Validates whether the specified relationship can be added for the given family member.
     *
     * This method ensures that a family member does not already have a conflicting relationship
     * before a new connection is established. Specifically, it checks for the following constraints:
     * - A family member cannot have more than one [FATHER] relationship.
     * - A family member cannot have more than one [MOTHER] relationship.
     * - A family member cannot have more than one [MARRIAGE] relationship.
     *
     * If the specified relationship is already present for the given member, an exception is thrown.
     *
     * @param member the family member whose connections are being validated.
     * @param relationship the type of relationship to validate (e.g., [FATHER], [MOTHER], [MARRIAGE]).
     * @throws InvalidRelationshipException if the family member already has the specified relationship.
     */
    @Throws(InvalidRelationshipException::class)
    private fun validateNotMoreThanOneMemberConnection(member: FamilyMember, relationship: Realations) {
        val connections = adjacencyList[member.ID] ?: return

        for (connection in connections) {
            if (relationship == connection.relationship) {
                throw InvalidRelationshipException(member, relationship)
            }
        }
    }

    /**
     * Adds a marriage connection between two family members.
     *
     * This method establishes a marriage relationship between [memberOne] and [memberTwo],
     * ensuring that neither member is already married before proceeding. If either member is already
     * married, an exception will be thrown. Upon successful validation, the marriage relationship will
     * be added in both directions: [memberOne] will be added as the spouse of [memberTwo], and vice versa.
     *
     * Steps:
     * - Validates that neither [memberOne] nor [memberTwo] is already married.
     * - Creates a marriage connection where [memberOne] is added as the spouse of [memberTwo].
     * - Creates a marriage connection where [memberTwo] is added as the spouse of [memberOne].
     *
     * @param memberOne the first family member involved in the marriage.
     * @param memberTwo the second family member involved in the marriage.
     * @throws InvalidRelationshipException if either [memberOne] or [memberTwo] is already married.
     */
    @Throws(InvalidRelationshipException::class)
    private fun addMarriageConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
        validateMarriage(memberOne, memberTwo)

        // Add memberOne as the spouse of memberTwo
        adjacencyList[memberTwo.ID]?.add(Connection(memberOne, Realations.MARRIAGE))

        // Add memberTwo as the spouse of memberOne
        adjacencyList[memberOne.ID]?.add(Connection(memberTwo, Realations.MARRIAGE))
    }

    /**
     * Adds a parent-child relationship between two family members in the adjacency list.
     *
     * This method ensures a valid parent-child relationship. It first validates that the child
     * does not already have a conflicting relationship (e.g., multiple fathers or mothers).
     * Then, it adds the relationship to the adjacency list with the appropriate roles:
     * "FATHER" or "MOTHER" for the parent and "SON" or "DAUGHTER" for the child based on
     * the child's gender.
     *
     * Steps performed:
     * - Validates that the child does not already have a conflicting relationship.
     * - Creates the relationship from the parent's perspective ("FATHER" or "MOTHER").
     * - Creates the relationship from the child's perspective ("SON" or "DAUGHTER").
     *
     * @param parent the family member representing the parent.
     * @param child the family member representing the child.
     * @param parentRelation the relationship type from the parent's perspective ("FATHER" or "MOTHER").
     * @throws InvalidRelationshipException if the child already has a conflicting relationship (e.g., multiple fathers or mothers).
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as mother).
     */
    @Throws(InvalidRelationshipException::class, InvalidGenderRoleException::class)
    private fun addParentChildConnection(
        parent: FamilyMember,
        child: FamilyMember,
        parentRelation: Realations
    ) {
        // Determine child's role based on gender
        val childRelation = if (child.gender) Realations.SON else Realations.DAUGHTER

        // Validate gender roles
        validateGenderRole(parent, parentRelation) // Validate parent's role

        // Validate parent-child relationship
        validateNotMoreThanOneMemberConnection(child, parentRelation)

        // Add parent-child relationship
        adjacencyList[child.ID]?.add(Connection(parent, parentRelation))

        // Add child-parent relationship
        adjacencyList[parent.ID]?.add(Connection(child, childRelation))
    }

    /**
     * Adds a child-parent relationship between two family members in the adjacency list.
     *
     * This method ensures a valid child-parent relationship. It validates that the child
     * does not already have a conflicting relationship (e.g., multiple fathers or mothers).
     * Then, it adds the relationship to the adjacency list with the appropriate roles:
     * "FATHER" or "MOTHER" for the parent and "SON" or "DAUGHTER" for the child based on
     * the child's gender.
     *
     * Steps performed:
     * - Determines the parent’s relationship type based on gender ("FATHER" for male, "MOTHER" for female).
     * - Validates that the child does not already have a conflicting relationship with another member.
     * - Creates the relationship from the parent's perspective ("FATHER" or "MOTHER").
     * - Creates the relationship from the child's perspective ("SON" or "DAUGHTER").
     *
     * @param child the family member representing the child.
     * @param parent the family member representing the parent.
     * @param childRelation the relationship type from the child's perspective ("SON" or "DAUGHTER").
     * @throws InvalidRelationshipException if the child already has a conflicting relationship (e.g., multiple fathers or mothers).
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as mother).
     */
    @Throws(InvalidRelationshipException::class, InvalidGenderRoleException::class)
    private fun addChildParentConnection(
        child: FamilyMember,
        parent: FamilyMember,
        childRelation: Realations
    ) {
        // Determine parent's role based on gender
        val parentRelation = if (parent.gender) Realations.FATHER else Realations.MOTHER

        // Validate gender roles
        validateGenderRole(child, childRelation) // Validate child's role

        // Validate parent relationship
        validateNotMoreThanOneMemberConnection(child, parentRelation)

        // Add child-parent relationship
        adjacencyList[child.ID]?.add(Connection(parent, parentRelation))

        // Add parent-child relationship
        adjacencyList[parent.ID]?.add(Connection(child, childRelation))
    }

    /**
     * Adds a grandparent-grandchild relationship between two family members in the adjacency list.
     *
     * This method adds the relationship to the adjacency list with the appropriate roles: "GRANDFATHER" or "GRANDMOTHER"
     * for the grandparent and "GRANDSON" or "GRANDDAUGHTER" for the grandchild based on the grandchild's gender.
     *
     * Steps performed:
     * - Creates the relationship from the grandparent's perspective ("GRANDFATHER" or "GRANDMOTHER").
     * - Creates the relationship from the grandchild's perspective ("GRANDSON" or "GRANDDAUGHTER").
     *
     * @param grandparent the family member representing the grandparent.
     * @param grandchild the family member representing the grandchild.
     * @param grandparentRelation the relationship type from the grandparent's perspective ("GRANDFATHER" or "GRANDMOTHER").
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as grandmother).
     */
    @Throws(InvalidGenderRoleException::class)
    private fun addGrandparentGrandchildConnection(
        grandparent: FamilyMember,
        grandchild: FamilyMember,
        grandparentRelation: Realations
    ) {
        // Determine grandchild's role based on gender
        val grandchildRelation = if (grandchild.gender) Realations.GRANDSON else Realations.GRANDDAUGHTER

        // Validate gender roles
        validateGenderRole(grandparent, grandparentRelation) // Validate grandparent's role

        // Add grandparent-grandchild relationship
        adjacencyList[grandchild.ID]?.add(Connection(grandparent, grandparentRelation))

        // Add grandchild-grandparent relationship
        adjacencyList[grandparent.ID]?.add(Connection(grandchild, grandchildRelation))
    }


    /**
     * Adds a grandchild-grandparent relationship between two family members in the adjacency list.
     *
     * This method adds the relationship to the adjacency list with the appropriate roles: "GRANDFATHER" or "GRANDMOTHER"
     * for the grandparent and "GRANDSON" or "GRANDDAUGHTER" for the grandchild based on the grandchild's gender.
     *
     * Steps performed:
     * - Determines the grandparent's relationship type based on gender ("GRANDFATHER" for male, "GRANDMOTHER" for female).
     * - Creates the relationship from the grandparent's perspective ("GRANDFATHER" or "GRANDMOTHER").
     * - Creates the relationship from the grandchild's perspective ("GRANDSON" or "GRANDDAUGHTER").
     *
     * @param grandchild the family member representing the grandchild.
     * @param grandparent the family member representing the grandparent.
     * @param grandchildRelation the relationship type from the grandchild's perspective ("GRANDSON" or "GRANDDAUGHTER").
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as grandmother).
     */
    @Throws(InvalidGenderRoleException::class)
    private fun addGrandchildGrandparentConnection(
        grandchild: FamilyMember,
        grandparent: FamilyMember,
        grandchildRelation: Realations
    ) {
        // Determine grandparent's role based on gender
        val grandparentRelation = if (grandparent.gender) Realations.GRANDFATHER else Realations.GRANDMOTHER

        // Validate gender roles
        validateGenderRole(grandchild, grandchildRelation) // Validate grandchild's role

        // Add grandchild-grandparent relationship
        adjacencyList[grandchild.ID]?.add(Connection(grandparent, grandparentRelation))

        // Add grandparent-grandchild relationship
        adjacencyList[grandparent.ID]?.add(Connection(grandchild, grandchildRelation))
    }

    /**
     * Adds a cousins relationship between two family members in the adjacency list.
     *
     * This method creates a bidirectional cousin connection between two family members,
     * indicating that both members are cousins of each other. The relationship type is
     * represented as "COUSINS".
     *
     * Steps performed:
     * - Adds the cousin connection from the perspective of the first member to the second.
     * - Adds the cousin connection from the perspective of the second member to the first.
     *
     * @param memberOne the first family member in the cousins relationship.
     * @param memberTwo the second family member in the cousins relationship.
     */
    private fun addCousinsConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
        adjacencyList[memberOne.ID]?.add(Connection(memberTwo, Realations.COUSINS))
        adjacencyList[memberTwo.ID]?.add(Connection(memberOne, Realations.COUSINS))
    }

    /**
     * Adds a sibling relationship between two family members in the adjacency list.
     *
     * This method creates a bidirectional sibling connection between two family members,
     * indicating that both members are siblings of each other. The relationship type is
     * represented as "SIBLINGS".
     *
     * Steps performed:
     * - Adds the sibling connection from the perspective of the first member to the second.
     * - Adds the sibling connection from the perspective of the second member to the first.
     *
     * @param memberOne the first family member in the sibling relationship.
     * @param memberTwo the second family member in the sibling relationship.
     */
    private fun addSiblingConnection(memberOne: FamilyMember, memberTwo: FamilyMember) {
        adjacencyList[memberOne.ID]?.add(Connection(memberTwo, Realations.SIBLINGS))
        adjacencyList[memberTwo.ID]?.add(Connection(memberOne, Realations.SIBLINGS))
    }

    /**
     * Adds a connection between two family members in the adjacency list based on the specified relationship.
     *
     * @param memberOne  The first family member in the relationship.
     * @param memberTwo  The second family member in the relationship.
     * @param relation   The type of relationship between the two family members.
     *                  Supported relationships include:
     *                  - MARRIAGE
     *                  - FATHER
     *                  - MOTHER
     *                  - SON
     *                  - DAUGHTER
     *                  - GRANDMOTHER
     *                  - GRANDFATHER
     *                  - GRANDDAUGHTER
     *                  - GRANDSON
     *                  - COUSINS
     *                  - SIBLINGS
     *
     * @throws MemberNotInAdjacencyListException If one or both of the members are not present in the adjacency list.
     * @throws InvalidRelationshipException      If the specified relationship is invalid or conflicts with existing relationships.
     * @throws InvalidGenderRoleException        If the gender roles for the specified relationship are invalid (e.g., assigning "MOTHER" to a male member).
     */
    fun addConnectionToAdjacencyList(
        memberOne: FamilyMember,
        memberTwo: FamilyMember,
        relation: Realations
    ) {
        // Validate both members
        validateMembersExist(memberOne, memberTwo)

        when (relation) {
            Realations.MARRIAGE -> addMarriageConnection(memberOne, memberTwo)
            Realations.FATHER -> addParentChildConnection(memberOne, memberTwo, Realations.FATHER)
            Realations.MOTHER -> addParentChildConnection(memberOne, memberTwo, Realations.MOTHER)
            Realations.SON -> addChildParentConnection(memberOne, memberTwo, Realations.SON)
            Realations.DAUGHTER -> addChildParentConnection(memberOne, memberTwo, Realations.DAUGHTER)
            Realations.GRANDMOTHER -> addGrandparentGrandchildConnection(memberOne, memberTwo, Realations.GRANDMOTHER)
            Realations.GRANDFATHER -> addGrandparentGrandchildConnection(memberOne, memberTwo, Realations.GRANDFATHER)
            Realations.GRANDDAUGHTER -> addGrandchildGrandparentConnection(memberOne, memberTwo, Realations.GRANDDAUGHTER)
            Realations.GRANDSON -> addGrandchildGrandparentConnection(memberOne, memberTwo, Realations.GRANDSON)
            Realations.COUSINS -> addCousinsConnection(memberOne, memberTwo)
            Realations.SIBLINGS -> addSiblingConnection(memberOne, memberTwo)
        }
    }

    /**
     * Retrieves the list of connections associated with a specific family member.
     *
     * This function returns a list of [Connection] objects representing the relationships
     * that the family member with the given `memberId` has with other members in the
     * family tree.
     *
     * @param memberId The unique identifier of the family member whose connections
     *                 are to be retrieved.
     *
     * @return A list of [Connection] objects representing the connections for the
     *         specified member. If the member is not found in the adjacency list,
     *         `null` is returned.
     */
    fun getConnectionsForMember(memberId: Int): List<Connection>? {
        return adjacencyList[memberId]
    }

    /**
     * Retrieves a read-only view of the adjacency list representing the family connections.
     *
     * This method provides controlled access to the internal `adjacencyList` property,
     * ensuring encapsulation while allowing external code to obtain the connections
     * between family members in a safe manner. Each key in the returned map represents
     * a member's ID, and the associated value is a list of `Connection` objects
     * detailing their relationships.
     *
     * @return A map where the keys are member IDs (Int) and the values are lists of
     *         `Connection` objects, representing the relationships for each member.
     */
    fun getAdjacencyList(): Map<Int, List<Connection>> {
        return adjacencyList.mapValues { it.value.toList() } // Return a read-only copy
    }

    /**
     * Updates or sets the connections for a specific family member in the adjacency list.
     *
     * This function safely modifies the private `adjacencyList` by replacing the list of connections
     * for the specified family member with the provided list. The input list is converted to a mutable
     * list to maintain the flexibility of the adjacency list structure.
     *
     * This function is intended to be used by the `FirebaseManager` for managing the connections
     * of family members. When adding a new member to the adjacency list, use the function
     * `addConnectionToAdjacencyList` instead.
     *
     * Notes:
     *
     * - This function replaces any existing connections for the given `memberId`.
     * - Ensure the `connectionsForMember` list is constructed correctly, as invalid data can lead
     *   to incorrect family relationship mappings.
     *
     * @param memberId The unique identifier of the family member whose connections are to be updated.
     * @param connectionsForMember A list of `Connection` objects representing the new connections
     *                              for the specified family member.
     */
    fun setConnectionsForMember(memberId: Int, connectionsForMember: List<Connection>) {
        adjacencyList[memberId] = connectionsForMember.toMutableList()
    }
}
