package com.lucrus.main.validation;

import android.util.Patterns;

import java.util.HashMap;

/**
 * Created by lucrus on 13/10/16.
 */
public class EmailAddress extends BaseValidator {

    public EmailAddress(Object obj) {
        super(obj);
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;
        String exp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        exp = Patterns.EMAIL_ADDRESS.pattern();
        RegularExpression re = new RegularExpression(new HashMap<>());
        re.setExp(exp);

        return re.validate(s);
    }
}
