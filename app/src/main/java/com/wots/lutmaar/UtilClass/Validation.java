package com.wots.lutmaar.UtilClass;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by NIK on 20-02-2015.
 */
public class Validation {
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{3}-\\d{7}";

    // Error Messages
    private static final String REQUIRED_MSG = "Please fill ";
    private static final String EMAIL_MSG = "Invalid email";
    private static final String PHONE_MSG = "###-#######";

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG);
    }

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (!hasText(editText,"E-Mail") ) return false;

        // pattern doesn't match so returning false
        if ( !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        };

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText,String info) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG +" "+ info);
            return false;
        }

        return true;
    }

    public static boolean verify(EditText etRegiPass, EditText etRegiConfiPass) {
        String s1 = etRegiPass.getText().toString();
        String s2 = etRegiConfiPass.getText().toString();
        if(s1.equals(s2)) {
           return true;
        }
        etRegiConfiPass.setError("Password not match");
        return false;
    }
}
