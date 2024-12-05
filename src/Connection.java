/**
 * The {@code Connection} class represents a relationship between a family member
 * and another member in the family tree. This class encapsulates both the family
 * member involved in the connection and the type of relationship from the perspective
 * of the owner of the connection.
 *
 * <p>In a family tree, connections are not merely links between nodes; they also
 * carry specific semantics about the type of relationship (e.g., FATHER, SON, etc.).
 * This class allows precise and organized representation of such relationships,
 * ensuring that the connections are both meaningful and easy to traverse.</p>
 *
 * <p>The {@code Connection} class is particularly useful in adjacency list
 * implementations, where each family member's connections need to be stored
 * alongside the type of relationship. By using this class, we can efficiently
 * manage and query relationships within the family tree.</p>
 *
 * <p>For example, if a family member has multiple connections (e.g., children or siblings),
 * each connection can be stored as a {@code Connection} object, making it clear
 * how the members are related without ambiguity.</p>
 */
public class Connection {
    private final FamilyMember member;
    private final Realations relationship;

    /**
     * Constructs a {@code Connection} that represents the relationship between
     * the owner of the connection and the specified family member.
     *
     * <p>The relationship is described from the perspective of the owner of the connection.
     * For example, if the owner of the connection is the father of the specified family member,
     * the relationship should be {@code FATHER}. Conversely, if the owner is the son,
     * the relationship should be {@code SON}.</p>
     *
     * @param member       the family member being connected.
     * @param relationship the type of relationship as seen from the perspective
     *                     of the owner of the connection (e.g., FATHER, SON, etc.).
     */
    public Connection(FamilyMember member, Realations relationship) {
        this.member = member;
        this.relationship = relationship;
    }

    public FamilyMember getMember() {
        return member;
    }

    public Realations getRelationship() {
        return relationship;
    }
}