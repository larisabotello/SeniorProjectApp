package com.example.laris.testregistration.HelperClasses;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by laris on 11/1/2017.
 */

public class UserInformation {
    private String username;
    private String Fullname;
    private String email;
    private String dob;

    public UserInformation(String email, String username, String Fname, String DOB){
        this.email = email;
        this.username = username;
        this.Fullname = Fname;
        this.dob = DOB;
    }
    public String getUsername(){
        return username;
    }
    public void setFirstName(String firstName) {
        firstName = firstName;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setLastName(String lastName) {
        lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = email;
    }

    public String getDOB() {
        return dob;
    }

    public void setDOB(String DOB) {
        this.dob = DOB;
    }

}
