package com.lucrus.main.validation;

import android.util.Log;

import java.util.Map;

/**
 * Created by lucrus on 13/10/16.
 */
public class RequiredWhen extends BaseValidator {
    private String key;
    private String keyValue;
    private String operator;


    public RequiredWhen(Object obj) {
        super(obj);
        Map<String, Object> params = (Map<String, Object>) obj;
        key = "" + params.get("key");
        keyValue = "" + params.get("key-value");
        operator = "" + params.get("operator");
    }

    @Override
    public String getRelatedFieldKey() {
        return key;
    }

    @Override
    public boolean validate(String s) {
        if ("false".equalsIgnoreCase(s)) {
            s = "";
        }
        try {
            if (("equal".equalsIgnoreCase(operator) && keyValue.equalsIgnoreCase(relatedFieldValue)) ||
                    ("not-equal".equalsIgnoreCase(operator) && !keyValue.equalsIgnoreCase(relatedFieldValue))) {
                return s != null && s.trim().length() > 0;
            }
            return true;
        } catch (Exception e) {
            Log.e("VALIDATOR", e.getMessage(), e);
            return true;
        }
    }


}
