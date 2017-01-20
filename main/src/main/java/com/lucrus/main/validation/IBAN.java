package com.lucrus.main.validation;

import org.iban4j.IbanUtil;

/**
 * Created by lucrus on 13/10/16.
 */
public class IBAN extends BaseValidator {

    public IBAN(Object obj) {
        super(obj);
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;
        try {
            IbanUtil.validate(s.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
