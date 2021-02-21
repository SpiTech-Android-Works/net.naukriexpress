package app.spitech.appSDK;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hp on 4/9/2016.
 */
public class Validation {

    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    // validating email id
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(.\\d+)?");
    }

    // validating password with retype password
    public static boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

    // validating mobile
    public static boolean isValidMobile(String mobile) {
        if (mobile != null && mobile.trim().length() == 10 && android.util.Patterns.PHONE.matcher(mobile).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // validating mobile
    public static boolean isNotEmpty(String data) {
        if (data == null || data.equalsIgnoreCase("null") || data.equalsIgnoreCase("NULL") || data.trim().equalsIgnoreCase("") || data.trim().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidName(String data) {
        String patern = "^[a-zA-Z\\s]+$";
        if (data != null && !data.trim().equalsIgnoreCase("") && Pattern.matches(patern, data) == true) {
            return true;
        }
        return false;
    }

}
