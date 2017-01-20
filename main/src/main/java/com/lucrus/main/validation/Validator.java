package com.lucrus.main.validation;

/**
 * Created by lucrus on 12/10/16.
 */

public interface Validator {
    boolean validate(String s);

    String getRelatedFieldKey();

    void setRelatedFieldValue(String value);

    String errorMessage();
}
