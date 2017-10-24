package com.ntech.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    static Pattern p = null;
    static Matcher m = null;

    public static boolean checkPassword(String password){
        p = Pattern.compile("[\\S]{6,20}");
        m = p.matcher(password);
        return  m.matches();
    }

    public static boolean checkUserName(String userName){
        p = Pattern.compile("[a-zA-Z0-9_]{6,20}");
        m = p.matcher(userName);
        return  m.matches();
    }

    public static boolean checkEmail(String email){
        p = Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
        m = p.matcher(email);
        return  m.matches();
    }
}
