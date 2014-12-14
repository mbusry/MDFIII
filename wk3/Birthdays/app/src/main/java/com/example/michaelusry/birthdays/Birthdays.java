package com.example.michaelusry.birthdays;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by michael on 12/8/14.
 */
public class Birthdays implements Serializable {

    private static final long serialVersionUID = 514116325584636891L;
    private static final String TAG = "Birthdays";


    private String mfirstName = "";
    private String mlastName = "";
    private String mdateofbirth = "";


    public Birthdays() {
        mfirstName = "";
        mlastName = "";
        mdateofbirth = "";
        Log.i(TAG, "public");

    }

    public void setData(String _firstName, String _lastName, String _dob){
        mfirstName = _firstName;
        mlastName = _lastName;
        mdateofbirth = _dob;

    }

//    public void getData(){
//        return mfirstName, mlastName, mdateofbirth;
//
//    }


    public String getFirstName() {
        return mfirstName;
    }

    public String getLastName() {
        return mlastName;
    }

    public String getDOB() {
        return mdateofbirth;
    }

//    public String setFirstName() {
//        return firstName;
//    }
//
//    public String setLastName() {
//        return lastName;
//    }
//
//    public String setDOB() {
//        return dateofbirth;
//    }



}
