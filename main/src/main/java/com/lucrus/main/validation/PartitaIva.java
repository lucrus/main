package com.lucrus.main.validation;

/**
 * Created by lucrus on 13/10/16.
 */

public class PartitaIva extends BaseValidator {

    public PartitaIva(Object obj) {
        super(obj);
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;

        return controllaPIVA(s);
    }

    public boolean controllaPIVA(String pi) {
        int i, c, s;
        if (pi.length() == 0) {
            return false;
        }
        if (pi.length() != 11) {
            return false;
        }
        for (i = 0; i < 11; i++) {
            if (pi.charAt(i) < '0' || pi.charAt(i) > '9')
                return false;
        }
        s = 0;
        for (i = 0; i <= 9; i += 2) {
            s += pi.charAt(i) - '0';
        }
        for (i = 1; i <= 9; i += 2) {
            c = 2 * (pi.charAt(i) - '0');
            if (c > 9) {
                c = c - 9;
            }
            s += c;
        }
        if ((10 - s % 10) % 10 != pi.charAt(10) - '0') {
            return false;
        }
        return true;
    }
}
