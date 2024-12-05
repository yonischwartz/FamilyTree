/**
 * Singleton class to manage the mapping of family members by unique IDs.
 * <p>
 * This class maintains a mapping between unique integer IDs and their
 * corresponding {@code FamilyMember} objects. It ensures that each family
 * member is assigned a unique ID and provides methods for accessing and
 * updating this mapping.
 * </p>
 * <p>
 * The singleton pattern is used to ensure a single instance of this class
 * throughout the application.
 * </p>
 */
public class MemberDataBase {

    // ***********************************************************************************

    // singleton constructor

    // Static variable to hold the single instance of MemberDataBase
    private static MemberDataBase instance;

    // Private constructor to prevent instantiation from outside
    private MemberDataBase() {}

    // Public method to provide access to the single instance
    public static MemberDataBase getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new MemberDataBase();
        return instance;
    }

    // ***********************************************************************************

    // A field that holds a map of all the family members in the tree
    private final MemberMapByID idaMap = MemberMapByID.getInstance();

    // A field that holds the adjacency list, which represents the family connections
    private final FamilyConnections familyConnections = FamilyConnections.getInstance();

    public void addNewMemberToTree(FamilyMember familyMember) {
        idaMap.addNewMemberToIDMap(familyMember);
        familyConnections.addNewMemberToAdjacencyList(familyMember);
    }

    /**
     * Connects two family members in the tree, establishing their relationship.
     *
     * <p>The connection must describe the relationship from the perspective of the
     * first member. For example, if the first member is the son of the second member,
     * the connection should be {@code SON}, not {@code FATHER}.</p>
     *
     * <p>Steps performed:</p>
     * <ul>
     *     <li>Checks if both members are already part of the tree.</li>
     *     <li>Throws an exception if either member is not part of the tree.</li>
     *     <li>Establishes the relationship between the two members in the adjacency list.</li>
     * </ul>
     *
     * @param memberOne the family member initiating the connection.
     * @param memberTwo the family member to which the connection is being established.
     * @param relation  the type of connection describing the relationship from
     *                  the perspective of {@code memberOne} (e.g., SON, DAUGHTER, etc.).
     *
     * @throws MemberNotInTreeException if either {@code memberOne} or {@code memberTwo}
     *                                  is not already part of the tree.
     */
    private void connectMemberToMember (FamilyMember memberOne,
                                        FamilyMember memberTwo,
                                        Realations relation)
            throws  MemberNotInTreeException,
                    InvalidRelationshipException,
                    InvalidGenderRoleException,
                    MemberNotInAdjacencyListException {

        if (!idaMap.isMemberInTree(memberOne) || !idaMap.isMemberInTree(memberTwo)) {
            throw new MemberNotInTreeException();
        }

        familyConnections.addConnectionToAdjacencyList(memberOne, memberTwo, relation);
    }

    // Public method that provides a new ID to a member, the user wants to add to the tree
    public int getNewID() {
        return idaMap.getNewID();
    }

    public boolean CheckIfNameExists (String firstName, String lastName) {

        // TODO: write this function
        return true;
    }

    public FamilyMember getMemberByName(String firstName, String lastName) {
        // TODO: write this function

        // for now
        return new FamilyMember("", "", false);

    }

}