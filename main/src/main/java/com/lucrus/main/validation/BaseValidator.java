package com.lucrus.main.validation;

import java.util.Map;

/**
 * Created by lucrus on 12/10/16.
 */

public abstract class BaseValidator implements Validator {
    protected String message;
    protected String relatedFieldValue;

    public BaseValidator(Object obj) {
        super();
        Map<String, Object> params = (Map<String, Object>) obj;
        message = "" + params.get("message");
    }

    public String errorMessage() {
        return message;
    }

    @Override
    public String getRelatedFieldKey() {
        return null;
    }

    @Override
    public void setRelatedFieldValue(String value) {
        relatedFieldValue = value;
        if ("false".equalsIgnoreCase(relatedFieldValue)) {
            relatedFieldValue = "";
        }
    }
}
