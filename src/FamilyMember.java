
public class FamilyMember {

    private String firstName;
    private String lastName;
    private boolean gender;
    private final int ID;


    public FamilyMember(String firstName, String lastName, boolean gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.ID = MemberDataBase.getInstance().getNewID();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean getGender() { return gender;}

    public String getFullName() { return firstName + " " + lastName;
    }

    public int getID() {
        return ID;
    }






}





