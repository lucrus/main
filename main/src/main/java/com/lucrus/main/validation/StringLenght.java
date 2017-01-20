package com.lucrus.main.validation;

import java.util.Map;

/**
 * Created by lucrus on 13/10/16.
 */
public class StringLenght extends BaseValidator {
    private int min;
    private int max;

    public StringLenght(Object obj) {
        super(obj);
        Map<String, Object> params = (Map<String, Object>) obj;
        min = Integer.parseInt(params.get("min") + "");
        max = Integer.parseInt(params.get("max") + "");
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;
        return s.length() >= min && s.length() <= max;
    }
}
