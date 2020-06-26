package fu.is1304.dv.fptsocial.entity;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {
    private String UID;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private int course;
    private String department;
    private String avatar;
    private String cover;
    private String startDate;

    public User() {
    }

    public User(String UID, String firstName, String lastName, String gender, String dob, int course, String department, String avatar, String cover, String startDate) {
        this.UID = UID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.course = course;
        this.department = department;
        this.avatar = avatar;
        this.cover = cover;
        this.startDate = startDate;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
