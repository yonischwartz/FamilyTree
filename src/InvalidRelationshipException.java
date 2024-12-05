/**
 * Exception thrown when a family member is assigned a relationship
 * that would result in an invalid or contradictory connection.
 *
 * <p>Examples of invalid connections include:</p>
 * <ul>
 *     <li>Assigning a second father or mother to the same member.</li>
 *     <li>Adding a second spouse when one already exists.</li>
 * </ul>
 */
public class InvalidRelationshipException extends Exception {

    /**
     * Constructs an {@code InvalidRelationshipException} with a default message.
     */
    public InvalidRelationshipException() {
        super("Invalid relationship: A family member cannot have conflicting or duplicate relationships.");
    }

    /**
     * Constructs an {@code InvalidRelationshipException} with a specific message.
     *
     * @param message the specific error message.
     */
    public InvalidRelationshipException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code InvalidRelationshipException} with a message that includes details
     * about the member and the attempted relationship.
     *
     * @param familyMember the family member for whom the invalid relationship was attempted.
     * @param connection   the conflicting or duplicate relationship that caused the error.
     */
    public InvalidRelationshipException(FamilyMember familyMember, Realations relation) {
        super("Invalid relationship for member " + familyMember.getFullName() +
                ": Cannot add relationship '" + relation +
                "' because it conflicts with an existing relationship.");
    }
}
