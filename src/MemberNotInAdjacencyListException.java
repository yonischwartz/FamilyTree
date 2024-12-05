/**
 * Exception thrown when attempting to reference a family member
 * who is not present in the adjacency list.
 */
public class MemberNotInAdjacencyListException extends Exception {

    /**
     * Default constructor for the exception.
     */
    public MemberNotInAdjacencyListException() {}

    /**
     * Helper method to generate a custom error message.
     *
     * <p>This method is marked as {@code static} because it does not rely on any
     * instance variables or behavior from the exception class. Instead, it operates
     * solely based on the input parameter ({@code memberNotInList}). This allows it
     * to be called without requiring an instance of the {@code MemberNotInAdjacencyListException} class.</p>
     *
     * @param memberNotInList the family member who is not in the adjacency list.
     * @return a descriptive error message.
     */
    private static String createMessage(FamilyMember memberNotInList) {
        return "The member " + memberNotInList.getFullName() + " is not in the adjacency list.";
    }

    /**
     * Constructor that accepts a family member and generates a specific error message.
     *
     * @param memberNotInList the family member who is not in the adjacency list.
     */
    public MemberNotInAdjacencyListException(FamilyMember memberNotInList) {
        super(createMessage(memberNotInList));
    }

    /**
     * Constructor that accepts a custom error message.
     *
     * @param message the custom error message for the exception.
     */
    public MemberNotInAdjacencyListException(String message) {
        super(message);
    }
}
