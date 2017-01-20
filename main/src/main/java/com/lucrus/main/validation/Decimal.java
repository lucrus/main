package com.lucrus.main.validation;

/**
 * Created by lucrus on 14/10/16.
 */

public class Decimal extends BaseValidator {

    public Decimal(Object obj) {
        super(obj);
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) {
            return true;
        }
        try {
            Double.parseDouble(s.replaceAll(",", "."));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
