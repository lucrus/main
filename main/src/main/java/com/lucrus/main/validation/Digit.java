package com.lucrus.main.validation;

/**
 * Created by lucrus on 13/10/16.
 */

public class Digit extends BaseValidator {


    public Digit(Object obj) {
        super(obj);

    }


    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;

        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
