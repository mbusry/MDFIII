package michaelusry.com.birthdays;

import java.io.Serializable;

/**
 * Created by Michael Usry  on 3/16/15.
 */
public class Birthday implements Serializable {

    private static final long serialVersionUID = 517116325584636891L;

    private String mFirstName;
    private String mLastName;
    private String mDateOfBirth;

    public Birthday(String _fn, String _ln, String _dob) {
        mFirstName = _fn;
        mLastName = _ln;
        mDateOfBirth = _dob;
    }


    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getDateOfBirth() {
        return mDateOfBirth;
    }


}
