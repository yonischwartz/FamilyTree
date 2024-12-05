import java.util.HashMap;
import java.util.Map;

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

public class MemberMapByID {

    // An integer that keeps track of the last id added,
    // in order to know what id does a new member should get.
    private int lastIDAdded = 0;

    // Static variable to hold the single instance of MemberMapByID
    private static MemberMapByID instance;

    // Map to store family members by ID
    private final Map<Integer, FamilyMember> idMap;

    // Private constructor to prevent instantiation from outside
    private MemberMapByID() {
        idMap = new HashMap<>();
    }

    // Public method to provide access to the single instance
    public static MemberMapByID getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new MemberMapByID();
        return instance;
    }

    // Public method that provides a new ID to a member, the user wants to add to the tree
    public int getNewID() {
        return ++lastIDAdded;
    }

    public void addNewMemberToIDMap(FamilyMember familyMember) {
        idMap.put(familyMember.getID(), familyMember);
    }

    public boolean isMemberInTree(FamilyMember familyMember) {
        return idMap.containsKey(familyMember.getID());
    }
}


