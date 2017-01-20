package com.lucrus.main.validation;

/**
 * Created by lucrus on 12/10/16.
 */

public class Required extends BaseValidator {


    public Required(Object obj) {
        super(obj);

    }

    @Override
    public boolean validate(String s) {
        if ("false".equalsIgnoreCase(s)) {
            s = "";
        }
        return s != null && s.trim().length() > 0;
    }

}
