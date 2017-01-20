package com.lucrus.main.validation;

import java.util.Map;

/**
 * Created by lucrus on 13/10/16.
 */
public class Choice extends BaseValidator {
    private String key;
    //private String keyValue;
    //private String operator;


    public Choice(Object obj) {
        super(obj);
        Map<String, Object> params = (Map<String, Object>) obj;
        key = "" + params.get("key");
    }

    @Override
    public String getRelatedFieldKey() {
        return key;
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0 || "false".equalsIgnoreCase(s)) {
            return relatedFieldValue != null && relatedFieldValue.trim().length() > 0; //"true".equalsIgnoreCase(relatedFieldValue);
        } else {
            return relatedFieldValue == null || relatedFieldValue.trim().length() == 0 || "false".equalsIgnoreCase(relatedFieldValue);
        }

    }


}
