/**
 * Exception thrown when a family member is assigned a relationship role that contradicts their gender.
 *
 * <p>This exception is triggered when an invalid relationship role is attempted to be established, such as:
 * <ul>
 *     <li>Assigning a female as a "SON".</li>
 *     <li>Assigning a male as a "MOTHER".</li>
 *     <li>Other similar gender-role mismatches.</li>
 * </ul>
 * </p>
 */
public class InvalidGenderRoleException extends Exception {
    private final FamilyMember member;
    private final Realations invalidRole;

    /**
     * Constructs an InvalidGenderRoleException.
     *
     * @param member the family member who was incorrectly assigned the invalid role.
     * @param invalidRole the invalid role (e.g., SON, MOTHER) that was attempted to be assigned.
     */
    public InvalidGenderRoleException(FamilyMember member, Realations invalidRole) {
        super(String.format("Invalid gender-role assignment: Member '%s' (gender: %s) cannot be assigned role '%s'.",
                member.getFullName(), member.getGender() ? "Male" : "Female", invalidRole));
        this.member = member;
        this.invalidRole = invalidRole;
    }

//    /**
//     * Retrieves the family member associated with this exception.
//     *
//     * @return the family member who was incorrectly assigned the invalid role.
//     */
//    public FamilyMember getMember() {
//        return member;
//    }
//
//    /**
//     * Retrieves the invalid role that was attempted to be assigned.
//     *
//     * @return the invalid role (e.g., SON, MOTHER) that was attempted.
//     */
//    public Realations getInvalidRole() {
//        return invalidRole;
//    }


}
