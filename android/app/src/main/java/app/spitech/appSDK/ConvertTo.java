package app.spitech.appSDK;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConvertTo {


    public static String customDate(String date,String formatInput,String formatOutput){
        String result="";
        try {
            if(Validation.isNotEmpty(date)){  //yyyy-MM-dd = 2020-12-31
                SimpleDateFormat originalFormat = new SimpleDateFormat(formatInput, Locale.ENGLISH);
                SimpleDateFormat targetFormat = new SimpleDateFormat(formatOutput);
                Date dateObject = originalFormat.parse(date);
                result = targetFormat.format(dateObject);  // 20120821
            }
        }catch (Exception ex){
            Log.e("customDate",ex.toString());
            result="";
        }
        return  result;
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static String getSubString(String str,int length){
        if(str.length()>(length+4)){
            str=str.substring(0,length)+"....";
        }
        return str;
    }
}
