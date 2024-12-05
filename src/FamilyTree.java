
public class FamilyTree {

    private final MemberDataBase memberDataBase = MemberDataBase.getInstance();

    /**
     * Adds the first "yeshiva" family member to the family tree.
     *
     * <p>This method is a one-time function specifically designed to add the initial "yeshiva" family member
     * to the family tree. It creates a new {@code YeshivaFamilyMember} instance with the provided details
     * and registers it in the member database.</p>
     *
     * <p>This function should only be used during the initial setup of the family tree and is not intended
     * for adding subsequent family members.</p>
     *
     * @param firstName the first name of the "yeshiva" family member.
     * @param lastName  the last name of the "yeshiva" family member.
     * @param gender    the gender of the "yeshiva" family member (true for male, false for female).
     * @param machzor   the machzor (cycle or group) associated with the "yeshiva" family member.
     */
    public void addFirstMemberToTreeString (String firstName,
                                            String lastName,
                                            boolean gender,
                                            int machzor) {

        YeshivaFamilyMember firstYeshivaFamilyMember =
                new YeshivaFamilyMember(firstName,lastName, gender, machzor);

        memberDataBase.addNewMemberToTree(firstYeshivaFamilyMember);
    }
    
    public void addNewYeshivaFamilyMember (String firstName,
                                           String lastName,
                                           boolean gender,
                                           int machzor) {

        YeshivaFamilyMember NewYeshivaFamilyMember =
                new YeshivaFamilyMember(firstName,lastName, gender, machzor);

        memberDataBase.addNewMemberToTree(NewYeshivaFamilyMember);
    }
}
