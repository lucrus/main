package com.lucrus.main.validation;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by lucrus on 13/10/16.
 */

public class RegularExpression extends BaseValidator {
    private String exp;

    public RegularExpression(Object obj) {
        super(obj);
        Map<String, Object> params = (Map<String, Object>) obj;
        exp = "" + params.get("regexp");
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;

        //Pattern p = Pattern.compile(exp);
        //return p.matcher(s).matches();
        return Pattern.matches(exp, s);
    }


    public void setExp(String exp) {
        this.exp = exp;
    }
}
