/**
 * Family members who are or were related to yeshiva,
 * including students, rabbis, and staff
 */

public class YeshivaFamilyMember extends FamilyMember{


    private int machzor;

    public YeshivaFamilyMember(String firstName, String lastName, boolean gender, int machzor) {
        super(firstName, lastName, gender);
        this.machzor = machzor;
    }

    // If someone doesn't have a machzor,
    // for instance a Rabbi that didn't learn in the yeshiva
    public YeshivaFamilyMember(String firstName, String lastName, boolean gender) {
        super(firstName, lastName, gender);
        this.machzor = 0;
    }
}
