package com.lucrus.main.validation;

/**
 * Created by lucrus on 13/10/16.
 */
public class PosCode extends BaseValidator {

    public PosCode(Object obj) {
        super(obj);
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;
        if (s.length() == 10) {
            try {
                Long.parseLong(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        if (s.length() == 11) {
            try {
                Long.parseLong(s.substring(0, 10));
            } catch (Exception e) {
                return false;
            }
            char c = s.charAt(10);
            return Character.isLetter(c);
        }
        return false;
    }
}
