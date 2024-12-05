import java.util.*;

/**
 * The {@code FamilyConnections} class manages the relationships between family members
 * using an adjacency list representation implemented as a map.
 * This class allows users to find and manage the connections associated with
 * a specific family member.
 *
 * <p>This class follows the Singleton design pattern to ensure that there is only one
 * instance managing all family connections across the application.</p>
 *
 */

public class FamilyConnections {

    // ***********************************************************************************

    // singleton constructor

    // Static variable to hold the single instance of FamilyConnections
    private static FamilyConnections instance;

    // Private constructor to prevent instantiation from outside
    private FamilyConnections() {}

    // Public method to provide access to the single instance
    public static FamilyConnections getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new FamilyConnections();
        return instance;
    }

    // ***********************************************************************************

    // Map to store family connections (adjacency list)
    private static final Map<Integer, List<Connection>> adjacencyList = new HashMap<>();

    /**
     * A map storing the expected gender for each relationship type.
     * The key is the relationship,and the value is the expected gender:
     * {@code true} means male, {@code false} means female.
     */
    private static final Map<Realations, Boolean> relationshipGenderMap;

    static {
        // Initialize the map only once
        relationshipGenderMap = new HashMap<>();
        relationshipGenderMap.put(Realations.FATHER, true);          // FATHER should be male
        relationshipGenderMap.put(Realations.MOTHER, false);         // MOTHER should be female
        relationshipGenderMap.put(Realations.SON, true);             // SON should be male
        relationshipGenderMap.put(Realations.DAUGHTER, false);       // DAUGHTER should be female
        relationshipGenderMap.put(Realations.GRANDMOTHER, false);    // GRANDMOTHER should be female
        relationshipGenderMap.put(Realations.GRANDFATHER, true);     // GRANDFATHER should be male
        relationshipGenderMap.put(Realations.GRANDSON, true);        // GRANDSON should be male
        relationshipGenderMap.put(Realations.GRANDDAUGHTER, false);  // GRANDDAUGHTER should be female
    }

    public void addNewMemberToAdjacencyList(FamilyMember familyMember) {
        adjacencyList.put(familyMember.getID(), new ArrayList<>());
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
    private void validateGenderRole(FamilyMember member, Realations relationship)
            throws InvalidGenderRoleException {
        // Check if the relationship exists in the map and get the expected gender
        Boolean expectedGender = relationshipGenderMap.get(relationship);

        // If the relationship exists and the gender doesn't match, throw an exception
        if (expectedGender != null && member.getGender() != expectedGender) {
            throw new InvalidGenderRoleException(member, relationship);
        }
    }

    /**
     * Validates that both specified family members exist in the adjacency list.
     *
     * <p>This method checks if the IDs of {@code memberOne} and {@code memberTwo}
     * are present in the {@code adjacencyList}. If either member is not found,
     * a {@link MemberNotInAdjacencyListException} is thrown, identifying the
     * member that is missing.</p>
     *
     * @param memberOne the first family member to validate.
     * @param memberTwo the second family member to validate.
     * @throws MemberNotInAdjacencyListException if either {@code memberOne} or
     *         {@code memberTwo} is not found in the adjacency list.
     */
    private void validateMembersExist(FamilyMember memberOne, FamilyMember memberTwo)
            throws MemberNotInAdjacencyListException {
        if (!adjacencyList.containsKey(memberOne.getID())) {
            throw new MemberNotInAdjacencyListException(memberOne);
        }
        if (!adjacencyList.containsKey(memberTwo.getID())) {
            throw new MemberNotInAdjacencyListException(memberTwo);
        }
    }

    /**
     * Validates whether the specified family members can be connected by a marriage relationship.
     *
     * <p>This method ensures that neither of the provided family members already has an existing
     * marriage relationship before a new marriage connection is established. Specifically:
     * <ul>
     *     <li>Checks that {@code memberOne} does not already have a {@code MARRIAGE} connection.</li>
     *     <li>Checks that {@code memberTwo} does not already have a {@code MARRIAGE} connection.</li>
     *     <li>Ensures that both {@code memberOne} and {@code memberTwo} are of opposite genders.
     *     A same-gender marriage is not allowed.</li>
     * </ul>
     * If either family member already has a spouse, or if both members are of the same gender, an
     * {@code InvalidRelationshipException} is thrown.
     *
     * @param memberOne the first family member in the marriage relationship.
     * @param memberTwo the second family member in the marriage relationship.
     * @throws InvalidRelationshipException if either {@code memberOne} or {@code memberTwo} is already married,
     *                                      or if both members are of the same gender (same-gender marriages are not allowed).
     */
    private void validateMarriage(FamilyMember memberOne, FamilyMember memberTwo)
            throws InvalidRelationshipException {
        validateNotMoreThanOneMemberConnection(memberOne, Realations.MARRIAGE);
        validateNotMoreThanOneMemberConnection(memberTwo, Realations.MARRIAGE);

        // Check if both members are the same gender for marriage
        if (memberOne.getGender() == memberTwo.getGender()) {
            throw new InvalidRelationshipException("Same-gender marriages are not allowed.");
        }
    }

    /**
     * Validates whether the specified relationship can be added for the given family member.
     *
     * <p>This method ensures that a family member does not already have a conflicting relationship
     * before a new connection is established. Specifically, it checks for the following constraints:
     * <ul>
     *     <li>A family member cannot have more than one {@code FATHER} relationship.</li>
     *     <li>A family member cannot have more than one {@code MOTHER} relationship.</li>
     *     <li>A family member cannot have more than one {@code MARRIAGE} relationship.</li>
     * </ul>
     * If the specified relationship is already present for the given member, an exception is thrown.
     *
     * @param member      the family member whose connections are being validated.
     * @param relationship the type of relationship to validate (e.g., {@code FATHER}, {@code MOTHER}, {@code MARRIAGE}).
     * @throws InvalidRelationshipException if the family member already has the specified relationship.
     */
    private void validateNotMoreThanOneMemberConnection(FamilyMember member, Realations relationship)
            throws InvalidRelationshipException {

        for (Connection connection : adjacencyList.get(member.getID())) {
            if (relationship == connection.getRelationship()) {
                throw new InvalidRelationshipException(member, relationship);
            }
        }
    }

    /**
     * Adds a marriage connection between two family members.
     *
     * <p>This method establishes a marriage relationship between {@code memberOne} and {@code memberTwo},
     * ensuring that neither member is already married before proceeding. If either member is already
     * married, an exception will be thrown. Upon successful validation, the marriage relationship will
     * be added in both directions: {@code memberOne} will be added as the spouse of {@code memberTwo},
     * and vice versa.</p>
     *
     * <p>The following steps are carried out:
     * <ul>
     *     <li>Validates that neither {@code memberOne} nor {@code memberTwo} is already married.</li>
     *     <li>Creates a marriage connection where {@code memberOne} is added as the spouse of {@code memberTwo}.</li>
     *     <li>Creates a marriage connection where {@code memberTwo} is added as the spouse of {@code memberOne}.</li>
     * </ul>
     *
     * @param memberOne the first family member involved in the marriage.
     * @param memberTwo the second family member involved in the marriage.
     * @throws InvalidRelationshipException if either {@code memberOne} or {@code memberTwo} is already married.
     */
    private void addMarriageConnection(FamilyMember memberOne, FamilyMember memberTwo)
            throws InvalidRelationshipException {
        validateMarriage(memberOne, memberTwo);

        // Add memberOne as the spouse of memberTwo
        adjacencyList.get(memberTwo.getID())
                .add(new Connection(memberOne, Realations.MARRIAGE));

        // Add memberTwo as the spouse of memberOne
        adjacencyList.get(memberOne.getID())
                .add(new Connection(memberTwo, Realations.MARRIAGE));
    }

    /**
     * Adds a parent-child relationship between two family members in the adjacency list.
     *
     * <p>This method ensures a valid parent-child relationship. It first validates that the child
     * does not already have a conflicting relationship (e.g., multiple fathers or mothers).
     * Then, it adds the relationship to the adjacency list with the appropriate roles:
     * "FATHER" or "MOTHER" for the parent and "SON" or "DAUGHTER" for the child based on
     * the child's gender.</p>
     *
     * <p>Steps performed:</p>
     * <ul>
     *     <li>Validates that the child does not already have a conflicting relationship.</li>
     *     <li>Creates the relationship from the parent's perspective ("FATHER" or "MOTHER").</li>
     *     <li>Creates the relationship from the child's perspective ("SON" or "DAUGHTER").</li>
     * </ul>
     *
     * @param parent        the family member representing the parent.
     * @param child         the family member representing the child.
     * @param parentRelation the relationship type from the parent's perspective ("FATHER" or "MOTHER").
     * @throws InvalidRelationshipException if the child already has a conflicting relationship (e.g., multiple fathers or mothers).
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as mother).
     */
    private void addParentChildConnection(FamilyMember parent,
                                          FamilyMember child,
                                          Realations parentRelation)
            throws InvalidRelationshipException, InvalidGenderRoleException {

        // Determine child's role based on gender
        Realations childRelation = child.getGender() ? Realations.SON : Realations.DAUGHTER;

        // Validate gender roles
        validateGenderRole(parent, parentRelation);  // Validate parent's role

        // Validate parent-child relationship
        validateNotMoreThanOneMemberConnection(child, parentRelation);

        // Add parent-child relationship
        adjacencyList.get(child.getID())
                .add(new Connection(parent, parentRelation));

        // Add child-parent relationship
        adjacencyList.get(parent.getID())
                .add(new Connection(child, childRelation));
    }

    /**
     * Adds a child-parent relationship between two family members in the adjacency list.
     *
     * <p>This method ensures a valid child-parent relationship. It validates that the child
     * does not already have a conflicting relationship (e.g., multiple fathers or mothers).
     * Then, it adds the relationship to the adjacency list with the appropriate roles:
     * "FATHER" or "MOTHER" for the parent and "SON" or "DAUGHTER" for the child based on
     * the child's gender.</p>
     *
     * <p>The following steps are performed:</p>
     * <ul>
     *     <li>Determines the parent’s relationship type based on gender ("FATHER" for male, "MOTHER" for female).</li>
     *     <li>Validates that the child does not already have a conflicting relationship with another member.</li>
     *     <li>Creates the relationship from the parent's perspective ("FATHER" or "MOTHER").</li>
     *     <li>Creates the relationship from the child's perspective ("SON" or "DAUGHTER").</li>
     * </ul>
     *
     * @param child         the family member representing the child.
     * @param parent        the family member representing the parent.
     * @param childRelation the relationship type from the child's perspective ("SON" or "DAUGHTER").
     * @throws InvalidRelationshipException if the child already has a conflicting relationship (e.g., multiple fathers or mothers).
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as mother).
     */
    private void addChildParentConnection(FamilyMember child,
                                          FamilyMember parent,
                                          Realations childRelation)
            throws InvalidRelationshipException, InvalidGenderRoleException {

        // Determine parent's role based on gender
        Realations parentRelation = parent.getGender() ? Realations.FATHER : Realations.MOTHER;

        // Validate gender roles
        validateGenderRole(child, childRelation);     // Validate child's role

        // Validate parent relationship
        validateNotMoreThanOneMemberConnection(child, parentRelation);

        // Add child-parent relationship
        adjacencyList.get(child.getID())
                .add(new Connection(parent, parentRelation));

        // Add parent-child relationship
        adjacencyList.get(parent.getID())
                .add(new Connection(child, childRelation));
    }

    /**
     * Adds a grandparent-grandchild relationship between two family members in the adjacency list.
     *
     * <p>This method adds the relationship to the adjacency list with the appropriate roles: "GRANDFATHER" or "GRANDMOTHER"
     * for the grandparent and "GRANDSON" or "GRANDDAUGHTER" for the grandchild based on the grandchild's gender.</p>
     *
     * <p>Steps performed:</p>
     * <ul>
     *     <li>Creates the relationship from the grandparent's perspective ("GRANDFATHER" or "GRANDMOTHER").</li>
     *     <li>Creates the relationship from the grandchild's perspective ("GRANDSON" or "GRANDDAUGHTER").</li>
     * </ul>
     *
     * @param grandparent       the family member representing the grandparent.
     * @param grandchild        the family member representing the grandchild.
     * @param grandparentRelation the relationship type from the grandparent's perspective ("GRANDFATHER" or "GRANDMOTHER").
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as grandmother).
     */
    private void addGrandparentGrandchildConnection(FamilyMember grandparent,
                                                    FamilyMember grandchild,
                                                    Realations grandparentRelation)
            throws InvalidGenderRoleException {

        // Determine grandchild's role based on gender
        Realations grandchildRelation = grandchild.getGender() ? Realations.GRANDSON : Realations.GRANDDAUGHTER;

        // Validate gender roles
        validateGenderRole(grandparent, grandparentRelation);  // Validate grandparent's role

        // Add grandparent-grandchild relationship
        adjacencyList.get(grandchild.getID())
                .add(new Connection(grandparent, grandparentRelation));

        // Add grandchild-grandparent relationship
        adjacencyList.get(grandparent.getID())
                .add(new Connection(grandchild, grandchildRelation));
    }

    /**
     * Adds a grandchild-grandparent relationship between two family members in the adjacency list.
     *
     * <p>This method adds the relationship to the adjacency list with the appropriate roles: "GRANDFATHER" or "GRANDMOTHER"
     * for the grandparent and "GRANDSON" or "GRANDDAUGHTER" for the grandchild based on the grandchild's gender.</p>
     *
     * <p>The following steps are performed:</p>
     * <ul>
     *     <li>Determines the grandparent’s relationship type based on gender ("GRANDFATHER" for male, "GRANDMOTHER" for female).</li>
     *     <li>Creates the relationship from the grandparent's perspective ("GRANDFATHER" or "GRANDMOTHER").</li>
     *     <li>Creates the relationship from the grandchild's perspective ("GRANDSON" or "GRANDDAUGHTER").</li>
     * </ul>
     *
     * @param grandchild        the family member representing the grandchild.
     * @param grandparent       the family member representing the grandparent.
     * @param grandchildRelation the relationship type from the grandchild's perspective ("GRANDSON" or "GRANDDAUGHTER").
     * @throws InvalidGenderRoleException if the relationship is inconsistent with the family member's gender (e.g., male as grandmother).
     */
    private void addGrandchildGrandparentConnection(FamilyMember grandchild,
                                                    FamilyMember grandparent,
                                                    Realations grandchildRelation)
            throws InvalidGenderRoleException {

        // Determine grandparent's role based on gender
        Realations grandparentRelation = grandparent.getGender() ? Realations.GRANDFATHER : Realations.GRANDMOTHER;

        // Validate gender roles
        validateGenderRole(grandchild, grandchildRelation);     // Validate grandchild's role

        // Add grandchild-grandparent relationship
        adjacencyList.get(grandchild.getID())
                .add(new Connection(grandparent, grandparentRelation));

        // Add grandparent-grandchild relationship
        adjacencyList.get(grandparent.getID())
                .add(new Connection(grandchild, grandchildRelation));
    }

    /**
     * Adds a cousins relationship between two family members in the adjacency list.
     *
     * <p>This method creates a bidirectional cousin connection between two family members,
     * indicating that both members are cousins of each other. The relationship type is
     * represented as "COUSINS".</p>
     *
     * <p>Steps performed:</p>
     * <ul>
     *     <li>Adds the cousin connection from the perspective of the first member to the second.</li>
     *     <li>Adds the cousin connection from the perspective of the second member to the first.</li>
     * </ul>
     *
     * @param memberOne the first family member in the cousins relationship.
     * @param memberTwo the second family member in the cousins relationship.
     */
    private void addCousinsConnection(FamilyMember memberOne, FamilyMember memberTwo) {

        adjacencyList.get(memberOne.getID())
                .add(new Connection(memberTwo, Realations.COUSINS));

        adjacencyList.get(memberTwo.getID())
                .add(new Connection(memberOne, Realations.COUSINS));
    }

    /**
     * Adds a sibling relationship between two family members in the adjacency list.
     *
     * <p>This method creates a bidirectional sibling connection between two family members,
     * indicating that both members are siblings of each other. The relationship type is
     * represented as "SIBLINGS".</p>
     *
     * <p>Steps performed:</p>
     * <ul>
     *     <li>Adds the sibling connection from the perspective of the first member to the second.</li>
     *     <li>Adds the sibling connection from the perspective of the second member to the first.</li>
     * </ul>
     *
     * @param memberOne the first family member in the sibling relationship.
     * @param memberTwo the second family member in the sibling relationship.
     */
    private void addSiblingConnection(FamilyMember memberOne, FamilyMember memberTwo) {

        adjacencyList.get(memberOne.getID())
                .add(new Connection(memberTwo, Realations.SIBLINGS));

        adjacencyList.get(memberTwo.getID())
                .add(new Connection(memberOne, Realations.SIBLINGS));
    }

    public void addConnectionToAdjacencyList   (FamilyMember memberOne,
                                                FamilyMember memberTwo,
                                                Realations relation)
                                                throws  MemberNotInAdjacencyListException,
                                                        InvalidRelationshipException,
                                                        InvalidGenderRoleException{
        // Validate both members
        validateMembersExist(memberOne, memberTwo);

        switch (relation) {

            case MARRIAGE:

                addMarriageConnection(memberOne, memberTwo);
                break;

            case FATHER:

                addParentChildConnection(memberOne, memberTwo, Realations.FATHER);
                break;

            case MOTHER:

                addParentChildConnection(memberOne, memberTwo, Realations.MOTHER);
                break;

            case SON:

                addChildParentConnection(memberOne, memberTwo, Realations.SON);
                break;

            case DAUGHTER:

                addChildParentConnection(memberOne, memberTwo, Realations.DAUGHTER);
                break;

            case GRANDMOTHER:

                addGrandparentGrandchildConnection(memberOne, memberTwo, Realations.GRANDMOTHER);
                break;

            case GRANDFATHER:

                addGrandparentGrandchildConnection(memberOne, memberTwo, Realations.GRANDFATHER);
                break;

            case GRANDDAUGHTER:

                addGrandchildGrandparentConnection(memberOne, memberTwo, Realations.GRANDDAUGHTER);
                break;

            case GRANDSON:

                addGrandchildGrandparentConnection(memberOne, memberTwo, Realations.GRANDSON);
                break;

            case COUSINS:

                addCousinsConnection(memberOne, memberTwo);
                break;

            case SIBLINGS:

                addSiblingConnection(memberOne, memberTwo);
                break;
        }
    }
}


