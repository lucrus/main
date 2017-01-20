package com.lucrus.main.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by lucrus on 12/10/16.
 */

public class Between extends BaseValidator {
    private Double min, max;


    public Between(Object obj) {
        super(obj);
        Map<String, Object> params = (Map<String, Object>) obj;
        min = Double.parseDouble(params.get("min") + "");
        max = Double.parseDouble(params.get("max") + "");
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) {
            return true;
        }

        try {
            Double val = Double.parseDouble(s);
            ArrayList<Double> aaa = new ArrayList<>();
            aaa.add(val);
            aaa.add(min);
            aaa.add(max);
            Collections.sort(aaa);
            return val == aaa.get(1);
        } catch (Exception e) {
            return false;
        }
    }
}
